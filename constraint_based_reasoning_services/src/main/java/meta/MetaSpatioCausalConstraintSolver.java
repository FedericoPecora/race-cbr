package meta;

import java.util.HashMap;
import java.util.Vector;
import meta.MetaCausalConstraint.markings;
import meta.simplePlanner.SimpleDomain;
import meta.simplePlanner.SimpleReusableResource;
import multi.activity.Activity;
import multi.activity.ActivityNetworkSolver;

import multi.allenInterval.AllenIntervalConstraint;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraint2;
import sandbox.spatial.rectangleAlgebra2.RectangularRegion2;
import sandbox.spatial.rectangleAlgebra2.SpatialFluent;
import sandbox.spatial.rectangleAlgebra2.SpatialFluentSolver;
import sandbox.spatial.rectangleAlgebra2.UnaryRectangleConstraint2;
import symbols.SymbolicValueConstraint;
import framework.Constraint;
import framework.ConstraintNetwork;
import framework.Variable;
import framework.VariablePrototype;
import framework.meta.MetaConstraintSolver;
import framework.meta.MetaVariable;

public class MetaSpatioCausalConstraintSolver extends MetaConstraintSolver{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MetaSpatioCausalConstraintSolver(long origin, long horizon, long animationTime) {
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

		for (int j = 0; j < this.metaConstraints.size(); j++) 
			if(this.metaConstraints.get(j) instanceof MetaCausalConstraint ){
				MetaCausalConstraint mcc = (MetaCausalConstraint)this.metaConstraints.get(j);
				for (Variable v : activityToRemove) {
					for (SimpleReusableResource2 rr : mcc.getCurrentReusableResourcesUsedByActivity((Activity)v)) {
						rr.removeUsage((Activity)v);
					}
				}
			}



		groundSolver.removeVariables(activityToRemove.toArray(new Variable[activityToRemove.size()]));
		
		//#################################
		//this is when the whole plan is failed and we want to try the new spatial candidate
		Vector<String> oldGoal = new Vector<String>();
		Vector<String> newGoal = new Vector<String>();
		for (int i = 0; i < metaValue.getConstraints().length; i++) {
			if(metaValue.getConstraints()[i] instanceof UnaryRectangleConstraint2){
				//this if will check for unboudned obj in order to create the goal
				if(((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getType().equals(UnaryRectangleConstraint2.Type.At)){ 
					if(((MetaSpatialFluentConstraint)this.metaConstraints.get(0)).isUnboundedBoundingBox(
							((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getBounds()[0], 
							((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getBounds()[1], 
							((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getBounds()[2], 
							((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getBounds()[3])){

						for (int j = 0; j < metaVariable.getVariables().length; j++) {
							if(((RectangularRegion2)metaValue.getConstraints()[i].getScope()[0]).getName().compareTo
									(((SpatialFluent)metaVariable.getVariables()[j]).getName()) == 0){
								((Activity)((SpatialFluent)metaVariable.getVariables()[j]).getActivity()).setMarking(markings.UNJUSTIFIED);
								if(((MetaSpatialFluentConstraint)this.metaConstraints.get(0)).getPotentialCulprit()
										.contains(((RectangularRegion2)metaValue.getConstraints()[i].getScope()[0]).getName())){
//									System.out.println("HERE IS THE NEW GOAL: " + (((SpatialFluent)metaVariable.getVariables()[j]).getName()));									
									newGoal.add(((RectangularRegion2)metaValue.getConstraints()[i].getScope()[0]).getName());
								}
								else {
//									System.out.println("HERE IS THE OLD GOAL: " + ((RectangularRegion2)metaValue.getConstraints()[i].getScope()[0]).getName());
									oldGoal.add(((RectangularRegion2)metaValue.getConstraints()[i].getScope()[0]).getName());
								}
							}
						}						
					}
				}
			}
		}
		
		
		
		
		//HERE IS THE PLACE WE ADD "AFTER" ALLEN RELATION BETWEEN THE INITIAL GOAL AND GENERATED GOAL
		//FOR EXAMPLE PLACE CUP AFTER PLACING KNIFE IF KNIFE IS ONE OF THE CULPRIT
		for (int i = 0; i < metaVariable.getVariables().length; i++) {
			for (int j = 0; j < metaVariable.getVariables().length; j++) {
				if(metaVariable.getVariables()[i] instanceof SpatialFluent){
					if(oldGoal.contains((((SpatialFluent)metaVariable.getVariables()[i]).getName())) &&
							newGoal.contains((((SpatialFluent)metaVariable.getVariables()[j]).getName()))){
//						System.out.println("From: " + (((SpatialFluent)metaVariable.getVariables()[i]).getActivity()));
//						System.out.println("To: " + (((SpatialFluent)metaVariable.getVariables()[j]).getActivity()));
						
						AllenIntervalConstraint oldgoalAfternewGoal = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds());
						oldgoalAfternewGoal.setFrom((((SpatialFluent)metaVariable.getVariables()[i]).getActivity()));
						oldgoalAfternewGoal.setTo((((SpatialFluent)metaVariable.getVariables()[j]).getActivity()));									
						((SpatialFluentSolver)this.getConstraintSolvers()[0]).getConstraintSolvers()[1].addConstraints(new Constraint[] {oldgoalAfternewGoal});
					}
				}
			}			
		}
		//##################################



	}

	@Override
	protected void addResolverSub(ConstraintNetwork metaVariable, ConstraintNetwork metaValue) {

		//		System.out.println("number: "+ metaVariable.getVariables().length);
		//		for (int i = 0; i < metaVariable.getVariables().length; i++) {
		//			System.out.println(metaVariable.getVariables()[i]);
		//		}
		
		Vector<String> oldGoal = new Vector<String>();
		Vector<String> newGoal = new Vector<String>();
		for (int i = 0; i < metaValue.getConstraints().length; i++) {
			if(metaValue.getConstraints()[i] instanceof UnaryRectangleConstraint2){
				//this if will check for unboudned obj in order to create the goal
				if(((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getType().equals(UnaryRectangleConstraint2.Type.At)){ 
					if(((MetaSpatialFluentConstraint)this.metaConstraints.get(0)).isUnboundedBoundingBox(
							((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getBounds()[0], 
							((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getBounds()[1], 
							((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getBounds()[2], 
							((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getBounds()[3])){

						for (int j = 0; j < metaVariable.getVariables().length; j++) {
							if(((RectangularRegion2)metaValue.getConstraints()[i].getScope()[0]).getName().compareTo
									(((SpatialFluent)metaVariable.getVariables()[j]).getName()) == 0){
								((Activity)((SpatialFluent)metaVariable.getVariables()[j]).getActivity()).setMarking(markings.UNJUSTIFIED);
								if(((MetaSpatialFluentConstraint)this.metaConstraints.get(0)).getPotentialCulprit()
										.contains(((RectangularRegion2)metaValue.getConstraints()[i].getScope()[0]).getName())){
//									System.out.println("HERE IS THE NEW GOAL: " + (((SpatialFluent)metaVariable.getVariables()[j]).getName()));									
									newGoal.add(((RectangularRegion2)metaValue.getConstraints()[i].getScope()[0]).getName());
								}
								else {
//									System.out.println("HERE IS THE OLD GOAL: " + ((RectangularRegion2)metaValue.getConstraints()[i].getScope()[0]).getName());
									oldGoal.add(((RectangularRegion2)metaValue.getConstraints()[i].getScope()[0]).getName());
								}
							}
						}						
					}
				}
			}
		}
		
		
		
		
		//HERE IS THE PLACE WE ADD "AFTER" ALLEN RELATION BETWEEN THE INITIAL GOAL AND GENERATED GOAL
		//FOR EXAMPLE PLACE CUP AFTER PLACING KNIFE IF KNIFE IS ONE OF THE CULPRIT
		for (int i = 0; i < metaVariable.getVariables().length; i++) {
			for (int j = 0; j < metaVariable.getVariables().length; j++) {
				if(metaVariable.getVariables()[i] instanceof SpatialFluent){
					if(oldGoal.contains((((SpatialFluent)metaVariable.getVariables()[i]).getName())) &&
							newGoal.contains((((SpatialFluent)metaVariable.getVariables()[j]).getName()))){
//						System.out.println("From: " + (((SpatialFluent)metaVariable.getVariables()[i]).getActivity()));
//						System.out.println("To: " + (((SpatialFluent)metaVariable.getVariables()[j]).getActivity()));
						
						AllenIntervalConstraint oldgoalAfternewGoal = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds());
						oldgoalAfternewGoal.setFrom((((SpatialFluent)metaVariable.getVariables()[i]).getActivity()));
						oldgoalAfternewGoal.setTo((((SpatialFluent)metaVariable.getVariables()[j]).getActivity()));									
						((SpatialFluentSolver)this.getConstraintSolvers()[0]).getConstraintSolvers()[1].addConstraints(new Constraint[] {oldgoalAfternewGoal});
					}
				}
			}			
		}
		
		
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




		//Set resource usage if necessary
		//		for (Variable v : metaValue.getVariables()) {
		//			SimpleDomain sd = (SimpleDomain)this.metaConstraints.elementAt(0);
		//			for (SimpleReusableResource rr : sd.getCurrentReusableResourcesUsedByActivity(v)) {
		//				rr.setUsage((Activity)v);
		//			}
		//		}

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
