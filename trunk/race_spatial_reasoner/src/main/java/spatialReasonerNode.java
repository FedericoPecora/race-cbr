import geometry_msgs.Point;
import geometry_msgs.Pose;
import geometry_msgs.Quaternion;

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
import org.ros.internal.message.RawMessage;
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
import spatial.rectangleAlgebra.AugmentedRectangleConstraintSolver;
import spatial.rectangleAlgebra.BoundingBox;
import spatial.rectangleAlgebra.RectangleConstraintSolver;
import spatial.rectangleAlgebra.RectangularRegion;
import spatial.rectangleAlgebra.SpatialAssertionalRelation;
import spatial.rectangleAlgebra.SpatialRule;
import time.APSPSolver;
import time.Bounds;
import utility.logging.LoggerNotDefined;
import utility.logging.MetaCSPLogging;


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
	private HashMap<String, String> reifiedCons = new HashMap<String, String>(); //<manAreaConstrain1, tabel1> it stores all refied constraint  for each Passive Object 
	private ConnectedNode node;
	private Vector<SpatialRule> spatialKnowledge = new Vector<SpatialRule>();
	private HashMap<String, String> passiveObjCoor = new HashMap<String, String>();//<counter1, boundingBox1> : it is for querying BB to get table1, counter1, and table2 coordinate
	private HashMap<String, Vector<SpatialRule>> relatedSpatialRelToFluent = new HashMap<String, Vector<SpatialRule>>();//<manAreaLeft1, SpatialRule(From, To, AugmentedRAConstraint(2D bouned Allen))>
	private boolean done = false;
	private boolean[] doneSubRoutines = null;
	private HashMap<String, String> paasiveObjCategories = new HashMap<String, String>(); //<door1, Door>
	private HashMap<String, String> fluentCategories = new HashMap<String, String>(); //<manAreaLeft1, ManAreaLeft>
	private HashMap<String, String> passObjToPoseFlunet = new HashMap<String, String>(); //<table1, poseTable1>
	private HashMap<String, spatial.rectangleAlgebra.Point> passiveObjPose = new HashMap<String, spatial.rectangleAlgebra.Point>(); //<poseTable1, Point(3321, 4521)>
	private HashMap<String, String> posFleuntofArea = new HashMap<String, String>(); //<manAreaLeft1, poseMANTable1>

	private boolean[] doneSubRoutines1 = null;
	
	@Override
	public void onStart(ConnectedNode connectedNode) {

		MetaCSPLogging.setLevel(MetaSpatialConstraintSolver.class, Level.FINEST);
		MetaCSPLogging.setLevel(RectangleConstraintSolver.class, Level.FINEST);
		this.node = connectedNode;
		StaticSpatialKnowledge.getSpatialKnowledge(spatialKnowledge);
		//StaticSpatialKnowledge.getSpatialKnowledge(spatialKnowledge);
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
		

		System.out.println("helooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooos");
		
		
		
		filltest();
		
		boolean proceed1 = false;
		do {
			if (doneSubRoutines1 != null) {
				proceed1 = true;
				for (boolean b : doneSubRoutines1) {
					if (!b) {
						proceed1 = false;
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

		} while(!proceed1);

		
		System.out.println("NOTOCE: "  + posFleuntofArea.size());
		for (String test : posFleuntofArea.keySet()) {
			System.out.println(test + " " +posFleuntofArea.get(test));
		}

		//updateFluentCoordinate();

		//		Subscriber<Fluent> fluentArrsubscriber = connectedNode.newSubscriber(MYTOPIC, Fluent._TYPE);
		//		fluentArrsubscriber.addMessageListener(new MessageListener<Fluent>() {
		//			@Override
		//			public void onNewMessage(Fluent message) {				
		//				//onNewObject(message);
		//			}
		//		});

	}
	
	private void filltest(){
		
		int counter = fluentCategories.size();
		
		doneSubRoutines1 = new boolean[counter];
		Arrays.fill(doneSubRoutines1, false);
		int i = 0;
		System.out.println("man injaaaaaaaaaaaaaaaaaaaaaam nemibii");
		for (String st : fluentCategories.keySet()) {
			getPoseflunet(st, i++);
		}
		
		
	}

	private void createSpatialCN() {

		System.out.println("... done!");

		for (String fstr : relatedSpatialRelToFluent.keySet()) {

			Vector<SpatialAssertionalRelation> saRelations = new Vector<SpatialAssertionalRelation>();
			Vector<SpatialRule> srules = new Vector<SpatialRule>();

			for (int i = 0; i < relatedSpatialRelToFluent.get(fstr).size(); i++) 
				srules.add(relatedSpatialRelToFluent.get(fstr).get(i));

			if(srules.size() == 0)
				continue;

			SpatialAssertionalRelation sa0 = new SpatialAssertionalRelation(fstr, fluentCategories.get(fstr));
			sa0.setCoordinate(new BoundingBox(new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
			saRelations.add(sa0);

			//getCategory
			SpatialAssertionalRelation sa1 = new SpatialAssertionalRelation(reifiedCons.get(areaInsToConsIns.get(fstr)), paasiveObjCategories.get(reifiedCons.get(areaInsToConsIns.get(fstr))));
			sa1.setCoordinate(getPassiveObjectCoor(reifiedCons.get(areaInsToConsIns.get(fstr)))); 
			saRelations.add(sa1);

			metaSpatialReasoner(fstr, srules, saRelations);
		}

	}

	private BoundingBox getPassiveObjectCoor(String str){

		if(str.compareTo("table1") == 0)
			return new BoundingBox(new Bounds((781 - 35),(781 - 35)), new Bounds((781 + 35), (781 + 35)), new Bounds((1144 - 35), (1144 - 35)), new Bounds((1144 + 35), (1144 + 35)));

		else if(str.compareTo("table2") == 0)
			return new BoundingBox(new Bounds((1036 - 35),(1036 - 35)), new Bounds((1036 + 35), (1036 + 35)), new Bounds((1143 - 35), (1143 - 35)), new Bounds((1143 + 35), (1143 + 35)));

		else if(str.compareTo("counter1") == 0)
			return new BoundingBox(new Bounds((538 - 35),(538 - 35)), new Bounds((538 + 35), (538 + 35)), new Bounds((1007 - 70), (1007 - 70)), new Bounds((1007 + 70), (1007 + 70)));

		//		long x = passiveObjPose.get(passObjToPoseFlunet.get(str)).x();
		//		long y = passiveObjPose.get(passObjToPoseFlunet.get(str)).y();
		//		return new BoundingBox(new Bounds((x - 35),(x - 35)), new Bounds((x + 35), (x + 35)), new Bounds((y - 35), (y - 35)), new Bounds((y + 35), (y + 35)));



		return null;
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
			//blackboard/c
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
		cup.setName("cup10");
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
				System.out.println(response.getResult());
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
			for (Property p : f.getProperties()){
				if(p.getRoleType().contains("Constraint")) counter++;
				//else if(p.getRoleType().contains("hasPose")) counter++;
			}
					
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
							if(p.getRoleType().contains("hasPose")){
								passObjToPoseFlunet.put(f.getName(), p.getObjectFiller());
								getPosFluentOfPassiveObject(p.getObjectFiller());
							}
							if(p.getFillerType().compareTo("BoundingBox") == 0)
								passiveObjCoor.put(f.getName(), p.getObjectFiller());
						}			
					}
	}


	private void getPosFluentOfPassiveObject(String posfluent) {

		ServiceClient<RetrieveFluentRequest, RetrieveFluentResponse> getFluentByStringClient = null;
		try {
			getFluentByStringClient = node.newServiceClient("blackboard/retrieve_fluent", RetrieveFluent._TYPE);
		} 
		catch (org.ros.exception.ServiceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final RetrieveFluentRequest req= getFluentByStringClient.newMessage();
		req.setInstance(posfluent);

		getFluentByStringClient.call(req, new ServiceResponseListener<RetrieveFluentResponse>() {

			@Override
			public void onSuccess(RetrieveFluentResponse response) {

				double x = 0, y = 0;
				for (Property p : response.getFluent().getProperties()) {
					if(p.getRoleType().compareTo("hasX") == 0)
						x = (p.getFloatFiller() * 100);
					else if(p.getRoleType().compareTo("hasY") == 0)
						y = (p.getFloatFiller() * 100);
				}
				passiveObjPose.put(response.getFluent().getName(), new spatial.rectangleAlgebra.Point(x, y));

			}


			@Override
			public void onFailure(RemoteException arg0) {
				System.out.println("retrival failed");
			}
		});

	}


	private void getConstraintFluent(String getObjectFiller, final int counter) {

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
					Vector<SpatialRule> sr = new Vector<SpatialRule>();
					areaInsToConsIns.put(p.getObjectFiller(), response.getFluent().getName());
					for (int i = 0; i < spatialKnowledge.size(); i++) {
						if(spatialKnowledge.get(i).getFrom().compareTo(p.getFillerType()) == 0 && 
								spatialKnowledge.get(i).getTo().compareTo(paasiveObjCategories.get(reifiedCons.get(response.getFluent().getName()))) == 0){
							sr.add(spatialKnowledge.get(i));
							fluentCategories.put(p.getObjectFiller(), p.getFillerType());
						}
						if(spatialKnowledge.get(i).getFrom().compareTo(p.getFillerType()) == 0 && 
								spatialKnowledge.get(i).getTo().compareTo(p.getFillerType()) == 0)
							sr.add(spatialKnowledge.get(i));
					}
					relatedSpatialRelToFluent.put(p.getObjectFiller(), sr);

				}
				doneSubRoutines[counter] = true;
			}


			@Override
			public void onFailure(RemoteException arg0) {
				System.out.println("retrival failed");
			}
		});
	}

	private void getPoseflunet(String ineterstedArea, final int counter){

		ServiceClient<RetrieveFluentRequest, RetrieveFluentResponse> getFluentByStringClient = null;
		try {
			getFluentByStringClient = node.newServiceClient("blackboard/retrieve_fluent", RetrieveFluent._TYPE);
		} 
		catch (org.ros.exception.ServiceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final RetrieveFluentRequest req= getFluentByStringClient.newMessage();
		req.setInstance(ineterstedArea);

		getFluentByStringClient.call(req, new ServiceResponseListener<RetrieveFluentResponse>() {

			@Override
			public void onSuccess(RetrieveFluentResponse response) {

				for (Property p : response.getFluent().getProperties()) {
					if(p.getRoleType().compareTo("hasPose") == 0){
						//System.out.println(response.getFluent().getName() + " " + p.getObjectFiller());
						posFleuntofArea.put(response.getFluent().getName(), p.getObjectFiller());
					}
				}
				doneSubRoutines1[counter] = true;	
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
