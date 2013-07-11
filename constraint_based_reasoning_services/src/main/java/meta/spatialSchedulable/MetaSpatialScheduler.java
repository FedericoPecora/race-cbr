package meta.spatialSchedulable;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Vector;

import meta.MetaCausalConstraint;
import meta.MetaSpatialFluentConstraint;
import meta.SimpleReusableResource2;
import meta.MetaCausalConstraint.markings;
import meta.symbolsAndTime.Schedulable.PEAKCOLLECTION;
import multi.activity.Activity;
import multi.activity.ActivityNetworkSolver;
import multi.allenInterval.AllenIntervalConstraint;
import multi.spatial.rectangleAlgebra.BoundingBox;
import multi.spatial.rectangleAlgebra.RectangleConstraint;
import multi.spatial.rectangleAlgebra.RectangularRegion;
import multi.spatial.rectangleAlgebra.UnaryRectangleConstraint;
import multi.spatioTemporal.SpatialFluentSolver;

import spatial.utility.SpatialAssertionalRelation2;

import symbols.SymbolicValueConstraint;
import time.APSPSolver;
import time.Bounds;
import framework.Constraint;
import framework.ConstraintNetwork;
import framework.Variable;
import framework.VariablePrototype;
import framework.meta.MetaConstraintSolver;
import framework.meta.MetaVariable;

public class MetaSpatialScheduler  extends MetaConstraintSolver{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MetaSpatialScheduler(long origin, long horizon, long animationTime) {
		super(new Class[] {RectangleConstraint.class, UnaryRectangleConstraint.class, AllenIntervalConstraint.class, SymbolicValueConstraint.class}, 
				animationTime, new SpatialFluentSolver(origin, horizon)	);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void preBacktrack() {
		// TODO Auto-generated method stub

	}

	@Override
	public void postBacktrack(MetaVariable metaVariable) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void retractResolverSub(ConstraintNetwork metaVariable,
			ConstraintNetwork metaValue) {

		ActivityNetworkSolver groundSolver = (ActivityNetworkSolver)((SpatialFluentSolver)this.getConstraintSolvers()[0]).getConstraintSolvers()[1];
		Vector<Variable> activityToRemove = new Vector<Variable>();

		for (Variable v : metaValue.getVariables()) {
			if (!metaVariable.containsVariable(v)) {
				if (v instanceof VariablePrototype) {
					Variable vReal = metaValue.getSubstitution((VariablePrototype)v);
					if (vReal != null) {
						activityToRemove.add(vReal);
					}
				}
			}
		}

		for (int j = 0; j < this.metaConstraints.size(); j++){ 
			if(this.metaConstraints.get(j) instanceof MetaCausalConstraint ){
				MetaCausalConstraint mcc = (MetaCausalConstraint)this.metaConstraints.get(j);
				for (Variable v : activityToRemove) {
					for (SimpleReusableResource2 rr : mcc.getCurrentReusableResourcesUsedByActivity((Activity)v)) {
						rr.removeUsage((Activity)v);
					}
				}
			}
		}
		
		boolean isRtractingSpatialRelations = false;
		for (int i = 0; i < metaValue.getVariables().length; i++) {
			if(metaValue.getVariables()[i] instanceof RectangularRegion ){
				isRtractingSpatialRelations = true;
				break;
			}
		}
					
		
		if(isRtractingSpatialRelations){
			System.out.println("Meta Value of MetaSpatialConstraint is retracted");
			for (int i = 0; i < this.metaConstraints.size(); i++){
				if(this.metaConstraints.get(i) instanceof SpatialSchedulable ){	
					for (int j = 0; j < ((SpatialSchedulable)this.metaConstraints.get(i)).getsAssertionalRels().length; j++) {
							((SpatialSchedulable)this.metaConstraints.get(i)).getsAssertionalRels()[j].setUnaryAtRectangleConstraint
							(((SpatialSchedulable)this.metaConstraints.get(i)).getCurrentAssertionalCons().
									get(((SpatialSchedulable)this.metaConstraints.get(i)).getsAssertionalRels()[j].getFrom()));
					}			
				}
			}
		}
		

		groundSolver.removeVariables(activityToRemove.toArray(new Variable[activityToRemove.size()]));
		
		
	}

	@Override
	protected void addResolverSub(ConstraintNetwork metaVariable, ConstraintNetwork metaValue) {
		
		ActivityNetworkSolver groundSolver = (ActivityNetworkSolver)((SpatialFluentSolver)this.getConstraintSolvers()[0]).getConstraintSolvers()[1];

		//Make real variables from variable prototypes
		for (Variable v :  metaValue.getVariables()) {
			if (v instanceof VariablePrototype) {
				// 	Parameters for real instantiation: the first is the component itself, the second is
				//	the symbol of the Activity to be instantiated
				String component = (String)((VariablePrototype) v).getParameters()[0];
				String symbol = (String)((VariablePrototype) v).getParameters()[1];
				Activity tailActivity = (Activity)groundSolver.createVariable(component);
				tailActivity.setSymbolicDomain(symbol);
				tailActivity.setMarking(v.getMarking());
				metaValue.addSubstitution((VariablePrototype)v, tailActivity);
			}
		}
		

		
		//Involve real variables in the constraints
		for (Constraint con : metaValue.getConstraints()) {
			Constraint clonedConstraint = (Constraint)con.clone();  
			Variable[] oldScope = con.getScope();
			Variable[] newScope = new Variable[oldScope.length];
			for (int i = 0; i < oldScope.length; i++) {
				if (oldScope[i] instanceof VariablePrototype) newScope[i] = metaValue.getSubstitution((VariablePrototype)oldScope[i]);
				else newScope[i] = oldScope[i];
			}
			clonedConstraint.setScope(newScope);
			metaValue.removeConstraint(con);
			metaValue.addConstraint(clonedConstraint);
		}
		

//		for (int j = 0; j < this.metaConstraints.size(); j++) {
//			if(this.metaConstraints.get(j) instanceof SpatialSchedulable ){
//				HashMap<String, Rectangle> old_on = ((SpatialSchedulable)this.metaConstraints.elementAt(j)).getPreviosRectangularRegion();
//				HashMap<String, Rectangle> new_on = ((SpatialSchedulable)this.metaConstraints.elementAt(j)).getUpdatedRectangularRegion();
////				System.out.println("old: " + old_on);
////				System.out.println("new: " + new_on);
//				for (int j2 = 0; j2 < this.metaConstraints.size(); j2++) {
//					if(this.metaConstraints.get(j2) instanceof MetaOccupiedConstraint){
//						((MetaOccupiedConstraint)this.metaConstraints.elementAt(j2)).setUsage(old_on, new_on);
//					}
//				}
//			}
//		}
		
		for (Variable v : metaValue.getVariables()) {
			for (int j = 0; j < this.metaConstraints.size(); j++) {
				if(this.metaConstraints.get(j) instanceof MetaCausalConstraint ){
					MetaCausalConstraint metaCausalConatraint = (MetaCausalConstraint)this.metaConstraints.elementAt(j);
					for (SimpleReusableResource2 rr : metaCausalConatraint.getCurrentReusableResourcesUsedByActivity(v)) {
						rr.setUsage((Activity)v);
					}
				}
			}
		}
	}
		

	@Override
	protected double getUpperBound() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void setUpperBound() {
		// TODO Auto-generated method stub

	}

	@Override
	protected double getLowerBound() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void setLowerBound() {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean hasConflictClause(ConstraintNetwork metaValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void resetFalseClause() {
		// TODO Auto-generated method stub

	}
	
	public HashMap<String, BoundingBox> getOldRectangularRegion(){
		
		
		for (int j = 0; j < this.metaConstraints.size(); j++){ 
			if(this.metaConstraints.get(j) instanceof SpatialSchedulable ){
				return ((SpatialSchedulable)this.metaConstraints.get(j)).getOldRectangularRegion();
			}
		}
		
		
		return null;
	}

	
}
