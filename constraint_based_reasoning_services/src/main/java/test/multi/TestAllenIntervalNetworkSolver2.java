package test.multi;

import multi.allenInterval.AllenInterval;
import multi.allenInterval.AllenIntervalConstraint;
import multi.allenInterval.AllenIntervalConstraint.Type;
import multi.allenInterval.AllenIntervalNetworkSolver;
import framework.ConstraintNetwork;

public class TestAllenIntervalNetworkSolver2 {
	
	public static void main(String[] args) {
		AllenIntervalNetworkSolver solver = new AllenIntervalNetworkSolver(0, 100);
		AllenInterval act0 = (AllenInterval)solver.createVariable();
		AllenInterval act1 = (AllenInterval)solver.createVariable();
		//AllenInterval act2 = (AllenInterval)solver.createVariable();

		ConstraintNetwork.draw(solver.getConstraintNetwork());
		ConstraintNetwork.draw(solver.getConstraintSolvers()[0].getConstraintNetwork());
		
		AllenIntervalConstraint con0 = new AllenIntervalConstraint(Type.Equals);
		con0.setFrom(act0);
		con0.setTo(act1);
		
		AllenIntervalConstraint con1 = new AllenIntervalConstraint(Type.Equals);
		con1.setFrom(act1);
		con1.setTo(act0);

		
//		solver.addConstraint(con0);
//		solver.setOptions(OPTIONS.ALLOW_INCONSISTENCIES);
//		solver.addConstraint(con1);
		
//		((APSPSolver)solver.getConstraintSolvers()[0]).draw();
		
		
		AllenIntervalConstraint[] allConstraints = {con0,con1};
		if (!solver.addConstraints(allConstraints)) { 
			System.out.println("Failed to add constraints!");
			//System.exit(0);
		}
		
		
		
		
				
		//System.out.println(act0.getDomain().chooseValues());
		
	}
	

}
