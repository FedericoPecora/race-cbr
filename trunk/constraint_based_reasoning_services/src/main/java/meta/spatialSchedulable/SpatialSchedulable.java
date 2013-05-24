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

import orbital.algorithm.Combinatorical;

import sandbox.spatial.rectangleAlgebra2.RectangleConstraint2;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraintNetwork2;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraintSolver2;
import sandbox.spatial.rectangleAlgebra2.RectangularRegion2;
import sandbox.spatial.rectangleAlgebra2.SpatialAssertionalRelation2;
import sandbox.spatial.rectangleAlgebra2.SpatialFluent;
import sandbox.spatial.rectangleAlgebra2.SpatialFluentSolver;
import sandbox.spatial.rectangleAlgebra2.SpatialRule2;
import sandbox.spatial.rectangleAlgebra2.UnaryRectangleConstraint2;
import symbols.SymbolicDomain;
import time.APSPSolver;
import time.Bounds;
import framework.Constraint;
import framework.ConstraintNetwork;
import framework.ConstraintSolver;
import framework.ValueOrderingH;
import framework.Variable;
import framework.VariableOrderingH;
import framework.meta.MetaConstraint;
import framework.meta.MetaVariable;
import framework.multi.MultiBinaryConstraint;
import framework.multi.MultiDomain;
import meta.MetaSpatialFluentConstraint;
import meta.MetaCausalConstraint.markings;
import meta.symbolsAndTime.MCSData;
import meta.symbolsAndTime.Schedulable;
import meta.symbolsAndTime.Schedulable.PEAKCOLLECTION;
import multi.activity.Activity;
import multi.activity.ActivityComparator;
import multi.activity.ActivityNetwork;
import multi.allenInterval.AllenIntervalConstraint;
import multi.allenInterval.AllenIntervalNetworkSolver;

public class SpatialSchedulable extends MetaConstraint {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1583183627952907595L;
	private long origin = 0, horizon = 1000;
	private SpatialAssertionalRelation2[] sAssertionalRels;
	private SpatialRule2[] rules;
	private boolean markMetaVar = false;
	private HashMap<HashMap<String, Bounds[]>, Integer> permutation;
	private Vector<String> initialUnboundedObjName = new Vector<String>();
	private Vector<String> potentialCulprit = new Vector<String>();
	private HashMap<Activity, SpatialFluent> activityToFluent = new HashMap<Activity, SpatialFluent>();
	private Vector<Activity> activeCulprit = new Vector<Activity>();

	public SpatialSchedulable(VariableOrderingH varOH, ValueOrderingH valOH) {
		super(varOH, valOH);
		this.beforeParameter = 1;
	}

	public void setSpatialRules(SpatialRule2... rules) {
		this.rules = new SpatialRule2[rules.length];
		this.rules = rules;
	}

	public void setSpatialAssertionalRelations(
			SpatialAssertionalRelation2... sAssertionalRels) {
		this.sAssertionalRels = new SpatialAssertionalRelation2[sAssertionalRels.length];
		this.sAssertionalRels = sAssertionalRels;
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

	protected Vector<Activity> activities;

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
	
	private Vector<Activity> removeCulpritsFromCurrentActivity() {
		
		if(activeCulprit.size() == 0) return activities;
		return activities;
//		Vector<Activity> tmpgroundVars = new Vector<Activity>(); 
//		for (int i = 0; i < activities.size(); i++) {
//			boolean mark = false;
//			for (int j = 0; j < activeCulprit.size(); j++) {
//				if(activities.get(i).getID() == activeCulprit.get(j).getID()){
//					mark = true;
//				}
//			}
//			if(!mark)
//				tmpgroundVars.add(activities.get(i));
//		}
//		
//		return tmpgroundVars;
	}

	
	// Finds sets of overlapping activities and assesses whether they are
	// conflicting (e.g., over-consuming a resource)
	private ConstraintNetwork[] samplingPeakCollection() {

		if (activities != null && !activities.isEmpty()) {
			
			Vector<Activity> currentacts= removeCulpritsFromCurrentActivity(); 
			
//			System.out.println("currentacts" + currentacts);
			Activity[] groundVars = currentacts.toArray(new Activity[currentacts.size()]);			
			Arrays.sort(groundVars, new ActivityComparator(true));
			Vector<ConstraintNetwork> ret = new Vector<ConstraintNetwork>();
			HashMap<Activity, ActivityNetwork> usages = new HashMap<Activity, ActivityNetwork>();
			Vector<Vector<Activity>> overlappingAll = new Vector<Vector<Activity>>();

			// if an activity is spatially inconsistent even with itself
			for (Activity act : activities) {
				if (isConflicting(new Activity[] { act })) {
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
				long end = (groundVars[i]).getTemporalVariable().getLET();
				Bounds intersection = new Bounds(start, end);
//				System.out.println("intersection1: " + groundVars[i] + " " +intersection);
				for (int j = 0; j < groundVars.length; j++) {
					if (i != j) {
						start = (groundVars[j]).getTemporalVariable().getEST();
						end = (groundVars[j]).getTemporalVariable().getLET();
						Bounds nextInterval = new Bounds(start, end);
//						 System.out.println("nextinterval: " + groundVars[j] + " " +nextInterval);
//						 System.out.println("____________________________________");
						Bounds intersectionNew = intersection.intersectStrict(nextInterval);
						if (intersectionNew != null) {
							overlapping.add(groundVars[j]);
							if (isConflicting(overlapping.toArray(new Activity[overlapping.size()]))) {
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
				
//				System.out.println("retActivities: " + retActivities);
				
				
				for (Vector<Activity> actVec : retActivities) {
					ActivityNetwork tmp = new ActivityNetwork(null);
					for (Activity act : actVec){
						tmp.addVariable(act);
					}
					ret.add(tmp);					
				}
//				setPermutationHashMAP(overlappingAll.get(0).toArray(new Activity[overlappingAll.get(0).size()]));				
//				return ret.toArray(new ConstraintNetwork[ret.size()]);
//				setPermutationHashMAP(overlappingAll.lastElement().toArray(new Activity[overlappingAll.lastElement().size()]));
				return new ConstraintNetwork[]{ret.lastElement()};
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
//		return samplingPeakCollection();
		return completePeakCollection();
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
		permutation = new HashMap<HashMap<String, Bounds[]>, Integer>();
		Vector<ConstraintNetwork> ret = new Vector<ConstraintNetwork>();
		RectangleConstraintNetwork2 mvalue = new RectangleConstraintNetwork2(this.metaCS.getConstraintSolvers()[0]);
		
		//#########################################################################################################
		ConstraintNetwork conflict = metaVariable.getConstraintNetwork();
		Vector<SpatialFluent> conflictvars = new Vector<SpatialFluent>();
		Vector<RectangularRegion2> conflictRecvars = new Vector<RectangularRegion2>();
		HashMap<String, RectangularRegion2> getVariableByName = new HashMap<String, RectangularRegion2>();

		for (int j = 0; j < conflict.getVariables().length; j++) {
			conflictvars.add(activityToFluent.get((Activity) conflict.getVariables()[j]));
			conflictRecvars.add(activityToFluent.get((Activity) conflict.getVariables()[j]).getRectangularRegion());
		}
		setPermutationHashMAP(conflictvars, conflictRecvars);
		Vector<HashMap<String, Bounds[]>> alternativeSets = generateAllAlternativeSet(conflictRecvars);

		HashMap<String, Bounds[]> alternativeSet = alternativeSets.get(0);
		mvalue.join(createTBOXspatialNetwork(((SpatialFluentSolver) this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[0],getVariableByName)); // TBOX general knowledge in RectangleCN
		// Att At cpnstraint
		Vector<RectangularRegion2> metaVaribales = new Vector<RectangularRegion2>();		
		for (SpatialFluent var : conflictvars) {
			Bounds[] atBounds = new Bounds[alternativeSet.get(var.getRectangularRegion().getName()).length];
			for (int j = 0; j < atBounds.length; j++) {
				Bounds at = new Bounds(alternativeSet.get(var.getName())[j].min,alternativeSet.get(var.getName())[j].max);
				atBounds[j] = at;
			}
			UnaryRectangleConstraint2 atCon = new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, atBounds);
			atCon.setFrom(var.getRectangularRegion());
			atCon.setTo(var.getRectangularRegion());

			metaVaribales.add(var.getRectangularRegion());
			mvalue.addConstraint(atCon);
			mvalue.addVariable(var.getRectangularRegion());
		}
		Vector<RectangleConstraint2> assertionList = new Vector<RectangleConstraint2>();
		for (int i = 0; i < sAssertionalRels.length; i++) {
			for (int j = 0; j < metaVaribales.size(); j++) {
				if (sAssertionalRels[i].getFrom().compareTo(
						((metaVaribales.get(j))).getName()) == 0) {
					RectangleConstraint2 assertion = new RectangleConstraint2(
							new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals,
									AllenIntervalConstraint.Type.Equals.getDefaultBounds()),
							new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals,
									AllenIntervalConstraint.Type.Equals.getDefaultBounds()));

					assertion.setFrom(((RectangularRegion2) metaVaribales.get(j)));
					assertion.setTo(getVariableByName.get(sAssertionalRels[i].getTo()));

//					System.out.println(assertion);
					assertionList.add(assertion);
					mvalue.addConstraint(assertion);
				}
			}
		}

		//###########################################################################################################
		logger.finest("pregenerated meta value for scoring: " + mvalue);

//		System.out.println("mvalue" + mvalue);
		Vector<Activity> oldGoal = new Vector<Activity>();
		Vector<String> newGoal = new Vector<String>();
		HashMap<String, Activity> culpritActivities = new HashMap<String, Activity>(); 
		Vector<Activity> passiveCulprit = new Vector<Activity>();
		
		
		for (int i = 0; i < mvalue.getConstraints().length; i++) {
			if (mvalue.getConstraints()[i] instanceof UnaryRectangleConstraint2) {
				// this if will check for unboudned obj in order to create the goal
				if (((UnaryRectangleConstraint2) mvalue.getConstraints()[i])
						.getType().equals(UnaryRectangleConstraint2.Type.At)) {
					if (this.isUnboundedBoundingBox(((UnaryRectangleConstraint2) mvalue.getConstraints()[i]).getBounds()[0],
							((UnaryRectangleConstraint2) mvalue.getConstraints()[i]).getBounds()[1],
							((UnaryRectangleConstraint2) mvalue.getConstraints()[i]).getBounds()[2],
							((UnaryRectangleConstraint2) mvalue.getConstraints()[i]).getBounds()[3])) {

						for (int j = 0; j < metaVariable.getConstraintNetwork().getVariables().length; j++) {
							if (((RectangularRegion2) mvalue.getConstraints()[i].getScope()[0]).getName().compareTo
									(((SpatialFluent) activityToFluent.get((Activity)metaVariable.getConstraintNetwork().getVariables()[j]) ).getName()) == 0) {
//								System.out.println("ADDED ACTIVITY: " + ((Activity)(metaVariable.getConstraintNetwork().getVariables()[j])));
//								((Activity)(metaVariable.getConstraintNetwork().getVariables()[j])).setMarking(markings.UNJUSTIFIED);								
								if (this.getPotentialCulprit().contains(((RectangularRegion2) mvalue.getConstraints()[i].getScope()[0]).getName())) {
//									System.out.println("HERE IS THE NEW GOAL: " + ((RectangularRegion2)mvalue.getConstraints()[i].getScope()[0]).getName());
									culpritActivities.put(((RectangularRegion2)mvalue.getConstraints()[i].getScope()[0]).getName(), 
											((Activity)(metaVariable.getConstraintNetwork().getVariables()[j])));
									newGoal.add(((RectangularRegion2) mvalue.getConstraints()[i].getScope()[0]).getName());
									activeCulprit.add(((Activity)(metaVariable.getConstraintNetwork().getVariables()[j])));
									
								} else {
									System.out.println("HERE IS THE OLD GOAL: " + ((RectangularRegion2)mvalue.getConstraints()[i].getScope()[0]).getName());
									System.out.println("OLD GOAL ACTIVTY: " + ((Activity)(metaVariable.getConstraintNetwork().getVariables()[j])));
//									oldGoal.add(((RectangularRegion2) mvalue.getConstraints()[i].getScope()[0]).getName());
									oldGoal.add(((Activity)(metaVariable.getConstraintNetwork().getVariables()[j])));
								}
							}
						}
					}else{
						
							for (int j = 0; j < metaVariable.getConstraintNetwork().getVariables().length; j++) {
								if (((RectangularRegion2) mvalue.getConstraints()[i].getScope()[0]).getName().compareTo
										(((SpatialFluent) activityToFluent.get((Activity)metaVariable.getConstraintNetwork().getVariables()[j]) ).getName()) == 0) {
									if (this.getPotentialCulprit().contains(((RectangularRegion2) mvalue.getConstraints()[i].getScope()[0]).getName())) {
//										System.out.println("HERE IS PASSIVE CULPRIT: " + ((RectangularRegion2)mvalue.getConstraints()[i].getScope()[0]).getName());
										if(!passiveCulprit.contains(((Activity)(metaVariable.getConstraintNetwork().getVariables()[j]))))
											passiveCulprit.add(((Activity)(metaVariable.getConstraintNetwork().getVariables()[j])));

								}
							}
						}
					}
				}						
			}
		}
	
		//set new Goal After old activity
		for (String st :newGoal) {
			
			//add new fluent
			SpatialFluent newgoalFlunet = (SpatialFluent)((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0])).createVariable();
			newgoalFlunet.setName(st);
			((Activity)newgoalFlunet.getInternalVariables()[1]).setSymbolicDomain(culpritActivities.get(st).getSymbolicVariable().toString()
					.subSequence(21, ((Activity)culpritActivities.get(st)).getSymbolicVariable().toString().length() - 1).toString());
			((Activity)newgoalFlunet.getInternalVariables()[1]).setMarking(markings.UNJUSTIFIED);
			newgoalFlunet.setRectangularRegion(activityToFluent.get(culpritActivities.get(st)).getRectangularRegion());			
			activityToFluent.put(((Activity)newgoalFlunet.getInternalVariables()[1]), newgoalFlunet);
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			//update assertional rule
			for (int j = 0; j < sAssertionalRels.length; j++) {
				if(sAssertionalRels[j].getFrom().compareTo(st) == 0)
				sAssertionalRels[j].setUnaryAtRectangleConstraint(new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, 
						new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
			}			
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			activities.add(((Activity)newgoalFlunet.getInternalVariables()[1]));
			AllenIntervalConstraint newOnAfteroldOn = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After,
					 AllenIntervalConstraint.Type.After.getDefaultBounds());
			newOnAfteroldOn.setFrom(((Activity)newgoalFlunet.getInternalVariables()[1]));
			newOnAfteroldOn.setTo(culpritActivities.get(st));
			((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0])).getConstraintSolvers()[1].addConstraints(new
					 Constraint[] {newOnAfteroldOn});
			System.out.println(newOnAfteroldOn);
			 
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//			 //new goal before old goal ! i am not sure if it is neccessary
//			 AllenIntervalConstraint newgoalBeforeOldeGoal = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before,
//					 AllenIntervalConstraint.Type.Before.getDefaultBounds());
//			 newgoalBeforeOldeGoal.setFrom(((Activity)newgoalFlunet.getInternalVariables()[1]));
//			 newgoalBeforeOldeGoal.setTo(culpritActivities.get(st));
//			 ((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0])).getConstraintSolvers()[1].addConstraints(new
//					 Constraint[] {newgoalBeforeOldeGoal});
//			 System.out.println(newgoalBeforeOldeGoal);
		}
		
	
//		Activity hold = null;
////		var: robot1::<SymbolicVariable 6: [pick_cup1_table2(arm)]>U<AllenInterval 6 (I-TP: 14 15 ) [[11, INF], [21, INF]]>/JUSTIFIED
//		for (int i = 0; i < ((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0])).getConstraintSolvers()[1].getVariables().length; i++) {
//			
//			if(((Activity)((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0])).getConstraintSolvers()[1].getVariables()[i])
//					.getSymbolicVariable().toString().contains("pick_cup1_table2")){
////				hold = ((Activity)((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0])).getConstraintSolvers()[1].getVariables()[i]);
//				System.out.println("var: " + ((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0])).getConstraintSolvers()[1].getVariables()[i]);
//				AllenIntervalConstraint oldgoalAfternewGoal = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Deadline, new Bounds(21,21));
//				oldgoalAfternewGoal.setFrom(((Activity)((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0])).getConstraintSolvers()[1].getVariables()[i]));
//				oldgoalAfternewGoal.setTo(((Activity)((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0])).getConstraintSolvers()[1].getVariables()[i]));									
//				((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0])).getConstraintSolvers()[1].addConstraints(new Constraint[] {oldgoalAfternewGoal});
//
//			}
//		}
	
	

		
//		//set old Activity before the other activity that is potential culprit
//		for (int j = 0; j < activeCulprit.size(); j++) {
//			for (int j2 = 0; j2 < passiveCulprit.size(); j2++) {
//				
//				 AllenIntervalConstraint getRidOfOverlap = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before,
//						 AllenIntervalConstraint.Type.Before.getDefaultBounds());
//				 System.out.println(activeCulprit.get(j));
//				 getRidOfOverlap.setFrom(activeCulprit.get(j));
//				 getRidOfOverlap.setTo(passiveCulprit.get(j2));
//				 ((SpatialFluentSolver)(this.metaCS.getConstraintSolvers()[0])).getConstraintSolvers()[1].addConstraints(new
//						 Constraint[] {getRidOfOverlap});
//				 
//				 System.out.println(getRidOfOverlap);
//			}
//		}
		
		ret.add(mvalue);
//		System.out.println(ret);
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

	public boolean isConflicting(Activity[] peak) {
		
//		System.out.println("------------------------------------------------------------");
//		for (int i = 0; i < peak.length; i++) {
//			System.out.println(peak[i]);
//		}
		
		
		
		Vector<UnaryRectangleConstraint2> atConstraints = new Vector<UnaryRectangleConstraint2>();
		HashMap<String, SpatialFluent> currentFluent = new HashMap<String, SpatialFluent>();
		Vector<RectangularRegion2> targetRecs = new Vector<RectangularRegion2>();

		for (int i = 0; i < peak.length; i++) {
			currentFluent.put(activityToFluent.get(peak[i]).getName(),activityToFluent.get(peak[i]));
			targetRecs.add(activityToFluent.get(peak[i]).getRectangularRegion());
		}

		// ###################################################################################################
		RectangleConstraintSolver2 iterSolver = new RectangleConstraintSolver2(
				origin, horizon);
		HashMap<String, RectangularRegion2> getVariableByName = new HashMap<String, RectangularRegion2>();

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
				UnaryRectangleConstraint2 uConsSize = new UnaryRectangleConstraint2(
						UnaryRectangleConstraint2.Type.Size, sizeBounds);

				if (getVariableByName.get(this.rules[i].getFrom()) != null)
					uConsSize.setFrom(getVariableByName.get(this.rules[i]
							.getFrom()));
				else {
					RectangularRegion2 var = (RectangularRegion2) iterSolver
							.createVariable();
					var.setName(this.rules[i].getFrom());
					uConsSize.setFrom(var);
					getVariableByName.put(this.rules[i].getFrom(), var);
				}
				if (getVariableByName.get(this.rules[i].getTo()) != null)
					uConsSize
							.setTo(getVariableByName.get(this.rules[i].getTo()));
				else {
					RectangularRegion2 var = (RectangularRegion2) iterSolver
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

				RectangleConstraint2 uConsBinary = new RectangleConstraint2(
						xAllenCon, yAllenCon);

				if (getVariableByName.get(this.rules[i].getFrom()) != null)
					uConsBinary.setFrom(getVariableByName.get(this.rules[i]
							.getFrom()));
				else {
					RectangularRegion2 var = (RectangularRegion2) iterSolver
							.createVariable();
					var.setName(this.rules[i].getFrom());
					uConsBinary.setFrom(var);
					getVariableByName.put(this.rules[i].getFrom(), var);
				}
				if (getVariableByName.get(this.rules[i].getTo()) != null)
					uConsBinary.setTo(getVariableByName.get(this.rules[i]
							.getTo()));
				else {
					RectangularRegion2 var = (RectangularRegion2) iterSolver
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
		Vector<RectangularRegion2> metaVaribales = new Vector<RectangularRegion2>();
		for (int i = 0; i < sAssertionalRels.length; i++) {

			SpatialFluent sf = currentFluent.get(sAssertionalRels[i].getFrom());
			if (sf == null)
				continue;
			// Add at constraint of indivisuals
			if (sAssertionalRels[i].getUnaryAtRectangleConstraint() != null) {
				RectangularRegion2 var = (RectangularRegion2) iterSolver
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

				UnaryRectangleConstraint2 atCon = new UnaryRectangleConstraint2(
						UnaryRectangleConstraint2.Type.At, atBounds);
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
		Vector<RectangleConstraint2> assertionList = new Vector<RectangleConstraint2>();
		for (int i = 0; i < sAssertionalRels.length; i++) {
			for (int j = 0; j < metaVaribales.size(); j++) {
				if (sAssertionalRels[i].getFrom()
						.compareTo(
								((RectangularRegion2) (metaVaribales.get(j)))
										.getName()) == 0) {
					RectangleConstraint2 assertion = new RectangleConstraint2(
							new AllenIntervalConstraint(
									AllenIntervalConstraint.Type.Equals,
									AllenIntervalConstraint.Type.Equals
											.getDefaultBounds()),
							new AllenIntervalConstraint(
									AllenIntervalConstraint.Type.Equals,
									AllenIntervalConstraint.Type.Equals
											.getDefaultBounds()));

					assertion.setFrom(((RectangularRegion2) metaVaribales
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
				.toArray(new RectangleConstraint2[assertionList.size()])))
			isConsistent = false;
		
//		System.out.println(isConsistent);
//		System.out.println("------------------------------------------------------------");
		return (!isConsistent);
	}

	public void setUsage(SpatialFluent... sfs) {
		if (activities == null)
			activities = new Vector<Activity>();
		for (SpatialFluent sf : sfs)
			if (!activities.contains(sf.getActivity())) {
				activities.add(sf.getActivity());
				activityToFluent.put(sf.getActivity(), sf);
			}

	}

	public void removeUsage(Activity... acts) {
		for (Activity act : acts)
			activities.removeElement(act);
		// System.out.println("-->" + activities.size());
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
		return false;
	}

	private Vector<String> getPotentialCulprit() {
		return potentialCulprit;
	}

	private Vector<String> getInitialUnboundedObject() {
		return initialUnboundedObjName;
	}

	public Vector<HashMap<String, Bounds[]>> generateAllAlternativeSet(Vector<RectangularRegion2> targetRecs) {

		class ConstraintNetworkSortingCritera {

			public double rigidityNumber = 0;
			public int culpritLevel = 0;

			ConstraintNetworkSortingCritera(double rigidityNumber,
					int culpritLevel) {
				this.culpritLevel = culpritLevel;
				this.rigidityNumber = rigidityNumber;
			}
		}

		final HashMap<ConstraintNetwork, ConstraintNetworkSortingCritera> sortingCN = new HashMap<ConstraintNetwork, ConstraintNetworkSortingCritera>();
		HashMap<ConstraintNetwork, HashMap<String, Bounds[]>> cnToInitPose = new HashMap<ConstraintNetwork, HashMap<String, Bounds[]>>();
		for (HashMap<String, Bounds[]> iterCN : permutation.keySet()) {
			RectangleConstraintSolver2 iterSolver = new RectangleConstraintSolver2(origin, horizon);
			HashMap<String, RectangularRegion2> getVariableByName = new HashMap<String, RectangularRegion2>();

			Vector<MultiBinaryConstraint> addedGeneralKn = new Vector<MultiBinaryConstraint>();
			for (int i = 0; i < this.rules.length; i++) {

				if (this.rules[i].getFrom().compareTo(this.rules[i].getTo()) == 0) {
					Bounds[] sizeBounds = new Bounds[this.rules[i]
							.getUnaryRAConstraint().getBounds().length];
					for (int j = 0; j < sizeBounds.length; j++) {
						Bounds bSize = new Bounds(this.rules[i]
								.getUnaryRAConstraint().getBounds()[j].min,
								this.rules[i].getUnaryRAConstraint()
										.getBounds()[j].max);
						sizeBounds[j] = bSize;
					}
					UnaryRectangleConstraint2 uConsSize = new UnaryRectangleConstraint2(
							UnaryRectangleConstraint2.Type.Size, sizeBounds);

					if (getVariableByName.get(this.rules[i].getFrom()) != null)
						uConsSize.setFrom(getVariableByName.get(this.rules[i]
								.getFrom()));
					else {
						RectangularRegion2 var = (RectangularRegion2) iterSolver
								.createVariable();
						var.setName(this.rules[i].getFrom());
						uConsSize.setFrom(var);
						getVariableByName.put(this.rules[i].getFrom(), var);
					}
					if (getVariableByName.get(this.rules[i].getTo()) != null)
						uConsSize.setTo(getVariableByName.get(this.rules[i]
								.getTo()));
					else {
						RectangularRegion2 var = (RectangularRegion2) iterSolver
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

					RectangleConstraint2 uConsBinary = new RectangleConstraint2(
							xAllenCon, yAllenCon);

					if (getVariableByName.get(this.rules[i].getFrom()) != null)
						uConsBinary.setFrom(getVariableByName.get(this.rules[i]
								.getFrom()));
					else {
						RectangularRegion2 var = (RectangularRegion2) iterSolver
								.createVariable();
						var.setName(this.rules[i].getFrom());
						uConsBinary.setFrom(var);
						getVariableByName.put(this.rules[i].getFrom(), var);
					}
					if (getVariableByName.get(this.rules[i].getTo()) != null)
						uConsBinary.setTo(getVariableByName.get(this.rules[i]
								.getTo()));
					else {
						RectangularRegion2 var = (RectangularRegion2) iterSolver
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
			Vector<RectangularRegion2> metaVaribales = new Vector<RectangularRegion2>();

			for (RectangularRegion2 Metavar : targetRecs) {
				RectangularRegion2 var = (RectangularRegion2) iterSolver
						.createVariable();
				var.setName(Metavar.getName());

				Bounds[] atBounds = new Bounds[iterCN.get(Metavar.getName()).length];
				for (int j = 0; j < atBounds.length; j++) {
					Bounds at = new Bounds(
							iterCN.get(Metavar.getName())[j].min,
							iterCN.get(Metavar.getName())[j].max);
					atBounds[j] = at;
				}

				UnaryRectangleConstraint2 atCon = new UnaryRectangleConstraint2(
						UnaryRectangleConstraint2.Type.At, atBounds);
				atCon.setFrom(var);
				atCon.setTo(var);
				metaVaribales.add(var);
				if (!iterSolver.addConstraint(atCon))
					System.out.println("Failed to add AT constraint");
			}

			Vector<RectangleConstraint2> assertionList = new Vector<RectangleConstraint2>();
			for (int i = 0; i < sAssertionalRels.length; i++) {
				for (int j = 0; j < metaVaribales.size(); j++) {
					if (sAssertionalRels[i].getFrom().compareTo(
							((RectangularRegion2) (metaVaribales.get(j)))
									.getName()) == 0) {
						RectangleConstraint2 assertion = new RectangleConstraint2(
								new AllenIntervalConstraint(
										AllenIntervalConstraint.Type.Equals,
										AllenIntervalConstraint.Type.Equals
												.getDefaultBounds()),
								new AllenIntervalConstraint(
										AllenIntervalConstraint.Type.Equals,
										AllenIntervalConstraint.Type.Equals
												.getDefaultBounds()));

						assertion.setFrom(((RectangularRegion2) metaVaribales
								.get(j)));
						assertion.setTo(getVariableByName
								.get(sAssertionalRels[i].getTo()));
						// System.out.println(assertion);
						assertionList.add(assertion);
					}
				}
			}

			boolean isConsistent = true;

			// MetaCSPLogging.setLevel(Level.FINE);
			if (!iterSolver.addConstraints(assertionList
					.toArray(new RectangleConstraint2[assertionList.size()]))) {
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
				RectangleConstraintNetwork2 l1 = (RectangleConstraintNetwork2) o1;
				RectangleConstraintNetwork2 l2 = (RectangleConstraintNetwork2) o2;
				Integer first = (Integer) sortingCN.get(l1).culpritLevel;
				Integer second = (Integer) sortingCN.get(l2).culpritLevel;
				int i = first.compareTo(second);
				if (i != 0)
					return i;

				RectangleConstraintNetwork2 r1 = (RectangleConstraintNetwork2) o1;
				RectangleConstraintNetwork2 r2 = (RectangleConstraintNetwork2) o2;
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
			ConstraintNetwork ct = new RectangleConstraintNetwork2(null);
			ct = (RectangleConstraintNetwork2) i.next();
			alternativeSets.add(cnToInitPose.get(ct));

		}
		return alternativeSets;

	}
	
	private ConstraintNetwork[] completePeakCollection() {

	if (activities != null && !activities.isEmpty()) {
			logger.finest("Doing complete peak collection with " + activities.size() + " activities...");
			
			Vector<Activity> currentacts= removeCulpritsFromCurrentActivity(); 
			
//			System.out.println("currentacts" + currentacts);
			Activity[] groundVars = currentacts.toArray(new Activity[currentacts.size()]);			

			
			Vector<Long> discontinuities = new Vector<Long>();
			for (Activity a : groundVars) {
				long start = a.getTemporalVariable().getEST();
				long end = a.getTemporalVariable().getEST();
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
					Bounds interval1 = new Bounds(a.getTemporalVariable().getEST(), a.getTemporalVariable().getLET());
					Bounds intersection = interval.intersectStrict(interval1);
					if (intersection != null && !intersection.isSingleton()) {
						onePeak.add(a);
					}
				}
			}
			
//			System.out.println("superPeaks" + superPeaks);
			
			Vector<ConstraintNetwork> ret = new Vector<ConstraintNetwork>();
			for (HashSet<Activity> superSet : superPeaks) {
				for (Set<Activity> s : powerSet(superSet)) {
					if (!s.isEmpty()) {
						ActivityNetwork cn = new ActivityNetwork(null);
						for (Activity a : s) cn.addVariable(a); 
						if (!ret.contains(cn) && isConflicting(s.toArray(new Activity[s.size()]))) ret.add(cn);
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

	
	private void generateCombinantion(Vector<UnaryRectangleConstraint2> atConstraints) {

		Vector<UnaryRectangleConstraint2> boundedUnaryCons = new Vector<UnaryRectangleConstraint2>();
		Vector<UnaryRectangleConstraint2> unboundedUnaryCons = new Vector<UnaryRectangleConstraint2>();

		HashMap<Vector<UnaryRectangleConstraint2>, Integer> rank = new HashMap<Vector<UnaryRectangleConstraint2>, Integer>();
		for (int i = 0; i < atConstraints.size(); i++) {
			Bounds[] boundsX = ((AllenIntervalConstraint) ((UnaryRectangleConstraint2) atConstraints
					.get(i)).getInternalConstraints()[0]).getBounds();
			Bounds[] boundsY = ((AllenIntervalConstraint) ((UnaryRectangleConstraint2) atConstraints
					.get(i)).getInternalConstraints()[1]).getBounds();
			if (!isUnboundedBoundingBox(boundsX[0], boundsX[1], boundsY[0],boundsY[1])
					&& ((RectangularRegion2) ((UnaryRectangleConstraint2) atConstraints.get(i))
							.getFrom()).getOntologicalProp().isMovable()) {

				potentialCulprit.add(((RectangularRegion2) ((UnaryRectangleConstraint2) atConstraints.get(i)).getFrom()).getName());
				logger.fine("one potential culprit can be: " + ((RectangularRegion2) ((UnaryRectangleConstraint2) atConstraints.get(i)).getFrom()).getName());
				boundedUnaryCons.add((UnaryRectangleConstraint2) atConstraints.get(i));
			} else {
				if (((RectangularRegion2) ((UnaryRectangleConstraint2) atConstraints.get(i)).getFrom()).getOntologicalProp().isMovable())
					initialUnboundedObjName.add(((RectangularRegion2) ((UnaryRectangleConstraint2) atConstraints.get(i)).getFrom()).getName());
				unboundedUnaryCons.add((UnaryRectangleConstraint2) atConstraints.get(i));
			}
		}

		Combinatorical c = Combinatorical.getPermutations(boundedUnaryCons.size(), 2, true);
//		System.out.println("c.count: " + c.count());
		while (c.hasNext()) {
			int[] combination = c.next();
			int culpritNumber = 0;
			Vector<UnaryRectangleConstraint2> tmpboundedUnaryCons = new Vector<UnaryRectangleConstraint2>();
			Vector<UnaryRectangleConstraint2> justCulprit = new Vector<UnaryRectangleConstraint2>();
			for (int i = 0; i < combination.length; i++) {
				if (combination[i] == 1) {
					UnaryRectangleConstraint2 utmp = new UnaryRectangleConstraint2(
							UnaryRectangleConstraint2.Type.At, new Bounds(0,
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
		 

		for (Vector<UnaryRectangleConstraint2> cc : rank.keySet()) {
			HashMap<String, Bounds[]> culprit = new HashMap<String, Bounds[]>();

			for (int i = 0; i < cc.size(); i++) {

				Bounds[] bounds = new Bounds[4];
				bounds[0] = ((AllenIntervalConstraint) ((UnaryRectangleConstraint2) cc
						.get(i)).getInternalConstraints()[0]).getBounds()[0];
				bounds[1] = ((AllenIntervalConstraint) ((UnaryRectangleConstraint2) cc
						.get(i)).getInternalConstraints()[0]).getBounds()[1];
				bounds[2] = ((AllenIntervalConstraint) ((UnaryRectangleConstraint2) cc
						.get(i)).getInternalConstraints()[1]).getBounds()[0];
				bounds[3] = ((AllenIntervalConstraint) ((UnaryRectangleConstraint2) cc
						.get(i)).getInternalConstraints()[1]).getBounds()[1];

				culprit.put(
						((RectangularRegion2) cc.get(i).getFrom()).getName(),
						bounds);
			}
			for (int i = 0; i < unboundedUnaryCons.size(); i++) {
				Bounds[] bounds = new Bounds[4];
				bounds[0] = ((AllenIntervalConstraint) ((UnaryRectangleConstraint2) unboundedUnaryCons
						.get(i)).getInternalConstraints()[0]).getBounds()[0];
				bounds[1] = ((AllenIntervalConstraint) ((UnaryRectangleConstraint2) unboundedUnaryCons
						.get(i)).getInternalConstraints()[0]).getBounds()[1];
				bounds[2] = ((AllenIntervalConstraint) ((UnaryRectangleConstraint2) unboundedUnaryCons
						.get(i)).getInternalConstraints()[1]).getBounds()[0];
				bounds[3] = ((AllenIntervalConstraint) ((UnaryRectangleConstraint2) unboundedUnaryCons
						.get(i)).getInternalConstraints()[1]).getBounds()[1];

				culprit.put(((RectangularRegion2) unboundedUnaryCons.get(i)
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

	private RectangleConstraintNetwork2 createTBOXspatialNetwork(ConstraintSolver solver,
			HashMap<String, RectangularRegion2> getVariableByName) {

		// general knowledge
		RectangleConstraintNetwork2 ret = new RectangleConstraintNetwork2(
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
				UnaryRectangleConstraint2 uConsSize = new UnaryRectangleConstraint2(
						UnaryRectangleConstraint2.Type.Size, sizeBounds);

				if (getVariableByName.get(this.rules[i].getFrom()) != null)
					uConsSize.setFrom(getVariableByName.get(this.rules[i]
							.getFrom()));
				else {
					RectangularRegion2 var = (RectangularRegion2) solver
							.createVariable();
					var.setName(this.rules[i].getFrom());
					uConsSize.setFrom(var);
					getVariableByName.put(this.rules[i].getFrom(), var);
				}
				if (getVariableByName.get(this.rules[i].getTo()) != null)
					uConsSize
							.setTo(getVariableByName.get(this.rules[i].getTo()));
				else {
					RectangularRegion2 var = (RectangularRegion2) solver
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

				RectangleConstraint2 uConsBinary = new RectangleConstraint2(
						xAllenCon, yAllenCon);

				if (getVariableByName.get(this.rules[i].getFrom()) != null)
					uConsBinary.setFrom(getVariableByName.get(this.rules[i]
							.getFrom()));
				else {
					RectangularRegion2 var = (RectangularRegion2) solver
							.createVariable();
					var.setName(this.rules[i].getFrom());
					uConsBinary.setFrom(var);
					getVariableByName.put(this.rules[i].getFrom(), var);
				}
				if (getVariableByName.get(this.rules[i].getTo()) != null)
					uConsBinary.setTo(getVariableByName.get(this.rules[i]
							.getTo()));
				else {
					RectangularRegion2 var = (RectangularRegion2) solver
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
	
	private void setPermutationHashMAP(Vector<SpatialFluent> conflictvars, Vector<RectangularRegion2> targetRecs){
		
		Vector<UnaryRectangleConstraint2> atConstraints = new Vector<UnaryRectangleConstraint2>();
		HashMap<String, SpatialFluent> currentFluent = new HashMap<String, SpatialFluent>();
//		Vector<RectangularRegion2> targetRecs = new Vector<RectangularRegion2>();

		for (int i = 0; i < conflictvars.size(); i++) {
			currentFluent.put(conflictvars.get(i).getName(), conflictvars.get(i));
		}
		
//		for (int i = 0; i < peak.length; i++) {
//			currentFluent.put(activityToFluent.get(peak[i]).getName(),activityToFluent.get(peak[i]));
//			targetRecs.add(activityToFluent.get(peak[i]).getRectangularRegion());
//		}

		// Add at constraint
		RectangleConstraintSolver2 iterSolver = new RectangleConstraintSolver2(origin, horizon);
		Vector<RectangularRegion2> metaVaribales = new Vector<RectangularRegion2>();
		for (int i = 0; i < sAssertionalRels.length; i++) {

			SpatialFluent sf = currentFluent.get(sAssertionalRels[i].getFrom());
			if (sf == null)
				continue;
			// Add at constraint of indivisuals
			RectangularRegion2 var = (RectangularRegion2) iterSolver.createVariable();
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

				UnaryRectangleConstraint2 atCon = new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, atBounds);
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
