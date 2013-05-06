package test.tableConfiguration;

import java.util.Vector;
import java.util.logging.Level;

import orbital.algorithm.Combinatorical;

import meta.MetaSpatialConstraint;
import meta.MetaSpatialConstraintSolver;
import meta.MetaSpatialConstraintSolver2;
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
import utility.logging.MetaCSPLogging;

public class TestCulpritDetection {

	/**
	 * @param args
	 */
	public static void main(String[] args) {


//		//Spatial Knowledge
//
//		Vector<SpatialRule> srules = new Vector<SpatialRule>();
//
//		SpatialRule r1 = new SpatialRule("fork", "table", 
//				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, APSPSolver.INF),new Bounds(5, APSPSolver.INF)),
//						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, APSPSolver.INF),new Bounds(5, APSPSolver.INF)))
//				);
//		srules.add(r1);
//
//		SpatialRule r2 = new SpatialRule("knife", "table", 
//				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, APSPSolver.INF),new Bounds(5, APSPSolver.INF)),
//						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, APSPSolver.INF),new Bounds(5, APSPSolver.INF)))
//				);
//		srules.add(r2);
//
//		SpatialRule r9 = new SpatialRule("dish", "table", 
//				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, APSPSolver.INF),new Bounds(5, APSPSolver.INF)),
//						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, APSPSolver.INF),new Bounds(5, APSPSolver.INF)))
//				);
//		srules.add(r9);
//
//		SpatialRule r3 = new SpatialRule("fork", "dish", 
//				new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Before,
//						QualitativeAllenIntervalConstraint.Type.During)));
//		srules.add(r3);
//
//		SpatialRule r4 = new SpatialRule("knife", "dish", 
//				new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.After,
//						QualitativeAllenIntervalConstraint.Type.During)));
//		srules.add(r4);
//
//		SpatialRule r5 = new SpatialRule("dish", "dish", 
//				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
//						new Bounds(6, 10)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10, 14))));
//		srules.add(r5);
//
//		SpatialRule r6 = new SpatialRule("fork", "fork", 
//				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
//						new Bounds(5, 5)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10, 10))));
//		srules.add(r6);
//
//		SpatialRule r7 = new SpatialRule("knife", "knife", 
//				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
//						new Bounds(5, 5)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10, 10))));
//		srules.add(r7);
//
//
//
//		//Assertion
//		Vector<SpatialAssertionalRelation> saRelations = new Vector<SpatialAssertionalRelation>();
//
//		SpatialAssertionalRelation sa1 = new SpatialAssertionalRelation("table1", "table");
//		sa1.setCoordinate(new BoundingBox(new Bounds(0, 0), new Bounds(70, 70), new Bounds(0, 0), new Bounds(50, 50)));
//		OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
//		tableOnto.setMovable(false);
//		sa1.setOntologicalProp(tableOnto);
//		saRelations.add(sa1);
//
//		SpatialAssertionalRelation sa3 = new SpatialAssertionalRelation("fork1", "fork");
////		sa3.setCoordinate(new BoundingBox(new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
//		sa3.setCoordinate(new BoundingBox(new Bounds(20, 20), new Bounds(25, 25), new Bounds(10, 10), new Bounds(20, 20)));				
//		saRelations.add(sa3);
//		
//		SpatialAssertionalRelation sa2 = new SpatialAssertionalRelation("knife1", "knife");
////		sa2.setCoordinate(new BoundingBox(new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
//		sa2.setCoordinate(new BoundingBox(new Bounds(30, 30), new Bounds(35, 35), new Bounds(10, 10), new Bounds(20, 20)));
//		saRelations.add(sa2);
//
//
//		SpatialAssertionalRelation sa4 = new SpatialAssertionalRelation("dish1", "dish");
//		sa4.setCoordinate(new BoundingBox(new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
//		saRelations.add(sa4);
//
//
//
//
//
//		//.......................................................................................................
//		MetaSpatialConstraintSolver metaSolver = new MetaSpatialConstraintSolver(0);
//
//		MetaSpatialConstraint objectsPosition = new MetaSpatialConstraint();
//		objectsPosition.setSpatialRules(srules.toArray(new SpatialRule[srules.size()]));
//		objectsPosition.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation[saRelations.size()]));
//
//		metaSolver.addMetaConstraint(objectsPosition);
//		metaSolver.backtrack();
//		
//		if(objectsPosition.isConsistent())
//			System.out.println("consistenct");
//		else System.out.println("inconsistenct");
//			
//
//		//		System.out.println(objectsPosition.getGnuplotScript("dish1", "fork"));
//
//		objectsPosition.culpritDetector();
//		System.out.println("culprits: " + objectsPosition.getCulprits()[0].getName());
		
		////////////////////////////////////////////////////////////////////////////////////
		
		

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

		Vector<SpatialRule> srules = new Vector<SpatialRule>();

		SpatialRule r2 = new SpatialRule("cup", "knife", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(15,20)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()))
				);
		srules.add(r2);

		SpatialRule r3 = new SpatialRule("cup", "fork", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(15,20)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During , AllenIntervalConstraint.Type.During.getDefaultBounds()))

				);
		srules.add(r3);


//		SpatialRule r2 = new SpatialRule("cup", "knife", 
//				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(8,16)),
//						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(10, APSPSolver.INF), new Bounds(1, APSPSolver.INF)))
//				);
//		srules.add(r2);
//
//		SpatialRule r3 = new SpatialRule("cup", "fork", 
//				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(8,16)),
//						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During , new Bounds(10, APSPSolver.INF), new Bounds(1, APSPSolver.INF)))
//
//				);
//		srules.add(r3);

		SpatialRule r4 = new SpatialRule("fork", "table", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r4);

		SpatialRule r5 = new SpatialRule("knife", "table", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r5);

		//		SpatialRule r6 = new SpatialRule("cup", "table", 
		//				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
		//						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
		//				);
		//		srules.add(r6);

		
		SpatialRule r7 = new SpatialRule("knife", "knife", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						knife_size_x), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, knife_size_y)));
		srules.add(r7);

		SpatialRule r8 = new SpatialRule("cup", "cup", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						cup_size_x), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, cup_size_y)));
		srules.add(r8);

		SpatialRule r9 = new SpatialRule("fork", "fork", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						fork_size_x), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, fork_size_y)));
		srules.add(r9);
		



		

		Vector<SpatialAssertionalRelation> saRelations = new Vector<SpatialAssertionalRelation>();

		SpatialAssertionalRelation sa1 = new SpatialAssertionalRelation("table1", "table");
		sa1.setCoordinate(new BoundingBox(new Bounds(0, 0), new Bounds(100, 100), new Bounds(0, 0), new Bounds(99, 99)));
		OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
		tableOnto.setMovable(false);
		sa1.setOntologicalProp(tableOnto);
		saRelations.add(sa1);

		SpatialAssertionalRelation sa3 = new SpatialAssertionalRelation("fork1", "fork");
		sa3.setCoordinate(new BoundingBox(new Bounds(31, 31), new Bounds(37, 37), new Bounds(13, 13), new Bounds(32, 32)));
//		sa3.setCoordinate(new BoundingBox(new Bounds(32, 32), new Bounds(37, 37), new Bounds(10, 10), new Bounds(30, 30)));
//		sa3.setCoordinate(new BoundingBox(new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		saRelations.add(sa3);

		
		SpatialAssertionalRelation sa2 = new SpatialAssertionalRelation("knife1", "knife");
		sa2.setCoordinate(new BoundingBox(new Bounds(45,45), new Bounds(51,51), new Bounds(10, 10), new Bounds(33, 33)));
//		sa2.setCoordinate(new BoundingBox(new Bounds(64,64), new Bounds(72,72), new Bounds(11, 11), new Bounds(33, 33)));
//		sa2.setCoordinate(new BoundingBox(new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		saRelations.add(sa2);


		SpatialAssertionalRelation sa4 = new SpatialAssertionalRelation("cup1", "cup");
		sa4.setCoordinate(new BoundingBox(new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		saRelations.add(sa4);
		
		//.......................................................................................................
		MetaSpatialConstraintSolver metaSolver = new MetaSpatialConstraintSolver(0);

		MetaSpatialConstraint objectsPosition = new MetaSpatialConstraint();
		objectsPosition.setSpatialRules(srules.toArray(new SpatialRule[srules.size()]));
		objectsPosition.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation[saRelations.size()]));
		
		MetaCSPLogging.setLevel(MetaSpatialConstraintSolver.class, Level.FINEST);
		
		metaSolver.addMetaConstraint(objectsPosition);
		metaSolver.backtrack();
		
		if(objectsPosition.isConsistent())
			System.out.println("consistent");
		else System.out.println("inconsistent");
			

		System.out.println("cup: " + objectsPosition.getRectangle("cup"));
		System.out.println("knife: " + objectsPosition.getRectangle("knife"));

		objectsPosition.culpritDetector();
		System.out.println("culprits: " + objectsPosition.getCulprits()[0].getName());



	}

}
