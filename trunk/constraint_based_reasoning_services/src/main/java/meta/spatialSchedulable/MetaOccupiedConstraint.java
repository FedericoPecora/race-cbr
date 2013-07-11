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
import multi.allenInterval.AllenIntervalConstraint;
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

	long beforeParameter = 1;
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
		
		
		Vector<SpatialFluent> unboundedsf = new Vector<SpatialFluent>();
		Vector<SpatialFluent> boundedsf = new Vector<SpatialFluent>();
		
		for (int i = 0; i < peak.length; i++) {
			if(isUnboundedBoundingBox(
							new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[0]).getEST(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[0]).getLST()),
							new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[0]).getEET(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[0]).getLET()), 
							new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[1]).getEST(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[1]).getLST()), 
							new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[1]).getEET(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularRegion().getInternalVariables()[1]).getLET()))){//it was bouneded
//				System.out.println("--isunbounded--: " + activityToFluent.get(peak[i]));
				unboundedsf.add(activityToFluent.get(peak[i]));
			}
			else{
//				System.out.println("--isbounded--: " + activityToFluent.get(peak[i]));
				boundedsf.add(activityToFluent.get(peak[i]));
			}
		}
		
		if(unboundedsf.size() == 0 || boundedsf.size() == 0) return false;
		
		Rectangle rec1 = new BoundingBox(
				new Bounds(((AllenInterval)boundedsf.get(0).getRectangularRegion().getInternalVariables()[0]).getEST(), ((AllenInterval)boundedsf.get(0).getRectangularRegion().getInternalVariables()[0]).getLST()),
				new Bounds(((AllenInterval)boundedsf.get(0).getRectangularRegion().getInternalVariables()[0]).getEET(), ((AllenInterval)boundedsf.get(0).getRectangularRegion().getInternalVariables()[0]).getLET()), 
				new Bounds(((AllenInterval)boundedsf.get(0).getRectangularRegion().getInternalVariables()[1]).getEST(), ((AllenInterval)boundedsf.get(0).getRectangularRegion().getInternalVariables()[1]).getLST()), 
				new Bounds(((AllenInterval)boundedsf.get(0).getRectangularRegion().getInternalVariables()[1]).getEET(), ((AllenInterval)boundedsf.get(0).getRectangularRegion().getInternalVariables()[1]).getLET())).getAlmostCentreRectangle();
		
		Rectangle  rec2 = null;
		for (String str : ((MetaSpatialScheduler)this.metaCS).getOldRectangularRegion().keySet()) {
			if(unboundedsf.get(0).getRectangularRegion().getName().compareTo(str) == 0)
				rec2 = ((MetaSpatialScheduler)this.metaCS).getOldRectangularRegion().get(str).getAlmostCentreRectangle();
		}
		
				
		
		if(rec1.intersects(rec2)){
			
			System.out.println("rec1: " + rec1);
			System.out.println("rec2: " + rec2);					
			return true;
		}
		
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

		ConstraintNetwork conflict = metaVariable.getConstraintNetwork();
		
		System.out.println("====MetaVaribales====");
		for (int i = 0; i < conflict.getVariables().length; i++) {
			System.out.println(conflict.getVariables()[i]);
		}
		System.out.println("====MetaVaribales====");
		
		//we know that is the result of binary conflict! so it is safe not to enumerate all, and hard coded
		Vector<ConstraintNetwork> ret = new Vector<ConstraintNetwork>();
		
		AllenIntervalConstraint before01 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(this.beforeParameter, APSPSolver.INF));
		before01.setFrom((Activity) conflict.getVariables()[0]);			
		before01.setTo((Activity) conflict.getVariables()[1]);
		ActivityNetwork resolver0 = new ActivityNetwork(((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[1]);
		resolver0.addVariable((Activity) conflict.getVariables()[0]);
		resolver0.addVariable((Activity) conflict.getVariables()[1]);
		resolver0.addConstraint(before01);
		ret.add(resolver0);
		
		AllenIntervalConstraint before10 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(this.beforeParameter, APSPSolver.INF));
		before10.setFrom((Activity) conflict.getVariables()[1]);			
		before10.setTo((Activity) conflict.getVariables()[0]);
		ActivityNetwork resolver = new ActivityNetwork(((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[1]);
		resolver.addVariable((Activity) conflict.getVariables()[1]);
		resolver.addVariable((Activity) conflict.getVariables()[0]);
		resolver.addConstraint(before10);
		ret.add(resolver);
		
		return ret.toArray(new ConstraintNetwork[ret.size()]);
		
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
		return "MetaOccupiedConstraint";
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
