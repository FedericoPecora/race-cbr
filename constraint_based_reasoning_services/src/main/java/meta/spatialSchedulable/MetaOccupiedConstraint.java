package meta.spatialSchedulable;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Vector;

import time.APSPSolver;
import time.Bounds;

import multi.activity.Activity;
import multi.activity.ActivityNetwork;
import multi.activity.ActivityNetworkSolver;
import multi.allenInterval.AllenInterval;
import multi.spatial.rectangleAlgebra.BoundingBox;
import multi.spatial.rectangleAlgebra.RectangularRegion;
import multi.spatioTemporal.SpatialFluent;
import multi.spatioTemporal.SpatialFluentSolver;
import framework.Constraint;
import framework.ConstraintNetwork;
import framework.ValueOrderingH;
import framework.VariableOrderingH;
import framework.meta.MetaConstraint;
import framework.meta.MetaVariable;

public class MetaOccupiedConstraint extends MetaConstraint {

	/**
	 * 
	 */
	private static final long serialVersionUID = 886075917563921380L;
	private HashMap<String, Rectangle> old_on;
	private HashMap<String, Rectangle> new_on;
	protected Vector<Activity> activities;
	private HashMap<Activity, SpatialFluent> activityToFluent;

	public MetaOccupiedConstraint(VariableOrderingH varOH, ValueOrderingH valOH) {
		super(varOH, valOH);

	}
	
	public void setUsage(HashMap<String, Rectangle> old_on, HashMap<String, Rectangle> new_on) {
		
		this.old_on = new HashMap<String, Rectangle>();
		this.new_on = new HashMap<String, Rectangle>();
		this.old_on = old_on;
		this.new_on = new_on;
	}
	
	@Override
	public ConstraintNetwork[] getMetaVariables() {
		
		activityToFluent = new HashMap<Activity, SpatialFluent>();
		activities = new Vector<Activity>();
		for (int i = 0; i < ((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables().length; i++) {
			if (!activities.contains(((SpatialFluent)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]).getActivity())) {
				activities.add(((SpatialFluent)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]).getActivity());
				activityToFluent.put(((SpatialFluent)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]).getActivity(), 
						((SpatialFluent)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]));
			}

		}
		return binaryPeakCollection();
	}

	private ConstraintNetwork[] binaryPeakCollection() {
		if (activities != null && !activities.isEmpty()) {
			Vector<ConstraintNetwork> ret = new Vector<ConstraintNetwork>();
			logger.finest("Doing binary peak collection with " + activities.size() + " activities...");
			Activity[] groundVars = activities.toArray(new Activity[activities.size()]);
			for (Activity a : groundVars) {
				if (isConflicting(new Activity[] {a})) {
					ActivityNetwork cn = new ActivityNetwork(null);
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
					if (bi.intersectStrict(bj) != null && isConflicting(new Activity[] {groundVars[i], groundVars[j]})) {
						ActivityNetwork cn = new ActivityNetwork(null);
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


	private boolean isConflicting(Activity[] peak) {
		
		if(peak.length == 1) return false;
		
		System.out.println("_________________________________________________");
		for (int i = 0; i < peak.length; i++) {
			System.out.println("peak: " + activityToFluent.get(peak[i]));
		}
		System.out.println("_________________________________________________");
		
//		class IntersectRectangluarRegion{
//		String rectangle1 = "";
//		String rectangle2 = "";			
//		}
//	
//		
//		Vector<HashMap<String, BoundingBox>> newRectangularRegion = new Vector<HashMap<String,BoundingBox>>();
//		HashMap<String, BoundingBox> oldRectangularRegion = new HashMap<String, BoundingBox>();
//
//		for (int i = 0; i < peak.length; i++) {
//			System.out.println(",,,," + ((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[0]).getEST());
//
//			if(isUnboundedBoundingBox(
//							new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[0]).getEST(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[0]).getLST()),
//							new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[0]).getEET(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[0]).getLET()), 
//							new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[1]).getEST(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[1]).getLST()), 
//							new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[1]).getEET(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[1]).getLET()))){//it was bouneded
//				System.out.println("--isunbounded--: " + activityToFluent.get(peak[i]));
//				//newRectangularRegion.put()
//			}
//			else{
//				System.out.println();
////				oldRectangularRegion.put{}
//			}
//		}

		
		
//		//check in the rectangleNetwork for the new placement
//		Vector<IntersectRectangluarRegion> intersectionSet = new Vector<IntersectRectangluarRegion>();
//		for (String newreg : newRectangularRegion.get(0).keySet()){
//			for (String oldreg : oldRectangularRegion.keySet()) {
//				if(oldreg.compareTo(newreg) != 0){
//					if(newRectangularRegion.get(0).get(newreg).getAlmostCentreRectangle().intersects(oldRectangularRegion.get(oldreg).getAlmostCentreRectangle())){						
//						boolean isAdded = false;
//						for (int i = 0; i < intersectionSet.size(); i++) {
//							if((intersectionSet.get(i).rectangle1.compareTo(newreg) == 0 && intersectionSet.get(i).rectangle2.compareTo(oldreg) == 0) 
//									|| (intersectionSet.get(i).rectangle1.compareTo(oldreg) == 0 && intersectionSet.get(i).rectangle2.compareTo(newreg) == 0))
//								isAdded = true;
//						}
//						if(!isAdded){
//							IntersectRectangluarRegion irr = new IntersectRectangluarRegion();
//							irr.rectangle1 = newreg;
//							irr.rectangle2 = oldreg;
//							intersectionSet.add(irr);
//						}
//					}
//				}
//			}
//		}			
	

		return false;
	}
	
	private boolean isUnboundedBoundingBox(Bounds xLB, Bounds xUB, Bounds yLB, Bounds yUB) {

		long horizon = ((ActivityNetworkSolver)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[1]).getHorizon();
		
		if( (xLB.min == 0 && xLB.max == horizon) && (xUB.min == 0&& xUB.max == horizon) &&
			(yLB.min == 0 && yLB.max == horizon) &&(yLB.min == 0 && yUB.max == horizon))
			return true;
			
		return false;
	}


	@Override
	public ConstraintNetwork[] getMetaValues(MetaVariable metaVariable) {
		
		//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		//Now it is the time for special cases! like swaping, in this case we want to state that if new places of the objects is physically overlapped
		//by the old places of the object, it has to be considered. we handle this case by adding the temporal constraint (before) between old_on (x) and new_on(y) 
		//where x and y are different
		
		//		--onculoritRegBeforeanotherNewOn---: (atLocation::<SymbolicVariable 3: [at_fork1_table1()]>U<AllenInterval 3 (I-TP: 8 9 ) [[20, 20], [21, 1000]]>/JUSTIFIED
		//				) --[Before] [1, INF]--> (atLocation::<SymbolicVariable 7: [at_knife1_table1()]>U<AllenInterval 7 (I-TP: 16 17 ) [[0, 1000], [0, 1000]]>/UNJUSTIFIED

		
		
//		for (int i = 0; i < intersectionSet.size(); i++) {			
//		for (SpatialFluent spatialFluent : newGoalFluentsVector) {
//			if(spatialFluent.getName().compareTo(intersectionSet.get(i).rectangle2) == 0){
//				
//				for (int j = 0; j < operators.size(); j++) {
//					if(culpritActivities.get(intersectionSet.get(i).rectangle1).getDomain().toString().
//							contains(operators.get(j).getHead().substring(operators.get(j).getHead().indexOf("::")+2, operators.get(j).getHead().length()))){
//						System.out.println("operators.get(j).getHead(): "  +operators.get(j).getHead());
//						for(String req: operators.get(j).getRequirementActivities()){
//								
//								System.out.println("hallowww2: " + req.substring(req.indexOf("::")+2, req.length()));
//								AllenIntervalConstraint onculoritRegBeforeanotherNewOn = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After,
//								AllenIntervalConstraint.Type.After.getDefaultBounds());
//								//place knife after old_on of fork
//								Activity placing = (Activity)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[1].createVariable("test");
//								placing.setMarking(markings.JUSTIFIED);
//								
//								onculoritRegBeforeanotherNewOn.setFrom(culpritActivities.get(intersectionSet.get(i).rectangle1)); //placing 
//								onculoritRegBeforeanotherNewOn.setTo(((Activity)spatialFluent.getInternalVariables()[1])); //old_on
//								actNetwork.addConstraint(onculoritRegBeforeanotherNewOn);
//								System.out.println("--onculoritRegBeforeanotherNewOn---: " + onculoritRegBeforeanotherNewOn);
//								break;
//								
//								
////								AllenIntervalConstraint onculoritRegBeforeanotherNewOn = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After,
////								AllenIntervalConstraint.Type.After.getDefaultBounds());
////								//place knife after old_on of fork
////								onculoritRegBeforeanotherNewOn.setFrom(culpritActivities.get(intersectionSet.get(i).rectangle1)); //new_on
////								onculoritRegBeforeanotherNewOn.setTo(((Activity)spatialFluent.getInternalVariables()[1])); //old_on
////								actNetwork.addConstraint(onculoritRegBeforeanotherNewOn);
////								System.out.println("--onculoritRegBeforeanotherNewOn---: " + onculoritRegBeforeanotherNewOn);
////								break;
//
//						}
//					}
//				}
//			}
//		}						
//	}

		
		return null;
	}

	@Override
	public ConstraintNetwork[] getMetaValues(MetaVariable metaVariable,
			int initial_time) {
		
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
