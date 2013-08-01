package test;

import java.util.Vector;
import java.util.logging.Level;

import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraintSolver;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangularRegion;
import org.metacsp.multi.spatial.rectangleAlgebra.UnaryRectangleConstraint;
import org.metacsp.time.APSPSolver;
import org.metacsp.time.Bounds;
import org.metacsp.utility.logging.MetaCSPLogging;
import org.metacsp.framework.Constraint;
import org.metacsp.framework.ConstraintNetwork;

public class TestIranRAConstraint2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		RectangleConstraintSolver solver = new RectangleConstraintSolver(0,1000);
		Vector<Constraint> allConstraints = new Vector<Constraint>();
		
		//..........................................................
		//T-BOX Variables
		RectangularRegion knife = (RectangularRegion)solver.createVariable();		
		knife.setName("knife");
		
		RectangularRegion fork = (RectangularRegion)solver.createVariable();
		fork.setName("fork");
		
		RectangularRegion table = (RectangularRegion)solver.createVariable();
		table.setName("table");
		
		RectangularRegion cup = (RectangularRegion)solver.createVariable();
		cup.setName("cup");
		

		
		//..........................................................
		
		RectangularRegion knife1 = (RectangularRegion)solver.createVariable();
		knife1.setName("knife1");
		UnaryRectangleConstraint atKnife1 = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, new Bounds(45,45), new Bounds(51, 51), new Bounds(10, 10), new Bounds(33, 33));
		atKnife1.setFrom(knife1);
		atKnife1.setTo(knife1);
		allConstraints.add(atKnife1);
		
		
		RectangularRegion fork1 = (RectangularRegion)solver.createVariable();
		fork1.setName("fork1");
		UnaryRectangleConstraint atFork1 = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At,
				new Bounds(31, 31), new Bounds(37, 37), new Bounds(13, 13), new Bounds(32, 32));
		atFork1.setFrom(fork1);
		atFork1.setTo(fork1);
		allConstraints.add(atFork1);
		
		RectangularRegion table1 = (RectangularRegion)solver.createVariable();
		table1.setName("table1");
		UnaryRectangleConstraint atTable1 = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, new Bounds(0,0), new Bounds(100,100), new Bounds(0,0), new Bounds(99,99));
		atTable1.setFrom(table1);
		atTable1.setTo(table1);
		allConstraints.add(atTable1);

		
		RectangularRegion cup1 = (RectangularRegion)solver.createVariable();
		cup1.setName("cup1");
		UnaryRectangleConstraint atCup1 = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
				new Bounds(0,APSPSolver.INF), new Bounds(0,APSPSolver.INF), new Bounds(0,APSPSolver.INF), new Bounds(0,APSPSolver.INF));
		atCup1.setFrom(cup1);
		atCup1.setTo(cup1);
		allConstraints.add(atCup1);

		//..........................................................
//		[{RectangularRegion cup [[[0, INF], [0, INF]], [[0, INF], [0, INF]]]} ---([Before], [During])--> ({RectangularRegion knife [[[0, INF], [0, INF]], [[0, INF], [0, INF]]]}]
//				[{RectangularRegion cup [[[0, INF], [0, INF]], [[0, INF], [0, INF]]]} ---([After], [During])--> ({RectangularRegion fork [[[0, INF], [0, INF]], [[0, INF], [0, INF]]]}]

		//T-BOX Constraints
		UnaryRectangleConstraint sizeFork = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, new Bounds(4, 8), new Bounds(18, 24));
		sizeFork.setFrom(fork);
		sizeFork.setTo(fork);
		allConstraints.add(sizeFork);

		UnaryRectangleConstraint sizeCup = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, new Bounds(4, 7), new Bounds(4, 7));
		sizeCup.setFrom(cup);
		sizeCup.setTo(cup);
		allConstraints.add(sizeCup);

		
		UnaryRectangleConstraint sizeKnife = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, new Bounds(4, 8), new Bounds(18, 24));
		sizeKnife.setFrom(knife);
		sizeKnife.setTo(knife);
		allConstraints.add(sizeKnife);

		RectangleConstraint forkDuringTable = new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During), new AllenIntervalConstraint(AllenIntervalConstraint.Type.During));
		forkDuringTable.setFrom(fork);
		forkDuringTable.setTo(table);
		allConstraints.add(forkDuringTable);

		
		RectangleConstraint knifeDuringTable = new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During), new AllenIntervalConstraint(AllenIntervalConstraint.Type.During));
		knifeDuringTable.setFrom(knife);
		knifeDuringTable.setTo(table);
		allConstraints.add(knifeDuringTable);

		RectangleConstraint CupToFork = new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(15,20)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.During));
		CupToFork.setFrom(cup);
		CupToFork.setTo(fork);
		allConstraints.add(CupToFork);

		RectangleConstraint cupToknife = new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(15,20)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.During));
		cupToknife.setFrom(cup);
		cupToknife.setTo(knife);
		allConstraints.add(cupToknife);

		
		//..........................................................
		//A-BOX to T-BOX Constraints

		RectangleConstraint cupAssertion = new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals));
		cupAssertion.setFrom(cup);
		cupAssertion.setTo(cup1);
		allConstraints.add(cupAssertion);

		
		RectangleConstraint knifAssertion = new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals));
		knifAssertion.setFrom(knife);
		knifAssertion.setTo(knife1);
		allConstraints.add(knifAssertion);
		
		RectangleConstraint forkAssertion = new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals));
		forkAssertion.setFrom(fork);
		forkAssertion.setTo(fork1);
		allConstraints.add(forkAssertion);

		RectangleConstraint dishAssertion = new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals));
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
