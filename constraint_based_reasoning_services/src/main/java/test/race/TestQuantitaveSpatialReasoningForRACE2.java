package test.race;

import java.util.Vector;

import org.metacsp.multi.spatial.rectangleAlgebra.BoundingBox;
import org.metacsp.time.qualitative.QualitativeAllenIntervalConstraint;
import spatial.rectangleAlgebra_OLD.RectangleConstraint;
import spatial.rectangleAlgebra_OLD.RectangleConstraintSolver;
import spatial.rectangleAlgebra_OLD.RectangularRegion;
import spatial.rectangleAlgebra_OLD.TwoDimensionsAllenConstraint;
import org.metacsp.time.Bounds;
import org.metacsp.framework.ConstraintNetwork;
import org.metacsp.framework.Variable;

public class TestQuantitaveSpatialReasoningForRACE2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		RectangleConstraintSolver solver = new RectangleConstraintSolver();
		Vector<RectangleConstraint> allConstraints = new Vector<RectangleConstraint>();
		
		Variable[] vars = solver.createVariables(8);
		

		
		RectangularRegion knife = (RectangularRegion)vars[0];		
		knife.setName("knife");
		
		RectangularRegion fork = (RectangularRegion)vars[1];
		fork.setName("fork");
		
		RectangularRegion dish = (RectangularRegion)vars[2];
		dish.setName("dish");
		
		RectangularRegion cup = (RectangularRegion)vars[3];
		cup.setName("cup");
		
		
		//Spatial Knowledge		
		RectangleConstraint cupToPlateMat = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Overlaps, 
				QualitativeAllenIntervalConstraint.Type.After), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Starts, 
						QualitativeAllenIntervalConstraint.Type.After), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.During, 
								QualitativeAllenIntervalConstraint.Type.After), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Finishes, 
										QualitativeAllenIntervalConstraint.Type.After), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.OverlappedBy, 
												QualitativeAllenIntervalConstraint.Type.After));
		cupToPlateMat.setFrom(cup);
		cupToPlateMat.setTo(dish);
		allConstraints.add(cupToPlateMat);

		
		RectangleConstraint knifetoPlateMat = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.After,
				QualitativeAllenIntervalConstraint.Type.During));
		knifetoPlateMat.setFrom(knife);
		knifetoPlateMat.setTo(dish);
		allConstraints.add(knifetoPlateMat);
		
		
		
		RectangleConstraint forktoPlateMath = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Before,
				QualitativeAllenIntervalConstraint.Type.During));
		forktoPlateMath.setFrom(fork);
		forktoPlateMath.setTo(dish);
		allConstraints.add(forktoPlateMath);
		
		//What we observe
		RectangularRegion knife1 = (RectangularRegion)vars[4];
		knife1.setBoundingBox(new BoundingBox(new Bounds(50, 50), new Bounds(55, 60), new Bounds(12, 12), new Bounds(26, 30)));
		knife1.setName("knife1");
		
		RectangularRegion fork1 = (RectangularRegion)vars[5];
		fork1.setBoundingBox(new BoundingBox(new Bounds(5, 7), new Bounds(10, 12), new Bounds(14, 16), new Bounds(24, 26)));
		fork1.setName("fork1");
		
		RectangularRegion dish1 = (RectangularRegion)vars[6];
		dish1.setName("dish1");
		
		RectangularRegion cup1 = (RectangularRegion)vars[7];
		cup1.setBoundingBox(new BoundingBox(new Bounds(20, 22), new Bounds(28, 33), new Bounds(35, 37), new Bounds(42, 44)));
		cup1.setName("cup1");

		RectangleConstraint cupAssertion = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals, 
				QualitativeAllenIntervalConstraint.Type.Equals));
		cupAssertion.setFrom(cup);
		cupAssertion.setTo(cup1);
		allConstraints.add(cupAssertion);

		
		RectangleConstraint knifAssertion = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals,
				QualitativeAllenIntervalConstraint.Type.Equals));
		knifAssertion.setFrom(knife);
		knifAssertion.setTo(knife1);
		allConstraints.add(knifAssertion);
		
		
		
		RectangleConstraint forkAssertion = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals,
				QualitativeAllenIntervalConstraint.Type.Equals));
		forkAssertion.setFrom(fork);
		forkAssertion.setTo(fork1);
		allConstraints.add(forkAssertion);

		RectangleConstraint dishAssertion = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals,
				QualitativeAllenIntervalConstraint.Type.Equals));
		dishAssertion.setFrom(dish);
		dishAssertion.setTo(dish1);
		allConstraints.add(dishAssertion);
		
		
		
		
		
		RectangleConstraint[] allConstraintsArray = allConstraints.toArray(new RectangleConstraint[allConstraints.size()]);
		if (!solver.addConstraints(allConstraintsArray)) { 
			System.out.println("Failed to add constraints!");
			System.exit(0);
		}
		
		ConstraintNetwork.draw(solver.getConstraintNetwork());


	}

}
