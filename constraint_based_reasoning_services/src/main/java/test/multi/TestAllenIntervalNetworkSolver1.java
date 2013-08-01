package test.multi;

import org.metacsp.multi.allenInterval.AllenInterval;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint.Type;
import org.metacsp.multi.allenInterval.AllenIntervalNetworkSolver;
import org.metacsp.time.APSPSolver;
import org.metacsp.time.Bounds;
import org.metacsp.framework.ConstraintNetwork;

public class TestAllenIntervalNetworkSolver1 {
	
	public static void main(String[] args) {
		
		// this classes extends MultiConstraintSolver that has the field constraintSolvers
		// this array of constraintSolvers is initialized to APSPSolver
		AllenIntervalNetworkSolver solver = new AllenIntervalNetworkSolver(0, 100);
		
		// this method instantiates a variable within the constraintSolver
		// in particular this method calls the createVariableSub that is particular of the specific variable
		// this variable and the related constraints, if any, are put in the internalConstraintSolvers of "solver"
		// furthermore, mutual references between (solver, internalSolvers) and (variable, related constraints) are created
		AllenInterval act0 = (AllenInterval)solver.createVariable();
		AllenInterval act1 = (AllenInterval)solver.createVariable();
		//AllenInterval act2 = (AllenInterval)solver.createVariable();

		ConstraintNetwork.draw(solver.getConstraintNetwork());
		ConstraintNetwork.draw(solver.getConstraintSolvers()[0].getConstraintNetwork());
		
		AllenIntervalConstraint con0 = new AllenIntervalConstraint(Type.Overlaps, new Bounds(0, APSPSolver.INF));
		con0.setFrom(act0);
		con0.setTo(act1);
		
		AllenIntervalConstraint con1 = new AllenIntervalConstraint(Type.Overlaps, new Bounds(0, APSPSolver.INF));
		con1.setFrom(act1);
		con1.setTo(act0);

		AllenIntervalConstraint con2 = new AllenIntervalConstraint(Type.Release, new Bounds(5, 5));
		con2.setFrom(act0);
		con2.setTo(act0);

//		solver.addConstraint(con0);
//		solver.setOptions(OPTIONS.ALLOW_INCONSISTENCIES);
//		solver.addConstraint(con1);
//		solver.addConstraint(con2);
		
//		((APSPSolver)solver.getConstraintSolvers()[0]).draw();
		
		
		AllenIntervalConstraint[] allConstraints = {con0,con1,con2};
		if (!solver.addConstraints(allConstraints)) { 
			System.out.println("Failed to add constraints!");
			//System.exit(0);
		}
		
		
		
		
				
		//System.out.println(act0.getDomain().chooseValues());
		
	}
	

}
