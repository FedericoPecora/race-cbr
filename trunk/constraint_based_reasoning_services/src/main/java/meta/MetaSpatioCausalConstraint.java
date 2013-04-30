package meta;

import java.util.Vector;

import spatial.rectangleAlgebra.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra.AugmentedRectangleConstraintSolver;
import spatial.rectangleAlgebra.RectangularRegion;
import spatial.rectangleAlgebra.SpatialAssertionalRelation;
import spatial.rectangleAlgebra.SpatialRule;
import meta.simplePlanner.SimpleDomain;
import meta.simplePlanner.SimpleDomain.markings;
import multi.activity.Activity;
import multi.activity.ActivityNetwork;
import multi.activity.ActivityNetworkSolver;
import multi.allenInterval.AllenIntervalNetworkSolver;
import framework.Constraint;
import framework.ConstraintNetwork;
import framework.ConstraintSolver;
import framework.ValueOrderingH;
import framework.Variable;
import framework.VariableOrderingH;
import framework.meta.MetaConstraint;
import framework.meta.MetaVariable;

public class MetaSpatioCausalConstraint extends SimpleDomain{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private SpatialAssertionalRelation[] sAssertionalRels;
    private SpatialRule[] rules;
    private MetaSpatialConstraintSolver metaSpatialConstrainSolver = new MetaSpatialConstraintSolver(0);
    private MetaSpatialConstraint objectsPosition = new MetaSpatialConstraint();
	int counter = 0;
    
    public MetaSpatioCausalConstraint(int[] capacities, String[] resourceNames,
			String domainName, SpatialRule[] rules) {
				
		super(capacities, resourceNames, domainName);
		this.rules = rules;
		// TODO Auto-generated constructor stub
	}
	
    
	
	public void setSpatialAssertionalRelations(SpatialAssertionalRelation ...sAssertionalRels){
		
		this.sAssertionalRels = sAssertionalRels;
		
	}
	
	private void getUnbounedRectangularRegion(){		
		//creat the unjustified activities
		//get coordinat of rectangularRegion
		
		
		 
		AugmentedRectangleConstraintSolver gs= (AugmentedRectangleConstraintSolver)metaSpatialConstrainSolver.getConstraintSolvers()[0];
		System.out.println("heloooooooooooo"+ ((AllenIntervalNetworkSolver)((AugmentedRectangleConstraintSolver)metaSpatialConstrainSolver.getConstraintSolvers()[0]).getInternalSolver()[0]).getVariables().length);
		for (int i = 0; i < gs.getVariables().length; i++) {
			System.out.println(gs.getVariables()[i]);
		}
		
		
		
		
		if(counter == 0){
			ActivityNetworkSolver groundSolver = (ActivityNetworkSolver)this.metaCS.getConstraintSolvers()[0];		
			Activity one = (Activity)groundSolver.createVariable("cup1");
			one.setSymbolicDomain("at()");
			one.setMarking(markings.UNJUSTIFIED);
			counter++;
		}
		
	}
	
	private void synthesizeSpatialknowlege(){
		

		objectsPosition.setSpatialRules(this.rules);
		objectsPosition.setSpatialAssertionalRelations(this.sAssertionalRels);

		metaSpatialConstrainSolver.addMetaConstraint(objectsPosition);
		
		metaSpatialConstrainSolver.backtrack();
		
	}
	
	@Override
	public ConstraintNetwork[] getMetaVariables() {
		synthesizeSpatialknowlege();
		
		//if getUnboundedObjected changed
		getUnbounedRectangularRegion();
		
		return super.getMetaVariables();
	}

}
