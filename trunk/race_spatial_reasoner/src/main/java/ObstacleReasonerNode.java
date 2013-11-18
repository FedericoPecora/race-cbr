import geometry_msgs.Point;
import geometry_msgs.Pose;
import geometry_msgs.Quaternion;

import java.awt.Rectangle;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import meta.MetaSpatialConstraint;
import meta.MetaSpatialConstraintSolver;
import org.metacsp.meta.TCSP.TCSPSolver;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.BoundingBox;
import org.metacsp.spatial.cardinal.CardinalConstraint;
import spatial.rectangleAlgebra_OLD.RectangleConstraint;
import spatial.rectangleAlgebra_OLD.SpatialAssertionalRelation;
import spatial.rectangleAlgebra_OLD.SpatialRule;

import org.ros.address.InetAddressFactory;
import org.ros.concurrent.CancellableLoop;
import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.internal.message.RawMessage;
import org.ros.internal.node.client.MasterClient;
import org.ros.internal.node.response.Response;
import org.ros.master.client.SystemState;
import org.ros.master.client.TopicSystemState;
import org.ros.message.MessageListener;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMainExecutor;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseBuilder;
import org.ros.node.service.ServiceResponseListener;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;



import java.util.Vector;
import java.util.logging.Level;

import javax.management.ServiceNotFoundException;
import javax.vecmath.Point3d;

import race_msgs.Fluent;
import race_msgs.FluentArray;
import race_msgs.GetFluentsByQuery;
import race_msgs.GetFluentsByQueryRequest;
import race_msgs.GetFluentsByQueryResponse;


import org.metacsp.time.APSPSolver;
import org.metacsp.time.Bounds;
import org.metacsp.time.qualitative.QualitativeAllenIntervalConstraint;
import org.metacsp.utility.logging.LoggerNotDefined;
import org.metacsp.utility.logging.MetaCSPLogging;


import race_msgs.*;


/**
 * @author Iran Mansouri
 *
 */
public class ObstacleReasonerNode extends AbstractNodeMain {
	private ConnectedNode node;
	private static final String MYTOPIC = "blackboard/passiveObjectTopic"; //to get Occupied coordinates


	@Override
	public void onStart(ConnectedNode connectedNode) {

		this.node = connectedNode;
		
		Subscriber<Fluent> fluentArrsubscriber = connectedNode.newSubscriber(MYTOPIC, Fluent._TYPE);
		fluentArrsubscriber.addMessageListener(new MessageListener<Fluent>() {
			@Override
			public void onNewMessage(Fluent message) {	
				System.out.println("get new furniture");

				getObstacleAreaFromPerception();
			}
		});

		//get the fluent from 
		//get all the area coordinate (look at the Martin code for visualizer)
		//iteratively check which area is occupied
		//add fluent to blackboard of type  (look at deliverable 2.3!4!)




//		Subscriber<Fluent> fluentArrsubscriber = connectedNode.newSubscriber(MYTOPIC, Fluent._TYPE);
//		fluentArrsubscriber.addMessageListener(new MessageListener<Fluent>() {
//			@Override
//			public void onNewMessage(Fluent message) {	
//				System.out.println("get new furniture");
//
//				
//			}
//		});




	}
	
		
	private void getObstacleAreaFromPerception() {

		ServiceClient<GetFluentsByQueryRequest, GetFluentsByQueryResponse> getPassiveObjsClients = null;
		boolean print = false;
		while (true)
		{
			try {
				getPassiveObjsClients = node.newServiceClient("blackboard/get_fluents_by_query", GetFluentsByQuery._TYPE);
			}
			catch (org.ros.exception.ServiceNotFoundException e) {
				System.out.println("waiting for service 'blackboard/get_fluents_by_query'...");
				print = true;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
				}
				continue;
			}

			break;
		}
		if (print)
			System.out.println("... done waiting for service.");


		final GetFluentsByQueryRequest poRequest = getPassiveObjsClients.newMessage();
		poRequest.setQuery("select distinct ?instance where {?instance a ?subclass. ?subclass rdfs:subClassOf+ race:Furniture}");

		getPassiveObjsClients.call(poRequest, new ServiceResponseListener<GetFluentsByQueryResponse>() {

			@Override
			public void onSuccess(GetFluentsByQueryResponse response) {

				if(response.getFluents().size() == 0){
					System.out.println("waiting for initial knowledge to be loaded");
				}
				if(response.getFluents().size() != 0);
					//call the Process

			}

			@Override
			public void onFailure(RemoteException arg0) {
				System.out.println("Get passive Objects is failed");

			}
		});

	}

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("race_get_obstacle_reasoner");
	}
}
