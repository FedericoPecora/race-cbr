import java.awt.Rectangle;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import meta.MetaSpatialConstraintSolver;

import org.ros.address.InetAddressFactory;
import org.ros.exception.ServiceException;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMainExecutor;
import org.ros.node.service.ServiceResponseBuilder;
import org.ros.node.topic.Subscriber;


import java.util.Vector;
import java.util.logging.Level;

import race_msgs.Fluent;
import race_msgs.FluentArray;
import spatial.rectangleAlgebra.BoundingBox;
import spatial.rectangleAlgebra.RectangleConstraintSolver;
import spatial.rectangleAlgebra.SpatialAssertionalRelation;
import time.Bounds;
import utility.logging.MetaCSPLogging;

import race_msgs.IsConsistent;
import race_msgs.IsConsistentRequest;
import race_msgs.IsConsistentResponse;
import race_msgs.IsHumanWorkingRequest;
import race_msgs.IsHumanWorkingResponse;


/**
 * @author Iran Mansouri
 *
 */
public class spatialReasonerNode extends AbstractNodeMain {
	// private static final String TEST_CLIENT_NODE_NAME = "/race_test_client";
	
	private static final String FOCUSED_OBJECTS = "focused_objects";
	private static final String SPATIAL_REASONER = "/is_consistent";
	
	private Vector<SpatialAssertionalRelation> saRelations = null;
	private GetPositionService getPosSrv;
	private Node node;

	@Override
	public void onStart(ConnectedNode connectedNode) {
		MetaCSPLogging.setLevel(MetaSpatialConstraintSolver.class, Level.FINEST);
		MetaCSPLogging.setLevel(RectangleConstraintSolver.class, Level.FINEST);
		while (true) {
			try {
				connectedNode.getCurrentTime();
				break; // no exception, so let's stop waiting
			} catch (NullPointerException e) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
				}
			}
		}
		
		try {
			this.node = connectedNode;
			
			Subscriber<FluentArray> fluentArrsubscriber = connectedNode.newSubscriber(FOCUSED_OBJECTS, FluentArray._TYPE);
			fluentArrsubscriber.addMessageListener(new MessageListener<FluentArray>() {
				
				@Override
				public void onNewMessage(FluentArray message) {
					onNewObject(message);
				}
			});
			
			
			connectedNode.newServiceServer(SPATIAL_REASONER, IsConsistent._TYPE, new ServiceResponseBuilder<IsConsistentRequest, IsConsistentResponse>() {
				
				@Override
				public void build(IsConsistentRequest req, IsConsistentResponse response) throws ServiceException{
					
					response.setIsConsistent(callIsSpatiallyConsistent(req));
				}
			});
			
			
	
		} catch (Exception e) {
			e.printStackTrace();
			connectedNode.getLog().fatal(e);
		}
	}

	private boolean callIsSpatiallyConsistent(IsConsistentRequest req) {
		
		saRelations = new Vector<SpatialAssertionalRelation>();
		List<Fluent> fluents = (List<Fluent>) req.getAggregateParts();		
		for (Fluent fl : fluents) {
			System.out.println("name: " + fl.getName());
			SpatialAssertionalRelation sa1 = new SpatialAssertionalRelation(fl.getName(), fl.getType());
			sa1.setCoordinate(getBoundingBoxByName(fl.getName()));
			saRelations.add(sa1);
		}
		
		//makeSARelation();
		IsSpatiallyConsistent isSpatiallyConsistentService = new IsSpatiallyConsistent();
		//should be parse from the Ontology
		return isSpatiallyConsistentService.isMetaConstraintConsistent(req.getAggregateName(), saRelations);
	}
	
	private BoundingBox getBoundingBoxByName(String str){
		if(str.compareTo("cup1") == 0)
			return new BoundingBox(new Bounds(20, 20), new Bounds(28, 28), new Bounds(35, 35), new Bounds(42, 42));
		if(str.compareTo("knife1") == 0)
			return new BoundingBox(new Bounds(50, 50), new Bounds(55, 55), new Bounds(12, 12), new Bounds(26, 26));
		if(str.compareTo("fork1") == 0)
			return new BoundingBox(new Bounds(5, 5), new Bounds(10, 10), new Bounds(14, 14), new Bounds(24, 24));
		if(str.compareTo("dish1") == 0)
			return new BoundingBox(new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE));
		return null;
	}
	
	private void onNewObject(FluentArray message) {

		saRelations = new Vector<SpatialAssertionalRelation>();
		List<Fluent> focusedOccourances = message.getFluents();
		for (Fluent fluent : focusedOccourances) {
			SpatialAssertionalRelation sa1 = new SpatialAssertionalRelation(fluent.getName(), fluent.getType());
			sa1.setCoordinate(getBoundingBoxByName(fluent.getName()));
			saRelations.add(sa1);
			System.out.println("subscriber");

		}
		//.......................................................................................................


	}

	private void makeSARelation(){
		
		saRelations = new Vector<SpatialAssertionalRelation>();
				
		SpatialAssertionalRelation sa1 = new SpatialAssertionalRelation("cup1", "cup");
		sa1.setCoordinate(new BoundingBox(new Bounds(20, 20), new Bounds(28, 28), new Bounds(35, 35), new Bounds(42, 42)));
		saRelations.add(sa1);
		
		SpatialAssertionalRelation sa2 = new SpatialAssertionalRelation("knife1", "knife");
		sa2.setCoordinate(new BoundingBox(new Bounds(50, 50), new Bounds(55, 55), new Bounds(12, 12), new Bounds(26, 26)));
		saRelations.add(sa2);

		SpatialAssertionalRelation sa3 = new SpatialAssertionalRelation("fork1", "fork");
		sa3.setCoordinate(new BoundingBox(new Bounds(5, 5), new Bounds(10, 10), new Bounds(14, 14), new Bounds(24, 24)));
		saRelations.add(sa3);
		
		SpatialAssertionalRelation sa4 = new SpatialAssertionalRelation("dish1", "dish");
		sa4.setCoordinate(new BoundingBox(new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE)));
		saRelations.add(sa4);
		
		
//		SpatialAssertionalRelation sa5 = new SpatialAssertionalRelation("napkin1", "napkin");
//		sa5.setCoordinate(new BoundingBox(new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE)));
//		saRelations.add(sa5);
//		
//		SpatialAssertionalRelation sa6 = new SpatialAssertionalRelation("dish2", "dish");
//		sa6.setCoordinate(new BoundingBox(new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE)));
//		saRelations.add(sa6);
	}
	
	
	public static void main(String[] args) throws URISyntaxException {
		
//		NodeMain nodeMain = new spatialReasonerNode();
//		
//		URI uri = new URI("http://localhost:11311");
//		String host = InetAddressFactory.newNonLoopback().getHostName();
//		NodeConfiguration nodeConfiguration =
//		    NodeConfiguration.newPublic(host, uri);
//
//		NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
//		nodeMainExecutor.execute(nodeMain, nodeConfiguration);
		//nodeRunner.shutdown();
		
	}


	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("race_spatial_reasoner");
	}
}
