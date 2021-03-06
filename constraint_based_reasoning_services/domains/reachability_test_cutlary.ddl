(SimpleDomain TestSimpleHybridPlanningDomain)
(Controllable RobotProprioception) #proprioception
(Controllable atLocation) #tabletop perception
(Resource arm  2)
(Resource fieldOfView 200)
(Resource robot1 1)
(Resource arm1 1)
(Resource arm2 1)

(SimpleOperator  
 (Head RobotAction::ask_human_to_reachable_cup1_table1()) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource robot1(1))  
 (RequiredResource fieldOfView(200)) 
)
(SimpleOperator  
 (Head RobotAction::ask_human_to_reachable_knife1_table1()) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource robot1(1))  
 (RequiredResource fieldOfView(200)) 
)
(SimpleOperator  
 (Head RobotAction::ask_human_to_reachable_fork1_table1()) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource robot1(1))  
 (RequiredResource fieldOfView(200)) 
)
(SimpleOperator  
 (Head RobotAction::moveTo_manipulationArea_cup1_table1()) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource robot1(1))  
)
(SimpleOperator  
 (Head RobotAction::moveTo_manipulationArea_knife1_table1()) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource robot1(1))  
)
(SimpleOperator  
 (Head RobotAction::moveTo_manipulationArea_fork1_table1()) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource robot1(1))  
)
(SimpleOperator  
 (Head RobotSense::sensing_before_placing_cup1_table1()) 
 (RequiredState req1 atLocation::at_robot1_table1()) 
 (Constraint During(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource fieldOfView(200))  
 (RequiredResource robot1(1))  
)
(SimpleOperator  
 (Head RobotSense::sensing_before_picking_cup1_table1()) 
 (RequiredState req1 atLocation::at_robot1_table1()) 
 (Constraint During(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource fieldOfView(200))  
 (RequiredResource robot1(1))  
)
(SimpleOperator  
 (Head RobotSense::sensing_before_picking_cup1_tray1()) 
 (RequiredState req1 atLocation::at_robot1_table1()) 
 (Constraint During(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource fieldOfView(200))  
 (RequiredResource robot1(1))  
)
(SimpleOperator  
 (Head RobotSense::sensing_before_placing_cup1_tray1()) 
 (RequiredState req1 atLocation::at_robot1_table1()) 
 (Constraint During(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource fieldOfView(200))  
 (RequiredResource robot1(1))  
)
(SimpleOperator  
 (Head RobotSense::sensing_before_placing_knife1_table1()) 
 (RequiredState req1 atLocation::at_robot1_table1()) 
 (Constraint During(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource fieldOfView(200))  
 (RequiredResource robot1(1))  
)
(SimpleOperator  
 (Head RobotSense::sensing_before_picking_knife1_table1()) 
 (RequiredState req1 atLocation::at_robot1_table1()) 
 (Constraint During(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource fieldOfView(200))  
 (RequiredResource robot1(1))  
)
(SimpleOperator  
 (Head RobotSense::sensing_before_picking_knife1_tray1()) 
 (RequiredState req1 atLocation::at_robot1_table1()) 
 (Constraint During(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource fieldOfView(200))  
 (RequiredResource robot1(1))  
)
(SimpleOperator  
 (Head RobotSense::sensing_before_placing_knife1_tray1()) 
 (RequiredState req1 atLocation::at_robot1_table1()) 
 (Constraint During(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource fieldOfView(200))  
 (RequiredResource robot1(1))  
)
(SimpleOperator  
 (Head RobotSense::sensing_before_placing_fork1_table1()) 
 (RequiredState req1 atLocation::at_robot1_table1()) 
 (Constraint During(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource fieldOfView(200))  
 (RequiredResource robot1(1))  
)
(SimpleOperator  
 (Head RobotSense::sensing_before_picking_fork1_table1()) 
 (RequiredState req1 atLocation::at_robot1_table1()) 
 (Constraint During(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource fieldOfView(200))  
 (RequiredResource robot1(1))  
)
(SimpleOperator  
 (Head RobotSense::sensing_before_picking_fork1_tray1()) 
 (RequiredState req1 atLocation::at_robot1_table1()) 
 (Constraint During(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource fieldOfView(200))  
 (RequiredResource robot1(1))  
)
(SimpleOperator  
 (Head RobotSense::sensing_before_placing_fork1_tray1()) 
 (RequiredState req1 atLocation::at_robot1_table1()) 
 (Constraint During(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource fieldOfView(200))  
 (RequiredResource robot1(1))  
)
#######################################################
(SimpleOperator  
 (Head atLocation::at_cup1_table1()) 
 (RequiredState req1 RobotAction::place_cup1_arm1__table1()) 
 (Constraint StartedBy(Head,req1)) 
 (Constraint OverlappedBy(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
)
(SimpleOperator  
 (Head atLocation::at_cup1_table1()) 
 (RequiredState req1 RobotAction::place_cup1_arm2__table1()) 
 (Constraint StartedBy(Head,req1)) 
 (Constraint OverlappedBy(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
)
(SimpleOperator  
 (Head RobotAction::place_cup1_arm1__table1()) 
 (RequiredState req1 RobotProprioception::holding_arm1_cup1()) 
 (RequiredState req2 RobotSense::sensing_before_placing_cup1_table1()) 
 (RequiredState req3 atLocation::at_robot1_table1()) 
 (RequiredState req4 atLocation::at_robot1_manipulationArea_cup1_table1()) 
 (Constraint During(Head,req4)) 
 (Constraint During(req2,req3)) 
 (Constraint During(Head,req3)) 
 (Constraint MetBy(Head,req2)) 
 (Constraint MetBy(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource arm(1)) 
 (RequiredResource fieldOfView(1)) 
 (RequiredResource robot1(1)) 
 (RequiredResource arm1(1)) 
)
(SimpleOperator  
 (Head RobotAction::place_cup1_arm2__table1()) 
 (RequiredState req1 RobotProprioception::holding_arm2_cup1()) 
 (RequiredState req2 RobotSense::sensing_before_placing_cup1_table1()) 
 (RequiredState req3 atLocation::at_robot1_table1()) 
 (RequiredState req4 atLocation::at_robot1_manipulationArea_cup1_table1()) 
 (Constraint During(Head,req4)) 
 (Constraint During(req2,req3)) 
 (Constraint During(Head,req3)) 
 (Constraint MetBy(Head,req2)) 
 (Constraint MetBy(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource arm(1)) 
 (RequiredResource fieldOfView(1)) 
 (RequiredResource robot1(1)) 
 (RequiredResource arm2(1)) 
)
(SimpleOperator  
 (Head RobotProprioception::holding_arm1_cup1()) 
 (RequiredState req1 RobotAction::pick_cup1_arm1__table1()) 
 (Constraint MetBy(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource arm(1)) 
 (RequiredResource arm1(1)) 
)
(SimpleOperator  
 (Head RobotProprioception::holding_arm2_cup1()) 
 (RequiredState req1 RobotAction::pick_cup1_arm2__table1()) 
 (Constraint MetBy(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource arm(1)) 
 (RequiredResource arm2(1)) 
)
(SimpleOperator  
 (Head RobotAction::pick_cup1_arm1__table1()) 
 (RequiredState req1 atLocation::at_cup1_table1()) 
 (RequiredState req2 RobotSense::sensing_before_picking_cup1_table1()) 
 (RequiredState req3 atLocation::at_robot1_table1()) 
 (RequiredState req4 atLocation::at_robot1_manipulationArea_cup1_table1()) 
 (Constraint During(Head,req4)) 
 (Constraint During(req2,req3)) 
 (Constraint During(Head,req3)) 
 (Constraint MetBy(Head,req2)) 
 (Constraint OverlappedBy(Head,req1)) 
 (Constraint Finishes(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource arm(1)) 
 (RequiredResource fieldOfView(1)) 
 (RequiredResource robot1(1)) 
 (RequiredResource arm1(1)) 
)
(SimpleOperator  
 (Head RobotAction::pick_cup1_arm2__table1()) 
 (RequiredState req1 atLocation::at_cup1_table1()) 
 (RequiredState req2 RobotSense::sensing_before_picking_cup1_table1()) 
 (RequiredState req3 atLocation::at_robot1_table1()) 
 (RequiredState req4 atLocation::at_robot1_manipulationArea_cup1_table1()) 
 (Constraint During(Head,req4)) 
 (Constraint During(req2,req3)) 
 (Constraint During(Head,req3)) 
 (Constraint MetBy(Head,req2)) 
 (Constraint OverlappedBy(Head,req1)) 
 (Constraint Finishes(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource arm(1)) 
 (RequiredResource fieldOfView(1)) 
 (RequiredResource robot1(1)) 
 (RequiredResource arm2(1)) 
)
#######################################################
#######################################################
(SimpleOperator  
 (Head atLocation::at_knife1_table1()) 
 (RequiredState req1 RobotAction::place_knife1_arm1__table1()) 
 (Constraint StartedBy(Head,req1)) 
 (Constraint OverlappedBy(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
)
(SimpleOperator  
 (Head atLocation::at_knife1_table1()) 
 (RequiredState req1 RobotAction::place_knife1_arm2__table1()) 
 (Constraint StartedBy(Head,req1)) 
 (Constraint OverlappedBy(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
)
(SimpleOperator  
 (Head RobotAction::place_knife1_arm1__table1()) 
 (RequiredState req1 RobotProprioception::holding_arm1_knife1()) 
 (RequiredState req2 RobotSense::sensing_before_placing_knife1_table1()) 
 (RequiredState req3 atLocation::at_robot1_table1()) 
 (RequiredState req4 atLocation::at_robot1_manipulationArea_knife1_table1()) 
 (Constraint During(Head,req4)) 
 (Constraint During(req2,req3)) 
 (Constraint During(Head,req3)) 
 (Constraint MetBy(Head,req2)) 
 (Constraint MetBy(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource arm(1)) 
 (RequiredResource fieldOfView(1)) 
 (RequiredResource robot1(1)) 
 (RequiredResource arm1(1)) 
)
(SimpleOperator  
 (Head RobotAction::place_knife1_arm2__table1()) 
 (RequiredState req1 RobotProprioception::holding_arm2_knife1()) 
 (RequiredState req2 RobotSense::sensing_before_placing_knife1_table1()) 
 (RequiredState req3 atLocation::at_robot1_table1()) 
 (RequiredState req4 atLocation::at_robot1_manipulationArea_knife1_table1()) 
 (Constraint During(Head,req4)) 
 (Constraint During(req2,req3)) 
 (Constraint During(Head,req3)) 
 (Constraint MetBy(Head,req2)) 
 (Constraint MetBy(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource arm(1)) 
 (RequiredResource fieldOfView(1)) 
 (RequiredResource robot1(1)) 
 (RequiredResource arm2(1)) 
)
(SimpleOperator  
 (Head RobotProprioception::holding_arm1_knife1()) 
 (RequiredState req1 RobotAction::pick_knife1_arm1__table1()) 
 (Constraint MetBy(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource arm(1)) 
 (RequiredResource arm1(1)) 
)
(SimpleOperator  
 (Head RobotProprioception::holding_arm2_knife1()) 
 (RequiredState req1 RobotAction::pick_knife1_arm2__table1()) 
 (Constraint MetBy(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource arm(1)) 
 (RequiredResource arm2(1)) 
)
(SimpleOperator  
 (Head RobotAction::pick_knife1_arm1__table1()) 
 (RequiredState req1 atLocation::at_knife1_table1()) 
 (RequiredState req2 RobotSense::sensing_before_picking_knife1_table1()) 
 (RequiredState req3 atLocation::at_robot1_table1()) 
 (RequiredState req4 atLocation::at_robot1_manipulationArea_knife1_table1()) 
 (Constraint During(Head,req4)) 
 (Constraint During(req2,req3)) 
 (Constraint During(Head,req3)) 
 (Constraint MetBy(Head,req2)) 
 (Constraint OverlappedBy(Head,req1)) 
 (Constraint Finishes(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource arm(1)) 
 (RequiredResource fieldOfView(1)) 
 (RequiredResource robot1(1)) 
 (RequiredResource arm1(1)) 
)
(SimpleOperator  
 (Head RobotAction::pick_knife1_arm2__table1()) 
 (RequiredState req1 atLocation::at_knife1_table1()) 
 (RequiredState req2 RobotSense::sensing_before_picking_knife1_table1()) 
 (RequiredState req3 atLocation::at_robot1_table1()) 
 (RequiredState req4 atLocation::at_robot1_manipulationArea_knife1_table1()) 
 (Constraint During(Head,req4)) 
 (Constraint During(req2,req3)) 
 (Constraint During(Head,req3)) 
 (Constraint MetBy(Head,req2)) 
 (Constraint OverlappedBy(Head,req1)) 
 (Constraint Finishes(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource arm(1)) 
 (RequiredResource fieldOfView(1)) 
 (RequiredResource robot1(1)) 
 (RequiredResource arm2(1)) 
)
#######################################################
#######################################################
(SimpleOperator  
 (Head atLocation::at_fork1_table1()) 
 (RequiredState req1 RobotAction::place_fork1_arm1__table1()) 
 (Constraint StartedBy(Head,req1)) 
 (Constraint OverlappedBy(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
)
(SimpleOperator  
 (Head atLocation::at_fork1_table1()) 
 (RequiredState req1 RobotAction::place_fork1_arm2__table1()) 
 (Constraint StartedBy(Head,req1)) 
 (Constraint OverlappedBy(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
)
(SimpleOperator  
 (Head RobotAction::place_fork1_arm1__table1()) 
 (RequiredState req1 RobotProprioception::holding_arm1_fork1()) 
 (RequiredState req2 RobotSense::sensing_before_placing_fork1_table1()) 
 (RequiredState req3 atLocation::at_robot1_table1()) 
 (RequiredState req4 atLocation::at_robot1_manipulationArea_fork1_table1()) 
 (Constraint During(Head,req4)) 
 (Constraint During(req2,req3)) 
 (Constraint During(Head,req3)) 
 (Constraint MetBy(Head,req2)) 
 (Constraint MetBy(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource arm(1)) 
 (RequiredResource fieldOfView(1)) 
 (RequiredResource robot1(1)) 
 (RequiredResource arm1(1)) 
)
(SimpleOperator  
 (Head RobotAction::place_fork1_arm2__table1()) 
 (RequiredState req1 RobotProprioception::holding_arm2_fork1()) 
 (RequiredState req2 RobotSense::sensing_before_placing_fork1_table1()) 
 (RequiredState req3 atLocation::at_robot1_table1()) 
 (RequiredState req4 atLocation::at_robot1_manipulationArea_fork1_table1()) 
 (Constraint During(Head,req4)) 
 (Constraint During(req2,req3)) 
 (Constraint During(Head,req3)) 
 (Constraint MetBy(Head,req2)) 
 (Constraint MetBy(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource arm(1)) 
 (RequiredResource fieldOfView(1)) 
 (RequiredResource robot1(1)) 
 (RequiredResource arm2(1)) 
)
(SimpleOperator  
 (Head RobotProprioception::holding_arm1_fork1()) 
 (RequiredState req1 RobotAction::pick_fork1_arm1__table1()) 
 (Constraint MetBy(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource arm(1)) 
 (RequiredResource arm1(1)) 
)
(SimpleOperator  
 (Head RobotProprioception::holding_arm2_fork1()) 
 (RequiredState req1 RobotAction::pick_fork1_arm2__table1()) 
 (Constraint MetBy(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource arm(1)) 
 (RequiredResource arm2(1)) 
)
(SimpleOperator  
 (Head RobotAction::pick_fork1_arm1__table1()) 
 (RequiredState req1 atLocation::at_fork1_table1()) 
 (RequiredState req2 RobotSense::sensing_before_picking_fork1_table1()) 
 (RequiredState req3 atLocation::at_robot1_table1()) 
 (RequiredState req4 atLocation::at_robot1_manipulationArea_fork1_table1()) 
 (Constraint During(Head,req4)) 
 (Constraint During(req2,req3)) 
 (Constraint During(Head,req3)) 
 (Constraint MetBy(Head,req2)) 
 (Constraint OverlappedBy(Head,req1)) 
 (Constraint Finishes(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource arm(1)) 
 (RequiredResource fieldOfView(1)) 
 (RequiredResource robot1(1)) 
 (RequiredResource arm1(1)) 
)
(SimpleOperator  
 (Head RobotAction::pick_fork1_arm2__table1()) 
 (RequiredState req1 atLocation::at_fork1_table1()) 
 (RequiredState req2 RobotSense::sensing_before_picking_fork1_table1()) 
 (RequiredState req3 atLocation::at_robot1_table1()) 
 (RequiredState req4 atLocation::at_robot1_manipulationArea_fork1_table1()) 
 (Constraint During(Head,req4)) 
 (Constraint During(req2,req3)) 
 (Constraint During(Head,req3)) 
 (Constraint MetBy(Head,req2)) 
 (Constraint OverlappedBy(Head,req1)) 
 (Constraint Finishes(Head,req1)) 
 (Constraint Duration[2000,INF](Head)) 
 (RequiredResource arm(1)) 
 (RequiredResource fieldOfView(1)) 
 (RequiredResource robot1(1)) 
 (RequiredResource arm2(1)) 
)
#######################################################

