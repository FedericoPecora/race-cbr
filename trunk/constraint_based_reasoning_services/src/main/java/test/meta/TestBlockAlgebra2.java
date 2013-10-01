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

public class TestBlockAlgebra2 {
	
	//oneCulprit example
	static int arm_resources = 1;
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
		MetaCausalConstraint2 metaCausalConstraint = new MetaCausalConstraint2(new int[] {arm_resources}, new String[] {"arm"}, "blockWorldDomain");
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
		metaSpatialSchedulable.setInitialGoal(new String[]{"block3"});
		
		
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
		ConstraintNetwork.draw(((SpatialFluentSolver2)metaSpatioCasualSolver.getConstraintSolvers()[0]).getConstraintSolvers()[0].getConstraintNetwork(), "BA Constraint Network");
		ConstraintNetwork.draw(((SpatialFluentSolver2)metaSpatioCasualSolver.getConstraintSolvers()[0]).getConstraintSolvers()[1].getConstraintNetwork(), "Activity Constraint Network");
		
		System.out.println(((BlockConstraintSolver)((SpatialFluentSolver2)metaSpatioCasualSolver.getConstraintSolvers()[0])
				.getConstraintSolvers()[0]).extractBoundingBoxesFromSTPs("block3").getAlmostCentreRecCuboid());

		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//		for (int i = 0; i < ((BlockConstraintSolver)((SpatialFluentSolver2)metaSpatioCasualSolver.getConstraintSolvers()[0])
//				.getConstraintSolvers()[0]).extractAllBoundingBoxesFromSTPs().length; i++) {
//			System.out.println(((BlockConstraintSolver)((SpatialFluentSolver2)metaSpatioCasualSolver.getConstraintSolvers()[0])
//					.getConstraintSolvers()[0]).extractAllBoundingBoxesFromSTPs()[i].getAlmostCentreRectangle());
//		}
		
		for (String str : ((BlockConstraintSolver)((SpatialFluentSolver2)metaSpatioCasualSolver.getConstraintSolvers()[0])
				.getConstraintSolvers()[0]).extractAllBoundingBoxesFromSTPs().keySet()) {
			System.out.println(str + "--> " +((BlockConstraintSolver)((SpatialFluentSolver2)metaSpatioCasualSolver.getConstraintSolvers()[0])
					.getConstraintSolvers()[0]).extractAllBoundingBoxesFromSTPs().get(str).getAlmostCentreRecCuboid());
			
		}
		
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		
		
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
		
		SpatialFluent2 block1Flunet = (SpatialFluent2)grounSpatialFluentSolver.createVariable("atLocation");
		block1Flunet.setName("block1");
		((RectangularCuboidRegion)block1Flunet.getInternalVariables()[0]).setName("block1");
		((Activity)block1Flunet.getInternalVariables()[1]).setSymbolicDomain("at_block1_table1()");
		((Activity)block1Flunet.getInternalVariables()[1]).setMarking(markings.JUSTIFIED);
		spatialFleunts.add(block1Flunet);
		
		AllenIntervalConstraint releaseOnblock1 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(10,10));
		releaseOnblock1.setFrom(block1Flunet.getActivity());
		releaseOnblock1.setTo(block1Flunet.getActivity());
		cons.add(releaseOnblock1);
		
		
		AllenIntervalConstraint onblock1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(1,APSPSolver.INF));
		onblock1Duration.setFrom(block1Flunet.getActivity());
		onblock1Duration.setTo(block1Flunet.getActivity());
		cons.add(onblock1Duration);

		//-------------------------------------------------------------------------------------------
		
		
		SpatialFluent2 block3Flunet = (SpatialFluent2)grounSpatialFluentSolver.createVariable("atLocation");
		block3Flunet.setName("block3");
		((RectangularCuboidRegion)block3Flunet.getInternalVariables()[0]).setName("block3");
		((Activity)block3Flunet.getInternalVariables()[1]).setSymbolicDomain("at_block3_table1()");
		((Activity)block3Flunet.getInternalVariables()[1]).setMarking(markings.UNJUSTIFIED);
		spatialFleunts.add(block3Flunet);
		
		//----------------------------------------------------------------------------------------
		
		SpatialFluent2 block2Flunet = (SpatialFluent2)grounSpatialFluentSolver.createVariable("atLocation");
		block2Flunet.setName("block2");
		((RectangularCuboidRegion)block2Flunet.getInternalVariables()[0]).setName("block2");
		((Activity)block2Flunet.getInternalVariables()[1]).setSymbolicDomain("at_block2_table1()");
		((Activity)block2Flunet.getInternalVariables()[1]).setMarking(markings.JUSTIFIED);
		spatialFleunts.add(block2Flunet);

		AllenIntervalConstraint releaseOnBlock2 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(10,10));
		releaseOnBlock2.setFrom(block2Flunet.getActivity());
		releaseOnBlock2.setTo(block2Flunet.getActivity());
		cons.add(releaseOnBlock2);
		
		AllenIntervalConstraint onblock2Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(1,APSPSolver.INF));
		onblock2Duration.setFrom(block2Flunet.getActivity());
		onblock2Duration.setTo(block2Flunet.getActivity());
		cons.add(onblock2Duration);
		
		
				
		//----------------------------------------------------------------------------------------		
		
		Activity two = (Activity)grounSpatialFluentSolver.getConstraintSolvers()[1].createVariable("robot1");
		two.setSymbolicDomain("holding_block3(arm)");
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


		SimpleOperator operator1 = new SimpleOperator("atLocation::at_block3_table1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_block3_table1(arm)"},
				new int[] {0});
		operator1.addConstraint(atCup1Duration, 0, 0);
		operators.add(operator1);

		SimpleOperator operator10 = new SimpleOperator("atLocation::at_block3_tray1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_block3_tray1(arm)"},
				new int[] {0});
		operator10.addConstraint(atCup1Duration, 0, 0);
		operators.add(operator10);

		SimpleOperator operator2 = new SimpleOperator("robot1::place_block3_table1(arm)",
				new AllenIntervalConstraint[] {placeCupAfterholding},
				new String[] {"robot1::holding_block3(arm)"},
				new int[] {1});
		operator2.addConstraint(placeCup1Duration, 0, 0);
		operators.add(operator2);

		SimpleOperator operator11 = new SimpleOperator("robot1::place_block3_tray1(arm)",
				new AllenIntervalConstraint[] {placeCupAfterholding},
				new String[] {"robot1::holding_block3(arm)"},
				new int[] {1});
		operator11.addConstraint(placeCup1Duration, 0, 0);
		operators.add(operator11);

		SimpleOperator operator3a = new SimpleOperator("robot1::holding_block3(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_block3_table2(arm)"},
				new int[] {1});
		operator3a.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator3a);

		SimpleOperator operator3b = new SimpleOperator("robot1::holding_block3(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_block3_table1(arm)"},
				new int[] {1});
		operator3b.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator3b);

		SimpleOperator operator3c = new SimpleOperator("robot1::holding_block3(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_block3_tray1(arm)"},
				new int[] {1});
		operator3c.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator3c);

		SimpleOperator operator42 = new SimpleOperator("robot1::pick_block3_table1(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt},
				new String[] {"atLocation::at_block3_table1()"},
				new int[] {100});
		operator42.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator42);

		SimpleOperator operator41 = new SimpleOperator("robot1::pick_block3_table2(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt},
				new String[] {"atLocation::at_block3_table2()"},
				new int[] {100});
		operator41.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator41);

		SimpleOperator operator411 = new SimpleOperator("robot1::pick_block3_tray1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"atLocation::at_block3_tray1()"},
				new int[] {1});
		operator411.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator411);

		//.....................................................................
		SimpleOperator operator4 = new SimpleOperator("atLocation::at_block1_table1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_block1_table1(arm)"},
				new int[] {0});
		operator4.addConstraint(atKnife1Duration, 0, 0);
		operators.add(operator4);

		SimpleOperator operator5 = new SimpleOperator("robot1::place_block1_table1(arm)",
				new AllenIntervalConstraint[] {placeKnifeAfterholding},
				new String[] {"robot1::holding_block1(arm)"},
				new int[] {1});
		operator5.addConstraint(placeKnife1Duration, 0, 0);
		operators.add(operator5);
		
		SimpleOperator operator6 = new SimpleOperator("robot1::holding_block1(arm)",
				new AllenIntervalConstraint[] {holdingKnifeAfterPick},
				new String[] {"robot1::pick_block1_table1(arm)"},
				new int[] {1});
		operator6.addConstraint(holdingKnife1Duration, 0, 0);
		operators.add(operator6);

		/*---*/SimpleOperator operator100 = new SimpleOperator("atLocation::at_block1_tray1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_block1_tray1(arm)"},
				new int[] {0});
		operator100.addConstraint(atCup1Duration, 0, 0);
		operators.add(operator100);		

		/*---*/SimpleOperator operator111 = new SimpleOperator("robot1::place_block1_tray1(arm)",
				new AllenIntervalConstraint[] {placeCupAfterholding},
				new String[] {"robot1::holding_block1(arm)"},
				new int[] {1});
		operator111.addConstraint(placeCup1Duration, 0, 0);
		operators.add(operator111);

		/*---*/SimpleOperator operator3cc = new SimpleOperator("robot1::holding_block1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_block1_tray1(arm)"},
				new int[] {1});
		operator3cc.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator3cc);

		
		SimpleOperator operator2res = new SimpleOperator("robot1::pick_block1_table1(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt},
				new String[] {"atLocation::at_block1_table1()"},
				new int[] {1});
		operator2res.addConstraint(pickKnife1Duration, 0, 0);
		operators.add(operator2res);

		/*---*/SimpleOperator operator411a = new SimpleOperator("robot1::pick_block1_tray1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"atLocation::at_block1_tray1()"},
				new int[] {1});
		operator411a.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator411a);

		
		//........................

		SimpleOperator operator7 = new SimpleOperator("atLocation::at_block2_table1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_block2_table1(arm)"},
				new int[] {0});
		operator7.addConstraint(atFork1Duration, 0, 0);
		operators.add(operator7);


		SimpleOperator operator8 = new SimpleOperator("robot1::place_block2_table1(arm)",
				new AllenIntervalConstraint[] {placeForkAfterholding},
				new String[] {"robot1::holding_block2(arm)"},
				new int[] {1});
		operator8.addConstraint(placeFork1Duration, 0, 0);
		operators.add(operator8);
		
		SimpleOperator operator9 = new SimpleOperator("robot1::holding_block2(arm)",
				new AllenIntervalConstraint[] {holdingForkAfterPick},
				new String[] {"robot1::pick_block2_table1(arm)"},
				new int[] {1});
		operator9.addConstraint(holdingFork1Duration, 0, 0);
		operators.add(operator9);

		
		/*---*/SimpleOperator operator101 = new SimpleOperator("atLocation::at_block2_tray1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_block2_tray1(arm)"},
				new int[] {0});
		operator101.addConstraint(atCup1Duration, 0, 0);
		operators.add(operator101);		

		
		/*---*/SimpleOperator operator11a = new SimpleOperator("robot1::place_block2_tray1(arm)",
				new AllenIntervalConstraint[] {placeCupAfterholding},
				new String[] {"robot1::holding_block2(arm)"},
				new int[] {1});
		operator11a.addConstraint(placeCup1Duration, 0, 0);
		operators.add(operator11a);


		/*---*/SimpleOperator operator3cb = new SimpleOperator("robot1::holding_block2(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_block2_tray1(arm)"},
				new int[] {1});
		operator3cb.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator3cb);

		
		SimpleOperator operator4res = new SimpleOperator("robot1::pick_block2_table1(arm)",
				new AllenIntervalConstraint[] {holdingKnifeAfterPick},
				new String[] {"atLocation::at_block2_table1()"},
				new int[] {1});
		operator4res.addConstraint(pickFork1Duration, 0, 0);
		operators.add(operator4res);

		/*---*/SimpleOperator operator411b = new SimpleOperator("robot1::pick_block2_tray1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"atLocation::at_block2_tray1()"},
				new int[] {1});
		operator411b.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator411b);
		
	}

	private static void getSpatialKnowledge(Vector<SpatialRule2> srules){
		
		Bounds block1_size_x = new Bounds(10,10);
		Bounds block1_size_y = new Bounds(6,6);
		Bounds block1_size_z = new Bounds(5,5);
		Bounds block3_size_x = new Bounds(10, 10);
		Bounds block3_size_y = new Bounds(6, 6);
		Bounds block3_size_z = new Bounds(5, 5);
		Bounds block2_size_x = new Bounds(10,10);
		Bounds block2_size_y = new Bounds(6, 6);
		Bounds block2_size_z = new Bounds(5, 5);
		Bounds withinReach_y_lower = new Bounds(5, 20);
		Bounds withinReach_y_upper = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_x_lower = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_x_upper = new Bounds(5, APSPSolver.INF);


		SpatialRule2 r2 = new SpatialRule2("blockC", "blockB", 
				new BlockAlgebraConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Overlaps, AllenIntervalConstraint.Type.Overlaps.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals, AllenIntervalConstraint.Type.Equals.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds()))
				);
		srules.add(r2);

		SpatialRule2 r3 = new SpatialRule2("blockC", "blockA", 
				new BlockAlgebraConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy, new Bounds(3,3)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals , AllenIntervalConstraint.Type.Equals.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds()))

				);
		srules.add(r3);

		SpatialRule2 r10 = new SpatialRule2("blockA", "blockB", 
				new BlockAlgebraConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(2,5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals , AllenIntervalConstraint.Type.Equals.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals, AllenIntervalConstraint.Type.Equals.getDefaultBounds()))

				);
		srules.add(r10);

		
		SpatialRule2 r4 = new SpatialRule2("blockB", "table", 
				new BlockAlgebraConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds()))
				);
		srules.add(r4);

		SpatialRule2 r5 = new SpatialRule2("blockA", "table", 
				new BlockAlgebraConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds()))
				);
		srules.add(r5);

		SpatialRule2 r7 = new SpatialRule2("blockA", "blockA", 
				new UnaryBlockConstraint(UnaryBlockConstraint.Type.Size, block1_size_x, block1_size_y, block1_size_z));
		srules.add(r7);

		SpatialRule2 r8 = new SpatialRule2("blockC", "blockC", 
				new UnaryBlockConstraint(UnaryBlockConstraint.Type.Size, block3_size_x, block3_size_y, block3_size_z));
		srules.add(r8);

		SpatialRule2 r9 = new SpatialRule2("blockB", "blockB", 
				new UnaryBlockConstraint(UnaryBlockConstraint.Type.Size, block2_size_x, block2_size_y, block2_size_z));
		srules.add(r9);
	}
	
	private static void getAssertionalRule(Vector<SpatialAssertionalRelation2> saRelations){
		
	
		SpatialAssertionalRelation2 sa1 = new SpatialAssertionalRelation2("table1", "table");
		sa1.setUnaryAtBlockConstraint(new UnaryBlockConstraint(UnaryBlockConstraint.Type.At, 
				new Bounds(0, 0), new Bounds(60, 60), new Bounds(0, 0), new Bounds(60, 60), new Bounds(0, 0), new Bounds(1, 1)));
		OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
		tableOnto.setMovable(false);
		sa1.setOntologicalProp(tableOnto);
		saRelations.add(sa1);
		//............................................................................................

		SpatialAssertionalRelation2 sa3 = new SpatialAssertionalRelation2("block1", "blockA");		
		sa3.setUnaryAtBlockConstraint(new UnaryBlockConstraint(UnaryBlockConstraint.Type.At, 
				new Bounds(18,18), new Bounds(28,28), new Bounds(10, 10), new Bounds(16, 16), new Bounds(1, 1), new Bounds(6, 6)));
		OntologicalSpatialProperty forkOnto = new OntologicalSpatialProperty();
		forkOnto.setMovable(true);
		sa3.setOntologicalProp(forkOnto);
		saRelations.add(sa3);

		
		//.........................................................................................
		SpatialAssertionalRelation2 sa2 = new SpatialAssertionalRelation2("block2", "blockB");
		sa2.setUnaryAtBlockConstraint(new UnaryBlockConstraint(UnaryBlockConstraint.Type.At, 
//				new Bounds(45,45), new Bounds(51,51), new Bounds(10, 10), new Bounds(33, 33)));
				new Bounds(40,40), new Bounds(46,46), new Bounds(15, 15), new Bounds(25, 25), new Bounds(1, 1), new Bounds(6, 6)));
		OntologicalSpatialProperty knifeOnto = new OntologicalSpatialProperty();
		knifeOnto.setMovable(true);
		sa2.setOntologicalProp(knifeOnto);
		saRelations.add(sa2);
		
		//....................................................................................
		SpatialAssertionalRelation2 sa4 = new SpatialAssertionalRelation2("block3", "blockC");
		sa4.setUnaryAtBlockConstraint(new UnaryBlockConstraint(UnaryBlockConstraint.Type.At, 
				new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		OntologicalSpatialProperty cupOnto = new OntologicalSpatialProperty();
		cupOnto.setMovable(true);
		sa4.setOntologicalProp(cupOnto);

//		sa4.associateSpatialFlunt(cupFlunet);
		saRelations.add(sa4);

	}

	
	
}
