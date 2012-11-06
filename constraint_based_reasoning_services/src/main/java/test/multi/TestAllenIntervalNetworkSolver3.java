package test.multi;

import multi.allenInterval.AllenInterval;
import multi.allenInterval.AllenIntervalConstraint;
import multi.allenInterval.AllenIntervalConstraint.Type;
import multi.allenInterval.AllenIntervalNetworkSolver;
import time.APSPSolver;
import time.Bounds;
import framework.ConstraintNetwork;

//created for test for ICAPS WS
public class TestAllenIntervalNetworkSolver3 {
	
	public static void main(String[] args) {
		
		
		
		
		AllenIntervalNetworkSolver solver = new AllenIntervalNetworkSolver(0, 500);
		//cooking
		AllenInterval act0 = (AllenInterval)solver.createVariable();
		//location
		AllenInterval act1 = (AllenInterval)solver.createVariable();
		//stove
		AllenInterval act2 = (AllenInterval)solver.createVariable();

		//ConstraintNetwork.draw(solver.getConstraintNetwork());
		//ConstraintNetwork.draw(solver.getConstraintSolvers()[0].getConstraintNetwork());
		
		AllenIntervalConstraint con0 = new AllenIntervalConstraint(Type.Contains, new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF));
		con0.setFrom(act0);
		con0.setTo(act1);
		
		AllenIntervalConstraint con1 = new AllenIntervalConstraint(Type.Contains, new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF));
		con1.setFrom(act0);
		con1.setTo(act2);

		AllenIntervalConstraint con2 = new AllenIntervalConstraint(Type.Before, new Bounds(0, APSPSolver.INF));
		con2.setFrom(act1);
		con2.setTo(act2);
		
		AllenIntervalConstraint con3 = new AllenIntervalConstraint(Type.At, new Bounds(1, 1), new Bounds(12, 12));
		con3.setFrom(act1);
		con3.setTo(act1);
		
		AllenIntervalConstraint con4 = new AllenIntervalConstraint(Type.At, new Bounds(15, 15), new Bounds(18, 18));
		con4.setFrom(act2);
		con4.setTo(act2);

//		AllenIntervalConstraint con3 = new AllenIntervalConstraint(solver, Type.Release, new Bounds(1, 1));
//		con3.setFrom(act1);
//		con3.setTo(act1);
//		AllenIntervalConstraint con4 = new AllenIntervalConstraint(solver, Type.Duration, new Bounds(12, 12));
//		con4.setFrom(act1);
//		con4.setTo(act1);
//		
//		AllenIntervalConstraint con5 = new AllenIntervalConstraint(solver, Type.Release, new Bounds(3, 3));
//		con5.setFrom(act2);
//		con5.setTo(act2);
//		AllenIntervalConstraint con6 = new AllenIntervalConstraint(solver, Type.Duration, new Bounds(5, 5));
//		con6.setFrom(act2);
//		con6.setTo(act2);
		
		System.out.println(solver.addConstraint(con0));
		System.out.println(solver.addConstraint(con1));
		System.out.println(solver.addConstraint(con2));
		System.out.println(solver.addConstraint(con3));
		System.out.println(solver.addConstraint(con4));
//		System.out.println(solver.addConstraint(con5));
//		System.out.println(solver.addConstraint(con6));
		
		ConstraintNetwork.draw(solver.getConstraintNetwork());
		
		
//		((APSPSolver)solver.getConstraintSolvers()[0]).draw();
		
		
//		AllenIntervalConstraint[] allConstraints = {con0,con1,con2, con3, con4};
//		solver.addConstraints(allConstraints);
		//ConstraintNetwork.draw(solver.getConstraintSolvers()[0].getConstraintNetwork());
		System.out.println(act0);
		System.out.println(act0.getEST());
		System.out.println(act0.getLST());
		System.out.println(act0.getEET());
		System.out.println(act0.getLET());
		/*if (!solver.addConstraints(allConstraints)) { 
			System.out.println("Failed to add constraints!");
			//System.exit(0);
		}*/
		
		
		
		
				
		//System.out.println(act0.getDomain().chooseValues());
		
	}
	

}
