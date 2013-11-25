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
import race_msgs.Property;
import race_msgs.RetrieveFluent;
import race_msgs.RetrieveFluentRequest;
import race_msgs.RetrieveFluentResponse;


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

	private HashMap<String, Rectangle> obstacles = new HashMap<String, Rectangle>();

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

		//test
		obstacles.put("testArea", new Rectangle(100, 100, 50, 100));

		//get all bounding boxes 
		getAllAreas();

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

	private void getAllAreas() {

		ServiceClient<GetFluentsByQueryRequest, GetFluentsByQueryResponse> getBBs = null;
		try {
			getBBs = node.newServiceClient("blackboard/get_fluents_by_query", GetFluentsByQuery._TYPE);
		} catch (org.ros.exception.ServiceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		final GetFluentsByQueryRequest bbRequest = getBBs.newMessage();
		bbRequest.setQuery("select distinct ?instance where {?instance upper:hasBoundingBox ?any}");
		
//		bbRequest.setQuery("select distinct ?instance where {?instance rdf:type ?subclass . ?subclass rdfs:subClassOf+ owl:BoundingBox}");
//		bbRequest.setQuery("select distinct ?instance where {?instance a ?subclass. ?subclass rdfs:subClassOf+ race:BoundingBox}");

		getBBs.call(bbRequest, new ServiceResponseListener<GetFluentsByQueryResponse>() {

			@Override
			public void onSuccess(GetFluentsByQueryResponse response) {

				for (Fluent f : response.getFluents()) {
					System.out.println("-->" + f.getName());
					for (Property p : f.getProperties()){
						if(p.getRoleType().compareTo("hasBoundingBox") == 0){
							System.out.println("******"  + p.getRoleType());
							System.out.println("=====" + p.getObjectFiller());
							getAnArea(p.getObjectFiller());
						}

						//						if(p.getRoleType().compareTo("hasPose") == 0)
						//							System.out.println(p.getObjectFiller());



					}

				}

				System.out.println(response.getFluents());

			}



			@Override
			public void onFailure(RemoteException arg0) {
				System.out.println("Get passive Objects is failed");

			}
		});

	}

	private void getAnArea(String str) {
		//	
		//		ServiceClient<RetrieveFluentRequest, RetrieveFluentResponse> getFluentByStringClient = null;
		//		try {
		//			getFluentByStringClient = node.newServiceClient("blackboard/retrieve_fluent", RetrieveFluent._TYPE);
		//		} 
		//		catch (org.ros.exception.ServiceNotFoundException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		final RetrieveFluentRequest req= getFluentByStringClient.newMessage();
		//
		//		req.setInstance("pose" + posfluent.toUpperCase().substring(0, 1) + posfluent.substring(1, posfluent.length()));
		//
		//
		//		getFluentByStringClient.call(req, new ServiceResponseListener<RetrieveFluentResponse>() {
		//
		//			@Override
		//			public void onSuccess(RetrieveFluentResponse response) {
		//
		//				double x = 0, y = 0;
		//				for (Property p : response.getFluent().getProperties()) {
		//					if(p.getRoleType().compareTo("hasX") == 0)
		//						x = (p.getFloatFiller() * 100);
		//					else if(p.getRoleType().compareTo("hasY") == 0)
		//						y = (p.getFloatFiller() * 100);
		//				}
		//				passiveObjPose.put(posfluent, new org.metacsp.multi.spatial.rectangleAlgebra.Point(x, y));
		//				doneSubRoutines1[counter] = true;
		//			}
		//
		//
		//			@Override
		//			public void onFailure(RemoteException arg0) {
		//				System.out.println("retrival failed");
		//			}
		//		});



	}

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("race_get_obstacle_reasoner");
	}
}
