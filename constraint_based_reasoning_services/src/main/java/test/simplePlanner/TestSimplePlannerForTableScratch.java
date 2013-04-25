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
				
		SimpleDomain rd = new SimpleDomain(new int[] {2}, new String[] {"arm"}, "TestDomain");
		
		AllenIntervalConstraint duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(1,APSPSolver.INF));
		AllenIntervalConstraint moveToDuringLocalization = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds());
		
		SimpleOperator operator1 = new SimpleOperator("Robot1::place_fork(arm)",
				new AllenIntervalConstraint[] {moveToDuringLocalization},
				new String[] {"Robot1::pickup_fork(arm)"},
				null);
		operator1.addConstraint(duration, 0, 0);
		rd.addOperator(operator1);
		
		SimpleOperator operator2 = new SimpleOperator("Robot1::place_cup(arm)",
				new AllenIntervalConstraint[] {moveToDuringLocalization},
				new String[] {"Robot1::pickup_cup(arm)"},
				null);
		rd.addOperator(operator2);

		
		SimpleOperator operator3 = new SimpleOperator("Robot1::place_knife(arm)",
				new AllenIntervalConstraint[] {moveToDuringLocalization},
				new String[] {"Robot1::pickup_knife(arm)"},
				null);
		rd.addOperator(operator3);
		
		SimpleOperator operator4 = new SimpleOperator("Robot1::pickup_cup(arm)",
				null,
				null,
				new int[] {1});
		rd.addOperator(operator4);
		
		
		SimpleOperator operator5 = new SimpleOperator("Robot1::pickup_fork(arm)",
				null,
				null,
				new int[] {1});
		rd.addOperator(operator5);
		
		SimpleOperator operator6 = new SimpleOperator("Robot1::pickup_knife(arm)",
				null,
				null,
				new int[] {1});
		rd.addOperator(operator6);
		
		
		
		SimpleOperator operator7 = new SimpleOperator("Robot1::place_cup(arm)",
				null,
				null,
				new int[] {1});
		rd.addOperator(operator7);
		
		
		SimpleOperator operator8 = new SimpleOperator("Robot1::place_fork(arm)",
				null,
				null,
				new int[] {1});
		rd.addOperator(operator8);
		
		SimpleOperator operator9 = new SimpleOperator("Robot1::place_knife(arm)",
				null,
				null,
				new int[] {1});
		rd.addOperator(operator9);

//		SimpleOperator operator4 = new SimpleOperator("Robot1::pickup_cup()",
//				new AllenIntervalConstraint[] {moveToDuringLocalization},
//				new String[] {"Robot1::place_fork()"},
//				null);
//		rd.addOperator(operator4);
//
//		
//		SimpleOperator operator5 = new SimpleOperator("Robot1::pickup_cup()",
//				new AllenIntervalConstraint[] {moveToDuringLocalization},
//				new String[] {"Robot1::place_knife()"},
//				null);
//		rd.addOperator(operator5);
		

		
		
		
		//This adds the domain as a meta-constraint of the SimplePlanner
		planner.addMetaConstraint(rd);
		//... and we also add all its resources as separate meta-constraints
		for (Schedulable sch : rd.getSchedulingMetaConstraints()) planner.addMetaConstraint(sch);
		
		// INITIAL AND GOAL STATE DEFS
		Activity one = (Activity)groundSolver.createVariable("Robot1");
		one.setSymbolicDomain("place_cup(arm)");
		
		// ... this is a goal (i.e., an activity to justify through the meta-constraint)
		one.setMarking(markings.UNJUSTIFIED);
		

		Activity two = (Activity)groundSolver.createVariable("Robot1");
		two.setSymbolicDomain("place_fork(arm)");
		
		// ... this is a goal (i.e., an activity to justify through the meta-constraint)
		two.setMarking(markings.UNJUSTIFIED);
		
		
		Activity three = (Activity)groundSolver.createVariable("Robot1");
		three.setSymbolicDomain("place_knife(arm)");
		
		// ... this is a goal (i.e., an activity to justify through the meta-constraint)
		three.setMarking(markings.UNJUSTIFIED);
		

		TimelinePublisher tp = new TimelinePublisher(groundSolver, new Bounds(0,25), "Robot1");
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
