package test.JAIR14;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraintSolver;
import org.metacsp.multi.spatioTemporal.SpatialFluentSolver;

public class CausalDomainGenerator {

	static String PATH = "domains/testDeskBenchmarkDomain"; //it has to + resource number + ddl  
	static int armResources = 5;
	
	

	
	
	
	public static void main(String[] args) {
		
		String[] objVar = new String[]{"cup1", "knife1" ,"fork1"};
		
		for (int i = 1; i <= armResources; i++) {

			String  simpleDomain = "(SimpleDomain TestSimpleHybridPlanningDomain)"; 
			String controllable1 = "(Controllable RobotProprioception) #proprioception";
			String controllable2 = "(Controllable atLocation) #tabletop perception";
			
			String resource1 = "(Resource arm " + i + ")"; 

			//writ in PATH + armReources +ddl
			
			BufferedWriter causalDomain = null;
			String operator = "";
			operator = simpleDomain + "\n" +controllable1 + "\n" +controllable2 + "\n" + resource1; 
			
			//generateOperator(objVar)
			
			try{
				
				causalDomain = new BufferedWriter(new FileWriter(PATH +"_"+ i +".ddl", false));
				causalDomain.write(operator);
				causalDomain.newLine();
				causalDomain.flush();
			}				
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		
		
	}
}
