package test.race;

import meta.simplePlanner.SimpleDomain;
import meta.simplePlanner.SimpleDomain.markings;
import meta.simplePlanner.SimpleOperator;
import meta.simplePlanner.SimplePlanner;
import meta.symbolsAndTime.Schedulable;
import meta.symbolsAndTime.Scheduler;
import meta.symbolsAndTime.SymbolicTimeline;
import multi.activity.Activity;
import multi.activity.ActivityNetworkSolver;
import multi.allenInterval.AllenIntervalConstraint;
import time.APSPSolver;
import time.Bounds;
import framework.ConstraintNetwork;


public class TestReusableResourceSchedulerForRACE {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		SimplePlanner planner = new SimplePlanner(0,600,0);
		

		ActivityNetworkSolver groundSolver = (ActivityNetworkSolver)((Scheduler)planner.getConstraintSolvers()[0]).getConstraintSolvers()[0];
		SimpleDomain rd = new SimpleDomain(new int[] {625}, new String[] {"tray"}, "Iran's domain");
		
		AllenIntervalConstraint durationMoveTo = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10,APSPSolver.INF));
		AllenIntervalConstraint equal = new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds());

		
		SimpleOperator rule1 = new SimpleOperator("Robot1::MoveCutlery()",
				new AllenIntervalConstraint[] {equal},
				new String[] {"PutObject::Cutlery(tray)"},
				null);
		rule1.addConstraint(durationMoveTo, 0, 0);
		rd.addOperator(rule1);
		
		
		SimpleOperator rule2 = new SimpleOperator("Robot1::MoveGlass()",
				new AllenIntervalConstraint[] {equal},
				new String[] {"PutObject::Glass(tray)"},
				null);
		rule2.addConstraint(durationMoveTo, 0, 0);	
		rd.addOperator(rule2);
		
		SimpleOperator rule3 = new SimpleOperator("Robot1::MovePlate()",
				new AllenIntervalConstraint[] {equal},
				new String[] {"PutObject::Plate(tray)"},
				null);
		rule3.addConstraint(durationMoveTo, 0, 0);
		rd.addOperator(rule3);
	
///............................................................................................		
		SimpleOperator rule4 = new SimpleOperator("PutObject::Plate(tray)",
				null,
				null,
				new int[] {400});
		rd.addOperator(rule4);
		
		SimpleOperator rule5 = new SimpleOperator("PutObject::Glass(tray)",
				null,
				null,
				new int[] {225});
		rd.addOperator(rule5);
		
		SimpleOperator rule6 = new SimpleOperator("PutObject::Cutlery(tray)",
				null,
				null,
				new int[] {50});
		rd.addOperator(rule6);

		planner.addMetaConstraint(rd);
		//... and we also add all its resources as separate meta-constraints
		for (Schedulable sch : rd.getSchedulingMetaConstraints()) planner.addMetaConstraint(sch);
		
		Activity three = (Activity)groundSolver.createVariable("Robot1");
		three.setSymbolicDomain("MovePlate()");
		three.setMarking(markings.UNJUSTIFIED);
		
		Activity one = (Activity)groundSolver.createVariable("Robot1");
		one.setSymbolicDomain("MoveGlass()");
		one.setMarking(markings.UNJUSTIFIED);
		
		Activity two = (Activity)groundSolver.createVariable("Robot1");
		two.setSymbolicDomain("MoveCutlery()");
		two.setMarking(markings.UNJUSTIFIED);
		

		
		
		planner.backtrack();
		

		ConstraintNetwork.draw(groundSolver.getConstraintNetwork(), "Constraint Network");

		SymbolicTimeline tlRobot1 = new SymbolicTimeline(groundSolver,"Robot1");
		tlRobot1.draw();

		SymbolicTimeline tlMoveObject = new SymbolicTimeline(groundSolver,"PutObject");
		tlMoveObject.draw();
		
		((ActivityNetworkSolver)groundSolver).drawAsGantt();
		
		planner.draw();

		
	}

}
