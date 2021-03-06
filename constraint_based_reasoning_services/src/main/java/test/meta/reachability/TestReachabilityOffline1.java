package test.meta.reachability;


import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;


import org.metacsp.framework.Constraint;
import org.metacsp.framework.ConstraintNetwork;
import org.metacsp.framework.ValueOrderingH;
import org.metacsp.framework.VariableOrderingH;
import org.metacsp.meta.hybridPlanner.FluentBasedSimpleDomain;
import org.metacsp.meta.hybridPlanner.MetaOccupiedConstraint;
import org.metacsp.meta.hybridPlanner.MetaSpatialAdherenceConstraint;
import org.metacsp.meta.hybridPlanner.SimpleHybridPlanner;
import org.metacsp.meta.simplePlanner.SimpleDomain.markings;
import org.metacsp.multi.activity.Activity;
import org.metacsp.multi.activity.ActivityNetworkSolver;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraintSolver;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangularRegion;
import org.metacsp.multi.spatial.rectangleAlgebra.UnaryRectangleConstraint;
import org.metacsp.multi.spatial.rectangleAlgebraNew.toRemove.OntologicalSpatialProperty;
import org.metacsp.multi.spatioTemporal.SpatialFluent;
import org.metacsp.multi.spatioTemporal.SpatialFluentSolver;
import org.metacsp.spatial.utility.SpatialAssertionalRelation;
import org.metacsp.spatial.utility.SpatialRule;
import org.metacsp.time.APSPSolver;
import org.metacsp.time.Bounds;
import org.metacsp.utility.logging.MetaCSPLogging;
import org.metacsp.utility.timelinePlotting.TimelinePublisher;
import org.metacsp.utility.timelinePlotting.TimelineVisualizer;


public class TestReachabilityOffline1 {

	//purely symbolic without metaConstraint
	//without explicitly separate arms (this is the characteristic of the domain)

	static int pad = 0;    
	static long duration = 1000;

	public static void main(String[] args) {


		SimpleHybridPlanner simpleHybridPlanner = new SimpleHybridPlanner(0, 100000, 0);


		FluentBasedSimpleDomain.parseDomain(simpleHybridPlanner, "domains/reachability_test_fixed.ddl", FluentBasedSimpleDomain.class); //did not terminate
		
		//Most critical conflict is the one with most activities 
		VariableOrderingH varOH = new VariableOrderingH() {
			@Override
			public int compare(ConstraintNetwork arg0, ConstraintNetwork arg1) {
				return arg1.getVariables().length - arg0.getVariables().length;
			}
			@Override
			public void collectData(ConstraintNetwork[] allMetaVariables) { }
		};
		// no value ordering
		ValueOrderingH valOH = new ValueOrderingH() {
			@Override
			public int compare(ConstraintNetwork o1, ConstraintNetwork o2) { return 0; }
		};
		MetaSpatialAdherenceConstraint metaSpatialAdherence = new MetaSpatialAdherenceConstraint(varOH, valOH);
		SpatialFluentSolver groundSolver = (SpatialFluentSolver)simpleHybridPlanner.getConstraintSolvers()[0];

		MetaCSPLogging.setLevel(SimpleHybridPlanner.class, Level.FINEST);
		MetaCSPLogging.setLevel(MetaSpatialAdherenceConstraint.class, Level.FINEST);
		//#################################################################################################################
		//add metaOccupiedConstraint
		MetaOccupiedConstraint metaOccupiedConstraint = new MetaOccupiedConstraint(null, null);
		metaOccupiedConstraint.setPad(pad);
		//#################################################################################################################
		//this is spatial general and assetional rule
		Vector<SpatialRule> srules = new Vector<SpatialRule>();
		Vector<SpatialAssertionalRelation> saRelations = new Vector<SpatialAssertionalRelation>();
		HashMap<String, Rectangle> observation = new HashMap<String, Rectangle>();

		getSpatialKnowledge(srules);
		observation = getAssertionalRule(saRelations);
		insertCurrentStateCurrentGoal(groundSolver);
		//#################################################################################################################
		//add spatial general and assertional rule to MetaSpatialFluentConstraint
		metaSpatialAdherence.setSpatialRules(srules.toArray(new SpatialRule[srules.size()]));
		metaSpatialAdherence.setSpatialAssertionalRelations(saRelations);
		metaSpatialAdherence.setInitialGoal(new String[]{"at_cup1_table1"});


		//add meta constraint

		simpleHybridPlanner.addMetaConstraint(metaOccupiedConstraint);
		simpleHybridPlanner.addMetaConstraint(metaSpatialAdherence);


		long timeNow = Calendar.getInstance().getTimeInMillis();
		simpleHybridPlanner.backtrack();
		
		System.out.println("TOTAL TIME: " + (Calendar.getInstance().getTimeInMillis()-timeNow));

		//#####################################################################################################################
		//visualization
		ConstraintNetwork.draw(((SpatialFluentSolver)simpleHybridPlanner.getConstraintSolvers()[0]).getConstraintSolvers()[0].getConstraintNetwork(), "RA Constraint Network");
		ConstraintNetwork.draw(((SpatialFluentSolver)simpleHybridPlanner.getConstraintSolvers()[0]).getConstraintSolvers()[1].getConstraintNetwork(), "Activity Constraint Network");



		HashMap<String, Rectangle> recs = new HashMap<String, Rectangle>(); 
		for (String str : ((RectangleConstraintSolver)((SpatialFluentSolver)simpleHybridPlanner.getConstraintSolvers()[0])
				.getConstraintSolvers()[0]).extractAllBoundingBoxesFromSTPs().keySet()) {
			if(str.endsWith("1")){
				System.out.println(str + " --> " +((RectangleConstraintSolver)((SpatialFluentSolver)simpleHybridPlanner.getConstraintSolvers()[0])
						.getConstraintSolvers()[0]).extractAllBoundingBoxesFromSTPs().get(str).getAlmostCentreRectangle());
				recs.put( str,((RectangleConstraintSolver)((SpatialFluentSolver)simpleHybridPlanner.getConstraintSolvers()[0])
						.getConstraintSolvers()[0]).extractAllBoundingBoxesFromSTPs().get(str).getAlmostCentreRectangle());
			}
		}               


		ActivityNetworkSolver actSolver = ((ActivityNetworkSolver)((SpatialFluentSolver)simpleHybridPlanner.getConstraintSolvers()[0]).getConstraintSolvers()[1]);
		TimelinePublisher tp = new TimelinePublisher(actSolver, new Bounds(0,100), "RobotAction","RobotProprioception", "atLocation");
		TimelineVisualizer viz = new TimelineVisualizer(tp);
		tp.publish(false, false);
		tp.publish(false, true);
		tp.publish(true, false);
		//#####################################################################################################################
		//sort Activity based on the start time for debugging purpose
		HashMap<Activity, Long> starttimes = new HashMap<Activity, Long>();
		for (int i = 0; i < actSolver.getVariables().length; i++) {
			starttimes.put((Activity) actSolver.getVariables()[i], ((Activity)actSolver.getVariables()[i]).getTemporalVariable().getStart().getLowerBound());                       
		}

		//          Collections.sort(starttimes.values());
		starttimes =  sortHashMapByValuesD(starttimes);
		for (Activity act : starttimes.keySet()) {
			System.out.println(act + " --> " + starttimes.get(act));
		}
		//#####################################################################################################################
	}

	private static LinkedHashMap sortHashMapByValuesD(HashMap passedMap) {
		ArrayList mapKeys = new ArrayList(passedMap.keySet());
		ArrayList mapValues = new ArrayList(passedMap.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		LinkedHashMap sortedMap =  new LinkedHashMap();

		Iterator valueIt = ((java.util.List<SpatialRule>) mapValues).iterator();
		while (valueIt.hasNext()) {
			long val = (Long) valueIt.next();
			Iterator keyIt = ((java.util.List<SpatialRule>) mapKeys).iterator();

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

	private static void setFluentintoNetwork(Vector<Constraint> cons, SpatialFluentSolver grounSpatialFluentSolver, String component, 
			String name, String symbolicDomain, markings mk, long release){

		SpatialFluent sf = (SpatialFluent)grounSpatialFluentSolver.createVariable(component);
		sf.setName(name);

		((RectangularRegion)sf.getInternalVariables()[0]).setName(name);
		((Activity)sf.getInternalVariables()[1]).setSymbolicDomain(symbolicDomain);
		((Activity)sf.getInternalVariables()[1]).setMarking(mk);

		if(mk.equals(markings.JUSTIFIED) && release != -1){
			AllenIntervalConstraint onDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
			onDuration.setFrom(sf.getActivity());
			onDuration.setTo(sf.getActivity());
			cons.add(onDuration);

			AllenIntervalConstraint releaseOn = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(release, release));
			releaseOn.setFrom(sf.getActivity());
			releaseOn.setTo(sf.getActivity());
			cons.add(releaseOn);                                    
		}

	}

	private static void insertCurrentStateCurrentGoal(SpatialFluentSolver grounSpatialFluentSolver) {

		Vector<Constraint> cons = new Vector<Constraint>();


//		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "table1", "at_robot1_table1()", markings.JUSTIFIED,  8);
//		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "fork1", "at_fork1_table1()", markings.JUSTIFIED, 8);
//		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "knife1", "at_knife1_table1()", markings.JUSTIFIED,8);
//		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "cup1", "at_cup1_table1()", markings.UNJUSTIFIED, -1);

		
//		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_robot1_table1", "at_robot1_table1()", markings.JUSTIFIED,  1);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_table1_table1", "at_table1_table1()", markings.JUSTIFIED,  8);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_fork1_table1", "at_fork1_table1()", markings.JUSTIFIED, 8);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_knife1_table1", "at_knife1_table1()", markings.JUSTIFIED,8);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_cup1_table1", "at_cup1_table1()", markings.UNJUSTIFIED, -1);

		//===================================================================================================================
		//initial State
		//===================================================================================================================

		Activity two = (Activity)grounSpatialFluentSolver.getConstraintSolvers()[1].createVariable("RobotProprioception");
		two.setSymbolicDomain("holding_cup1()");
		two.setMarking(markings.JUSTIFIED);
		AllenIntervalConstraint releaseHolding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(1,1));
		releaseHolding.setFrom(two);
		releaseHolding.setTo(two);
		cons.add(releaseHolding);

		AllenIntervalConstraint durationHolding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		durationHolding.setFrom(two);
		durationHolding.setTo(two);
		cons.add(durationHolding);
		
		Activity two1 = (Activity)grounSpatialFluentSolver.getConstraintSolvers()[1].createVariable("atLocation");
		two1.setSymbolicDomain("at_robot1_table1()");
		two1.setMarking(markings.JUSTIFIED);
		AllenIntervalConstraint releaseatRobot = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(1,1));
		releaseatRobot.setFrom(two1);
		releaseatRobot.setTo(two1);
		cons.add(releaseatRobot);

		AllenIntervalConstraint durationHolding1 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		durationHolding1.setFrom(two1);
		durationHolding1.setTo(two1);
		cons.add(durationHolding1);
		
		
//		Activity two11 = (Activity)grounSpatialFluentSolver.getConstraintSolvers()[1].createVariable("atLocation");
//		two11.setSymbolicDomain("at_robot1_manipulationArea_cup1_table1()");
//		two11.setMarking(markings.JUSTIFIED);
//		AllenIntervalConstraint releaseatRobot1 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(1,1));
//		releaseatRobot1.setFrom(two11);
//		releaseatRobot1.setTo(two11);
//		cons.add(releaseatRobot1);
//
//		AllenIntervalConstraint durationHolding11 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
//		durationHolding11.setFrom(two11);
//		durationHolding11.setTo(two11);
//		cons.add(durationHolding11);

		grounSpatialFluentSolver.getConstraintSolvers()[1].addConstraints(cons.toArray(new Constraint[cons.size()]));

	}



	private static void getSpatialKnowledge(Vector<SpatialRule> srules){

		Bounds knife_size_x = new Bounds(4, 8);
		Bounds knife_size_y = new Bounds(18, 24);
		Bounds cup_size_x = new Bounds(4, 7);
		Bounds cup_size_y = new Bounds(4, 7);
		Bounds fork_size_x = new Bounds(4, 8);
		Bounds fork_size_y = new Bounds(18, 24);



		SpatialRule r7 = new SpatialRule("knife_table", "knife_table", 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, knife_size_x, knife_size_y));
		srules.add(r7);

		SpatialRule r8 = new SpatialRule("cup_table", "cup_table", 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, cup_size_x, cup_size_y));
		srules.add(r8);

		SpatialRule r9 = new SpatialRule("fork_table", "fork_table", 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, fork_size_x, fork_size_y));
		srules.add(r9);


		//Every thing should be on the table            
		addOnTableConstraint(srules, "fork_table");
		addOnTableConstraint(srules, "knife_table");
		addOnTableConstraint(srules, "cup_table");



		SpatialRule r2 = new SpatialRule("cup_table", "knife_table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(15, 20)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds() ))
				);
		srules.add(r2);



		SpatialRule r3 = new SpatialRule("cup_table", "fork_table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(15, 20)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During , AllenIntervalConstraint.Type.During.getDefaultBounds()))

				);
		srules.add(r3);


	}



	private static void addOnTableConstraint(Vector<SpatialRule> srules, String str){

		Bounds withinReach_y_lower = new Bounds(5, 20);
		Bounds withinReach_y_upper = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_x_lower = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_x_upper = new Bounds(5, APSPSolver.INF);

		SpatialRule r8 = new SpatialRule(str, "table_table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r8);

	}

	private static void insertAtConstraint(HashMap<String, Rectangle> recs, Vector<SpatialAssertionalRelation> saRelations, 
			String fluentId, String catg,long xl, long xu, long yl, long yu, boolean movable){

		if(xl == 0 && xu == 0 && yl == 0 && yu == 0){
			SpatialAssertionalRelation table_assertion = new SpatialAssertionalRelation(fluentId, catg);
			table_assertion.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
					new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
			OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
			tableOnto.setMovable(movable);
			table_assertion.setOntologicalProp(tableOnto);
			saRelations.add(table_assertion);                       

		}
		else{
			SpatialAssertionalRelation table_assertion = new SpatialAssertionalRelation(fluentId, catg);
			table_assertion.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
					new Bounds(xl, xl), new Bounds(xu, xu), new Bounds(yl, yl), new Bounds(yu, yu)));
			OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
			tableOnto.setMovable(movable);
			table_assertion.setOntologicalProp(tableOnto);
			saRelations.add(table_assertion);                       
			recs.put(fluentId, new Rectangle((int)(xl), (int)(yl), (int)(xu - xl), (int)(yu - yl)));
		}


	}

	private static HashMap<String, Rectangle> getAssertionalRule(Vector<SpatialAssertionalRelation> saRelations){

		HashMap<String, Rectangle> recs = new HashMap<String, Rectangle>();
		
//		//just knife should be replaced due to spatial heuristic
//		insertAtConstraint(recs, saRelations, "table", 0, 100, 0, 99, false);
//		insertAtConstraint(recs, saRelations, "fork", 31, 37, 13, 32, true);
//		insertAtConstraint(recs, saRelations, "knife", 40, 46, 10, 33, true);
//		insertAtConstraint(recs, saRelations, "cup", 0, 0, 0, 0, true);
		
		
		
		//both fork and knife should be replaced
		insertAtConstraint(recs, saRelations, "at_table1_table1", "table_table", 0, 60, 0, 99, false);
		insertAtConstraint(recs, saRelations, "at_fork1_table1", "fork_table", 20, 26, 13, 32, true);
		insertAtConstraint(recs, saRelations, "at_knife1_table1", "knife_table",30, 36, 10, 33, true);
		insertAtConstraint(recs, saRelations, "at_cup1_table1", "cup_table", 0, 0, 0, 0, true);

		return recs;
	}


}
