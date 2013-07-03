package meta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import sandbox.spatial.rectangleAlgebra2.SpatialFluent;
import sandbox.spatial.rectangleAlgebra2.SpatialFluentSolver;

import meta.simplePlanner.SimpleOperator;
import meta.simplePlanner.SimpleDomain.markings;
import meta.symbolsAndTime.Schedulable;
import multi.activity.Activity;
import multi.activity.ActivityNetwork;
import multi.activity.ActivityNetworkSolver;
import multi.allenInterval.AllenIntervalConstraint;
import framework.Constraint;
import framework.ConstraintNetwork;
import framework.ValueOrderingH;
import framework.Variable;
import framework.VariableOrderingH;
import framework.VariablePrototype;
import framework.meta.MetaConstraint;
import framework.meta.MetaVariable;

public class MetaCausalConstraint extends MetaConstraint {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5143516447467774523L;
	private Vector<SimpleOperator> operators;
	private HashMap<String,SimpleReusableResource2> resourcesMap;
	private HashMap<SimpleReusableResource2,HashMap<Activity,Integer>> currentResourceUtilizers;

	private String name;
	
	public enum markings {UNJUSTIFIED, JUSTIFIED, DIRTY, STATIC, IGNORE, PLANNED, UNPLANNED, PERMANENT};
	
	public Schedulable[] getSchedulingMetaConstraints() {
		return currentResourceUtilizers.keySet().toArray(new Schedulable[currentResourceUtilizers.keySet().size()]);
	}

	public MetaCausalConstraint(int[] capacities, String[] resourceNames, String domainName) {
		super(null, null);
		this.name = domainName;
		currentResourceUtilizers = new HashMap<SimpleReusableResource2,HashMap<Activity,Integer>>();
		resourcesMap = new HashMap<String, SimpleReusableResource2>();
		operators = new Vector<SimpleOperator>();
				
		for (int i = 0; i < capacities.length; i++) {
			//Most critical conflict is the one with most activities 
			VariableOrderingH varOH = new VariableOrderingH() {
				@Override
				public int compare(ConstraintNetwork arg0, ConstraintNetwork arg1) {
					return arg1.getVariables().length - arg0.getVariables().length;
				}
				@Override
				public void collectData(ConstraintNetwork[] allMetaVariables) { }
			};
			// no value ordering
			ValueOrderingH valOH = new ValueOrderingH() {
				@Override
				public int compare(ConstraintNetwork o1, ConstraintNetwork o2) { return 0; }
			};
			resourcesMap.put(resourceNames[i], new SimpleReusableResource2(varOH, valOH, capacities[i], this, resourceNames[i]));
		}
		
		// for every RRS just created, put it coupled with a vector of variables
		for (SimpleReusableResource2 rr : resourcesMap.values()) currentResourceUtilizers.put(rr,new HashMap<Activity, Integer>());
	}
	
	public void addOperator(SimpleOperator r) {
		operators.add(r);
	}
	
	public SimpleOperator[] getOperators() {
		return operators.toArray(new SimpleOperator[operators.size()]);
	}		

	@Override
	public ConstraintNetwork[] getMetaVariables() {
//		ActivityNetworkSolver groundSolver = (ActivityNetworkSolver)this.metaCS.getConstraintSolvers()[0];
//		(SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[0]
		Vector<ConstraintNetwork> ret = new Vector<ConstraintNetwork>();
		for (int i = 0; i < ((ActivityNetworkSolver)(((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).
				getConstraintSolvers()[1])).getVariables().length; i++) {
			if(((ActivityNetworkSolver)(((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[1])).
					getVariables()[i].getMarking().equals(markings.UNJUSTIFIED)){
//				System.out.println("inside: " +((ActivityNetworkSolver)(((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).
//						getConstraintSolvers()[1])).getVariables()[i]);
				ActivityNetwork nw = new ActivityNetwork(null);
				nw.addVariable(((ActivityNetworkSolver)(((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).
						getConstraintSolvers()[1])).getVariables()[i]);
				ret.add(nw);
			}
		}
		
		return ret.toArray(new ConstraintNetwork[ret.size()]);
	}

	private ConstraintNetwork expandOperator(SimpleOperator possibleOperator, Activity problematicActivity) {		
		ActivityNetwork activityNetworkToReturn = new ActivityNetwork(null);
		ActivityNetworkSolver groundSolver = (ActivityNetworkSolver)(((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[1]);
		String possibleOperatorHead = possibleOperator.getHead();
		String possibleOperatorSymbol = possibleOperatorHead.substring(possibleOperatorHead.indexOf("::")+2, possibleOperatorHead.length());
		String possibleOperatorComponent = possibleOperatorHead.substring(0, possibleOperatorHead.indexOf("::"));
		
		Vector<Variable> operatorTailActivitiesToInsert = new Vector<Variable>();
		
		if (possibleOperator.getRequirementActivities() != null) {
			for (String possibleOperatorTail : possibleOperator.getRequirementActivities()) {
				String possibleOperatorTailComponent = possibleOperatorTail.substring(0, possibleOperatorTail.indexOf("::"));
				String possibleOperatorTailSymbol = possibleOperatorTail.substring(possibleOperatorTail.indexOf("::")+2, possibleOperatorTail.length());
				if (possibleOperatorTailComponent.equals(possibleOperatorComponent) && possibleOperatorTailSymbol.equals(possibleOperatorSymbol)) {
					operatorTailActivitiesToInsert.add(problematicActivity);
				}
				else {
					VariablePrototype tailActivity = new VariablePrototype(
							(ActivityNetworkSolver)(((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[1]),
							possibleOperatorTailComponent, possibleOperatorTailSymbol);
					
					
//					Activity tailActivity = (Activity)((ActivityNetworkSolver)(((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).
//							getConstraintSolvers()[1])).createVariable(possibleOperatorTailComponent);
//					tailActivity.setSymbolicDomain(possibleOperatorTailSymbol);
					
					tailActivity.setMarking(markings.UNJUSTIFIED);
					operatorTailActivitiesToInsert.add(tailActivity);
				}
			}
			

			
			Vector<AllenIntervalConstraint> allenIntervalConstraintsToAdd = new Vector<AllenIntervalConstraint>();

			for (int i = 0; i < possibleOperator.getRequirementConstraints().length; i++) {
				AllenIntervalConstraint con = (AllenIntervalConstraint)possibleOperator.getRequirementConstraints()[i].clone();
				con.setFrom(problematicActivity);
				con.setTo(operatorTailActivitiesToInsert.elementAt(i));
				allenIntervalConstraintsToAdd.add(con);
			}
			for (AllenIntervalConstraint con : allenIntervalConstraintsToAdd) activityNetworkToReturn.addConstraint(con);
			
			Vector<AllenIntervalConstraint> toAddExtra = new Vector<AllenIntervalConstraint>();
			for (int i = 0; i < operatorTailActivitiesToInsert.size(); i++) {
				AllenIntervalConstraint[][] ec = possibleOperator.getExtraConstraints();
				if (ec != null) {
					AllenIntervalConstraint[] con = ec[i];
					for (int j = 0; j < con.length; j++) {
						if (con[j] != null) {
							AllenIntervalConstraint newCon = (AllenIntervalConstraint) con[j].clone();
							if (i == 0) newCon.setFrom(problematicActivity);
							else newCon.setFrom(operatorTailActivitiesToInsert.elementAt(i-1));
							if (j == 0) newCon.setTo(problematicActivity);
							else newCon.setTo(operatorTailActivitiesToInsert.elementAt(j-1));
							toAddExtra.add(newCon);
						}
					}
				}
			}

			if (!toAddExtra.isEmpty()) {
				for (AllenIntervalConstraint con : toAddExtra) activityNetworkToReturn.addConstraint(con);
			}
		}
		else if (possibleOperator.getExtraConstraints()[0][0] != null) {
			AllenIntervalConstraint ec = possibleOperator.getExtraConstraints()[0][0];
			AllenIntervalConstraint newCon = (AllenIntervalConstraint) ec.clone();
			newCon.setFrom(problematicActivity);
			newCon.setTo(problematicActivity);
			activityNetworkToReturn.addConstraint(newCon);
		}
		
		if (possibleOperator.getUsages() != null) {
			String resource = possibleOperatorSymbol.substring(possibleOperatorSymbol.indexOf("(")+1,possibleOperatorSymbol.indexOf(")"));
			String[] resourceArray = resource.split(",");
			if (!resource.equals("")) {
				for (int i = 0; i < resourceArray.length; i++) {
					String oneResource = resourceArray[i];
					HashMap<Activity, Integer> utilizers = currentResourceUtilizers.get(resourcesMap.get(oneResource));
					utilizers.put(problematicActivity, possibleOperator.getUsages()[i]);
					activityNetworkToReturn.addVariable(problematicActivity);
				}
			}
		}
		return activityNetworkToReturn;						
	}
	
	
	@Override
	public ConstraintNetwork[] getMetaValues(MetaVariable metaVariable, int initialTime) {
		return getMetaValues(metaVariable);
	}
	
	@Override
	public ConstraintNetwork[] getMetaValues(MetaVariable metaVariable) {
		Vector<ConstraintNetwork> retPossibleConstraintNetworks = new Vector<ConstraintNetwork>();
		ConstraintNetwork problematicNetwork = metaVariable.getConstraintNetwork();
		Activity problematicActivity = (Activity)problematicNetwork.getVariables()[0];
		
		//Include also unifications
		SpatialFluentSolver s = ((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]);
		ActivityNetworkSolver ans = (ActivityNetworkSolver)s.getConstraintSolvers()[1];
		for (Variable var : ans.getVariables()) {
			Activity act = (Activity)var;
			//If this unifies
			if (!act.equals(problematicActivity)) {
				if (act.getSymbolicVariable().getDomain().toString().equals(problematicActivity.getSymbolicVariable().getDomain().toString())) {
					ActivityNetwork newResolver = new ActivityNetwork(null);
					AllenIntervalConstraint unificationCon = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals);
					unificationCon.setFrom(problematicActivity);
					unificationCon.setTo(act);
					newResolver.addConstraint(unificationCon);
					retPossibleConstraintNetworks.add(newResolver);
				}
			}
		}
		
		//Include possible operator expansions
		for (SimpleOperator r : operators) {
			String problematicActivitySymbolicDomain = problematicActivity.getSymbolicVariable().getDomain().toString();
			String operatorHead = r.getHead();
			String opeatorHeadComponent = operatorHead.substring(0, operatorHead.indexOf("::"));
			String operatorHeadSymbol = operatorHead.substring(operatorHead.indexOf("::")+2, operatorHead.length());
			
			if (opeatorHeadComponent.equals(problematicActivity.getComponent())) {
				if (problematicActivitySymbolicDomain.contains(operatorHeadSymbol)) {
					ConstraintNetwork newResolver = expandOperator(r,problematicActivity);
					retPossibleConstraintNetworks.add(newResolver);
				}
			}
		}
		
		
		if (!retPossibleConstraintNetworks.isEmpty()){
//			System.out.println("== TO JUSTIFY: " + problematicActivity + " ==");
//			System.out.println("====== " + retPossibleConstraintNetworks.size() + " metaValues ======");
//			for (ConstraintNetwork cn : retPossibleConstraintNetworks) {
//				System.out.println("===> vars: " + Arrays.toString(cn.getVariables()));
//				System.out.println("===> cons: " + Arrays.toString(cn.getConstraints()));
//			}
			return retPossibleConstraintNetworks.toArray(new ConstraintNetwork[retPossibleConstraintNetworks.size()]);
		}
		
		ActivityNetwork nullActivityNetwork = new ActivityNetwork(null);
		return new ConstraintNetwork[] {nullActivityNetwork};
	}

	@Override
	public void markResolvedSub(MetaVariable con, ConstraintNetwork metaValue) {
		con.getConstraintNetwork().getVariables()[0].setMarking(markings.JUSTIFIED);
	}

	@Override
	public void draw(ConstraintNetwork network) {
		// TODO Auto-generated method stub	
	}
	
	public HashMap<String, SimpleReusableResource2> getResources() {
		return resourcesMap;
	}
	// Given a variable act, it returns all the RubReusRes that are currently exploited by the variable
	public SimpleReusableResource2[] getCurrentReusableResourcesUsedByActivity(Variable act) {
		Vector<SimpleReusableResource2> ret = new Vector<SimpleReusableResource2>();
		for (SimpleReusableResource2 rr : currentResourceUtilizers.keySet()) {
			if (currentResourceUtilizers.get(rr).containsKey(act)) 
				ret.add(rr);
		}
		return ret.toArray(new SimpleReusableResource2[ret.size()]);
	}

	public int getResourceUsageLevel(SimpleReusableResource2 rr, Variable act) {
		return currentResourceUtilizers.get(rr).get(act);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "MetaCasualConstraint " + this.name;
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
