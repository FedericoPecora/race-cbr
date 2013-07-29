package test.tableConfiguration;

import java.util.Vector;
import meta.MetaSpatialConstraint;
import meta.MetaSpatialConstraintSolver;
import multi.allenInterval.AllenIntervalConstraint;
import multi.spatial.rectangleAlgebra.BoundingBox;
import spatial.rectangleAlgebra_OLD.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra_OLD.SpatialAssertionalRelation;
import spatial.rectangleAlgebra_OLD.SpatialRule;
import time.APSPSolver;
import time.Bounds;


public class TestSetTableFromScratch2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Bounds knife_size_x = new Bounds(5, 7);
		Bounds knife_size_y = new Bounds(20, 20);
		Bounds cup_size_x = new Bounds(5, 7);
		Bounds cup_size_y = new Bounds(5, 7);
		Bounds fork_size_x = new Bounds(5, 7);
		Bounds fork_size_y = new Bounds(20, 20);
		Bounds withinReach_y_lower = new Bounds(20, 20);
		Bounds withinReach_y_upper = new Bounds(20, APSPSolver.INF);
		Bounds withinReach_x_lower = new Bounds(20, APSPSolver.INF);
		Bounds withinReach_x_upper = new Bounds(20, APSPSolver.INF);

		
		Vector<SpatialRule> srules = new Vector<SpatialRule>();

		
		SpatialRule r2 = new SpatialRule("cup", "knife", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(20, 20)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(10, APSPSolver.INF), new Bounds(1, APSPSolver.INF)))
				);
		srules.add(r2);

		SpatialRule r3 = new SpatialRule("cup", "fork", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(20, 20)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During , new Bounds(10, APSPSolver.INF), new Bounds(1, APSPSolver.INF)))

				);
		srules.add(r3);

		
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


		
		//Assertion
		Vector<SpatialAssertionalRelation> saRelations = new Vector<SpatialAssertionalRelation>();




		SpatialAssertionalRelation sa1 = new SpatialAssertionalRelation("table1", "table");
		sa1.setCoordinate(new BoundingBox(new Bounds(0, 0), new Bounds(100, 100), new Bounds(0, 0), new Bounds(100, 100)));
		saRelations.add(sa1);

		SpatialAssertionalRelation sa2 = new SpatialAssertionalRelation("knife1", "knife");
		sa2.setCoordinate(new BoundingBox(new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		saRelations.add(sa2);

		SpatialAssertionalRelation sa3 = new SpatialAssertionalRelation("fork1", "fork");
		sa3.setCoordinate(new BoundingBox(new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));				
		saRelations.add(sa3);

		SpatialAssertionalRelation sa4 = new SpatialAssertionalRelation("cup1", "cup");
		sa4.setCoordinate(new BoundingBox(new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
		saRelations.add(sa4);



		//.......................................................................................................
		MetaSpatialConstraintSolver metaSolver = new MetaSpatialConstraintSolver(0);

		MetaSpatialConstraint objectsPosition = new MetaSpatialConstraint();
		objectsPosition.setSpatialRules(srules.toArray(new SpatialRule[srules.size()]));
		objectsPosition.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation[saRelations.size()]));

		metaSolver.addMetaConstraint(objectsPosition);
		metaSolver.backtrack();


		//System.out.println(objectsPosition.getGnuplotScript("cup", "fork", "knife"));
		System.out.println(objectsPosition.getRectangle("cup"));
		System.out.println(objectsPosition.getRectangle("fork"));
		System.out.println(objectsPosition.getRectangle("knife"));
		
		//...................................................
		
//        AllenIntervalNetworkSolver solver = new AllenIntervalNetworkSolver(0, 2000);
//        AllenInterval[] intervals = (AllenInterval[])solver.createVariables(3);
//
//        MetaCSPLogging.setLevel(Level.FINEST);
////      MetaCSPLogging.setLevel(solver.getClass(), Level.FINEST);
////      MetaCSPLogging.setLevel(solver.getConstraintSolvers()[0].getClass(), Level.FINEST);
//        
//        //DRAW IT!
//        ConstraintNetwork.draw(solver.getConstraintNetwork());
//        
//        AllenIntervalConstraint con1 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, APSPSolver.INF),new Bounds(5, APSPSolver.INF));
//        con1.setFrom(intervals[0]);
//        con1.setTo(intervals[1]);
//        
//        
////		AllenIntervalConstraint releaseX = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, 
////				new Bounds(0, APSPSolver.INF));
////		releaseX.setFrom(intervals[0]);
////		releaseX.setTo(intervals[0]);
////		
////
////		AllenIntervalConstraint deadlineX = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Deadline, 
////				new Bounds(0, APSPSolver.INF));
////		deadlineX.setFrom(intervals[0]);
////		deadlineX.setTo(intervals[0]);
////		
////		
////		AllenIntervalConstraint releaseX1 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, 
////				new Bounds(0, 0));
////		releaseX1.setFrom(intervals[1]);
////		releaseX1.setTo(intervals[1]);
////		
////
////		AllenIntervalConstraint deadlineX1 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Deadline, 
////				new Bounds(100, 100));
////		deadlineX1.setFrom(intervals[1]);
////		deadlineX1.setTo(intervals[1]);
//		
//		
//
//        
////        AllenIntervalConstraint con2 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(30, 40));
////        con2.setFrom(intervals[0]);
////        con2.setTo(intervals[0]);
////
////        AllenIntervalConstraint con3 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Overlaps, AllenIntervalConstraint.Type.Overlaps.getDefaultBounds());
////        con3.setFrom(intervals[1]);
////        con3.setTo(intervals[2]);
//
//        //Constraint[] cons = new Constraint[]{con1,releaseX, releaseX1, deadlineX, deadlineX1};
//		Constraint[] cons = new Constraint[]{con1};
////        System.out.println(solver.getConstraintNetwork());
//        System.out.println(solver.addConstraintsDebug(cons) == null);
        
		
	}

}
