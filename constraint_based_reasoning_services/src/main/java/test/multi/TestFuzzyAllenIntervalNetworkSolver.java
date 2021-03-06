package test.multi;

import org.metacsp.time.qualitative.SimpleAllenInterval;
import org.metacsp.framework.ConstraintNetwork;
import org.metacsp.fuzzyAllenInterval.FuzzyAllenIntervalConstraint;
import org.metacsp.fuzzyAllenInterval.FuzzyAllenIntervalNetworkSolver;

public class TestFuzzyAllenIntervalNetworkSolver {
	
	public static void main(String[] args) {
		FuzzyAllenIntervalNetworkSolver solver = new FuzzyAllenIntervalNetworkSolver();
		SimpleAllenInterval act0 = (SimpleAllenInterval)solver.createVariable();
		SimpleAllenInterval act1 = (SimpleAllenInterval)solver.createVariable();
		SimpleAllenInterval act2 = (SimpleAllenInterval)solver.createVariable();

		ConstraintNetwork.draw(solver.getConstraintNetwork());
				
		FuzzyAllenIntervalConstraint con0 = new FuzzyAllenIntervalConstraint(FuzzyAllenIntervalConstraint.Type.After);
		con0.setFrom(act0);
		con0.setTo(act1);
		//if (!solver.addConstraint(con0)) System.out.println("Failed to add constraint " + con0);
		
		FuzzyAllenIntervalConstraint con1 = new FuzzyAllenIntervalConstraint(FuzzyAllenIntervalConstraint.Type.Contains);
		con1.setFrom(act1);
		con1.setTo(act2);
		//if (!solver.addConstraint(con1)) System.out.println("Failed to add constraint " + con1);

		FuzzyAllenIntervalConstraint con3 = new FuzzyAllenIntervalConstraint(FuzzyAllenIntervalConstraint.Type.Meets);
		con3.setFrom(act2);
		con3.setTo(act0);
		//if (!solver.addConstraint(con3)) System.out.println("Failed to add constraint " + con2);

		
		FuzzyAllenIntervalConstraint[] allConstraints = {con0,con1,con3};
		if (!solver.addConstraints(allConstraints)) {
			System.out.println("Failed to add constraints!");
			System.exit(0);
		}
		
		System.out.println(solver.getPosibilityDegree());
		
		
		
	}
	

}
