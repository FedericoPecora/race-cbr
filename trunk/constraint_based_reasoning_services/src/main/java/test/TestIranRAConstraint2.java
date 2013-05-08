package test;

import java.util.Vector;
import java.util.logging.Level;

import multi.allenInterval.AllenIntervalConstraint;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraint2;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraintSolver2;
import sandbox.spatial.rectangleAlgebra2.RectangularRegion2;
import sandbox.spatial.rectangleAlgebra2.UnaryRectangleConstraint2;
import time.APSPSolver;
import time.Bounds;
import utility.logging.MetaCSPLogging;
import framework.Constraint;
import framework.ConstraintNetwork;

public class TestIranRAConstraint2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		RectangleConstraintSolver2 solver = new RectangleConstraintSolver2(0,1000);
		Vector<Constraint> allConstraints = new Vector<Constraint>();
		
		//..........................................................
		//T-BOX Variables
		RectangularRegion2 knife = (RectangularRegion2)solver.createVariable();		
		knife.setName("knife");
		
		RectangularRegion2 fork = (RectangularRegion2)solver.createVariable();
		fork.setName("fork");
		
		RectangularRegion2 table = (RectangularRegion2)solver.createVariable();
		table.setName("table");
		
		RectangularRegion2 cup = (RectangularRegion2)solver.createVariable();
		cup.setName("cup");
		

		
		//..........................................................
		
		RectangularRegion2 knife1 = (RectangularRegion2)solver.createVariable();
		knife1.setName("knife1");
		UnaryRectangleConstraint2 atKnife1 = new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, new Bounds(45,45), new Bounds(51, 51), new Bounds(10, 10), new Bounds(33, 33));
		atKnife1.setFrom(knife1);
		atKnife1.setTo(knife1);
		allConstraints.add(atKnife1);
		
		
		RectangularRegion2 fork1 = (RectangularRegion2)solver.createVariable();
		fork1.setName("fork1");
		UnaryRectangleConstraint2 atFork1 = new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At,
				new Bounds(31, 31), new Bounds(37, 37), new Bounds(13, 13), new Bounds(32, 32));
		atFork1.setFrom(fork1);
		atFork1.setTo(fork1);
		allConstraints.add(atFork1);
		
		RectangularRegion2 table1 = (RectangularRegion2)solver.createVariable();
		table1.setName("table1");
		UnaryRectangleConstraint2 atTable1 = new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, new Bounds(0,0), new Bounds(100,100), new Bounds(0,0), new Bounds(99,99));
		atTable1.setFrom(table1);
		atTable1.setTo(table1);
		allConstraints.add(atTable1);

		
		RectangularRegion2 cup1 = (RectangularRegion2)solver.createVariable();
		cup1.setName("cup1");
		UnaryRectangleConstraint2 atCup1 = new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, 
				new Bounds(0,APSPSolver.INF), new Bounds(0,APSPSolver.INF), new Bounds(0,APSPSolver.INF), new Bounds(0,APSPSolver.INF));
		atCup1.setFrom(cup1);
		atCup1.setTo(cup1);
		allConstraints.add(atCup1);

		//..........................................................
//		[{RectangularRegion2 cup [[[0, INF], [0, INF]], [[0, INF], [0, INF]]]} ---([Before], [During])--> ({RectangularRegion2 knife [[[0, INF], [0, INF]], [[0, INF], [0, INF]]]}]
//				[{RectangularRegion2 cup [[[0, INF], [0, INF]], [[0, INF], [0, INF]]]} ---([After], [During])--> ({RectangularRegion2 fork [[[0, INF], [0, INF]], [[0, INF], [0, INF]]]}]

		//T-BOX Constraints
		UnaryRectangleConstraint2 sizeFork = new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.Size, new Bounds(4, 8), new Bounds(18, 24));
		sizeFork.setFrom(fork);
		sizeFork.setTo(fork);
		allConstraints.add(sizeFork);

		UnaryRectangleConstraint2 sizeCup = new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.Size, new Bounds(4, 7), new Bounds(4, 7));
		sizeCup.setFrom(cup);
		sizeCup.setTo(cup);
		allConstraints.add(sizeCup);

		
		UnaryRectangleConstraint2 sizeKnife = new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.Size, new Bounds(4, 8), new Bounds(18, 24));
		sizeKnife.setFrom(knife);
		sizeKnife.setTo(knife);
		allConstraints.add(sizeKnife);

		RectangleConstraint2 forkDuringTable = new RectangleConstraint2(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During), new AllenIntervalConstraint(AllenIntervalConstraint.Type.During));
		forkDuringTable.setFrom(fork);
		forkDuringTable.setTo(table);
		allConstraints.add(forkDuringTable);

		
		RectangleConstraint2 knifeDuringTable = new RectangleConstraint2(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During), new AllenIntervalConstraint(AllenIntervalConstraint.Type.During));
		knifeDuringTable.setFrom(knife);
		knifeDuringTable.setTo(table);
		allConstraints.add(knifeDuringTable);

		RectangleConstraint2 CupToFork = new RectangleConstraint2(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(15,20)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.During));
		CupToFork.setFrom(cup);
		CupToFork.setTo(fork);
		allConstraints.add(CupToFork);

		RectangleConstraint2 cupToknife = new RectangleConstraint2(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(15,20)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.During));
		cupToknife.setFrom(cup);
		cupToknife.setTo(knife);
		allConstraints.add(cupToknife);

		
		//..........................................................
		//A-BOX to T-BOX Constraints

		RectangleConstraint2 cupAssertion = new RectangleConstraint2(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals));
		cupAssertion.setFrom(cup);
		cupAssertion.setTo(cup1);
		allConstraints.add(cupAssertion);

		
		RectangleConstraint2 knifAssertion = new RectangleConstraint2(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals));
		knifAssertion.setFrom(knife);
		knifAssertion.setTo(knife1);
		allConstraints.add(knifAssertion);
		
		RectangleConstraint2 forkAssertion = new RectangleConstraint2(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals));
		forkAssertion.setFrom(fork);
		forkAssertion.setTo(fork1);
		allConstraints.add(forkAssertion);

		RectangleConstraint2 dishAssertion = new RectangleConstraint2(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals));
		dishAssertion.setFrom(table);
		dishAssertion.setTo(table1);
		allConstraints.add(dishAssertion);
		
		Constraint[] allConstraintsArray = allConstraints.toArray(new Constraint[allConstraints.size()]);
//		MetaCSPLogging.setLevel(Level.FINEST);
		if (!solver.addConstraints(allConstraintsArray)) { 
			System.out.println("Failed to add constraints!");
			System.exit(0);
		}
		
		//String[] st = new String[]{"fork", "knife", "dish", "cup"};
//		System.out.println(solver.drawAlmostCentreRectangle(100, fork));
		System.out.println(solver.extractBoundingBoxesFromSTPs("cup").getAlmostCentreRectangle());
		
		

//		ConstraintNetwork.draw(solver.getConstraintNetwork());

	}

}
