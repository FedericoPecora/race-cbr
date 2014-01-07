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



import org.metacsp.meta.simplePlanner.SimpleOperator;

import meta.spatialSchedulable.MetaCausalConstraint2;
import meta.spatialSchedulable.MetaCausalConstraint2.markings;
import meta.spatialSchedulable.MetaOccupiedConstraint2;
import meta.spatialSchedulable.MetaSpatialScheduler2;
import meta.spatialSchedulable.SpatialSchedulable2;

import org.metacsp.meta.symbolsAndTime.Schedulable;
import org.metacsp.multi.activity.Activity;
import org.metacsp.multi.activity.ActivityNetworkSolver;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.multi.spatial.blockAlgebra.BlockAlgebraConstraint;
import org.metacsp.multi.spatial.blockAlgebra.BlockConstraintSolver;
import org.metacsp.multi.spatial.blockAlgebra.RectangularCuboidRegion;
import org.metacsp.multi.spatial.blockAlgebra.UnaryBlockConstraint;
import org.metacsp.multi.spatial.rectangleAlgebraNew.toRemove.OntologicalSpatialProperty;
import org.metacsp.multi.spatioTemporal.SpatialFluent2;
import org.metacsp.multi.spatioTemporal.SpatialFluentSolver2;

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

public class TestBlockAlgebra1 {
	
	//oneCulprit example
	static int arm_resources = 2;
	static int pad = 0;
	
	public static void main(String[] args) {

		MetaSpatialScheduler2 metaSpatioCasualSolver = new MetaSpatialScheduler2(0, 1000, 0);
		
		
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
		SpatialSchedulable2 metaSpatialSchedulable = new SpatialSchedulable2(varOH, valOH);
		SpatialFluentSolver2 groundSolver = (SpatialFluentSolver2)metaSpatioCasualSolver.getConstraintSolvers()[0];
		
		MetaCSPLogging.setLevel(MetaSpatialScheduler2.class, Level.FINEST);
		MetaCSPLogging.setLevel(SpatialSchedulable2.class, Level.FINEST);
		//#################################################################################################################
		//add metaOccupiedConstraint
		MetaOccupiedConstraint2 metaOccupiedConstraint = new MetaOccupiedConstraint2(null, null);
		metaOccupiedConstraint.setPad(pad);
		//#################################################################################################################
		MetaCausalConstraint2 metaCausalConstraint = new MetaCausalConstraint2(new int[] {arm_resources}, new String[] {"arm"}, "WellSetTableDomain");
		Vector<SimpleOperator> operators = new Vector<SimpleOperator>();
		addOperator(operators);
		for (int i = 0; i < operators.size(); i++) {
			metaCausalConstraint.addOperator(operators.get(i));
		}
		
		//#################################################################################################################
		//this is spatial general and assetional rule
		Vector<SpatialRule2> srules = new Vector<SpatialRule2>();
		Vector<SpatialAssertionalRelation2> saRelations = new Vector<SpatialAssertionalRelation2>();
		Vector<SpatialFluent2> spatialFleunts = new Vector<SpatialFluent2>();
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
		metaSpatialSchedulable.setInitialGoal(new String[]{"cup1"});
		
		
		//add meta constraint
				
				
		for (Schedulable sch : metaCausalConstraint.getSchedulingMetaConstraints()) {
			metaSpatioCasualSolver.addMetaConstraint(sch);
		}
		metaSpatioCasualSolver.addMetaConstraint(metaOccupiedConstraint);
		metaSpatioCasualSolver.addMetaConstraint(metaCausalConstraint);
		metaSpatioCasualSolver.addMetaConstraint(metaSpatialSchedulable);
		

		metaSpatioCasualSolver.backtrack();

		//#####################################################################################################################
		//visualization
		ConstraintNetwork.draw(((SpatialFluentSolver2)metaSpatioCasualSolver.getConstraintSolvers()[0]).getConstraintSolvers()[0].getConstraintNetwork(), "RA Constraint Network");
		ConstraintNetwork.draw(((SpatialFluentSolver2)metaSpatioCasualSolver.getConstraintSolvers()[0]).getConstraintSolvers()[1].getConstraintNetwork(), "Activity Constraint Network");
		
		System.out.println(((BlockConstraintSolver)((SpatialFluentSolver2)metaSpatioCasualSolver.getConstraintSolvers()[0])
				.getConstraintSolvers()[0]).extractBoundingBoxesFromSTPs("cup1").getAlmostCentreRecCuboid());

		
		ActivityNetworkSolver actSolver = ((ActivityNetworkSolver)((SpatialFluentSolver2)metaSpatioCasualSolver.getConstraintSolvers()[0]).getConstraintSolvers()[1]);
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
	
	private static void insertCurrentStateCurrentGoal(SpatialFluentSolver2 grounSpatialFluentSolver, 
			Vector<SpatialFluent2> spatialFleunts) {
		
		Vector<Constraint> cons = new Vector<Constraint>();
		
		SpatialFluent2 tableFlunet = (SpatialFluent2)grounSpatialFluentSolver.createVariable("atLocation");
		tableFlunet.setName("table1");
		
		((RectangularCuboidRegion)tableFlunet.getInternalVariables()[0]).setName("table1");
		((Activity)tableFlunet.getInternalVariables()[1]).setSymbolicDomain("at_table1()");
		((Activity)tableFlunet.getInternalVariables()[1]).setMarking(markings.JUSTIFIED);
		spatialFleunts.add(tableFlunet);
		
		AllenIntervalConstraint ontable1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(1,APSPSolver.INF));
		ontable1Duration.setFrom(tableFlunet.getActivity());
		ontable1Duration.setTo(tableFlunet.getActivity());
		cons.add(ontable1Duration);
		
		AllenIntervalConstraint releaseOnTable = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(10,10));
		releaseOnTable.setFrom(tableFlunet.getActivity());
		releaseOnTable.setTo(tableFlunet.getActivity());
		cons.add(releaseOnTable);
		//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		
//		AllenIntervalConstraint releaseOnCup = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(1,1));
//		releaseOnCup.setFrom(cupFlunet.getActivity());
//		releaseOnCup.setTo(cupFlunet.getActivity());
//		cons.add(releaseOnCup);

		
		//...................................................it comes to the scene later
		SpatialFluent2 knifeFlunet = (SpatialFluent2)grounSpatialFluentSolver.createVariable("atLocation");
		knifeFlunet.setName("knife1");
		((RectangularCuboidRegion)knifeFlunet.getInternalVariables()[0]).setName("knife1");
		((Activity)knifeFlunet.getInternalVariables()[1]).setSymbolicDomain("at_knife1_table1()");
		((Activity)knifeFlunet.getInternalVariables()[1]).setMarking(markings.JUSTIFIED);
		spatialFleunts.add(knifeFlunet);
		
		
		SpatialFluent2 cupFlunet = (SpatialFluent2)grounSpatialFluentSolver.createVariable("atLocation");
		cupFlunet.setName("cup1");
		((RectangularCuboidRegion)cupFlunet.getInternalVariables()[0]).setName("cup1");
		((Activity)cupFlunet.getInternalVariables()[1]).setSymbolicDomain("at_cup1_table1()");
		((Activity)cupFlunet.getInternalVariables()[1]).setMarking(markings.UNJUSTIFIED);
		spatialFleunts.add(cupFlunet);
		
//		AllenIntervalConstraint onCup1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(1,APSPSolver.INF));
//		onCup1Duration.setFrom(cupFlunet.getActivity());
//		onCup1Duration.setTo(cupFlunet.getActivity());
//		cons.add(onCup1Duration);
		
		SpatialFluent2 forkFlunet = (SpatialFluent2)grounSpatialFluentSolver.createVariable("atLocation");
		forkFlunet.setName("fork1");
		((RectangularCuboidRegion)forkFlunet.getInternalVariables()[0]).setName("fork1");
		((Activity)forkFlunet.getInternalVariables()[1]).setSymbolicDomain("at_fork1_table1()");
		((Activity)forkFlunet.getInternalVariables()[1]).setMarking(markings.JUSTIFIED);
		spatialFleunts.add(forkFlunet);
		
		AllenIntervalConstraint releaseOnFork = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(10,10));
		releaseOnFork.setFrom(forkFlunet.getActivity());
		releaseOnFork.setTo(forkFlunet.getActivity());
		cons.add(releaseOnFork);
		
		AllenIntervalConstraint onFork1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(1,APSPSolver.INF));
		onFork1Duration.setFrom(forkFlunet.getActivity());
		onFork1Duration.setTo(forkFlunet.getActivity());
		cons.add(onFork1Duration);
		
		AllenIntervalConstraint releaseOnKnife = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(10,10));
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
		
		AllenIntervalConstraint durationHolding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(1,APSPSolver.INF));
		durationHolding.setFrom(two);
		durationHolding.setTo(two);
		cons.add(durationHolding);

		
		grounSpatialFluentSolver.getConstraintSolvers()[1].addConstraints(cons.toArray(new Constraint[cons.size()]));
		
	}

	private static void addOperator(Vector<SimpleOperator> operators) {
		
		long duration = 1;
		
//		AllenIntervalConstraint atStartedByPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.StartedBy, AllenIntervalConstraint.Type.StartedBy.getDefaultBounds());
//		AllenIntervalConstraint pickFinishesAt = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Finishes, AllenIntervalConstraint.Type.Finishes.getDefaultBounds());

		AllenIntervalConstraint pickFinishesAt = new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy, AllenIntervalConstraint.Type.Finishes);
		AllenIntervalConstraint atStartedByPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy, AllenIntervalConstraint.Type.StartedBy);

//		AllenIntervalConstraint pickFinishesAt = new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy, AllenIntervalConstraint.Type.OverlappedBy.getDefaultBounds());
//		AllenIntervalConstraint atStartedByPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy, AllenIntervalConstraint.Type.OverlappedBy.getDefaultBounds());

		
		AllenIntervalConstraint atCupAfterPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy, AllenIntervalConstraint.Type.OverlappedBy.getDefaultBounds());
		AllenIntervalConstraint atCup1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint placeCupAfterholding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint placeCup1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint holdingCupAfterPick = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint holdingCup1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint pickCup1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));


		AllenIntervalConstraint atKnifeAfterPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy, AllenIntervalConstraint.Type.OverlappedBy.getDefaultBounds());
		AllenIntervalConstraint atKnife1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint placeKnifeAfterholding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint placeKnife1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint holdingKnifeAfterPick = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint holdingKnife1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint pickKnife1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		

		AllenIntervalConstraint atForkAfterPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy, AllenIntervalConstraint.Type.OverlappedBy.getDefaultBounds());
		AllenIntervalConstraint atFork1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint placeForkAfterholding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint placeFork1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint holdingForkAfterPick = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint holdingFork1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint pickFork1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));


		SimpleOperator operator1 = new SimpleOperator("atLocation::at_cup1_table1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_cup1_table1(arm)"},
				new int[] {0});
		operator1.addConstraint(atCup1Duration, 0, 0);
		operators.add(operator1);

		SimpleOperator operator10 = new SimpleOperator("atLocation::at_cup1_tray1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
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

		SimpleOperator operator11 = new SimpleOperator("robot1::place_cup1_tray1(arm)",
				new AllenIntervalConstraint[] {placeCupAfterholding},
				new String[] {"robot1::holding_cup1(arm)"},
				new int[] {1});
		operator11.addConstraint(placeCup1Duration, 0, 0);
		operators.add(operator11);

		SimpleOperator operator3a = new SimpleOperator("robot1::holding_cup1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_cup1_table2(arm)"},
				new int[] {1});
		operator3a.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator3a);

		SimpleOperator operator3b = new SimpleOperator("robot1::holding_cup1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_cup1_table1(arm)"},
				new int[] {1});
		operator3b.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator3b);

		SimpleOperator operator3c = new SimpleOperator("robot1::holding_cup1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_cup1_tray1(arm)"},
				new int[] {1});
		operator3c.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator3c);

		SimpleOperator operator42 = new SimpleOperator("robot1::pick_cup1_table1(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt},
				new String[] {"atLocation::at_cup1_table1()"},
				new int[] {100});
		operator42.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator42);

		SimpleOperator operator41 = new SimpleOperator("robot1::pick_cup1_table2(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt},
				new String[] {"atLocation::at_cup1_table2()"},
				new int[] {100});
		operator41.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator41);

		SimpleOperator operator411 = new SimpleOperator("robot1::pick_cup1_tray1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"atLocation::at_cup1_tray1()"},
				new int[] {1});
		operator411.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator411);

		//.....................................................................
		SimpleOperator operator4 = new SimpleOperator("atLocation::at_knife1_table1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
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

		/*---*/SimpleOperator operator100 = new SimpleOperator("atLocation::at_knife1_tray1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_knife1_tray1(arm)"},
				new int[] {0});
		operator100.addConstraint(atCup1Duration, 0, 0);
		operators.add(operator100);		

		/*---*/SimpleOperator operator111 = new SimpleOperator("robot1::place_knife1_tray1(arm)",
				new AllenIntervalConstraint[] {placeCupAfterholding},
				new String[] {"robot1::holding_knife1(arm)"},
				new int[] {1});
		operator111.addConstraint(placeCup1Duration, 0, 0);
		operators.add(operator111);

		/*---*/SimpleOperator operator3cc = new SimpleOperator("robot1::holding_knife1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_knife1_tray1(arm)"},
				new int[] {1});
		operator3cc.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator3cc);

		
		SimpleOperator operator2res = new SimpleOperator("robot1::pick_knife1_table1(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt},
				new String[] {"atLocation::at_knife1_table1()"},
				new int[] {1});
		operator2res.addConstraint(pickKnife1Duration, 0, 0);
		operators.add(operator2res);

		/*---*/SimpleOperator operator411a = new SimpleOperator("robot1::pick_knife1_tray1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"atLocation::at_knife1_tray1()"},
				new int[] {1});
		operator411a.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator411a);

		
		//........................

		SimpleOperator operator7 = new SimpleOperator("atLocation::at_fork1_table1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
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

		
		/*---*/SimpleOperator operator101 = new SimpleOperator("atLocation::at_fork1_tray1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_fork1_tray1(arm)"},
				new int[] {0});
		operator101.addConstraint(atCup1Duration, 0, 0);
		operators.add(operator101);		

		
		/*---*/SimpleOperator operator11a = new SimpleOperator("robot1::place_fork1_tray1(arm)",
				new AllenIntervalConstraint[] {placeCupAfterholding},
				new String[] {"robot1::holding_fork1(arm)"},
				new int[] {1});
		operator11a.addConstraint(placeCup1Duration, 0, 0);
		operators.add(operator11a);


		/*---*/SimpleOperator operator3cb = new SimpleOperator("robot1::holding_fork1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_fork1_tray1(arm)"},
				new int[] {1});
		operator3cb.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator3cb);

		
		SimpleOperator operator4res = new SimpleOperator("robot1::pick_fork1_table1(arm)",
				new AllenIntervalConstraint[] {holdingKnifeAfterPick},
				new String[] {"pickOverlappedByAt"},
				new int[] {1});
		operator4res.addConstraint(pickFork1Duration, 0, 0);
		operators.add(operator4res);

		/*---*/SimpleOperator operator411b = new SimpleOperator("robot1::pick_fork1_tray1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"atLocation::at_fork1_tray1()"},
				new int[] {1});
		operator411b.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator411b);
		
	}

	private static void getSpatialKnowledge(Vector<SpatialRule2> srules){
		
		Bounds knife_size_x = new Bounds(4, 8);
		Bounds knife_size_y = new Bounds(18, 24);
		Bounds knife_size_z = new Bounds(5, 5);
		Bounds cup_size_x = new Bounds(4, 7);
		Bounds cup_size_y = new Bounds(4, 7);
		Bounds cup_size_z = new Bounds(5, 5);
		Bounds fork_size_x = new Bounds(4, 8);
		Bounds fork_size_y = new Bounds(18, 24);
		Bounds fork_size_z = new Bounds(5, 5);
		Bounds withinReach_y_lower = new Bounds(5, 20);
		Bounds withinReach_y_upper = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_x_lower = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_x_upper = new Bounds(5, APSPSolver.INF);


		SpatialRule2 r2 = new SpatialRule2("cup", "knife", 
				new BlockAlgebraConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(15,20)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals, AllenIntervalConstraint.Type.Equals.getDefaultBounds()))
				);
		srules.add(r2);

		SpatialRule2 r3 = new SpatialRule2("cup", "fork", 
				new BlockAlgebraConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(15,20)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During , AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals, AllenIntervalConstraint.Type.Equals.getDefaultBounds()))

				);
		srules.add(r3);

		SpatialRule2 r4 = new SpatialRule2("fork", "table", 
				new BlockAlgebraConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds()))
				);
		srules.add(r4);

		SpatialRule2 r5 = new SpatialRule2("knife", "table", 
				new BlockAlgebraConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds()))
				);
		srules.add(r5);

		SpatialRule2 r7 = new SpatialRule2("knife", "knife", 
				new UnaryBlockConstraint(UnaryBlockConstraint.Type.Size, knife_size_x, knife_size_y, knife_size_z));
		srules.add(r7);

		SpatialRule2 r8 = new SpatialRule2("cup", "cup", 
				new UnaryBlockConstraint(UnaryBlockConstraint.Type.Size, cup_size_x, cup_size_y, cup_size_z));
		srules.add(r8);

		SpatialRule2 r9 = new SpatialRule2("fork", "fork", 
				new UnaryBlockConstraint(UnaryBlockConstraint.Type.Size, fork_size_x, fork_size_y, fork_size_z));
		srules.add(r9);
	}
	
	private static void getAssertionalRule(Vector<SpatialAssertionalRelation2> saRelations){
		
	
		SpatialAssertionalRelation2 sa1 = new SpatialAssertionalRelation2("table1", "table");
		sa1.setUnaryAtBlockConstraint(new UnaryBlockConstraint(UnaryBlockConstraint.Type.At, 
				new Bounds(0, 0), new Bounds(100, 100), new Bounds(0, 0), new Bounds(99, 99), new Bounds(0, 0), new Bounds(1, 1)));
		OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
		tableOnto.setMovable(false);
		sa1.setOntologicalProp(tableOnto);
//		sa1.associateSpatialFlunt(tableFlunet);
		
		saRelations.add(sa1);
		//............................................................................................

		SpatialAssertionalRelation2 sa3 = new SpatialAssertionalRelation2("fork1", "fork");		
		sa3.setUnaryAtBlockConstraint(new UnaryBlockConstraint(UnaryBlockConstraint.Type.At, 
				new Bounds(31, 31), new Bounds(37, 37), new Bounds(13, 13), new Bounds(32, 32), new Bounds(1, 1), new Bounds(6, 6)));
		OntologicalSpatialProperty forkOnto = new OntologicalSpatialProperty();
		forkOnto.setMovable(true);
		sa3.setOntologicalProp(forkOnto);
//		sa3.associateSpatialFlunt(forkFlunet);
		saRelations.add(sa3);

		//		SpatialAssertionalRelation2 sa2 = new SpatialAssertionalRelation2("knife1", "knife");
		//		sa2.setUnaryAtRectangleConstraint(new UnaryBlockConstraint(UnaryBlockConstraint.Type.At, 
		//				new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		//		saRelations.add(sa2);
		
		//.........................................................................................
		SpatialAssertionalRelation2 sa2 = new SpatialAssertionalRelation2("knife1", "knife");
		sa2.setUnaryAtBlockConstraint(new UnaryBlockConstraint(UnaryBlockConstraint.Type.At, 
//				new Bounds(45,45), new Bounds(51,51), new Bounds(10, 10), new Bounds(33, 33)));
				new Bounds(40,40), new Bounds(46,46), new Bounds(10, 10), new Bounds(33, 33), new Bounds(1, 1), new Bounds(6, 6)));
		OntologicalSpatialProperty knifeOnto = new OntologicalSpatialProperty();
		knifeOnto.setMovable(true);
		sa2.setOntologicalProp(knifeOnto);

//		sa2.associateSpatialFlunt(knifeFlunet);		
		saRelations.add(sa2);
		
		//....................................................................................
		SpatialAssertionalRelation2 sa4 = new SpatialAssertionalRelation2("cup1", "cup");
		sa4.setUnaryAtBlockConstraint(new UnaryBlockConstraint(UnaryBlockConstraint.Type.At, 
				new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		OntologicalSpatialProperty cupOnto = new OntologicalSpatialProperty();
		cupOnto.setMovable(true);
		sa4.setOntologicalProp(cupOnto);

//		sa4.associateSpatialFlunt(cupFlunet);
		saRelations.add(sa4);

	}
	
}