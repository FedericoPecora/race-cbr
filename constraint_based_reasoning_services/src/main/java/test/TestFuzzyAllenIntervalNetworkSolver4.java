package test;

import framework.ConstraintNetwork;
import fuzzyAllenInterval.FuzzyAllenIntervalConstraint;
import fuzzyAllenInterval.FuzzyAllenIntervalNetworkSolver;
import time.qualitative.SimpleAllenInterval;

public class TestFuzzyAllenIntervalNetworkSolver4 {
	
	public static void main(String[] args) {
		FuzzyAllenIntervalNetworkSolver solver = new FuzzyAllenIntervalNetworkSolver();
		
		SimpleAllenInterval sai0 = (SimpleAllenInterval)solver.createVariable("aComponent");
		SimpleAllenInterval sai1 = (SimpleAllenInterval)solver.createVariable("aComponent");
		SimpleAllenInterval sai2 = (SimpleAllenInterval)solver.createVariable("aComponent");
		
		FuzzyAllenIntervalConstraint con0 = new FuzzyAllenIntervalConstraint(FuzzyAllenIntervalConstraint.Type.During);
		con0.setFrom(sai0);
		con0.setTo(sai1);

		FuzzyAllenIntervalConstraint con1 = new FuzzyAllenIntervalConstraint(FuzzyAllenIntervalConstraint.Type.Contains);
		con1.setFrom(sai0);
		con1.setTo(sai2);
		
		FuzzyAllenIntervalConstraint con2 = new FuzzyAllenIntervalConstraint(FuzzyAllenIntervalConstraint.Type.Before);
		con2.setFrom(sai1);
		con2.setTo(sai2);
		
		
		ConstraintNetwork.draw(solver.getConstraintNetwork());
		
		solver.addConstraint(con0);
		solver.addConstraint(con1);
		solver.addConstraint(con2);
		
		FuzzyAllenIntervalConstraint[] allConstraints = {con0,con1,con2};
		if (!solver.addConstraints(allConstraints)) { 
			System.out.println("Failed to add constraints!");
			System.exit(0);
		}
		
		
		System.out.println("Poss: " + solver.getPosibilityDegree());
		
	}
	

}
