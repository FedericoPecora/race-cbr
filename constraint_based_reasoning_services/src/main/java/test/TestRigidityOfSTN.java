package test;

import java.util.logging.Level;

import meta.MetaSpatialConstraint;

import org.metacsp.time.APSPSolver;
import org.metacsp.time.SimpleDistanceConstraint;
import org.metacsp.utility.logging.MetaCSPLogging;
import org.metacsp.framework.Constraint;
import org.metacsp.framework.ConstraintNetwork;
import org.metacsp.framework.Variable;

public class TestRigidityOfSTN {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int a = 11;
		System.out.println(a/2);
		

		APSPSolver solver = new APSPSolver(0, 100);
		
		MetaCSPLogging.setLevel(solver.getClass(), Level.FINE);
		
		//solver.setOptions(framework.ConstraintSolver.OPTIONS.AUTO_PROPAGATE);
		Variable[] vars = solver.createVariables(1);
		Variable one = vars[0];
		//Variable two = vars[1];
//		Variable three = vars[2];
		
		ConstraintNetwork.draw(solver.getConstraintNetwork());
		
		SimpleDistanceConstraint con1 = new SimpleDistanceConstraint();
		con1.setFrom(one);
		con1.setTo(one);
		con1.setMinimum(5);
		con1.setMaximum(5);
		
//		SimpleDistanceConstraint con2 = new SimpleDistanceConstraint();
//		con2.setFrom(one);
//		con2.setTo(two);
//		con2.setMinimum(7);
//		con2.setMaximum(9);
//		
//		SimpleDistanceConstraint con3 = new SimpleDistanceConstraint();
//		con3.setFrom(solver.getVariable(0));
//		con3.setTo(three);
//		con3.setMinimum(40);
//		con3.setMaximum(100);
		
		
		
//		SimpleDistanceConstraint con4 = new SimpleDistanceConstraint();
//		con4.setFrom(two);
//		con4.setTo(three);
//		con4.setMinimum(56);
//		con4.setMaximum(100);
//		
//		solver.addConstraint(con4);
//		
//		SimpleDistanceConstraint con5 = new SimpleDistanceConstraint();
//		con5.setFrom(one);
//		con5.setTo(three);
//		con5.setMinimum(70);
//		con5.setMaximum(100);	

		SimpleDistanceConstraint[] cons = {con1};
		solver.addConstraint(con1); 
//		solver.addConstraint(con2); 
//		solver.addConstraint(con3); 
//		solver.addConstraint(con4); 
//		solver.addConstraint(con5);
		
		System.out.println("RMS rigity" + solver.getRMSRigidity());
		
		//solver.draw();


	}

}
