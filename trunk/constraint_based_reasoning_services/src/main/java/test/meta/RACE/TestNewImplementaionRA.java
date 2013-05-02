package test.meta.RACE;

import java.util.Vector;
import java.util.logging.Level;

import orbital.algorithm.Combinatorical;

import meta.MetaSpatialConstraint;
import meta.MetaSpatialConstraint2;
import meta.MetaSpatialConstraintSolver;
import meta.MetaSpatialConstraintSolver2;
import multi.allenInterval.AllenIntervalConstraint;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraint2;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraintSolver2;
import sandbox.spatial.rectangleAlgebra2.SpatialAssertionalRelation2;
import sandbox.spatial.rectangleAlgebra2.SpatialRule2;
import sandbox.spatial.rectangleAlgebra2.UnaryRectangleConstraint2;
import sandbox.spatial.rectangleAlgebra2.UnaryRectangleConstraint2.Type;
import spatial.rectangleAlgebra.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra.BoundingBox;
import spatial.rectangleAlgebra.OntologicalSpatialProperty;
import spatial.rectangleAlgebra.QualitativeAllenIntervalConstraint;
import spatial.rectangleAlgebra.SpatialAssertionalRelation;
import spatial.rectangleAlgebra.SpatialRule;
import spatial.rectangleAlgebra.TwoDimensionsAllenConstraint;
import time.APSPSolver;
import time.Bounds;
import utility.logging.MetaCSPLogging;

public class TestNewImplementaionRA {

	/**
	 * @param args
	 */
	public static void main(String[] args) {


//		int[] test = {1, 2};
//		Combinatorical c = Combinatorical.getPermutations(3, 2,  true);
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

		Vector<SpatialRule2> srules = new Vector<SpatialRule2>();


		Bounds knife_size_x = new Bounds(4, 8);
		Bounds knife_size_y = new Bounds(18, 24);
		Bounds cup_size_x = new Bounds(4, 7);
		Bounds cup_size_y = new Bounds(4, 7);
		Bounds fork_size_x = new Bounds(4, 8);
		Bounds fork_size_y = new Bounds(18, 24);
		Bounds withinReach_y_lower = new Bounds(5, 20);
		Bounds withinReach_y_upper = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_x_lower = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_x_upper = new Bounds(5, APSPSolver.INF);


		SpatialRule2 r2 = new SpatialRule2("cup", "knife", 
				new RectangleConstraint2(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(15,20)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds() ))
				);
		srules.add(r2);

		SpatialRule2 r3 = new SpatialRule2("cup", "fork", 
				new RectangleConstraint2(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(15,20)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During , AllenIntervalConstraint.Type.During.getDefaultBounds()))

				);
		srules.add(r3);

		SpatialRule2 r4 = new SpatialRule2("fork", "table", 
				new RectangleConstraint2(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r4);

		SpatialRule2 r5 = new SpatialRule2("knife", "table", 
				new RectangleConstraint2(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r5);





		SpatialRule2 r7 = new SpatialRule2("knife", "knife", 
				new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.Size, knife_size_x, knife_size_y));
		srules.add(r7);

		SpatialRule2 r8 = new SpatialRule2("cup", "cup", 
				new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.Size, cup_size_x, cup_size_y));
		srules.add(r8);

		SpatialRule2 r9 = new SpatialRule2("fork", "fork", 
				new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.Size, fork_size_x, fork_size_y));
		srules.add(r9);


		//Assertion
		Vector<SpatialAssertionalRelation2> saRelations = new Vector<SpatialAssertionalRelation2>();

		SpatialAssertionalRelation2 sa1 = new SpatialAssertionalRelation2("table1", "table");
		sa1.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, 
				new Bounds(0, 0), new Bounds(100, 100), new Bounds(0, 0), new Bounds(99, 99)));
		OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
		tableOnto.setMovable(false);
		sa1.setOntologicalProp(tableOnto);
		saRelations.add(sa1);

		SpatialAssertionalRelation2 sa3 = new SpatialAssertionalRelation2("fork1", "fork");		
		sa3.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, 
				new Bounds(31, 31), new Bounds(37, 37), new Bounds(13, 13), new Bounds(32, 32)));
		saRelations.add(sa3);

//		SpatialAssertionalRelation2 sa2 = new SpatialAssertionalRelation2("knife1", "knife");
//		sa2.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, 
//				new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
//		saRelations.add(sa2);

				SpatialAssertionalRelation2 sa2 = new SpatialAssertionalRelation2("knife1", "knife");
				sa2.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, 
						new Bounds(45,45), new Bounds(51,51), new Bounds(10, 10), new Bounds(33, 33)));
				saRelations.add(sa2);



		SpatialAssertionalRelation2 sa4 = new SpatialAssertionalRelation2("cup1", "cup");
		sa4.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, 
				new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		saRelations.add(sa4);

		//.......................................................................................................

		//		MetaCSPLogging.setLevel(MetaSpatialConstraintSolver2.class, Level.FINEST);
		//		MetaCSPLogging.setLevel(RectangleConstraintSolver2.class, Level.FINEST);

		MetaSpatialConstraintSolver2 metaSolver = new MetaSpatialConstraintSolver2(0, 1000, 0);
		
		

		MetaSpatialConstraint2 objectsPosition = new MetaSpatialConstraint2();
		objectsPosition.setSpatialRules(srules.toArray(new SpatialRule2[srules.size()]));
		objectsPosition.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation2[saRelations.size()]));
		objectsPosition.testSpagetti();


//		metaSolver.addMetaConstraint(objectsPosition);
//		metaSolver.backtrack();
//
//		System.out.println(objectsPosition.getRectangle("cup1"));
//		System.out.println(objectsPosition.getRectangle("knife1"));
//		System.out.println(objectsPosition.getRectangle("fork1"));

	}

}
