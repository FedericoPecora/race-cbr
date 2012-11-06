package race_spatial_reasoner;

import java.awt.Rectangle;
import java.util.Vector;

import meta.MetaSpatialConstraint;
import meta.MetaSpatialConstraintSolver;
import multi.allenInterval.AllenIntervalConstraint;
import spatial.rectangleAlgebra.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra.QualitativeAllenIntervalConstraint;
import spatial.rectangleAlgebra.SpatialAssertionalRelation;
import spatial.rectangleAlgebra.SpatialRule;
import spatial.rectangleAlgebra.TwoDimensionsAllenConstraint;
import time.Bounds;

public class GetPositionService  {
	

	private Vector<SpatialRule> srules = null;
	
	public GetPositionService(){
		
	}
	
	private synchronized void buildSpatialKnowledge(){
		srules = new Vector<SpatialRule>();
		SpatialRule r1 = new SpatialRule("cup", "dish", 
				new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.During, 
				QualitativeAllenIntervalConstraint.Type.After), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.OverlappedBy, 
						QualitativeAllenIntervalConstraint.Type.After)));
		srules.add(r1);

		SpatialRule r2 = new SpatialRule("knife", "dish", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(4, 10)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()))
		);
		srules.add(r2);
		
		SpatialRule r3 = new SpatialRule("fork", "dish", 
				new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Before,
						QualitativeAllenIntervalConstraint.Type.During)));
		srules.add(r3);

		SpatialRule r4 = new SpatialRule("dish", "dish", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(10, 20)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10, 20))));
		srules.add(r4);
		
	}



	public synchronized  Rectangle getRec(String objName,
			Vector<SpatialAssertionalRelation> saRelations) {
		
		System.out.println("heloooooooooooooooooooooooooooooooo");
		buildSpatialKnowledge();
		System.out.println("byeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
		MetaSpatialConstraintSolver metaSolver = new MetaSpatialConstraintSolver(0);				
		MetaSpatialConstraint objectsPosition = new MetaSpatialConstraint();
		objectsPosition.setSpatialRules(srules.toArray(new SpatialRule[srules.size()]));
		objectsPosition.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation[saRelations.size()]));
		
		System.out.println("fooooooooooooooooooooooooooooooooooooo");
		
		metaSolver.addMetaConstraint(objectsPosition);
		if(metaSolver.backtrack())
			return objectsPosition.getRectangle(objName);
		return null;
	}


}
