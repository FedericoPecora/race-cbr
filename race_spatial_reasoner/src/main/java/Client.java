
import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import race_msgs.IsConsistent;
import race_msgs.IsConsistentRequest;
import race_msgs.IsConsistentResponse;
import race_msgs.IsHumanWorkingRequest;



/**
 * 
 * @author Iran Mansouri
 */
public class Client extends AbstractNodeMain {

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("ask_for_position");
	}

	@Override
	public void onStart(final ConnectedNode connectedNode) {

//		ServiceClient<getPositionServiceRequest, getPositionServiceResponse> serviceClient;
//		try {
//			serviceClient = connectedNode.newServiceClient("get_obj_position", getPositionService._TYPE);
//		} catch (ServiceNotFoundException e) {
//			throw new RosRuntimeException(e);
//		}
//		final getPositionServiceRequest request = serviceClient.newMessage();
//		request.setObjName("dish1");
//		serviceClient.call(request, new ServiceResponseListener<getPositionServiceResponse>() {
//			@Override
//			public void onSuccess(getPositionServiceResponse response) {
//				connectedNode.getLog().info(
//						String.format(request.getObjName()));
//				System.out.println("X: " + response.getX() + "Y: " + response.getY() + "Height: " + response.getHeight() + "Width" +response.getWidth());
//			}
//
//			@Override
//			public void onFailure(RemoteException e) {
//				throw new RosRuntimeException(e);
//			}
//		});
	
		ServiceClient<IsConsistentRequest, IsConsistentResponse> serviceClient;
		try {
			serviceClient = connectedNode.newServiceClient("is_consistent", IsConsistent._TYPE);
		} catch (ServiceNotFoundException e) {
			throw new RosRuntimeException(e);
		}
		final IsConsistentRequest request = serviceClient.newMessage();
		request.setAggregateName("aggregate");
		serviceClient.call(request, new ServiceResponseListener<IsConsistentResponse>() {
			@Override
			public void onSuccess(IsConsistentResponse response) {
//				connectedNode.getLog().info(
//						String.format(request.getAggregateName());
				System.out.println(response.getIsConsistent());
			}

			@Override
			public void onFailure(RemoteException e) {
				throw new RosRuntimeException(e);
			}
		});

	
	}
}
