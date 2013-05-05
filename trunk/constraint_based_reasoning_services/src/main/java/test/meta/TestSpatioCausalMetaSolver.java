package test.meta;


import java.util.Vector;
import java.util.logging.Level;

import meta.MetaSpatioCausalConstraint;
import meta.simplePlanner.SimpleDomain;
import meta.simplePlanner.SimpleOperator;
import meta.simplePlanner.SimplePlanner;
import meta.simplePlanner.SimpleDomain.markings;
import meta.symbolsAndTime.Schedulable;
import multi.activity.Activity;
import multi.activity.ActivityNetworkSolver;
import multi.allenInterval.AllenIntervalConstraint;
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

		SimplePlanner planner = new SimplePlanner(0,600,0);		
		// This is a pointer toward the ActivityNetwork solver of the Scheduler
		ActivityNetworkSolver groundSolver = (ActivityNetworkSolver)planner.getConstraintSolvers()[0];

		MetaCSPLogging.setLevel(planner.getClass(), Level.FINEST);
//		MetaCSPLogging.setLevel(Level.FINEST);
//		MetaCSPLogging.setLevel(planner.getClass(), Level.FINE);
				
		Vector<SpatialRule> srules = new Vector<SpatialRule>();
		getSpatialKnowledge(srules);
		
		MetaSpatioCausalConstraint rd = new MetaSpatioCausalConstraint(new int[] {1}, new String[] {"arm"}, "TestDomain", srules.toArray(new SpatialRule[srules.size()]));

		
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

		
		SimpleOperator operator1 = new SimpleOperator("cup1::at()",
				new AllenIntervalConstraint[] {atCupAfterPlace},
				new String[] {"Robot1::place_cup1(arm)"},
				new int[] {1});
		operator1.addConstraint(atCup1Duration, 0, 0);
		rd.addOperator(operator1);
		
		SimpleOperator operator2 = new SimpleOperator("Robot1::place_cup1(arm)",
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

		SimpleOperator operator4 = new SimpleOperator("knife1::at()",
				new AllenIntervalConstraint[] {atKnifeAfterPlace},
				new String[] {"Robot1::place_knife1(arm)"},
				new int[] {1});
		operator4.addConstraint(atKnife1Duration, 0, 0);
		rd.addOperator(operator4);
		
		SimpleOperator operator5 = new SimpleOperator("Robot1::place_knife1(arm)",
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
		
		SimpleOperator operator7 = new SimpleOperator("fork1::at()",
				new AllenIntervalConstraint[] {atForkAfterPlace},
				new String[] {"Robot1::place_fork1(arm)"},
				new int[] {1});
		operator7.addConstraint(atFork1Duration, 0, 0);
		rd.addOperator(operator7);
		
		SimpleOperator operator8 = new SimpleOperator("Robot1::place_fork1(arm)",
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

		
		//This adds the domain as a meta-constraint of the SimplePlanner
		planner.addMetaConstraint(rd);
		//... and we also add all its resources as separate meta-constraints
		for (Schedulable sch : rd.getSchedulingMetaConstraints()) planner.addMetaConstraint(sch);
		
		
		
		
		
		Vector<SpatialAssertionalRelation> saRelations = new Vector<SpatialAssertionalRelation>();
		getAssertionalRule(saRelations);
		rd.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation[saRelations.size()]));

		
		

		TimelinePublisher tp = new TimelinePublisher(groundSolver, new Bounds(0,120), "Robot1", "cup1", "knife1");
		TimelineVisualizer viz = new TimelineVisualizer(tp);
		tp.publish(false, false);
		tp.publish(false, true);
		

		
		planner.backtrack();
		
		ConstraintNetwork.draw(groundSolver.getConstraintNetwork(), "Constraint Network");
		
		planner.draw();
		tp.publish(true, false);
	}
	
	private static void getSpatialKnowledge(Vector<SpatialRule> srules){
		
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


		SpatialRule r2 = new SpatialRule("cup", "knife", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(15,20)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds() ))
				);
		srules.add(r2);

		SpatialRule r3 = new SpatialRule("cup", "fork", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(15,20)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During , AllenIntervalConstraint.Type.During.getDefaultBounds()))

				);
		srules.add(r3);

		SpatialRule r4 = new SpatialRule("fork", "table", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r4);

		SpatialRule r5 = new SpatialRule("knife", "table", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r5);

		//		SpatialRule r6 = new SpatialRule("cup", "table", 
		//				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
		//						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
		//				);
		//		srules.add(r6);

		SpatialRule r7 = new SpatialRule("knife", "knife", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						knife_size_x), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, knife_size_y)));
		srules.add(r7);

		SpatialRule r8 = new SpatialRule("cup", "cup", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						cup_size_x), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, cup_size_y)));
		srules.add(r8);

		SpatialRule r9 = new SpatialRule("fork", "fork", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						fork_size_x), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, fork_size_y)));
		srules.add(r9);
	}
	
	private static void getAssertionalRule(Vector<SpatialAssertionalRelation> saRelations){
		
		SpatialAssertionalRelation sa1 = new SpatialAssertionalRelation("table1", "table");
		sa1.setCoordinate(new BoundingBox(new Bounds(0, 0), new Bounds(100, 100), new Bounds(0, 0), new Bounds(99, 99)));
		OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
		tableOnto.setMovable(false);
		sa1.setOntologicalProp(tableOnto);
		saRelations.add(sa1);

		SpatialAssertionalRelation sa3 = new SpatialAssertionalRelation("fork1", "fork");
		sa3.setCoordinate(new BoundingBox(new Bounds(31, 31), new Bounds(37, 37), new Bounds(13, 13), new Bounds(32, 32)));
		saRelations.add(sa3);

		
		SpatialAssertionalRelation sa2 = new SpatialAssertionalRelation("knife1", "knife");
		sa2.setCoordinate(new BoundingBox(new Bounds(45,45), new Bounds(51,51), new Bounds(10, 10), new Bounds(33, 33)));
//		sa2.setCoordinate(new BoundingBox(new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		saRelations.add(sa2);


		SpatialAssertionalRelation sa4 = new SpatialAssertionalRelation("cup1", "cup");
		sa4.setCoordinate(new BoundingBox(new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		saRelations.add(sa4);

	}

}
