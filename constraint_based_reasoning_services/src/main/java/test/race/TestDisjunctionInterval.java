package test.race;

import org.metacsp.multi.allenInterval.AllenInterval;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.multi.allenInterval.AllenIntervalNetworkSolver;
import org.metacsp.framework.Constraint;
import org.metacsp.framework.ConstraintNetwork;

public class TestDisjunctionInterval {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		AllenIntervalNetworkSolver solver = new AllenIntervalNetworkSolver(0, 100);
		AllenInterval[] intervals = (AllenInterval[])solver.createVariables(2);
		
		
		//DRAW IT!
		ConstraintNetwork.draw(solver.getConstraintNetwork());
		
		AllenIntervalConstraint.Type[] test = new AllenIntervalConstraint.Type[3];
		test[0] = AllenIntervalConstraint.Type.Overlaps;
		test[1] = AllenIntervalConstraint.Type.Contains;
		test[2] = AllenIntervalConstraint.Type.FinishedBy;
		
		AllenIntervalConstraint con1 = new AllenIntervalConstraint(test);
		con1.setFrom(intervals[0]);
		con1.setTo(intervals[1]);
		
		

		Constraint[] cons = new Constraint[]{con1};
		solver.addConstraints(cons);

	}

}
