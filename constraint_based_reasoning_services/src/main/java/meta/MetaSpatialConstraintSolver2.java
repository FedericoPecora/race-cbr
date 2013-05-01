package meta;

import sandbox.spatial.rectangleAlgebra2.RectangleConstraint2;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraintSolver2;
import sandbox.spatial.rectangleAlgebra2.UnaryRectangleConstraint2;
import spatial.rectangleAlgebra.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra.AugmentedRectangleConstraintSolver;
import framework.ConstraintNetwork;
import framework.meta.MetaConstraintSolver;
import framework.meta.MetaVariable;

public class MetaSpatialConstraintSolver2  extends MetaConstraintSolver{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8700618716230870106L;

	public MetaSpatialConstraintSolver2(long origin, long horizon, long animationTime) {
		super(new Class[]{RectangleConstraint2.class, UnaryRectangleConstraint2.class}, 
				animationTime, new RectangleConstraintSolver2(origin, horizon));
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
		// TODO Auto-generated method stub
		
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

