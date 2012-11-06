package test.race;

import java.io.IOException;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import multi.allenInterval.AllenIntervalConstraint;
import spatial.rectangleAlgebra.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra.AugmentedRectangleConstraintSolver;
import spatial.rectangleAlgebra.BoundingBox;
import spatial.rectangleAlgebra.QualitativeAllenIntervalConstraint;
import spatial.rectangleAlgebra.RectangularRegion;
import spatial.rectangleAlgebra.TwoDimensionsAllenConstraint;
import time.Bounds;
import framework.Constraint;
import framework.Variable;

public class TestAugmentedRARACE {

	/**
	 * @param args
	 */
	
	private static final Logger logger = Logger.getLogger(TestAugmentedRARACE.class.getPackage().getName());
	public static void main(String[] args) {
		
		//list of all spatial entities(RA variables) 
		//list of All constraint
		//NominalSpatialRelation nominalSR1 = new NominalSpatialRelation("PlacingAreaNorth", "Table", "PlacingAreaNorthConstraint");		
		//list of ABOX relvent assertion
		
		//Initialize RA solver
		AugmentedRectangleConstraintSolver solver = new AugmentedRectangleConstraintSolver(); 
		Vector<Constraint> allConstraints = new Vector<Constraint>();
		
		
		Variable[] vars = solver.createVariables(7);
		
		RectangularRegion man = (RectangularRegion)vars[0];		
		man.setName("man");
		
		RectangularRegion mas = (RectangularRegion)vars[1];
		mas.setName("mas");
		
		RectangularRegion mae = (RectangularRegion)vars[2];
		mae.setName("mae");
		
		RectangularRegion maw = (RectangularRegion)vars[3];
		maw.setName("maw");
		
		RectangularRegion table = (RectangularRegion)vars[4];
		table.setName("table");
		

		
		//Spatial Knowledge		
		AugmentedRectangleConstraint northTable = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.During, 
				QualitativeAllenIntervalConstraint.Type.Finishes));
		northTable.setFrom(man);
		northTable.setTo(table);
		allConstraints.add(northTable);
		
		
		AugmentedRectangleConstraint southTable = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.During,
				QualitativeAllenIntervalConstraint.Type.Starts));
		southTable.setFrom(mas);
		southTable.setTo(table);
		allConstraints.add(southTable);
		
		
		AugmentedRectangleConstraint eastTable = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Finishes,
				QualitativeAllenIntervalConstraint.Type.Equals));
		eastTable.setFrom(mae);
		eastTable.setTo(table);
		allConstraints.add(eastTable);
		
		
		AugmentedRectangleConstraint westTable = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Starts,
				QualitativeAllenIntervalConstraint.Type.Equals));
		westTable.setFrom(maw);
		westTable.setTo(table);
		allConstraints.add(westTable);
		
		AugmentedRectangleConstraint southToNorth = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals,
				QualitativeAllenIntervalConstraint.Type.Meets));
		southToNorth.setFrom(mas);
		southToNorth.setTo(man);
		allConstraints.add(southToNorth);
		
		AugmentedRectangleConstraint southToEast = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Meets,
				QualitativeAllenIntervalConstraint.Type.Starts));
		southToEast.setFrom(mas);
		southToEast.setTo(mae);
		allConstraints.add(southToEast);
		
		AugmentedRectangleConstraint southToWest = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.MetBy,
				QualitativeAllenIntervalConstraint.Type.Starts));
		southToWest.setFrom(mas);
		southToWest.setTo(maw);
		allConstraints.add(southToWest);
		
		//What we have in the Abox
		RectangularRegion table1 = (RectangularRegion)vars[5];
		table1.setBoundingBox(new BoundingBox(new Bounds(0, 0), new Bounds(20, 20), new Bounds(0, 0), new Bounds(10, 10)));
		table1.setName("table1");
		
		RectangularRegion mas1 = (RectangularRegion)vars[6];
		mas1.setName("mas1");
		
		AugmentedRectangleConstraint durationmas1 = new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
				new Bounds(5, 10)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(4, 6)));
		durationmas1.setFrom(mas1);
		durationmas1.setTo(mas1);
		allConstraints.add(durationmas1);
		
//		AugmentedRectangleConstraint durationEast = new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
//				new Bounds(1, 3)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
//						new Bounds(10, 10)));
//		durationEast.setFrom(mae);
//		durationEast.setTo(mae);
//		allConstraints.add(durationEast);
//		
//		AugmentedRectangleConstraint durationWest = new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
//				new Bounds(1, 3)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
//						new Bounds(10, 10)));
//		durationWest.setFrom(maw);
//		durationWest.setTo(maw);
//		allConstraints.add(durationWest);
		
		
		//Assertion
		AugmentedRectangleConstraint masAssertion = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals, 
				QualitativeAllenIntervalConstraint.Type.Equals));
		masAssertion.setFrom(mas1);
		masAssertion.setTo(mas);
		allConstraints.add(masAssertion);

		
		AugmentedRectangleConstraint tableAssertion = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals,
				QualitativeAllenIntervalConstraint.Type.Equals));
		tableAssertion.setFrom(table1);
		tableAssertion.setTo(table);
		allConstraints.add(tableAssertion);
		
		
		
		Constraint[] allConstraintsArray = allConstraints.toArray(new Constraint[allConstraints.size()]);
		if (!solver.addConstraints(allConstraintsArray)) { 
			System.out.println("Failed to add constraints!");
			System.exit(0);
		}
		
		String[] st = new String[]{"mae", "maw", "man", "mas", "table"};
		//String[] st = new String[]{"mas", "table"};
		System.out.println(solver.drawAlmostCentreRectangle(30, st));
		//System.out.println(solver.drawMinMaxRectangle(30, st));
		
//		System.out.println(solver.extractBoundingBoxesFromSTPs("mas1").getMinRectabgle());
//		System.out.println(solver.extractBoundingBoxesFromSTPs("mas1").getMaxRectabgle());
//		System.out.println("before: " + solver.getInternalSolver()[0].getVariable(6));
//		AllenIntervalConstraint midPonit1 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, 				 
//				new Bounds(5,5));
//		midPonit1.setFrom(solver.getInternalSolver()[0].getVariable(6));
//		midPonit1.setTo(solver.getInternalSolver()[0].getVariable(6));
//		solver.addInternalConstraint(midPonit1);		
//		System.out.println("after: " + solver.getInternalSolver()[0].getVariable(6));
//		System.out.println(solver.extractBoundingBoxesFromSTPs("mas1").getMinRectabgle());
//		System.out.println(solver.extractBoundingBoxesFromSTPs("mas1").getMaxRectabgle());
//		System.out.println(solver.extractBoundingBoxesFromSTPs("mas1").getACentrePointSolution().x);
//		System.out.println(solver.extractBoundingBoxesFromSTPs("mas1").getACentrePointSolution().y);	
		
		
		//test logger
		try {
		    // Create a file handler that write log record to a file called my.log
		    FileHandler handler = new FileHandler("augmentedRATest.log");
		    logger.addHandler(handler);
		    logger.log(Level.SEVERE, solver.drawMinMaxRectangle(30, st));
		} catch (IOException e) {
		}
		
	}

}
