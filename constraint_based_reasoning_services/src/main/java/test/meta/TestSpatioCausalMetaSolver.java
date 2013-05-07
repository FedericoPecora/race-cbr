package test.meta;


import java.util.Vector;
import java.util.logging.Level;

import meta.MetaSpatialConstraint2;
import meta.MetaSpatialConstraintSolver2;
import meta.MetaSpatioCausalConstraint;
import meta.MetaSpatioCausalConstraintSolver;
import meta.simplePlanner.SimpleDomain;
import meta.simplePlanner.SimpleOperator;
import meta.simplePlanner.SimplePlanner;
import meta.simplePlanner.SimpleDomain.markings;
import meta.symbolsAndTime.Schedulable;
import multi.activity.Activity;
import multi.activity.ActivityNetworkSolver;
import multi.allenInterval.AllenIntervalConstraint;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraint2;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraintSolver2;
import sandbox.spatial.rectangleAlgebra2.RectangularRegion2;
import sandbox.spatial.rectangleAlgebra2.SpatialAssertionalRelation2;
import sandbox.spatial.rectangleAlgebra2.SpatialFuent;
import sandbox.spatial.rectangleAlgebra2.SpatialFluentSolver;
import sandbox.spatial.rectangleAlgebra2.SpatialRule2;
import sandbox.spatial.rectangleAlgebra2.UnaryRectangleConstraint2;
import spatial.rectangleAlgebra.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra.BoundingBox;
import spatial.rectangleAlgebra.OntologicalSpatialProperty;
import spatial.rectangleAlgebra.SpatialAssertionalRelation;
import spatial.rectangleAlgebra.SpatialRule;
import time.APSPSolver;
import time.Bounds;
import utility.logging.MetaCSPLogging;
import utility.timelinePlotting.TimelinePublisher;
import utility.timelinePlotting.TimelineVisualizer;
import framework.ConstraintNetwork;

public class TestSpatioCausalMetaSolver {
	
	
	public static void main(String[] args) {


		MetaCSPLogging.setLevel(TimelinePublisher.class, Level.FINEST);
		
		//#################################################################################################################
		SimplePlanner planner = new SimplePlanner(0,600,0);		
		// This is a pointer toward the ActivityNetwork solver of the Scheduler
		ActivityNetworkSolver groundSolver = (ActivityNetworkSolver)planner.getConstraintSolvers()[0];
		MetaCSPLogging.setLevel(planner.getClass(), Level.FINEST);
		SimpleDomain rd = new SimpleDomain(new int[] {1}, new String[] {"arm"}, "TestDomain");		
		addOperator(rd);		
		//This adds the domain as a meta-constraint of the SimplePlanner
		planner.addMetaConstraint(rd);
		//... and we also add all its resources as separate meta-constraints
		for (Schedulable sch : rd.getSchedulingMetaConstraints()) planner.addMetaConstraint(sch);
		
		//#################################################################################################################
		
		//this is spatial general and assetional rule
		Vector<SpatialRule2> srules = new Vector<SpatialRule2>();
		Vector<SpatialAssertionalRelation2> saRelations = new Vector<SpatialAssertionalRelation2>();
		getSpatialKnowledge(srules);
		getAssertionalRule(saRelations);

		//#################################################################################################################
		//current situation + goal
//		SpatialFluentSolver spSolver = new SpatialFluentSolver(0, 1000);
//		
//		SpatialFeunt forkAtTable = (SpatialFeunt)spSolver.createVariable();
//		//set symbolic domain for planning
//		//set name + at constraint 
//		
//		SpatialFeunt knifeAtTable = (SpatialFeunt)spSolver.createVariable();
//		SpatialFeunt cupAtTable = (SpatialFeunt)spSolver.createVariable();
		
		
		
		//#################################################################################################################
		
		MetaSpatioCausalConstraintSolver metaSpatioCasualSolver = new MetaSpatioCausalConstraintSolver(0, 1000, 0);
		MetaSpatialConstraint2 objectsPosition = new MetaSpatialConstraint2();
		objectsPosition.setSpatialRules(srules.toArray(new SpatialRule2[srules.size()]));
		objectsPosition.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation2[saRelations.size()]));




		metaSpatioCasualSolver.addMetaConstraint(objectsPosition);
		metaSpatioCasualSolver.addMetaConstraint(rd);
		metaSpatioCasualSolver.backtrack();

		ConstraintNetwork.draw(metaSpatioCasualSolver.getConstraintSolvers()[0].getConstraintNetwork(), "Constraint Network");
		((RectangleConstraintSolver2)metaSpatioCasualSolver.getConstraintSolvers()[0]).extractBoundingBoxesFromSTPs("cup1").getAlmostCentreRectangle();

		
		

		TimelinePublisher tp = new TimelinePublisher(groundSolver, new Bounds(0,120), "Robot1", "cup1", "knife1");
		TimelineVisualizer viz = new TimelineVisualizer(tp);
		tp.publish(false, false);
		tp.publish(false, true);
		

		
		planner.backtrack();
		
		ConstraintNetwork.draw(groundSolver.getConstraintNetwork(), "Constraint Network");
		
		planner.draw();
		tp.publish(true, false);
	}
	
	private static void addOperator(SimpleDomain rd) {
		
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

		
		SimpleOperator operator1 = new SimpleOperator("robot::at_cup1_table1()",
				new AllenIntervalConstraint[] {atCupAfterPlace},
				new String[] {"Robot1::place_cup1_table1(arm)"},
				new int[] {1});
		operator1.addConstraint(atCup1Duration, 0, 0);
		rd.addOperator(operator1);
		
		SimpleOperator operator2 = new SimpleOperator("Robot1::place_cup1_table1(arm)",
				new AllenIntervalConstraint[] {placeCupAfterholding},
				new String[] {"Robot1::holding_cup1(arm)"},
				new int[] {1});
		operator2.addConstraint(placeCup1Duration, 0, 0);
		rd.addOperator(operator2);

		SimpleOperator operator3 = new SimpleOperator("Robot1::holding_cup1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"Robot1::pick_cup1(arm)"},
				new int[] {1});
		operator3.addConstraint(holdingCup1Duration, 0, 0);
		rd.addOperator(operator3);
		
		SimpleOperator operator1res = new SimpleOperator("Robot1::pick_cup1(arm)",
				null,
				null,
				new int[] {1});
		operator1res.addConstraint(pickCup1Duration, 0, 0);
		rd.addOperator(operator1res);
		
		//........................

		SimpleOperator operator4 = new SimpleOperator("robot::at_knife1_table1()",
				new AllenIntervalConstraint[] {atKnifeAfterPlace},
				new String[] {"Robot1::place_knife1_table1(arm)"},
				new int[] {1});
		operator4.addConstraint(atKnife1Duration, 0, 0);
		rd.addOperator(operator4);
		
		SimpleOperator operator5 = new SimpleOperator("Robot1::place_knife1_table1(arm)",
				new AllenIntervalConstraint[] {placeKnifeAfterholding},
				new String[] {"Robot1::holding_knife1(arm)"},
				new int[] {1});
		operator5.addConstraint(placeKnife1Duration, 0, 0);
		rd.addOperator(operator5);

		SimpleOperator operator6 = new SimpleOperator("Robot1::holding_knife1(arm)",
				new AllenIntervalConstraint[] {holdingKnifeAfterPick},
				new String[] {"Robot1::pick_knife1(arm)"},
				new int[] {1});
		operator6.addConstraint(holdingKnife1Duration, 0, 0);
		rd.addOperator(operator6);
		
		SimpleOperator operator2res = new SimpleOperator("Robot1::pick_knife1(arm)",
				null,
				null,
				new int[] {1});
		operator2res.addConstraint(pickKnife1Duration, 0, 0);
		rd.addOperator(operator2res);

		//........................
		
		SimpleOperator operator7 = new SimpleOperator("robot::at_fork_table1()",
				new AllenIntervalConstraint[] {atForkAfterPlace},
				new String[] {"Robot1::place_fork1_table1(arm)"},
				new int[] {1});
		operator7.addConstraint(atFork1Duration, 0, 0);
		rd.addOperator(operator7);
		
		SimpleOperator operator8 = new SimpleOperator("Robot1::place_fork1_table1(arm)",
				new AllenIntervalConstraint[] {placeForkAfterholding},
				new String[] {"Robot1::holding_fork1(arm)"},
				new int[] {1});
		operator8.addConstraint(placeFork1Duration, 0, 0);
		rd.addOperator(operator8);

		SimpleOperator operator9 = new SimpleOperator("Robot1::holding_fork1(arm)",
				new AllenIntervalConstraint[] {holdingForkAfterPick},
				new String[] {"Robot1::pick_fork1(arm)"},
				new int[] {1});
		operator9.addConstraint(holdingFork1Duration, 0, 0);
		rd.addOperator(operator9);
		
		SimpleOperator operator3res = new SimpleOperator("Robot1::pick_fork1(arm)",
				null,
				null,
				new int[] {1});
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
		
		SpatialFluentSolver spatialFluentSolver = new SpatialFluentSolver(0, 1000);

		

		SpatialAssertionalRelation2 sa1 = new SpatialAssertionalRelation2("table1", "table");
		sa1.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, 
				new Bounds(0, 0), new Bounds(100, 100), new Bounds(0, 0), new Bounds(99, 99)));
		OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
		tableOnto.setMovable(false);
		sa1.setOntologicalProp(tableOnto);
		
		SpatialFuent tableFlunet = (SpatialFuent)spatialFluentSolver.createVariable();
		((RectangularRegion2)tableFlunet.getInternalVariables()[0]).setName("table1");
		((Activity)tableFlunet.getInternalVariables()[1]).setSymbolicDomain("at_table1()");
		sa1.associateSpatialFlunt(tableFlunet);
		
		saRelations.add(sa1);
		//............................................................................................

		SpatialAssertionalRelation2 sa3 = new SpatialAssertionalRelation2("fork1", "fork");		
		sa3.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, 
				new Bounds(31, 31), new Bounds(37, 37), new Bounds(13, 13), new Bounds(32, 32)));
		
		SpatialFuent forkFlunet = (SpatialFuent)spatialFluentSolver.createVariable();
		((RectangularRegion2)forkFlunet.getInternalVariables()[0]).setName("fork1");
		((Activity)forkFlunet.getInternalVariables()[1]).setSymbolicDomain("at_fork1_table1()");
		sa3.associateSpatialFlunt(forkFlunet);

		saRelations.add(sa3);

		//		SpatialAssertionalRelation2 sa2 = new SpatialAssertionalRelation2("knife1", "knife");
		//		sa2.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, 
		//				new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		//		saRelations.add(sa2);
		
		//.........................................................................................
		SpatialAssertionalRelation2 sa2 = new SpatialAssertionalRelation2("knife1", "knife");
		sa2.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, 
				new Bounds(45,45), new Bounds(51,51), new Bounds(10, 10), new Bounds(33, 33)));
		
		SpatialFuent knifeFlunet = (SpatialFuent)spatialFluentSolver.createVariable();
		((RectangularRegion2)knifeFlunet.getInternalVariables()[0]).setName("knife1");
		((Activity)knifeFlunet.getInternalVariables()[1]).setSymbolicDomain("at_knife1_table1()");
		sa2.associateSpatialFlunt(knifeFlunet);
		
		saRelations.add(sa2);
		
		//....................................................................................
		SpatialAssertionalRelation2 sa4 = new SpatialAssertionalRelation2("cup1", "cup");
		sa4.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, 
				new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		
		SpatialFuent cupFlunet = (SpatialFuent)spatialFluentSolver.createVariable();
		((RectangularRegion2)cupFlunet.getInternalVariables()[0]).setName("cup1");
		((Activity)cupFlunet.getInternalVariables()[1]).setSymbolicDomain("at_cup1_table1()");
		((Activity)cupFlunet.getInternalVariables()[1]).setMarking(markings.UNJUSTIFIED);
		sa4.associateSpatialFlunt(cupFlunet);
		
		saRelations.add(sa4);


	}

}
