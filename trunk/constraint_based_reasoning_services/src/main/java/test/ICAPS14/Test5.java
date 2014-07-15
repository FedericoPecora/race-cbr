package test.ICAPS14;

import java.awt.List;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraintSolver;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangularRegion;
import org.metacsp.multi.spatial.rectangleAlgebra.UnaryRectangleConstraint;
import org.metacsp.multi.spatial.rectangleAlgebraNew.toRemove.OntologicalSpatialProperty;
import org.metacsp.multi.spatioTemporal.SpatialFluent;
import org.metacsp.multi.spatioTemporal.SpatialFluentSolver;

import spatial.utility.SpatialAssertionalRelation2;
import spatial.utility.SpatialRule2;
import org.metacsp.time.APSPSolver;
import org.metacsp.time.Bounds;
import org.metacsp.utility.logging.MetaCSPLogging;
import org.metacsp.utility.timelinePlotting.TimelinePublisher;
import org.metacsp.utility.timelinePlotting.TimelineVisualizer;
import org.metacsp.framework.Constraint;
import org.metacsp.framework.ConstraintNetwork;
import org.metacsp.framework.ValueOrderingH;
import org.metacsp.framework.VariableOrderingH;

public class Test5 {
	
	//the Number of Objects are the same= 6 + 1 (including cup)
	//missplaced: 5
	//variance: 1 , 2, 3 arms
	//Initial situation: Holding
	static int arm_resources = 2;
	static int pad = 0;
	
	static long duration = 5;
	
	public static void main(String[] args) {


		MetaSpatialScheduler metaSpatioCasualSolver = new MetaSpatialScheduler(0, 1000, 0);
		
		
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
		SpatialSchedulable metaSpatialSchedulable = new SpatialSchedulable(varOH, valOH);
		SpatialFluentSolver groundSolver = (SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0];
		
		MetaCSPLogging.setLevel(MetaSpatialScheduler.class, Level.FINEST);
		MetaCSPLogging.setLevel(SpatialSchedulable.class, Level.FINEST);
		//#################################################################################################################
		//add metaOccupiedConstraint
		MetaOccupiedConstraint metaOccupiedConstraint = new MetaOccupiedConstraint(null, null);
		metaOccupiedConstraint.setPad(pad);
		//#################################################################################################################
		MetaCausalConstraint metaCausalConstraint = new MetaCausalConstraint(new int[] {arm_resources}, new String[] {"arm"}, "WellSetDeskDomain");
		Vector<SimpleOperator> operators = new Vector<SimpleOperator>();
		addOperator(operators);
		for (int i = 0; i < operators.size(); i++) {
			metaCausalConstraint.addOperator(operators.get(i));
		}
		
		//#################################################################################################################
		//this is spatial general and assetional rule
		Vector<SpatialRule2> srules = new Vector<SpatialRule2>();
		Vector<SpatialAssertionalRelation2> saRelations = new Vector<SpatialAssertionalRelation2>();
		HashMap<String, Rectangle> observation = new HashMap<String, Rectangle>();
		
		getSpatialKnowledge(srules);
		observation = getAssertionalRule(saRelations);
		insertCurrentStateCurrentGoal(groundSolver);
		for (int i = 0; i < operators.size(); i++) {
			metaSpatialSchedulable.addOperator(operators.get(i));
		}
		//#################################################################################################################
		//add spatial general and assertional rule to MetaSpatialFluentConstraint
		metaSpatialSchedulable.setSpatialRules(srules.toArray(new SpatialRule2[srules.size()]));
		metaSpatialSchedulable.setSpatialAssertionalRelations(saRelations);
		metaSpatialSchedulable.setInitialGoal(new String[]{"cup1"});
		
		
		//add meta constraint
		
		for (Schedulable sch : metaCausalConstraint.getSchedulingMetaConstraints()) {
			metaSpatioCasualSolver.addMetaConstraint(sch);
		}
		metaSpatioCasualSolver.addMetaConstraint(metaOccupiedConstraint);
		metaSpatioCasualSolver.addMetaConstraint(metaCausalConstraint);
		metaSpatioCasualSolver.addMetaConstraint(metaSpatialSchedulable);

//		System.out.println(((RectangleConstraintSolver)((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0]).
//				getConstraintSolvers()[0]).drawAlmostCentreRectangle(130, observation)); 
		
		
		long timeNow = Calendar.getInstance().getTimeInMillis();
		metaSpatioCasualSolver.backtrack();
		System.out.println("TOTAL TIME: " + (Calendar.getInstance().getTimeInMillis()-timeNow));
		
		//#####################################################################################################################
		//visualization
		ConstraintNetwork.draw(((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0]).getConstraintSolvers()[0].getConstraintNetwork(), "RA Constraint Network");
		ConstraintNetwork.draw(((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0]).getConstraintSolvers()[1].getConstraintNetwork(), "Activity Constraint Network");
		

		
		HashMap<String, Rectangle> recs = new HashMap<String, Rectangle>(); 
		for (String str : ((RectangleConstraintSolver)((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0])
				.getConstraintSolvers()[0]).extractAllBoundingBoxesFromSTPs().keySet()) {
			if(str.endsWith("1")){
//				System.out.println(str + " --> " +((RectangleConstraintSolver)((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0])
//						.getConstraintSolvers()[0]).extractAllBoundingBoxesFromSTPs().get(str).getAlmostCentreRectangle());
				recs.put( str,((RectangleConstraintSolver)((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0])
						.getConstraintSolvers()[0]).extractAllBoundingBoxesFromSTPs().get(str).getAlmostCentreRectangle());
			}
		}		
		
		System.out.println(((RectangleConstraintSolver)((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0]).
				getConstraintSolvers()[0]).drawAlmostCentreRectangle(130, observation));
		
		System.out.println(((RectangleConstraintSolver)((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0]).
				getConstraintSolvers()[0]).drawAlmostCentreRectangle(130, recs));
		
		
		
		ActivityNetworkSolver actSolver = ((ActivityNetworkSolver)((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0]).getConstraintSolvers()[1]);
		TimelinePublisher tp = new TimelinePublisher(actSolver, new Bounds(0,100), "robot1", "atLocation");
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
		
//		Collections.sort(starttimes.values());
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
	
	private static void setFluentintoNetwork(Vector<Constraint> cons, SpatialFluentSolver grounSpatialFluentSolver, String component, 
			String name, String symbolicDomain, markings mk, long release){
		
		SpatialFluent sf = (SpatialFluent)grounSpatialFluentSolver.createVariable(component);
		sf.setName(name);
		
		((RectangularRegion)sf.getInternalVariables()[0]).setName(name);
		((Activity)sf.getInternalVariables()[1]).setSymbolicDomain(symbolicDomain);
		((Activity)sf.getInternalVariables()[1]).setMarking(mk);
		
		if(mk.equals(markings.JUSTIFIED)){
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
		
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "table1", "at_table1()", markings.JUSTIFIED,  10);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "book1", "at_book1_table1()", markings.JUSTIFIED, 10);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "cup1", "at_cup1_table1()", markings.UNJUSTIFIED, 10);
		
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "monitor1", "at_monitor1_table1()", markings.JUSTIFIED, 10);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "keyboard1", "at_keyboard1_table1()", markings.JUSTIFIED,  10);		
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "pen1", "at_pen1_table1()", markings.JUSTIFIED,  10);		
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "notebook1", "at_notebook1_table1()", markings.JUSTIFIED, 10);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "penHolder1", "at_penHolder1_table1()", markings.JUSTIFIED, 10);
		//===================================================================================================================		
		Activity two = (Activity)grounSpatialFluentSolver.getConstraintSolvers()[1].createVariable("robot1");
		two.setSymbolicDomain("holding_cup1(arm)");
		two.setMarking(markings.JUSTIFIED);
		AllenIntervalConstraint releaseHolding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(10,10));
		releaseHolding.setFrom(two);
		releaseHolding.setTo(two);
		cons.add(releaseHolding);
		
		AllenIntervalConstraint durationHolding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		durationHolding.setFrom(two);
		durationHolding.setTo(two);
		cons.add(durationHolding);

		
		grounSpatialFluentSolver.getConstraintSolvers()[1].addConstraints(cons.toArray(new Constraint[cons.size()]));
		
	}

	
	private static Vector<SimpleOperator> getObjectPickAndPlaceOperator(String obj){

		Vector<SimpleOperator> operators = new Vector<SimpleOperator>();
		
		
		
		AllenIntervalConstraint pickFinishesAt = new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy, AllenIntervalConstraint.Type.Finishes);
		AllenIntervalConstraint atStartedByPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy, AllenIntervalConstraint.Type.StartedBy);
		AllenIntervalConstraint placeMetByholding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint holdingMetByPick = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());		
		AllenIntervalConstraint holdingDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint pickDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint atDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint placeDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		
		
//		SimpleOperator operator4 = new SimpleOperator("atLocation::at_"+ obj +"_table1()",
//				new AllenIntervalConstraint[] {atStartedByPlace},
//				new String[] {"robot1::place_"+ obj +"_table1(arm)"},
//				new int[] {0});
//		operator4.addConstraint(atDuration, 0, 0);
//		operators.add(operator4);
//
//		SimpleOperator operator5 = new SimpleOperator("robot1::place_"+ obj +"_table1(arm)",
//				new AllenIntervalConstraint[] {placeMetByholding},
//				new String[] {"robot1::holding_"+ obj +"(arm)"},
//				new int[] {1});
//		operator5.addConstraint(placeDuration, 0, 0);
//		operators.add(operator5);
//		
//		SimpleOperator operator6 = new SimpleOperator("robot1::holding_"+ obj +"(arm)",
//				new AllenIntervalConstraint[] {holdingMetByPick},
//				new String[] {"robot1::pick_"+ obj +"_table1(arm)"},
//				new int[] {1});
//		operator6.addConstraint(holdingDuration, 0, 0);
//		operators.add(operator6);
//
//		/*---*/SimpleOperator operator100 = new SimpleOperator("atLocation::at_"+ obj +"_tray1()",
//				new AllenIntervalConstraint[] {atStartedByPlace},
//				new String[] {"robot1::place_"+ obj +"_tray1(arm)"},
//				new int[] {0});
//		operator100.addConstraint(atDuration, 0, 0);
//		operators.add(operator100);		
//
//		/*---*/SimpleOperator operator111 = new SimpleOperator("robot1::place_"+ obj +"_tray1(arm)",
//				new AllenIntervalConstraint[] {placeMetByholding},
//				new String[] {"robot1::holding_"+ obj +"(arm)"},
//				new int[] {1});
//		operator111.addConstraint(placeDuration, 0, 0);
//		operators.add(operator111);
//
//		/*---*/SimpleOperator operator3cc = new SimpleOperator("robot1::holding_"+ obj +"(arm)",
//				new AllenIntervalConstraint[] {holdingMetByPick},
//				new String[] {"robot1::pick_"+ obj +"_tray1(arm)"},
//				new int[] {1});
//		operator3cc.addConstraint(holdingDuration, 0, 0);
//		operators.add(operator3cc);
//
//		
//		SimpleOperator operator2res = new SimpleOperator("robot1::pick_"+ obj +"_table1(arm)",
//				new AllenIntervalConstraint[] {pickFinishesAt},
//				new String[] {"atLocation::at_"+ obj +"_table1()"},
//				new int[] {1});
//		operator2res.addConstraint(pickDuration, 0, 0);
//		operators.add(operator2res);
//
//		/*---*/SimpleOperator operator411a = new SimpleOperator("robot1::pick_"+ obj +"_tray1(arm)",
//				new AllenIntervalConstraint[] {pickFinishesAt},
//				new String[] {"atLocation::at_"+ obj +"_tray1()"},
//				new int[] {1});
//		operator411a.addConstraint(holdingDuration, 0, 0);
//		operators.add(operator411a);

		//##########################################################################

		SimpleOperator operator6 = new SimpleOperator("robot1::holding_"+ obj +"(arm)",
				new AllenIntervalConstraint[] {holdingMetByPick},
				new String[] {"robot1::pick_"+ obj +"_table1(arm)"},
				new int[] {1});
		operator6.addConstraint(holdingDuration, 0, 0);
		operators.add(operator6);
		
		SimpleOperator operator3cc = new SimpleOperator("robot1::holding_"+ obj +"(arm)",
				new AllenIntervalConstraint[] {holdingMetByPick},
				new String[] {"robot1::pick_"+ obj +"_tray1(arm)"},
				new int[] {1});
		operator3cc.addConstraint(holdingDuration, 0, 0);
		operators.add(operator3cc);
		
		SimpleOperator operator111 = new SimpleOperator("robot1::place_"+ obj +"_tray1(arm)",
				new AllenIntervalConstraint[] {placeMetByholding},
				new String[] {"robot1::holding_"+ obj +"(arm)"},
				new int[] {1});
		operator111.addConstraint(placeDuration, 0, 0);
		operators.add(operator111);
		
		SimpleOperator operator411a = new SimpleOperator("robot1::pick_"+ obj +"_tray1(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt},
				new String[] {"atLocation::at_"+ obj +"_tray1()"},
				new int[] {1});
		operator411a.addConstraint(holdingDuration, 0, 0);
		operators.add(operator411a);
		
		SimpleOperator operator100 = new SimpleOperator("atLocation::at_"+ obj +"_tray1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_"+ obj +"_tray1(arm)"},
				new int[] {0});
		operator100.addConstraint(atDuration, 0, 0);
		operators.add(operator100);	
		

		
		
		SimpleOperator operator2res = new SimpleOperator("robot1::pick_"+ obj +"_table1(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt},
				new String[] {"atLocation::at_"+ obj +"_table1()"},
				new int[] {1});
		operator2res.addConstraint(pickDuration, 0, 0);
		operators.add(operator2res);
				
		
		SimpleOperator operator4 = new SimpleOperator("atLocation::at_"+ obj +"_table1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_"+ obj +"_table1(arm)"},
				new int[] {0});
		operator4.addConstraint(atDuration, 0, 0);
		operators.add(operator4);

		SimpleOperator operator5 = new SimpleOperator("robot1::place_"+ obj +"_table1(arm)",
				new AllenIntervalConstraint[] {placeMetByholding},
				new String[] {"robot1::holding_"+ obj +"(arm)"},
				new int[] {1});
		operator5.addConstraint(placeDuration, 0, 0);
		operators.add(operator5);

		return operators;
	}
	
	private static void addOperator(Vector<SimpleOperator> operators) {
		
		operators.addAll(getObjectPickAndPlaceOperator("cup1"));
		operators.addAll(getObjectPickAndPlaceOperator("book1"));
		operators.addAll(getObjectPickAndPlaceOperator("keyboard1"));
		operators.addAll(getObjectPickAndPlaceOperator("pen1"));
		operators.addAll(getObjectPickAndPlaceOperator("monitor1"));		
		operators.addAll(getObjectPickAndPlaceOperator("penHolder1"));		
		operators.addAll(getObjectPickAndPlaceOperator("notebook1"));
		
		
		
	}

	private static void getSpatialKnowledge(Vector<SpatialRule2> srules){
		
				
		//Size part
		addSizeConstraint(srules, "table", 120, 120);
		addSizeConstraint(srules, "book", 10, 10);		
		addSizeConstraint(srules, "monitor", 45, 15);
		addSizeConstraint(srules, "keyboard", 40, 20);
		addSizeConstraint(srules, "notebook",15, 20);
		addSizeConstraint(srules, "cup", 5, 5);
		addSizeConstraint(srules, "pen", 1, 18);		
		addSizeConstraint(srules, "penHolder", 10, 5);

		
		

		//Every thing should be on the table		
		addOnTableConstraint(srules, "monitor");
		addOnTableConstraint(srules, "keyboard");
		addOnTableConstraint(srules, "notebook");
		addOnTableConstraint(srules, "pen");
		addOnTableConstraint(srules, "book");
		addOnTableConstraint(srules, "cup");
		addOnTableConstraint(srules, "penHolder");
		
		
		SpatialRule2 pen_notebook = new SpatialRule2("pen", "notebook", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(3,5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds() ))
				);
		srules.add(pen_notebook);

		SpatialRule2 notebook_keyboard = new SpatialRule2("notebook", "keyboard", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds() ))
				);
		srules.add(notebook_keyboard);
		
		SpatialRule2 keyboard_monitor = new SpatialRule2("keyboard", "monitor", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds() ))
				);
		srules.add(keyboard_monitor);

		SpatialRule2 cup_keyboard= new SpatialRule2("cup", "keyboard", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds() ))
				);
		srules.add(cup_keyboard);
		
		SpatialRule2 cup_book= new SpatialRule2("cup", "book", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds() ))
				);
		srules.add(cup_book);
		

		SpatialRule2 penHolder_book= new SpatialRule2("penHolder", "book", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Overlaps, AllenIntervalConstraint.Type.Overlaps.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds() ))
				);
		srules.add(penHolder_book);

		
	}
	
	private static void addSizeConstraint(Vector<SpatialRule2> srules, String str, long size_x, long size_y){
		
		SpatialRule2 sizeCon = new SpatialRule2(str, str, 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, new Bounds(size_x, size_x), new Bounds(size_y, size_y)));
		srules.add(sizeCon);
		
	}

	private static void addOnTableConstraint(Vector<SpatialRule2> srules, String str){
		
		Bounds withinReach_y_lower = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_y_upper = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_x_lower = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_x_upper = new Bounds(5, APSPSolver.INF);
		
		SpatialRule2 r8 = new SpatialRule2(str, "table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r8);
		
	}
	
	private static void insertAtConstraint(HashMap<String, Rectangle> recs, Vector<SpatialAssertionalRelation2> saRelations, 
			String str, long xl, long xu, long yl, long yu, boolean movable){
		
		if(xl == 0 && xu == 0 && yl == 0 && yu == 0){
			SpatialAssertionalRelation2 table_assertion = new SpatialAssertionalRelation2(str+"1", str);
			table_assertion.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
					new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
			OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
			tableOnto.setMovable(movable);
			table_assertion.setOntologicalProp(tableOnto);
			saRelations.add(table_assertion);			
			
		}
		else{
			SpatialAssertionalRelation2 table_assertion = new SpatialAssertionalRelation2(str+"1", str);
			table_assertion.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
					new Bounds(xl, xl), new Bounds(xu, xu), new Bounds(yl, yl), new Bounds(yu, yu)));
			OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
			tableOnto.setMovable(movable);
			table_assertion.setOntologicalProp(tableOnto);
			saRelations.add(table_assertion);			
			recs.put(str+"1", new Rectangle((int)(xl), (int)(yl), (int)(xu - xl), (int)(yu - yl)));
		}
		

	}
	
	private static HashMap<String, Rectangle> getAssertionalRule(Vector<SpatialAssertionalRelation2> saRelations){
		
		HashMap<String, Rectangle> recs = new HashMap<String, Rectangle>();

		
		insertAtConstraint(recs, saRelations, "table", 0, 120, 0, 120, false);
		insertAtConstraint(recs, saRelations, "cup", 0, 0, 0, 0, true);
		insertAtConstraint(recs, saRelations, "monitor", 25, 70, 80, 95, false);
		
//		insertAtConstraint(recs, saRelations, "book", 98, 108, 57, 67, true); //true
		insertAtConstraint(recs, saRelations, "book", 45, 55, 20, 30, true); //false
//		insertAtConstraint(recs, saRelations, "keyboard", 50, 90, 35, 55, true); //false not overlapped with cup
		insertAtConstraint(recs, saRelations, "keyboard", 56, 106, 20, 40, true); //false overlapped with cup
//		insertAtConstraint(recs, saRelations, "keyboard", 27, 67, 45, 65, true); //true

		
		insertAtConstraint(recs, saRelations, "pen", 6, 7, 20, 38, true); //false
		insertAtConstraint(recs, saRelations, "notebook", 100, 115, 60, 80, true); ////false 15 20		
		insertAtConstraint(recs, saRelations, "penHolder", 9, 19, 74, 79, true); //false //10, 5
		
		
//		insertAtConstraint(recs, saRelations, "pen", 28, 29, 22, 40, true); //true
//		insertAtConstraint(recs, saRelations, "notebook", 9, 24, 21, 41, true); //true
//		insertAtConstraint(recs, saRelations, "penHolder", 93, 103, 68, 73, true); //true
		


		return recs;

		
		

	}
	
}

