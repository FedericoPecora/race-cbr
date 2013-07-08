package test.meta;

import java.util.Vector;
import java.util.logging.Level;

import meta.MetaCausalConstraint;
import meta.MetaSpatialFluentConstraint;
import meta.MetaSpatioCausalConstraintSolver;
import meta.MetaCausalConstraint.markings;
import meta.simplePlanner.SimpleOperator;
import meta.symbolsAndTime.Schedulable;
import multi.activity.Activity;
import multi.activity.ActivityNetworkSolver;
import multi.allenInterval.AllenIntervalConstraint;
import multi.spatial.rectangleAlgebra.RectangleConstraint;
import multi.spatial.rectangleAlgebra.RectangleConstraintSolver;
import multi.spatial.rectangleAlgebra.RectangularRegion;
import multi.spatial.rectangleAlgebra.UnaryRectangleConstraint;
import multi.spatial.rectangleAlgebraNew.toRemove.OntologicalSpatialProperty;
import multi.spatioTemporal.SpatialFluent;
import multi.spatioTemporal.SpatialFluentSolver;

import spatial.utility.SpatialAssertionalRelation2;
import spatial.utility.SpatialRule2;
import time.APSPSolver;
import time.Bounds;
import utility.logging.MetaCSPLogging;
import utility.timelinePlotting.TimelinePublisher;
import utility.timelinePlotting.TimelineVisualizer;
import framework.ConstraintNetwork;

public class TestSpatioCausalMetaSolver2 {

	public static void main(String[] args) {


		MetaSpatioCausalConstraintSolver metaSpatioCasualSolver = new MetaSpatioCausalConstraintSolver(0, 1000, 0);
		MetaSpatialFluentConstraint metaSpatialFluentConstraint = new MetaSpatialFluentConstraint();
		SpatialFluentSolver grounSpatialFluentSolver = (SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0];
		
		//#################################################################################################################
		MetaCausalConstraint metaCausalConstraint = new MetaCausalConstraint(new int[] {2}, new String[] {"arm"}, "WellSetTableDomain");		
		addOperator(metaCausalConstraint);		
		//#################################################################################################################
		//this is spatial general and assetional rule
		Vector<SpatialRule2> srules = new Vector<SpatialRule2>();
		Vector<SpatialAssertionalRelation2> saRelations = new Vector<SpatialAssertionalRelation2>();
		getSpatialKnowledge(srules);
		getAssertionalRule(saRelations);
		
		//#################################################################################################################
		//add spatial general and assertional rule to MetaSpatialFluentConstraint
		metaSpatialFluentConstraint.setSpatialRules(srules.toArray(new SpatialRule2[srules.size()]));
		metaSpatialFluentConstraint.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation2[saRelations.size()]));
		insertCurrentStateCurrentGoal(grounSpatialFluentSolver);
		//add meta constraint
		metaSpatioCasualSolver.addMetaConstraint(metaSpatialFluentConstraint);		
		metaSpatioCasualSolver.addMetaConstraint(metaCausalConstraint);
		for (Schedulable sch : metaCausalConstraint.getSchedulingMetaConstraints()) {
			metaSpatioCasualSolver.addMetaConstraint(sch);
		}
		
		MetaCSPLogging.setLevel(MetaSpatioCausalConstraintSolver.class, Level.FINEST);
		MetaCSPLogging.setLevel(MetaSpatialFluentConstraint.class, Level.FINE);
		metaSpatioCasualSolver.backtrack();

		//#####################################################################################################################
		//visualization
		ConstraintNetwork.draw(((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0]).getConstraintSolvers()[0].getConstraintNetwork(), "RA Constraint Network");
		ConstraintNetwork.draw(((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0]).getConstraintSolvers()[1].getConstraintNetwork(), "Activity Constraint Network");
		
		System.out.println(((RectangleConstraintSolver)((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0])
				.getConstraintSolvers()[0]).extractBoundingBoxesFromSTPs("cup1").getAlmostCentreRectangle());


		ActivityNetworkSolver acSolver = ((ActivityNetworkSolver)((SpatialFluentSolver)metaSpatioCasualSolver.getConstraintSolvers()[0]).getConstraintSolvers()[1]);
		TimelinePublisher tp = new TimelinePublisher(acSolver, new Bounds(0,200), "robot1");
		TimelineVisualizer viz = new TimelineVisualizer(tp);
		tp.publish(false, false);
		tp.publish(false, true);
		tp.publish(true, false);
		//#####################################################################################################################
	}
	
	private static void insertCurrentStateCurrentGoal(SpatialFluentSolver grounSpatialFluentSolver) {
		
		SpatialFluent tableFlunet = (SpatialFluent)grounSpatialFluentSolver.createVariable();
		tableFlunet.setName("table1");
		((RectangularRegion)tableFlunet.getInternalVariables()[0]).setName("table1");
		((Activity)tableFlunet.getInternalVariables()[1]).setSymbolicDomain("at_table1()");
		((Activity)tableFlunet.getInternalVariables()[1]).setMarking(markings.JUSTIFIED);
		

		
		SpatialFluent cupFlunet = (SpatialFluent)grounSpatialFluentSolver.createVariable();
		cupFlunet.setName("cup1");
		((RectangularRegion)cupFlunet.getInternalVariables()[0]).setName("cup1");
		((Activity)cupFlunet.getInternalVariables()[1]).setSymbolicDomain("place_cup1_table1(arm)");
		((Activity)cupFlunet.getInternalVariables()[1]).setMarking(markings.UNJUSTIFIED);
		
		SpatialFluent knifeFlunet = (SpatialFluent)grounSpatialFluentSolver.createVariable();
		knifeFlunet.setName("knife1");
		((RectangularRegion)knifeFlunet.getInternalVariables()[0]).setName("knife1");
		((Activity)knifeFlunet.getInternalVariables()[1]).setSymbolicDomain("place_knife1_table1(arm)");
		((Activity)knifeFlunet.getInternalVariables()[1]).setMarking(markings.JUSTIFIED);
		
		
		SpatialFluent forkFlunet = (SpatialFluent)grounSpatialFluentSolver.createVariable();
		forkFlunet.setName("fork1");
		((RectangularRegion)forkFlunet.getInternalVariables()[0]).setName("fork1");
		((Activity)forkFlunet.getInternalVariables()[1]).setSymbolicDomain("place_fork1_table1(arm)");
		((Activity)forkFlunet.getInternalVariables()[1]).setMarking(markings.JUSTIFIED);
		
		
		SpatialFluent irrelevance = (SpatialFluent)grounSpatialFluentSolver.createVariable();
		irrelevance.setName("");
		((RectangularRegion)irrelevance.getInternalVariables()[0]).setName("fork1");
		((Activity)irrelevance.getInternalVariables()[1]).setSymbolicDomain("place_fork1_table1(arm)");
		((Activity)irrelevance.getInternalVariables()[1]).setMarking(markings.JUSTIFIED);
		
	}

	private static void addOperator(MetaCausalConstraint rd) {
		
		AllenIntervalConstraint atCupAfterPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds());
		AllenIntervalConstraint atCup1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint placeCupAfterholding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds());
		AllenIntervalConstraint placeCup1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint holdingCupAfterPick = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds());
		AllenIntervalConstraint holdingCup1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint pickCup1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));

		
		AllenIntervalConstraint atKnifeAfterPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds());
		AllenIntervalConstraint atKnife1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint placeKnifeAfterholding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds());
		AllenIntervalConstraint placeKnife1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint holdingKnifeAfterPick = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds());
		AllenIntervalConstraint holdingKnife1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint pickKnife1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));

		
		AllenIntervalConstraint atForkAfterPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds());
		AllenIntervalConstraint atFork1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint placeForkAfterholding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds());
		AllenIntervalConstraint placeFork1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint holdingForkAfterPick = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds());
		AllenIntervalConstraint holdingFork1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint pickFork1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));

		
		SimpleOperator operator1 = new SimpleOperator("robot1::at_cup1_table1()",
				new AllenIntervalConstraint[] {atCupAfterPlace},
				new String[] {"robot1::place_cup1_table1(arm)"},
				new int[] {0});
		operator1.addConstraint(atCup1Duration, 0, 0);
		rd.addOperator(operator1);
		
		SimpleOperator operator2 = new SimpleOperator("robot1::place_cup1_table1(arm)",
				new AllenIntervalConstraint[] {placeCupAfterholding},
				new String[] {"robot1::holding_cup1(arm)"},
				new int[] {2});
		operator2.addConstraint(placeCup1Duration, 0, 0);
		rd.addOperator(operator2);

		SimpleOperator operator3 = new SimpleOperator("robot1::holding_cup1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_cup1(arm)"},
				new int[] {1});
		operator3.addConstraint(holdingCup1Duration, 0, 0);
		rd.addOperator(operator3);
		
		SimpleOperator operator1res = new SimpleOperator("robot1::pick_cup1(arm)",
				null,
				null,
				new int[] {2});
		operator1res.addConstraint(pickCup1Duration, 0, 0);
		rd.addOperator(operator1res);
		
		//........................

		SimpleOperator operator4 = new SimpleOperator("robot1::at_knife1_table1()",
				new AllenIntervalConstraint[] {atKnifeAfterPlace},
				new String[] {"robot1::place_knife1_table1(arm)"},
				new int[] {0});
		operator4.addConstraint(atKnife1Duration, 0, 0);
		rd.addOperator(operator4);
		
		SimpleOperator operator5 = new SimpleOperator("robot1::place_knife1_table1(arm)",
				new AllenIntervalConstraint[] {placeKnifeAfterholding},
				new String[] {"robot1::holding_knife1(arm)"},
				new int[] {2});
		operator5.addConstraint(placeKnife1Duration, 0, 0);
		rd.addOperator(operator5);

		SimpleOperator operator6 = new SimpleOperator("robot1::holding_knife1(arm)",
				new AllenIntervalConstraint[] {holdingKnifeAfterPick},
				new String[] {"robot1::pick_knife1(arm)"},
				new int[] {1});
		operator6.addConstraint(holdingKnife1Duration, 0, 0);
		rd.addOperator(operator6);
		
		SimpleOperator operator2res = new SimpleOperator("robot1::pick_knife1(arm)",
				null,
				null,
				new int[] {2});
		operator2res.addConstraint(pickKnife1Duration, 0, 0);
		rd.addOperator(operator2res);

		//........................
		
		SimpleOperator operator7 = new SimpleOperator("robot1::at_fork_table1()",
				new AllenIntervalConstraint[] {atForkAfterPlace},
				new String[] {"robot1::place_fork1_table1(arm)"},
				new int[] {0});
		operator7.addConstraint(atFork1Duration, 0, 0);
		rd.addOperator(operator7);
		
		SimpleOperator operator8 = new SimpleOperator("robot1::place_fork1_table1(arm)",
				new AllenIntervalConstraint[] {placeForkAfterholding},
				new String[] {"robot1::holding_fork1(arm)"},
				new int[] {2});
		operator8.addConstraint(placeFork1Duration, 0, 0);
		rd.addOperator(operator8);

		SimpleOperator operator9 = new SimpleOperator("robot1::holding_fork1(arm)",
				new AllenIntervalConstraint[] {holdingForkAfterPick},
				new String[] {"robot1::pick_fork1(arm)"},
				new int[] {1});
		operator9.addConstraint(holdingFork1Duration, 0, 0);
		rd.addOperator(operator9);
		
		SimpleOperator operator3res = new SimpleOperator("robot1::pick_fork1(arm)",
				null,
				null,
				new int[] {2});
		operator3res.addConstraint(pickFork1Duration, 0, 0);
		rd.addOperator(operator3res);

		
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
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(15,20)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds() ))
				);
		srules.add(r2);

		SpatialRule2 r3 = new SpatialRule2("cup", "fork", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(15,20)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During , AllenIntervalConstraint.Type.During.getDefaultBounds()))

				);
		srules.add(r3);

		SpatialRule2 r4 = new SpatialRule2("fork", "table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r4);

		SpatialRule2 r5 = new SpatialRule2("knife", "table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r5);


		SpatialRule2 r7 = new SpatialRule2("knife", "knife", 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, knife_size_x, knife_size_y));
		srules.add(r7);

		SpatialRule2 r8 = new SpatialRule2("cup", "cup", 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, cup_size_x, cup_size_y));
		srules.add(r8);

		SpatialRule2 r9 = new SpatialRule2("fork", "fork", 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, fork_size_x, fork_size_y));
		srules.add(r9);
	}
	
	private static void getAssertionalRule(Vector<SpatialAssertionalRelation2> saRelations){
		
	
		SpatialAssertionalRelation2 sa1 = new SpatialAssertionalRelation2("table1", "table");
		sa1.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
				new Bounds(0, 0), new Bounds(60, 60), new Bounds(0, 0), new Bounds(99, 99)));
		OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
		tableOnto.setMovable(false);
		sa1.setOntologicalProp(tableOnto);
//		sa1.associateSpatialFlunt(tableFlunet);
		
		saRelations.add(sa1);
		//............................................................................................

		SpatialAssertionalRelation2 sa3 = new SpatialAssertionalRelation2("fork1", "fork");		
		sa3.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
				new Bounds(20, 20), new Bounds(26, 26), new Bounds(13, 13), new Bounds(32, 32)));

//		sa3.associateSpatialFlunt(forkFlunet);
		saRelations.add(sa3);

		//		SpatialAssertionalRelation2 sa2 = new SpatialAssertionalRelation2("knife1", "knife");
		//		sa2.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
		//				new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		//		saRelations.add(sa2);
		
		//.........................................................................................
		SpatialAssertionalRelation2 sa2 = new SpatialAssertionalRelation2("knife1", "knife");
		sa2.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
				new Bounds(30,30), new Bounds(36,36), new Bounds(10, 10), new Bounds(33, 33)));
		
//		sa2.associateSpatialFlunt(knifeFlunet);		
		saRelations.add(sa2);
		
		//....................................................................................
		SpatialAssertionalRelation2 sa4 = new SpatialAssertionalRelation2("cup1", "cup");
		sa4.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
				new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		
//		sa4.associateSpatialFlunt(cupFlunet);
		saRelations.add(sa4);

	}
	
}
