package test.meta.RACE;

import java.util.Vector;
import org.metacsp.framework.ConstraintNetwork;
import meta.MetaSpatialConstraint2;
import meta.MetaSpatialConstraintSolver2;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint;

import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraintSolver;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangularRegion;
import org.metacsp.multi.spatial.rectangleAlgebra.UnaryRectangleConstraint;
import org.metacsp.multi.spatial.rectangleAlgebraNew.toRemove.OntologicalSpatialProperty;
import org.metacsp.multi.spatioTemporal.SpatialFluent;
import org.metacsp.multi.spatioTemporal.SpatialFluentSolver;

import spatial.utility.SpatialAssertionalRelation2;
import spatial.utility.SpatialRule2;

import org.metacsp.time.APSPSolver;
import org.metacsp.time.Bounds;

public class TestNewImplementaionRA {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

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
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(15,20)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds() ))
				);
		srules.add(r2);

		SpatialRule2 r3 = new SpatialRule2("cup", "fork", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(15,20)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During , AllenIntervalConstraint.Type.During.getDefaultBounds()))

				);
		srules.add(r3);

		SpatialRule2 r4 = new SpatialRule2("fork", "table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r4);

		SpatialRule2 r5 = new SpatialRule2("knife", "table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r5);





		SpatialRule2 r7 = new SpatialRule2("knife", "knife", 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, knife_size_x, knife_size_y));
		srules.add(r7);

		SpatialRule2 r8 = new SpatialRule2("cup", "cup", 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, cup_size_x, cup_size_y));
		srules.add(r8);

		SpatialRule2 r9 = new SpatialRule2("fork", "fork", 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, fork_size_x, fork_size_y));
		srules.add(r9);

		//###############################################################################################
		//Assertion
		Vector<SpatialAssertionalRelation2> saRelations = new Vector<SpatialAssertionalRelation2>();

		SpatialAssertionalRelation2 sa1 = new SpatialAssertionalRelation2("table1", "table");
		sa1.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
				new Bounds(0, 0), new Bounds(100, 100), new Bounds(0, 0), new Bounds(99, 99)));
		OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
		tableOnto.setMovable(false);
		sa1.setOntologicalProp(tableOnto);
		
		
		saRelations.add(sa1);
		//............................................................................................

		SpatialAssertionalRelation2 sa3 = new SpatialAssertionalRelation2("fork1", "fork");		
		sa3.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
				new Bounds(31, 31), new Bounds(37, 37), new Bounds(13, 13), new Bounds(32, 32)));
		

		saRelations.add(sa3);

		//		SpatialAssertionalRelation2 sa2 = new SpatialAssertionalRelation2("knife1", "knife");
		//		sa2.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
		//				new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		//		saRelations.add(sa2);
		
		//.........................................................................................
		SpatialAssertionalRelation2 sa2 = new SpatialAssertionalRelation2("knife1", "knife");
		sa2.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
				new Bounds(45,45), new Bounds(51,51), new Bounds(10, 10), new Bounds(33, 33)));
				
		saRelations.add(sa2);
		
		//....................................................................................
		SpatialAssertionalRelation2 sa4 = new SpatialAssertionalRelation2("cup1", "cup");
		sa4.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
				new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		
				
		saRelations.add(sa4);


		//####################################################################################################
		MetaSpatialConstraintSolver2 metaSolver = new MetaSpatialConstraintSolver2(0, 1000, 0);
		MetaSpatialConstraint2 objectsPosition = new MetaSpatialConstraint2();
		objectsPosition.setSpatialRules(srules.toArray(new SpatialRule2[srules.size()]));
		objectsPosition.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation2[saRelations.size()]));


		//		MetaCSPLogging.setLevel(MetaSpatialConstraintSolver2.class, Level.FINEST);
		//		MetaCSPLogging.setLevel(MetaSpatialConstraint2.class, Level.FINEST);


		metaSolver.addMetaConstraint(objectsPosition);
		metaSolver.backtrack();
		
//		System.out.println(metaSolver.getConstraintSolvers()[0]);
		
		ConstraintNetwork.draw(metaSolver.getConstraintSolvers()[0].getConstraintNetwork(), "Constraint Network");
		((RectangleConstraintSolver)metaSolver.getConstraintSolvers()[0]).extractBoundingBoxesFromSTPs("cup1").getAlmostCentreRectangle();

		//
		//		System.out.println(objectsPosition.getRectangle("cup1"));
		//		System.out.println(objectsPosition.getRectangle("knife1"));
		//		System.out.println(objectsPosition.getRectangle("fork1"));


	}

}
