package meta;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;

import meta.simplePlanner.SimpleDomain.markings;
import multi.allenInterval.AllenIntervalConstraint;

import sandbox.spatial.rectangleAlgebra2.RectangleConstraint2;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraintNetwork2;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraintSolver2;
import sandbox.spatial.rectangleAlgebra2.RectangularRegion2;
import sandbox.spatial.rectangleAlgebra2.SpatialAssertionalRelation2;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraintSolver2;
import sandbox.spatial.rectangleAlgebra2.SpatialRule2;
import sandbox.spatial.rectangleAlgebra2.UnaryRectangleConstraint2;
import spatial.rectangleAlgebra.AugmentedRectangleConstraintNetwork;
import utility.logging.MetaCSPLogging;
import framework.Constraint;
import framework.ConstraintNetwork;
import framework.meta.MetaConstraint;
import framework.meta.MetaVariable;
import framework.multi.MultiBinaryConstraint;

public class MetaSpatialConstraint2 extends MetaConstraint{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6726241392958991868L;
	private SpatialAssertionalRelation2[] sAssertionalRels;
	private RectangleConstraintSolver2 solver;
	private HashMap<String, RectangularRegion2> getVariableByName = new HashMap<String, RectangularRegion2>();
	private Vector<RectangularRegion2> targetRecs = new Vector<RectangularRegion2>();
	private Vector<MultiBinaryConstraint> addedGeneralKn;
	private Vector<UnaryRectangleConstraint2> addedUnaryConstraint;
	private HashMap<Vector<RectangularRegion2>, Boolean> mark = new HashMap<Vector<RectangularRegion2>, Boolean>();
	
	public MetaSpatialConstraint2() {
		//for now!
		super(null, null);
		solver = new RectangleConstraintSolver2(0,1000);
		addedGeneralKn = new Vector<MultiBinaryConstraint>();
		addedUnaryConstraint = new Vector<UnaryRectangleConstraint2>();  
	}
	
	public void setSpatialRules(SpatialRule2 ...rules){
		
		//Add size and RA constraint
				for (int i = 0; i < rules.length; i++) {
			if(getVariableByName.get(rules[i].getFrom()) != null )
				rules[i].getRAConstraint().setFrom(getVariableByName.get(rules[i].getFrom()));
			else{
				RectangularRegion2 var = (RectangularRegion2)solver.createVariable();
				var.setName(rules[i].getFrom());
				rules[i].getRAConstraint().setFrom(var);
				getVariableByName.put(rules[i].getFrom(), var);
			}
			if(getVariableByName.get(rules[i].getTo()) != null )
				rules[i].getRAConstraint().setTo(getVariableByName.get(rules[i].getTo()));
			else{
				RectangularRegion2 var = (RectangularRegion2)solver.createVariable();
				var.setName(rules[i].getTo());
				rules[i].getRAConstraint().setTo(var);
				getVariableByName.put(rules[i].getTo(), var);
			}
			addedGeneralKn.add(rules[i].getRAConstraint());
			
		}
		
	}
	

	
	public void setSpatialAssertionalRelations(SpatialAssertionalRelation2 ...sAssertionalRels){
		

		this.sAssertionalRels  = sAssertionalRels;
		for (int i = 0; i < sAssertionalRels.length; i++){
			//add TBox knowledge which is not already added by spatial constraint 
			if(getVariableByName.get(sAssertionalRels[i].getTo()) == null){
				RectangularRegion2 var1 = (RectangularRegion2)solver.createVariable();
				var1.setName(sAssertionalRels[i].getTo());
				getVariableByName.put(sAssertionalRels[i].getTo(), var1);
			}			
			RectangularRegion2 var = (RectangularRegion2)solver.createVariable();
			var.setName(sAssertionalRels[i].getFrom());
			
			//we set no variables for this unary relation
			if(sAssertionalRels[i].getUnaryAtRectangleConstraint() != null)	{	
				sAssertionalRels[i].getUnaryAtRectangleConstraint().setFrom(var);
				sAssertionalRels[i].getUnaryAtRectangleConstraint().setTo(var);
				addedUnaryConstraint.add(sAssertionalRels[i].getUnaryAtRectangleConstraint());
			}
				
			if(sAssertionalRels[i].getUnaryAtRectangleConstraint() != null)
				var.setOntologicalProp(sAssertionalRels[i].getOntologicalProp());
			//var.setMarking(markings.UNJUSTIFIED);
			targetRecs.add(var);			
		}

		solver.addConstraints(addedUnaryConstraint.toArray(new UnaryRectangleConstraint2[addedUnaryConstraint.size()]));
		solver.addConstraints(addedGeneralKn.toArray(new MultiBinaryConstraint[addedGeneralKn.size()]));
		
		mark.put(targetRecs, true);
	}

	

	@Override
	public ConstraintNetwork[] getMetaVariables() {
		
		Vector<RectangleConstraintNetwork2> ret = new Vector<RectangleConstraintNetwork2>();
		RectangleConstraintNetwork2 raNetwork = new RectangleConstraintNetwork2(null);
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
		Vector<RectangleConstraint2> assertionList = new Vector<RectangleConstraint2>();
		for (int i = 0; i < sAssertionalRels.length; i++) {
			for (int j = 0; j < conflict.getVariables().length; j++) {
				if(sAssertionalRels[i].getFrom().compareTo(((RectangularRegion2)(conflict.getVariables()[j])).getName()) == 0){
					RectangleConstraint2 assertion = new RectangleConstraint2(
							new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals), 
							new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals));
					
					assertion.setFrom(((RectangularRegion2)conflict.getVariables()[j]));
					assertion.setTo(getVariableByName.get(sAssertionalRels[i].getTo()));
					assertionList.add(assertion);
					break;
				}
			}
		}
		
		MetaCSPLogging.setLevel(Level.FINEST);
		RectangleConstraintNetwork2 unification = new RectangleConstraintNetwork2(null);
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
	
	public Rectangle getRectangle(String st){
		return solver.extractBoundingBoxesFromSTPs(st).getAlmostCentreRectangle();
    }
	

	

}
