import java.awt.Rectangle;
import java.util.Vector;

import meta.MetaSpatialConstraint;
import meta.MetaSpatialConstraintSolver;

import spatial.rectangleAlgebra.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra.QualitativeAllenIntervalConstraint;
import spatial.rectangleAlgebra.SpatialAssertionalRelation;
import spatial.rectangleAlgebra.SpatialRule;
import spatial.rectangleAlgebra.TwoDimensionsAllenConstraint;




public class IsSpatiallyConsistent {
	
	private Vector<SpatialRule> srules = null;

	public void buildSpatialKnowledge(){
		
		srules = new Vector<SpatialRule>();
		SpatialRule r1 = new SpatialRule("cup", "dish", 
				new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.During, 
				QualitativeAllenIntervalConstraint.Type.After), new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.OverlappedBy, 
						QualitativeAllenIntervalConstraint.Type.After)));
		srules.add(r1);

		
		SpatialRule r2 = new SpatialRule("knife", "dish", 
				new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.After, 
						QualitativeAllenIntervalConstraint.Type.During)));
		srules.add(r2);
		
		SpatialRule r3 = new SpatialRule("fork", "dish", 
				new AugmentedRectangleConstraint(new TwoDimensionsAllenConstraint(QualitativeAllenIntervalConstraint.Type.Before,
						QualitativeAllenIntervalConstraint.Type.During)));
		srules.add(r3);

	
	}
	
	
	public synchronized  boolean isMetaConstraintConsistent(String objName,
			Vector<SpatialAssertionalRelation> saRelations) {
		
		buildSpatialKnowledge();
		MetaSpatialConstraintSolver metaSolver = new MetaSpatialConstraintSolver(0);
		MetaSpatialConstraint objectsPosition = new MetaSpatialConstraint();
		
		System.out.println(srules.get(0));
		objectsPosition.setSpatialRules(srules.toArray(new SpatialRule[srules.size()]));
		objectsPosition.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation[saRelations.size()]));
		
		
		
		metaSolver.addMetaConstraint(objectsPosition);
		if(metaSolver.backtrack()){
			return true;
		}
			
		return false;
	}


}
