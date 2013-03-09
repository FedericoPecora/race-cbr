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
import meta.TCSP.TCSPSolver;
import multi.allenInterval.AllenIntervalConstraint;
import spatial.cardinal.CardinalConstraint;

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

import race_msgs.Fluent;
import race_msgs.FluentArray;
import spatial.cardinal.CardinalConstraint;
import spatial.rectangleAlgebra.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra.AugmentedRectangleConstraintSolver;
import spatial.rectangleAlgebra.BoundingBox;
import spatial.rectangleAlgebra.QualitativeAllenIntervalConstraint;
import spatial.rectangleAlgebra.QualitativeAllenIntervalConstraint.Type;
import spatial.rectangleAlgebra.RectangleConstraint;
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
	private HashMap<String, String> reifiedCons = new HashMap<String, String>(); //<manAreaConstrain1, tabel1> it stores all refied constraint  for each Passive Object 
	private ConnectedNode node;
	private Vector<SpatialRule> spatialKnowledge = new Vector<SpatialRule>();
	private HashMap<String, String> passiveObjCoor = new HashMap<String, String>();//<counter1, boundingBox1> : it is for querying BB to get table1, counter1, and table2 coordinate
	private HashMap<String, Vector<SpatialRule>> relatedSpatialRelToFluent = new HashMap<String, Vector<SpatialRule>>();//<manAreaLeft1, SpatialRule(From, To, AugmentedRAConstraint(2D bouned Allen))>
	private boolean[] doneSubRoutines = null;
	private boolean[] doneSubRoutines1 = null;
	private boolean[] doneSubRoutines2 = null;
	private static final String MYTOPIC = "blackboard/passiveObjectTopic";
	private HashMap<String, String> paasiveObjCategories = new HashMap<String, String>(); //<door1, Door>
	private HashMap<String, String> fluentCategories = new HashMap<String, String>(); //<manAreaLeft1, ManAreaLeft>
	private HashMap<String, Rectangle> recs = new HashMap<String, Rectangle>();
	private HashMap<String, CardinalConstraint.Type> regionsOrientation = new HashMap<String, CardinalConstraint.Type>();
	private HashMap<String, String> passObjToPoseFlunet = new HashMap<String, String>(); //<table1, poseTable1>
	private HashMap<String, spatial.rectangleAlgebra.Point> passiveObjPose = new HashMap<String, spatial.rectangleAlgebra.Point>(); //<poseTable1, Point(3321, 4521)>
	private HashMap<String, spatial.rectangleAlgebra.Point> passiveObjSize = new HashMap<String, spatial.rectangleAlgebra.Point>(); 
	private enum subroutin {GETFLUNET, GETPOSE, ADDFLUNETS};
	private ArrayList<Fluent> fluents = new ArrayList<Fluent>();

	
	@Override
	public void onStart(ConnectedNode connectedNode) {

		//MetaCSPLogging.setLevel(MetaSpatialConstraintSolver.class, Level.FINEST);
		//MetaCSPLogging.setLevel(RectangleConstraintSolver.class, Level.FINEST);
		this.node = connectedNode;

		StaticSpatialKnowledge.getSpatialKnowledge(spatialKnowledge);
		
		
		Subscriber<Fluent> fluentArrsubscriber = connectedNode.newSubscriber(MYTOPIC, Fluent._TYPE);
		fluentArrsubscriber.addMessageListener(new MessageListener<Fluent>() {
			@Override
			public void onNewMessage(Fluent message) {	
				System.out.println("get new furniture");
				onStaticKnowlegeLoaded(message);
			}
		});
		
		getPassiveObject();
		waitForTerminate(subroutin.GETFLUNET);

		
		fillPassiveObjPose();
		waitForTerminate(subroutin.GETPOSE);
		

		createSpatialCN();
		addCoordinatesToBB();
		waitForTerminate(subroutin.ADDFLUNETS);
		
		callAddFluentsService();
		System.exit(0);

	}
	
	private void callAddFluentsService() {
		ServiceClient<AddFluentsRequest, AddFluentsResponse> serviceClient = null;
		try {
			serviceClient = node.newServiceClient("blackboard/add_fluents", AddFluents._TYPE);

		} 
		catch (org.ros.exception.ServiceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final AddFluentsRequest request = serviceClient.newMessage();
		request.setFluents(fluents);
		
		serviceClient.call(request, new ServiceResponseListener<AddFluentsResponse>() {

			@Override
			public void onSuccess(AddFluentsResponse response) {
				System.out.println(fluents.size() + " fluents Added");
                System.out.println(response.getResult().getErrorMessage());
			}

			@Override
			public void onFailure(RemoteException arg0) {
				System.out.println("system call failed");

			}
		});

		
	}

	private void waitForTerminate(subroutin option){
		
		if(option.compareTo(subroutin.GETFLUNET) == 0){
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
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} while(!proceed);	
		}
		else if(option.compareTo(subroutin.GETPOSE) == 0){
			boolean proceed = false;
			do {
				if (doneSubRoutines1 != null) {
					proceed = true;
					for (boolean b : doneSubRoutines1) {
						if (!b) {
							proceed = false;
							break;
						}
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} while(!proceed);	
		}
		else if(option.compareTo(subroutin.ADDFLUNETS) == 0){
			boolean proceed = false;
			do {
				if (doneSubRoutines2 != null) {
					proceed = true;
					for (boolean b : doneSubRoutines2) {
						if (!b) {
							proceed = false;
							break;
						}
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} while(!proceed);	
		}
		
	}

	private void onStaticKnowlegeLoaded(Fluent message){
		
		getPassiveObject();

	}
	
	private void fillPassiveObjPose(){

		int counter = paasiveObjCategories.size();
		doneSubRoutines1 = new boolean[counter];
		Arrays.fill(doneSubRoutines1, false);
		int i = 0;
		for (String st : paasiveObjCategories.keySet()) {;
		getPosFluentOfPassiveObject(st, i++);
		}


	}

	private void addCoordinatesToBB() {
		
		int counter = 0;
		counter = recs.size();
		doneSubRoutines2 = new boolean[counter];
		Arrays.fill(doneSubRoutines2, false);
		int i = 0;

		for (String str : recs.keySet()) {
			addPoseFluent(str, "pose");
			addPoseFluent(str, "poseBB");
			addBBFleunt(str);
			updateFluentCoordinate(str, i++);
			
		}
		

	}

	private void addBBFleunt(String str) {

		Fluent f = node.getTopicMessageFactory().newFromType(Fluent._TYPE);
		f.setName("boundingBox" + str);
		f.setType("BoundingBox");
		FlexibleTimepoint ft1 = node.getTopicMessageFactory().newFromType(FlexibleTimepoint._TYPE);
		ft1.setLower(new Time(0));
		ft1.setUpper(new Time(0));
		f.setStartTime(ft1);
		FlexibleTimepoint ft2 = node.getTopicMessageFactory().newFromType(FlexibleTimepoint._TYPE);
		ft2.setLower(new Time(0));
		ft2.setUpper(new Time(0));
		f.setFinishTime(ft2);
		ArrayList<Property> props = new ArrayList<Property>();
		Property prop = node.getTopicMessageFactory().newFromType(Property._TYPE);
		prop.setRoleType("hasXSize");
		prop.setFillerType("xsd:float");
		prop.setFloatFiller((float)getBoundingBoxSizeRelativeToOrientation(str).x() / 100);
		props.add(prop);

		Property prop1 = node.getTopicMessageFactory().newFromType(Property._TYPE);
		prop1.setRoleType("hasYSize");
		prop1.setFillerType("xsd:float");
		prop1.setFloatFiller((float)getBoundingBoxSizeRelativeToOrientation(str).y() / 100);
		props.add(prop1);

		Property prop2 = node.getTopicMessageFactory().newFromType(Property._TYPE);
		prop2.setRoleType("hasZSize");
		prop2.setFillerType("xsd:float");
		prop2.setFloatFiller(0);
		props.add(prop2);
		
		//hasPose, Pose, poseBBPAERCounter1
		Property prop3 = node.getTopicMessageFactory().newFromType(Property._TYPE);
		prop3.setRoleType("hasPose");
		prop3.setFillerType("Pose");
		prop3.setObjectFiller("poseBB" + str);
		props.add(prop3);

		f.setProperties(props);
		fluents.add(f);

	}

	private spatial.rectangleAlgebra.Point getBoundingBoxSizeRelativeToOrientation(String str){


		if(regionsOrientation.get(str).compareTo(spatial.cardinal.CardinalConstraint.Type.North) == 0 ||
				regionsOrientation.get(str).compareTo(spatial.cardinal.CardinalConstraint.Type.South) == 0)
			return new spatial.rectangleAlgebra.Point((recs.get(str).height), (recs.get(str).width));
		else 
			return new spatial.rectangleAlgebra.Point((recs.get(str).width),(recs.get(str).height));
	}


	private void addPoseFluent(String str, String prefix) {

		Fluent f = node.getTopicMessageFactory().newFromType(Fluent._TYPE);
		f.setName(prefix + str);
		f.setType("Pose");
		FlexibleTimepoint ft1 = node.getTopicMessageFactory().newFromType(FlexibleTimepoint._TYPE);
		ft1.setLower(new Time(0));
		ft1.setUpper(new Time(0));
		f.setStartTime(ft1);
		FlexibleTimepoint ft2 = node.getTopicMessageFactory().newFromType(FlexibleTimepoint._TYPE);
		ft2.setLower(new Time(0));
		ft2.setUpper(new Time(0));
		f.setFinishTime(ft2);
		ArrayList<Property> props = new ArrayList<Property>();
		Property prop = node.getTopicMessageFactory().newFromType(Property._TYPE);
		prop.setRoleType("hasX");
		prop.setFillerType("xsd:float");
		prop.setFloatFiller((float)(recs.get(str).x + (recs.get(str).width /2)) / 100);
		props.add(prop);

		Property prop1 = node.getTopicMessageFactory().newFromType(Property._TYPE);
		prop1.setRoleType("hasY");
		prop1.setFillerType("xsd:float");
		prop1.setFloatFiller((float)(recs.get(str).y + (recs.get(str).height /2)) / 100);
		props.add(prop1);

		Property prop2 = node.getTopicMessageFactory().newFromType(Property._TYPE);
		prop2.setRoleType("hasZ");
		prop2.setFillerType("xsd:float");
		prop2.setFloatFiller(getZPose(str));
		props.add(prop2);


		
		//hasYaw
		Property prop3 = node.getTopicMessageFactory().newFromType(Property._TYPE);
		prop3.setRoleType("hasYaw");
		prop3.setFillerType("xsd:float");
		prop3.setFloatFiller(CardinalConstraint.CardinalRelationToMetricOrientation[regionsOrientation.get(str).ordinal()]);
		props.add(prop3);

		f.setProperties(props);
		fluents.add(f);

	}

	private void createSpatialCN() {

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


		long x = passiveObjPose.get(str).x();
		long y = passiveObjPose.get(str).y();
		long sizex = getPassiveObjSize(str).x();
		long sizey = getPassiveObjSize(str).y();

		return new BoundingBox(new Bounds((x - sizex),(x - sizex)), new Bounds((x + sizex), (x + sizex)), 
				new Bounds((y - sizey), (y - sizey)), new Bounds((y + sizey), (y + sizey)));

	}

	private spatial.rectangleAlgebra.Point getPassiveObjSize(String str){

		if(str.compareTo("table1") == 0 || str.compareTo("HorizontalTable") == 0)
			return new spatial.rectangleAlgebra.Point(35, 35);
		else if(str.compareTo("table2") == 0 || str.compareTo("VerticalTable") == 0)
			return new spatial.rectangleAlgebra.Point(35, 35);
		else if(str.compareTo("counter1") == 0 || str.compareTo("Counter") == 0)
			return new spatial.rectangleAlgebra.Point(35, 70);
		return null;

	}





	private void metaSpatialReasoner(String fstr, Vector<SpatialRule> srules,
			Vector<SpatialAssertionalRelation> saRelations) {

		MetaSpatialConstraintSolver metaSolver = new MetaSpatialConstraintSolver(0);

		MetaSpatialConstraint objectsPosition = new MetaSpatialConstraint();
		objectsPosition.setSpatialRules(srules.toArray(new SpatialRule[srules.size()]));
		objectsPosition.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation[saRelations.size()]));

		metaSolver.addMetaConstraint(objectsPosition);
		metaSolver.backtrack();

		int testx = objectsPosition.getRectangle(fstr).x + (objectsPosition.getRectangle(fstr).width / 2);
		int testy = objectsPosition.getRectangle(fstr).y + (objectsPosition.getRectangle(fstr).height / 2);
//		System.out.println(fstr +  " --- " + (float)testx/100 + ", "+ (float)testy/100 + " " +		
//				CardinalConstraint.CardinalRelationToMetricOrientation[regionsOrientation.get(fstr).ordinal()]);
		recs.put(fstr, objectsPosition.getRectangle(fstr));

	}


	private void getPassiveObject() {
		
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
					if(response.getFluents().size() != 0)
						onGetPassiveObjects(response.getFluents());
					
				}
	
				@Override
				public void onFailure(RemoteException arg0) {
					System.out.println("Get passive Objects is failed");
	
				}
			});

	}

	private float getZPose(String str){

		if(regionsOrientation.get(str).compareTo(spatial.cardinal.CardinalConstraint.Type.NO) == 0)
			return (float)(getPassiveObjSize(paasiveObjCategories.get(reifiedCons.get(areaInsToConsIns.get(str)))).x * 2 + 1) / 100;

		return (float)0.0;

	}
	
	private void updateFluentCoordinate(String st, final int counter) {

		Fluent f = node.getTopicMessageFactory().newFromType(Fluent._TYPE);
		f.setName(st);
		f.setType(fluentCategories.get(st));
		ArrayList<Property> props = new ArrayList<Property>();

		//[hasBoundingBox, BoundingBox, boundingBoxPAERCounter1]
		Property prop = node.getTopicMessageFactory().newFromType(Property._TYPE);
		prop.setRoleType("hasBoundingBox");
		prop.setFillerType("BoundingBox");
		prop.setObjectFiller("boundingBox" + st);
		props.add(prop);

		//[hasPose, Pose, posePAERCounter1]
		Property prop1 = node.getTopicMessageFactory().newFromType(Property._TYPE);
		prop1.setRoleType("hasPose");
		prop1.setFillerType("Pose");
		prop1.setObjectFiller("pose" + st);
		props.add(prop1);

		f.setProperties(props);
		fluents.add(f);
		doneSubRoutines2[counter] = true;


	}

	private void onGetPassiveObjects(List<Fluent> passiveObjectsFluents) {

		int counter = 0;
		for (Fluent f : passiveObjectsFluents) {
			System.out.println(f.getName());
			for (Property p : f.getProperties()){
				if(p.getRoleType().contains("Constraint"))counter++;
			}

		}
		//counter += passiveObjectsFluents.size() * 2;
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


	private void getPosFluentOfPassiveObject(final String posfluent, final int counter) {

		ServiceClient<RetrieveFluentRequest, RetrieveFluentResponse> getFluentByStringClient = null;
		try {
			getFluentByStringClient = node.newServiceClient("blackboard/retrieve_fluent", RetrieveFluent._TYPE);
		} 
		catch (org.ros.exception.ServiceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final RetrieveFluentRequest req= getFluentByStringClient.newMessage();

		req.setInstance("pose" + posfluent.toUpperCase().substring(0, 1) + posfluent.substring(1, posfluent.length()));


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
				passiveObjPose.put(posfluent, new spatial.rectangleAlgebra.Point(x, y));
				doneSubRoutines1[counter] = true;
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
					//					if(p.getRoleType().compareTo("hasRectangleAlgebraConstraintA") == 0);
					//						System.out.println("helloo" + p.getFillerType() + p.getObjectFiller());
					for (int i = 0; i < spatialKnowledge.size(); i++) {
						if(spatialKnowledge.get(i).getFrom().compareTo(p.getFillerType()) == 0 && 
								spatialKnowledge.get(i).getTo().compareTo(paasiveObjCategories.get(reifiedCons.get(response.getFluent().getName()))) == 0){
							sr.add(spatialKnowledge.get(i));
							regionsOrientation.put(p.getObjectFiller(), RectangleConstraint.getCardinalConstraint(
									QualitativeAllenIntervalConstraint.lookupTypeByInt(spatialKnowledge.get(i).getRaCons().getBoundedConstraintX().getType().ordinal()),
									QualitativeAllenIntervalConstraint.lookupTypeByInt(spatialKnowledge.get(i).getRaCons().getBoundedConstraintY().getType().ordinal())
									));
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


	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("race_spatial_reasoner");
	}
}
