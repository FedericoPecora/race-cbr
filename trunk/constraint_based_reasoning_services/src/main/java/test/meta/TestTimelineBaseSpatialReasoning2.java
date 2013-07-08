package test.meta;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;

import orbital.math.Stat;

import meta.MetaCausalConstraint;
import meta.MetaSpatialFluentConstraint;
import meta.MetaSpatioCausalConstraintSolver;
import meta.MetaCausalConstraint.markings;
import meta.simplePlanner.SimpleOperator;
import meta.spatialSchedulable.MetaSpatialScheduler;
import meta.spatialSchedulable.SpatialSchedulable;
import meta.symbolsAndTime.Schedulable;
import multi.activity.Activity;
import multi.activity.ActivityNetworkSolver;
import multi.allenInterval.AllenIntervalConstraint;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraint2;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraintNetwork2;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraintSolver2;
import sandbox.spatial.rectangleAlgebra2.RectangularRegion2;
import sandbox.spatial.rectangleAlgebra2.SpatialAssertionalRelation2;
import sandbox.spatial.rectangleAlgebra2.SpatialFluent;
import sandbox.spatial.rectangleAlgebra2.SpatialFluentSolver;
import sandbox.spatial.rectangleAlgebra2.SpatialRule2;
import sandbox.spatial.rectangleAlgebra2.UnaryRectangleConstraint2;
import spatial.rectangleAlgebra.OntologicalSpatialProperty;
import time.APSPSolver;
import time.Bounds;
import utility.logging.MetaCSPLogging;
import utility.timelinePlotting.TimelinePublisher;
import utility.timelinePlotting.TimelineVisualizer;
import framework.Constraint;
import framework.ConstraintNetwork;
import framework.ValueOrderingH;
import framework.VariableOrderingH;

public class TestTimelineBaseSpatialReasoning2 {
	//two culprit examples
	static int arm_resources = 2;
	
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
		MetaCausalConstraint metaCausalConstraint = new MetaCausalConstraint(new int[] {arm_resources}, new String[] {"arm"}, "WellSetTableDomain");
		Vector<SimpleOperator> operators = new Vector<SimpleOperator>();
		addOperator(operators);
		for (int i = 0; i < operators.size(); i++) {
			metaCausalConstraint.addOperator(operators.get(i));
		}
		
		//#################################################################################################################
		//this is spatial general and assetional rule
		Vector<SpatialRule2> srules = new Vector<SpatialRule2>();
		Vector<SpatialAssertionalRelation2> saRelations = new Vector<SpatialAssertionalRelation2>();
		Vector<SpatialFluent> spatialFleunts = new Vector<SpatialFluent>();
		getSpatialKnowledge(srules);
		getAssertionalRule(saRelations);
		insertCurrentStateCurrentGoal(groundSolver, spatialFleunts);
		for (int i = 0; i < operators.size(); i++) {
			metaSpatialSchedulable.addOperator(operators.get(i));
		}
		//#################################################################################################################
		//add spatial general and assertional rule to MetaSpatialFluentConstraint
		metaSpatialSchedulable.setSpatialRules(srules.toArray(new SpatialRule2[srules.size()]));
		metaSpatialSchedulable.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation2[saRelations.size()]));
		metaSpatialSchedulable.setUsage(spatialFleunts.toArray(new SpatialFluent[spatialFleunts.size()]));
		
		for (Schedulable sch : metaCausalConstraint.getSchedulingMetaConstraints()) {
			metaSpatioCasualSolver.addMetaConstraint(sch);
		}				
		//add meta constraint
		metaSpatioCasualSolver.addMetaConstraint(metaCausalConstraint);
		metaSpatioCasualSolver.addMetaConstraint(metaSpatialSchedulable);
		metaSpatioCasualSolver.backtrack();
  
		//#####################################################################################################################
		//visualization
		ConstraintNetwork.draw(((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0]).getConstraintSolvers()[0].getConstraintNetwork(), "RA Constraint Network");
		ConstraintNetwork.draw(((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0]).getConstraintSolvers()[1].getConstraintNetwork(), "Activity Constraint Network");
		
		System.out.println(((RectangleConstraintSolver2)((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0])
				.getConstraintSolvers()[0]).extractBoundingBoxesFromSTPs("cup1").getAlmostCentreRectangle());

		
		ActivityNetworkSolver actSolver = ((ActivityNetworkSolver)((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0]).getConstraintSolvers()[1]);
		TimelinePublisher tp = new TimelinePublisher(actSolver, new Bounds(0,100), "robot1");
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
	
	private static void insertCurrentStateCurrentGoal(SpatialFluentSolver grounSpatialFluentSolver, 
			Vector<SpatialFluent> spatialFleunts) {
		
		Vector<Constraint> cons = new Vector<Constraint>();
		
		SpatialFluent tableFlunet = (SpatialFluent)grounSpatialFluentSolver.createVariable("table1");
		tableFlunet.setName("table1");
		((RectangularRegion2)tableFlunet.getInternalVariables()[0]).setName("table1");
		((Activity)tableFlunet.getInternalVariables()[1]).setSymbolicDomain("on_table1()");
		((Activity)tableFlunet.getInternalVariables()[1]).setMarking(markings.JUSTIFIED);
		spatialFleunts.add(tableFlunet);
		
//		AllenIntervalConstraint ontable1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
//		ontable1Duration.setFrom(tableFlunet.getActivity());
//		ontable1Duration.setTo(tableFlunet.getActivity());
//		cons.add(ontable1Duration);
		
		AllenIntervalConstraint releaseOnTable = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(20,20));
		releaseOnTable.setFrom(tableFlunet.getActivity());
		releaseOnTable.setTo(tableFlunet.getActivity());
		cons.add(releaseOnTable);
		//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		
		
//		AllenIntervalConstraint onCup1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(40,APSPSolver.INF));
//		onCup1Duration.setFrom(cupFlunet.getActivity());
//		onCup1Duration.setTo(cupFlunet.getActivity());
//		cons.add(onCup1Duration);
//		
//		AllenIntervalConstraint releaseOnCup = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(1,1));
//		releaseOnCup.setFrom(cupFlunet.getActivity());
//		releaseOnCup.setTo(cupFlunet.getActivity());
//		cons.add(releaseOnCup);

		
		//...................................................it comes to the scene later
		SpatialFluent knifeFlunet = (SpatialFluent)grounSpatialFluentSolver.createVariable("robot1");
		knifeFlunet.setName("knife1");
		((RectangularRegion2)knifeFlunet.getInternalVariables()[0]).setName("knife1");
		((Activity)knifeFlunet.getInternalVariables()[1]).setSymbolicDomain("on_knife1_table1()");
		((Activity)knifeFlunet.getInternalVariables()[1]).setMarking(markings.JUSTIFIED);
		spatialFleunts.add(knifeFlunet);
		
		
		SpatialFluent cupFlunet = (SpatialFluent)grounSpatialFluentSolver.createVariable("robot1");
		cupFlunet.setName("cup1");
		((RectangularRegion2)cupFlunet.getInternalVariables()[0]).setName("cup1");
		((Activity)cupFlunet.getInternalVariables()[1]).setSymbolicDomain("on_cup1_table1()");
		((Activity)cupFlunet.getInternalVariables()[1]).setMarking(markings.UNJUSTIFIED);
		spatialFleunts.add(cupFlunet);
		
		
		SpatialFluent forkFlunet = (SpatialFluent)grounSpatialFluentSolver.createVariable("robot1");
		forkFlunet.setName("fork1");
		((RectangularRegion2)forkFlunet.getInternalVariables()[0]).setName("fork1");
		((Activity)forkFlunet.getInternalVariables()[1]).setSymbolicDomain("on_fork1_table1()");
		((Activity)forkFlunet.getInternalVariables()[1]).setMarking(markings.JUSTIFIED);
		spatialFleunts.add(forkFlunet);
		
		AllenIntervalConstraint releaseOnFork = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(20,20));
		releaseOnFork.setFrom(forkFlunet.getActivity());
		releaseOnFork.setTo(forkFlunet.getActivity());
		cons.add(releaseOnFork);
		
		AllenIntervalConstraint onFork1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(1,APSPSolver.INF));
		onFork1Duration.setFrom(forkFlunet.getActivity());
		onFork1Duration.setTo(forkFlunet.getActivity());
		cons.add(onFork1Duration);
		
		AllenIntervalConstraint releaseOnKnife = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(20,20));
		releaseOnKnife.setFrom(knifeFlunet.getActivity());
		releaseOnKnife.setTo(knifeFlunet.getActivity());
		cons.add(releaseOnKnife);
		
		
		AllenIntervalConstraint onknife1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(1,APSPSolver.INF));
		onknife1Duration.setFrom(knifeFlunet.getActivity());
		onknife1Duration.setTo(knifeFlunet.getActivity());
		cons.add(onknife1Duration);
		
//		Activity one = (Activity)grounSpatialFluentSolver.getConstraintSolvers()[1].createVariable("robot1");
//		one.setSymbolicDomain("pick_cup1_table2(arm)");
//		one.setMarking(markings.JUSTIFIED);
//		AllenIntervalConstraint releasePickUp = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(1,1));
//		releasePickUp.setFrom(one);
//		releasePickUp.setTo(one);
//		cons.add(releasePickUp);
		
		
		Activity two = (Activity)grounSpatialFluentSolver.getConstraintSolvers()[1].createVariable("robot1");
		two.setSymbolicDomain("holding_cup1(arm)");
		two.setMarking(markings.JUSTIFIED);
		AllenIntervalConstraint releaseHolding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(10,10));
		releaseHolding.setFrom(two);
		releaseHolding.setTo(two);
		cons.add(releaseHolding);
		
		grounSpatialFluentSolver.getConstraintSolvers()[1].addConstraints(cons.toArray(new Constraint[cons.size()]));
		
	}

	private static void addOperator(Vector<SimpleOperator> operators) {
		
		AllenIntervalConstraint atCupAfterPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint atCup1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint placeCupAfterholding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint placeCup1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint holdingCupAfterPick = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint holdingCup1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint pickCup1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));

		
		AllenIntervalConstraint atKnifeAfterPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint atKnife1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint placeKnifeAfterholding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint placeKnife1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint holdingKnifeAfterPick = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint holdingKnife1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint pickKnife1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));

		
		AllenIntervalConstraint atForkAfterPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint atFork1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint placeForkAfterholding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint placeFork1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint holdingForkAfterPick = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint holdingFork1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint pickFork1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		

		
		SimpleOperator operator1 = new SimpleOperator("robot1::on_cup1_table1()",
				new AllenIntervalConstraint[] {atCupAfterPlace},
				new String[] {"robot1::place_cup1_table1(arm)"},
				new int[] {0});
		operator1.addConstraint(atCup1Duration, 0, 0);
		operators.add(operator1);

		SimpleOperator operator1a = new SimpleOperator("robot1::on_cup1_table2()",
				new AllenIntervalConstraint[] {atCupAfterPlace},
				new String[] {"robot1::place_cup1_table2(arm)"},
				new int[] {0});
		operator1a.addConstraint(atCup1Duration, 0, 0);
		operators.add(operator1a);
		
		SimpleOperator operator10 = new SimpleOperator("robot1::on_cup1_tray1()",
				new AllenIntervalConstraint[] {atCupAfterPlace},
				new String[] {"robot1::place_cup1_tray1(arm)"},
				new int[] {0});
		operator10.addConstraint(atCup1Duration, 0, 0);
		operators.add(operator10);
		
		SimpleOperator operator2 = new SimpleOperator("robot1::place_cup1_table1(arm)",
				new AllenIntervalConstraint[] {placeCupAfterholding},
				new String[] {"robot1::holding_cup1(arm)"},
				new int[] {1});
		operator2.addConstraint(placeCup1Duration, 0, 0);
		operators.add(operator2);

		SimpleOperator operator2a = new SimpleOperator("robot1::place_cup1_table2(arm)",
				new AllenIntervalConstraint[] {placeCupAfterholding},
				new String[] {"robot1::holding_cup1(arm)"},
				new int[] {100});
		operator2a.addConstraint(placeCup1Duration, 0, 0);
		operators.add(operator2a);
		
		SimpleOperator operator11 = new SimpleOperator("robot1::place_cup1_tray1(arm)",
				new AllenIntervalConstraint[] {placeCupAfterholding},
				new String[] {"robot1::holding_cup1(arm)"},
				new int[] {1});
		operator11.addConstraint(placeCup1Duration, 0, 0);
		operators.add(operator11);

		SimpleOperator operator3a = new SimpleOperator("robot1::holding_cup1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_cup1_table2(arm)"},
				new int[] {100});
		operator3a.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator3a);

		SimpleOperator operator3b = new SimpleOperator("robot1::holding_cup1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_cup1_table1(arm)"},
				new int[] {100});
		operator3b.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator3b);
		
		SimpleOperator operator3c = new SimpleOperator("robot1::holding_cup1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_cup1_tray1(arm)"},
				new int[] {1});
		operator3c.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator3c);

		SimpleOperator operator42 = new SimpleOperator("robot1::pick_cup1_table1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::on_cup1_table1()"},
				new int[] {100});
		operator42.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator42);

		SimpleOperator operator41 = new SimpleOperator("robot1::pick_cup1_table2(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::on_cup1_table2()"},
				new int[] {100});
		operator41.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator41);
		
		SimpleOperator operator411 = new SimpleOperator("robot1::pick_cup1_tray1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::on_cup1_tray1()"},
				new int[] {1});
		operator411.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator411);
	
		
		//		SimpleOperator operator13 = new SimpleOperator("robot1::holding_cup1(arm)",
		//		new AllenIntervalConstraint[] {holdingCupAfterPick},
		//		new String[] {"robot1::pick_cup1_table1(arm)"},
		//		new int[] {1});
		//operator13.addConstraint(holdingCup1Duration, 0, 0);
		//operators.add(operator13);


		
		//.....................................................................
		
		SimpleOperator operator4 = new SimpleOperator("robot1::on_knife1_table1()",
				new AllenIntervalConstraint[] {atKnifeAfterPlace},
				new String[] {"robot1::place_knife1_table1(arm)"},
				new int[] {0});
		operator4.addConstraint(atKnife1Duration, 0, 0);
		operators.add(operator4);
		
		
		SimpleOperator operator5 = new SimpleOperator("robot1::place_knife1_table1(arm)",
				new AllenIntervalConstraint[] {placeKnifeAfterholding},
				new String[] {"robot1::holding_knife1(arm)"},
				new int[] {1});
		operator5.addConstraint(placeKnife1Duration, 0, 0);
		operators.add(operator5);
		

		SimpleOperator operator6 = new SimpleOperator("robot1::holding_knife1(arm)",
				new AllenIntervalConstraint[] {holdingKnifeAfterPick},
				new String[] {"robot1::pick_knife1_table1(arm)"},
				new int[] {1});
		operator6.addConstraint(holdingKnife1Duration, 0, 0);
		operators.add(operator6);
		
		
		SimpleOperator operator2res = new SimpleOperator("robot1::pick_knife1_table1(arm)",
				new AllenIntervalConstraint[] {holdingKnifeAfterPick},
				new String[] {"robot1::on_knife1_table1()"},
				new int[] {1});
		operator2res.addConstraint(pickKnife1Duration, 0, 0);
		operators.add(operator2res);
		
		//........................
		
		SimpleOperator operator7 = new SimpleOperator("robot1::on_fork1_table1()",
				new AllenIntervalConstraint[] {atForkAfterPlace},
				new String[] {"robot1::place_fork1_table1(arm)"},
				new int[] {0});
		operator7.addConstraint(atFork1Duration, 0, 0);
		operators.add(operator7);
		
		
		SimpleOperator operator8 = new SimpleOperator("robot1::place_fork1_table1(arm)",
				new AllenIntervalConstraint[] {placeForkAfterholding},
				new String[] {"robot1::holding_fork1(arm)"},
				new int[] {1});
		operator8.addConstraint(placeFork1Duration, 0, 0);
		operators.add(operator8);
		

		SimpleOperator operator9 = new SimpleOperator("robot1::holding_fork1(arm)",
				new AllenIntervalConstraint[] {holdingForkAfterPick},
				new String[] {"robot1::pick_fork1_table1(arm)"},
				new int[] {1});
		operator9.addConstraint(holdingFork1Duration, 0, 0);
		operators.add(operator9);
		
		
		SimpleOperator operator4res = new SimpleOperator("robot1::pick_fork1_table1(arm)",
				new AllenIntervalConstraint[] {holdingKnifeAfterPick},
				new String[] {"robot1::on_fork1_table1()"},
				new int[] {1});
		operator4res.addConstraint(pickFork1Duration, 0, 0);
		operators.add(operator4res);

	}

	private static void getSpatialKnowledge(Vector<SpatialRule2> srules){
		
		Bounds knife_size_x = new Bounds(4, 8);
		Bounds knife_size_y = new Bounds(18, 24);
		Bounds cup_size_x = new Bounds(4, 7);
		Bounds cup_size_y = new Bounds(4, 7);
		Bounds fork_size_x = new Bounds(4, 8);
		Bounds fork_size_y = new Bounds(18, 24);
		Bounds withinReach_y_lower = new Bounds(5, 20);
		Bounds withinReach_y_upper = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_x_lower = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_x_upper = new Bounds(5, APSPSolver.INF);


		SpatialRule2 r2 = new SpatialRule2("cup", "knife", 
				new RectangleConstraint2(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(15,20)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds() ))
				);
		srules.add(r2);

		SpatialRule2 r3 = new SpatialRule2("cup", "fork", 
				new RectangleConstraint2(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(15,20)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During , AllenIntervalConstraint.Type.During.getDefaultBounds()))

				);
		srules.add(r3);

		SpatialRule2 r4 = new SpatialRule2("fork", "table", 
				new RectangleConstraint2(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r4);

		SpatialRule2 r5 = new SpatialRule2("knife", "table", 
				new RectangleConstraint2(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r5);

		SpatialRule2 r7 = new SpatialRule2("knife", "knife", 
				new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.Size, knife_size_x, knife_size_y));
		srules.add(r7);

		SpatialRule2 r8 = new SpatialRule2("cup", "cup", 
				new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.Size, cup_size_x, cup_size_y));
		srules.add(r8);

		SpatialRule2 r9 = new SpatialRule2("fork", "fork", 
				new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.Size, fork_size_x, fork_size_y));
		srules.add(r9);
	}
	
	private static void getAssertionalRule(Vector<SpatialAssertionalRelation2> saRelations){
		
		
		SpatialAssertionalRelation2 sa1 = new SpatialAssertionalRelation2("table1", "table");
		sa1.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, 
				new Bounds(0, 0), new Bounds(60, 60), new Bounds(0, 0), new Bounds(99, 99)));
		OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
		tableOnto.setMovable(false);
		sa1.setOntologicalProp(tableOnto);
//		sa1.associateSpatialFlunt(tableFlunet);
		
		saRelations.add(sa1);
		//............................................................................................

		SpatialAssertionalRelation2 sa3 = new SpatialAssertionalRelation2("fork1", "fork");		
		sa3.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, 
				new Bounds(20, 20), new Bounds(26, 26), new Bounds(13, 13), new Bounds(32, 32)));
		OntologicalSpatialProperty forkOnto = new OntologicalSpatialProperty();
		forkOnto.setMovable(true);
		sa3.setOntologicalProp(forkOnto);
//		sa3.associateSpatialFlunt(forkFlunet);
		saRelations.add(sa3);

		//		SpatialAssertionalRelation2 sa2 = new SpatialAssertionalRelation2("knife1", "knife");
		//		sa2.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, 
		//				new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		//		saRelations.add(sa2);
		
		//.........................................................................................
		SpatialAssertionalRelation2 sa2 = new SpatialAssertionalRelation2("knife1", "knife");
		sa2.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, 
				new Bounds(30,30), new Bounds(36,36), new Bounds(10, 10), new Bounds(33, 33)));
		OntologicalSpatialProperty knifeOnto = new OntologicalSpatialProperty();
		knifeOnto.setMovable(true);
		sa2.setOntologicalProp(knifeOnto);
//		sa2.associateSpatialFlunt(knifeFlunet);		
		saRelations.add(sa2);
		
		//....................................................................................
		SpatialAssertionalRelation2 sa4 = new SpatialAssertionalRelation2("cup1", "cup");
		sa4.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, 
				new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		OntologicalSpatialProperty cupOnto = new OntologicalSpatialProperty();
		cupOnto.setMovable(true);
		sa4.setOntologicalProp(cupOnto);

//		sa4.associateSpatialFlunt(cupFlunet);
		saRelations.add(sa4);

	}


	
	
}
