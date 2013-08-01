package test;

import org.metacsp.time.APSPSolver;
import org.metacsp.time.SimpleDistanceConstraint;
import org.metacsp.framework.ConstraintNetwork;
import org.metacsp.framework.Variable;


public class TestAPSPSolver {
	
	public static void main(String[] args) {
		//test		
		APSPSolver solver = new APSPSolver(0, 100);
		//solver.setOptions(framework.ConstraintSolver.OPTIONS.AUTO_PROPAGATE);
		Variable[] vars = solver.createVariables(3);
		Variable one = vars[0];
		Variable two = vars[1];
		
		ConstraintNetwork.draw(solver.getConstraintNetwork());
		
		SimpleDistanceConstraint con1 = new SimpleDistanceConstraint();
		con1.setFrom(solver.getSource());
		con1.setTo(one);
		con1.setMinimum(5);
		con1.setMaximum(10);
		
		solver.addConstraint(con1);
		
		SimpleDistanceConstraint con2 = new SimpleDistanceConstraint();
		con2.setFrom(one);
		con2.setTo(two);
		con2.setMinimum(1);
		con2.setMaximum(APSPSolver.INF);

		solver.addConstraint(con2);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SimpleDistanceConstraint con3 = new SimpleDistanceConstraint();
		con3.setFrom(solver.getSource());
		con3.setTo(one);
		con3.setMinimum(0);
		con3.setMaximum(7);
		//con3.setMinimum(7);
		//con3.setMaximum(APSPSolver.INF);
		
		solver.addConstraint(con3);

		
		//solver.addConstraints(new SimpleDistanceConstraint[] {con1,con2,con3});
//		System.out.println(solver.addConstraint(con1));
//		System.out.println(solver.addConstraint(con2));
//		System.out.println(solver.addConstraint(con3));
	
		//solver.draw();
		System.out.println(solver.getDescription());
		
		System.out.println(one.getDescription());

	}

}
