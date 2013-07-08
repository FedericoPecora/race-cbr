package meta;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Vector;

import spatial.rectangleAlgebra_OLD.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra_OLD.AugmentedRectangleConstraintNetwork;
import spatial.rectangleAlgebra_OLD.AugmentedRectangleConstraintSolver;
import time.qualitative.QualitativeAllenIntervalConstraint;
import spatial.rectangleAlgebra_OLD.RectangularRegion;
import spatial.rectangleAlgebra_OLD.SpatialAssertionalRelation;
import spatial.rectangleAlgebra_OLD.SpatialRule;
import spatial.rectangleAlgebra_OLD.TwoDimensionsAllenConstraint;
import framework.Constraint;
import framework.ConstraintNetwork;
import framework.meta.MetaConstraint;
import framework.meta.MetaVariable;

public class MetaSpatialConstraint extends MetaConstraint{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5141205719155181874L;
	private SpatialAssertionalRelation[] sAssertionalRels;
	private AugmentedRectangleConstraintSolver solver;
	private HashMap<String, RectangularRegion> getVariableByName = new HashMap<String, RectangularRegion>();
	private Vector<RectangularRegion> targetRecs = new Vector<RectangularRegion>();
	private HashMap<Vector<RectangularRegion>, Boolean> mark = new HashMap<Vector<RectangularRegion>, Boolean>();
	private Vector<AugmentedRectangleConstraint> addedRAconstraint; 
	
	public MetaSpatialConstraint() {
		//for now!
		super(null, null);
		solver = new AugmentedRectangleConstraintSolver();
		addedRAconstraint = new Vector<AugmentedRectangleConstraint>();
	}
	
	public void setSpatialRules(SpatialRule ...rules){

		for (int i = 0; i < rules.length; i++) {
			if(getVariableByName.get(rules[i].getFrom()) != null )
				rules[i].getRaCons().setFrom(getVariableByName.get(rules[i].getFrom()));
			else{
				RectangularRegion var = (RectangularRegion)solver.createVariable();
				var.setName(rules[i].getFrom());
				rules[i].getRaCons().setFrom(var);
				getVariableByName.put(rules[i].getFrom(), var);
			}
			if(getVariableByName.get(rules[i].getTo()) != null )
				rules[i].getRaCons().setTo(getVariableByName.get(rules[i].getTo()));
			else{
				RectangularRegion var = (RectangularRegion)solver.createVariable();
				var.setName(rules[i].getTo());
				rules[i].getRaCons().setTo(var);
				getVariableByName.put(rules[i].getTo(), var);
			}
			addedRAconstraint.add(rules[i].getRaCons());
		}
	}
	
	public void setSpatialAssertionalRelations(SpatialAssertionalRelation ...sAssertionalRels){
		this.sAssertionalRels  = sAssertionalRels;
		for (int i = 0; i < sAssertionalRels.length; i++){
			//add TBox knowledge which is not already added by spatial constraint 
			if(getVariableByName.get(sAssertionalRels[i].getTo()) == null){
				RectangularRegion var1 = (RectangularRegion)solver.createVariable();
				var1.setName(sAssertionalRels[i].getTo());
				getVariableByName.put(sAssertionalRels[i].getTo(), var1);
			}			
			RectangularRegion var = (RectangularRegion)solver.createVariable();
			var.setName(sAssertionalRels[i].getFrom());
			if(sAssertionalRels[i].getCoordinate() != null)
				var.setBoundingBox(sAssertionalRels[i].getCoordinate());
			if(sAssertionalRels[i].getCoordinate() != null)
				var.setOntologicalProp(sAssertionalRels[i].getOntologicalProp());
			//var.setMarking(markings.UNJUSTIFIED);
			targetRecs.add(var);			
		}
		solver.addConstraints(addedRAconstraint.toArray(new AugmentedRectangleConstraint[addedRAconstraint.size()]));
		mark.put(targetRecs, true);
	}

	
	//set of focused objects is meta variable "not rule"! meta variables are objects instances the named class (Ontological concept) the ARA constraint is just there
	//on the ground constraint network
	@Override
	public ConstraintNetwork[] getMetaVariables() {
		
		Vector<AugmentedRectangleConstraintNetwork> ret = new Vector<AugmentedRectangleConstraintNetwork>();
		AugmentedRectangleConstraintNetwork raNetwork = new AugmentedRectangleConstraintNetwork(null);
		if(mark.get(targetRecs)){
			for (int i = 0; i < targetRecs.size(); i++) {				
				raNetwork.addVariable(targetRecs.get(i));			
			}
			ret.add(raNetwork);
			return ret.toArray(new ConstraintNetwork[ret.size()]);
		}
		else
			return null;
	}
	
	//meta value essentially is the position of meta values
	@Override
	public ConstraintNetwork[] getMetaValues(MetaVariable metaVariable) {
		
		if(metaVariable == null)
			return null;
		ConstraintNetwork conflict = metaVariable.getConstraintNetwork();
		Vector<AugmentedRectangleConstraint> assertionList = new Vector<AugmentedRectangleConstraint>();
		for (int i = 0; i < sAssertionalRels.length; i++) {
			for (int j = 0; j < conflict.getVariables().length; j++) {
				if(sAssertionalRels[i].getFrom().compareTo(((RectangularRegion)conflict.getVariables()[j]).getName()) == 0){
					AugmentedRectangleConstraint assertion = new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Equals, 
							QualitativeAllenIntervalConstraint.Type.Equals));
					assertion.setFrom(((RectangularRegion)conflict.getVariables()[j]));
					assertion.setTo(getVariableByName.get(sAssertionalRels[i].getTo()));
					assertionList.add(assertion);
					break;
				}
			}
		}
		AugmentedRectangleConstraintNetwork unification = new AugmentedRectangleConstraintNetwork(null);
		for (int i = 0; i < assertionList.size(); i++) {
			unification.addConstraint(assertionList.get(i));
		}
		ConstraintNetwork[] ret = new ConstraintNetwork[]{unification}; 		
		return ret;
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
		//metaVariable.getConstraintNetwork().getVariables()[0].setMarking(markings.JUSTIFIED);
		mark.put(targetRecs, false);
		
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
	
	public String getGnuplotScript(String ...st){
		return solver.drawAlmostCentreRectangle(30, st);
	}
	
	public Rectangle getRectangle(String st){
		return solver.extractBoundingBoxesFromSTPs(st).getAlmostCentreRectangle();
	}
	
	public void culpritDetector(){
		solver.minimalCulpritDetector();
	}
	
	public RectangularRegion[] getCulprits(){
		return solver.getCulpritSet();
	}
	
	//this method return consistency, because the metaSpatial constraint always return true,(because we do not want the relations retracted)
	public boolean isConsistent(){
		return solver.inConsistent();
	}

}
