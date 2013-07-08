package test.race;

import java.util.Vector;

import time.qualitative.QualitativeAllenIntervalConstraint;
import spatial.rectangleAlgebra_OLD.RectangleConstraint;
import spatial.rectangleAlgebra_OLD.RectangleConstraintSolver;
import spatial.rectangleAlgebra_OLD.TwoDimensionsAllenConstraint;
import framework.Variable;

public class TestConvexityAndPreconvexity {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		RectangleConstraintSolver solver = new RectangleConstraintSolver();
		Vector<RectangleConstraint> allConstraints = new Vector<RectangleConstraint>();
		
		Variable[] vars = solver.createVariables(8);
		
		
//		QualitativeAllenIntervalConstraint test = new QualitativeAllenIntervalConstraint(QualitativeAllenIntervalConstraint.Type.FinishedBy, 
//				QualitativeAllenIntervalConstraint.Type.Finishes, 
//				QualitativeAllenIntervalConstraint.Type.Equals);
//		
//		int dimention = QualitativeAllenIntervalConstraint.getDimension(QualitativeAllenIntervalConstraint.Type.FinishedBy, 
//				QualitativeAllenIntervalConstraint.Type.Finishes, 
//				QualitativeAllenIntervalConstraint.Type.Equals);
//		
//		QualitativeAllenIntervalConstraint.Type[] convex = test.getAllenConvexClosure(QualitativeAllenIntervalConstraint.Type.FinishedBy, 
//				QualitativeAllenIntervalConstraint.Type.Finishes
//				); 
//		
//		System.out.println(test.isPreconvex(QualitativeAllenIntervalConstraint.Type.During, 
//				QualitativeAllenIntervalConstraint.Type.FinishedBy));
		
		RectangleConstraint test1 = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.During, 
				QualitativeAllenIntervalConstraint.Type.After) ,new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.FinishedBy, 
						QualitativeAllenIntervalConstraint.Type.Overlaps), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals, 
								QualitativeAllenIntervalConstraint.Type.Starts)
				);
		
//		int dimention = RectangleConstraint.getDimension(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.During, 
//				QualitativeAllenIntervalConstraint.Type.After) ,new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.FinishedBy, 
//						QualitativeAllenIntervalConstraint.Type.Overlaps), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals, 
//								QualitativeAllenIntervalConstraint.Type.Starts)
//		);
		//weak preconvex
		TwoDimensionsAllenConstraint[] tda = {new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.During, 
				QualitativeAllenIntervalConstraint.Type.During), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals, 
						QualitativeAllenIntervalConstraint.Type.Starts), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Starts, 
								QualitativeAllenIntervalConstraint.Type.Equals)};
		//convex
		TwoDimensionsAllenConstraint[] tda1 = {new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Starts, 
				QualitativeAllenIntervalConstraint.Type.Starts), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals, 
						QualitativeAllenIntervalConstraint.Type.Starts), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Starts, 
								QualitativeAllenIntervalConstraint.Type.Equals), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals, 
										QualitativeAllenIntervalConstraint.Type.Equals)};
		//strong preconvex
		TwoDimensionsAllenConstraint[] tda2 = {new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.During, 
				QualitativeAllenIntervalConstraint.Type.During), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Starts, 
						QualitativeAllenIntervalConstraint.Type.Starts), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Starts, 
								QualitativeAllenIntervalConstraint.Type.Equals)};
		
		TwoDimensionsAllenConstraint[] tda3 = {new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Before, 
				QualitativeAllenIntervalConstraint.Type.Before), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.After, 
						QualitativeAllenIntervalConstraint.Type.After)};
		
//		System.out.println(RectangleConstraint.isConvexRelation(tda));
//		
//		System.out.println(RectangleConstraint.isConvexRelation(tda1));
//		
//		System.out.println(RectangleConstraint.isWeakPreconvexRelation(tda));
//		
//		System.out.println(RectangleConstraint.isWeakPreconvexRelation(tda1));
//		
//		System.out.println(RectangleConstraint.isWeakPreconvexRelation(tda2));
//		
//		System.out.println(RectangleConstraint.isWeakPreconvexRelation(tda3));
		
		
		
		TwoDimensionsAllenConstraint[] wellSetTable = {new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Overlaps, 
								QualitativeAllenIntervalConstraint.Type.After),new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Meets, 
						QualitativeAllenIntervalConstraint.Type.After), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Starts, 
								QualitativeAllenIntervalConstraint.Type.After)};
		System.out.println(RectangleConstraint.isConvexRelation(wellSetTable));
		System.out.println(RectangleConstraint.isWeakPreconvexRelation(wellSetTable));

	}

}
