package test.tableConfiguration;

import java.util.Vector;

import org.apache.log4j.Logger;

import meta.MetaSpatialConstraint;
import meta.MetaSpatialConstraintSolver;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.BoundingBox;
import org.metacsp.time.qualitative.QualitativeAllenIntervalConstraint;
import spatial.rectangleAlgebra_OLD.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra_OLD.AugmentedRectangleConstraintSolver;
import spatial.rectangleAlgebra_OLD.RectangleConstraint;
import spatial.rectangleAlgebra_OLD.RectangleConstraintSolver;
import spatial.rectangleAlgebra_OLD.RectangularRegion;
import spatial.rectangleAlgebra_OLD.SpatialAssertionalRelation;
import spatial.rectangleAlgebra_OLD.SpatialRule;
import spatial.rectangleAlgebra_OLD.TwoDimensionsAllenConstraint;
import org.metacsp.time.APSPSolver;
import org.metacsp.time.Bounds;

public class TestCoffeeTableConfiguration {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//Spatial Knowledge
		Vector<SpatialRule> srules = new Vector<SpatialRule>();
		
		//knowledge we have: both cup and sugerPot should have space from edge at least 5 cm
		//cup should be south west of the sugerPot
		//cup has 10*10 size
		
		
		//sugerPot 
		SpatialRule r1 = new SpatialRule("sugerPot", "table", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, APSPSolver.INF),new Bounds(5, APSPSolver.INF)))
		);
		srules.add(r1);

		SpatialRule r2 = new SpatialRule("cup", "table", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, APSPSolver.INF),new Bounds(5, APSPSolver.INF)))
		);
		srules.add(r2);
		
		SpatialRule r3 = new SpatialRule("cup", "sugerPot", 
				new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Before,
						QualitativeAllenIntervalConstraint.Type.Before)));
		srules.add(r3);
		
		SpatialRule r4 = new SpatialRule("cup", "cup", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(10, 10)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10, 10))));
		srules.add(r4);


		//Assertion
		Vector<SpatialAssertionalRelation> saRelations = new Vector<SpatialAssertionalRelation>();
		
		SpatialAssertionalRelation sa1 = new SpatialAssertionalRelation("cup1", "cup");
		sa1.setCoordinate(new BoundingBox(new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		saRelations.add(sa1);
		
		SpatialAssertionalRelation sa2 = new SpatialAssertionalRelation("sugerPot1", "sugerPot");
		sa2.setCoordinate(new BoundingBox(new Bounds(40, 40), new Bounds(50, 50), new Bounds(20, 20), new Bounds(35, 35)));
		saRelations.add(sa2);
		
		SpatialAssertionalRelation sa3 = new SpatialAssertionalRelation("table1", "table");
		sa3.setCoordinate(new BoundingBox(new Bounds(0, 0), new Bounds(70, 70), new Bounds(0, 0), new Bounds(50, 50)));
		saRelations.add(sa3);
		
		//.......................................................................................................
		MetaSpatialConstraintSolver metaSolver = new MetaSpatialConstraintSolver(0);
		
		
		MetaSpatialConstraint objectsPosition = new MetaSpatialConstraint();
		objectsPosition.setSpatialRules(srules.toArray(new SpatialRule[srules.size()]));
		objectsPosition.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation[saRelations.size()]));
		
		metaSolver.addMetaConstraint(objectsPosition);
		metaSolver.backtrack();
		
		System.out.println(objectsPosition.getRectangle("cup1"));

	}

}
