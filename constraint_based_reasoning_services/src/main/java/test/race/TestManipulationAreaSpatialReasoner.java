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

public class TestManipulationAreaSpatialReasoner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		RectangleConstraintSolver solver = new RectangleConstraintSolver();
		Vector<RectangleConstraint> allConstraints = new Vector<RectangleConstraint>();
		
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
		RectangleConstraint northTable = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.During, 
				QualitativeAllenIntervalConstraint.Type.Finishes));
		northTable.setFrom(man);
		northTable.setTo(table);
		allConstraints.add(northTable);

		
		RectangleConstraint southTable = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.During,
				QualitativeAllenIntervalConstraint.Type.Starts));
		southTable.setFrom(mas);
		southTable.setTo(table);
		allConstraints.add(southTable);
		
		
		RectangleConstraint eastTable = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Finishes,
				QualitativeAllenIntervalConstraint.Type.Equals));
		eastTable.setFrom(mae);
		eastTable.setTo(table);
		allConstraints.add(eastTable);
		
		RectangleConstraint westTable = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Starts,
				QualitativeAllenIntervalConstraint.Type.Equals));
		westTable.setFrom(maw);
		westTable.setTo(table);
		allConstraints.add(westTable);
		
		RectangleConstraint southToNorth = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals,
				QualitativeAllenIntervalConstraint.Type.Meets));
		southToNorth.setFrom(mas);
		southToNorth.setTo(man);
		allConstraints.add(southToNorth);
		
		RectangleConstraint southToEast = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Meets,
				QualitativeAllenIntervalConstraint.Type.Starts));
		southToEast.setFrom(mas);
		southToEast.setTo(mae);
		allConstraints.add(southToEast);
		
		RectangleConstraint southToWest = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.MetBy,
				QualitativeAllenIntervalConstraint.Type.Starts));
		southToWest.setFrom(mas);
		southToWest.setTo(maw);
		allConstraints.add(southToWest);
		
		//What we observe
		RectangularRegion table1 = (RectangularRegion)vars[5];
		table1.setBoundingBox(new BoundingBox(new Bounds(0, 0), new Bounds(20, 20), new Bounds(0, 0), new Bounds(10, 10)));
		table1.setName("table1");
		
		RectangularRegion mas1 = (RectangularRegion)vars[6];
		mas1.setName("mas1");
		
		
		//Assertion
		RectangleConstraint masAssertion = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals, 
				QualitativeAllenIntervalConstraint.Type.Equals));
		masAssertion.setFrom(mas1);
		masAssertion.setTo(mas);
		allConstraints.add(masAssertion);

		
		RectangleConstraint tableAssertion = new RectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals,
				QualitativeAllenIntervalConstraint.Type.Equals));
		tableAssertion.setFrom(table1);
		tableAssertion.setTo(table);
		allConstraints.add(tableAssertion);
		
		
		
		RectangleConstraint[] allConstraintsArray = allConstraints.toArray(new RectangleConstraint[allConstraints.size()]);
		if (!solver.addConstraints(allConstraintsArray)) { 
			System.out.println("Failed to add constraints!");
			System.exit(0);
		}
		
		ConstraintNetwork.draw(solver.getConstraintNetwork());

	}

}
