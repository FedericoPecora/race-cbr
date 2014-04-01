
package test.JAIR14;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import meta.MetaCausalConstraint.markings;

import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraintSolver;
import org.metacsp.multi.spatioTemporal.SpatialFluentSolver;

public class CausalDomainGeneratorWOSesning {

	static String PATH = "/home/iran/Desktop/benchmark/testCase1/domain/deskDomain";   
	static int armResources = 5;
	static String duration = "2000";
	
	
	public static void main(String[] args) {
		
		String[] objVar = new String[]{"cup1", "book1", "monitor1", "keyboard1", "pen1", "notebook1", "penHolder1"};
		
//		String[] objVar = new String[]{"cup1", "knife1", "fork1"};
		
		for (int i = 1; i <= armResources; i++) {

			String  simpleDomain = "(SimpleDomain TestSimpleHybridPlanningDomain)"; 
			String controllable1 = "(Controllable RobotProprioception) #proprioception";
			String controllable2 = "(Controllable atLocation) #tabletop perception";
			
			String resource1 = "(Resource arm " + i + ")"; 
			//String resource2 = "(Resource kinect 1)"; 
			
			//writ in PATH + armReources +ddl
			
			BufferedWriter causalDomain = null;
			String operator = "";
			operator += simpleDomain + "\n" +controllable1 + "\n" +controllable2 + "\n" + resource1 + "\n" +  "\n" + "\n"; 
			
			//operator += getSensingOperator(objVar);
			
			operator += getPickAndPlaceOperator(objVar);
			
//			operator += getMoveOperator(objVar);
			
			System.out.println(operator);
			
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



	private static String getSensingOperator(String[] objVar) {
		
		String ret = "";
		
		for (int i = 0; i < objVar.length; i++) {
			
			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotSense::sensing_before_placing_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req1 atLocation::at_robot1_table1())" +  " \n"+
					" (Constraint During(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource kinect(1)) " +  " \n"+
					")" + "\n" ;

			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotSense::sensing_before_picking_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req1 atLocation::at_robot1_table1())" +  " \n"+
					" (Constraint During(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource kinect(1)) " +  " \n"+
					")" + "\n" ;


			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotSense::sensing_before_picking_"+ objVar[i] +"_tray1())" +  " \n"+
					" (RequiredState req1 atLocation::at_robot1_table1())" +  " \n"+
					" (Constraint During(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource kinect(1)) " +  " \n"+
					")" + "\n" ;

			
			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotSense::sensing_before_placing_"+ objVar[i] +"_tray1())" +  " \n"+
					" (RequiredState req1 atLocation::at_robot1_table1())" +  " \n"+
					" (Constraint During(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource kinect(1)) " +  " \n"+
					")" + "\n" ;

			
		}
				
		return ret;
	}
	
	
	private static String getPickAndPlaceOperator(String[] objVar) {

		String ret = "";

		for (int i = 0; i < objVar.length; i++) {
			
			//tray
			ret += "#######################################################" + "\n" ;

			
			ret +=  "(SimpleOperator " + " \n"+
					" (Head atLocation::at_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req1 RobotAction::place_"+ objVar[i] +"_table1())" +  " \n"+
					" (Constraint StartedBy(Head,req1))" +  " \n"+
					" (Constraint OverlappedBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					")" + "\n" ;

			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotAction::place_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req1 RobotProprioception::holding_"+ objVar[i] +"())" +  " \n"+
					//" (RequiredState req2 RobotSense::sensing_before_placing_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req3 atLocation::at_robot1_table1())" +  " \n"+
					//" (Constraint During(req2,req3))" +  " \n"+
					" (Constraint During(Head,req3))" +  " \n"+
					//" (Constraint MetBy(Head,req2))" +  " \n"+
					" (Constraint MetBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(1))" +  " \n"+
					")" + "\n" ;;

			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotProprioception::holding_"+ objVar[i] +"())" +  " \n"+
					" (RequiredState req1 RobotAction::pick_"+ objVar[i] +"_table1())" +  " \n"+
					" (Constraint MetBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(1))" +  " \n"+
					")" + "\n" ;;

			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotAction::pick_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req1 atLocation::at_"+ objVar[i] +"_table1())" +  " \n"+
					//" (RequiredState req2 RobotSense::sensing_before_picking_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req3 atLocation::at_robot1_table1())" +  " \n"+
					//" (Constraint During(req2,req3))" +  " \n"+
					" (Constraint During(Head,req3))" +  " \n"+
					//" (Constraint MetBy(Head,req2))" +  " \n"+
					" (Constraint OverlappedBy(Head,req1))" +  " \n"+
					" (Constraint Finishes(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(1))" +  " \n"+
					")" + "\n" ;
				
			///////////////////////////////////////////////////////////////////////////////////////////
			ret +=  "(SimpleOperator " + " \n"+
					" (Head atLocation::at_"+ objVar[i] +"_tray1())" +  " \n"+
					" (RequiredState req1 RobotAction::place_"+ objVar[i] +"_tray1())" +  " \n"+
					" (Constraint StartedBy(Head,req1))" +  " \n"+
					" (Constraint OverlappedBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					")" + "\n" ;
			
			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotAction::pick_"+ objVar[i] +"_tray1())" +  " \n"+
					" (RequiredState req1 atLocation::at_"+ objVar[i] +"_tray1())" +  " \n"+
					//" (RequiredState req2 RobotSense::sensing_before_picking_"+ objVar[i] +"_tray1())" +  " \n"+
					//" (Constraint MetBy(Head,req2))" +  " \n"+
					" (Constraint MetBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(1))" +  " \n"+
					")" + "\n" ;

			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotProprioception::holding_"+ objVar[i] +"())" +  " \n"+
					" (RequiredState req1 RobotAction::pick_"+ objVar[i] +"_tray1())" +  " \n"+
					" (Constraint MetBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(1))" +  " \n"+
					")" + "\n" ;

			
			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotAction::place_"+ objVar[i] +"_tray1())" +  " \n"+
					" (RequiredState req1 RobotProprioception::holding_"+ objVar[i] +"())" +  " \n"+
					//" (RequiredState req2 RobotSense::sensing_before_placing_"+ objVar[i] +"_tray1())" +  " \n"+
					" (RequiredState req3 atLocation::at_robot1_table1())" +  " \n"+
					" (Constraint During(Head,req3))" +  " \n"+
					//" (Constraint MetBy(Head,req2))" +  " \n"+
					" (Constraint MetBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(1))" +  " \n"+
					")" + "\n" ;
			

		}
		

		return ret;
	}

	
}
