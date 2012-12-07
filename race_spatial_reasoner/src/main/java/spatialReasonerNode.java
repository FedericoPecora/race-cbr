import java.awt.Rectangle;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import meta.MetaSpatialConstraint;
import meta.MetaSpatialConstraintSolver;
import meta.TCSP.TCSPSolver;
import multi.allenInterval.AllenIntervalConstraint;

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

import com.sun.org.apache.bcel.internal.generic.NEW;


import java.util.Vector;
import java.util.logging.Level;

import javax.management.ServiceNotFoundException;

import race_msgs.Fluent;
import race_msgs.FluentArray;
import spatial.rectangleAlgebra.AugmentedRectangleConstraint;
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
	private HashMap<String, String> areaInsToConsIns = new HashMap<String, String>(); //this is a map from area instance to constraint Instances
	private Vector<Fluent> demandedAreaFluent = new Vector<Fluent>();
	private static final String MYTOPIC = "blackboard/mytopic";
	private HashMap<String, String> reifiedCons = new HashMap<String, String>(); //<manAreaConstrain1, tabel1>
	private ConnectedNode node;
	private Vector<SpatialRule> spatialKnowledge;
	private HashMap<String, String> passiveObjCoor = new HashMap<String, String>();
	private HashMap<String, SpatialRule> relatedSpatialRelToFluent = new HashMap<String, SpatialRule>();
	private HashMap<String, Rectangle> flunetsCoord = new HashMap<String, Rectangle>();
	private boolean done = false;
	private boolean[] doneSubRoutines = null;
	private HashMap<String, String> paasiveObjCategories = new HashMap<String, String>(); //<door1, Door>
	private HashMap<String, String> fluentCategories = new HashMap<String, String>(); //<manAreaLeft1, ManAreaLeft>
	
	@Override
	public void onStart(ConnectedNode connectedNode) {

		MetaCSPLogging.setLevel(MetaSpatialConstraintSolver.class, Level.FINEST);
		MetaCSPLogging.setLevel(RectangleConstraintSolver.class, Level.FINEST);
		this.node = connectedNode;
		spatialKnowledge = StaticSpatialKnowledge.getSpatialKnowledge();
		//listen to OnDemand Topic () -- get Passive Objects
		getPassiveObject();
		
		boolean proceed = false;
		do {
			if (doneSubRoutines != null) {
				proceed = true;
				for (boolean b : doneSubRoutines) {
					if (!b) {
						proceed = false;
						break;
					}
				}
			}
			System.out.print(".");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} while(!proceed);
		
		createSpatialCN();
		
		//updateFluentCoordinate();

//		Subscriber<Fluent> fluentArrsubscriber = connectedNode.newSubscriber(MYTOPIC, Fluent._TYPE);
//		fluentArrsubscriber.addMessageListener(new MessageListener<Fluent>() {
//			@Override
//			public void onNewMessage(Fluent message) {				
//				//onNewObject(message);
//			}
//		});

	}


	private void createSpatialCN() {
		
		System.out.println("... done!");
		for (String fstr : relatedSpatialRelToFluent.keySet()) {
			
			System.out.println("hiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
			Vector<SpatialAssertionalRelation> saRelations = new Vector<SpatialAssertionalRelation>();
			Vector<SpatialRule> srules = new Vector<SpatialRule>();
			
			//System.out.println(reifiedCons.get(areaInsToConsIns.get(fstr)));
			
			srules.add(getPassiveObjectSize(reifiedCons.get(areaInsToConsIns.get(fstr))));
			srules.add(relatedSpatialRelToFluent.get(fstr));
			
			//.........................................

			SpatialAssertionalRelation sa0 = new SpatialAssertionalRelation(fstr, fluentCategories.get(fstr));
			sa0.setCoordinate(new BoundingBox(new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE)));
			saRelations.add(sa0);
			
			//getCategory
			SpatialAssertionalRelation sa1 = new SpatialAssertionalRelation(reifiedCons.get(areaInsToConsIns.get(fstr)), paasiveObjCategories.get(reifiedCons.get(areaInsToConsIns.get(fstr))));
			sa1.setCoordinate(new BoundingBox(new Bounds(70,70), new Bounds(140, 140), new Bounds(70, 70), new Bounds(140, 140)));
			saRelations.add(sa1);
			
			metaSpatialReasoner(fstr, srules, saRelations);

		}
		
		
	}


	private SpatialRule getPassiveObjectSize(String passObj) {
		
		String catStr = paasiveObjCategories.get(passObj);
		SpatialRule ret = new SpatialRule(catStr, catStr, 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
								getSizeOfPassiveObj(passObj)[0]), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, getSizeOfPassiveObj(passObj)[1])));
		return ret;
		
	}


	private Bounds[] getSizeOfPassiveObj(String passObj) {
		// TODO Auto-generated method stub
		Bounds[] bounds = new Bounds[2];


		if(passObj.compareTo("table1") == 0){

			bounds[0] = new Bounds(70, 70);
			bounds[1] = new Bounds(70, 70);

		}
		else if(passObj.compareTo("table2") == 0){

			bounds[0] = new Bounds(70, 70);
			bounds[1] = new Bounds(70, 70);

		}
		else if(passObj.compareTo("counter1") == 0){

			bounds[0] = new Bounds(70, 70);
			bounds[1] = new Bounds(70, 70);

		}

		return bounds;
	}


	private void metaSpatialReasoner(String fstr, Vector<SpatialRule> srules,
			Vector<SpatialAssertionalRelation> saRelations) {
		
		System.out.println("this is the constraint: " + fstr);
		MetaSpatialConstraintSolver metaSolver = new MetaSpatialConstraintSolver(0);

		MetaSpatialConstraint objectsPosition = new MetaSpatialConstraint();
		objectsPosition.setSpatialRules(srules.toArray(new SpatialRule[srules.size()]));
		objectsPosition.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation[saRelations.size()]));
		
		metaSolver.addMetaConstraint(objectsPosition);
		metaSolver.backtrack();
		
		System.out.println(objectsPosition.getRectangle(fstr));

		
	}


	private void getPassiveObject() {
		
		ServiceClient<GetFluentsByQueryRequest, GetFluentsByQueryResponse> getPassiveObjsClients = null;
		try {
			getPassiveObjsClients = node.newServiceClient("blackboard/get_fluents_by_query", GetFluentsByQuery._TYPE);
		} 
		catch (org.ros.exception.ServiceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final GetFluentsByQueryRequest poRequest = getPassiveObjsClients.newMessage();
		poRequest.setQuery("select distinct ?instance where {?instance a ?subclass. ?subclass rdfs:subClassOf+ race:Furniture}");

		getPassiveObjsClients.call(poRequest, new ServiceResponseListener<GetFluentsByQueryResponse>() {

			@Override
			public void onSuccess(GetFluentsByQueryResponse response) {
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
				//System.out.println(response.getBlackboardResponse());
			}

			@Override
			public void onFailure(RemoteException arg0) {
				System.out.println("system call failed");

			}
		});

		
	}

	private void onGetPassiveObjects(List<Fluent> passiveObjectsFluents) {

		int counter = 0;
		for (Fluent f : passiveObjectsFluents) {
			System.out.println(f.getName());
			for (Property p : f.getProperties())
				if(p.getRoleType().contains("Constraint")) counter++;
		}
		doneSubRoutines = new boolean[counter];
		Arrays.fill(doneSubRoutines, false);
		int i = 0;
		
		for (Fluent f : passiveObjectsFluents) {			
			for (Property p : f.getProperties()) {
				if(p.getRoleType().contains("Constraint")){
					reifiedCons.put(p.getObjectFiller(), f.getName());
					paasiveObjCategories.put(f.getName(), f.getType());
					getConstraintFluent(p.getObjectFiller(),i++);
				}
				if(p.getFillerType().compareTo("BoundingBox") == 0)
					passiveObjCoor.put(f.getName(), p.getObjectFiller());
			}			
		}
	}


	private void getConstraintFluent(String getObjectFiller, final int counter) {

		//System.out.println("Requesting result " + counter);
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
					//if(reifiedCons.containsKey(p.getObjectFiller())){
						areaInsToConsIns.put(p.getObjectFiller(), response.getFluent().getName());
//						System.out.println("p.getObjectFiller(): " + p.getObjectFiller());
//						System.out.println("p.getFillerType()" + p.getFillerType());
//						System.out.println("response.getFluent().getName()" + response.getFluent().getName());
						//System.out.println("areaInsToConsIns HashMap: " + p.getObjectFiller() + " " +response.getFluent().getName());
						for (int i = 0; i < spatialKnowledge.size(); i++) {
//							System.out.println("From: " + p.getFillerType());
//							System.out.println("To: " + reifiedCons.get(response.getFluent().getName()));
							if(spatialKnowledge.get(i).getFrom().compareTo(p.getFillerType()) == 0 && 
									spatialKnowledge.get(i).getTo().compareTo(paasiveObjCategories.get(reifiedCons.get(response.getFluent().getName()))) == 0){
								relatedSpatialRelToFluent.put(p.getObjectFiller(), spatialKnowledge.get(i));
								fluentCategories.put(p.getObjectFiller(), p.getFillerType());
							}
						}
					//}				
				}
				//System.out.println("Got result " + counter);
				doneSubRoutines[counter] = true;
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
				System.out.println("this is where I have:" + response.getFluent().getName());
				demandedAreaFluent.add((Fluent)response);
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
