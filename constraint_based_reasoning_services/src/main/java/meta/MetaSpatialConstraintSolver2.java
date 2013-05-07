package meta;

import sandbox.spatial.rectangleAlgebra2.RectangleConstraint2;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraintSolver2;
import sandbox.spatial.rectangleAlgebra2.UnaryRectangleConstraint2;
import spatial.rectangleAlgebra.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra.AugmentedRectangleConstraintSolver;
import time.Bounds;
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
		
//		System.out.println("metaValue: " + metaValue);
		
		for (int i = 0; i < metaValue.getConstraints().length; i++) {
			if(metaValue.getConstraints()[i] instanceof UnaryRectangleConstraint2)
				//this if will check for unboudned obj in order to create the goal
				if(((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getType().equals(UnaryRectangleConstraint2.Type.At)) 
					if(((MetaSpatialConstraint2)this.metaConstraints.get(0)).isUnboundedBoundingBox(
							((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getBounds()[0], 
							((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getBounds()[1], 
							((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getBounds()[2], 
							((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getBounds()[3]));
//						System.out.println(((UnaryRectangleConstraint2)metaValue.getConstraints()[i]).getTo());
					
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

