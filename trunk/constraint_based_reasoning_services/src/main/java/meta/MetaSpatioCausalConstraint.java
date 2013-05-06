package meta;

import java.util.Vector;

import sandbox.spatial.rectangleAlgebra2.RectangleConstraintSolver2;
import sandbox.spatial.rectangleAlgebra2.SpatialAssertionalRelation2;
import sandbox.spatial.rectangleAlgebra2.SpatialRule2;
import spatial.rectangleAlgebra.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra.AugmentedRectangleConstraintSolver;
import spatial.rectangleAlgebra.RectangularRegion;
import spatial.rectangleAlgebra.SpatialAssertionalRelation;
import spatial.rectangleAlgebra.SpatialRule;
import time.APSPSolver;
import time.Bounds;
import meta.simplePlanner.SimpleDomain;
import meta.simplePlanner.SimpleDomain.markings;
import multi.activity.Activity;
import multi.activity.ActivityNetwork;
import multi.activity.ActivityNetworkSolver;
import multi.allenInterval.AllenIntervalConstraint;
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
    private SpatialAssertionalRelation2[] sAssertionalRels;
    private SpatialRule2[] rules;
    private MetaSpatialConstraintSolver metaSpatialConstrainSolver = new MetaSpatialConstraintSolver(0);
    private MetaSpatialConstraint objectsPosition = new MetaSpatialConstraint();
	int counter = 0;
    long origin = 0, horizon = 1000;
	
    public MetaSpatioCausalConstraint(int[] capacities, String[] resourceNames,
			String domainName, SpatialRule2[] rules) {
				
		super(capacities, resourceNames, domainName);
		this.rules = rules;
		// TODO Auto-generated constructor stub
	}
	
    
	
	public void setSpatialAssertionalRelations(SpatialAssertionalRelation2 ...sAssertionalRels){		
		this.sAssertionalRels = sAssertionalRels;		
	}
	
	private void getUnbounedRectangularRegion(){		
		
		
		if(counter == 0){
			ActivityNetworkSolver groundSolver = (ActivityNetworkSolver)this.metaCS.getConstraintSolvers()[0];		
			Activity one = (Activity)groundSolver.createVariable("cup1");
			one.setSymbolicDomain("at()");
			one.setMarking(markings.UNJUSTIFIED);
			counter++;
					
			Activity two = (Activity)groundSolver.createVariable("knife1");
			two.setSymbolicDomain("at()");
			two.setMarking(markings.UNJUSTIFIED);
			counter++;
			
			AllenIntervalConstraint twoAfterOne = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After);
			twoAfterOne.setFrom(one);
			twoAfterOne.setTo(two);
			groundSolver.addConstraints(new Constraint[] {twoAfterOne});
		}
		
	}
	
	private void synthesizeSpatialknowlege(){
		


		
	}
	
	@Override
	public ConstraintNetwork[] getMetaVariables() {
//		synthesizeSpatialknowlege();
		
		//if getUnboundedObjected changed
		getUnbounedRectangularRegion();
		
		return super.getMetaVariables();
	}

}
