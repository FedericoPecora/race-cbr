package test.simplePlanner;

import java.util.logging.Level;

import meta.simplePlanner.SimpleDomain;
import meta.simplePlanner.SimpleOperator;
import meta.simplePlanner.SimplePlanner;
import meta.simplePlanner.SimpleDomain.markings;
import meta.symbolsAndTime.Schedulable;
import multi.activity.Activity;
import multi.activity.ActivityNetworkSolver;
import multi.allenInterval.AllenIntervalConstraint;
import time.APSPSolver;
import time.Bounds;
import utility.logging.MetaCSPLogging;
import utility.timelinePlotting.TimelinePublisher;
import utility.timelinePlotting.TimelineVisualizer;
import framework.ConstraintNetwork;

public class TestSimplePlannerForTableScratch {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		MetaCSPLogging.setLevel(TimelinePublisher.class, Level.FINEST);

		SimplePlanner planner = new SimplePlanner(0,600,0);		
		// This is a pointer toward the ActivityNetwork solver of the Scheduler
		ActivityNetworkSolver groundSolver = (ActivityNetworkSolver)planner.getConstraintSolvers()[0];

		MetaCSPLogging.setLevel(planner.getClass(), Level.FINEST);
//		MetaCSPLogging.setLevel(Level.FINEST);
//		MetaCSPLogging.setLevel(planner.getClass(), Level.FINE);
				
		SimpleDomain rd = new SimpleDomain(new int[] {1}, new String[] {"arm"}, "TestDomain");

		AllenIntervalConstraint durationPickupCup = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint durationPickupFork = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint durationPlaceCup = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint durationPlaceFork = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint placeForkAfterPickup = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds());
		AllenIntervalConstraint placeCupAfterPickup = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds());


		SimpleOperator operator1 = new SimpleOperator("Robot1::place_fork(arm)",
				new AllenIntervalConstraint[] {placeForkAfterPickup},
				new String[] {"Robot1::pickup_fork(arm)"},
				new int[] {1});
		operator1.addConstraint(durationPlaceFork, 0, 0);
		rd.addOperator(operator1);
		
		SimpleOperator operator2 = new SimpleOperator("Robot1::place_cup()",
				new AllenIntervalConstraint[] {placeCupAfterPickup},
				new String[] {"Robot1::pickup_cup(arm)"},
				new int[] {1});
		operator2.addConstraint(durationPlaceCup, 0, 0);
		rd.addOperator(operator2);

		SimpleOperator operator1res = new SimpleOperator("Robot1::pickup_fork(arm)",
				null,
				null,
				new int[] {1});
		operator1res.addConstraint(durationPickupFork, 0, 0);
		rd.addOperator(operator1res);
		
		SimpleOperator operator2res = new SimpleOperator("Robot1::pickup_cup(arm)",
				null,
				null,
				new int[] {1});
		operator2res.addConstraint(durationPickupCup, 0, 0);
		rd.addOperator(operator2res);
		
		
		
		//This adds the domain as a meta-constraint of the SimplePlanner
		planner.addMetaConstraint(rd);
		//... and we also add all its resources as separate meta-constraints
		for (Schedulable sch : rd.getSchedulingMetaConstraints()) planner.addMetaConstraint(sch);
		
		// INITIAL AND GOAL STATE DEFS
		Activity one = (Activity)groundSolver.createVariable("Robot1");
		one.setSymbolicDomain("place_cup()");
		
		// ... this is a goal (i.e., an activity to justify through the meta-constraint)
		one.setMarking(markings.UNJUSTIFIED);
		

		Activity two = (Activity)groundSolver.createVariable("Robot1");
		two.setSymbolicDomain("pickup_cup(arm)");
		
		// ... this is a goal (i.e., an activity to justify through the meta-constraint)
		two.setMarking(markings.JUSTIFIED);
		
//		
//		Activity three = (Activity)groundSolver.createVariable("Robot1");
//		three.setSymbolicDomain("place_knife(arm)");
//		
//		// ... this is a goal (i.e., an activity to justify through the meta-constraint)
//		three.setMarking(markings.UNJUSTIFIED);
		

		TimelinePublisher tp = new TimelinePublisher(groundSolver, new Bounds(0,60), "Robot1");
		//TimelinePublisher can also be instantiated w/o bounds, in which case the bounds are calculated every time publish is called
//		TimelinePublisher tp = new TimelinePublisher(groundSolver, "Robot1", "Robot2", "LocalizationService", "RFIDReader1", "LaserScanner1");
		TimelineVisualizer viz = new TimelineVisualizer(tp);
		tp.publish(false, false);
		//the following call is marked as "skippable" and will most likely be skipped because the previous call has not finished rendering...
		tp.publish(false, true);
		
		planner.backtrack();
		
		ConstraintNetwork.draw(groundSolver.getConstraintNetwork(), "Constraint Network");
		
		planner.draw();
		tp.publish(true, false);


	}

}
