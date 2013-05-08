package meta;

import meta.simplePlanner.SimpleDomain;
import meta.symbolsAndTime.Schedulable;
import multi.activity.Activity;
import framework.Constraint;
import framework.ConstraintNetwork;
import framework.ValueOrderingH;
import framework.VariableOrderingH;

public class SimpleReusableResource2 extends Schedulable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 2718546052018866910L;
	/**
	 * 
	 */

	private int capacity;
	private MetaCausalConstraint rd;
	private String name;
	
	public SimpleReusableResource2(VariableOrderingH varOH, ValueOrderingH valOH, int capacity, MetaCausalConstraint rd, String name) {
		super(varOH, valOH);
		this.capacity = capacity;
		this.rd = rd;
		this.name = name;
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
		return "SimpleReusableResource " + name + ", capacity = " + capacity;
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
