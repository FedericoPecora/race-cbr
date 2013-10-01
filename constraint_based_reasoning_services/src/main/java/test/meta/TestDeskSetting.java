package test.meta;

import java.awt.List;
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

import com.hp.hpl.jena.sparql.algebra.op.OpReduced;

public class TestDeskSetting {
	
	//oneCulprit example
	static int arm_resources = 1;
	static int pad = 0;
	
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
		getSpatialKnowledge(srules);
		getAssertionalRule(saRelations);
		insertCurrentStateCurrentGoal(groundSolver);
		for (int i = 0; i < operators.size(); i++) {
			metaSpatialSchedulable.addOperator(operators.get(i));
		}
		//#################################################################################################################
		//add spatial general and assertional rule to MetaSpatialFluentConstraint
		metaSpatialSchedulable.setSpatialRules(srules.toArray(new SpatialRule2[srules.size()]));
		metaSpatialSchedulable.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation2[saRelations.size()]));
		metaSpatialSchedulable.setInitialGoal(new String[]{"cup1"});
		
		
		//add meta constraint
		for (Schedulable sch : metaCausalConstraint.getSchedulingMetaConstraints()) {
			metaSpatioCasualSolver.addMetaConstraint(sch);
		}
		metaSpatioCasualSolver.addMetaConstraint(metaOccupiedConstraint);
		metaSpatioCasualSolver.addMetaConstraint(metaCausalConstraint);
		metaSpatioCasualSolver.addMetaConstraint(metaSpatialSchedulable);


		long timeNow = Calendar.getInstance().getTimeInMillis();
		metaSpatioCasualSolver.backtrack();
		System.out.println("TOTAL TIME: " + (Calendar.getInstance().getTimeInMillis()-timeNow));
		
		//#####################################################################################################################
		//visualization
		ConstraintNetwork.draw(((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0]).getConstraintSolvers()[0].getConstraintNetwork(), "RA Constraint Network");
		ConstraintNetwork.draw(((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0]).getConstraintSolvers()[1].getConstraintNetwork(), "Activity Constraint Network");
		
//		System.out.println(((RectangleConstraintSolver)((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0])
//				.getConstraintSolvers()[0]).extractBoundingBoxesFromSTPs("cup1").getAlmostCentreRectangle());
		
		
		for (String str : ((RectangleConstraintSolver)((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0])
				.getConstraintSolvers()[0]).extractAllBoundingBoxesFromSTPs().keySet()) {
			System.out.println(str + " --> " +((RectangleConstraintSolver)((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0])
					.getConstraintSolvers()[0]).extractAllBoundingBoxesFromSTPs().get(str).getAlmostCentreRectangle());
		}		

		
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
			String name, String symbolicDomain, markings mk, long duration, long release){
		
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
		
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "table1", "at_table1()", markings.JUSTIFIED, 1, 10);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "monitor1", "at_monitor1_table1()", markings.JUSTIFIED, 1, 10);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "keyboard1", "at_keyboard1_table1()", markings.JUSTIFIED, 1, 10);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "pen1", "at_pen1_table1()", markings.JUSTIFIED, 1, 10);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "book1", "at_book1_table1()", markings.JUSTIFIED, 1, 10);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "notebook1", "at_notebook1_table1()", markings.JUSTIFIED, 1, 10);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "cup1", "at_cup1_table1()", markings.UNJUSTIFIED, 1, 10);
		
		//===================================================================================================================		
		Activity two = (Activity)grounSpatialFluentSolver.getConstraintSolvers()[1].createVariable("robot1");
		two.setSymbolicDomain("holding_cup1(arm)");
		two.setMarking(markings.JUSTIFIED);
		AllenIntervalConstraint releaseHolding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(10,10));
		releaseHolding.setFrom(two);
		releaseHolding.setTo(two);
		cons.add(releaseHolding);
		
		AllenIntervalConstraint durationHolding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(1,APSPSolver.INF));
		durationHolding.setFrom(two);
		durationHolding.setTo(two);
		cons.add(durationHolding);

		
		grounSpatialFluentSolver.getConstraintSolvers()[1].addConstraints(cons.toArray(new Constraint[cons.size()]));
		
	}

	
	private static Vector<SimpleOperator> getObjectPickAndPlaceOperator(String obj){

		Vector<SimpleOperator> operators = new Vector<SimpleOperator>();
		
		long duration = 5;
		
		AllenIntervalConstraint pickFinishesAt = new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy, AllenIntervalConstraint.Type.Finishes);
		AllenIntervalConstraint atStartedByPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy, AllenIntervalConstraint.Type.StartedBy);
		AllenIntervalConstraint placeMetByholding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint holdingMetByPick = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());		
		AllenIntervalConstraint holdingDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint pickDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint atDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint placeDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		
		
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
		
		SimpleOperator operator6 = new SimpleOperator("robot1::holding_"+ obj +"(arm)",
				new AllenIntervalConstraint[] {holdingMetByPick},
				new String[] {"robot1::pick_"+ obj +"_table1(arm)"},
				new int[] {1});
		operator6.addConstraint(holdingDuration, 0, 0);
		operators.add(operator6);

		/*---*/SimpleOperator operator100 = new SimpleOperator("atLocation::at_"+ obj +"_tray1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_"+ obj +"_tray1(arm)"},
				new int[] {0});
		operator100.addConstraint(atDuration, 0, 0);
		operators.add(operator100);		

		/*---*/SimpleOperator operator111 = new SimpleOperator("robot1::place_"+ obj +"_tray1(arm)",
				new AllenIntervalConstraint[] {placeMetByholding},
				new String[] {"robot1::holding_"+ obj +"(arm)"},
				new int[] {1});
		operator111.addConstraint(placeDuration, 0, 0);
		operators.add(operator111);

		/*---*/SimpleOperator operator3cc = new SimpleOperator("robot1::holding_"+ obj +"(arm)",
				new AllenIntervalConstraint[] {holdingMetByPick},
				new String[] {"robot1::pick_"+ obj +"_tray1(arm)"},
				new int[] {1});
		operator3cc.addConstraint(holdingDuration, 0, 0);
		operators.add(operator3cc);

		
		SimpleOperator operator2res = new SimpleOperator("robot1::pick_"+ obj +"_table1(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt},
				new String[] {"atLocation::at_"+ obj +"_table1()"},
				new int[] {1});
		operator2res.addConstraint(pickDuration, 0, 0);
		operators.add(operator2res);

		/*---*/SimpleOperator operator411a = new SimpleOperator("robot1::pick_"+ obj +"_tray1(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt},
				new String[] {"atLocation::at_"+ obj +"_tray1()"},
				new int[] {1});
		operator411a.addConstraint(holdingDuration, 0, 0);
		operators.add(operator411a);

		return operators;
	}
	
	private static void addOperator(Vector<SimpleOperator> operators) {
		
		operators.addAll(getObjectPickAndPlaceOperator("cup1"));
		operators.addAll(getObjectPickAndPlaceOperator("book1"));
		operators.addAll(getObjectPickAndPlaceOperator("keyboard1"));
		operators.addAll(getObjectPickAndPlaceOperator("pen1"));
		operators.addAll(getObjectPickAndPlaceOperator("monitor1"));		
		
	}

	private static void getSpatialKnowledge(Vector<SpatialRule2> srules){
		
		
		Bounds monitor_size_x = new Bounds(45, 45);
		Bounds monitor_size_y = new Bounds(15, 15);
		
		Bounds keyboard_size_x = new Bounds(40, 40);
		Bounds keyboard_size_y = new Bounds(20, 20);
		
		Bounds notbook_size_x = new Bounds(15, 15);
		Bounds notbook_size_y = new Bounds(20, 20);
		
		Bounds cup_size_x = new Bounds(5, 5);
		Bounds cup_size_y = new Bounds(5, 5);

		Bounds pen_size_x = new Bounds(1, 1);
		Bounds pen_size_y = new Bounds(18, 18);

		Bounds book_size_x = new Bounds(10, 10);
		Bounds book_size_y = new Bounds(10, 10);
		
		Bounds table_size_x = new Bounds(120, 120);
		Bounds table_size_y = new Bounds(100, 100);
		
		Bounds withinReach_y_lower = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_y_upper = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_x_lower = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_x_upper = new Bounds(5, APSPSolver.INF);


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

		
		//Every thing should be on the table		
		SpatialRule2 r4 = new SpatialRule2("monitor", "table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r4);

		SpatialRule2 r5 = new SpatialRule2("keyboard", "table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r5);

		SpatialRule2 r6 = new SpatialRule2("notebook", "table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r6);

		SpatialRule2 r7 = new SpatialRule2("pen", "table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r7);
		
		SpatialRule2 r8 = new SpatialRule2("book", "table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r8);
		
		//Size part
		SpatialRule2 table_size = new SpatialRule2("table", "table", 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, table_size_x, table_size_y));
		srules.add(table_size);
		
		SpatialRule2 monitor_size = new SpatialRule2("monitor", "monitor", 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, monitor_size_x, monitor_size_y));
		srules.add(monitor_size);

		SpatialRule2 keyboard_size = new SpatialRule2("keyboard", "keyboard", 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, keyboard_size_x, keyboard_size_y));
		srules.add(keyboard_size);

		SpatialRule2 notbook_size = new SpatialRule2("notebook", "notebook", 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, notbook_size_x, notbook_size_y));
		srules.add(notbook_size);
		
		SpatialRule2 cup_size = new SpatialRule2("cup", "cup", 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, cup_size_x, cup_size_y));
		srules.add(cup_size);
		
		SpatialRule2 pen_size = new SpatialRule2("pen", "pen", 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, pen_size_x, pen_size_y));
		srules.add(pen_size);

		SpatialRule2 book_size = new SpatialRule2("book", "book", 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, book_size_x, book_size_y));
		srules.add(book_size);
		
		
	}
	
	private static void getAssertionalRule(Vector<SpatialAssertionalRelation2> saRelations){
		
	
		SpatialAssertionalRelation2 table_assertion = new SpatialAssertionalRelation2("table1", "table");
		table_assertion.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
				new Bounds(0, 0), new Bounds(120, 120), new Bounds(0, 0), new Bounds(100, 100)));
		OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
		tableOnto.setMovable(false);
		table_assertion.setOntologicalProp(tableOnto);
		saRelations.add(table_assertion);
		//............................................................................................

		SpatialAssertionalRelation2 monitor_assertion = new SpatialAssertionalRelation2("monitor1", "monitor");		
		monitor_assertion.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
				new Bounds(25, 25), new Bounds(70,70), new Bounds(80, 80), new Bounds(95, 95)));
		OntologicalSpatialProperty monitorOnto = new OntologicalSpatialProperty();
		monitorOnto.setMovable(false);
		monitor_assertion.setOntologicalProp(monitorOnto);
		saRelations.add(monitor_assertion);
		//.........................................................................................
		SpatialAssertionalRelation2 pen_assertion = new SpatialAssertionalRelation2("pen1", "pen");
		pen_assertion.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
				new Bounds(6, 6), new Bounds(7, 7), new Bounds(20, 20), new Bounds(38, 38)));
		OntologicalSpatialProperty penOnto = new OntologicalSpatialProperty();
		penOnto.setMovable(true);
		pen_assertion.setOntologicalProp(penOnto);
		saRelations.add(pen_assertion);

		//....................................................................................
		SpatialAssertionalRelation2 book_assertion = new SpatialAssertionalRelation2("book1", "book");
		book_assertion.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
				new Bounds(45,45), new Bounds(55, 55), new Bounds(20, 20), new Bounds(30, 30)));
		OntologicalSpatialProperty bookOnto = new OntologicalSpatialProperty();
		bookOnto.setMovable(true);
		book_assertion.setOntologicalProp(bookOnto);
		saRelations.add(book_assertion);

		//....................................................................................
		SpatialAssertionalRelation2 keyboard_assertion = new SpatialAssertionalRelation2("keyboard1", "keyboard");
		keyboard_assertion.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
				new Bounds(56, 56), new Bounds(106,106), new Bounds(20, 20), new Bounds(40, 40)));
		OntologicalSpatialProperty keyboardOnto = new OntologicalSpatialProperty();
		keyboardOnto.setMovable(true);
		keyboard_assertion.setOntologicalProp(keyboardOnto);
		saRelations.add(keyboard_assertion);
		//....................................................................................
		
		SpatialAssertionalRelation2 cup_assertion = new SpatialAssertionalRelation2("cup1", "cup");
		cup_assertion.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
				new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		OntologicalSpatialProperty cupOnto = new OntologicalSpatialProperty();
		cupOnto.setMovable(true);
		cup_assertion.setOntologicalProp(cupOnto);
		saRelations.add(cup_assertion);
		
		SpatialAssertionalRelation2 notebook_assertion = new SpatialAssertionalRelation2("notebook1", "notebook");
		notebook_assertion.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
				new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		OntologicalSpatialProperty notebookOnto = new OntologicalSpatialProperty();
		notebookOnto.setMovable(true);
		notebook_assertion.setOntologicalProp(notebookOnto);
		saRelations.add(notebook_assertion);

	}
	
}
