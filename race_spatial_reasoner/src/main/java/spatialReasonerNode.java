import java.awt.Rectangle;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import meta.MetaSpatialConstraint;
import meta.MetaSpatialConstraintSolver;
import meta.TCSP.TCSPSolver;

import org.ros.address.InetAddressFactory;
import org.ros.concurrent.CancellableLoop;
import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
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

import race_msgs.Fluent;
import race_msgs.FluentArray;
import spatial.rectangleAlgebra.BoundingBox;
import spatial.rectangleAlgebra.RectangleConstraintSolver;
import spatial.rectangleAlgebra.SpatialAssertionalRelation;
import spatial.rectangleAlgebra.SpatialRule;
import time.Bounds;
import utility.logging.MetaCSPLogging;

import race_msgs.AddFluent;
import race_msgs.AddFluentRequest;
import race_msgs.AddFluentResponse;
import race_msgs.FlexibleTimepoint;

import race_msgs.*;


/**
 * @author Iran Mansouri
 *
 */
public class spatialReasonerNode extends AbstractNodeMain {

	//each element of this vector is related to each passive object and hash map <conatranitFiller, passiveObjectCategory> 
	private HashMap<Fluent, String> flunetCoor = new HashMap<Fluent, String>(); //this is the final result the coordinate of asked Fluents
	private HashMap<String, String> areaInsToConsIns = new HashMap<String, String>(); //this is a map from area instance to constraint Instances
	private Vector<Fluent> demandedAreaFluent = new Vector<Fluent>();
	private static final String MYTOPIC = "blackboard/mytopic";
	private HashMap<String, String> reifiedCons = new HashMap<String, String>();
	private ConnectedNode node;
	private Vector<SpatialRule> spatialKnowledge;
	private HashMap<String, String> passiveObjCoor = new HashMap<String, String>();
	private HashMap<Fluent, SpatialRule> relatedSpatialRelToFluent = new HashMap<Fluent, SpatialRule>();

	@Override
	public void onStart(ConnectedNode connectedNode) {

		MetaCSPLogging.setLevel(MetaSpatialConstraintSolver.class, Level.FINEST);
		MetaCSPLogging.setLevel(RectangleConstraintSolver.class, Level.FINEST);
		this.node = connectedNode;
		spatialKnowledge = StaticSpatialKnowledge.getSpatialKnowledge();
		//listen to OnDemand Topic () -- get Passive Objects
		getPassiveObject();
		
		
		
		updateFluentCoordinate();

//		Subscriber<Fluent> fluentArrsubscriber = connectedNode.newSubscriber(MYTOPIC, Fluent._TYPE);
//		fluentArrsubscriber.addMessageListener(new MessageListener<Fluent>() {
//			@Override
//			public void onNewMessage(Fluent message) {				
//				//onNewObject(message);
//			}
//		});

	}


	private void getPassiveObject() {
		
		ServiceClient<FluentsByQueryRequest, FluentsByQueryResponse> getPassiveObjsClients = null;
		try {
			getPassiveObjsClients = node.newServiceClient("blackboard/fluents_by_query", FluentsByQuery._TYPE);

		} 
		catch (org.ros.exception.ServiceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final FluentsByQueryRequest poRequest = getPassiveObjsClients.newMessage();
		poRequest.setQuery("select distinct ?instance where {?instance a ?subclass. ?subclass rdfs:subClassOf+ race:Furniture}");

		getPassiveObjsClients.call(poRequest, new ServiceResponseListener<FluentsByQueryResponse>() {

			@Override
			public void onSuccess(FluentsByQueryResponse response) {
				System.out.println("we get the passive Objects");
				onGetPassiveObjects(response.getFluents());
			}

			@Override
			public void onFailure(RemoteException arg0) {
				System.out.println("Get passive Objects is failed");

			}
		});

		
	}

	private void updateFluentCoordinate() {
		
		Fluent cup = node.getTopicMessageFactory().newFromType(Fluent._TYPE);
		cup.setName("cup1");
		cup.setType("Cup");
		FlexibleTimepoint ft1 = node.getTopicMessageFactory().newFromType(FlexibleTimepoint._TYPE);
		ft1.setLower(new Time(3));
		ft1.setUpper(new Time(5));
		cup.setStartTime(ft1);
		FlexibleTimepoint ft2 = node.getTopicMessageFactory().newFromType(FlexibleTimepoint._TYPE);
		ft2.setLower(new Time(6));
		ft2.setUpper(new Time(9));
		cup.setFinishTime(ft2);
		//		ArrayList<Property> props = new ArrayList<Property>();
		//		Property prop = connectedNode.getTopicMessageFactory().newFromType(Property._TYPE);
		//		cup.setProperties(props);


		
		ServiceClient<AddFluentRequest, AddFluentResponse> serviceClient = null;
		try {
			serviceClient = node.newServiceClient("blackboard/add_fluent", AddFluent._TYPE);

		} 
		catch (org.ros.exception.ServiceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final AddFluentRequest request = serviceClient.newMessage();
		request.setFluent(cup);

		serviceClient.call(request, new ServiceResponseListener<AddFluentResponse>() {

			@Override
			public void onSuccess(AddFluentResponse response) {
				System.out.println(response.getBlackboardResponse());
			}

			@Override
			public void onFailure(RemoteException arg0) {
				System.out.println("system call failed");

			}
		});

		
	}

	private void onGetPassiveObjects(List<Fluent> passiveObjectsFluents) {

		for (Fluent f : passiveObjectsFluents) {
			System.out.println(f.getName());			
			for (Property p : f.getProperties()) {
				if(p.getRoleType().contains("Constraint")){
					reifiedCons.put(p.getObjectFiller(), f.getType());
					getConstraintFluent(p.getObjectFiller());
				}
				if(p.getFillerType().compareTo("BoundingBox") == 0)
					passiveObjCoor.put(f.getName(), p.getFillerType());
			}			
		}
	}


	private void getConstraintFluent(String getObjectFiller) {
		System.out.println("heloooooooooooooooooooooooooooooooooooooooooooo");
		ServiceClient<RetrieveFluentRequest, RetrieveFluentResponse> getFluentByStringClient = null;
		try {
			getFluentByStringClient = node.newServiceClient("blackboard/retrieve_fluent", RetrieveFluent._TYPE);
		} 
		catch (org.ros.exception.ServiceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final RetrieveFluentRequest req= getFluentByStringClient.newMessage();
		req.setInstance(getObjectFiller);

		getFluentByStringClient.call(req, new ServiceResponseListener<RetrieveFluentResponse>() {

			@Override
			public void onSuccess(RetrieveFluentResponse response) {

				for (Property p : response.getFluent().getProperties()) {
					//System.out.println("has Area:" + p.getFillerType());
					areaInsToConsIns.put(p.getObjectFiller(), response.getFluent().getName());
					System.out.println("Hash Map: " + p.getObjectFiller() + " "+ response.getFluent().getName());
					getAreaFluent(p.getObjectFiller());
					
				}
			}


			@Override
			public void onFailure(RemoteException arg0) {
				System.out.println("retrival failed");
			}
		});
	}
	
	
	private void getAreaFluent(String objectFiller) {
		
		ServiceClient<RetrieveFluentRequest, RetrieveFluentResponse> getFluentByStringClient = null;
		try {
			getFluentByStringClient = node.newServiceClient("blackboard/retrieve_fluent", RetrieveFluent._TYPE);
		} 
		catch (org.ros.exception.ServiceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final RetrieveFluentRequest req= getFluentByStringClient.newMessage();
		req.setInstance(objectFiller);

		getFluentByStringClient.call(req, new ServiceResponseListener<RetrieveFluentResponse>() {

			@Override
			public void onSuccess(RetrieveFluentResponse response) {
				demandedAreaFluent.add((Fluent)response);
				if(reifiedCons.containsKey(response.getFluent().getType())){
					for (int i = 0; i < spatialKnowledge.size(); i++) {
						if(spatialKnowledge.get(i).getFrom().compareTo(response.getFluent().getType()) == 0 && 
								spatialKnowledge.get(i).getTo().compareTo(reifiedCons.get(response.getFluent().getName())) == 0)
							relatedSpatialRelToFluent.put((Fluent)response, spatialKnowledge.get(i));
					}
				}
				
				System.out.println("");
			}


			@Override
			public void onFailure(RemoteException arg0) {
				System.out.println("retrival failed");
			}
			
			
		});		
	}
	
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("race_spatial_reasoner");
	}
}
