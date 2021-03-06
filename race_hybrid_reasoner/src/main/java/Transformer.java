/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * copied from: https://code.google.com/p/rosjava/source/browse/android_honeycomb_mr2/src/org/ros/android/views/visualization/?repo=android&r=07c00460a3826b976c153ea57353a54a4b275e37
 */
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import geometry_msgs.TransformStamped;
import org.ros.namespace.GraphName;
import org.ros.rosjava_geometry.Transform;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Very simple implementation of a TF transformer.
 * 
 * Currently, the class does not support time. Lookups always use the newest
 * transforms.
 * 
 * @author moesenle@google.com (Lorenz Moesenlechner)
 * 
 */
public class Transformer {

  /**
   * Mapping from child frame IDs to their respective transforms.
   */
  private final Map<GraphName, TransformStamped> transforms;

  private GraphName prefix;

  public Transformer() {
    transforms = Maps.newConcurrentMap();
    prefix = null;
  }

  /**
   * Adds a transform.
   * 
   * @param transform
   *          the transform to add
   */
  public void updateTransform(TransformStamped transform) {
    GraphName frame = GraphName.of(transform.getChildFrameId());
    transforms.put(frame, transform);
  }

  public TransformStamped getTransform(GraphName frame) {
    return transforms.get(makeFullyQualified(frame));
  }

  /**
   * Returns true if there is a transform chain from sourceFrame to targetFrame.
   * 
   * @param targetFrame
   * @param sourceFrame
   * @return true if there exists a transform from sourceFrame to targetFrame
   */
  public boolean canTransform(GraphName targetFrame, GraphName sourceFrame) {
    if (targetFrame == null || sourceFrame == null) {
      return false;
    }
    if (targetFrame.equals(sourceFrame)) {
      return true;
    }
    List<Transform> downTransforms = transformsToRoot(sourceFrame);
    List<Transform> upTransforms = transformsToRoot(targetFrame);
    if (downTransforms.size() == 0 && upTransforms.size() == 0) {
      return false;
    }
    if (downTransforms.size() > 0
        && upTransforms.size() > 0
        && !downTransforms.get(downTransforms.size() - 1).equals(
            upTransforms.get(upTransforms.size() - 1))) {
      return false;
    }
    return true;
  }

  /**
   * Returns the list of transforms to apply to transform from source frame to
   * target frame.
   * 
   * @return list of transforms from source frame to target frame
   */
  public List<Transform> lookupTransforms(GraphName targetFrame, GraphName sourceFrame) {
    List<Transform> result = Lists.newArrayList();
    if (makeFullyQualified(targetFrame).equals(makeFullyQualified(sourceFrame))) {
      return result;
    }
    List<Transform> upTransforms = transformsToRoot(sourceFrame);
    List<Transform> downTransforms = transformsToRoot(targetFrame);
    // TODO(moesenle): Check that if the transform chain has 0 length the frame
    // id is the root frame.
    Preconditions.checkState(upTransforms.size() > 0 || downTransforms.size() > 0,
        "Frames unknown: " + sourceFrame + " " + targetFrame);
    upTransforms = invertTransforms(upTransforms);
    Collections.reverse(downTransforms);
    if (upTransforms.size() > 0 && downTransforms.size() > 0) {
      Preconditions.checkState(
          upTransforms.get(upTransforms.size() - 1).equals(downTransforms.get(0)),
          "Cannot find transforms from " + sourceFrame + " to " + targetFrame
              + ". Transform trees not connected.");
    }
    result.addAll(upTransforms);
    result.addAll(downTransforms);
    return result;
  }

  /**
   * Returns the transform from source frame to target frame.
   */
  public Transform lookupTransform(GraphName targetFrame, GraphName sourceFrame) {
    List<Transform> transforms = lookupTransforms(targetFrame, sourceFrame);
    Transform result = Transform.identity();
    for (Transform transform : transforms) {
      result = result.multiply(transform);
    }
    return result;
  }

  /**
   * Returns the list of inverted transforms.
   * 
   * @param transforms
   *          the transforms to invert
   */
  private List<Transform> invertTransforms(List<Transform> transforms) {
    List<Transform> result = Lists.newArrayList();
    for (Transform transform : transforms) {
      result.add(transform.invert());
    }
    return result;
  }

  /**
   * Returns the list of transforms from frame to the root of the transform
   * tree. Note: the root of the tree is always the last transform in the list.
   * 
   * @param frame
   *          the start frame
   * @return the list of transforms from frame to root
   */
  private List<Transform> transformsToRoot(GraphName frame) {
    GraphName qualifiedFrame = makeFullyQualified(frame);
    List<Transform> result = Lists.newArrayList();
    while (true) {
      TransformStamped currentTransform = transforms.get(qualifiedFrame);
      if (currentTransform == null) {
        break;
      }
      result.add(Transform.fromTransformMessage(currentTransform.getTransform()));
      qualifiedFrame = makeFullyQualified(GraphName.of((currentTransform.getHeader().getFrameId())));
    }
    return result;
  }

  public void setPrefix(GraphName prefix) {
    this.prefix = prefix;
  }

  public GraphName makeFullyQualified(GraphName frame) {
    Preconditions.checkNotNull(frame, "Frame not specified.");

    GraphName prefixed = GraphName.of(frame.toString());   // clone frame
    if (prefix != null) {
      prefixed = prefix.join(frame);
    }
    GraphName global = prefixed.toGlobal();
    Preconditions.checkState(global.isGlobal());
    return global;
  }

  /**
   * Transforms a geometry_msgs.PoseStamped message to frame targetFrame
   * WARNING: this changes the original PoseStamped message (like the other messages in rosjava_geometry)!!!
   * @param targetFrame  The target frame
   * @param pose       geometry_msgs.msg.PoseStamped object; both in and out.
   */
  public void transformPose(GraphName targetFrame, geometry_msgs.PoseStamped pose)
  {
		Transform poseTrans = Transform.fromPoseMessage(pose.getPose());
		Transform frameTrans = this.lookupTransform(makeFullyQualified(GraphName.of(pose.getHeader().getFrameId())), makeFullyQualified(targetFrame));
		Transform resultTrans = frameTrans.multiply(poseTrans);

		resultTrans.toPoseStampedMessage(makeFullyQualified(targetFrame), pose.getHeader().getStamp(), pose);
  }
}
