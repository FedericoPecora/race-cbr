package meta.spatialSchedulable;

import java.util.Vector;

import org.metacsp.time.APSPSolver;
import org.metacsp.time.Bounds;
import org.metacsp.meta.symbolsAndTime.MCSData;
import org.metacsp.meta.symbolsAndTime.Schedulable;
import org.metacsp.multi.activity.Activity;

import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.framework.Constraint;
import org.metacsp.framework.ConstraintNetwork;
import org.metacsp.framework.ValueOrderingH;
import org.metacsp.framework.VariableOrderingH;
import org.metacsp.framework.meta.MetaVariable;

public class SimpleReusableResource3 extends Schedulable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 2718546052018866910L;
	/**
	 * 
	 */

	private int capacity;
	private MetaCausalConstraint2 rd;
	private String name;
	
	public SimpleReusableResource3(VariableOrderingH varOH, ValueOrderingH valOH, int capacity, MetaCausalConstraint2 rd, String name) {
		super(varOH, valOH);
		this.capacity = capacity;
		this.rd = rd;
		this.name = name;
//		setPeakCollectionStrategy(peakCollectionStrategy.COMPLETE);
	}
	
	long beforeParameter = 1;
	
	@Override
	public ConstraintNetwork[] getMetaValues(MetaVariable metaVariable) {	
		
		ConstraintNetwork conflict = metaVariable.getConstraintNetwork();
		MCSData[] mcsinfo = getOrderedMCSs(conflict);
		
		Vector<ConstraintNetwork> ret = new Vector<ConstraintNetwork>();
		if(mcsinfo == null) //unresolvabe MCS: no solution can be found
		{				
			//System.out.println("ESTA Fails: unresolvable MCS.");
			return null;
		}
		
		for (MCSData mcs : mcsinfo) {
			AllenIntervalConstraint before = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(beforeParameter, APSPSolver.INF));
			before.setFrom(mcs.mcsActFrom);			
			before.setTo(mcs.mcsActTo);
			ConstraintNetwork resolver = new ConstraintNetwork(mcs.mcsActFrom.getConstraintSolver());
			resolver.addVariable(mcs.mcsActFrom);
			resolver.addVariable(mcs.mcsActTo);
			resolver.addConstraint(before);
			ret.add(resolver);
		}
		
//		ActivityNetwork resolver = new ActivityNetwork(((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[1]);
//		Activity two = (Activity)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[1].createVariable("robot1");
//		two.setSymbolicDomain("on_cup1_tray1()");
//		two.setMarking(markings.UNJUSTIFIED);
//		resolver.addVariable(two);
//		ret.add(resolver);
		
		
		
		return ret.toArray(new ConstraintNetwork[ret.size()]);

	}

	
	@Override
	public boolean isConflicting(Activity[] peak) {
		int sum = 0;
		for (Activity act : peak) {
			sum += rd.getResourceUsageLevel(this, act);
			if (sum > capacity) return true;
		}
		return false;
	}

	@Override
	public void draw(ConstraintNetwork network) {
		// TODO Auto-generated method stub
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "SimpleReusableResource2 " + name + ", capacity = " + capacity;
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
