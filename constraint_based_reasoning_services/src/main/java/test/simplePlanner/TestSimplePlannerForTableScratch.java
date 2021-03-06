package test.simplePlanner;

import java.util.logging.Level;

import org.metacsp.meta.simplePlanner.SimpleDomain;
import org.metacsp.meta.simplePlanner.SimpleOperator;
import org.metacsp.meta.simplePlanner.SimplePlanner;
import org.metacsp.meta.simplePlanner.SimpleDomain.markings;
import org.metacsp.meta.symbolsAndTime.Schedulable;
import org.metacsp.multi.activity.Activity;
import org.metacsp.multi.activity.ActivityNetworkSolver;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.time.APSPSolver;
import org.metacsp.time.Bounds;
import org.metacsp.utility.logging.MetaCSPLogging;
import org.metacsp.utility.timelinePlotting.TimelinePublisher;
import org.metacsp.utility.timelinePlotting.TimelineVisualizer;
import org.metacsp.framework.ConstraintNetwork;

public class TestSimplePlannerForTableScratch {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		MetaCSPLogging.setLevel(TimelinePublisher.class, Level.FINEST);

		SimplePlanner planner = new SimplePlanner(0,600,0);		
		// This is a pointer toward the ActivityNetwork solver of the Scheduler
		ActivityNetworkSolver groundSolver = (ActivityNetworkSolver)planner.getConstraintSolvers()[0];

		MetaCSPLogging.setLevel(planner.getClass(), Level.FINE);
		
//		MetaCSPLogging.setLevel(Level.FINEST);
//		MetaCSPLogging.setLevel(planner.getClass(), Level.FINE);
				
		SimpleDomain rd = new SimpleDomain(new int[] {2}, new String[] {"arm"}, "TestDomain");

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
				new int[] {1});
		operator1.addConstraint(atCup1Duration, 0, 0);
		rd.addOperator(operator1);
		
		SimpleOperator operator2 = new SimpleOperator("robot1::place_cup1_table1(arm)",
				new AllenIntervalConstraint[] {placeCupAfterholding},
				new String[] {"robot1::holding_cup1(arm)"},
				new int[] {1});
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
				new int[] {1});
		operator1res.addConstraint(pickCup1Duration, 0, 0);
		rd.addOperator(operator1res);
		
		//........................

		SimpleOperator operator4 = new SimpleOperator("robot1::at_knife1_table1()",
				new AllenIntervalConstraint[] {atKnifeAfterPlace},
				new String[] {"robot1::place_knife1_table1(arm)"},
				new int[] {1});
		operator4.addConstraint(atKnife1Duration, 0, 0);
		rd.addOperator(operator4);
		
		SimpleOperator operator5 = new SimpleOperator("robot1::place_knife1_table1(arm)",
				new AllenIntervalConstraint[] {placeKnifeAfterholding},
				new String[] {"robot1::holding_knife1(arm)"},
				new int[] {1});
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
				new int[] {1});
		operator2res.addConstraint(pickKnife1Duration, 0, 0);
		rd.addOperator(operator2res);

		//........................
		
		SimpleOperator operator7 = new SimpleOperator("robot1::at_fork_table1()",
				new AllenIntervalConstraint[] {atForkAfterPlace},
				new String[] {"robot1::place_fork1_table1(arm)"},
				new int[] {1});
		operator7.addConstraint(atFork1Duration, 0, 0);
		rd.addOperator(operator7);
		
		SimpleOperator operator8 = new SimpleOperator("robot1::place_fork1_table1(arm)",
				new AllenIntervalConstraint[] {placeForkAfterholding},
				new String[] {"robot1::holding_fork1(arm)"},
				new int[] {1});
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
				new int[] {1});
		operator3res.addConstraint(pickFork1Duration, 0, 0);
		rd.addOperator(operator3res);
		
		
		//This adds the domain as a meta-constraint of the SimplePlanner
		planner.addMetaConstraint(rd);
		//... and we also add all its resources as separate meta-constraints
		MetaCSPLogging.setLevel(Schedulable.class, Level.FINEST);
		for (Schedulable sch : rd.getSchedulingMetaConstraints()) planner.addMetaConstraint(sch);
		
		// INITIAL AND GOAL STATE DEFS
		Activity three = (Activity)groundSolver.createVariable("robot1");
		three.setSymbolicDomain("at_knife1_table1()");
		three.setMarking(markings.UNJUSTIFIED);
		
		
		Activity one = (Activity)groundSolver.createVariable("robot1");
		one.setSymbolicDomain("at_cup1_table1()");
		one.setMarking(markings.UNJUSTIFIED);
		

		Activity two = (Activity)groundSolver.createVariable("robot1");
		two.setSymbolicDomain("at_fork1_table1()");
		two.setMarking(markings.UNJUSTIFIED);
		
		
		
		
		planner.backtrack();
		TimelinePublisher tp = new TimelinePublisher(groundSolver, new Bounds(0,100), "robot1");
		//TimelinePublisher can also be instantiated w/o bounds, in which case the bounds are calculated every time publish is called
//		TimelinePublisher tp = new TimelinePublisher(groundSolver, "Robot1", "Robot2", "LocalizationService", "RFIDReader1", "LaserScanner1");
		TimelineVisualizer viz = new TimelineVisualizer(tp);
		tp.publish(false, false);
		//the following call is marked as "skippable" and will most likely be skipped because the previous call has not finished rendering...
		tp.publish(false, true);
		
		
		
		ConstraintNetwork.draw(groundSolver.getConstraintNetwork(), "Constraint Network");
		
		planner.draw();
		tp.publish(true, false);


	}

}
