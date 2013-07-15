package test.race;

import meta.symbolsAndTime.Scheduler;
import meta.symbolsAndTime.StateVariable;
import meta.symbolsAndTime.SymbolicTimeline;
import multi.activity.Activity;
import multi.activity.ActivityNetworkSolver;
import multi.allenInterval.AllenIntervalConstraint;

import time.Bounds;
import framework.Constraint;
import framework.ConstraintNetwork;
import framework.ValueOrderingH;
import framework.VariableOrderingH;

public class TestStateVariableSchedulerForRACE {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		final Scheduler metaSolver = new Scheduler(0,600,0);
//		final ActivityNetworkSolver groundSolver = (ActivityNetworkSolver)metaSolver.getConstraintSolvers()[0];
//		
//		Activity one = (Activity)groundSolver.createVariable("comp1");
//		one.setSymbolicDomain("F", "G");
//		Activity oneA = (Activity)groundSolver.createVariable("comp1");
//		oneA.setSymbolicDomain("A", "B", "C");
//		Activity oneB = (Activity)groundSolver.createVariable("comp1");
//		oneB.setSymbolicDomain("D", "E");
//
//		Activity oneAA = (Activity)groundSolver.createVariable("comp1");
//		oneAA.setSymbolicDomain("A", "G");
//		Activity oneAB = (Activity)groundSolver.createVariable("comp1");
//		oneAB.setSymbolicDomain("B", "F");
//		Activity oneAC = (Activity)groundSolver.createVariable("comp1");
//		oneAC.setSymbolicDomain("C", "E");
//
//		//metaSolver.draw();
//
//		//DURATIONS
//		AllenIntervalConstraint dur1 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(35, 55));
//		dur1.setFrom(oneA);
//		dur1.setTo(oneA);
//		AllenIntervalConstraint dur2 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(35, 55));
//		dur2.setFrom(oneB);
//		dur2.setTo(oneB);
//		AllenIntervalConstraint dur3 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(35, 55));
//		dur3.setFrom(one);
//		dur3.setTo(one);
//		AllenIntervalConstraint dur4 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(35, 55));
//		dur4.setFrom(oneAA);
//		dur4.setTo(oneAA);
//		AllenIntervalConstraint dur5 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(35, 55));
//		dur5.setFrom(oneAB);
//		dur5.setTo(oneAB);
//		AllenIntervalConstraint dur6 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(35, 55));
//		dur6.setFrom(oneAC);
//		dur6.setTo(oneAC);
//
//
//		//PRECEDENCES
//		AllenIntervalConstraint con1 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(0, APSPSolver.INF));
//		con1.setFrom(one);
//		con1.setTo(oneA);
//		AllenIntervalConstraint con2 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(0, APSPSolver.INF));
//		con2.setFrom(one);
//		con2.setTo(oneB);
//		AllenIntervalConstraint con3 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(0, APSPSolver.INF));
//		con3.setFrom(oneA);
//		con3.setTo(oneAA);
//		AllenIntervalConstraint con4 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(0, APSPSolver.INF));
//		con4.setFrom(oneA);
//		con4.setTo(oneAB);
//		AllenIntervalConstraint con5 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(0, APSPSolver.INF));
//		con5.setFrom(oneA);
//		con5.setTo(oneAC);
//	
//		//Add the constraints
//		//Constraint[] cons = new Constraint[]{dur1,dur2,dur3,dur4,dur5,dur6,con1,con2,con3,con4,con5};
//		Constraint[] cons = new Constraint[]{dur1,dur2,dur3,dur4,dur5,dur6};
//		groundSolver.addConstraints(cons);
//		
//		//Most critical conflict is the one with most activities (largest peak)
//		VariableOrderingH varOH = new VariableOrderingH() {
//			@Override
//			public int compare(ConstraintNetwork arg0, ConstraintNetwork arg1) {
//				// TODO Auto-generated method stub
//				return arg1.getVariables().length - arg0.getVariables().length;
//			}
//
//			@Override
//			public void collectData(ConstraintNetwork[] allMetaVariables) {
//				// TODO Auto-generated method stub
//				
//			}
//		};
//		
//		ValueOrderingH valOH = new ValueOrderingH() {
//			@Override
//			public int compare(ConstraintNetwork o1, ConstraintNetwork o2) {
//				// TODO Auto-generated method stub
//				return 0;
//			}
//		};
//		
//		StateVariable sv = new StateVariable(varOH, valOH, metaSolver, new SymbolicDomain(null, "A", "B", "C", "D", "E", "F", "G"));
//		sv.setUsage(one,oneA,oneB,oneAA,oneAB,oneAC);
//		metaSolver.addMetaConstraint(sv);
//		metaSolver.backtrack();
//
//		
//		SymbolicTimeline tl = new SymbolicTimeline(groundSolver,"comp1");
//		tl.draw();
//		
////		SymbolicTimeline tl2 = new SymbolicTimeline(groundSolver,"comp2");
////		tl2.draw();

//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
		
		final Scheduler metaSolver = new Scheduler(0,600,0);
		final ActivityNetworkSolver groundSolver = (ActivityNetworkSolver)metaSolver.getConstraintSolvers()[0];
		
		Activity one = (Activity)groundSolver.createVariable("Robot1");
		one.setSymbolicDomain("task1", "task2");
		Activity oneA = (Activity)groundSolver.createVariable("Robot1");
		oneA.setSymbolicDomain("task1");
//		Activity oneB = (Activity)groundSolver.createVariable("comp1");
//		oneB.setSymbolicDomain("D", "E");

//		Activity oneAA = (Activity)groundSolver.createVariable("comp1");
//		oneAA.setSymbolicDomain("A", "G");
//		Activity oneAB = (Activity)groundSolver.createVariable("comp1");
//		oneAB.setSymbolicDomain("B", "F");
//		Activity oneAC = (Activity)groundSolver.createVariable("comp1");
//		oneAC.setSymbolicDomain("C", "E");

		//metaSolver.draw();

		//DURATIONS
		AllenIntervalConstraint dur1 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(35, 55));
		dur1.setFrom(oneA);
		dur1.setTo(oneA);
//		AllenIntervalConstraint dur2 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(35, 55));
//		dur2.setFrom(oneB);
//		dur2.setTo(oneB);
		AllenIntervalConstraint dur3 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(35, 55));
		dur3.setFrom(one);
		dur3.setTo(one);
//		AllenIntervalConstraint dur4 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(35, 55));
//		dur4.setFrom(oneAA);
//		dur4.setTo(oneAA);
//		AllenIntervalConstraint dur5 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(35, 55));
//		dur5.setFrom(oneAB);
//		dur5.setTo(oneAB);
//		AllenIntervalConstraint dur6 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(35, 55));
//		dur6.setFrom(oneAC);
//		dur6.setTo(oneAC);


	
	
		//Add the constraints
		//Constraint[] cons = new Constraint[]{dur1,dur2,dur3,dur4,dur5,dur6,con1,con2,con3,con4,con5};
		Constraint[] cons = new Constraint[]{dur1,dur3};
		groundSolver.addConstraints(cons);
		
		//Most critical conflict is the one with most activities (largest peak)
		VariableOrderingH varOH = new VariableOrderingH() {
			@Override
			public int compare(ConstraintNetwork arg0, ConstraintNetwork arg1) {
				// TODO Auto-generated method stub
				return arg1.getVariables().length - arg0.getVariables().length;
			}

			@Override
			public void collectData(ConstraintNetwork[] allMetaVariables) {
				// TODO Auto-generated method stub
				
			}
		};
		
		ValueOrderingH valOH = new ValueOrderingH() {
			@Override
			public int compare(ConstraintNetwork o1, ConstraintNetwork o2) {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		
		StateVariable sv = new StateVariable(varOH, valOH, metaSolver, new String[] {"task1", "task2"});
		sv.setUsage(one,oneA);
		metaSolver.addMetaConstraint(sv);
		metaSolver.backtrack();
		
		((ActivityNetworkSolver)groundSolver).drawAsGantt();
		
		SymbolicTimeline tl = new SymbolicTimeline(groundSolver,"Robot1");
		tl.draw();
		
//		SymbolicTimeline tl2 = new SymbolicTimeline(groundSolver,"comp2");
//		tl2.draw();


		
		
	}

}
