package test.tableConfiguration;

import java.util.Vector;

import meta.MetaSpatialConstraint;
import meta.MetaSpatialConstraintSolver;
import multi.allenInterval.AllenIntervalConstraint;
import spatial.rectangleAlgebra.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra.BoundingBox;
import spatial.rectangleAlgebra.QualitativeAllenIntervalConstraint;
import spatial.rectangleAlgebra.SpatialAssertionalRelation;
import spatial.rectangleAlgebra.SpatialRule;
import spatial.rectangleAlgebra.TwoDimensionsAllenConstraint;
import time.Bounds;

public class TestCulpritDetection {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Spatial Knowledge
				Vector<SpatialRule> srules = new Vector<SpatialRule>();
				
				
				SpatialRule r1 = new SpatialRule("fork", "table", 
						new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 21),new Bounds(5, Long.MAX_VALUE)),
								new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, Long.MAX_VALUE),new Bounds(5, Long.MAX_VALUE)))
				);
				srules.add(r1);

				SpatialRule r2 = new SpatialRule("knife", "table", 
						new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, Long.MAX_VALUE),new Bounds(5, Long.MAX_VALUE)),
								new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, Long.MAX_VALUE),new Bounds(5, Long.MAX_VALUE)))
				);
				srules.add(r2);
				
				SpatialRule r3 = new SpatialRule("fork", "dish", 
						new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Before,
								QualitativeAllenIntervalConstraint.Type.During)));
				srules.add(r3);
				
				SpatialRule r4 = new SpatialRule("knife", "dish", 
						new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.After,
								QualitativeAllenIntervalConstraint.Type.During)));
				srules.add(r4);
				
				SpatialRule r5 = new SpatialRule("dish", "dish", 
						new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
								new Bounds(10, 10)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(14, 14))));
				srules.add(r5);
				
				SpatialRule r6 = new SpatialRule("fork", "fork", 
						new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
								new Bounds(5, 5)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10, 10))));
				srules.add(r6);
				
				SpatialRule r7 = new SpatialRule("knife", "knife", 
						new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
								new Bounds(5, 5)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10, 10))));
				srules.add(r7);
				
				//Assertion
				Vector<SpatialAssertionalRelation> saRelations = new Vector<SpatialAssertionalRelation>();
				
				SpatialAssertionalRelation sa1 = new SpatialAssertionalRelation("table1", "table");
				sa1.setCoordinate(new BoundingBox(new Bounds(0, 0), new Bounds(70, 70), new Bounds(0, 0), new Bounds(50, 50)));
				saRelations.add(sa1);
				
				SpatialAssertionalRelation sa2 = new SpatialAssertionalRelation("knife1", "knife");
				sa2.setCoordinate(new BoundingBox(new Bounds(30, 30), new Bounds(35, 35), new Bounds(10, 10), new Bounds(20, 20)));
				saRelations.add(sa2);

				SpatialAssertionalRelation sa3 = new SpatialAssertionalRelation("fork1", "fork");
				sa3.setCoordinate(new BoundingBox(new Bounds(20, 20), new Bounds(25, 25), new Bounds(10, 10), new Bounds(20, 20)));				
				saRelations.add(sa3);
				
				SpatialAssertionalRelation sa4 = new SpatialAssertionalRelation("dish1", "dish");
				sa4.setCoordinate(new BoundingBox(new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE)));
				saRelations.add(sa4);
				
//				SpatialAssertionalRelation sa5 = new SpatialAssertionalRelation("napkin1", "napkin");
//				sa5.setCoordinate(new BoundingBox(new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE)));
//				saRelations.add(sa5);
				
				//.......................................................................................................
				MetaSpatialConstraintSolver metaSolver = new MetaSpatialConstraintSolver(0);
						
				MetaSpatialConstraint objectsPosition = new MetaSpatialConstraint();
				objectsPosition.setSpatialRules(srules.toArray(new SpatialRule[srules.size()]));
				objectsPosition.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation[saRelations.size()]));
				
				metaSolver.addMetaConstraint(objectsPosition);
				metaSolver.backtrack();
				
				System.out.println(objectsPosition.getRectangle("dish1"));
				objectsPosition.culpritDetector();


	}

}
