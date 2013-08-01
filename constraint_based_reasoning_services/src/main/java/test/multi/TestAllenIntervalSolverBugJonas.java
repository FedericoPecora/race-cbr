package test.multi;

import org.metacsp.multi.allenInterval.AllenInterval;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint.Type;
import org.metacsp.multi.allenInterval.AllenIntervalNetworkSolver;
import org.metacsp.time.APSPSolver;
import org.metacsp.time.Bounds;


public class TestAllenIntervalSolverBugJonas {

	public static void main(String[] args) {
		AllenIntervalNetworkSolver solver = new AllenIntervalNetworkSolver(0, 100);
		AllenInterval interval = (AllenInterval) solver.createVariable();
		
		//Create a constraint to fix the source interval in time
		AllenIntervalConstraint aic = new AllenIntervalConstraint(Type.At,
			new Bounds(10, 10),
			new Bounds(20, 20),
			new Bounds(0, APSPSolver.INF)
		);
		aic.setFrom(interval);
		aic.setTo(interval);
		if(!solver.addConstraint(aic)) {
			System.out.println("Failed to add constraint");
		} else {
			//Prints "AllenInterval 0 [[0,100], [0,100]]"
			System.out.println(interval);
		}
	}
}
