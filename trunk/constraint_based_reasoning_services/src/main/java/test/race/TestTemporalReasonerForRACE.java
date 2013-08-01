package test.race;

import org.metacsp.multi.allenInterval.AllenInterval;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.multi.allenInterval.AllenIntervalNetworkSolver;
import org.metacsp.time.Bounds;
import org.metacsp.framework.Constraint;
import org.metacsp.framework.ConstraintNetwork;

public class TestTemporalReasonerForRACE {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	

		//define the SWRL rule here, to make sure that it is compatible
		//before an aggregate (activity) is fired (is instantiated in A Box) the intervals are open ended (-infinity, +infinity)
		//I have to make an activity network solver to show that we have ability of checking value consistency and temporal consistency 
		
		
		//Define the interval 
		AllenIntervalNetworkSolver solver = new AllenIntervalNetworkSolver(0, 100);
		AllenInterval[] intervals = (AllenInterval[])solver.createVariables(3);

		ConstraintNetwork.draw(solver.getConstraintNetwork());
		int emptyHandInterval_st = 2, emptyHandInterval_ft = 8;  
		int getObjectFromInterval_st = 4, getObjectFromInterval_ft = 10;
		int moveObjectToInterval_st = 12, moveObjectToInterval_ft = 18;
		
		//For Demo
		intervals[0].setName("emptyHandInterval");
		intervals[1].setName("getObjectFromInterval");
		intervals[2].setName("moveObjectToInterval");
		
		AllenIntervalConstraint emptyHandInterval_release = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(emptyHandInterval_st, emptyHandInterval_st));
		emptyHandInterval_release.setFrom(intervals[0]);
		emptyHandInterval_release.setTo(intervals[0]);
		
		AllenIntervalConstraint emptyHandInterval_deadline = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Deadline, new Bounds(emptyHandInterval_ft, emptyHandInterval_ft));
		emptyHandInterval_deadline.setFrom(intervals[0]);
		emptyHandInterval_deadline.setTo(intervals[0]);

		AllenIntervalConstraint getObjectFromInterval_release = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(getObjectFromInterval_st, getObjectFromInterval_st));
		getObjectFromInterval_release.setFrom(intervals[1]);
		getObjectFromInterval_release.setTo(intervals[1]);
		
		AllenIntervalConstraint getObjectFromInterval_deadline = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Deadline, new Bounds(getObjectFromInterval_ft, getObjectFromInterval_ft));
		getObjectFromInterval_deadline.setFrom(intervals[1]);
		getObjectFromInterval_deadline.setTo(intervals[1]);
		
		AllenIntervalConstraint moveObjectToInterval_release = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(moveObjectToInterval_st, moveObjectToInterval_st));
		moveObjectToInterval_release.setFrom(intervals[2]);
		moveObjectToInterval_release.setTo(intervals[2]);
		
		AllenIntervalConstraint moveObjectToInterval_deadline = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Deadline, new Bounds(moveObjectToInterval_ft, moveObjectToInterval_ft));
		moveObjectToInterval_deadline.setFrom(intervals[2]);
		moveObjectToInterval_deadline.setTo(intervals[2]);

		//Check the temporal requirements defined in the SWRL rule
		AllenIntervalConstraint overlaps = new AllenIntervalConstraint( AllenIntervalConstraint.Type.Overlaps, AllenIntervalConstraint.Type.Overlaps.getDefaultBounds());
		overlaps.setFrom(intervals[0]);
		overlaps.setTo(intervals[1]);

		//Check the temporal requirements defined in the SWRL rule
		AllenIntervalConstraint before = new AllenIntervalConstraint( AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds());
		before.setFrom(intervals[1]);
		before.setTo(intervals[2]);

		
		Constraint[] cons = new Constraint[]{emptyHandInterval_release, emptyHandInterval_deadline, getObjectFromInterval_release, 
				getObjectFromInterval_deadline, moveObjectToInterval_release, moveObjectToInterval_deadline, before, overlaps};	
		if (!solver.addConstraints(cons)) { 
			System.out.println("Failed to add constraints!");
			System.exit(0);
		}
		else System.out.println("true!");

		

	}

}
