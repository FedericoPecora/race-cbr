package meta.spatialSchedulable;

import java.util.Vector;

import meta.MetaCausalConstraint;
import meta.MetaSpatialFluentConstraint;
import meta.SimpleReusableResource2;
import meta.MetaCausalConstraint.markings;
import meta.symbolsAndTime.Schedulable.PEAKCOLLECTION;
import multi.activity.Activity;
import multi.activity.ActivityNetworkSolver;
import multi.allenInterval.AllenIntervalConstraint;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraint2;
import sandbox.spatial.rectangleAlgebra2.RectangularRegion2;
import sandbox.spatial.rectangleAlgebra2.SpatialFluent;
import sandbox.spatial.rectangleAlgebra2.SpatialFluentSolver;
import sandbox.spatial.rectangleAlgebra2.UnaryRectangleConstraint2;
import symbols.SymbolicValueConstraint;
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
	private static  int counter = 1;
	
	public MetaSpatialScheduler(long origin, long horizon, long animationTime) {
		super(new Class[] {RectangleConstraint2.class, UnaryRectangleConstraint2.class, AllenIntervalConstraint.class, SymbolicValueConstraint.class}, 
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
		
		if(counter == 2){
			Activity hold = null;
			
			for (int i = 0; i < groundSolver.getVariables().length; i++) {
				System.out.println("var: " + groundSolver.getConstraintSolvers()[1].getVariables()[i]);
				if(((Activity)groundSolver.getVariables()[i]).getSymbolicVariable().toString().contains("holding_cup1")){
					hold = ((Activity)groundSolver.getVariables()[i]);
				}
			}
			Activity pick = (Activity)groundSolver.createVariable("robot1");
			pick.setSymbolicDomain("pick_knife1(arm)");
			AllenIntervalConstraint oldgoalAfternewGoal = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds());
			oldgoalAfternewGoal.setFrom(hold);
			oldgoalAfternewGoal.setTo(pick);									
			groundSolver.addConstraints(new Constraint[] {oldgoalAfternewGoal});
		}	
		
		counter++;
		
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

	
}
