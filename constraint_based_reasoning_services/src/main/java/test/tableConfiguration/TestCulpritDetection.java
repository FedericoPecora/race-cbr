package test.tableConfiguration;

import java.util.Vector;

import orbital.algorithm.Combinatorical;

import meta.MetaSpatialConstraint;
import meta.MetaSpatialConstraintSolver;
import multi.allenInterval.AllenIntervalConstraint;
import spatial.rectangleAlgebra.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra.BoundingBox;
import spatial.rectangleAlgebra.OntologicalSpatialProperty;
import spatial.rectangleAlgebra.QualitativeAllenIntervalConstraint;
import spatial.rectangleAlgebra.SpatialAssertionalRelation;
import spatial.rectangleAlgebra.SpatialRule;
import spatial.rectangleAlgebra.TwoDimensionsAllenConstraint;
import time.APSPSolver;
import time.Bounds;

public class TestCulpritDetection {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		int[] test = {1, 2};
//		Combinatorical c = Combinatorical.getPermutations(5, 2,  true);
//		System.out.println(c.count());
//		while (c.hasNext()) {
//			int[] combination = c.next();
//			//System.out.println("Doing " + Arrays.toString(combination));
//			boolean skip = false;
//			for (int i = 0; i < combination.length; i++) {
//				System.out.print(combination[i]);
//			}
//			System.out.println("hey");
//		}
		//Spatial Knowledge
		Vector<SpatialRule> srules = new Vector<SpatialRule>();

		SpatialRule r1 = new SpatialRule("fork", "table", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, APSPSolver.INF),new Bounds(5, APSPSolver.INF)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, APSPSolver.INF),new Bounds(5, APSPSolver.INF)))
				);
		srules.add(r1);

		SpatialRule r2 = new SpatialRule("knife", "table", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, APSPSolver.INF),new Bounds(5, APSPSolver.INF)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, APSPSolver.INF),new Bounds(5, APSPSolver.INF)))
				);
		srules.add(r2);
		
		SpatialRule r9 = new SpatialRule("dish", "table", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, APSPSolver.INF),new Bounds(5, APSPSolver.INF)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, APSPSolver.INF),new Bounds(5, APSPSolver.INF)))
				);
		srules.add(r9);

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
						new Bounds(6, 10)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10, 14))));
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
		OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
		tableOnto.setMovable(false);
		sa1.setOntologicalProp(tableOnto);
		saRelations.add(sa1);

		SpatialAssertionalRelation sa2 = new SpatialAssertionalRelation("knife1", "knife");
		sa2.setCoordinate(new BoundingBox(new Bounds(30, 30), new Bounds(35, 35), new Bounds(10, 10), new Bounds(20, 20)));
		saRelations.add(sa2);

		SpatialAssertionalRelation sa3 = new SpatialAssertionalRelation("fork1", "fork");
		sa3.setCoordinate(new BoundingBox(new Bounds(20, 20), new Bounds(25, 25), new Bounds(10, 10), new Bounds(20, 20)));				
		saRelations.add(sa3);

		SpatialAssertionalRelation sa4 = new SpatialAssertionalRelation("dish1", "dish");
		sa4.setCoordinate(new BoundingBox(new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		saRelations.add(sa4);

		
//		SpatialAssertionalRelation sa1 = new SpatialAssertionalRelation("table1", "table");
//		sa1.setCoordinate(new BoundingBox(new Bounds(0, 0), new Bounds(70, 70), new Bounds(0, 0), new Bounds(50, 50)));
//		OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
//		tableOnto.setMovable(false);
//		sa1.setOntologicalProp(tableOnto);
//		saRelations.add(sa1);
//
//		SpatialAssertionalRelation sa2 = new SpatialAssertionalRelation("knife1", "knife");
//		sa2.setCoordinate(new BoundingBox(new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE)));
//		saRelations.add(sa2);
//
//		SpatialAssertionalRelation sa3 = new SpatialAssertionalRelation("fork1", "fork");
//		sa3.setCoordinate(new BoundingBox(new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE)));				
//		saRelations.add(sa3);
//
//		SpatialAssertionalRelation sa4 = new SpatialAssertionalRelation("dish1", "dish");
//		sa4.setCoordinate(new BoundingBox(new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE), new Bounds(0, Long.MAX_VALUE)));
//		saRelations.add(sa4);

		
		//.......................................................................................................
		MetaSpatialConstraintSolver metaSolver = new MetaSpatialConstraintSolver(0);

		MetaSpatialConstraint objectsPosition = new MetaSpatialConstraint();
		objectsPosition.setSpatialRules(srules.toArray(new SpatialRule[srules.size()]));
		objectsPosition.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation[saRelations.size()]));

		metaSolver.addMetaConstraint(objectsPosition);
		metaSolver.backtrack();
		
		
//		System.out.println(objectsPosition.getGnuplotScript("dish1", "fork"));
//		System.out.println(objectsPosition.getRectangle("dish1"));
		objectsPosition.culpritDetector();


	}

}
