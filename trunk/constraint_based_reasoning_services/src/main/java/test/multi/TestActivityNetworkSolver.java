package test.multi;

import java.util.logging.Level;

import multi.activity.ActivityNetworkSolver;
import utility.logging.MetaCSPLogging;
import framework.Variable;

public class TestActivityNetworkSolver {
	
	
	public static void main(String[] args) {
				
		ActivityNetworkSolver solver = new ActivityNetworkSolver(0,100);

		MetaCSPLogging.setLevel(Level.FINEST);
		
		Variable[] acts = solver.createVariables(100);

		
		
	}

}
