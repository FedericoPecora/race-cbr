package meta.spatialSchedulable;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Vector;
import org.metacsp.time.APSPSolver;
import org.metacsp.time.Bounds;

import org.metacsp.multi.activity.Activity;
import org.metacsp.multi.activity.ActivityNetworkSolver;
import org.metacsp.multi.allenInterval.AllenInterval;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.BoundingBox;
import org.metacsp.multi.spatioTemporal.SpatialFluent2;
import org.metacsp.multi.spatioTemporal.SpatialFluentSolver2;
import org.metacsp.framework.Constraint;
import org.metacsp.framework.ConstraintNetwork;
import org.metacsp.framework.ValueOrderingH;
import org.metacsp.framework.VariableOrderingH;
import org.metacsp.framework.meta.MetaConstraint;
import org.metacsp.framework.meta.MetaVariable;

public class MetaOccupiedConstraint2 extends MetaConstraint {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1100412278998217070L;

	private int pad = 0;

	long beforeParameter = 1;
	public MetaOccupiedConstraint2(VariableOrderingH varOH, ValueOrderingH valOH) {
		super(varOH, valOH);

	}
	
	public void setPad(int pad){
		this.pad = pad;
	}
	
	@Override
	public ConstraintNetwork[] getMetaVariables() {
		
		HashMap<Activity, SpatialFluent2> activityToFluent = new HashMap<Activity, SpatialFluent2>();
		Vector<Activity> activities = new Vector<Activity>();
		for (int i = 0; i < ((SpatialFluentSolver2)this.metaCS.getConstraintSolvers()[0]).getVariables().length; i++) {
			if(((SpatialFluent2)((SpatialFluentSolver2)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]).getRectangularCuboidRegion().getOntologicalProp().isMovable()){
				activities.add(((SpatialFluent2)((SpatialFluentSolver2)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]).getActivity());
				activityToFluent.put(((SpatialFluent2)((SpatialFluentSolver2)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]).getActivity(), 
				((SpatialFluent2)((SpatialFluentSolver2)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]));
			}
		}
		
//		System.out.println("===================================================");
//		System.out.println("activities: " + activityToFluent);
//		System.out.println("===================================================");
		
		return binaryPeakCollection(activityToFluent);
	}

	
	
	private ConstraintNetwork[] binaryPeakCollection(HashMap<Activity, SpatialFluent2> aTOsf) {
		
		
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


	private boolean isConflicting(Activity[] peak, HashMap<Activity, SpatialFluent2> activityToFluent) {
		
		if(peak.length == 1) return false;
		
//		System.out.println("_________________________________________________");
//		for (int i = 0; i < peak.length; i++) {
//			System.out.println("peak: " + activityToFluent.get(peak[i]));
//		}
//		System.out.println("_________________________________________________");
//		System.out.println("==============================================================");
		
		Vector<SpatialFluent2> unboundedsf = new Vector<SpatialFluent2>();
		Vector<SpatialFluent2> boundedsf = new Vector<SpatialFluent2>();
		//this is between unbounded object which refers to the objects in the past that has to moved, therefore neew spatial fluent generated and previous one becomes spatially unbounded 
		for (int i = 0; i < peak.length; i++) {
			if(isUnboundedBoundingBox(
							new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularCuboidRegion().getInternalVariables()[0]).getEST(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularCuboidRegion().getInternalVariables()[0]).getLST()),
							new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularCuboidRegion().getInternalVariables()[0]).getEET(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularCuboidRegion().getInternalVariables()[0]).getLET()), 
							new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularCuboidRegion().getInternalVariables()[1]).getEST(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularCuboidRegion().getInternalVariables()[1]).getLST()), 
							new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularCuboidRegion().getInternalVariables()[1]).getEET(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularCuboidRegion().getInternalVariables()[1]).getLET()),
							new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularCuboidRegion().getInternalVariables()[2]).getEST(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularCuboidRegion().getInternalVariables()[2]).getLST()), 
							new Bounds(((AllenInterval)activityToFluent.get(peak[i]).getRectangularCuboidRegion().getInternalVariables()[2]).getEET(), ((AllenInterval)activityToFluent.get(peak[i]).getRectangularCuboidRegion().getInternalVariables()[2]).getLET()))							
							 //&& (((Activity)activityToFluent.get(peak[i]).getActivity()).getTemporalVariable().getEST() != ((Activity)activityToFluent.get(peak[i]).getActivity()).getTemporalVariable().getLST())
							){//it was bouneded
//					System.out.println("--isunbounded--: " + activityToFluent.get(peak[i]));
				unboundedsf.add(activityToFluent.get(peak[i]));
			}
			else{ 
//				if (((Activity)activityToFluent.get(peak[i]).getActivity()).getTemporalVariable().getEST() != ((Activity)activityToFluent.get(peak[i]).getActivity()).getTemporalVariable().getLST())
					boundedsf.add(activityToFluent.get(peak[i]));
//			}

//				System.out.println("--isbounded--: " + activityToFluent.get(peak[i]));
				
			}
		}
		
//		System.out.println("==============================================================");
		if(unboundedsf.size() == 0 || boundedsf.size() == 0) return false;
		
		if(unboundedsf.lastElement().getName().compareTo(boundedsf.lastElement().getName()) == 0) return false; 
		
		Rectangle rec1 = new BoundingBox(
				new Bounds(((AllenInterval)boundedsf.get(0).getRectangularCuboidRegion().getInternalVariables()[0]).getEST(), ((AllenInterval)boundedsf.get(0).getRectangularCuboidRegion().getInternalVariables()[0]).getLST()),
				new Bounds(((AllenInterval)boundedsf.get(0).getRectangularCuboidRegion().getInternalVariables()[0]).getEET(), ((AllenInterval)boundedsf.get(0).getRectangularCuboidRegion().getInternalVariables()[0]).getLET()), 
				new Bounds(((AllenInterval)boundedsf.get(0).getRectangularCuboidRegion().getInternalVariables()[1]).getEST(), ((AllenInterval)boundedsf.get(0).getRectangularCuboidRegion().getInternalVariables()[1]).getLST()), 
				new Bounds(((AllenInterval)boundedsf.get(0).getRectangularCuboidRegion().getInternalVariables()[1]).getEET(), ((AllenInterval)boundedsf.get(0).getRectangularCuboidRegion().getInternalVariables()[1]).getLET())
				).getAlmostCentreRectangle();
//		System.out.println("rec1: " + boundedsf.get(0)..getRectangularCuboidRegion());
		Rectangle  rec2 = null;
		for (String str : ((MetaSpatialScheduler2)this.metaCS).getOldBlockRegion().keySet()) {
			if(unboundedsf.get(0).getRectangularCuboidRegion().getName().compareTo(str) == 0){
				rec2 = ((MetaSpatialScheduler2)this.metaCS).getOldBlockRegion().get(str).getAlmostCentreRectangle();
//				System.out.println("rec2: " + unboundedsf.get(0)..getRectangularCuboidRegion());
			}
		}
		
		Rectangle r1new = new Rectangle(((int)rec1.getMinX()) - pad, ((int)rec1.getMinY()) - pad, (int)rec1.getWidth() + (2 * pad), (int)rec1.getHeight() + (2 * pad));
		Rectangle r2new = new Rectangle(((int)rec2.getMinX()) - pad, ((int)rec2.getMinY()) - pad, (int)rec2.getWidth() + (2 * pad), (int)rec2.getHeight() + (2 * pad));
		
//		System.out.println("=================================================");
//		System.out.println("rec1: " + rec1);
//		System.out.println("rec1new: " + r1new);
//		System.out.println("rec2: " + rec2);
//		System.out.println("rec2new: " + r2new);
//		System.out.println("=================================================");
		
		if(r1new.intersects(r2new)){
//			System.out.println("--These are conflicting--");
//			System.out.println("---------------------------");
			return true;
		}
		else{
//			System.out.println("These are not conflicting");
//			System.out.println("---------------------------");
		}
		
		

		
		return false;
	}
	

	private boolean isUnboundedBoundingBox(Bounds xLB, Bounds xUB, Bounds yLB, Bounds yUB, Bounds zLB, Bounds zUB) {

		long horizon = ((ActivityNetworkSolver)((SpatialFluentSolver2)this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[1]).getHorizon();
		
		if( (xLB.min == 0 && xLB.max == horizon) && (xUB.min == 0&& xUB.max == horizon) &&
			(yLB.min == 0 && yLB.max == horizon) &&(yLB.min == 0 && yUB.max == horizon) &&
			(zLB.min == 0 && zLB.max == horizon) &&(zLB.min == 0 && zUB.max == horizon))
			return true;
			
		return false;
	}


	@Override
	public ConstraintNetwork[] getMetaValues(MetaVariable metaVariable) {

		ConstraintNetwork conflict = metaVariable.getConstraintNetwork();
		//we know that is the result of binary conflict! so it is safe not to enumerate all, and hard coded
		Vector<ConstraintNetwork> ret = new Vector<ConstraintNetwork>();
		
		AllenIntervalConstraint before01 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(beforeParameter, APSPSolver.INF));
		before01.setFrom((Activity) conflict.getVariables()[0]);			
		before01.setTo((Activity) conflict.getVariables()[1]);
		ConstraintNetwork resolver0 = new ConstraintNetwork(((SpatialFluentSolver2)this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[1]);
		resolver0.addVariable((Activity) conflict.getVariables()[0]);
		resolver0.addVariable((Activity) conflict.getVariables()[1]);
		resolver0.addConstraint(before01);
		ret.add(resolver0);
		
		AllenIntervalConstraint before10 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(beforeParameter, APSPSolver.INF));
		before10.setFrom((Activity) conflict.getVariables()[1]);			
		before10.setTo((Activity) conflict.getVariables()[0]);
		ConstraintNetwork resolver = new ConstraintNetwork(((SpatialFluentSolver2)this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[1]);
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
