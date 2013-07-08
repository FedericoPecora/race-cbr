
import java.util.ArrayList;
import java.util.Vector;

import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import race_msgs.AddFluent;
import race_msgs.AddFluentRequest;
import race_msgs.AddFluentResponse;
import race_msgs.FlexibleTimepoint;
import race_msgs.Fluent;
import race_msgs.IsConsistent;
import race_msgs.IsConsistentRequest;
import race_msgs.IsConsistentResponse;
import race_msgs.IsHumanWorkingRequest;
import race_msgs.Property;

import spatial.*;
import spatial.rectangleAlgebra_OLD.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra_OLD.SpatialRule;



/**
 * 
 * @author Iran Mansouri
 */


public class Client extends AbstractNodeMain {

	private ConnectedNode node;
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("ask_for_position");
	}

	@Override
	public void onStart(final ConnectedNode connectedNode) {

		//MetaCSPLogging.setLevel(MetaSpatialConstraintSolver.class, Level.FINEST);
		//MetaCSPLogging.setLevel(RectangleConstraintSolver.class, Level.FINEST);
		this.node = connectedNode;
//		addConceptualObjectFluent("cnsp3");
//		addConceptualObjectFluent("cnsp4");
//		addAllenConstraint("finishes1", "Finishes");
//		addAllenConstraint("meets2", "Meets");
//		addRAconstraint("finishMeet1");
		
		Vector<SpatialRule> spatialKnowledge = new Vector<SpatialRule>();
		StaticSpatialKnowledge.getSpatialKnowledge(spatialKnowledge);
		for (SpatialRule spatialRule : spatialKnowledge) {
			addRAconstraint(spatialRule.getFrom() + "_" + spatialRule.getTo(), spatialRule.getRaCons());
		}
		
	}
		



	private void addConceptualObjectFluent(String str) {
				
		Fluent f = node.getTopicMessageFactory().newFromType(Fluent._TYPE);
		f.setName(str);
		f.setType("ConceptualObject");
		FlexibleTimepoint ft1 = node.getTopicMessageFactory().newFromType(FlexibleTimepoint._TYPE);
		ft1.setLower(new Time(0));
		ft1.setUpper(new Time(0));
		f.setStartTime(ft1);
		FlexibleTimepoint ft2 = node.getTopicMessageFactory().newFromType(FlexibleTimepoint._TYPE);
		ft2.setLower(new Time(0));
		ft2.setUpper(new Time(0));
		f.setFinishTime(ft2);

		ServiceClient<AddFluentRequest, AddFluentResponse> serviceClient = null;
		try {
			serviceClient = node.newServiceClient("blackboard/add_fluent", AddFluent._TYPE);

		} 
		catch (org.ros.exception.ServiceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final AddFluentRequest request = serviceClient.newMessage();
		request.setFluent(f);

		serviceClient.call(request, new ServiceResponseListener<AddFluentResponse>() {

			@Override
			public void onSuccess(AddFluentResponse response) {
				System.out.println("ADD ConceptualObject");
				System.out.println(response.getResult().getResultCode());
				System.out.println(response.getResult().getErrorMessage());
			}

			@Override
			public void onFailure(RemoteException arg0) {
				System.out.println("system call failed");

			}
		});


	}
	
	private void addAllenConstraint(String st, String type) {
		
		Fluent f = node.getTopicMessageFactory().newFromType(Fluent._TYPE);
		f.setName(st);
		f.setType(type);
		ArrayList<Property> props = new ArrayList<Property>();

		//[hasBoundingBox, BoundingBox, boundingBoxPAERCounter1]
		Property prop = node.getTopicMessageFactory().newFromType(Property._TYPE);
		prop.setRoleType("hasFirst");
		prop.setFillerType("ConceptualObject");
		prop.setObjectFiller("cnsp3");
		props.add(prop);

		//[hasPose, Pose, posePAERCounter1]
		Property prop1 = node.getTopicMessageFactory().newFromType(Property._TYPE);
		prop1.setRoleType("hasSecond");
		prop1.setFillerType("ConceptualObject");
		prop1.setObjectFiller("cnsp4");
		props.add(prop1);

		f.setProperties(props);



		ServiceClient<AddFluentRequest, AddFluentResponse> serviceClient = null;
		try {
			serviceClient = node.newServiceClient("blackboard/add_fluent", AddFluent._TYPE);

		} 
		catch (org.ros.exception.ServiceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final AddFluentRequest request = serviceClient.newMessage();
		request.setFluent(f);

		serviceClient.call(request, new ServiceResponseListener<AddFluentResponse>() {

			@Override
			public void onSuccess(AddFluentResponse response) {
				System.out.println("Update fleunt to b");
				System.out.println(response.getResult().getResultCode());
				System.out.println(response.getResult().getErrorMessage());
			}

			@Override
			public void onFailure(RemoteException arg0) {
				System.out.println("system call failed");

			}
		});

	}
	
	private void addRAconstraint(String st, AugmentedRectangleConstraint ara) {
		
//		upper:SpatialConstraint
//		upper:hasFirst exactly 1 upper:PassiveObject
//		upper:hasSecond exactly 1 upper:PassiveObject
//		upper:hasYAllenConstraint exactly 1 upper:AllenConstraint
//		upper:hasXAllenConstraint exactly 1 upper:AllenConstraint
		
		Fluent f = node.getTopicMessageFactory().newFromType(Fluent._TYPE);
		f.setName(st);
		f.setType("RectangleAlgebraConstraints");
		ArrayList<Property> props = new ArrayList<Property>();

		//[hasBoundingBox, BoundingBox, boundingBoxPAERCounter1]
		Property prop = node.getTopicMessageFactory().newFromType(Property._TYPE);
		prop.setRoleType("hasXAllenConstraint");
		prop.setFillerType("Meets");
		prop.setObjectFiller("meets2");
		props.add(prop);

		//[hasPose, Pose, posePAERCounter1]
		Property prop1 = node.getTopicMessageFactory().newFromType(Property._TYPE);
		prop1.setRoleType("hasYAllenConstraint");
		prop1.setFillerType("Finishes");
		prop1.setObjectFiller("finishes1");
		props.add(prop1);

		f.setProperties(props);



		ServiceClient<AddFluentRequest, AddFluentResponse> serviceClient = null;
		try {
			serviceClient = node.newServiceClient("blackboard/add_fluent", AddFluent._TYPE);

		} 
		catch (org.ros.exception.ServiceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final AddFluentRequest request = serviceClient.newMessage();
		request.setFluent(f);

		serviceClient.call(request, new ServiceResponseListener<AddFluentResponse>() {

			@Override
			public void onSuccess(AddFluentResponse response) {
				System.out.println("Add RA constraint");
				System.out.println(response.getResult().getResultCode());
				System.out.println(response.getResult().getErrorMessage());
			}

			@Override
			public void onFailure(RemoteException arg0) {
				System.out.println("system call failed");

			}
		});
		
	}


}
