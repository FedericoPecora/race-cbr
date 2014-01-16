import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;


import meta.MetaCausalConstraint;
import meta.MetaCausalConstraint.markings;
import org.metacsp.meta.simplePlanner.SimpleOperator;
import meta.spatialSchedulable.MetaOccupiedConstraint;
import meta.spatialSchedulable.MetaSpatialScheduler;
import meta.spatialSchedulable.SpatialSchedulable;
import org.metacsp.meta.symbolsAndTime.Schedulable;
import org.metacsp.multi.activity.Activity;
import org.metacsp.multi.activity.ActivityNetworkSolver;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.BoundingBox;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraintSolver;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangularRegion;
import org.metacsp.multi.spatial.rectangleAlgebra.UnaryRectangleConstraint;
import org.metacsp.multi.spatial.rectangleAlgebraNew.toRemove.OntologicalSpatialProperty;
import org.metacsp.multi.spatioTemporal.SpatialFluent;
import org.metacsp.multi.spatioTemporal.SpatialFluentSolver;

import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.service.ServiceResponseBuilder;
import org.ros.node.topic.Subscriber;

import org.metacsp.framework.Constraint;
import org.metacsp.framework.ConstraintNetwork;
import org.metacsp.framework.ValueOrderingH;
import org.metacsp.framework.VariableOrderingH;
import geometry_msgs.Point;
import geometry_msgs.Pose;
import geometry_msgs.PoseStamped;
import geometry_msgs.Quaternion;
import geometry_msgs.TransformStamped;

//import pr2_papm_sim.GetNextCommand;
//import pr2_papm_sim.GetNextCommandRequest;
//import pr2_papm_sim.GetNextCommandResponse;
import race_msgs.GetNextCommand;
import race_msgs.GetNextCommandRequest;
import race_msgs.GetNextCommandResponse;
import race_msgs.ObjectHypothesis;

import spatial.rectangleAlgebra_OLD.SpatialAssertionalRelation;
import spatial.utility.SpatialAssertionalRelation2;
import spatial.utility.SpatialRule2;
import std_msgs.Header;
import tf.tfMessage;

import org.metacsp.time.APSPSolver;
import org.metacsp.time.Bounds;
import org.metacsp.utility.logging.MetaCSPLogging;




/**
 * @author Iran Mansouri
 * Orebro University 
 * Under RACE Poject
 */

public class RACEPR2HybridDispatcherService extends AbstractNodeMain{
	
	private int arm_resources = 2;
	private int pad = 2;
	private String context = "TestRACE";
	private Vector<SpatialAssertionalRelation2> saRelations = new Vector<SpatialAssertionalRelation2>();
	private HashMap<String, SpatialFluent> nameToSpatialFluent = new HashMap<String, SpatialFluent>();
	private boolean debug = true;
	private boolean triggerPerception = true;
	private Transformer transformer = new Transformer();
	private Subscriber<tfMessage> tfSubscriber;
	  
	private double table_x_map = 7.68 - 0.35; 
	private double table_y_map = 11.50 + 0.35;
	private double table_size = 0.70; 
	
	private VariableOrderingH varOH = new VariableOrderingH() {//Most critical conflict is the one with most activities
		@Override
		public int compare(ConstraintNetwork arg0, ConstraintNetwork arg1) {
			return arg1.getVariables().length - arg0.getVariables().length;
		}
		@Override
		public void collectData(ConstraintNetwork[] allMetaVariables) { }
	};
	
	private ValueOrderingH valOH = new ValueOrderingH() {// no value ordering
		@Override
		public int compare(ConstraintNetwork o1, ConstraintNetwork o2) { return 0; }
	};

	private ConnectedNode node;
	private SpatialSchedulable metaSpatialSchedulable = new SpatialSchedulable(varOH, valOH);
	private MetaSpatialScheduler metaSpatioCasualSolver = new MetaSpatialScheduler(0, 1000000, 0);
	private MetaCausalConstraint metaCausalConstraint = new MetaCausalConstraint(new int[] {arm_resources}, new String[] {"arm"}, context);
	private MetaOccupiedConstraint metaOccupiedConstraint = new MetaOccupiedConstraint(null, null);
	private SpatialFluentSolver groundSolver;
	private Vector<Activity> dispatchedActivity;
	private AllenIntervalConstraint deadlineConstraint; 
	
	@Override
	public void onStart(ConnectedNode connectedNode) {
		
		this.node = connectedNode;
		
	    transformer.setPrefix(GraphName.of(node.getParameterTree().getString("~tf_prefix", "")));
	    tfSubscriber = node.newSubscriber(GraphName.of("tf"), tf.tfMessage._TYPE); 
	    tfSubscriber.addMessageListener(new MessageListener<tfMessage>() {
	      @Override
	      public void onNewMessage(tfMessage message) {
	        for (TransformStamped transform : message.getTransforms()) {
	          transformer.updateTransform(transform);
	        }
	      }
	    });
	    
		groundSolver = (SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0];
		
		dispatchedActivity = new Vector<Activity>();
//		saRelations = new Vector<SpatialAssertionalRelation2>();
		
//		MetaCSPLogging.setLevel(MetaSpatialScheduler.class, Level.FINE);
//		MetaCSPLogging.setLevel(SpatialSchedulable.class, Level.FINE);
		//#################################################################################################################
		//add metaCausalConstraint
		
		Vector<SimpleOperator> operators = new Vector<SimpleOperator>();
		CausalKnowledge.getCausalKnowledge(context, operators);
		for (int i = 0; i < operators.size(); i++) {
			metaCausalConstraint.addOperator(operators.get(i));
		}
		//#################################################################################################################
		//add metaOccupiedConstraint
		metaOccupiedConstraint.setPad(pad);
		Vector<SpatialRule2> srules = new Vector<SpatialRule2>();
		SpatialKnowledge.getSpatialKnowledge(context, srules);		
		metaSpatialSchedulable.setSpatialRules(srules.toArray(new SpatialRule2[srules.size()]));
		metaSpatialSchedulable.setInitialGoal(new String[]{"cup1"});
		//#################################################################################################################
		//add resource MetaConstraint and occupiedMetaConstraint
		for (Schedulable sch : metaCausalConstraint.getSchedulingMetaConstraints()) {
			metaSpatioCasualSolver.addMetaConstraint(sch);
		}				
		metaSpatioCasualSolver.addMetaConstraint(metaOccupiedConstraint);
		metaSpatioCasualSolver.addMetaConstraint(metaCausalConstraint);
		metaSpatioCasualSolver.addMetaConstraint(metaSpatialSchedulable);

		//#################################################################################################################
		//setInitialState (e.g., holding)
		insertCurrentState("atLocation", "at_counter()", markings.JUSTIFIED, 1);
		insertCurrentState("atLocation", "at_cup1_counter()", markings.JUSTIFIED, 1);
		

		//setInitialSpatialFleunt (e.g., cup1)
		insertSpatialFluent("cup1", "atLocation", "at_cup1_table1()", markings.UNJUSTIFIED, -1);
		
		
//		insertSpatialFluent("eatingArea1", "atLocation", "at_eatingArea1()", markings.UNJUSTIFIED,  -1);
//		insertSpatialFluent("robot1_manArea1", "atLocation", "at_robot1_manArea1()", markings.UNJUSTIFIED, -1);		
//		insertSpatialFluent("cup1_eatingArea1","atLocation",  "at_cup1_eatingArea1()", markings.UNJUSTIFIED, -1);
//		
//		//counter
//		insertSpatialFluent("robot1_counter1", "atLocation", "at_robot1_manAreaCounter()", markings.JUSTIFIED, 1);
//		insertSpatialFluent("cup1_counter1", "atLocation" , "at_cup1_counter()", markings.JUSTIFIED, 1);
//		
//		
//		insertAtConstraint("cup1_counter1", "cup_counter", 10, 20, 30, 35, true);
//		insertAtConstraint("robot1_counter1", "robot_counter", 5, 49, 25, 75, true);
//		insertAtConstraint("robot1_manArea1", "robot_manArea", 5, 49, 25, 75, false);
//		metaSpatialSchedulable.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation2[saRelations.size()]));
		

		//#################################################################################################################
		
		node.newServiceServer("get_next_command", GetNextCommand._TYPE, new ServiceResponseBuilder<GetNextCommandRequest, GetNextCommandResponse>() {
			
			@Override
			public void build(GetNextCommandRequest req, GetNextCommandResponse response){
				
				long rostime = (long)req.getTime().secs;				
				long currentTime = rostime % 100000;				
				System.out.println("currentTime: " + currentTime);				

				
				
				Vector<String> currentState = new Vector<String>();
				for (String cs : req.getCurrentStates()) {
					currentState.add(cs);
				}

				
				System.out.println("Current State: " + currentState);

				//ADD CURRENT TIME - 1 TO THE END TIME OF THE ACTIVITY
				if(currentState.size() != 0)
					insertFinishTime(currentState, currentTime - 1);
				
				if(currentState.size() != 0 && currentState.get(0).contains("sense")){
					System.out.println("triggerPerception is ON");
					buildAssertionRules(req.getObservations());
//					groundSolver.getConstraintSolvers()[1].removeConstraint(deadlineConstraint);
					
					//insertFluent
					for (ObjectHypothesis marker : req.getObservations()) {
						
						if(marker.getType().compareTo("table") == 0)
							insertSpatialFluent(marker.getType().concat("1"), "atLocation", "at_table1()", markings.UNJUSTIFIED, -1);						
						else insertSpatialFluent(marker.getType().concat("1"), "atLocation", "at_" + marker.getType().concat("1") + "_table1()", markings.JUSTIFIED, currentTime);
						
//						insertSpatialFluent(marker.getType().concat("1") + "_eatingArea1()", "atLocation", "at_" + marker.getType().concat("1") + "_eatingArea1()", markings.JUSTIFIED, currentTime - 2);
						
					}
					System.out.println("saRelation.size: " + saRelations.size());
					metaSpatialSchedulable.setSpatialAssertionalRelations(saRelations);
					
				}
				
				metaSpatioCasualSolver.backtrack();
				Vector<Activity> toBeDispatchedActions = getNextActions(currentState, currentTime);
				response.setActionCommand(getActionCommandResponse(toBeDispatchedActions));
				
				for (int i = 0; i < toBeDispatchedActions.size(); i++) {
					//Set the refinement (placement) related to the ToBeDispatched 
					if(toBeDispatchedActions.get(i).getInternalVariables()[1].toString().contains("place") && 
							!toBeDispatchedActions.get(i).getInternalVariables()[1].toString().contains("tray"))
						response.setPlacingBB(getObjectActionRefinement(toBeDispatchedActions));						
				}

				
				
				if(debug){
					System.out.println("____________________________________________________________-");
					HashMap<Activity, Long> starttimes = new HashMap<Activity, Long>();
					for (int i = 0; i < ((ActivityNetworkSolver)groundSolver.getConstraintSolvers()[1]).getVariables().length; i++) {
						starttimes.put((Activity) ((ActivityNetworkSolver)groundSolver.getConstraintSolvers()[1]).getVariables()[i], ((Activity)((ActivityNetworkSolver)groundSolver.getConstraintSolvers()[1]).getVariables()[i]).getTemporalVariable().getStart().getLowerBound());			
					}

					//		Collections.sort(starttimes.values());
					starttimes =  sortHashMapByValuesD(starttimes);
					for (Activity act : starttimes.keySet()) {
						System.out.println(act + " --> " + starttimes.get(act));
					}
					System.out.println("____________________________________________________________-");
				}
				

				
				System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				for (int i = 0; i < toBeDispatchedActions.size(); i++) {
					dispatchedActivity.add(toBeDispatchedActions.get(i));
					System.out.println(toBeDispatchedActions.get(i));
				}
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			}


		}); 			
	}
	


	
    @Override
    public void onShutdown(Node node) {
      tfSubscriber.shutdown();
    }

    public Transformer getTransformer() {
      return transformer;
    }

    public void setTransformer(Transformer transformer) {
      this.transformer = transformer;
    }
    
	private List<ObjectHypothesis> getObjectActionRefinement(Vector<Activity> toBeDispatchedActions) {
		
		Vector<String> placements = new Vector<String>();
		for (Activity act : toBeDispatchedActions) {
			if(act.getInternalVariables()[1].toString().contains("place")){
				placements.add(act.getInternalVariables()[1].toString().split("_")[1]);
			}
		}
		
		ArrayList<ObjectHypothesis> ohList = new ArrayList<ObjectHypothesis>();
		
		for (String str : placements) {
			System.out.println("str: " + str);
			Rectangle rec = ((RectangleConstraintSolver)((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0])
					.getConstraintSolvers()[0]).extractBoundingBoxesFromSTPsByName(str).lastElement().getAlmostCentreRectangle();
			ObjectHypothesis objectypo = node.getTopicMessageFactory().newFromType(ObjectHypothesis._TYPE);
			String ns = str.replaceAll("\\d*$", "");
			objectypo.setType(ns);
			
			
			PoseStamped poseS = node.getTopicMessageFactory().newFromType(PoseStamped._TYPE);
			Pose pose = node.getTopicMessageFactory().newFromType(Pose._TYPE);
			Point p = node.getTopicMessageFactory().newFromType(Point._TYPE);
			p.setX((((double)rec.getCenterY() / 100) + table_x_map));
			p.setY(((((double)rec.getCenterX() / 100) * -1) + table_y_map));
			
			
			p.setZ(0.0);
			Quaternion q = node.getTopicMessageFactory().newFromType(Quaternion._TYPE);
			
			q.setW(1.0);
			pose.setOrientation(q);
			p.setZ(0.72);
			
			pose.setPosition(p);
			poseS.setPose(pose);
			
			
			Header header = node.getTopicMessageFactory().newFromType(Header._TYPE);
			header.setFrameId("/map");
			poseS.setHeader(header);			
			
			
			System.out.println("p1: " + (double)(rec.getX() + rec.getWidth()) /100 + " " + (double)(rec.getY() + rec.getHeight()) /100);
			System.out.println("x in map" + (((double)rec.getCenterY() / 100) + table_x_map));
			System.out.println("y in map" + ((((double)rec.getCenterX() / 100) * -1) + table_y_map));


			
			race_msgs.BoundingBox bb = node.getTopicMessageFactory().newFromType(race_msgs.BoundingBox._TYPE);
			bb.setPoseStamped(poseS);
			objectypo.setBbox(bb);
			objectypo.setPose(poseS);
			ohList.add(objectypo);
		}
		
		
		return ohList;
	}
	
	private List<String> getActionCommandResponse(Vector<Activity> toBeDispatchedActions) {
		ArrayList<String> commands = new ArrayList<String>();
		for (int i = 0; i < toBeDispatchedActions.size(); i++) {
			commands.add(toBeDispatchedActions.get(i).getInternalVariables()[1].toString());
		}
		
		return commands;
	}
	
	private void insertFinishTime(Vector<String> operator, long endTime) {
		
		Vector<String> currentState = new Vector<String>();
		for (String op : operator) {
			currentState.add(op.substring(op.indexOf("::")+2, op.length()));
		}
		
//		String currentState = operator.substring(operator.indexOf("::")+2, operator.length());
		for (int j = 0; j < dispatchedActivity.size(); j++) {
			for (int k = 0; k < currentState.size(); k++) {
				if(dispatchedActivity.get(j).getInternalVariables()[1].toString().contains(currentState.get(k))){
					for (int i = 0; i < ((ActivityNetworkSolver)groundSolver.getConstraintSolvers()[1]).getVariables().length; i++) {
						if(dispatchedActivity.get(j).equals(
								((ActivityNetworkSolver)groundSolver.getConstraintSolvers()[1]).getVariables()[i])){
							AllenIntervalConstraint deadline = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Deadline, new Bounds(endTime,endTime));
							deadline.setFrom(((ActivityNetworkSolver)groundSolver.getConstraintSolvers()[1]).getVariables()[i]);
							deadline.setTo(((ActivityNetworkSolver)groundSolver.getConstraintSolvers()[1]).getVariables()[i]);
							
							deadlineConstraint = deadline;
//							System.out.println("deadline: " + deadline);
							
							groundSolver.getConstraintSolvers()[1].addConstraint(deadline);
							
						}
					}
				}			
			}
		}
	}

	
	private Vector<Activity> getNextActions(Vector<String> operator, long currentTime) {

		Vector<Activity> toBeDispatchedActions = new Vector<Activity>();
		ActivityNetworkSolver actSolver = ((ActivityNetworkSolver)((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0]).getConstraintSolvers()[1]);
		
		Vector<String> currentState = new Vector<String>();
		for (String op : operator) {
			currentState.add(op.substring(op.indexOf("::")+2, op.length()));
		}

		//#####################################################################################
		HashMap<Activity, Long> starttimes = new HashMap<Activity, Long>();
		for (int i = 0; i < actSolver.getVariables().length; i++) {
			starttimes.put((Activity) (actSolver).getVariables()[i], ((Activity)(actSolver).getVariables()[i]).getTemporalVariable().getStart().getLowerBound());			
		}

		//		Collections.sort(starttimes.values());
		starttimes =  sortHashMapByValuesD(starttimes);
		for (Activity act : starttimes.keySet()) {
			if(act.getTemporalVariable().getEnd().getLowerBound() !=  act.getTemporalVariable().getEnd().getUpperBound()&& 
					!act.getSymbolicVariable().getSymbols()[0].toString().contains("holding") &&
					act.getComponent().compareTo("robot1") == 0 && 
					!isContain((act).getInternalVariables()[1].toString(), currentState)){
				System.out.println(act + " --> " + starttimes.get(act));				
				toBeDispatchedActions.add(act);			

				break;
			}
		}
		
		
		
		
		//#####################################################################################
//		String currentState = operator.substring(operator.indexOf("::")+2, operator.length());
		
		//fiter predicate from toBedispatched
//		for (int i = 0; i < actSolver.getVariables().length; i++) {
//			if(((Activity)actSolver.getVariables()[i]).getTemporalVariable().getStart().getLowerBound() <= currentTime &&
//					((Activity)actSolver.getVariables()[i]).getTemporalVariable().getEnd().getLowerBound() !=  ((Activity)actSolver.getVariables()[i]).getTemporalVariable().getEnd().getUpperBound()&&
//					((Activity)actSolver.getVariables()[i]).getComponent().compareTo("robot1") == 0 && 
//					!isContain(((Activity)actSolver.getVariables()[i]).getInternalVariables()[1].toString(), currentState)
//					)
//				toBeDispatchedActions.add((Activity) actSolver.getVariables()[i]);			
//		}

		
//		Collections.reverse(toBeDispatchedActions);
		
		return toBeDispatchedActions;
	}
	
	private boolean isContain(String st, Vector<String> currentState) {
		for (String cs : currentState) {
			if(st.contains(cs))
				return true;
		}
		return false;
	}

	
	private  void insertAtConstraint(String assertion, String concept, long xl, long xu, long yl, long yu, boolean movable){
		
		if(xl == 0 && xu == 0 && yl == 0 && yu == 0){
			SpatialAssertionalRelation2 table_assertion = new SpatialAssertionalRelation2(assertion, concept);
			table_assertion.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
					new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
			OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
			tableOnto.setMovable(movable);
			table_assertion.setOntologicalProp(tableOnto);
			saRelations.add(table_assertion);			
			
		}
		else{
			SpatialAssertionalRelation2 table_assertion = new SpatialAssertionalRelation2(assertion, concept);
			table_assertion.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
					new Bounds(xl, xl), new Bounds(xu, xu), new Bounds(yl, yl), new Bounds(yu, yu)));
			OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
			tableOnto.setMovable(movable);
			table_assertion.setOntologicalProp(tableOnto);
			saRelations.add(table_assertion);			
		}
		

	}
	
	private void buildAssertionRules(List<ObjectHypothesis> hypothses) {

//		saRelations = new Vector<SpatialAssertionalRelation2>();
		
//		SpatialAssertionalRelation2 table_assertion = new SpatialAssertionalRelation2("eatingArea1", "eatingArea");
//		table_assertion.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
//				new Bounds(0, 0), new Bounds(70, 70), new Bounds(0, 0), new Bounds(35, 35)));
//		OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
//		tableOnto.setMovable(false);
//		table_assertion.setOntologicalProp(tableOnto);
//		saRelations.add(table_assertion);			
		


		
		for (ObjectHypothesis oh : hypothses) {
//			SpatialAssertionalRelation2 sa = new SpatialAssertionalRelation2(oh.getType() + "1" + "_eatingArea1", oh.getType());
			SpatialAssertionalRelation2 sa = new SpatialAssertionalRelation2(oh.getType() + "1", oh.getType());
			

			//filter immovable object from ontology
			if(oh.getType().compareTo("table") == 0){
				OntologicalSpatialProperty noneMOvable = new OntologicalSpatialProperty();
				noneMOvable.setMovable(false);
				sa.setOntologicalProp(noneMOvable);
			}
			else{
				OntologicalSpatialProperty movable = new OntologicalSpatialProperty();
				movable.setMovable(true);
				sa.setOntologicalProp(movable);
			}

			

			if(oh.getBbox().getPoseStamped().getPose().getPosition().getX() < 0)
				sa.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At,new Bounds(0, APSPSolver.INF), 
						new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF))) ;
			else{//bounded object as observed


//				geometry_msgs.PoseStamped p = node.getTopicMessageFactory().newFromType(geometry_msgs.PoseStamped._TYPE);
//				p.getHeader().setFrameId("/base_link");
//				p.getPose().getOrientation().setW(1.0);   // make valid quaternion
				
				System.out.println("Beforerrrr");
				geometry_msgs.PoseStamped p = oh.getBbox().getPoseStamped();
				System.out.println(
						"  frame_id> " + p.getHeader().getFrameId() +
						"  x: " + p.getPose().getPosition().getX() +
						", y: " + p.getPose().getPosition().getY() +
						", z: " + p.getPose().getPosition().getZ() +
						" ||| Orientation --- " +
						"  x: " + p.getPose().getOrientation().getX() +
						"  y: " + p.getPose().getOrientation().getY() +
						"  z: " + p.getPose().getOrientation().getZ() +
						"  w: " + p.getPose().getOrientation().getW()
						);
				System.out.println("AFTERRRER");
				try {
					getTransformer().transformPose(GraphName.of("/map"), p);
					System.out.println(
							"  frame_id> " + p.getHeader().getFrameId() +
							"  x: " + p.getPose().getPosition().getX() +
							", y: " + p.getPose().getPosition().getY() +
							", z: " + p.getPose().getPosition().getZ() +
							" ||| Orientation --- " +
							"  x: " + p.getPose().getOrientation().getX() +
							"  y: " + p.getPose().getOrientation().getY() +
							"  z: " + p.getPose().getOrientation().getZ() +
							"  w: " + p.getPose().getOrientation().getW()
							);
				} catch (java.lang.IllegalStateException e) {
					System.err.println(e);
				}
					
				long x_size = (long)((float)(oh.getBbox().getDimensions().getX() * 100) / 2);
				long y_size = (long)((float)(oh.getBbox().getDimensions().getY() * 100)/ 2);
				
				
				
				long x_center = (long)((-p.getPose().getPosition().getY() + table_y_map) * 100) + 1;
				long y_center = (long)((p.getPose().getPosition().getX() - table_x_map) * 100) + 1;
				
				System.out.println("x_center: " + x_center);
				System.out.println("y_center: " + y_center);
				
			    sa.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At,
			    		new Bounds((long)(x_center - x_size), (long)(x_center - x_size)), 
			    		new Bounds((long)(x_center + x_size), (long)(x_center + x_size)), 
			    		new Bounds((long)(y_center - y_size), (long)(y_center - y_size)), 
			    		new Bounds((long)(y_center + y_size), (long)(y_center + y_size))));
						
				System.out.println(oh.getType());
				System.out.println((long)(x_center - x_size) + " " + (long)(x_center - x_size));
				System.out.println((long)(x_center + x_size) + " " + (long)(x_center + x_size));
				System.out.println((long)(y_center - y_size) + " " + (long)(y_center - y_size));
				System.out.println((long)(y_center + y_size) + " " + (long)(y_center + y_size));
			}
			saRelations.add(sa);
		}
		

	}
	
	private void insertSpatialFluent(String fluentName, String componnetName, String symbolicDomainName, markings mark, long releaseTime){
		
		
		if(!nameToSpatialFluent.containsKey(fluentName)){
			Vector<Constraint> cons = new Vector<Constraint>();
			
			SpatialFluent sf = (SpatialFluent)groundSolver.createVariable(componnetName);
			sf.setName(fluentName);
			((RectangularRegion)sf.getInternalVariables()[0]).setName(fluentName);
			((Activity)sf.getInternalVariables()[1]).setSymbolicDomain(symbolicDomainName);
			((Activity)sf.getInternalVariables()[1]).setMarking(mark);
			
			dispatchedActivity.add(((Activity)sf.getInternalVariables()[1]));
			
			
			if(releaseTime != -1){
				AllenIntervalConstraint releaseOn = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(releaseTime,releaseTime));
				releaseOn.setFrom(sf.getActivity());
				releaseOn.setTo(sf.getActivity());
				cons.add(releaseOn);

				AllenIntervalConstraint onDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(1,APSPSolver.INF));
				onDuration.setFrom(sf.getActivity());
				onDuration.setTo(sf.getActivity());
				cons.add(onDuration);
				
				groundSolver.getConstraintSolvers()[1].addConstraints(cons.toArray(new Constraint[cons.size()]));
			}
			
			nameToSpatialFluent.put(fluentName, sf);
			
			
		}
//		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//		System.out.println(nameToSpatialFluent);
//		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}
	
	private void insertCurrentState(String componnetName, String symbolicDomainName, markings mark, long releaseTime){
		
		Vector<Constraint> cons = new Vector<Constraint>();
		
		Activity act = (Activity)groundSolver.getConstraintSolvers()[1].createVariable(componnetName);
		act.setSymbolicDomain(symbolicDomainName);
		act.setMarking(mark);
		dispatchedActivity.add(act);
		
		AllenIntervalConstraint releaseAct = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(releaseTime,releaseTime));
		releaseAct.setFrom(act);
		releaseAct.setTo(act);
		cons.add(releaseAct);

		AllenIntervalConstraint durationAct = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(1,APSPSolver.INF));
		durationAct.setFrom(act);
		durationAct.setTo(act);
		cons.add(durationAct);

		groundSolver.getConstraintSolvers()[1].addConstraints(cons.toArray(new Constraint[cons.size()]));
	}
	
	private long convertTime(Time stamp) {		
//	  	return (long)((stamp.toSeconds()*1000)%10e6);
		return (long)((stamp.toSeconds()/1000));
	}
	
	
	private static LinkedHashMap sortHashMapByValuesD(HashMap passedMap) {
		ArrayList mapKeys = new ArrayList(passedMap.keySet());
		ArrayList mapValues = new ArrayList(passedMap.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		LinkedHashMap sortedMap = 
				new LinkedHashMap();

		Iterator valueIt = ((java.util.List<SpatialRule2>) mapValues).iterator();
		while (valueIt.hasNext()) {
			long val = (Long) valueIt.next();
			Iterator keyIt = ((java.util.List<SpatialRule2>) mapKeys).iterator();

			while (keyIt.hasNext()) {
				Activity key = (Activity) keyIt.next();
				long comp1 = (Long) passedMap.get(key);
				long comp2 = val;

				if (comp1 == comp2){
					passedMap.remove(key);
					mapKeys.remove(key);
					sortedMap.put(key, val);
					break;
				}

			}

		}
		return sortedMap;
	}


	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("doro_get_next_command");
	}

}


