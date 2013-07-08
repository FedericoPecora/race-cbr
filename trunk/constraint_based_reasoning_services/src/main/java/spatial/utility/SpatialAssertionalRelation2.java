package spatial.utility;

import multi.spatial.rectangleAlgebra.UnaryRectangleConstraint;
import multi.spatial.rectangleAlgebraNew.toRemove.OntologicalSpatialProperty;
import multi.spatioTemporal.SpatialFluent;


public class SpatialAssertionalRelation2 extends AssertionalRelation{
	
	OntologicalSpatialProperty ontologicalProp;
	UnaryRectangleConstraint unaryRAConstraint;
	SpatialFluent spatialFluent;
	
	public SpatialAssertionalRelation2(String from, String to) {
		super(from, to);
		// TODO Auto-generated constructor stub
	}
	
	public void setUnaryAtRectangleConstraint(UnaryRectangleConstraint unaryRAConstraint){
		this.unaryRAConstraint = unaryRAConstraint;
	}
	
	public UnaryRectangleConstraint getUnaryAtRectangleConstraint(){
		return this.unaryRAConstraint;
	}
		
	public void setOntologicalProp(OntologicalSpatialProperty ontologicalProp) {
		this.ontologicalProp = ontologicalProp;
	}
	
	public OntologicalSpatialProperty getOntologicalProp() {
		return ontologicalProp;
	}
	
	public void associateSpatialFlunt (SpatialFluent spatialFluent){
		this.spatialFluent = spatialFluent;
	}
	
	public SpatialFluent getSpatialFleunt(){
		return this.spatialFluent;
	}
	
}
