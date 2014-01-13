import java.util.Vector;
import org.metacsp.meta.simplePlanner.SimpleOperator;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.time.APSPSolver;
import org.metacsp.time.Bounds;


public class CausalKnowledge {

	public static void getCausalKnowledge(String context, Vector<SimpleOperator> operators){


		if(context.compareTo("TestRACE") == 0){

			//			operators.addAll(getObjectPickAndPlaceOperator("cup1"));
			//			operators.addAll(getObjectPickAndPlaceOperator("fork1"));
			//			operators.addAll(getObjectPickAndPlaceOperator("knife1"));

			operators.addAll(getObjectPickAndPlaceOperatorSIMPLE("cup1"));
			operators.addAll(getObjectPickAndPlaceOperatorSIMPLE("fork1"));
			operators.addAll(getObjectPickAndPlaceOperatorSIMPLE("knife1"));

		}

		if(context.compareTo("WellSetTable") == 0)
			addOperator(operators);

	}


	private static Vector<SimpleOperator> getObjectPickAndPlaceOperator(String obj){

		Vector<SimpleOperator> operators = new Vector<SimpleOperator>();
		long duration = 5;

		AllenIntervalConstraint toLocationFinishesMove = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy);
		AllenIntervalConstraint moveMetByFromLocation = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());		
		AllenIntervalConstraint duringManArea = new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds());
		AllenIntervalConstraint pickFinishesAt = new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy, AllenIntervalConstraint.Type.Finishes);
		AllenIntervalConstraint atStartedByPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy, AllenIntervalConstraint.Type.StartedBy);
		AllenIntervalConstraint placeMetByholding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint holdingMetByPick = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());		
		AllenIntervalConstraint holdingDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint pickDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint atDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint placeDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint moveDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));

		SimpleOperator operator4 = new SimpleOperator("atLocation::at_"+ obj +"_eatingArea1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_"+ obj +"_eatingArea1(arm)"},
				new int[] {0});
		operator4.addConstraint(atDuration, 0, 0);
		operators.add(operator4);

		SimpleOperator operator5 = new SimpleOperator("robot1::place_"+ obj +"_eatingArea1(arm)",
				new AllenIntervalConstraint[] {placeMetByholding, duringManArea},
				new String[] {"robot1::holding_"+ obj +"(arm)", "atLocation::at_robot1_manArea1()"},
				new int[] {1,0});
		operator5.addConstraint(placeDuration, 0, 0);
		operators.add(operator5);



		SimpleOperator operator6 = new SimpleOperator("robot1::holding_"+ obj +"(arm)",
				new AllenIntervalConstraint[] {holdingMetByPick},
				new String[] {"robot1::pick_"+ obj +"_eatingArea1(arm)"},
				new int[] {1});
		operator6.addConstraint(holdingDuration, 0, 0);
		operators.add(operator6);


		SimpleOperator operator2res = new SimpleOperator("robot1::pick_"+ obj +"_eatingArea1(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt, duringManArea},
				new String[] {"atLocation::at_"+ obj +"_eatingArea1()", "atLocation::at_robot1_manArea1()"},
				new int[] {1, 1});
		operator2res.addConstraint(pickDuration, 0, 0);
		operators.add(operator2res);



		//ccounter	
		SimpleOperator operator41 = new SimpleOperator("robot1::pick_"+obj+"_counter(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt, duringManArea},
				new String[] {"atLocation::at_"+obj+"_counter()", "atLocation::at_robot1_manAreaCounter()"},
				new int[] {1, 1});
		operator41.addConstraint(pickDuration, 0, 0);
		operators.add(operator41);

		SimpleOperator operator42 = new SimpleOperator("atLocation::at_"+obj+"_counter()",
				new AllenIntervalConstraint[] {atStartedByPlace, duringManArea},
				new String[] {"robot1::place_"+ obj +"_counter(arm)", "atLocation::at_robot1_manAreaCounter()"},
				new int[] {0, 0});
		operator42.addConstraint(atDuration, 0, 0);
		operators.add(operator42);

		SimpleOperator operator3a = new SimpleOperator("robot1::holding_"+obj+"(arm)",
				new AllenIntervalConstraint[] {holdingMetByPick},
				new String[] {"robot1::pick_"+obj+"_counter(arm)"},
				new int[] {1});
		operator3a.addConstraint(holdingDuration, 0, 0);
		operators.add(operator3a);


		//tray Stuff
		/*---*/SimpleOperator operator100 = new SimpleOperator("atLocation::at_"+ obj +"_tray1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_"+ obj +"_tray1(arm)"},
				new int[] {0});
		operator100.addConstraint(atDuration, 0, 0);
		operators.add(operator100);		

		/*---*/SimpleOperator operator111 = new SimpleOperator("robot1::place_"+ obj +"_tray1(arm)",
				new AllenIntervalConstraint[] {placeMetByholding},
				new String[] {"robot1::holding_"+ obj +"(arm)"},
				new int[] {1});
		operator111.addConstraint(placeDuration, 0, 0);
		operators.add(operator111);

		/*---*/SimpleOperator operator3cc = new SimpleOperator("robot1::holding_"+ obj +"(arm)",
				new AllenIntervalConstraint[] {holdingMetByPick},
				new String[] {"robot1::pick_"+ obj +"_tray1(arm)"},
				new int[] {1});
		operator3cc.addConstraint(holdingDuration, 0, 0);
		operators.add(operator3cc);

		/*---*/SimpleOperator operator411a = new SimpleOperator("robot1::pick_"+ obj +"_tray1(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt},
				new String[] {"atLocation::at_"+ obj +"_tray1()"},
				new int[] {1});
		operator411a.addConstraint(holdingDuration, 0, 0);
		operators.add(operator411a);

		//Move
		SimpleOperator move1 = new SimpleOperator("robot1::move_manAreaCounter_manArea1()",
				new AllenIntervalConstraint[] {moveMetByFromLocation},
				new String[] {"atLocation::at_robot1_manAreaCounter()"},
				new int[] {0});
		move1.addConstraint(moveDuration, 0, 0);
		operators.add(move1);

		SimpleOperator move2 = new SimpleOperator("atLocation::at_robot1_manArea1()",
				new AllenIntervalConstraint[] {toLocationFinishesMove, new AllenIntervalConstraint(AllenIntervalConstraint.Type.StartedBy, AllenIntervalConstraint.Type.StartedBy.getDefaultBounds())},
				new String[] {"robot1::move_manAreaCounter_manArea1()", "robot1::sense_eatingArea1()"},
				new int[] {0, 0});
		move2.addConstraint(atDuration, 0, 0);
		operators.add(move2);


		return operators;


	}

	private static Vector<SimpleOperator> getObjectPickAndPlaceOperatorSIMPLE(String obj){

		long duration = 5;
		Vector<SimpleOperator> operators = new Vector<SimpleOperator>();

		AllenIntervalConstraint toLocationFinishesMove = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy);
		AllenIntervalConstraint moveMetByFromLocation = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());         
		AllenIntervalConstraint duringManArea = new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds());
		AllenIntervalConstraint pickFinishesAt = new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy, AllenIntervalConstraint.Type.Finishes);
		AllenIntervalConstraint atStartedByPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.StartedBy, AllenIntervalConstraint.Type.StartedBy.getDefaultBounds());
		AllenIntervalConstraint placeMetByholding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint holdingMetByPick = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());              
		AllenIntervalConstraint holdingDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint pickDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint atDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint placeDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint moveDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint senseDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint AfterSensing = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds());

		SimpleOperator operator4 = new SimpleOperator("atLocation::at_"+ obj +"_table1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_"+ obj +"_table1(arm)"},
				new int[] {0});
		operator4.addConstraint(atDuration, 0, 0);
		operators.add(operator4);

		SimpleOperator operator5 = new SimpleOperator("robot1::place_"+ obj +"_table1(arm)",
				new AllenIntervalConstraint[] {placeMetByholding, duringManArea, AfterSensing},
				new String[] {"robot1::holding_"+ obj +"(arm)", "atLocation::at_table1()", "robot1::sense_table1()"},
				new int[] {1,0, 0});
		operator5.addConstraint(placeDuration, 0, 0);
		operators.add(operator5);


		SimpleOperator operator6 = new SimpleOperator("robot1::holding_"+ obj +"(arm)",
				new AllenIntervalConstraint[] {holdingMetByPick},
				new String[] {"robot1::pick_"+ obj +"_table1(arm)"},
				new int[] {1});
		operator6.addConstraint(holdingDuration, 0, 0);
		operators.add(operator6);


		SimpleOperator operator2res = new SimpleOperator("robot1::pick_"+ obj +"_table1(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt, duringManArea, AfterSensing},
				new String[] {"atLocation::at_"+ obj +"_table1()", "atLocation::at_table1()", "robot1::sense_table1()"},
				new int[] {1, 0, 0});
		operator2res.addConstraint(pickDuration, 0, 0);
		operators.add(operator2res);


		//counter        
		SimpleOperator operator41 = new SimpleOperator("robot1::pick_"+obj+"_counter(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt, duringManArea},
				new String[] {"atLocation::at_"+obj+"_counter()", "atLocation::at_counter()"},
				new int[] {1, 0});
		operator41.addConstraint(pickDuration, 0, 0);
		operators.add(operator41);

		SimpleOperator operator42 = new SimpleOperator("atLocation::at_"+obj+"_counter()",
				new AllenIntervalConstraint[] {atStartedByPlace, duringManArea},
				new String[] {"robot1::place_"+ obj +"_counter(arm)", "atLocation::at_counter()"},
				new int[] {0, 0});
		operator42.addConstraint(atDuration, 0, 0);
		operators.add(operator42);

		SimpleOperator operator3a = new SimpleOperator("robot1::holding_"+obj+"(arm)",
				new AllenIntervalConstraint[] {holdingMetByPick},
				new String[] {"robot1::pick_"+obj+"_counter(arm)"},
				new int[] {1});
		operator3a.addConstraint(holdingDuration, 0, 0);
		operators.add(operator3a);


		//tray Stuff
		/*---*/SimpleOperator operator100 = new SimpleOperator("atLocation::at_"+ obj +"_tray1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_"+ obj +"_tray1(arm)"},
				new int[] {0});
		operator100.addConstraint(atDuration, 0, 0);
		operators.add(operator100);             

		/*---*/SimpleOperator operator111 = new SimpleOperator("robot1::place_"+ obj +"_tray1(arm)",
				new AllenIntervalConstraint[] {placeMetByholding},
				new String[] {"robot1::holding_"+ obj +"(arm)"},
				new int[] {1});
		operator111.addConstraint(placeDuration, 0, 0);
		operators.add(operator111);

		/*---*/SimpleOperator operator3cc = new SimpleOperator("robot1::holding_"+ obj +"(arm)",
				new AllenIntervalConstraint[] {holdingMetByPick},
				new String[] {"robot1::pick_"+ obj +"_tray1(arm)"},
				new int[] {1});
		operator3cc.addConstraint(holdingDuration, 0, 0);
		operators.add(operator3cc);

		/*---*/SimpleOperator operator411a = new SimpleOperator("robot1::pick_"+ obj +"_tray1(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt},
				new String[] {"atLocation::at_"+ obj +"_tray1()"},
				new int[] {1});
		operator411a.addConstraint(holdingDuration, 0, 0);
		operators.add(operator411a);

		//Move
		SimpleOperator move1 = new SimpleOperator("robot1::move_counter_table1()",
				new AllenIntervalConstraint[] {moveMetByFromLocation},
				new String[] {"atLocation::at_counter()"},
				new int[] {0});
		move1.addConstraint(moveDuration, 0, 0);
		operators.add(move1);

		SimpleOperator move2 = new SimpleOperator("atLocation::at_table1()",
				new AllenIntervalConstraint[] {toLocationFinishesMove},
				new String[] {"robot1::move_counter_table1()"},
				new int[] {0});
		move2.addConstraint(atDuration, 0, 0);
		operators.add(move2);

		//Sensing
		SimpleOperator sensingTable1 = new SimpleOperator("robot1::sense_table1()",
				new AllenIntervalConstraint[] {duringManArea},
				new String[] {"atLocation::at_table1()"},
				new int[] {0});
		sensingTable1.addConstraint(senseDuration, 0, 0);
		operators.add(sensingTable1);

		return operators;

		

	}


	private static void addOperator(Vector<SimpleOperator> operators) {

		long duration = 1;

		AllenIntervalConstraint atStartedByPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.StartedBy, AllenIntervalConstraint.Type.StartedBy.getDefaultBounds());
		AllenIntervalConstraint pickFinishesAt = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Finishes, AllenIntervalConstraint.Type.Finishes.getDefaultBounds());


		AllenIntervalConstraint atCupAfterPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy, AllenIntervalConstraint.Type.OverlappedBy.getDefaultBounds());
		AllenIntervalConstraint atCup1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint placeCupAfterholding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint placeCup1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint holdingCupAfterPick = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint holdingCup1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint pickCup1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));


		AllenIntervalConstraint atKnifeAfterPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy, AllenIntervalConstraint.Type.OverlappedBy.getDefaultBounds());
		AllenIntervalConstraint atKnife1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint placeKnifeAfterholding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint placeKnife1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint holdingKnifeAfterPick = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint holdingKnife1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint pickKnife1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));


		AllenIntervalConstraint atForkAfterPlace = new AllenIntervalConstraint(AllenIntervalConstraint.Type.OverlappedBy, AllenIntervalConstraint.Type.OverlappedBy.getDefaultBounds());
		AllenIntervalConstraint atFork1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint placeForkAfterholding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint placeFork1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint holdingForkAfterPick = new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.MetBy.getDefaultBounds());
		AllenIntervalConstraint holdingFork1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		AllenIntervalConstraint pickFork1Duration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));


		SimpleOperator operator1 = new SimpleOperator("atLocation::at_cup1_table1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_cup1_table1(arm)"},
				new int[] {0});
		operator1.addConstraint(atCup1Duration, 0, 0);
		operators.add(operator1);

		SimpleOperator operator10 = new SimpleOperator("atLocation::at_cup1_tray1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_cup1_tray1(arm)"},
				new int[] {0});
		operator10.addConstraint(atCup1Duration, 0, 0);
		operators.add(operator10);

		SimpleOperator operator2 = new SimpleOperator("robot1::place_cup1_table1(arm)",
				new AllenIntervalConstraint[] {placeCupAfterholding},
				new String[] {"robot1::holding_cup1(arm)"},
				new int[] {1});
		operator2.addConstraint(placeCup1Duration, 0, 0);
		operators.add(operator2);

		SimpleOperator operator11 = new SimpleOperator("robot1::place_cup1_tray1(arm)",
				new AllenIntervalConstraint[] {placeCupAfterholding},
				new String[] {"robot1::holding_cup1(arm)"},
				new int[] {1});
		operator11.addConstraint(placeCup1Duration, 0, 0);
		operators.add(operator11);

		SimpleOperator operator3a = new SimpleOperator("robot1::holding_cup1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_cup1_counter(arm)"},
				new int[] {1});
		operator3a.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator3a);

		SimpleOperator operator3b = new SimpleOperator("robot1::holding_cup1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_cup1_table1(arm)"},
				new int[] {1});
		operator3b.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator3b);

		SimpleOperator operator3c = new SimpleOperator("robot1::holding_cup1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_cup1_tray1(arm)"},
				new int[] {1});
		operator3c.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator3c);

		SimpleOperator operator42 = new SimpleOperator("robot1::pick_cup1_table1(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt},
				new String[] {"atLocation::at_cup1_table1()"},
				new int[] {100});
		operator42.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator42);

		SimpleOperator operator41 = new SimpleOperator("robot1::pick_cup1_counter(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt},
				new String[] {"atLocation::at_cup1_counter()"},
				new int[] {100});
		operator41.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator41);

		SimpleOperator operator411 = new SimpleOperator("robot1::pick_cup1_tray1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"atLocation::at_cup1_tray1()"},
				new int[] {1});
		operator411.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator411);

		//.....................................................................
		SimpleOperator operator4 = new SimpleOperator("atLocation::at_knife1_table1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_knife1_table1(arm)"},
				new int[] {0});
		operator4.addConstraint(atKnife1Duration, 0, 0);
		operators.add(operator4);

		SimpleOperator operator5 = new SimpleOperator("robot1::place_knife1_table1(arm)",
				new AllenIntervalConstraint[] {placeKnifeAfterholding},
				new String[] {"robot1::holding_knife1(arm)"},
				new int[] {1});
		operator5.addConstraint(placeKnife1Duration, 0, 0);
		operators.add(operator5);

		SimpleOperator operator6 = new SimpleOperator("robot1::holding_knife1(arm)",
				new AllenIntervalConstraint[] {holdingKnifeAfterPick},
				new String[] {"robot1::pick_knife1_table1(arm)"},
				new int[] {1});
		operator6.addConstraint(holdingKnife1Duration, 0, 0);
		operators.add(operator6);

		/*---*/SimpleOperator operator100 = new SimpleOperator("atLocation::at_knife1_tray1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_knife1_tray1(arm)"},
				new int[] {0});
		operator100.addConstraint(atCup1Duration, 0, 0);
		operators.add(operator100);		

		/*---*/SimpleOperator operator111 = new SimpleOperator("robot1::place_knife1_tray1(arm)",
				new AllenIntervalConstraint[] {placeCupAfterholding},
				new String[] {"robot1::holding_knife1(arm)"},
				new int[] {1});
		operator111.addConstraint(placeCup1Duration, 0, 0);
		operators.add(operator111);

		/*---*/SimpleOperator operator3cc = new SimpleOperator("robot1::holding_knife1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_knife1_tray1(arm)"},
				new int[] {1});
		operator3cc.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator3cc);


		SimpleOperator operator2res = new SimpleOperator("robot1::pick_knife1_table1(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt},
				new String[] {"atLocation::at_knife1_table1()"},
				new int[] {1});
		operator2res.addConstraint(pickKnife1Duration, 0, 0);
		operators.add(operator2res);

		/*---*/SimpleOperator operator411a = new SimpleOperator("robot1::pick_knife1_tray1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"atLocation::at_knife1_tray1()"},
				new int[] {1});
		operator411a.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator411a);


		//........................

		SimpleOperator operator7 = new SimpleOperator("atLocation::at_fork1_table1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_fork1_table1(arm)"},
				new int[] {0});
		operator7.addConstraint(atFork1Duration, 0, 0);
		operators.add(operator7);


		SimpleOperator operator8 = new SimpleOperator("robot1::place_fork1_table1(arm)",
				new AllenIntervalConstraint[] {placeForkAfterholding},
				new String[] {"robot1::holding_fork1(arm)"},
				new int[] {1});
		operator8.addConstraint(placeFork1Duration, 0, 0);
		operators.add(operator8);

		SimpleOperator operator9 = new SimpleOperator("robot1::holding_fork1(arm)",
				new AllenIntervalConstraint[] {holdingForkAfterPick},
				new String[] {"robot1::pick_fork1_table1(arm)"},
				new int[] {1});
		operator9.addConstraint(holdingFork1Duration, 0, 0);
		operators.add(operator9);


		SimpleOperator operator4res = new SimpleOperator("robot1::pick_fork1_table1(arm)",
				new AllenIntervalConstraint[] {pickFinishesAt},
				new String[] {"atLocation::at_fork1_table1()"},
				new int[] {1});
		operator4res.addConstraint(pickFork1Duration, 0, 0);
		operators.add(operator4res);

		/*---*/SimpleOperator operator411b = new SimpleOperator("robot1::pick_fork1_tray1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"atLocation::at_fork1_tray1()"},
				new int[] {1});
		operator411b.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator411b);

		/*---*/SimpleOperator operator101 = new SimpleOperator("atLocation::at_fork1_tray1()",
				new AllenIntervalConstraint[] {atStartedByPlace},
				new String[] {"robot1::place_fork1_tray1(arm)"},
				new int[] {0});
		operator101.addConstraint(atCup1Duration, 0, 0);
		operators.add(operator101);		


		/*---*/SimpleOperator operator11a = new SimpleOperator("robot1::place_fork1_tray1(arm)",
				new AllenIntervalConstraint[] {placeCupAfterholding},
				new String[] {"robot1::holding_fork1(arm)"},
				new int[] {1});
		operator11a.addConstraint(placeCup1Duration, 0, 0);
		operators.add(operator11a);


		/*---*/SimpleOperator operator3cb = new SimpleOperator("robot1::holding_fork1(arm)",
				new AllenIntervalConstraint[] {holdingCupAfterPick},
				new String[] {"robot1::pick_fork1_tray1(arm)"},
				new int[] {1});
		operator3cb.addConstraint(holdingCup1Duration, 0, 0);
		operators.add(operator3cb);


	}

}
