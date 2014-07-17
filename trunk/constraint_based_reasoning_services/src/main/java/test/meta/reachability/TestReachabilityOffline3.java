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
import org.metacsp.meta.hybridPlanner.MetaInverseReachabilityConstraint;
import org.metacsp.meta.hybridPlanner.MetaMoveBaseManagerConstraint;
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
import org.metacsp.spatial.reachability.ConfigurationVariable;
import org.metacsp.spatial.reachability.ReachabilityConstraint;
import org.metacsp.spatial.utility.SpatialAssertionalRelation;
import org.metacsp.spatial.utility.SpatialRule;
import org.metacsp.time.APSPSolver;
import org.metacsp.time.Bounds;
import org.metacsp.utility.logging.MetaCSPLogging;
import org.metacsp.utility.timelinePlotting.TimelinePublisher;
import org.metacsp.utility.timelinePlotting.TimelineVisualizer;

import spatial.utility.SpatialRule2;


public class TestReachabilityOffline3 {
	
	//here is an example of both fork and knife has to moved


	static int pad = 0;    
	static long duration = 1000;

	public static void main(String[] args) {
	
		SimpleHybridPlanner simpleHybridPlanner = new SimpleHybridPlanner(0, 100000, 0);


		FluentBasedSimpleDomain.parseDomain(simpleHybridPlanner, "domains/reachability_test_desk.ddl", FluentBasedSimpleDomain.class); //did not terminate
		
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
		//add metaMovebaseManagerConstraint
		MetaMoveBaseManagerConstraint metaMoveBaseMangerConstraint = new MetaMoveBaseManagerConstraint(null, null);
		//#################################################################################################################		
		//this is spatial general and assetional rule
		Vector<SpatialRule> srules = new Vector<SpatialRule>();
		Vector<SpatialAssertionalRelation> saRelations = new Vector<SpatialAssertionalRelation>();
		HashMap<String, Rectangle> observation = new HashMap<String, Rectangle>();

		getSpatialKnowledge(srules);
		observation = getAssertionalRule(saRelations);
		simpleHybridPlanner.addObservation(observation);
		insertCurrentStateCurrentGoal(groundSolver);
		//#################################################################################################################
		//add metaMovebaseManagerConstraint
		MetaInverseReachabilityConstraint metaInverseReachabilityConstraint = new MetaInverseReachabilityConstraint(null, null);
		metaInverseReachabilityConstraint.setSpatialAssertionalRelations(saRelations);
		//#################################################################################################################		
		//add spatial general and assertional rule to MetaSpatialFluentConstraint
		metaSpatialAdherence.setSpatialRules(srules.toArray(new SpatialRule[srules.size()]));
		metaSpatialAdherence.setSpatialAssertionalRelations(saRelations);
		metaSpatialAdherence.setInitialGoal(new String[]{"at_cup1_table1"});


		//add meta constraint
		simpleHybridPlanner.addMetaConstraint(metaOccupiedConstraint);
		simpleHybridPlanner.addMetaConstraint(metaSpatialAdherence);
		simpleHybridPlanner.addMetaConstraint(metaInverseReachabilityConstraint);
		simpleHybridPlanner.addMetaConstraint(metaMoveBaseMangerConstraint);
		
		
		

		long timeNow = Calendar.getInstance().getTimeInMillis();
		simpleHybridPlanner.backtrack();
		
		System.out.println("TOTAL TIME: " + (Calendar.getInstance().getTimeInMillis()-timeNow));

		//#####################################################################################################################
		//visualization
//		ConstraintNetwork.draw(((SpatialFluentSolver)simpleHybridPlanner.getConstraintSolvers()[0]).getConstraintSolvers()[0].getConstraintNetwork(), "RA Constraint Network");
//		ConstraintNetwork.draw(((SpatialFluentSolver)simpleHybridPlanner.getConstraintSolvers()[0]).getConstraintSolvers()[1].getConstraintNetwork(), "Activity Constraint Network");

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
//		TimelinePublisher tp = new TimelinePublisher(actSolver, new Bounds(0,100), "RobotAction","RobotProprioception", "atLocation");
//		TimelineVisualizer viz = new TimelineVisualizer(tp);
//		tp.publish(false, false);
//		tp.publish(false, true);
//		tp.publish(true, false);
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
		
		System.out.println("fluenttttttttttttttttttttttttttttttts");
		for (int i = 0; i < ((SpatialFluentSolver)simpleHybridPlanner.getConstraintSolvers()[0]).getVariables().length; i++) {
			System.out.println(((SpatialFluentSolver)simpleHybridPlanner.getConstraintSolvers()[0]).getVariables()[i]);
		}

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

	private static SpatialFluent setFluentintoNetwork(Vector<Constraint> cons, SpatialFluentSolver grounSpatialFluentSolver, String component, 
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
		
		return sf;

	}

	private static void insertCurrentStateCurrentGoal(SpatialFluentSolver grounSpatialFluentSolver) {

		Vector<Constraint> cons = new Vector<Constraint>();

//        SpatialFluent sf1 = setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_robot1_manipulationArea_cup1_table1", "at_robot1_manipulationArea_cup1_table1()", markings.JUSTIFIED,  -1);
//        SpatialFluent sf2 = setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_table1_table1", "at_table1_table1()", markings.JUSTIFIED,  8);
//        SpatialFluent sf3 = setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_fork1_table1", "at_fork1_table1()", markings.JUSTIFIED, 8);
//        SpatialFluent sf4 = setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_knife1_table1", "at_knife1_table1()", markings.JUSTIFIED,8);
//        SpatialFluent sf5 = setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_cup1_table1", "at_cup1_table1()", markings.UNJUSTIFIED, -1);

        SpatialFluent sf1 = setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_robot1_manipulationArea_cup1_table1", "at_robot1_manipulationArea_cup1_table1()", markings.JUSTIFIED,  -1);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_table1_table1", "at_table1_table1()", markings.JUSTIFIED,  8);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_book1_table1", "at_book1_table1()", markings.JUSTIFIED, 8);
		SpatialFluent sf5 = setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_cup1_table1", "at_cup1_table1()", markings.UNJUSTIFIED, -1);
		
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_monitor1_table1", "at_monitor1_table1()", markings.JUSTIFIED, 8);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_keyboard1_table1", "at_keyboard1_table1()", markings.JUSTIFIED,  8);		
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_pen1_table1", "at_pen1_table1()", markings.JUSTIFIED,  8);		
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_notebook1_table1", "at_notebook1_table1()", markings.JUSTIFIED, 8);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "at_penHolder1_table1", "at_penHolder1_table1()", markings.JUSTIFIED, 8);
        
        
        
        
		//===================================================================================================================
		//initial State
		//===================================================================================================================

		Activity two = (Activity)grounSpatialFluentSolver.getConstraintSolvers()[1].createVariable("RobotProprioception");
		two.setSymbolicDomain("holding_arm1_cup1()");
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
		
		//add reachability constraint
		ReachabilityConstraint rc2 = new ReachabilityConstraint(ReachabilityConstraint.Type.baseplacingReachable);
		rc2.setFrom((ConfigurationVariable)sf5.getConfigurationVariable());
		rc2.setTo((ConfigurationVariable)sf1.getConfigurationVariable());

		

		
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
		
		grounSpatialFluentSolver.getConstraintSolvers()[2].addConstraint(rc2);
		grounSpatialFluentSolver.getConstraintSolvers()[1].addConstraints(cons.toArray(new Constraint[cons.size()]));

	}



	private static void getSpatialKnowledge(Vector<SpatialRule> srules){

		//Size part
		//addSizeConstraint(srules, "table_table", 120, 120);
		addSizeConstraint(srules, "book_table", 10, 10);		
		addSizeConstraint(srules, "monitor_table", 45, 15);
		addSizeConstraint(srules, "keyboard_table", 40, 20);
		addSizeConstraint(srules, "notebook_table",15, 20);
		addSizeConstraint(srules, "cup_table", 5, 5);
		addSizeConstraint(srules, "pen_table", 1, 18);		
		addSizeConstraint(srules, "penHolder_table", 10, 5);


		

		//Every thing should be on the table		
		addOnTableConstraint(srules, "monitor_table");
		addOnTableConstraint(srules, "keyboard_table");
		addOnTableConstraint(srules, "notebook_table");
		addOnTableConstraint(srules, "pen_table");
		addOnTableConstraint(srules, "book_table");
		addOnTableConstraint(srules, "cup_table");
		addOnTableConstraint(srules, "penHolder_table");

		SpatialRule pen_notebook = new SpatialRule("pen_table", "notebook_table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(3,5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds() ))
				);
		srules.add(pen_notebook);

		SpatialRule notebook_keyboard = new SpatialRule("notebook_table", "keyboard_table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds() ))
				);
		srules.add(notebook_keyboard);
		
		SpatialRule keyboard_monitor = new SpatialRule("keyboard_table", "monitor_table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds() ))
				);
		srules.add(keyboard_monitor);

		SpatialRule cup_keyboard= new SpatialRule("cup_table", "keyboard_table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds() ))
				);
		srules.add(cup_keyboard);
		
		SpatialRule cup_book= new SpatialRule("cup_table", "book_table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds() ))
				);
		srules.add(cup_book);
		

		SpatialRule penHolder_book= new SpatialRule("penHolder_table", "book_table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Overlaps, AllenIntervalConstraint.Type.Overlaps.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds() ))
				);
		srules.add(penHolder_book);

	}

	private static void addSizeConstraint(Vector<SpatialRule> srules, String str, long size_x, long size_y){
		
		SpatialRule sizeCon = new SpatialRule(str, str, 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, new Bounds(size_x, size_x), new Bounds(size_y, size_y)));
		srules.add(sizeCon);
		
	}


	private static void addOnTableConstraint(Vector<SpatialRule> srules, String str){

		Bounds withinReach_y_lower = new Bounds(5, APSPSolver.INF);
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
			String fluentId, String catg,long xl, long xu, long yl, long yu, boolean movable, boolean obstacle){

		if(xl == 0 && xu == 0 && yl == 0 && yu == 0){
			SpatialAssertionalRelation table_assertion = new SpatialAssertionalRelation(fluentId, catg);
			table_assertion.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
					new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
			OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
			tableOnto.setMovable(movable);
			tableOnto.setObstacle(obstacle);
			table_assertion.setOntologicalProp(tableOnto);
			saRelations.add(table_assertion);                       

		}
		else{
			SpatialAssertionalRelation table_assertion = new SpatialAssertionalRelation(fluentId, catg);
			table_assertion.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
					new Bounds(xl, xl), new Bounds(xu, xu), new Bounds(yl, yl), new Bounds(yu, yu)));
			OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
			tableOnto.setMovable(movable);
			tableOnto.setObstacle(obstacle);
			table_assertion.setOntologicalProp(tableOnto);
			saRelations.add(table_assertion);
			recs.put(fluentId, new Rectangle((int)(xl), (int)(yl), (int)(xu - xl), (int)(yu - yl)));
		}


	}

	private static HashMap<String, Rectangle> getAssertionalRule(Vector<SpatialAssertionalRelation> saRelations){

		HashMap<String, Rectangle> recs = new HashMap<String, Rectangle>();


		//change in referance frame min and max 30-35
		

		
		//4 culprit
//		insertAtConstraint(recs, saRelations, "at_table1_table1", "table_table", 100, 220, 100, 220, false, false);
//		insertAtConstraint(recs, saRelations, "at_cup1_table1", "cup_table", 0, 0, 0, 0, true, false);
//		insertAtConstraint(recs, saRelations, "at_monitor1_table1","monitor_table" , 125, 170, 180, 195, false,false);
//		insertAtConstraint(recs, saRelations, "at_book1_table1", "book_table", 145, 155, 120, 130, true, false); //false
//		insertAtConstraint(recs, saRelations, "at_keyboard1_table1", "keyboard_table", 127, 167, 145, 165, true, false); //true
//		insertAtConstraint(recs, saRelations, "at_pen1_table1", "pen_table",106, 107, 120, 138, true, false); //false
//		insertAtConstraint(recs, saRelations, "at_notebook1_table1", "notebook_table", 200, 215, 160,180, true, false); ////false 15 20		
//		insertAtConstraint(recs, saRelations, "at_penHolder1_table1", "penHolder_table",109, 119, 174, 179, true, false); //false //10, 5

		//2 culprit
		insertAtConstraint(recs, saRelations, "at_table1_table1", "table_table", 100, 220, 100, 220, false, false);
		insertAtConstraint(recs, saRelations, "at_cup1_table1", "cup_table", 0, 0, 0, 0, true, false);
		insertAtConstraint(recs, saRelations, "at_monitor1_table1","monitor_table" , 125, 170, 180, 195, false,false);
		insertAtConstraint(recs, saRelations, "at_book1_table1", "book_table", 145, 155, 120, 130, true, false); //false
		insertAtConstraint(recs, saRelations, "at_keyboard1_table1", "keyboard_table", 127, 167, 145, 165, true, false); //true
        insertAtConstraint(recs, saRelations, "at_pen1_table1", "pen_table",126, 127, 112, 130, true, false); //true
		insertAtConstraint(recs, saRelations, "at_notebook1_table1", "notebook_table", 200, 215, 160,180, true, false); ////false 15 20		
		insertAtConstraint(recs, saRelations, "at_penHolder1_table1", "penHolder_table",184, 194, 166, 171, true, false); //true //10, 5
		
		
		return recs;
	}


}
