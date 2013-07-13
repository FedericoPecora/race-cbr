package meta.spatialSchedulable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.awt.Rectangle;

import orbital.algorithm.Combinatorical;

import spatial.utility.SpatialAssertionalRelation2;
import spatial.utility.SpatialRule2;
import time.APSPSolver;
import time.Bounds;
import framework.Constraint;
import framework.ConstraintNetwork;
import framework.ConstraintSolver;
import framework.ValueOrderingH;
import framework.VariableOrderingH;
import framework.meta.MetaConstraint;
import framework.meta.MetaVariable;
import framework.multi.MultiBinaryConstraint;
import meta.MetaCausalConstraint.markings;
import meta.simplePlanner.SimpleOperator;
import multi.activity.Activity;
import multi.activity.ActivityComparator;
import multi.activity.ActivityNetwork;
import multi.allenInterval.AllenInterval;
import multi.allenInterval.AllenIntervalConstraint;
import multi.allenInterval.AllenIntervalNetworkSolver;
import multi.spatial.rectangleAlgebra.BoundingBox;
import multi.spatial.rectangleAlgebra.RectangleConstraint;
import multi.spatial.rectangleAlgebra.RectangleConstraintNetwork;
import multi.spatial.rectangleAlgebra.RectangleConstraintSolver;
import multi.spatial.rectangleAlgebra.RectangularRegion;
import multi.spatial.rectangleAlgebra.UnaryRectangleConstraint;
import multi.spatioTemporal.SpatialFluent;
import multi.spatioTemporal.SpatialFluentSolver;

public class SpatialSchedulable extends MetaConstraint {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1583183627952907595L;
	private long origin = 0, horizon = 1000;
	private SpatialAssertionalRelation2[] sAssertionalRels;
	private SpatialRule2[] rules;
	private HashMap<HashMap<String, Bounds[]>, Integer> permutation;
	private Vector<String> initialUnboundedObjName = new Vector<String>();
	private Vector<String> potentialCulprit = new Vector<String>(); 
	private Vector<SimpleOperator> operators = new Vector<SimpleOperator>();
	private HashMap<String, UnaryRectangleConstraint> currentAssertionalCons;
	private Vector<HashMap<String, BoundingBox>> newRectangularRegion = null;
	private HashMap<String, BoundingBox> oldRectangularRegion = null;
//	private HashMap<Activity, SpatialFluent> activityToFluent = new HashMap<Activity, SpatialFluent>();
//	private HashMap<Activity, SpatialFluent> activityToFluent;
//	protected Vector<Activity> activities;
	
	public HashMap<String, UnaryRectangleConstraint> getCurrentAssertionalCons(){
		return currentAssertionalCons;
	}
		
	public void addOperator(SimpleOperator r) {
		operators.add(r);
	}
	
	public HashMap<String, BoundingBox> getOldRectangularRegion(){
		return oldRectangularRegion;
	}
	
	
	public SpatialSchedulable(VariableOrderingH varOH, ValueOrderingH valOH) {
		super(varOH, valOH);
		this.beforeParameter = 1;
	}

	public void setSpatialRules(SpatialRule2... rules) {
		this.rules = new SpatialRule2[rules.length];
		this.rules = rules;
	}

	public void setSpatialAssertionalRelations(SpatialAssertionalRelation2... sAssertionalRels) {
		this.sAssertionalRels = new SpatialAssertionalRelation2[sAssertionalRels.length];
		this.sAssertionalRels = sAssertionalRels;
	}
	
	public SpatialAssertionalRelation2[] getsAssertionalRels() {
		return sAssertionalRels;
	}
	

	public int getBeforeParameter() {
		return beforeParameter;
	}

	public void setBeforeParameter(int beforeParameter) {
		this.beforeParameter = beforeParameter;
	}

	int beforeParameter;

	public PEAKCOLLECTION getPeakCollectionStrategy() {
		return peakCollectionStrategy;
	}

	public void setPeakCollectionStrategy(PEAKCOLLECTION peakCollectionStrategy) {
		this.peakCollectionStrategy = peakCollectionStrategy;
	}

	

	public static enum PEAKCOLLECTION {
		SAMPLING, COMPLETE, BINARY
	};

	protected PEAKCOLLECTION peakCollectionStrategy = PEAKCOLLECTION.SAMPLING;

	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
		Set<Set<T>> sets = new HashSet<Set<T>>();
		if (originalSet.isEmpty()) {
			sets.add(new HashSet<T>());
			return sets;
		}
		List<T> list = new ArrayList<T>(originalSet);
		T head = list.get(0);
		Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
		for (Set<T> set : powerSet(rest)) {
			Set<T> newSet = new HashSet<T>();
			newSet.add(head);
			newSet.addAll(set);
			sets.add(newSet);
			sets.add(set);
		}
		return sets;
	}
	


	
	// Finds sets of overlapping activities and assesses whether they are
	// conflicting (e.g., over-consuming a resource)
	private ConstraintNetwork[] samplingPeakCollection(HashMap<Activity, SpatialFluent> aTOsf) {
		
		Vector<Activity> activities = new Vector<Activity>();
		for (Activity act : aTOsf.keySet()) {
			activities.add(act);
		}
		if (activities != null && !activities.isEmpty()) {
			
 			Activity[] groundVars = activities.toArray(new Activity[activities.size()]);			
			Arrays.sort(groundVars, new ActivityComparator(true));
			Vector<ConstraintNetwork> ret = new Vector<ConstraintNetwork>();
			Vector<Vector<Activity>> overlappingAll = new Vector<Vector<Activity>>();

			// if an activity is spatially inconsistent even with itself
			for (Activity act : activities) {
				if (isConflicting(new Activity[] { act }, aTOsf)) {
					ActivityNetwork temp = new ActivityNetwork(null);
					temp.addVariable(act);
					ret.add(temp);
				}
			}

			// groundVars are ordered activities
			for (int i = 0; i < groundVars.length; i++) {
				Vector<Activity> overlapping = new Vector<Activity>();
				overlapping.add(groundVars[i]);
				long start = (groundVars[i]).getTemporalVariable().getEST();
				long end = (groundVars[i]).getTemporalVariable().getEET();
				Bounds intersection = new Bounds(start, end);
//				System.out.println("intersection1: " + groundVars[i] + " " +intersection);
				for (int j = 0; j < groundVars.length; j++) {
					if (i != j) {
						start = (groundVars[j]).getTemporalVariable().getEST();
						end = (groundVars[j]).getTemporalVariable().getEET();
						Bounds nextInterval = new Bounds(start, end);
//						 System.out.println("nextinterval: " + groundVars[j] + " " +nextInterval);
//						 System.out.println("____________________________________");
						Bounds intersectionNew = intersection.intersectStrict(nextInterval);
						if (intersectionNew != null) {
							overlapping.add(groundVars[j]);
							if (isConflicting(overlapping.toArray(new Activity[overlapping.size()]), aTOsf)) {
								overlappingAll.add(overlapping);
								break;
							}
							else
								intersection = intersectionNew;
						}
					}
				}
			}
			if(overlappingAll.size() > 0){
				Vector<Vector<Activity>> retActivities = new Vector<Vector<Activity>>();
				Vector<Activity>  current = overlappingAll.get(0);
				for (int i = 1; i < overlappingAll.size(); i++) {
					if(!isEqual(current, overlappingAll.get(i))){
						retActivities.add(current);
						current = overlappingAll.get(i);
					}
				}
				retActivities.add(current);
				
				for (Vector<Activity> actVec : retActivities) {
					ActivityNetwork tmp = new ActivityNetwork(null);
					for (Activity act : actVec){
						tmp.addVariable(act);
					}
					ret.add(tmp);					
				}
				
//				System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//				for (int i = 0; i < ret.size(); i++) {
//					System.out.println("ret: " + ret);
//				}
//				System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
				
//				return new ConstraintNetwork[]{ret.size()};
				return ret.toArray(new ConstraintNetwork[ret.size()]);
			}
		}
		return (new ConstraintNetwork[0]);
	}


	private boolean isEqual(Vector<Activity> current, Vector<Activity> next) {
		
		if(current.size() != next.size()) return false;
		
		int[] nextId = new int[next.size()]; 
		int[] currentId = new int[current.size()];
		for (int i = 0; i < next.size(); i++){ 
			nextId[i] = next.get(i).getID();
			currentId[i] = current.get(i).getID();
		}
		
		Arrays.sort(nextId);
		Arrays.sort(currentId);
		
		for (int i = 0; i < currentId.length; i++) {
			if(currentId[i] != nextId[i])
				return false;
		}
		
		return true;
	}


	@Override
	public ConstraintNetwork[] getMetaVariables() {
		HashMap<Activity, SpatialFluent> activityToFluent = new HashMap<Activity, SpatialFluent>();
		Vector<Activity> activities = new Vector<Activity>();
		for (int i = 0; i < ((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables().length; i++) {
			activities.add(((SpatialFluent)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]).getActivity());
			activityToFluent.put(((SpatialFluent)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]).getActivity(), 
				((SpatialFluent)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]));
			
		}
		
//		System.out.println("==========================================================================");
//		System.out.println("activityToFluent: " + activityToFluent);
//		System.out.println("==========================================================================");
		
		return samplingPeakCollection(activityToFluent);
//		return completePeakCollection(activityToFluent);
	}

	@Override
	public ConstraintNetwork[] getMetaValues(MetaVariable metaVariable,
			int initialTime) {
		return getMetaValues(metaVariable);
	}

	@Override
	public ConstraintNetwork[] getMetaValues(MetaVariable metaVariable) {

		if (metaVariable == null)
			return null;
		//#######################################################################################################
		HashMap<Activity, SpatialFluent> activityToFluent = new HashMap<Activity, SpatialFluent>();
		Vector<Activity> activities = new Vector<Activity>();
		for (int i = 0; i < ((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables().length; i++) {
			activities.add(((SpatialFluent)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]).getActivity());
			activityToFluent.put(((SpatialFluent)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]).getActivity(), 
				((SpatialFluent)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables()[i]));
			
		}
		//########################################################################################################
		permutation = new HashMap<HashMap<String, Bounds[]>, Integer>();
		Vector<ConstraintNetwork> ret = new Vector<ConstraintNetwork>();
		RectangleConstraintNetwork mvalue = new RectangleConstraintNetwork(this.metaCS.getConstraintSolvers()[0]);
		ConstraintNetwork conflict = metaVariable.getConstraintNetwork();
		Vector<SpatialFluent> conflictvars = new Vector<SpatialFluent>();
		Vector<RectangularRegion> conflictRecvars = new Vector<RectangularRegion>();
		HashMap<String, RectangularRegion> getVariableByName = new HashMap<String, RectangularRegion>();
		newRectangularRegion = new Vector<HashMap<String, BoundingBox>>(); //this is for tracking the alternative position and check whether the new places is already overlapped by previous places 
		oldRectangularRegion = new HashMap<String, BoundingBox>();
		
		//#########################################################################################################
		
		for (int j = 0; j < conflict.getVariables().length; j++) {
			conflictvars.add(activityToFluent.get((Activity) conflict.getVariables()[j]));
			conflictRecvars.add(activityToFluent.get((Activity) conflict.getVariables()[j]).getRectangularRegion());
		}
		
		
		
		
		setPermutationHashMAP(conflictvars, conflictRecvars);//it only generate permutation, does not perform any propagation
		Vector<HashMap<String, Bounds[]>> alternativeSets = generateAllAlternativeSet(conflictRecvars);//it ranks the alternative
		HashMap<String, Bounds[]> alternativeSet = alternativeSets.get(0);
		
		
		
		// TBOX general knowledge in RectangleCN
		mvalue.join(createTBOXspatialNetwork(((SpatialFluentSolver) this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[0],getVariableByName)); 
		// Att At cpnstraint
		Vector<RectangularRegion> metaVaribales = new Vector<RectangularRegion>();		
		for (SpatialFluent var : conflictvars) {
			Bounds[] atBounds = new Bounds[alternativeSet.get(var.getRectangularRegion().getName()).length];
			for (int j = 0; j < atBounds.length; j++) {
				Bounds at = new Bounds(alternativeSet.get(var.getName())[j].min,alternativeSet.get(var.getName())[j].max);
				atBounds[j] = at;
			}
			UnaryRectangleConstraint atCon = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, atBounds);
			atCon.setFrom(var.getRectangularRegion());
			atCon.setTo(var.getRectangularRegion());

			metaVaribales.add(var.getRectangularRegion());
			mvalue.addConstraint(atCon);
			mvalue.addVariable(var.getRectangularRegion());
		}
		
		
		//###########################################################################################################
//		logger.finest("pregenerated meta value for scoring: " + mvalue);

//		System.out.println("mvalue" + mvalue);
		Vector<String> newGoal = new Vector<String>();
		HashMap<String, Activity> culpritActivities = new HashMap<String, Activity>(); 
		
		for (int i = 0; i < mvalue.getConstraints().length; i++) {
			if (mvalue.getConstraints()[i] instanceof UnaryRectangleConstraint) {
				// this if will check for unboudned obj in order to create the goal
				if (((UnaryRectangleConstraint) mvalue.getConstraints()[i])
						.getType().equals(UnaryRectangleConstraint.Type.At)) {
					if (this.isUnboundedBoundingBox(((UnaryRectangleConstraint) mvalue.getConstraints()[i]).getBounds()[0],
							((UnaryRectangleConstraint) mvalue.getConstraints()[i]).getBounds()[1],
							((UnaryRectangleConstraint) mvalue.getConstraints()[i]).getBounds()[2],
							((UnaryRectangleConstraint) mvalue.getConstraints()[i]).getBounds()[3])) {
						System.out.println("%%%%%%%%");
						for (int j = 0; j < metaVariable.getConstraintNetwork().getVariables().length; j++) {
							if (((RectangularRegion) mvalue.getConstraints()[i].getScope()[0]).getName().compareTo
									(((SpatialFluent) activityToFluent.get((Activity)metaVariable.getConstraintNetwork().getVariables()[j]) ).getName()) == 0) {
//								System.out.println("ADDED ACTIVITY: " + ((Activity)(metaVariable.getConstraintNetwork().getVariables()[j])));								
								if (this.getPotentialCulprit().contains(((RectangularRegion) mvalue.getConstraints()[i].getScope()[0]).getName())) {
									
									
									if(((Activity)(metaVariable.getConstraintNetwork().getVariables()[j])).getTemporalVariable().getEST() == 
											((Activity)(metaVariable.getConstraintNetwork().getVariables()[j])).getTemporalVariable().getLST()){
										System.out.println(((RectangularRegion)mvalue.getConstraints()[i].getScope()[0]).getName());
//										System.out.println("==== " + ((Activity)(metaVariable.getConstraintNetwork().getVariables()[j])));
										culpritActivities.put(((RectangularRegion)mvalue.getConstraints()[i].getScope()[0]).getName(), 
												((Activity)(metaVariable.getConstraintNetwork().getVariables()[j])));
										if(!newGoal.contains(((RectangularRegion) mvalue.getConstraints()[i].getScope()[0]).getName()))
											newGoal.add(((RectangularRegion) mvalue.getConstraints()[i].getScope()[0]).getName());
									}
								}
							}
						}
					}
				}						
			}
		}
		System.out.println("%%%%%%%%");

		
		

		//extract the fluent which is relevant to the original goal(s)
		Vector<Activity> originalGoals = new Vector<Activity>(); //e.g., cup1 in the so called wellSetTable Scenario
		for (Activity act : activityToFluent.keySet()) {
			if(initialUnboundedObjName.contains(activityToFluent.get(act).getName()))
				originalGoals.add(act);
		}
		//maintain the the current At unary constraint for retraction case
		currentAssertionalCons = new HashMap<String, UnaryRectangleConstraint>();
		Vector<String> nonMovableObj = new Vector<String>();
		for (int j = 0; j < sAssertionalRels.length; j++) {
			if(!sAssertionalRels[j].getOntologicalProp().isMovable()) {
				nonMovableObj.add(sAssertionalRels[j].getFrom());
				nonMovableObj.add(sAssertionalRels[j].getTo());
			}
			
			currentAssertionalCons.put(sAssertionalRels[j].getFrom(), new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At,
					new Bounds(sAssertionalRels[j].getUnaryAtRectangleConstraint().getBounds()[0].min, sAssertionalRels[j].getUnaryAtRectangleConstraint().getBounds()[0].max), 
					new Bounds(sAssertionalRels[j].getUnaryAtRectangleConstraint().getBounds()[1].min, sAssertionalRels[j].getUnaryAtRectangleConstraint().getBounds()[1].max),
					new Bounds(sAssertionalRels[j].getUnaryAtRectangleConstraint().getBounds()[2].min, sAssertionalRels[j].getUnaryAtRectangleConstraint().getBounds()[2].max),
					new Bounds(sAssertionalRels[j].getUnaryAtRectangleConstraint().getBounds()[3].min, sAssertionalRels[j].getUnaryAtRectangleConstraint().getBounds()[3].max)));
			
			BoundingBox bb = new BoundingBox(sAssertionalRels[j].getUnaryAtRectangleConstraint().getBounds()[0], 
					sAssertionalRels[j].getUnaryAtRectangleConstraint().getBounds()[1],
					sAssertionalRels[j].getUnaryAtRectangleConstraint().getBounds()[2],
					sAssertionalRels[j].getUnaryAtRectangleConstraint().getBounds()[3]);
			oldRectangularRegion.put(sAssertionalRels[j].getFrom(), bb);

		}
		
		Vector<SpatialFluent> newGoalFluentsVector = new Vector<SpatialFluent>();
		ActivityNetwork actNetwork = new ActivityNetwork(((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0])).getConstraintSolvers()[1]);
		//set new Goal After old activity
		for (String st :newGoal) {
			//add new fluent if there is not already a fluent which represent the goal, 
			//we make a difference between a fluent which has an activity with fixed release point (i.e., observation) 
			//and with the one already generated subgoal and for some other reason (e.g., resources) retracted
			SpatialFluent newgoalFlunet = null;
			boolean existed = false;
			for (Activity activity : activityToFluent.keySet()) { //this if should be deleted
				if(activity.getSymbolicVariable().getDomain().toString().contains(st) &&
						activity.getTemporalVariable().getEST() != activity.getTemporalVariable().getLST()){
					
					newgoalFlunet = activityToFluent.get(activity);
					((Activity)newgoalFlunet.getInternalVariables()[1]).setMarking(markings.UNJUSTIFIED);
					existed = true;
				}
			}
			if(!existed){
				newgoalFlunet = (SpatialFluent)((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0]))
						.createVariable(culpritActivities.get(st).getComponent());
				newgoalFlunet.setName(st);
				((Activity)newgoalFlunet.getInternalVariables()[1]).setSymbolicDomain(culpritActivities.get(st).getSymbolicVariable().toString()
						.subSequence(21, ((Activity)culpritActivities.get(st)).getSymbolicVariable().toString().length() - 1).toString());
				((Activity)newgoalFlunet.getInternalVariables()[1]).setMarking(markings.UNJUSTIFIED);
				((RectangularRegion)newgoalFlunet.getInternalVariables()[0]).setName(st);
				mvalue.addVariable(newgoalFlunet);
				activityToFluent.put(((Activity)newgoalFlunet.getInternalVariables()[1]), newgoalFlunet);
			}
			
			newGoalFluentsVector.add(newgoalFlunet);
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			//update assertional rule
			for (int j = 0; j < sAssertionalRels.length; j++) {
				if(sAssertionalRels[j].getFrom().compareTo(st) == 0)
				sAssertionalRels[j].setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
						new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
			}			
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			if(!activities.contains(((Activity)newgoalFlunet.getInternalVariables()[1])))
				activities.add(((Activity)newgoalFlunet.getInternalVariables()[1]));
			AllenIntervalConstraint newOnAfteroldOn = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After,
					 AllenIntervalConstraint.Type.After.getDefaultBounds());
			newOnAfteroldOn.setFrom(((Activity)newgoalFlunet.getInternalVariables()[1]));
			newOnAfteroldOn.setTo(culpritActivities.get(st));
			
			//((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0])).getConstraintSolvers()[1].addConstraints(new
//					 Constraint[] {newOnAfteroldOn});
			actNetwork.addConstraint(newOnAfteroldOn);			
			System.out.println("newOnAfteroldOn" + newOnAfteroldOn);
			
			
			//basically this commeneted block is not needed in the case of states overlappedby action
//			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//			//new goal before old goal e.g., on_knife before on_cup
//			//instead of that on_knife before precondition on_cup which is place_cup. the reason is I do not implement place_knife before plaace_cup
//			//is the activity place_knife is not existed!! it is not the valid argue, I will try later
//			for (int j = 0; j < originalGoals.size(); j++) {
//				//finding Precondition
//				for (int i = 0; i < operators.size(); i++) {
//					if(originalGoals.get(j).getSymbolicVariable().getDomain().toString().contains
//							(operators.get(i).getHead().substring(operators.get(i).getHead().indexOf("::")+2, operators.get(i).getHead().length()))){
//						for(String req: operators.get(i).getRequirementActivities()){
//							for (int k = 0; k < ((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0])).getConstraintSolvers()[1].getVariables().length; k++) {
//								if(((Activity)((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0])).getConstraintSolvers()[1].getVariables()[k]).getDomain().toString()
//								.contains(req.substring(req.indexOf("::")+2, req.length()))){
//									AllenIntervalConstraint culpritsBeforeOldeGoal = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before,
//											 AllenIntervalConstraint.Type.Before.getDefaultBounds());
//									culpritsBeforeOldeGoal.setFrom(((Activity)newgoalFlunet.getInternalVariables()[1]));
//									culpritsBeforeOldeGoal.setTo(((Activity)((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0])).getConstraintSolvers()[1].getVariables()[k]));
//									
//									//add constraint to Activty constraint Network
//									actNetwork.addConstraint(culpritsBeforeOldeGoal);
//									System.out.println("culpritsBeforeOldeGoal: " + culpritsBeforeOldeGoal);
//								}
//							}
//						}
//					}
//				}
//			}
			
			
			
//			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//			//new goal before old goal e.g., on_knife before on_cup
//			//instead of that on_knife before precondition on_cup which is place_cup. the reason is I do not implement place_knife before plaace_cup
//			//is the activity place_knife is not existed!! it is not the valid argue, I will try later
//			for (int j = 0; j < originalGoals.size(); j++) {
//				for (int i = 0; i < operators.size(); i++) {
//					if(originalGoals.get(j).getSymbolicVariable().getDomain().toString().contains
//							(operators.get(i).getHead().substring(operators.get(i).getHead().indexOf("::")+2, operators.get(i).getHead().length()))){
//						AllenIntervalConstraint culpritsBeforeOldeGoal = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before,
//											 AllenIntervalConstraint.Type.Before.getDefaultBounds());
//						culpritsBeforeOldeGoal.setFrom(((Activity)newgoalFlunet.getInternalVariables()[1]));
//						culpritsBeforeOldeGoal.setTo(originalGoals.get(j));
//						//add constraint to Activty constraint Network
//						actNetwork.addConstraint(culpritsBeforeOldeGoal);
//						System.out.println("culpritsBeforeOldeGoal: " + culpritsBeforeOldeGoal);
//					}
//				}
//			}
			
			
		}
		
		
		Vector<RectangleConstraint> assertionList = new Vector<RectangleConstraint>();
//		newGoal
		for(int i = 0; i < sAssertionalRels.length; i++) {
			boolean isAdded = false;
			if(newGoal.contains(sAssertionalRels[i].getFrom())){
				for (int j = 0; j < newGoalFluentsVector.size(); j++) { //this is rectangle represents new places!
					if (sAssertionalRels[i].getFrom().compareTo(
							((newGoalFluentsVector.get(j))).getName()) == 0) {
						RectangleConstraint assertion = new RectangleConstraint(
								new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals,
										AllenIntervalConstraint.Type.Equals.getDefaultBounds()),
								new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals,
										AllenIntervalConstraint.Type.Equals.getDefaultBounds()));

						assertion.setFrom(((RectangularRegion) newGoalFluentsVector.get(j).getRectangularRegion()));
						assertion.setTo(getVariableByName.get(sAssertionalRels[i].getTo()));
						assertionList.add(assertion);
						mvalue.addConstraint(assertion);
					}				
				}
				isAdded = true;
			}
			else{
				for (int j = 0; j < metaVaribales.size(); j++) { //this is rectangle represents new places!
					if (sAssertionalRels[i].getFrom().compareTo(((metaVaribales.get(j))).getName()) == 0) {

						RectangleConstraint assertion = new RectangleConstraint(
								new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals,
										AllenIntervalConstraint.Type.Equals.getDefaultBounds()),
								new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals,
										AllenIntervalConstraint.Type.Equals.getDefaultBounds()));

						assertion.setFrom(((RectangularRegion) metaVaribales.get(j)));
						assertion.setTo(getVariableByName.get(sAssertionalRels[i].getTo()));
						assertionList.add(assertion);
						mvalue.addConstraint(assertion);
						isAdded = true;
						
	              }
				}
			}
			if(!isAdded){ //goals and we do not care about the rest of the objects which is in the well set table and can not be observed and does not exist in the set of original goal!
				for (int j = 0; j < originalGoals.size(); j++) {
					if(activityToFluent.get(originalGoals.get(j)).getRectangularRegion().getName().compareTo(sAssertionalRels[i].getFrom()) == 0){
						RectangleConstraint assertion = new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals,
								AllenIntervalConstraint.Type.Equals.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals,
								AllenIntervalConstraint.Type.Equals.getDefaultBounds()));

						assertion.setFrom(activityToFluent.get(originalGoals.get(j)).getRectangularRegion());
						assertion.setTo(getVariableByName.get(sAssertionalRels[i].getTo()));
						assertionList.add(assertion);
						mvalue.addConstraint(assertion);

					}
				}
			}
		}
		
//		Vector<Activity> sfToBeRemoved = new Vector<Activity>();
////		System.out.println("mvalue: " + mvalue);
//		for (Activity act : activityToFluent.keySet()) {
//			for (int j = 0; j < newGoalFluentsVector.size(); j++) {
//				if(activityToFluent.get(act).equals(newGoalFluentsVector.get(j))){
//					sfToBeRemoved.add(act);
//				}
//			}
//		}
//		
//		System.out.println("Before: " + activityToFluent);
//		activityToFluent.remove(sfToBeRemoved.lastElement());
//		System.out.println("After: " + activityToFluent);
		
		
		actNetwork.join(mvalue);
		ret.add(actNetwork);
		
		return ret.toArray(new ConstraintNetwork[ret.size()]);

	}

	protected boolean temporalOverlap(Activity a1, Activity a2) {
		return !(a1.getTemporalVariable().getEET() <= a2.getTemporalVariable()
				.getEST() || a2.getTemporalVariable().getEET() <= a1
				.getTemporalVariable().getEST());
	}

	@Override
	public void markResolvedSub(MetaVariable con, ConstraintNetwork metaValue) {
		// TODO Auto-generated method stub
	}

	public boolean isConflicting(Activity[] peak, HashMap<Activity, SpatialFluent> aTOsf) {
		
//		System.out.println("------------------------------------------------------------");
//		for (int i = 0; i < peak.length; i++) {
//			System.out.println(peak[i]);
//		}
//		System.out.println("------------------------------------------------------------");
		
		Vector<UnaryRectangleConstraint> atConstraints = new Vector<UnaryRectangleConstraint>();
		HashMap<String, SpatialFluent> currentFluent = new HashMap<String, SpatialFluent>();
		Vector<RectangularRegion> targetRecs = new Vector<RectangularRegion>();

		for (int i = 0; i < peak.length; i++) {
			currentFluent.put(aTOsf.get(peak[i]).getName(),aTOsf.get(peak[i]));
			targetRecs.add(aTOsf.get(peak[i]).getRectangularRegion());
		}

		// ###################################################################################################
		RectangleConstraintSolver iterSolver = new RectangleConstraintSolver(
				origin, horizon);
		HashMap<String, RectangularRegion> getVariableByName = new HashMap<String, RectangularRegion>();

		Vector<MultiBinaryConstraint> addedGeneralKn = new Vector<MultiBinaryConstraint>();
		for (int i = 0; i < this.rules.length; i++) {

			if (this.rules[i].getFrom().compareTo(this.rules[i].getTo()) == 0) {
				Bounds[] sizeBounds = new Bounds[this.rules[i]
						.getUnaryRAConstraint().getBounds().length];
				for (int j = 0; j < sizeBounds.length; j++) {
					Bounds bSize = new Bounds(
							this.rules[i].getUnaryRAConstraint().getBounds()[j].min,
							this.rules[i].getUnaryRAConstraint().getBounds()[j].max);
					sizeBounds[j] = bSize;
				}
				UnaryRectangleConstraint uConsSize = new UnaryRectangleConstraint(
						UnaryRectangleConstraint.Type.Size, sizeBounds);

				if (getVariableByName.get(this.rules[i].getFrom()) != null)
					uConsSize.setFrom(getVariableByName.get(this.rules[i]
							.getFrom()));
				else {
					RectangularRegion var = (RectangularRegion) iterSolver
							.createVariable();
					var.setName(this.rules[i].getFrom());
					uConsSize.setFrom(var);
					getVariableByName.put(this.rules[i].getFrom(), var);
				}
				if (getVariableByName.get(this.rules[i].getTo()) != null)
					uConsSize
							.setTo(getVariableByName.get(this.rules[i].getTo()));
				else {
					RectangularRegion var = (RectangularRegion) iterSolver
							.createVariable();
					var.setName(this.rules[i].getTo());
					uConsSize.setTo(var);
					getVariableByName.put(this.rules[i].getTo(), var);
				}
				// System.out.println(tmpRule[i].getRAConstraint());
				addedGeneralKn.add(uConsSize);
			} else {

				Bounds[] allenBoundsX = new Bounds[(this.rules[i]
						.getBinaryRAConstraint())
						.getInternalAllenIntervalConstraints()[0].getBounds().length];
				for (int j = 0; j < allenBoundsX.length; j++) {
					Bounds bx = new Bounds(
							(this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[0]
									.getBounds()[j].min, (this.rules[i]
									.getBinaryRAConstraint())
									.getInternalAllenIntervalConstraints()[0]
									.getBounds()[j].max);
					allenBoundsX[j] = bx;
				}

				Bounds[] allenBoundsY = new Bounds[(this.rules[i]
						.getBinaryRAConstraint())
						.getInternalAllenIntervalConstraints()[1].getBounds().length];
				for (int j = 0; j < allenBoundsY.length; j++) {
					Bounds by = new Bounds(
							(this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[1]
									.getBounds()[j].min, (this.rules[i]
									.getBinaryRAConstraint())
									.getInternalAllenIntervalConstraints()[1]
									.getBounds()[j].max);
					allenBoundsY[j] = by;
				}

				AllenIntervalConstraint xAllenCon = new AllenIntervalConstraint(
						(this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[0]
								.getType(), allenBoundsX);
				AllenIntervalConstraint yAllenCon = new AllenIntervalConstraint(
						(this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[1]
								.getType(), allenBoundsY);

				RectangleConstraint uConsBinary = new RectangleConstraint(
						xAllenCon, yAllenCon);

				if (getVariableByName.get(this.rules[i].getFrom()) != null)
					uConsBinary.setFrom(getVariableByName.get(this.rules[i]
							.getFrom()));
				else {
					RectangularRegion var = (RectangularRegion) iterSolver
							.createVariable();
					var.setName(this.rules[i].getFrom());
					uConsBinary.setFrom(var);
					getVariableByName.put(this.rules[i].getFrom(), var);
				}
				if (getVariableByName.get(this.rules[i].getTo()) != null)
					uConsBinary.setTo(getVariableByName.get(this.rules[i]
							.getTo()));
				else {
					RectangularRegion var = (RectangularRegion) iterSolver
							.createVariable();
					var.setName(this.rules[i].getTo());
					uConsBinary.setTo(var);
					getVariableByName.put(this.rules[i].getTo(), var);
				}
				addedGeneralKn.add(uConsBinary);
			}
		}

		if (!iterSolver.addConstraints(addedGeneralKn
				.toArray(new MultiBinaryConstraint[addedGeneralKn.size()])))
			System.out.println("Failed to general knowledge add");

		// ####################################################################################
		// Add at constraint
		Vector<RectangularRegion> metaVaribales = new Vector<RectangularRegion>();
		for (int i = 0; i < sAssertionalRels.length; i++) {

			SpatialFluent sf = currentFluent.get(sAssertionalRels[i].getFrom());
			if (sf == null)
				continue;
			// Add at constraint of indivisuals
			if (sAssertionalRels[i].getUnaryAtRectangleConstraint() != null) {
				RectangularRegion var = (RectangularRegion) iterSolver
						.createVariable();
				var.setName(sAssertionalRels[i].getFrom());

				Bounds[] atBounds = new Bounds[sAssertionalRels[i]
						.getUnaryAtRectangleConstraint().getBounds().length];
				for (int j = 0; j < atBounds.length; j++) {
					Bounds b = new Bounds(
							sAssertionalRels[i].getUnaryAtRectangleConstraint()
									.getBounds()[j].min, sAssertionalRels[i]
									.getUnaryAtRectangleConstraint()
									.getBounds()[j].max);
					atBounds[j] = b;
				}

				UnaryRectangleConstraint atCon = new UnaryRectangleConstraint(
						UnaryRectangleConstraint.Type.At, atBounds);
				atCon.setFrom(var);
				atCon.setTo(var);
				atConstraints.add(atCon);
				metaVaribales.add(var);
				if (!iterSolver.addConstraint(atCon))
					System.out.println("Failed to add AT constraint");

			}

			if (sAssertionalRels[i].getOntologicalProp() != null)
				sf.getRectangularRegion().setOntologicalProp(
						sAssertionalRels[i].getOntologicalProp());
			// targetRecs.add(sf);
		}

		
		// ######################################################################################################
		Vector<RectangleConstraint> assertionList = new Vector<RectangleConstraint>();
		for (int i = 0; i < sAssertionalRels.length; i++) {
			for (int j = 0; j < metaVaribales.size(); j++) {
				if (sAssertionalRels[i].getFrom()
						.compareTo(
								((RectangularRegion) (metaVaribales.get(j)))
										.getName()) == 0) {
					RectangleConstraint assertion = new RectangleConstraint(
							new AllenIntervalConstraint(
									AllenIntervalConstraint.Type.Equals,
									AllenIntervalConstraint.Type.Equals
											.getDefaultBounds()),
							new AllenIntervalConstraint(
									AllenIntervalConstraint.Type.Equals,
									AllenIntervalConstraint.Type.Equals
											.getDefaultBounds()));

					assertion.setFrom(((RectangularRegion) metaVaribales
							.get(j)));
					assertion.setTo(getVariableByName.get(sAssertionalRels[i]
							.getTo()));
					// System.out.println(assertion);
					assertionList.add(assertion);
				}
			}
		}

		boolean isConsistent = true;

		// MetaCSPLogging.setLevel(Level.FINE);
		if (!iterSolver.addConstraints(assertionList
				.toArray(new RectangleConstraint[assertionList.size()])))
			isConsistent = false;
		
//		System.out.println(isConsistent);
//		System.out.println("------------------------------------------------------------");
		return (!isConsistent);
	}

	public void setUsage(SpatialFluent... sfs) {
//		if (activities == null)
//			activities = new Vector<Activity>();
//		for (SpatialFluent sf : sfs){
//			if (!activities.contains(sf.getActivity())) {
//				activities.add(sf.getActivity());
//				activityToFluent.put(sf.getActivity(), sf);
//			}
//		}
		
		for (int i = 0; i < sfs.length; i++) {
			for (int j = 0; j < sAssertionalRels.length; j++) {
				if(sAssertionalRels[j].getFrom().compareTo(sfs[i].getRectangularRegion().getName()) == 0){
					if(isUnboundedBoundingBox(sAssertionalRels[j].getUnaryAtRectangleConstraint().getBounds()[0], 
							sAssertionalRels[j].getUnaryAtRectangleConstraint().getBounds()[1], 
							sAssertionalRels[j].getUnaryAtRectangleConstraint().getBounds()[2],
							sAssertionalRels[j].getUnaryAtRectangleConstraint().getBounds()[3])){
						initialUnboundedObjName.add(sfs[i].getRectangularRegion().getName());
					}
				}
			}
		}
		
		

	}


	@Override
	public void draw(ConstraintNetwork network) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "SpatialScheduable ";
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
		return false;
	}

	private Vector<String> getPotentialCulprit() {
		return potentialCulprit;
	}

	private Vector<String> getInitialUnboundedObject() {
		return initialUnboundedObjName;
	}

	public Vector<HashMap<String, Bounds[]>> generateAllAlternativeSet(Vector<RectangularRegion> targetRecs) {

		class ConstraintNetworkSortingCritera {

			public double rigidityNumber = 0;
			public int culpritLevel = 0;

			ConstraintNetworkSortingCritera(double rigidityNumber,
					int culpritLevel) {
				this.culpritLevel = culpritLevel;
				this.rigidityNumber = rigidityNumber;
			}
		}
		
//		System.out.println("targetRecs: " + targetRecs);
		
		final HashMap<ConstraintNetwork, ConstraintNetworkSortingCritera> sortingCN = new HashMap<ConstraintNetwork, ConstraintNetworkSortingCritera>();
		HashMap<ConstraintNetwork, HashMap<String, Bounds[]>> cnToInitPose = new HashMap<ConstraintNetwork, HashMap<String, Bounds[]>>();
		for (HashMap<String, Bounds[]> iterCN : permutation.keySet()) {
			RectangleConstraintSolver iterSolver = new RectangleConstraintSolver(origin, horizon);
			HashMap<String, RectangularRegion> getVariableByName = new HashMap<String, RectangularRegion>();

			Vector<MultiBinaryConstraint> addedGeneralKn = new Vector<MultiBinaryConstraint>();
			for (int i = 0; i < this.rules.length; i++) {

				if (this.rules[i].getFrom().compareTo(this.rules[i].getTo()) == 0) {
					Bounds[] sizeBounds = new Bounds[this.rules[i].getUnaryRAConstraint().getBounds().length];
					for (int j = 0; j < sizeBounds.length; j++) {
						Bounds bSize = new Bounds(this.rules[i].getUnaryRAConstraint().getBounds()[j].min,
								this.rules[i].getUnaryRAConstraint().getBounds()[j].max);
						sizeBounds[j] = bSize;
					}
					UnaryRectangleConstraint uConsSize = new UnaryRectangleConstraint(
							UnaryRectangleConstraint.Type.Size, sizeBounds);

					if (getVariableByName.get(this.rules[i].getFrom()) != null)
						uConsSize.setFrom(getVariableByName.get(this.rules[i]
								.getFrom()));
					else {
						RectangularRegion var = (RectangularRegion) iterSolver.createVariable();
						var.setName(this.rules[i].getFrom());
						uConsSize.setFrom(var);
						getVariableByName.put(this.rules[i].getFrom(), var);
					}
					if (getVariableByName.get(this.rules[i].getTo()) != null)
						uConsSize.setTo(getVariableByName.get(this.rules[i]
								.getTo()));
					else {
						RectangularRegion var = (RectangularRegion) iterSolver
								.createVariable();
						var.setName(this.rules[i].getTo());
						uConsSize.setTo(var);
						getVariableByName.put(this.rules[i].getTo(), var);
					}
					// System.out.println(tmpRule[i].getRAConstraint());
					addedGeneralKn.add(uConsSize);
				} else {

					Bounds[] allenBoundsX = new Bounds[(this.rules[i]
							.getBinaryRAConstraint())
							.getInternalAllenIntervalConstraints()[0]
							.getBounds().length];
					for (int j = 0; j < allenBoundsX.length; j++) {
						Bounds bx = new Bounds(
								(this.rules[i].getBinaryRAConstraint())
										.getInternalAllenIntervalConstraints()[0]
										.getBounds()[j].min,
								(this.rules[i].getBinaryRAConstraint())
										.getInternalAllenIntervalConstraints()[0]
										.getBounds()[j].max);
						allenBoundsX[j] = bx;
					}

					Bounds[] allenBoundsY = new Bounds[(this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[1].getBounds().length];
					for (int j = 0; j < allenBoundsY.length; j++) {
						Bounds by = new Bounds((this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[1].getBounds()[j].min,
								(this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[1].getBounds()[j].max);
						allenBoundsY[j] = by;
					}

					AllenIntervalConstraint xAllenCon = new AllenIntervalConstraint(
							(this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[0]
									.getType(), allenBoundsX);
					AllenIntervalConstraint yAllenCon = new AllenIntervalConstraint(
							(this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[1]
									.getType(), allenBoundsY);

					RectangleConstraint uConsBinary = new RectangleConstraint(
							xAllenCon, yAllenCon);

					if (getVariableByName.get(this.rules[i].getFrom()) != null)
						uConsBinary.setFrom(getVariableByName.get(this.rules[i]
								.getFrom()));
					else {
						RectangularRegion var = (RectangularRegion) iterSolver
								.createVariable();
						var.setName(this.rules[i].getFrom());
						uConsBinary.setFrom(var);
						getVariableByName.put(this.rules[i].getFrom(), var);
					}
					if (getVariableByName.get(this.rules[i].getTo()) != null)
						uConsBinary.setTo(getVariableByName.get(this.rules[i]
								.getTo()));
					else {
						RectangularRegion var = (RectangularRegion) iterSolver
								.createVariable();
						var.setName(this.rules[i].getTo());
						uConsBinary.setTo(var);
						getVariableByName.put(this.rules[i].getTo(), var);
					}
					addedGeneralKn.add(uConsBinary);
				}
			}

			if (!iterSolver.addConstraints(addedGeneralKn
					.toArray(new MultiBinaryConstraint[addedGeneralKn.size()])))
				System.out.println("Failed to general knowledge add");

			// Att At cpnstraint
			Vector<RectangularRegion> metaVaribales = new Vector<RectangularRegion>();

			for (RectangularRegion Metavar : targetRecs) {
				RectangularRegion var = (RectangularRegion) iterSolver.createVariable();
				var.setName(Metavar.getName());

				Bounds[] atBounds = new Bounds[iterCN.get(Metavar.getName()).length];
				for (int j = 0; j < atBounds.length; j++) {
					Bounds at = new Bounds(
							iterCN.get(Metavar.getName())[j].min,
							iterCN.get(Metavar.getName())[j].max);
					atBounds[j] = at;
				}

				UnaryRectangleConstraint atCon = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, atBounds);
				atCon.setFrom(var);
				atCon.setTo(var);
				metaVaribales.add(var);
				if (!iterSolver.addConstraint(atCon))
					System.out.println("Failed to add AT constraint");
			}

			Vector<RectangleConstraint> assertionList = new Vector<RectangleConstraint>();
			for (int i = 0; i < sAssertionalRels.length; i++) {
				for (int j = 0; j < metaVaribales.size(); j++) {
					if (sAssertionalRels[i].getFrom().compareTo(((RectangularRegion) (metaVaribales.get(j))).getName()) == 0) {
						RectangleConstraint assertion = new RectangleConstraint(
								new AllenIntervalConstraint(
										AllenIntervalConstraint.Type.Equals,
										AllenIntervalConstraint.Type.Equals
												.getDefaultBounds()),
								new AllenIntervalConstraint(
										AllenIntervalConstraint.Type.Equals,
										AllenIntervalConstraint.Type.Equals
												.getDefaultBounds()));

						assertion.setFrom(((RectangularRegion) metaVaribales.get(j)));
						assertion.setTo(getVariableByName.get(sAssertionalRels[i].getTo()));
						// System.out.println(assertion);
						assertionList.add(assertion);
					}
				}
			}
			
//			System.out.println("assertionList: " + assertionList);

			boolean isConsistent = true;

			// MetaCSPLogging.setLevel(Level.FINE);
			if (!iterSolver.addConstraints(assertionList
					.toArray(new RectangleConstraint[assertionList.size()]))) {
				isConsistent = false;
				logger.fine("Failed to add Assertinal Constraint in first generation of all culprit..alternatives generate later...");
			}

			double rigidityavg = ((double) (((AllenIntervalNetworkSolver) iterSolver
					.getConstraintSolvers()[0]).getRigidityNumber()) + (double) (((AllenIntervalNetworkSolver) iterSolver
					.getConstraintSolvers()[1]).getRigidityNumber())) / 2;

			if (isConsistent) {
				sortingCN.put(iterSolver.getConstraintNetwork(),
						new ConstraintNetworkSortingCritera(rigidityavg,permutation.get(iterCN)));
				cnToInitPose.put(iterSolver.getConstraintNetwork(), iterCN);
			}

//			 System.out.println(iterSolver.extractBoundingBoxesFromSTPs("cup1").getAlmostCentreRectangle());
//			
//			 System.out.println("_______________________________________________________________________________");

		}

		ArrayList as = new ArrayList(sortingCN.keySet());
		Collections.sort(as, new Comparator() {
			public int compare(Object o1, Object o2) {
				RectangleConstraintNetwork l1 = (RectangleConstraintNetwork) o1;
				RectangleConstraintNetwork l2 = (RectangleConstraintNetwork) o2;
				Integer first = (Integer) sortingCN.get(l1).culpritLevel;
				Integer second = (Integer) sortingCN.get(l2).culpritLevel;
				int i = first.compareTo(second);
				if (i != 0)
					return i;

				RectangleConstraintNetwork r1 = (RectangleConstraintNetwork) o1;
				RectangleConstraintNetwork r2 = (RectangleConstraintNetwork) o2;
				Double firstRig = (Double) sortingCN.get(r1).rigidityNumber;
				Double secondRig = (Double) sortingCN.get(r2).rigidityNumber;

				i = firstRig.compareTo(secondRig);
				if (i != 0)
					return i;
				return -1;
			}
		});
		Vector<HashMap<String, Bounds[]>> alternativeSets = new Vector<HashMap<String, Bounds[]>>();
		Iterator i = as.iterator();
		while (i.hasNext()) {
			ConstraintNetwork ct = new RectangleConstraintNetwork(null);
			ct = (RectangleConstraintNetwork) i.next();
			HashMap<String, BoundingBox> strToBBs = new HashMap<String, BoundingBox>();
			for (int j = 0; j < ct.getVariables().length; j++) {
				BoundingBox bb = new BoundingBox(new Bounds(((AllenInterval)((RectangularRegion)ct.getVariables()[j]).getInternalVariables()[0]).getEST(), ((AllenInterval)((RectangularRegion)ct.getVariables()[j]).getInternalVariables()[0]).getLST()), 
						new Bounds(((AllenInterval)((RectangularRegion)ct.getVariables()[j]).getInternalVariables()[0]).getEET(), ((AllenInterval)((RectangularRegion)ct.getVariables()[j]).getInternalVariables()[0]).getLET()), 
						new Bounds(((AllenInterval)((RectangularRegion)ct.getVariables()[j]).getInternalVariables()[1]).getEST(), ((AllenInterval)((RectangularRegion)ct.getVariables()[j]).getInternalVariables()[1]).getLST()), 
						new Bounds(((AllenInterval)((RectangularRegion)ct.getVariables()[j]).getInternalVariables()[1]).getEET(), ((AllenInterval)((RectangularRegion)ct.getVariables()[j]).getInternalVariables()[1]).getLET()));
				
				strToBBs.put(((RectangularRegion)ct.getVariables()[j]).getName(), bb);
			}
			newRectangularRegion.add(strToBBs);
			alternativeSets.add(cnToInitPose.get(ct));

		}
		return alternativeSets;

	}
	
	private ConstraintNetwork[] completePeakCollection(HashMap<Activity, SpatialFluent> aTOsf) {
		Vector<Activity> activities = new Vector<Activity>();
		for (Activity act : aTOsf.keySet()) {
			activities.add(act);
		}
		
		if (activities != null && !activities.isEmpty()) {
			logger.finest("Doing complete peak collection with " + activities.size() + " activities...");
			
			Activity[] groundVars = activities.toArray(new Activity[activities.size()]);			
			Vector<Long> discontinuities = new Vector<Long>();
			for (Activity a : groundVars) {
				long start = a.getTemporalVariable().getEST();
				long end = a.getTemporalVariable().getEET();
				if (!discontinuities.contains(start)) discontinuities.add(start);
				if (!discontinuities.contains(end)) discontinuities.add(end);
			}
			
			Long[] discontinuitiesArray = discontinuities.toArray(new Long[discontinuities.size()]);
			Arrays.sort(discontinuitiesArray);
			
			HashSet<HashSet<Activity>> superPeaks = new HashSet<HashSet<Activity>>();

			for (int i = 0; i < discontinuitiesArray.length-1; i++) {
				HashSet<Activity> onePeak = new HashSet<Activity>();
				superPeaks.add(onePeak);
				Bounds interval = new Bounds(discontinuitiesArray[i], discontinuitiesArray[i+1]);
				for (Activity a : groundVars) {
					Bounds interval1 = new Bounds(a.getTemporalVariable().getEST(), a.getTemporalVariable().getEET());
					Bounds intersection = interval.intersectStrict(interval1);
					if (intersection != null && !intersection.isSingleton()) {
						onePeak.add(a);
					}
				}
			}
			
			
			
			Vector<ConstraintNetwork> ret = new Vector<ConstraintNetwork>();
			for (HashSet<Activity> superSet : superPeaks) {
				for (Set<Activity> s : powerSet(superSet)) {
					if (!s.isEmpty()) {
						ActivityNetwork cn = new ActivityNetwork(null);
						for (Activity a : s) cn.addVariable(a); 
						if (!ret.contains(cn) && isConflicting(s.toArray(new Activity[s.size()]), aTOsf)) ret.add(cn);
					}
				}
			}
			
//			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//			for (int i = 0; i < ret.size(); i++) {
//				System.out.println(ret.get(i));
//				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//			}
//			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			
			logger.finest("Done peak sampling");
			return ret.toArray(new ConstraintNetwork[ret.size()]);			
		} 
		
		return (new ConstraintNetwork[0]);
	}

	
	private void generateCombinantion(Vector<UnaryRectangleConstraint> atConstraints) {

		Vector<UnaryRectangleConstraint> boundedUnaryCons = new Vector<UnaryRectangleConstraint>();
		Vector<UnaryRectangleConstraint> unboundedUnaryCons = new Vector<UnaryRectangleConstraint>();

		HashMap<Vector<UnaryRectangleConstraint>, Integer> rank = new HashMap<Vector<UnaryRectangleConstraint>, Integer>();
		for (int i = 0; i < atConstraints.size(); i++) {
			Bounds[] boundsX = ((AllenIntervalConstraint) ((UnaryRectangleConstraint) atConstraints
					.get(i)).getInternalConstraints()[0]).getBounds();
			Bounds[] boundsY = ((AllenIntervalConstraint) ((UnaryRectangleConstraint) atConstraints
					.get(i)).getInternalConstraints()[1]).getBounds();
			if (!isUnboundedBoundingBox(boundsX[0], boundsX[1], boundsY[0],boundsY[1])
					&& ((RectangularRegion) ((UnaryRectangleConstraint) atConstraints.get(i))
							.getFrom()).getOntologicalProp().isMovable()) {

				potentialCulprit.add(((RectangularRegion) ((UnaryRectangleConstraint) atConstraints.get(i)).getFrom()).getName());
				logger.fine("one potential culprit can be: " + ((RectangularRegion) ((UnaryRectangleConstraint) atConstraints.get(i)).getFrom()).getName());
				boundedUnaryCons.add((UnaryRectangleConstraint) atConstraints.get(i));
			}
			else {				
//				if (((RectangularRegion) ((UnaryRectangleConstraint) atConstraints.get(i)).getFrom()).getOntologicalProp().isMovable())
//					initialUnboundedObjName.add(((RectangularRegion) ((UnaryRectangleConstraint) atConstraints.get(i)).getFrom()).getName());
				unboundedUnaryCons.add((UnaryRectangleConstraint) atConstraints.get(i));
			}
		}

		Combinatorical c = Combinatorical.getPermutations(boundedUnaryCons.size(), 2, true);
//		System.out.println("c.count: " + c.count());
		while (c.hasNext()) {
			int[] combination = c.next();
			int culpritNumber = 0;
			Vector<UnaryRectangleConstraint> tmpboundedUnaryCons = new Vector<UnaryRectangleConstraint>();
			Vector<UnaryRectangleConstraint> justCulprit = new Vector<UnaryRectangleConstraint>();
			for (int i = 0; i < combination.length; i++) {
				if (combination[i] == 1) {
					UnaryRectangleConstraint utmp = new UnaryRectangleConstraint(
							UnaryRectangleConstraint.Type.At, new Bounds(0,
									APSPSolver.INF), new Bounds(0,
									APSPSolver.INF), new Bounds(0,
									APSPSolver.INF), new Bounds(0,
									APSPSolver.INF));
					utmp.setFrom(boundedUnaryCons.get(i).getFrom());
					utmp.setTo(boundedUnaryCons.get(i).getTo());
					tmpboundedUnaryCons.add(utmp);
					justCulprit.add(utmp);
					culpritNumber++;
				}

				else
					tmpboundedUnaryCons.add(boundedUnaryCons.get(i));
				// System.out.print(combination[i]);

			}
			rank.put(tmpboundedUnaryCons, culpritNumber);

		}
		 

		for (Vector<UnaryRectangleConstraint> cc : rank.keySet()) {
			HashMap<String, Bounds[]> culprit = new HashMap<String, Bounds[]>();

			for (int i = 0; i < cc.size(); i++) {

				Bounds[] bounds = new Bounds[4];
				bounds[0] = ((AllenIntervalConstraint) ((UnaryRectangleConstraint) cc
						.get(i)).getInternalConstraints()[0]).getBounds()[0];
				bounds[1] = ((AllenIntervalConstraint) ((UnaryRectangleConstraint) cc
						.get(i)).getInternalConstraints()[0]).getBounds()[1];
				bounds[2] = ((AllenIntervalConstraint) ((UnaryRectangleConstraint) cc
						.get(i)).getInternalConstraints()[1]).getBounds()[0];
				bounds[3] = ((AllenIntervalConstraint) ((UnaryRectangleConstraint) cc
						.get(i)).getInternalConstraints()[1]).getBounds()[1];

				culprit.put(
						((RectangularRegion) cc.get(i).getFrom()).getName(),
						bounds);
			}
			for (int i = 0; i < unboundedUnaryCons.size(); i++) {
				Bounds[] bounds = new Bounds[4];
				bounds[0] = ((AllenIntervalConstraint) ((UnaryRectangleConstraint) unboundedUnaryCons
						.get(i)).getInternalConstraints()[0]).getBounds()[0];
				bounds[1] = ((AllenIntervalConstraint) ((UnaryRectangleConstraint) unboundedUnaryCons
						.get(i)).getInternalConstraints()[0]).getBounds()[1];
				bounds[2] = ((AllenIntervalConstraint) ((UnaryRectangleConstraint) unboundedUnaryCons
						.get(i)).getInternalConstraints()[1]).getBounds()[0];
				bounds[3] = ((AllenIntervalConstraint) ((UnaryRectangleConstraint) unboundedUnaryCons
						.get(i)).getInternalConstraints()[1]).getBounds()[1];

				culprit.put(((RectangularRegion) unboundedUnaryCons.get(i)
						.getFrom()).getName(), bounds);
			}
			permutation.put(culprit, rank.get(cc));
		}

		// System.out.println(permutation);
	}

	private boolean isUnboundedBoundingBox(Bounds xLB, Bounds xUB, Bounds yLB,
			Bounds yUB) {

		if (xLB.min != 0 && xLB.max != APSPSolver.INF)
			return false;
		if (xUB.min != 0 && xUB.max != APSPSolver.INF)
			return false;
		if (yLB.min != 0 && yLB.max != APSPSolver.INF)
			return false;
		if (yLB.min != 0 && yUB.max != APSPSolver.INF)
			return false;
		return true;
	}

	private RectangleConstraintNetwork createTBOXspatialNetwork(ConstraintSolver solver,
			HashMap<String, RectangularRegion> getVariableByName) {

		// general knowledge
		RectangleConstraintNetwork ret = new RectangleConstraintNetwork(
				solver);
		// Vector<MultiBinaryConstraint> addedGeneralKn = new
		// Vector<MultiBinaryConstraint>();
		for (int i = 0; i < this.rules.length; i++) {

			if (this.rules[i].getFrom().compareTo(this.rules[i].getTo()) == 0) {
				Bounds[] sizeBounds = new Bounds[this.rules[i]
						.getUnaryRAConstraint().getBounds().length];
				for (int j = 0; j < sizeBounds.length; j++) {
					Bounds bSize = new Bounds(
							this.rules[i].getUnaryRAConstraint().getBounds()[j].min,
							this.rules[i].getUnaryRAConstraint().getBounds()[j].max);
					sizeBounds[j] = bSize;
				}
				UnaryRectangleConstraint uConsSize = new UnaryRectangleConstraint(
						UnaryRectangleConstraint.Type.Size, sizeBounds);

				if (getVariableByName.get(this.rules[i].getFrom()) != null)
					uConsSize.setFrom(getVariableByName.get(this.rules[i]
							.getFrom()));
				else {
					RectangularRegion var = (RectangularRegion) solver
							.createVariable();
					var.setName(this.rules[i].getFrom());
					uConsSize.setFrom(var);
					getVariableByName.put(this.rules[i].getFrom(), var);
				}
				if (getVariableByName.get(this.rules[i].getTo()) != null)
					uConsSize
							.setTo(getVariableByName.get(this.rules[i].getTo()));
				else {
					RectangularRegion var = (RectangularRegion) solver
							.createVariable();
					var.setName(this.rules[i].getTo());
					uConsSize.setTo(var);
					getVariableByName.put(this.rules[i].getTo(), var);
				}
				// System.out.println(tmpRule[i].getRAConstraint());
				ret.addConstraint(uConsSize);
			} else {

				Bounds[] allenBoundsX = new Bounds[(this.rules[i]
						.getBinaryRAConstraint())
						.getInternalAllenIntervalConstraints()[0].getBounds().length];
				for (int j = 0; j < allenBoundsX.length; j++) {
					Bounds bx = new Bounds(
							(this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[0]
									.getBounds()[j].min, (this.rules[i]
									.getBinaryRAConstraint())
									.getInternalAllenIntervalConstraints()[0]
									.getBounds()[j].max);
					allenBoundsX[j] = bx;
				}

				Bounds[] allenBoundsY = new Bounds[(this.rules[i]
						.getBinaryRAConstraint())
						.getInternalAllenIntervalConstraints()[1].getBounds().length];
				for (int j = 0; j < allenBoundsY.length; j++) {
					Bounds by = new Bounds(
							(this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[1]
									.getBounds()[j].min, (this.rules[i]
									.getBinaryRAConstraint())
									.getInternalAllenIntervalConstraints()[1]
									.getBounds()[j].max);
					allenBoundsY[j] = by;
				}

				AllenIntervalConstraint xAllenCon = new AllenIntervalConstraint(
						(this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[0]
								.getType(), allenBoundsX);
				AllenIntervalConstraint yAllenCon = new AllenIntervalConstraint(
						(this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[1]
								.getType(), allenBoundsY);

				RectangleConstraint uConsBinary = new RectangleConstraint(
						xAllenCon, yAllenCon);

				if (getVariableByName.get(this.rules[i].getFrom()) != null)
					uConsBinary.setFrom(getVariableByName.get(this.rules[i]
							.getFrom()));
				else {
					RectangularRegion var = (RectangularRegion) solver
							.createVariable();
					var.setName(this.rules[i].getFrom());
					uConsBinary.setFrom(var);
					getVariableByName.put(this.rules[i].getFrom(), var);
				}
				if (getVariableByName.get(this.rules[i].getTo()) != null)
					uConsBinary.setTo(getVariableByName.get(this.rules[i]
							.getTo()));
				else {
					RectangularRegion var = (RectangularRegion) solver
							.createVariable();
					var.setName(this.rules[i].getTo());
					uConsBinary.setTo(var);
					getVariableByName.put(this.rules[i].getTo(), var);
				}
				ret.addConstraint(uConsBinary);
			}
		}

		return ret;
	}
	
	private void setPermutationHashMAP(Vector<SpatialFluent> conflictvars, Vector<RectangularRegion> targetRecs){
		
		Vector<UnaryRectangleConstraint> atConstraints = new Vector<UnaryRectangleConstraint>();
		HashMap<String, SpatialFluent> currentFluent = new HashMap<String, SpatialFluent>();
//		Vector<RectangularRegion> targetRecs = new Vector<RectangularRegion>();

		for (int i = 0; i < conflictvars.size(); i++) {
			currentFluent.put(conflictvars.get(i).getName(), conflictvars.get(i));
		}
		
 
		// Add at constraint
		RectangleConstraintSolver iterSolver = new RectangleConstraintSolver(origin, horizon);
		Vector<RectangularRegion> metaVaribales = new Vector<RectangularRegion>();
		for (int i = 0; i < sAssertionalRels.length; i++) {

			SpatialFluent sf = currentFluent.get(sAssertionalRels[i].getFrom());
			if (sf == null)
				continue;
			// Add at constraint of indivisuals
			RectangularRegion var = (RectangularRegion) iterSolver.createVariable();
			if (sAssertionalRels[i].getUnaryAtRectangleConstraint() != null) {				
				var.setName(sAssertionalRels[i].getFrom());				
				Bounds[] atBounds = new Bounds[sAssertionalRels[i].getUnaryAtRectangleConstraint().getBounds().length];
				for (int j = 0; j < atBounds.length; j++) {
					Bounds b = new Bounds(
							sAssertionalRels[i].getUnaryAtRectangleConstraint()
									.getBounds()[j].min, sAssertionalRels[i]
									.getUnaryAtRectangleConstraint()
									.getBounds()[j].max);
					atBounds[j] = b;
				}

				UnaryRectangleConstraint atCon = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, atBounds);
				atCon.setFrom(var);
				atCon.setTo(var);
				atConstraints.add(atCon);
				metaVaribales.add(var);
				if (!iterSolver.addConstraint(atCon))
					System.out.println("Failed to add AT constraint");

			}

			if (sAssertionalRels[i].getOntologicalProp() != null){
//				sf.getRectangularRegion().setOntologicalProp(sAssertionalRels[i].getOntologicalProp());
				var.setOntologicalProp(sAssertionalRels[i].getOntologicalProp());
			// targetRecs.add(sf);
			}
		}

		generateCombinantion(atConstraints);

		
	}
	

}
