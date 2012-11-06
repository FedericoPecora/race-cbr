package test.meta;

import meta.TCSP.MostConstrainedFirstVarOH;
import meta.TCSP.TCSPLabeling;
import meta.TCSP.TCSPSolver;
import meta.TCSP.WidestIntervalFirstValOH;
import multi.TCSP.DistanceConstraint;
import multi.TCSP.DistanceConstraintSolver;
import multi.TCSP.MultiTimePoint;
import time.APSPSolver;
import time.Bounds;
import framework.ConstraintNetwork;
import framework.ValueOrderingH;
import framework.Variable;
import framework.VariableOrderingH;


public class TestTCSPSolverForFuzzySAM {
	
	public static void main(String args[]) {
		
		TCSPSolver metaSolver = new TCSPSolver(0, 100, 0);
		DistanceConstraintSolver groundSolver = (DistanceConstraintSolver)metaSolver.getConstraintSolvers()[0];
		
		//DistanceConstraintSolver groundSolver = new DistanceConstraintSolver(0, 100);
		APSPSolver groundGroundSolver = (APSPSolver)groundSolver.getConstraintSolvers()[0];

		MultiTimePoint kOn = (MultiTimePoint)groundSolver.createVariable();//2
		MultiTimePoint kOff = (MultiTimePoint)groundSolver.createVariable();//3
		MultiTimePoint sOn = (MultiTimePoint)groundSolver.createVariable();//4
		MultiTimePoint sOff = (MultiTimePoint)groundSolver.createVariable();//5
		MultiTimePoint cookingOn = (MultiTimePoint)groundSolver.createVariable();//6
		MultiTimePoint cookingOff = (MultiTimePoint)groundSolver.createVariable();//7
		
		ConstraintNetwork.draw(groundSolver.getConstraintNetwork());

		long k1 = 1, k2 = 12;
		long s1 = 15, s2 = APSPSolver.INF;		
		
		//kitchen interval
		DistanceConstraint kStartAfterKEnd = new DistanceConstraint(new Bounds(0, APSPSolver.INF));
		kStartAfterKEnd.setFrom(kOn);
		kStartAfterKEnd.setTo(kOff);
		//kitchen release
		DistanceConstraint kOnCon = new DistanceConstraint(new Bounds(k1, k1), new Bounds(k1, APSPSolver.INF), new Bounds(0, k1), new Bounds(0, APSPSolver.INF));
		kOnCon.setFrom(groundSolver.getSource());
		kOnCon.setTo(kOn);
		//kitchen end
		DistanceConstraint kOffCon = new DistanceConstraint(new Bounds(k2, k2), new Bounds(k2, APSPSolver.INF), new Bounds(0, k2), new Bounds(0, APSPSolver.INF));
		kOffCon.setFrom(groundSolver.getSource());
		kOffCon.setTo(kOff);

		//stove interval
		DistanceConstraint sStartAfterSEnd = new DistanceConstraint(new Bounds(0, APSPSolver.INF));
		sStartAfterSEnd.setFrom(sOn);
		sStartAfterSEnd.setTo(sOff);
		//stove release
		DistanceConstraint sOnCon = new DistanceConstraint(new Bounds(s1, s1), new Bounds(s1, APSPSolver.INF), new Bounds(0, s1), new Bounds(0, APSPSolver.INF));
		sOnCon.setFrom(groundSolver.getSource());
		sOnCon.setTo(sOn);
		//stove end
		DistanceConstraint sOffCon = new DistanceConstraint(new Bounds(0, APSPSolver.INF), new Bounds(s2, APSPSolver.INF), new Bounds(0, s2), new Bounds(s2, s2));
		sOffCon.setFrom(groundSolver.getSource());
		sOffCon.setTo(sOff);
		
		//cooking interval
		DistanceConstraint cStartAfterCEnd = new DistanceConstraint(new Bounds(0, APSPSolver.INF));
		cStartAfterCEnd.setFrom(cookingOn);
		cStartAfterCEnd.setTo(cookingOff);
		
		//rule cooking DURING kitchen
		DistanceConstraint cStartAfterK = new DistanceConstraint(new Bounds(0, APSPSolver.INF));
		cStartAfterK.setFrom(kOn);
		cStartAfterK.setTo(cookingOn);
		DistanceConstraint cEndBeforeK = new DistanceConstraint(new Bounds(0, APSPSolver.INF));
		cEndBeforeK.setFrom(cookingOff);
		cEndBeforeK.setTo(kOff);

		//rule cooking CONSTAINS stove
		DistanceConstraint cStartBeforeSOn = new DistanceConstraint(new Bounds(0, APSPSolver.INF));
		cStartBeforeSOn.setFrom(cookingOn);
		cStartBeforeSOn.setTo(sOn);
		DistanceConstraint sOffBeforeCEnd = new DistanceConstraint(new Bounds(0, APSPSolver.INF));
		sOffBeforeCEnd.setFrom(sOff);
		sOffBeforeCEnd.setTo(cookingOff);

		
		groundSolver.addConstraints(new DistanceConstraint[] {kStartAfterKEnd,kOnCon,kOffCon,sStartAfterSEnd,sOnCon,sOffCon,cStartAfterCEnd,cStartAfterK,cEndBeforeK,cStartBeforeSOn,sOffBeforeCEnd});
				
		VariableOrderingH varOH = new MostConstrainedFirstVarOH();
		
		ValueOrderingH valOH = new WidestIntervalFirstValOH();
		
		metaSolver.addMetaConstraint(new TCSPLabeling(varOH, valOH));
		
		System.out.println("Solved? " + metaSolver.backtrack());
		
		for (Variable tp : groundGroundSolver.getVariables()) {
			System.out.println("TP " + tp);
		}
		
		ConstraintNetwork.draw(groundGroundSolver.getConstraintNetwork(), "STP");
		metaSolver.draw();
		
	}

}
