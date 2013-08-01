package spatial.utility;

import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.UnaryRectangleConstraint;


public class SpatialRule2 {
	

	private String from = "";
	private String to = "";
	private RectangleConstraint binaryRA;
	private UnaryRectangleConstraint unaryRA;
	
	public SpatialRule2(String from, String to, UnaryRectangleConstraint unaryRA){
		this.from = from;
		this.to = to;
		this.unaryRA = unaryRA;
	}
	
	public SpatialRule2(String from, String to, RectangleConstraint binaryRA){
		this.from = from;
		this.to = to;
		this.binaryRA = binaryRA;
	}

	
	
	public String getTo() {
		return to;
	}
	
	public String getFrom() {
		return from;
	}
	
	
	
	public UnaryRectangleConstraint getUnaryRAConstraint(){
		return this.unaryRA;
	}
	
	public RectangleConstraint getBinaryRAConstraint(){
		return this.binaryRA;
	}
	
	
	public String toString() {
		if(binaryRA != null)
			return "(" + this.getFrom() + ") --" + this.binaryRA + "--> (" + this.getTo() + ")";
		if(unaryRA != null)
			return "(" + this.getFrom() + ") --" + this.unaryRA + "--> (" + this.getTo() + ")";
		return null;
	}
	
	
	
	

}
