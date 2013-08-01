package test;

import org.metacsp.time.APSPSolver;
import org.metacsp.time.SimpleDistanceConstraint;
import org.metacsp.framework.ConstraintNetwork;
import org.metacsp.framework.Variable;


public class IranTestAPSPSolver {
	
	public static void main(String[] args) {
		
		APSPSolver solver = new APSPSolver(0, 100);
		//solver.setOptions(framework.ConstraintSolver.OPTIONS.AUTO_PROPAGATE);
		Variable[] vars = solver.createVariables(3);
		Variable one = vars[0];
		Variable two = vars[1];
//		Variable three = vars[2];
		
		//One < two
		SimpleDistanceConstraint prec = new SimpleDistanceConstraint();
		prec.setFrom(one);
		prec.setTo(two);
		prec.setMinimum(0);
		prec.setMaximum(APSPSolver.INF);

		//Initial bounds on one
		SimpleDistanceConstraint con1 = new SimpleDistanceConstraint();
		con1.setFrom(solver.getVariable(0));
		con1.setTo(one);
		con1.setMinimum(5);
		con1.setMaximum(7);

		//Initial bounds on two
		SimpleDistanceConstraint con2 = new SimpleDistanceConstraint();
		con2.setFrom(solver.getVariable(0));
		con2.setTo(two);
		con2.setMinimum(6);
		con2.setMaximum(10);
		
		//Auxiliary constraint related to one and two
		SimpleDistanceConstraint aux = new SimpleDistanceConstraint();
		aux.setFrom(one);
		aux.setTo(two);
		aux.setMinimum(0);
		aux.setMaximum(4);

		//Let's choose the LB of one and UB of two
//		SimpleDistanceConstraint con1a = new SimpleDistanceConstraint();
//		con1a.setFrom(solver.getVariable(0));
//		con1a.setTo(one);
//		con1a.setMinimum(5);
//		con1a.setMaximum(5);
//
//		SimpleDistanceConstraint con2a = new SimpleDistanceConstraint();
//		con2a.setFrom(solver.getVariable(0));
//		con2a.setTo(two);
//		con2a.setMinimum(10);
//		con2a.setMaximum(10);

		if (!solver.addConstraints(new SimpleDistanceConstraint[] {prec,con1,con2})) System.out.println("Could not add constraints (1)");
		if (!solver.addConstraints(new SimpleDistanceConstraint[] {aux})) System.out.println("Could not add constraints (2)");
//		if (!solver.addConstraints(new SimpleDistanceConstraint[] {con1a,con2a})) System.out.println("Could not add constraints (3)");
		
		ConstraintNetwork.draw(solver.getConstraintNetwork());
		
		/*
		 * We have (as result of propagation in STPx and STPy) updated bounds
		 * [[lx1,ux1] [lx2,ux2]] and [[ly1,uy1] [ly2,uy2]].  We can choose either
		 * of the two following bounding boxes: ((lx1,ly1) (lx2,ly2)) or ((lx1,ly1) (lx2,ly2))
		 * We also know that every point (x,y) along the segment connecting the centers of the two
		 * bounding boxes is the center of a bounding box that is the solution of STPx U STPy.
		 * We are also guaranteed that this bounding box is big enough to contain the dish, because
		 * we have modeled as minimum time lags the minimum size of the object to place.
		 */
		
	
		//........................................................................................................................
//		APSPSolver solver = new APSPSolver(0, 100);
//		//solver.setOptions(framework.ConstraintSolver.OPTIONS.AUTO_PROPAGATE);
//		Variable[] vars = solver.createVariables(2);
//		Variable one = vars[0];
//		Variable two = vars[1];
////		Variable three = vars[2];
//		
//		//One < two
//		SimpleDistanceConstraint prec = new SimpleDistanceConstraint();
//		prec.setFrom(one);
//		prec.setTo(two);
//		prec.setMinimum(10);
//		prec.setMaximum(20);
//
//		//Initial bounds on one
//		SimpleDistanceConstraint con1 = new SimpleDistanceConstraint();
//		con1.setFrom(solver.getVariable(0));
//		con1.setTo(one);
//		con1.setMinimum(10);
//		con1.setMaximum(10);
//
//		//Initial bounds on two
//		SimpleDistanceConstraint con2 = new SimpleDistanceConstraint();
//		con2.setFrom(solver.getVariable(0));
//		con2.setTo(two);
//		con2.setMinimum(20);
//		con2.setMaximum(20);
//		
//		//Auxiliary constraint related to one and two
//		
//
//		if (!solver.addConstraints(new SimpleDistanceConstraint[] {prec,con1,con2})) System.out.println("Could not add constraints (1)");
////		if (!solver.addConstraints(new SimpleDistanceConstraint[] {aux})) System.out.println("Could not add constraints (2)");
////		if (!solver.addConstraints(new SimpleDistanceConstraint[] {con1a,con2a})) System.out.println("Could not add constraints (3)");
//		
//		ConstraintNetwork.draw(solver.getConstraintNetwork());
		
		/*
		 * We have (as result of propagation in STPx and STPy) updated bounds
		 * [[lx1,ux1] [lx2,ux2]] and [[ly1,uy1] [ly2,uy2]].  We can choose either
		 * of the two following bounding boxes: ((lx1,ly1) (lx2,ly2)) or ((lx1,ly1) (lx2,ly2))
		 * We also know that every point (x,y) along the segment connecting the centers of the two
		 * bounding boxes is the center of a bounding box that is the solution of STPx U STPy.
		 * We are also guaranteed that this bounding box is big enough to contain the dish, because
		 * we have modeled as minimum time lags the minimum size of the object to place.
		 */
		
		System.out.println(solver.getRMSRigidity());
	}

}
