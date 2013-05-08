package meta;

import java.util.HashMap;


import meta.MetaCausalConstraint.markings;
import multi.activity.Activity;

import multi.allenInterval.AllenIntervalConstraint;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraint2;
import sandbox.spatial.rectangleAlgebra2.RectangularRegion2;
import sandbox.spatial.rectangleAlgebra2.SpatialFluent;
import sandbox.spatial.rectangleAlgebra2.SpatialFluentSolver;
import sandbox.spatial.rectangleAlgebra2.UnaryRectangleConstraint2;
import symbols.SymbolicValueConstraint;
import framework.ConstraintNetwork;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addResolverSub(ConstraintNetwork metaVariable,
			ConstraintNetwork metaValue) {
		
				
		for (int i = 0; i < metaValue.getConstraints().length; i++) {
			if(metaValue.getConstraints()[i] instanceof UnaryRectangleConstraint2)
				//this if will check for unboudned obj in order to create the goal
				if(((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getType().equals(UnaryRectangleConstraint2.Type.At)) 
					if(((MetaSpatialFluentConstraint)this.metaConstraints.get(0)).isUnboundedBoundingBox(
							((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getBounds()[0], 
							((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getBounds()[1], 
							((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getBounds()[2], 
							((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getBounds()[3])){
						
						for (int j = 0; j < metaVariable.getVariables().length; j++) {
							if(((RectangularRegion2)metaValue.getConstraints()[i].getScope()[0]).getName().compareTo
									(((SpatialFluent)metaVariable.getVariables()[j]).getName()) == 0)
								((Activity)((SpatialFluent)metaVariable.getVariables()[j]).getActivity()).setMarking(markings.UNJUSTIFIED);
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
