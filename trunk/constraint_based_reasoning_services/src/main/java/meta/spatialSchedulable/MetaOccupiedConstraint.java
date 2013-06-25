package meta.spatialSchedulable;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Vector;

import multi.activity.Activity;
import sandbox.spatial.rectangleAlgebra2.SpatialFluent;
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


	public MetaOccupiedConstraint(VariableOrderingH varOH, ValueOrderingH valOH) {
		super(varOH, valOH);
		// TODO Auto-generated constructor stub
	}
	
	public void setUsage(HashMap<String, Rectangle> old_on, HashMap<String, Rectangle> new_on) {
		
		this.old_on = new HashMap<String, Rectangle>();
		this.new_on = new HashMap<String, Rectangle>();
		this.old_on = old_on;
		this.new_on = new_on;
	}
	
	@Override
	public ConstraintNetwork[] getMetaVariables() {
		if(old_on == null)
			return null;
		else{
			System.out.println("DO STH");
		}
			
		return null;
	}

	@Override
	public ConstraintNetwork[] getMetaValues(MetaVariable metaVariable) {
		// TODO Auto-generated method stub
		return null;
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
