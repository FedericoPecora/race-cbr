package test.race;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.BoundingBox;
import org.metacsp.time.qualitative.QualitativeAllenIntervalConstraint;
import spatial.rectangleAlgebra_OLD.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra_OLD.AugmentedRectangleConstraintSolver;
import spatial.rectangleAlgebra_OLD.RectangularRegion;
import spatial.rectangleAlgebra_OLD.TwoDimensionsAllenConstraint;
import org.metacsp.time.Bounds;

public class TestQuantitaveSpatialReasoningForRACE {

	/**
	 * @param args
	 */
	
	private static final Logger logger = Logger.getLogger(TestQuantitaveSpatialReasoningForRACE.class.getPackage().getName());
	public static void main(String[] args) {
		
		logger.log(Level.FINEST, "Propagating (quad) with #TPs = " + "test");


		AugmentedRectangleConstraintSolver solver = new AugmentedRectangleConstraintSolver();
		Vector<AugmentedRectangleConstraint> allConstraints = new Vector<AugmentedRectangleConstraint>();
		
		//Variable[] vars = solver.createVariables(10);
		
		RectangularRegion knife = (RectangularRegion)solver.createVariable();		
		knife.setName("knife");
		
		RectangularRegion fork = (RectangularRegion)solver.createVariable();
		fork.setName("fork");
		
		RectangularRegion dish = (RectangularRegion)solver.createVariable();
		dish.setName("dish");
		
		RectangularRegion cup = (RectangularRegion)solver.createVariable();
		cup.setName("cup");
		
		RectangularRegion napkin = (RectangularRegion)solver.createVariable();
		napkin.setName("napkin");
		
		RectangularRegion napkin1 = (RectangularRegion)solver.createVariable();
		napkin1.setBoundingBox(new BoundingBox(new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE)));
		napkin1.setName("napkin1");
		
		RectangularRegion knife1 = (RectangularRegion)solver.createVariable();
		knife1.setBoundingBox(new BoundingBox(new Bounds(50, 50), new Bounds(55, 55), new Bounds(12, 12), new Bounds(26, 26)));
		knife1.setName("knife1");
		
		RectangularRegion fork1 = (RectangularRegion)solver.createVariable();
		fork1.setBoundingBox(new BoundingBox(new Bounds(5, 5), new Bounds(10, 10), new Bounds(14, 14), new Bounds(24, 24)));
		fork1.setName("fork1");
		
		RectangularRegion dish1 = (RectangularRegion)solver.createVariable();
		dish1.setBoundingBox(new BoundingBox(new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE)));
		dish1.setName("dish1");
		
		AugmentedRectangleConstraint sizeDish = new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
				new Bounds(10, 20)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(10, 20)));
		sizeDish.setFrom(dish1);
		sizeDish.setTo(dish1);
		allConstraints.add(sizeDish);
		
		RectangularRegion cup1 = (RectangularRegion)solver.createVariable();
		cup1.setBoundingBox(new BoundingBox(new Bounds(20, 20), new Bounds(28, 28), new Bounds(35, 35), new Bounds(42, 42)));
		cup1.setName("cup1");
		
		//..........................................................
		
		
		AugmentedRectangleConstraint napkinAssertion = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals, 
				QualitativeAllenIntervalConstraint.Type.Equals));
		napkinAssertion.setFrom(napkin1);
		napkinAssertion.setTo(napkin);
		allConstraints.add(napkinAssertion);
		
		
		//Spatial Knowledge		
		AugmentedRectangleConstraint cupToDish = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.During, 
				QualitativeAllenIntervalConstraint.Type.After), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.OverlappedBy, 
						QualitativeAllenIntervalConstraint.Type.After));
		cupToDish.setFrom(cup);
		cupToDish.setTo(dish);
		allConstraints.add(cupToDish);

		
//		AugmentedRectangleConstraint knifetoDish = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.After,
//				QualitativeAllenIntervalConstraint.Type.During));
//		knifetoDish.setFrom(knife);
//		knifetoDish.setTo(dish);
//		allConstraints.add(knifetoDish);
		
		AugmentedRectangleConstraint knifetoDish = new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(4, 10)),
				new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()));
		knifetoDish.setFrom(knife);
		knifetoDish.setTo(dish);
		allConstraints.add(knifetoDish);
		
		
		
		AugmentedRectangleConstraint forktoDish = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Before,
				QualitativeAllenIntervalConstraint.Type.During));
		forktoDish.setFrom(fork);
		forktoDish.setTo(dish);
		allConstraints.add(forktoDish);
		
		//What we observe


		AugmentedRectangleConstraint cupAssertion = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals, 
				QualitativeAllenIntervalConstraint.Type.Equals));
		cupAssertion.setFrom(cup);
		cupAssertion.setTo(cup1);
		allConstraints.add(cupAssertion);

		
		AugmentedRectangleConstraint knifAssertion = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals,
				QualitativeAllenIntervalConstraint.Type.Equals));
		knifAssertion.setFrom(knife);
		knifAssertion.setTo(knife1);
		allConstraints.add(knifAssertion);
		
		AugmentedRectangleConstraint forkAssertion = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals,
				QualitativeAllenIntervalConstraint.Type.Equals));
		forkAssertion.setFrom(fork);
		forkAssertion.setTo(fork1);
		allConstraints.add(forkAssertion);

		AugmentedRectangleConstraint dishAssertion = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals,
				QualitativeAllenIntervalConstraint.Type.Equals));
		dishAssertion.setFrom(dish);
		dishAssertion.setTo(dish1);
		allConstraints.add(dishAssertion);
		
		AugmentedRectangleConstraint[] allConstraintsArray = allConstraints.toArray(new AugmentedRectangleConstraint[allConstraints.size()]);
		if (!solver.addConstraints(allConstraintsArray)) { 
			System.out.println("Failed to add constraints!");
			System.exit(0);
		}
		
		//String[] st = new String[]{"fork", "knife", "dish", "cup"};
		String[] st = new String[]{"dish"};
		System.out.println(solver.drawAlmostCentreRectangle(100, st));
		//System.out.println(solver.drawMinMaxRectangle(30, st));


	}

}
