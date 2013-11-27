package meta.spatialSchedulable;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Vector;

import meta.MetaCausalConstraint.markings;

import org.metacsp.framework.Constraint;
import org.metacsp.framework.ConstraintNetwork;
import org.metacsp.framework.ValueOrderingH;
import org.metacsp.framework.VariableOrderingH;
import org.metacsp.framework.meta.MetaConstraint;
import org.metacsp.framework.meta.MetaVariable;
import org.metacsp.multi.activity.Activity;
import org.metacsp.multi.activity.ActivityNetworkSolver;
import org.metacsp.multi.allenInterval.AllenInterval;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.BoundingBox;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraintSolver;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangularRegion;
import org.metacsp.multi.spatial.rectangleAlgebra.UnaryRectangleConstraint;
import org.metacsp.multi.spatioTemporal.SpatialFluent;
import org.metacsp.multi.spatioTemporal.SpatialFluentSolver;
import org.metacsp.time.APSPSolver;
import org.metacsp.time.Bounds;

import spatial.utility.SpatialAssertionalRelation2;

public class MetaReachabilityChecker extends MetaConstraint  {

	/**
	 * Iran Mansouri
	 * TODO
	 * resolver: at_object should be during newRobotFluent
	 * Add new rectangle (manArea) for the newRobotFluent
	 * What if we add during relation when we add as a resolver!?!?
	 * 
	 */
	private static final long serialVersionUID = -2602296896261976692L;
//	private SpatialAssertionalRelation2[] sAssertionalRels;
	
	public MetaReachabilityChecker(VariableOrderingH varOH, ValueOrderingH valOH) {
		super(varOH, valOH);

	}
	
//	public void setSpatialAssertionalRelations(SpatialAssertionalRelation2... sAssertionalRels) {
//		this.sAssertionalRels = new SpatialAssertionalRelation2[sAssertionalRels.length];
//		this.sAssertionalRels = sAssertionalRels;
//	}

	private ConstraintNetwork[] binaryPeakCollection(HashMap<Activity, SpatialFluent> aTOsf) {


		Vector<Activity> activities = new Vector<Activity>();
		for (Activity act : aTOsf.keySet()) {
			activities.add(act);
		}


		if (activities != null && !activities.isEmpty()) {
			Vector<ConstraintNetwork> ret = new Vector<ConstraintNetwork>();
			logger.finest("Doing binary peak collection with " + activities.size() + " activities...");
			Activity[] groundVars = activities.toArray(new Activity[activities.size()]);
			for (Activity a : groundVars) {
				if (isConflicting(new Activity[] {a}, aTOsf)) {
					ConstraintNetwork cn = new ConstraintNetwork(null);
					cn.addVariable(a);
					ret.add(cn);
				}
			}
			if (!ret.isEmpty()) {
				return ret.toArray(new ConstraintNetwork[ret.size()]);
			}
			for (int i = 0; i < groundVars.length-1; i++) {
				for (int j = i+1; j < groundVars.length; j++) {
					Bounds bi = new Bounds(groundVars[i].getTemporalVariable().getEST(), groundVars[i].getTemporalVariable().getEET());
					Bounds bj = new Bounds(groundVars[j].getTemporalVariable().getEST(), groundVars[j].getTemporalVariable().getEET());
					if (bi.intersectStrict(bj) != null && isConflicting(new Activity[] {groundVars[i], groundVars[j]}, aTOsf)) {
						ConstraintNetwork cn = new ConstraintNetwork(null);
						cn.addVariable(groundVars[i]);
						cn.addVariable(groundVars[j]);
						ret.add(cn);
					}
				}
			}
			if (!ret.isEmpty()) {
				return ret.toArray(new ConstraintNetwork[ret.size()]);			
			}
		}
		return (new ConstraintNetwork[0]);
	}

	private boolean isUnboundedBoundingBox(Bounds xLB, Bounds xUB, Bounds yLB, Bounds yUB) {

		long horizon = ((ActivityNetworkSolver)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[1]).getHorizon();

		if( (xLB.min == 0 && xLB.max == horizon) && (xUB.min == 0&& xUB.max == horizon) &&
				(yLB.min == 0 && yLB.max == horizon) &&(yLB.min == 0 && yUB.max == horizon))
			return true;

		return false;
	}

	private boolean isConflicting(Activity[] peak, HashMap<Activity, SpatialFluent> activityToFluent) {


		if(peak.length == 1) return false;


		//check whether they are refering to the same object and discard it
		if(activityToFluent.get(peak[0]).getName().compareTo(activityToFluent.get(peak[1]).getName()) == 0) return false;

		//discard peaks which the robots spatial fluent is not involved
		boolean hasRobotinRelation = false;
		if(activityToFluent.get(peak[0]).getName().compareTo("robot1") == 0 || activityToFluent.get(peak[1]).getName().compareTo("robot1") == 0) hasRobotinRelation = true;
		if(!hasRobotinRelation) return false;

//		System.out.println("-------------------------------------------------");
//		for (int i = 0; i < peak.length; i++) {
//			System.out.println("peak: " + activityToFluent.get(peak[i]));
//		}
//		System.out.println("-------------------------------------------------");
		
		
		/*
		 * TODO
		 * check whether the second argument of the spatial fluent are in the relation
		 * this is ignored for now till  any relational language integrated to the hybrid planner 
		 */
		
		Rectangle robotRec = null;
		Rectangle recObj = null;
		for (int i = 0; i < peak.length; i++) {
			if(activityToFluent.get(peak[i]).getName().compareTo("robot1") == 0){
				
				if(!isUnboundedBoundingBox(
						new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[0]).getEST(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[0]).getLST()),
						new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[0]).getEET(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[0]).getLET()), 
						new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[1]).getEST(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[1]).getLST()), 
						new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[1]).getEET(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[1]).getLET()))							
						 //&& (((Activity)activityToFluent.get(peak[i]).getActivity()).getTemporalVariable().getEST() != ((Activity)activityToFluent.get(peak[i]).getActivity()).getTemporalVariable().getLST())
						){
					robotRec = ((RectangleConstraintSolver)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[0])
							.extractAllBoundingBoxesFromSTPs().get("robot1").getAlmostCentreRectangle();						
				}
				else{					
					robotRec = ((MetaSpatialScheduler)this.metaCS).getOldRectangularRegion().get("robot1").getAlmostCentreRectangle();
					System.out.println("getOldRobotRec: " + robotRec);
				}
				
				continue;
				
			}
			/*
			 *if there is an unbounded rectangle, this object becomes interesting only if it has to be manipulated, 
			 *meaning that there is a bounded rectangles (bounded rectangle means new replacements) refering to same spatial fluent 
			 */
			if(isUnboundedBoundingBox(
					new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[0]).getEST(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[0]).getLST()),
					new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[0]).getEET(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[0]).getLET()), 
					new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[1]).getEST(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[1]).getLST()), 
					new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[1]).getEET(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[1]).getLET()))							
					){

				int counter = 0;
				for (SpatialFluent sf : activityToFluent.values()) 
					if(sf.getName().compareTo(activityToFluent.get(peak[i]).getName()) == 0) counter++;
				if(counter > 1)
					for (String str : ((MetaSpatialScheduler)this.metaCS).getOldRectangularRegion().keySet()) {
						if(activityToFluent.get(peak[i]).getRectangularRegion().getName().compareTo(str) == 0){
							recObj = ((MetaSpatialScheduler)this.metaCS).getOldRectangularRegion().get(str).getAlmostCentreRectangle();
//							System.out.println("unbounded: " + activityToFluent.get(peak[i]));
						}
					}

			}
			else {
				
				recObj = ((RectangleConstraintSolver)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[0])
						.extractAllBoundingBoxesFromSTPs().get(activityToFluent.get(peak[i]).getName().replaceAll("[0-9]","")).getAlmostCentreRectangle();
//							System.out.println("bounded: " + activityToFluent.get(peak[i]));
			}
		}

		System.out.println("_________________________________________________");
		if(recObj != null){
			System.out.println("robotRec: " + robotRec);
			System.out.println("objRec: " + recObj);
			if(isReachable(robotRec, recObj)){
				System.out.println("here is the conflict: " + peak.length);
				for (int i = 0; i < peak.length; i++) {
					System.out.println("peak: " + activityToFluent.get(peak[i]));
				}
				return true;
			}
		}
		System.out.println("_________________________________________________");





		return false;
	}


	private boolean isReachable(Rectangle robotRec, Rectangle recObj) {



		double distance = Math.sqrt((robotRec.getCenterX()-recObj.getCenterX())*(robotRec.getCenterX()-recObj.getCenterX()) + (robotRec.getCenterY()-recObj.getCenterY())*(robotRec.getCenterY()-recObj.getCenterY()));
		if(distance > 80) return true;
		//		System.out.println("distance " + distance);
		return false;
	}

	@Override
	public ConstraintNetwork[] getMetaVariables() {

		HashMap<Activity, SpatialFluent> activityToFluent = new HashMap<Activity, SpatialFluent>();
		Vector<Activity> activities = new Vector<Activity>();
		for (int i = 0; i < ((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables().length; i++) {
			if(((SpatialFluent)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]).getRectangularRegion().getOntologicalProp().isMovable()){
				activities.add(((SpatialFluent)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]).getActivity());
				activityToFluent.put(((SpatialFluent)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]).getActivity(), 
						((SpatialFluent)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]));
			}
		}

//				System.out.println("===================================================");
//				System.out.println("activities: " + activityToFluent);
//				System.out.println("===================================================");

		return binaryPeakCollection(activityToFluent);

	}

	@Override
	public ConstraintNetwork[] getMetaValues(MetaVariable metaVariable) {

		ConstraintNetwork conflict = metaVariable.getConstraintNetwork();

		Vector<ConstraintNetwork> ret = new Vector<ConstraintNetwork>();
		HashMap<Activity, SpatialFluent> activityToFluent = new HashMap<Activity, SpatialFluent>();
		ConstraintNetwork actNetwork = new ConstraintNetwork(((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0])).getConstraintSolvers()[1]);

		for (int i = 0; i < ((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables().length; i++) {
			activityToFluent.put(((SpatialFluent)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]).getActivity(), 
					((SpatialFluent)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]));

		}

		SpatialFluent newrobotFlunet = null;
		SpatialFluent oldrobotFlunet = null;
		SpatialFluent objPlacementFlunet = null;
		for (int j = 0; j < conflict.getVariables().length; j++) {
			if(activityToFluent.get((Activity) conflict.getVariables()[j]).getName().compareTo("robot1") == 0){

				oldrobotFlunet = activityToFluent.get((Activity) conflict.getVariables()[j]);
				newrobotFlunet = (SpatialFluent)((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0]))
						.createVariable(((Activity) conflict.getVariables()[j]).getComponent());
				newrobotFlunet.setName(activityToFluent.get((Activity) conflict.getVariables()[j]).getName());

				((Activity)newrobotFlunet.getInternalVariables()[1]).setSymbolicDomain("at_robot1_manArea2()");

				((Activity)newrobotFlunet.getInternalVariables()[1]).setMarking(markings.UNJUSTIFIED);
				((RectangularRegion)newrobotFlunet.getInternalVariables()[0]).setName(activityToFluent.get((Activity) conflict.getVariables()[j]).getName());

				activityToFluent.put(((Activity)newrobotFlunet.getInternalVariables()[1]), newrobotFlunet);
				
			}
			else{
				objPlacementFlunet = activityToFluent.get((Activity) conflict.getVariables()[j]);
			}

		}

		System.out.println("new fluent: " + newrobotFlunet);
		
		
		AllenIntervalConstraint newrobotPoseAfteroldRobotPose = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After,
				AllenIntervalConstraint.Type.After.getDefaultBounds());
		newrobotPoseAfteroldRobotPose.setFrom(((Activity)newrobotFlunet.getInternalVariables()[1]));
		newrobotPoseAfteroldRobotPose.setTo(((Activity)oldrobotFlunet.getInternalVariables()[1]));

		System.out.println("new relation " + newrobotPoseAfteroldRobotPose);

		AllenIntervalConstraint objplacementOverlappedByRobotPosition = new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy,
				AllenIntervalConstraint.Type.OverlappedBy.getDefaultBounds());
		objplacementOverlappedByRobotPosition.setFrom(((Activity)objPlacementFlunet.getInternalVariables()[1]));
		objplacementOverlappedByRobotPosition.setTo(((Activity)newrobotFlunet.getInternalVariables()[1]));
		
		System.out.println("new overlap " + objplacementOverlappedByRobotPosition);
		
//		//update the new placement of the robot
//		for (int j = 0; j < sAssertionalRels.length; j++) {
//			if(sAssertionalRels[j].getFrom().compareTo(st) == 0)
//			sAssertionalRels[j].setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
//					new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
//		}
		
		actNetwork.addConstraint(newrobotPoseAfteroldRobotPose);			
		actNetwork.addConstraint(objplacementOverlappedByRobotPosition);




		ret.add(actNetwork);
		return ret.toArray(new ConstraintNetwork[ret.size()]);


	}

	@Override
	public ConstraintNetwork[] getMetaValues(MetaVariable metaVariable,
			int initial_time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void markResolvedSub(MetaVariable metaVariable,
			ConstraintNetwork metaValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(ConstraintNetwork network) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEdgeLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEquivalent(Constraint c) {
		// TODO Auto-generated method stub
		return false;
	}

}
