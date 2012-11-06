package test.race;

import meta.symbolsAndTime.ReusableResource;
import meta.symbolsAndTime.Scheduler;
import meta.symbolsAndTime.SymbolicTimeline;
import multi.activity.Activity;
import multi.activity.ActivityNetworkSolver;
import multi.allenInterval.AllenIntervalConstraint;
import time.Bounds;
import framework.Constraint;
import framework.ConstraintNetwork;
import framework.ValueOrderingH;
import framework.VariableOrderingH;

public class TestResourceScheduling2ForRACE {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		final Scheduler metaSolver = new Scheduler(0,600,0);
		final ActivityNetworkSolver groundSolver = (ActivityNetworkSolver)metaSolver.getConstraintSolvers()[0];
		
		Activity putPlate = (Activity)groundSolver.createVariable("MoveObject");
		putPlate.setSymbolicDomain("400");
		Activity putGlass = (Activity)groundSolver.createVariable("MoveObject");
		putGlass.setSymbolicDomain("225");
		Activity putCutlery = (Activity)groundSolver.createVariable("MoveObject");
		putCutlery.setSymbolicDomain("50");
		
		//DURATIONS
		AllenIntervalConstraint dur1 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(35, 55));
		dur1.setFrom(putPlate);
		dur1.setTo(putPlate);
		AllenIntervalConstraint dur2 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(35, 55));
		dur2.setFrom(putGlass);
		dur2.setTo(putGlass);
		AllenIntervalConstraint dur3 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(35, 55));
		dur3.setFrom(putCutlery);
		dur3.setTo(putCutlery);


		
		//Add the constraints
		Constraint[] cons = new Constraint[]{dur1,dur2,dur3/*,eqOne,eqTwo,eqThree*/};
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
		
		ReusableResource tray = new ReusableResource(varOH, valOH, 400);

		tray.setUsage(putPlate,putGlass,putCutlery);

		metaSolver.addMetaConstraint(tray);
		
		metaSolver.backtrack();
		SymbolicTimeline tlMoveObject = new SymbolicTimeline(groundSolver,"MoveObject");
		tlMoveObject.draw();
		
		
		System.out.println(metaSolver.getDescription());
		metaSolver.draw();


		

	}

}
