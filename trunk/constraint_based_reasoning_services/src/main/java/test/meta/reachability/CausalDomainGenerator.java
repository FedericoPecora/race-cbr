package test.meta.reachability;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import meta.MetaCausalConstraint.markings;

import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraintSolver;
import org.metacsp.multi.spatioTemporal.SpatialFluentSolver;

public class CausalDomainGenerator {

	static String PATH = "/home/iran/ros/race/race_cbr/constraint_based_reasoning_services/domains/reachability";   
	static String duration = "2000";


	public static void main(String[] args) {

		String[] objVar = new String[]{"cup1", "knife1", "fork1"};


		//		String[] objVar = new String[]{"cup1", "knife1", "fork1"};

		String  simpleDomain = "(SimpleDomain TestSimpleHybridPlanningDomain)"; 
		String controllable1 = "(Controllable RobotProprioception) #proprioception";
		String controllable2 = "(Controllable atLocation) #tabletop perception";

		String resource1 = "(Resource arm " + 2 + ")"; 
		String resource2 = "(Resource fieldOfView 200)"; 
		String resource3 = "(Resource robot1 1)"; 
		String resource4 = "(Resource manArea 1)"; 

		String resource5 = "(Resource arm1 1)"; 
		String resource6 = "(Resource arm2 1)"; 

		//writ in PATH + armReources +ddl

		BufferedWriter causalDomain = null;
		String operator = "";
		operator += simpleDomain + "\n" +controllable1 + "\n" +controllable2 + "\n" + resource1 + "\n" + 
				resource2 + "\n" + resource3 + "\n" + resource4 + "\n" + resource5 + "\n" + resource6 +"\n" + "\n"; 

//		operator += getSwapArms(objVar);
		
		operator += getAtManipulationAreaOperator(objVar);

//		operator += getMoveToOperator(objVar);

		operator += getSensingOperator(objVar);

		operator += getPickAndPlaceOperator(objVar);
		//operator += getPickAndPlaceOperatorWithUnifiedArms(objVar);
		


		System.out.println(operator);

		try{

			causalDomain = new BufferedWriter(new FileWriter(PATH +"_test"+".ddl", false));
			causalDomain.write(operator);
			causalDomain.newLine();
			causalDomain.flush();
		}				
		catch (IOException ioe) {
			ioe.printStackTrace();
		}


	}



	private static String getSwapArms(String[] objVar) {
		String ret = "";

		for (int i = 0; i < objVar.length; i++) {

			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotProprioception::holding_"+ "_arm1_"+ objVar[i] +"())" +  " \n"+
					" (RequiredState req1 RobotAction::swap_"+ objVar[i]+ "_arm2_"+ "between" +"_arm1"+"())" +  " \n"+
					" (Constraint MetBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(1))" +  " \n"+
					" (RequiredResource arm1(1))" +  " \n"+
					")" + "\n" ;

			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotProprioception::holding_"+ "_arm2_"+ objVar[i] +"())" +  " \n"+
					" (RequiredState req1 RobotAction::swap_"+ objVar[i]+ "_arm1_"+ "between" +"_arm2" +"())"+  " \n"+
					" (Constraint MetBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(1))" +  " \n"+
					" (RequiredResource arm2(1))" +  " \n"+
					")" + "\n" ;

			
			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotAction::swap_"+ objVar[i]+ "_arm2_"+ "between" +"_arm1" +"())"+  " \n"+
					" (RequiredState req1 RobotProprioception::holding_"+ "_arm2_"+ objVar[i] +"())" +   " \n"+
					" (Constraint MetBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(2))" +  " \n"+
					")" + "\n" ;

			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotAction::swap_"+ objVar[i]+ "_arm1_"+ "between" +"_arm2" +"())"+  " \n"+
					" (RequiredState req1 RobotProprioception::holding_"+ "_arm1_"+ objVar[i] +"())" +   " \n"+
					" (Constraint MetBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(2))" +  " \n"+
					")" + "\n" ;

			
		}

		return ret;
	}



	private static String getMoveToOperator(String[] objVar) {
		String ret = "";

		for (int i = 0; i < objVar.length; i++) {

			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotAction::moveTo_manipulationArea_"+ objVar[i] +"())" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource robot1(1)) " +  " \n"+
					")" + "\n" ;

		}

		return ret;
	}



	private static String getAtManipulationAreaOperator(String[] objVar) {

		String ret = "";

		for (int i = 0; i < objVar.length; i++) {

			ret +=  "(SimpleOperator " + " \n"+
					" (Head atLocation::at_robot1_manipulationArea_"+ objVar[i] +"_table1())" +  " \n"+
//					" (RequiredState req1 RobotAction::moveTo_manipulationArea_"+ objVar[i] +"())" +  " \n"+
//					" (Constraint MetBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource manArea(1)) " +  " \n"+
					")" + "\n" ;

		}

		return ret;
	}



	private static String getSensingOperator(String[] objVar) {

		String ret = "";

		for (int i = 0; i < objVar.length; i++) {

			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotSense::sensing_before_placing_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req1 atLocation::at_robot1_table1())" +  " \n"+
					//" (RequiredState req2 atLocation::at_robot1_manipulationArea_"+ objVar[i] +"_table1())" +  " \n"+
					//" (Constraint During(Head,req2))" +  " \n"+
					" (Constraint During(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource fieldOfView(200)) " +  " \n"+
					" (RequiredResource robot1(1)) " +  " \n"+					
					")" + "\n" ;

			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotSense::sensing_before_picking_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req1 atLocation::at_robot1_table1())" +  " \n"+
					//" (RequiredState req2 atLocation::at_robot1_manipulationArea_"+ objVar[i] +"_table1())" +  " \n"+
					//" (Constraint During(Head,req2))" +  " \n"+
					" (Constraint During(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource fieldOfView(200)) " +  " \n"+
					" (RequiredResource robot1(1)) " +  " \n"+
					")" + "\n" ;


			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotSense::sensing_before_picking_"+ objVar[i] +"_tray1())" +  " \n"+
					" (RequiredState req1 atLocation::at_robot1_table1())" +  " \n"+
					" (Constraint During(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource fieldOfView(200)) " +  " \n"+
					" (RequiredResource robot1(1)) " +  " \n"+
					")" + "\n" ;


			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotSense::sensing_before_placing_"+ objVar[i] +"_tray1())" +  " \n"+
					" (RequiredState req1 atLocation::at_robot1_table1())" +  " \n"+
					" (Constraint During(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource fieldOfView(200)) " +  " \n"+
					" (RequiredResource robot1(1)) " +  " \n"+
					")" + "\n" ;


		}

		return ret;
	}


	private static String getPickAndPlaceOperator(String[] objVar) {

		String ret = "";

		for (int i = 0; i < objVar.length; i++) {

			///////////////////////////////////////////////////////////////////////////////////////////

			ret += "#######################################################" + "\n" ;


			ret +=  "(SimpleOperator " + " \n"+
					" (Head atLocation::at_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req1 RobotAction::place_"+ objVar[i] + "_arm1_" +"_table1())" +  " \n"+
					" (Constraint StartedBy(Head,req1))" +  " \n"+
					" (Constraint OverlappedBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					")" + "\n" ;

			ret +=  "(SimpleOperator " + " \n"+
					" (Head atLocation::at_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req1 RobotAction::place_"+ objVar[i] + "_arm2_" +"_table1())" +  " \n"+
					" (Constraint StartedBy(Head,req1))" +  " \n"+
					" (Constraint OverlappedBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					")" + "\n" ;



			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotAction::place_"+ objVar[i]+ "_arm1_" +"_table1())" +  " \n"+
					" (RequiredState req1 RobotProprioception::holding_"+ "_arm1_"+ objVar[i] +"())" +  " \n"+
					" (RequiredState req2 RobotSense::sensing_before_placing_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req3 atLocation::at_robot1_table1())" +  " \n"+
					" (RequiredState req4 atLocation::at_robot1_manipulationArea_"+ objVar[i] +"_table1())" +  " \n"+
					" (Constraint During(Head,req4))" +  " \n"+
					" (Constraint During(req2,req3))" +  " \n"+
					" (Constraint During(Head,req3))" +  " \n"+
					" (Constraint MetBy(Head,req2))" +  " \n"+
					" (Constraint MetBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(1))" +  " \n"+
					" (RequiredResource fieldOfView(1))" +  " \n"+
					" (RequiredResource robot1(1))" +  " \n"+
					" (RequiredResource arm1(1))" +  " \n"+
					")" + "\n" ;


			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotAction::place_"+ objVar[i]+ "_arm2_" +"_table1())" +  " \n"+
					" (RequiredState req1 RobotProprioception::holding_"+ "_arm2_"+ objVar[i] +"())" +  " \n"+
					" (RequiredState req2 RobotSense::sensing_before_placing_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req3 atLocation::at_robot1_table1())" +  " \n"+
					" (RequiredState req4 atLocation::at_robot1_manipulationArea_"+ objVar[i] +"_table1())" +  " \n"+
					" (Constraint During(Head,req4))" +  " \n"+
					" (Constraint During(req2,req3))" +  " \n"+
					" (Constraint During(Head,req3))" +  " \n"+
					" (Constraint MetBy(Head,req2))" +  " \n"+
					" (Constraint MetBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(1))" +  " \n"+
					" (RequiredResource fieldOfView(1))" +  " \n"+
					" (RequiredResource robot1(1))" +  " \n"+
					" (RequiredResource arm2(1))" +  " \n"+
					")" + "\n" ;

			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotProprioception::holding_"+ "_arm1_"+ objVar[i] +"())" +  " \n"+
					" (RequiredState req1 RobotAction::pick_"+ objVar[i]+ "_arm1_" +"_table1())" +  " \n"+
					" (Constraint MetBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(1))" +  " \n"+
					" (RequiredResource arm1(1))" +  " \n"+
					")" + "\n" ;

			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotProprioception::holding_"+ "_arm2_" + objVar[i] +"())" +  " \n"+
					" (RequiredState req1 RobotAction::pick_"+ objVar[i]+ "_arm2_" +"_table1())" +  " \n"+
					" (Constraint MetBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(1))" +  " \n"+
					" (RequiredResource arm2(1))" +  " \n"+
					")" + "\n" ;

			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotAction::pick_"+ objVar[i] + "_arm1_" +"_table1())" +  " \n"+
					" (RequiredState req1 atLocation::at_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req2 RobotSense::sensing_before_picking_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req3 atLocation::at_robot1_table1())" +  " \n"+
					" (RequiredState req4 atLocation::at_robot1_manipulationArea_"+ objVar[i] +"_table1())" +  " \n"+
					" (Constraint During(Head,req4))" +  " \n"+
					" (Constraint During(req2,req3))" +  " \n"+
					" (Constraint During(Head,req3))" +  " \n"+
					" (Constraint MetBy(Head,req2))" +  " \n"+
					" (Constraint OverlappedBy(Head,req1))" +  " \n"+
					" (Constraint Finishes(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(1))" +  " \n"+
					" (RequiredResource fieldOfView(1))" +  " \n"+
					" (RequiredResource robot1(1))" +  " \n"+
					" (RequiredResource arm1(1))" +  " \n"+
					")" + "\n" ;

			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotAction::pick_"+ objVar[i] + "_arm2_" + "_table1())" +  " \n"+
					" (RequiredState req1 atLocation::at_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req2 RobotSense::sensing_before_picking_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req3 atLocation::at_robot1_table1())" +  " \n"+
					" (RequiredState req4 atLocation::at_robot1_manipulationArea_"+ objVar[i] +"_table1())" +  " \n"+
					" (Constraint During(Head,req4))" +  " \n"+
					" (Constraint During(req2,req3))" +  " \n"+
					" (Constraint During(Head,req3))" +  " \n"+
					" (Constraint MetBy(Head,req2))" +  " \n"+
					" (Constraint OverlappedBy(Head,req1))" +  " \n"+
					" (Constraint Finishes(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(1))" +  " \n"+
					" (RequiredResource fieldOfView(1))" +  " \n"+
					" (RequiredResource robot1(1))" +  " \n"+
					" (RequiredResource arm2(1))" +  " \n"+
					")" + "\n" ;

			ret += "#######################################################" + "\n" ;
//			ret +=  "(SimpleOperator " + " \n"+
//					" (Head atLocation::at_"+ objVar[i] +"_tray1())" +  " \n"+
//					" (RequiredState req1 RobotAction::place_"+ objVar[i]+ "_arm1_" +"_tray1())" +  " \n"+
//					" (Constraint StartedBy(Head,req1))" +  " \n"+
//					" (Constraint OverlappedBy(Head,req1))" +  " \n"+
//					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
//					")" + "\n" ;
//
//			ret +=  "(SimpleOperator " + " \n"+
//					" (Head atLocation::at_"+ objVar[i] +"_tray1())" +  " \n"+
//					" (RequiredState req1 RobotAction::place_"+ objVar[i]+ "_arm2_" +"_tray1())" +  " \n"+
//					" (Constraint StartedBy(Head,req1))" +  " \n"+
//					" (Constraint OverlappedBy(Head,req1))" +  " \n"+
//					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
//					")" + "\n" ;
//
//			ret +=  "(SimpleOperator " + " \n"+
//					" (Head RobotAction::pick_"+ objVar[i] + "_arm1_" +"_tray1())" +  " \n"+
//					" (RequiredState req1 atLocation::at_"+ objVar[i] +"_tray1())" +  " \n"+
//					" (RequiredState req2 RobotSense::sensing_before_picking_"+ objVar[i] +"_tray1())" +  " \n"+
//					" (Constraint MetBy(Head,req2))" +  " \n"+
//					" (Constraint MetBy(Head,req1))" +  " \n"+
//					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
//					" (RequiredResource arm(1))" +  " \n"+
//					" (RequiredResource fieldOfView(1))" +  " \n"+
//					" (RequiredResource arm1(1))" +  " \n"+
//					")" + "\n" ;
//
//			ret +=  "(SimpleOperator " + " \n"+
//					" (Head RobotAction::pick_"+ objVar[i] + "_arm2_" +"_tray1())" +  " \n"+
//					" (RequiredState req1 atLocation::at_"+ objVar[i] +"_tray1())" +  " \n"+
//					" (RequiredState req2 RobotSense::sensing_before_picking_"+ objVar[i] +"_tray1())" +  " \n"+
//					" (Constraint MetBy(Head,req2))" +  " \n"+
//					" (Constraint MetBy(Head,req1))" +  " \n"+
//					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
//					" (RequiredResource arm(1))" +  " \n"+
//					" (RequiredResource fieldOfView(1))" +  " \n"+
//					" (RequiredResource arm2(1))" +  " \n"+
//					")" + "\n" ;
//
//			ret +=  "(SimpleOperator " + " \n"+
//					" (Head RobotProprioception::holding_"+ "_arm1_"+ objVar[i] +"())" +  " \n"+
//					" (RequiredState req1 RobotAction::pick_"+ objVar[i]+ "_arm1_" +"_tray1())" +  " \n"+
//					" (Constraint MetBy(Head,req1))" +  " \n"+
//					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
//					" (RequiredResource arm(1))" +  " \n"+
//					" (RequiredResource arm1(1))" +  " \n"+
//					")" + "\n" ;
//
//			ret +=  "(SimpleOperator " + " \n"+
//					" (Head RobotProprioception::holding_"+ "_arm2_" + objVar[i] +"())" +  " \n"+
//					" (RequiredState req1 RobotAction::pick_"+ objVar[i]+ "_arm2_" +"_tray1())" +  " \n"+
//					" (Constraint MetBy(Head,req1))" +  " \n"+
//					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
//					" (RequiredResource arm(1))" +  " \n"+
//					" (RequiredResource arm2(1))" +  " \n"+
//					")" + "\n" ;
//
//			ret +=  "(SimpleOperator " + " \n"+
//					" (Head RobotAction::place_"+ objVar[i]+ "_arm1_" +"_tray1())" +  " \n"+
//					" (RequiredState req1 RobotProprioception::holding_"+ "_arm1_"+ objVar[i] +"())" +  " \n"+
//					" (RequiredState req2 RobotSense::sensing_before_placing_"+ objVar[i] +"_tray1())" +  " \n"+
//					" (RequiredState req3 atLocation::at_robot1_table1())" +  " \n"+
//					" (Constraint During(Head,req3))" +  " \n"+
//					" (Constraint MetBy(Head,req2))" +  " \n"+
//					" (Constraint MetBy(Head,req1))" +  " \n"+
//					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
//					" (RequiredResource arm(1))" +  " \n"+
//					" (RequiredResource fieldOfView(1))" +  " \n"+
//					" (RequiredResource arm1(1))" +  " \n"+
//					")" + "\n" ;
//
//			ret +=  "(SimpleOperator " + " \n"+
//					" (Head RobotAction::place_"+ objVar[i]+ "_arm2_" +"_tray1())" +  " \n"+
//					" (RequiredState req1 RobotProprioception::holding_"+ "_arm2_" + objVar[i] +"())" +  " \n"+
//					" (RequiredState req2 RobotSense::sensing_before_placing_"+ objVar[i] +"_tray1())" +  " \n"+
//					" (RequiredState req3 atLocation::at_robot1_table1())" +  " \n"+
//					" (Constraint During(Head,req3))" +  " \n"+
//					" (Constraint MetBy(Head,req2))" +  " \n"+
//					" (Constraint MetBy(Head,req1))" +  " \n"+
//					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
//					" (RequiredResource arm(1))" +  " \n"+
//					" (RequiredResource fieldOfView(1))" +  " \n"+
//					" (RequiredResource arm2(1))" +  " \n"+
//					")" + "\n" ;

		}


		return ret;
	}

	private static String getPickAndPlaceOperatorWithUnifiedArms(String[] objVar) {

		String ret = "";

		for (int i = 0; i < objVar.length; i++) {

			///////////////////////////////////////////////////////////////////////////////////////////

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
					" (RequiredState req2 RobotSense::sensing_before_placing_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req3 atLocation::at_robot1_table1())" +  " \n"+
					" (RequiredState req4 atLocation::at_robot1_manipulationArea_"+ objVar[i] +"_table1())" +  " \n"+
					" (Constraint During(Head,req4))" +  " \n"+
					" (Constraint During(req2,req3))" +  " \n"+
					" (Constraint During(Head,req3))" +  " \n"+
					" (Constraint MetBy(Head,req2))" +  " \n"+
					" (Constraint MetBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(1))" +  " \n"+
					" (RequiredResource fieldOfView(1))" +  " \n"+
					" (RequiredResource robot1(1))" +  " \n"+
					")" + "\n" ;

			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotProprioception::holding_"+ objVar[i] +"())" +  " \n"+
					" (RequiredState req1 RobotAction::pick_"+ objVar[i] +"_table1())" +  " \n"+
					" (Constraint MetBy(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(1))" +  " \n"+
					")" + "\n" ;

			ret +=  "(SimpleOperator " + " \n"+
					" (Head RobotAction::pick_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req1 atLocation::at_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req2 RobotSense::sensing_before_picking_"+ objVar[i] +"_table1())" +  " \n"+
					" (RequiredState req3 atLocation::at_robot1_table1())" +  " \n"+
					" (RequiredState req4 atLocation::at_robot1_manipulationArea_"+ objVar[i] +"_table1())" +  " \n"+
					" (Constraint During(Head,req4))" +  " \n"+
					" (Constraint During(req2,req3))" +  " \n"+
					" (Constraint During(Head,req3))" +  " \n"+
					" (Constraint MetBy(Head,req2))" +  " \n"+
					" (Constraint OverlappedBy(Head,req1))" +  " \n"+
					" (Constraint Finishes(Head,req1))" +  " \n"+
					" (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
					" (RequiredResource arm(1))" +  " \n"+
					" (RequiredResource fieldOfView(1))" +  " \n"+
					" (RequiredResource robot1(1))" +  " \n"+
					")" + "\n" ;

			//              ret += "#######################################################" + "\n" ;
			//              ret +=  "(SimpleOperator " + " \n"+
			//                              " (Head atLocation::at_"+ objVar[i] +"_tray1())" +  " \n"+
			//                              " (RequiredState req1 RobotAction::place_"+ objVar[i] +"_tray1())" +  " \n"+
			//                              " (Constraint StartedBy(Head,req1))" +  " \n"+
			//                              " (Constraint OverlappedBy(Head,req1))" +  " \n"+
			//                              " (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
			//                              ")" + "\n" ;
			//
			//              ret +=  "(SimpleOperator " + " \n"+
			//                              " (Head RobotAction::pick_"+ objVar[i] +"_tray1())" +  " \n"+
			//                              " (RequiredState req1 atLocation::at_"+ objVar[i] +"_tray1())" +  " \n"+
			//                              " (RequiredState req2 RobotSense::sensing_before_picking_"+ objVar[i] +"_tray1())" +  " \n"+
			//                              " (Constraint MetBy(Head,req2))" +  " \n"+
			//                              " (Constraint MetBy(Head,req1))" +  " \n"+
			//                              " (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
			//                              " (RequiredResource arm(1))" +  " \n"+
			//                              " (RequiredResource fieldOfView(1))" +  " \n"+
			//                              ")" + "\n" ;
			//
			//              ret +=  "(SimpleOperator " + " \n"+
			//                              " (Head RobotProprioception::holding_"+ objVar[i] +"())" +  " \n"+
			//                              " (RequiredState req1 RobotAction::pick_"+ objVar[i] +"_tray1())" +  " \n"+
			//                              " (Constraint MetBy(Head,req1))" +  " \n"+
			//                              " (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
			//                              " (RequiredResource arm(1))" +  " \n"+
			//                              ")" + "\n" ;
			//
			//
			//              ret +=  "(SimpleOperator " + " \n"+
			//                              " (Head RobotAction::place_"+ objVar[i] +"_tray1())" +  " \n"+
			//                              " (RequiredState req1 RobotProprioception::holding_"+ objVar[i] +"())" +  " \n"+
			//                              " (RequiredState req2 RobotSense::sensing_before_placing_"+ objVar[i] +"_tray1())" +  " \n"+
			//                              " (RequiredState req3 atLocation::at_robot1_table1())" +  " \n"+
			//                              " (Constraint During(Head,req3))" +  " \n"+
			//                              " (Constraint MetBy(Head,req2))" +  " \n"+
			//                              " (Constraint MetBy(Head,req1))" +  " \n"+
			//                              " (Constraint Duration[" + duration +",INF](Head))" +  " \n"+
			//                              " (RequiredResource arm(1))" +  " \n"+
			//                              " (RequiredResource fieldOfView(1))" +  " \n"+
			//                              ")" + "\n" ;

		}


		return ret;
	}


}

