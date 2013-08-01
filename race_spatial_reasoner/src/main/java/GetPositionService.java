
import java.awt.Rectangle;
import java.util.Vector;

import meta.MetaSpatialConstraint;
import meta.MetaSpatialConstraintSolver;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import spatial.rectangleAlgebra_OLD.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra_OLD.SpatialAssertionalRelation;
import spatial.rectangleAlgebra_OLD.SpatialRule;
import spatial.rectangleAlgebra_OLD.TwoDimensionsAllenConstraint;
import org.metacsp.time.qualitative.QualitativeAllenIntervalConstraint;
import org.metacsp.time.Bounds;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceResponseBuilder;
import org.ros.node.service.ServiceServer;


import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceResponseBuilder;
import org.ros.node.service.ServiceServer;



public class GetPositionService  extends AbstractNodeMain{


	private Vector<SpatialRule> srules = null;


//	@Override
//	public GraphName getDefaultNodeName() {
//		return new GraphName("get_position_service");
//	}
	


	@Override
	public void onStart(ConnectedNode connectedNode) {
//			    connectedNode.newServiceServer("add_two_ints", test_ros.AddTwoInts._TYPE,
//			        new ServiceResponseBuilder<AddTwoInts.Request, test_ros.AddTwoInts.Response>() {
//			          @Override
//			          public void build(test_ros.AddTwoInts.Request request,
//			              test_ros.AddTwoInts.Response response) {
//			            response.setSum(request.getA() + request.getB());
//			          }
//			        });
	}

	private void buildSpatialKnowledge(){
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

		buildSpatialKnowledge();
		MetaSpatialConstraintSolver metaSolver = new MetaSpatialConstraintSolver(0);				
		MetaSpatialConstraint objectsPosition = new MetaSpatialConstraint();
		objectsPosition.setSpatialRules(srules.toArray(new SpatialRule[srules.size()]));
		objectsPosition.setSpatialAssertionalRelations(saRelations.toArray(new SpatialAssertionalRelation[saRelations.size()]));



		metaSolver.addMetaConstraint(objectsPosition);
		if(metaSolver.backtrack())
			return objectsPosition.getRectangle(objName);
		return null;
	}

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("get_position_service");
	}


}
