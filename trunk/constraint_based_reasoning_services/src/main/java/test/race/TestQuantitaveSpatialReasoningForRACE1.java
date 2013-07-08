package test.race;

import java.util.Vector;

import multi.spatial.rectangleAlgebra.BoundingBox;
import time.qualitative.QualitativeAllenIntervalConstraint;
import spatial.rectangleAlgebra_OLD.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra_OLD.AugmentedRectangleConstraintSolver;
import spatial.rectangleAlgebra_OLD.RectangleConstraint;
import spatial.rectangleAlgebra_OLD.RectangleConstraintSolver;
import spatial.rectangleAlgebra_OLD.RectangularRegion;
import spatial.rectangleAlgebra_OLD.TwoDimensionsAllenConstraint;
import framework.ConstraintNetwork;

public class TestQuantitaveSpatialReasoningForRACE1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		

		AugmentedRectangleConstraintSolver solver = new AugmentedRectangleConstraintSolver();
		Vector<AugmentedRectangleConstraint> allConstraints = new Vector<AugmentedRectangleConstraint>();
		
		//Variable[] vars = solver.createVariables(4);
		
		
		
		//RectangularRegion knife = (RectangularRegion)vars[0];
		RectangularRegion knife = (RectangularRegion)solver.createVariable();
		//knife.setBoundingBox(new BoundingBox(new Bounds(8, 8), new Bounds(22, 22), new Bounds(4, 4), new Bounds(24, 24)));
		knife.setName("knife");
		
		//RectangularRegion fork = (RectangularRegion)vars[1];
		RectangularRegion fork = (RectangularRegion)solver.createVariable();
		//fork.setBoundingBox(new BoundingBox(new Bounds(5, 5), new Bounds(10, 10), new Bounds(5, 5), new Bounds(25, 25)));
		fork.setName("fork");
		
		RectangularRegion dish = (RectangularRegion)solver.createVariable();
		//RectangularRegion dish = (RectangularRegion)vars[2];
		dish.setName("dish");
		
		//RectangularRegion cup = (RectangularRegion)vars[3];
		RectangularRegion cup = (RectangularRegion)solver.createVariable();
		//knife.setBoundingBox(new BoundingBox(new Bounds(12, 12), new Bounds(18, 18), new Bounds(25, 25), new Bounds(35, 35)));
		cup.setName("cup");
		
		//Precise Model		
		AugmentedRectangleConstraint cupToPlateDish = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.During, 
				QualitativeAllenIntervalConstraint.Type.After));
		cupToPlateDish.setFrom(cup);
		cupToPlateDish.setTo(dish);
		allConstraints.add(cupToPlateDish);

		
		AugmentedRectangleConstraint knifetoPlate = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.After,
				QualitativeAllenIntervalConstraint.Type.During));
		knifetoPlate.setFrom(knife);
		knifetoPlate.setTo(dish);
		allConstraints.add(knifetoPlate);

		
		AugmentedRectangleConstraint forktoPlateMat = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Before,
				QualitativeAllenIntervalConstraint.Type.During));
		forktoPlateMat.setFrom(fork);
		forktoPlateMat.setTo(dish);
		allConstraints.add(forktoPlateMat);
		
		
		AugmentedRectangleConstraint[] allConstraintsArray = allConstraints.toArray(new AugmentedRectangleConstraint[allConstraints.size()]);
		if (!solver.addConstraints(allConstraintsArray)) { 
			System.out.println("Failed to add constraints!");
			System.exit(0);
		}
		
		ConstraintNetwork.draw(solver.getConstraintNetwork());

	}

}
