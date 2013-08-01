package meta;


import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraintSolver;
import org.metacsp.multi.spatial.rectangleAlgebra.UnaryRectangleConstraint;

import org.metacsp.framework.ConstraintNetwork;
import org.metacsp.framework.meta.MetaConstraintSolver;
import org.metacsp.framework.meta.MetaVariable;

public class MetaSpatialConstraintSolver2  extends MetaConstraintSolver{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8700618716230870106L;

	public MetaSpatialConstraintSolver2(long origin, long horizon, long animationTime) {
		super(new Class[]{RectangleConstraint.class, UnaryRectangleConstraint.class}, 
				animationTime, new RectangleConstraintSolver(origin, horizon));
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
	protected boolean addResolverSub(ConstraintNetwork metaVariable,
			ConstraintNetwork metaValue) {
		
//		System.out.println("metaValue: " + metaValue);
		
		for (int i = 0; i < metaValue.getConstraints().length; i++) {
			if(metaValue.getConstraints()[i] instanceof UnaryRectangleConstraint)
				//this if will check for unboudned obj in order to create the goal
				if(((UnaryRectangleConstraint)metaValue.getConstraints()[i]).getType().equals(UnaryRectangleConstraint.Type.At)) 
					if(((MetaSpatialConstraint2)this.metaConstraints.get(0)).isUnboundedBoundingBox(
							((UnaryRectangleConstraint)metaValue.getConstraints()[i]).getBounds()[0], 
							((UnaryRectangleConstraint)metaValue.getConstraints()[i]).getBounds()[1], 
							((UnaryRectangleConstraint)metaValue.getConstraints()[i]).getBounds()[2], 
							((UnaryRectangleConstraint)metaValue.getConstraints()[i]).getBounds()[3]));
//						System.out.println(((UnaryRectangleConstraint)metaValue.getConstraints()[i]).getTo());
					
		}
		return true;
		
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

