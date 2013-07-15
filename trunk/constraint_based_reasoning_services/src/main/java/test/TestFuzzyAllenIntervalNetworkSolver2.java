package test;

import framework.ConstraintNetwork;
import fuzzyAllenInterval.FuzzyAllenIntervalConstraint;
import fuzzyAllenInterval.FuzzyAllenIntervalNetworkSolver;
import time.qualitative.SimpleAllenInterval;

public class TestFuzzyAllenIntervalNetworkSolver2 {
	
	public static void main(String[] args) {
		FuzzyAllenIntervalNetworkSolver solver = new FuzzyAllenIntervalNetworkSolver();
		
		SimpleAllenInterval sai0 = (SimpleAllenInterval)solver.createVariable("aComponent");
		SimpleAllenInterval sai1 = (SimpleAllenInterval)solver.createVariable("aComponent");
		
		FuzzyAllenIntervalConstraint con0 = new FuzzyAllenIntervalConstraint(FuzzyAllenIntervalConstraint.Type.Before);
		con0.setFrom(sai0);
		con0.setTo(sai1);

		FuzzyAllenIntervalConstraint con1 = new FuzzyAllenIntervalConstraint(FuzzyAllenIntervalConstraint.Type.Before);
		con1.setFrom(sai1);
		con1.setTo(sai0);
				
		ConstraintNetwork.draw(solver.getConstraintNetwork());
				
		FuzzyAllenIntervalConstraint[] allConstraints = {con0,con1};
		if (!solver.addConstraints(allConstraints)) { 
			System.out.println("Failed to add constraints!");
			System.exit(0);
		}
		
		
		System.out.println("Poss: " + solver.getPosibilityDegree());
		
		
	}
	

}
