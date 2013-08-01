package test.race;

import java.util.Vector;


import org.metacsp.time.qualitative.QualitativeAllenIntervalConstraint;
import spatial.rectangleAlgebra_OLD.RectangleConstraint;
import spatial.rectangleAlgebra_OLD.RectangleConstraintSolver;
import spatial.rectangleAlgebra_OLD.RectangularRegion;
import spatial.rectangleAlgebra_OLD.TwoDimensionsAllenConstraint;
import org.metacsp.framework.ConstraintNetwork;
import org.metacsp.framework.Variable;

public class TestQualitativeSpatialReasoningForRACE {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//reference of frame wrt the robot is very important, in front of the robot is not enough
		
		
		//check the tractable version of Rectangle Alegbra
		 
		RectangleConstraintSolver solver = new RectangleConstraintSolver();
		Vector<RectangleConstraint> allConstraints = new Vector<RectangleConstraint>();
		
		Variable[] vars = solver.createVariables(5);
		
		RectangularRegion cup = (RectangularRegion)vars[0];
		cup.setName("cup");
		
		RectangularRegion knife = (RectangularRegion)vars[1];
		knife.setName("knife");
		
		RectangularRegion fork = (RectangularRegion)vars[2];
		fork.setName("fork");
		
		RectangularRegion plateMat = (RectangularRegion)vars[3];
		plateMat.setName("plateMat");
		
		RectangularRegion dish = (RectangularRegion)vars[4];
		dish.setName("dish");
		
		//A-box
//		RectangularRegion dish1 = (RectangularRegion)vars[5];
//		
//		RectangularRegion cup1 = (RectangularRegion)vars[6];
//
//		RectangularRegion knife1 = (RectangularRegion)vars[7];
//		
//		RectangularRegion fork1 = (RectangularRegion)vars[8];
//		
//		RectangularRegion plateMat1 = (RectangularRegion)vars[9];
		
			
		//Precise Model		
		RectangleConstraint dishToPlateMath = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.During, 
				QualitativeAllenIntervalConstraint.Type.During));
		dishToPlateMath.setFrom(dish);
		dishToPlateMath.setTo(plateMat);
		allConstraints.add(dishToPlateMath);
		
		RectangleConstraint cupToPlateMath = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.During, 
				QualitativeAllenIntervalConstraint.Type.After), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.During, 
						QualitativeAllenIntervalConstraint.Type.OverlappedBy));
		cupToPlateMath.setFrom(cup);
		cupToPlateMath.setTo(plateMat);
		allConstraints.add(cupToPlateMath);

		
		RectangleConstraint knifetoPlateMath = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.After,
				QualitativeAllenIntervalConstraint.Type.During), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.After,
						QualitativeAllenIntervalConstraint.Type.OverlappedBy), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.After,
								QualitativeAllenIntervalConstraint.Type.Overlaps));
		knifetoPlateMath.setFrom(knife);
		knifetoPlateMath.setTo(plateMat);
		allConstraints.add(knifetoPlateMath);

		RectangleConstraint forktoPlateMath = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Before,
				QualitativeAllenIntervalConstraint.Type.During), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Before,
						QualitativeAllenIntervalConstraint.Type.OverlappedBy), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Before,
								QualitativeAllenIntervalConstraint.Type.Overlaps));
		forktoPlateMath.setFrom(fork);
		forktoPlateMath.setTo(plateMat);
		allConstraints.add(forktoPlateMath);

		//Assertion of indivuals to the concepts
//		RectangleConstraint dishAssertion = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals,
//					QualitativeAllenIntervalConstraint.Type.Equals));
//		dishAssertion.setFrom(dish1);
//		dishAssertion.setTo(dish);
//		allConstraints.add(dishAssertion);
//			
//		RectangleConstraint cupAssertion = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals,
//					QualitativeAllenIntervalConstraint.Type.Equals));
//		cupAssertion.setFrom(cup1);
//		cupAssertion.setTo(cup);
//		allConstraints.add(cupAssertion);
//		
//		RectangleConstraint plateMatAssertion = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals,
//				QualitativeAllenIntervalConstraint.Type.Equals));
//		plateMatAssertion.setFrom(plateMat1);
//		plateMatAssertion.setTo(plateMat);
//		allConstraints.add(plateMatAssertion);
//		
//		RectangleConstraint knifeAssertion = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals,
//				QualitativeAllenIntervalConstraint.Type.Equals));
//		knifeAssertion.setFrom(knife1);
//		knifeAssertion.setTo(knife);
//		allConstraints.add(knifeAssertion);
//			
//		RectangleConstraint forkAssertion = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals,
//				QualitativeAllenIntervalConstraint.Type.Equals));
//		forkAssertion.setFrom(fork1);
//		forkAssertion.setTo(fork);
//		allConstraints.add(forkAssertion);
//		
//		//relations in A box
//		RectangleConstraint cupDishOverlapped = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.During,
//					QualitativeAllenIntervalConstraint.Type.OverlappedBy), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.OverlappedBy,
//							QualitativeAllenIntervalConstraint.Type.After));
//		cupDishOverlapped.setFrom(cup1);
//		cupDishOverlapped.setTo(dish1);
//		allConstraints.add(cupDishOverlapped);
			
		
		RectangleConstraint[] allConstraintsArray = allConstraints.toArray(new RectangleConstraint[allConstraints.size()]);
		if (!solver.addConstraints(allConstraintsArray)) { 
			System.out.println("Failed to add constraints!");
			System.exit(0);
		}
		
		ConstraintNetwork.draw(solver.getConstraintNetwork());
	}
}
