
import java.util.Vector;


import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraint;


import spatial.rectangleAlgebra_OLD.AugmentedRectangleConstraint;
import spatial.rectangleAlgebra_OLD.SpatialRule;
import spatial.utility.SpatialRule2;

import org.metacsp.time.Bounds;



public class StaticSpatialKnowledge {
	
	//private static Vector<SpatialRule> spatialRelations = new Vector<SpatialRule>();

	public static Vector<SpatialRule> getSpatialKnowledge(Vector<SpatialRule> spatialRelations){
		
		int manipulationAreaSize = 40;
		int premanipulationAreaSize = 40;
		int pmaSlack = 20; //before and after bounds for premanipulation Area
		int maSlack = 10; //before and after bounds for manipulation Area
		
		long observationAreaSlack = 65;
		long obserAreaSize = 40;
		
		long saw_x = 50; //sitting Area west and east x
		long saw_y = 60; //sitting Area west and east y
		long san_x = 60; //sitting Area north and east x
		long san_y = 50; //sitting Area north and east y
		
		
		
		SpatialRule r01 = new SpatialRule("EatingAreaWest", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Starts, AllenIntervalConstraint.Type.Starts.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals, AllenIntervalConstraint.Type.Equals.getDefaultBounds())
						)
		);
		spatialRelations.add(r01);
		
		SpatialRule r02 = new SpatialRule("EatingAreaEast", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Finishes, AllenIntervalConstraint.Type.Finishes.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals, AllenIntervalConstraint.Type.Equals.getDefaultBounds())
						)
		);
		spatialRelations.add(r02);

		
		SpatialRule r03 = new SpatialRule("EatingAreaNorth", "VerticalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals, AllenIntervalConstraint.Type.Equals.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Finishes, AllenIntervalConstraint.Type.Finishes.getDefaultBounds())
						)
		);
		
		spatialRelations.add(r03);
		
		SpatialRule r04 = new SpatialRule("EatingAreaSouth", "VerticalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals, AllenIntervalConstraint.Type.Equals.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Starts, AllenIntervalConstraint.Type.Starts.getDefaultBounds())
						)
		);
		spatialRelations.add(r04);

		SpatialRule r1 = new SpatialRule("ManipulationAreaSouth", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(maSlack,maSlack))
						)
		
		
		
		);
		spatialRelations.add(r1);
		
		SpatialRule r2 = new SpatialRule("ManipulationAreaNorth", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(maSlack,maSlack))
						)
		);
		spatialRelations.add(r2);
		
		SpatialRule r3 = new SpatialRule("SittingAreaWest", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Meets),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(5, 5))
						)
		);
		spatialRelations.add(r3);
		
		SpatialRule r4 = new SpatialRule("SittingAreaEast", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(5, 5))
						)
		);
		spatialRelations.add(r4);
		
		SpatialRule r5 = new SpatialRule("PlacingAreaWestRight", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(35, 35)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(30, 30))
						)
		);
		spatialRelations.add(r5);

		SpatialRule r6 = new SpatialRule("PlacingAreaWestLeft", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(35, 35)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(30, 30), new Bounds(5, 5))
						)
		);
		spatialRelations.add(r6);

		SpatialRule r7 = new SpatialRule("PlacingAreaEastRight", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(35, 35), new Bounds(5, 5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(30, 30), new Bounds(5, 5))
						)
		);
		spatialRelations.add(r7);
		
		SpatialRule r8 = new SpatialRule("PlacingAreaEastLeft", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(35, 35), new Bounds(5, 5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(30, 30))
						)
		);
		spatialRelations.add(r8);

//		ManipulationAreaWest Meets, Equals VerticalTable
		SpatialRule r9 = new SpatialRule("ManipulationAreaWest", "VerticalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(maSlack,maSlack)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds())
						)
		);
		spatialRelations.add(r9);
		
//		ManipulationAreaEast Meets , Equals VerticalTable
		SpatialRule r10 = new SpatialRule("ManipulationAreaEast", "VerticalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(maSlack,maSlack)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds())
						)
		);
		spatialRelations.add(r10);

//		SittingAreaSouth During[5, 5][5, 5], Meets VerticalTable 
		SpatialRule r11 = new SpatialRule("SittingAreaSouth", "VerticalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(5, 5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Meets)
						)
		);
		spatialRelations.add(r11);

//		SittingAreaNorth During[5, 5][5, 5], Meets VerticalTable
		SpatialRule r12 = new SpatialRule("SittingAreaNorth", "VerticalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(5, 5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy)
						)
		);
		spatialRelations.add(r12);
		
//		PlacingAreaNorthRight During[5, 5][30, 30], During[35, 35][5, 5] VerticalTable (26)
		SpatialRule r13 = new SpatialRule("PlacingAreaNorthRight", "VerticalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(30, 30)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(35, 35), new Bounds(5, 5))
						)
		);
		spatialRelations.add(r13);

		
//		PlacingAreaNorthLeft During[30, 30][5, 5], During[35, 35][5, 5] VerticalTable (27)
		SpatialRule r14 = new SpatialRule("PlacingAreaNorthLeft", "VerticalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(30, 30), new Bounds(5, 5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(35, 35), new Bounds(5, 5))
						)
		);
		spatialRelations.add(r14);

		
		
//		PlacingAreaSouthRight During[30, 30][5, 5], During[5, 5][35, 35] VerticalTable 
		SpatialRule r15 = new SpatialRule("PlacingAreaSouthRight", "VerticalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(30, 30), new Bounds(5, 5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(35, 35))
						)
		);
		spatialRelations.add(r15);

		
//		PlacingAreaSouthLeft During[5, 5][30, 30], During[5, 5][35, 35] TableVertical (29)
		SpatialRule r16 = new SpatialRule("PlacingAreaSouthLeft", "VerticalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(30, 30)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(35, 35))
						)
		);
		spatialRelations.add(r16);
		
//		the y axis is manipulated in order to close to hard code version! This number is not consistent with D1.3
		SpatialRule r17 = new SpatialRule("ManipulationAreaEast", "Counter", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(maSlack - 9,maSlack - 9)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds())
						)
		);
		spatialRelations.add(r17);

		
//		PlacingAreaSouth During[35, 35][5, 5], During[15, 15][55, 55] Counter
		SpatialRule r18 = new SpatialRule("PlacingAreaEastLeft", "Counter", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(35, 35), new Bounds(5, 5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(15, 15), new Bounds(55, 55))
						)
		);
		spatialRelations.add(r18);

		
//		PlacingAreaNorth During[35, 35][5, 5], During[55, 55][15, 15] Counter (34)
		SpatialRule r19 = new SpatialRule("PlacingAreaEastRight", "Counter", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(35, 35), new Bounds(5, 5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(55, 55), new Bounds(15, 15))
						)
		);
		spatialRelations.add(r19);
		
		
		SpatialRule r20 = new SpatialRule("ManipulationAreaNorth", "ManipulationAreaNorth", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(manipulationAreaSize,manipulationAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(manipulationAreaSize,manipulationAreaSize))));
		spatialRelations.add(r20);

		SpatialRule r21 = new SpatialRule("ManipulationAreaSouth", "ManipulationAreaSouth", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(manipulationAreaSize,manipulationAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(manipulationAreaSize,manipulationAreaSize))));
		spatialRelations.add(r21);
		
		SpatialRule r22 = new SpatialRule("ManipulationAreaWest", "ManipulationAreaWest", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(manipulationAreaSize,manipulationAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(manipulationAreaSize,manipulationAreaSize))));
		spatialRelations.add(r22);
		
		SpatialRule r23 = new SpatialRule("ManipulationAreaEast", "ManipulationAreaEast", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(manipulationAreaSize,manipulationAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(manipulationAreaSize,manipulationAreaSize))));
		spatialRelations.add(r23);
		

		SpatialRule r24 = new SpatialRule("SittingAreaWest", "SittingAreaWest", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(saw_x,saw_x)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(saw_y,saw_y))));
		spatialRelations.add(r24);
		
		SpatialRule r25 = new SpatialRule("SittingAreaEast", "SittingAreaEast", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(saw_x,saw_x)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(saw_y,saw_y))));
		spatialRelations.add(r25);
		
		
		SpatialRule r26 = new SpatialRule("SittingAreaNorth", "SittingAreaNorth", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(san_x,san_x)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(san_y,san_y))));
		spatialRelations.add(r26);
		
		SpatialRule r27 = new SpatialRule("SittingAreaSouth", "SittingAreaSouth", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(san_x,san_x)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(san_y,san_y))));
		spatialRelations.add(r27);

		//..................................................................
		SpatialRule r28 = new SpatialRule("PreManipulationAreaNorth", "PreManipulationAreaNorth", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(premanipulationAreaSize,premanipulationAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(premanipulationAreaSize,premanipulationAreaSize))));
		spatialRelations.add(r28);

		SpatialRule r29 = new SpatialRule("PreManipulationAreaSouth", "PreManipulationAreaSouth", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(premanipulationAreaSize,premanipulationAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(premanipulationAreaSize,premanipulationAreaSize))));
		spatialRelations.add(r29);
		
		SpatialRule r30 = new SpatialRule("PreManipulationAreaWest", "PreManipulationAreaWest", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(premanipulationAreaSize,premanipulationAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(premanipulationAreaSize,premanipulationAreaSize))));
		spatialRelations.add(r30);
		
		SpatialRule r31 = new SpatialRule("PreManipulationAreaEast", "PreManipulationAreaEast", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(premanipulationAreaSize,premanipulationAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(premanipulationAreaSize,premanipulationAreaSize))));
		spatialRelations.add(r31);

		SpatialRule r32 = new SpatialRule("PreManipulationAreaSouth", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(manipulationAreaSize + pmaSlack + 20 ,manipulationAreaSize + pmaSlack + 20))
						)
		);
		spatialRelations.add(r32);
		
		SpatialRule r33 = new SpatialRule("PreManipulationAreaNorth", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(manipulationAreaSize + pmaSlack + 20 ,manipulationAreaSize + pmaSlack + 20))
						)
		);
		spatialRelations.add(r33);


		//////////////////////////////////////////////////////////////////////////////////
		
		SpatialRule r34 = new SpatialRule("PreManipulationAreaWest", "VerticalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(manipulationAreaSize + pmaSlack,manipulationAreaSize + pmaSlack)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds())
						)
		);
		spatialRelations.add(r34);
		
//		ManipulationAreaEast Meets , Equals VerticalTable
		SpatialRule r35 = new SpatialRule("PreManipulationAreaEast", "VerticalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(manipulationAreaSize + pmaSlack,manipulationAreaSize + pmaSlack)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds())
						)
		);
		spatialRelations.add(r35);
		
		
		
		//_______________________________________________________________________________________________________________________________________
		
		SpatialRule r24a = new SpatialRule("ObservationAreaWest", "ObservationAreaWest", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(obserAreaSize,obserAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(obserAreaSize,obserAreaSize))));
		spatialRelations.add(r24a);
		
		SpatialRule r25a = new SpatialRule("ObservationAreaEast", "ObservationAreaEast", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(obserAreaSize,obserAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(obserAreaSize,obserAreaSize))));
		spatialRelations.add(r25a);
		
		
		SpatialRule r26a = new SpatialRule("ObservationAreaNorth", "ObservationAreaNorth", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(obserAreaSize,obserAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(obserAreaSize,obserAreaSize))));
		spatialRelations.add(r26a);
		
		SpatialRule r27a = new SpatialRule("ObservationAreaSouth", "ObservationAreaSouth", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(obserAreaSize,obserAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(obserAreaSize,obserAreaSize))));
		spatialRelations.add(r27a);
		
		
		SpatialRule r32a = new SpatialRule("ObservationAreaWest", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(observationAreaSlack ,observationAreaSlack)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds())						
						)
		);
		spatialRelations.add(r32a);
		
		SpatialRule r33a = new SpatialRule("ObservationAreaEast", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(observationAreaSlack , observationAreaSlack)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds())						
						)
		);
		spatialRelations.add(r33a);


		
		SpatialRule r34a = new SpatialRule("ObservationAreaNorth", "VerticalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(observationAreaSlack , observationAreaSlack))
						)
		);
		spatialRelations.add(r34a);
		
		SpatialRule r35a = new SpatialRule("ObservationAreaSouth", "VerticalTable", 
				new AugmentedRectangleConstraint(						
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(observationAreaSlack ,observationAreaSlack))
						)
		);
		spatialRelations.add(r35a);
		
		//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//		ManipulationAreaEast Meets , During[35, 35][35, 35] Counter
		SpatialRule r36 = new SpatialRule("PreManipulationAreaEast", "Counter", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(manipulationAreaSize + pmaSlack,manipulationAreaSize + pmaSlack)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds())
						)
		);
		spatialRelations.add(r36);
		
		
		//NearAreaCounter
		SpatialRule r37 = new SpatialRule("NearAreaCounter", "Counter", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Contains, new Bounds(100, 100), new Bounds(100, 100)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Contains, new Bounds(100, 100), new Bounds(100, 100))
						)
		);
		spatialRelations.add(r37);
		
		//NearAreaTable
		SpatialRule r38 = new SpatialRule("NearAreaTable", "VerticalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Contains, new Bounds(100, 100), new Bounds(100, 100)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Contains, new Bounds(100, 100), new Bounds(100, 100))
						)
		);
		spatialRelations.add(r38);
		
		//NearAreaTable
		SpatialRule r39 = new SpatialRule("NearAreaTable", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Contains, new Bounds(100, 100), new Bounds(100, 100)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Contains, new Bounds(100, 100), new Bounds(100, 100))
						)
		);
		spatialRelations.add(r39);
		
		
		return spatialRelations;
		
	}
	
	public static Vector<SpatialRule2> getSpatialKnowledge2(Vector<SpatialRule2> spatialRelations){
		
		int manipulationAreaSize = 40;
		int premanipulationAreaSize = 40;
		int pmaSlack = 20; //before and after bounds for premanipulation Area
		int maSlack = 10; //before and after bounds for manipulation Area
		SpatialRule2 r1 = new SpatialRule2("ManipulationAreaSouth", "HorizontalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(maSlack,maSlack))
						)
		
		
		
		);
		spatialRelations.add(r1);
		
		SpatialRule2 r2 = new SpatialRule2("ManipulationAreaNorth", "HorizontalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(maSlack,maSlack))
						)
		);
		spatialRelations.add(r2);
		
		SpatialRule2 r3 = new SpatialRule2("SittingAreaWest", "HorizontalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Meets),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(5, 5))
						)
		);
		spatialRelations.add(r3);
		
		SpatialRule2 r4 = new SpatialRule2("SittingAreaEast", "HorizontalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(5, 5))
						)
		);
		spatialRelations.add(r4);
		
		SpatialRule2 r5 = new SpatialRule2("PlacingAreaWestRight", "HorizontalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(35, 35)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(30, 30))
						)
		);
		spatialRelations.add(r5);

		SpatialRule2 r6 = new SpatialRule2("PlacingAreaWestLeft", "HorizontalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(35, 35)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(30, 30), new Bounds(5, 5))
						)
		);
		spatialRelations.add(r6);

		SpatialRule2 r7 = new SpatialRule2("PlacingAreaEastRight", "HorizontalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(35, 35), new Bounds(5, 5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(30, 30), new Bounds(5, 5))
						)
		);
		spatialRelations.add(r7);
		
		SpatialRule2 r8 = new SpatialRule2("PlacingAreaEastLeft", "HorizontalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(35, 35), new Bounds(5, 5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(30, 30))
						)
		);
		spatialRelations.add(r8);

//		ManipulationAreaWest Meets, Equals VerticalTable
		SpatialRule2 r9 = new SpatialRule2("ManipulationAreaWest", "VerticalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(maSlack,maSlack)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds())
						)
		);
		spatialRelations.add(r9);
		
//		ManipulationAreaEast Meets , Equals VerticalTable
		SpatialRule2 r10 = new SpatialRule2("ManipulationAreaEast", "VerticalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(maSlack,maSlack)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds())
						)
		);
		spatialRelations.add(r10);

//		SittingAreaSouth During[5, 5][5, 5], Meets VerticalTable 
		SpatialRule2 r11 = new SpatialRule2("SittingAreaSouth", "VerticalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(5, 5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Meets)
						)
		);
		spatialRelations.add(r11);

//		SittingAreaNorth During[5, 5][5, 5], Meets VerticalTable
		SpatialRule2 r12 = new SpatialRule2("SittingAreaNorth", "VerticalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(5, 5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy)
						)
		);
		spatialRelations.add(r12);
		
//		PlacingAreaNorthRight During[5, 5][30, 30], During[35, 35][5, 5] VerticalTable (26)
		SpatialRule2 r13 = new SpatialRule2("PlacingAreaNorthRight", "VerticalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(30, 30)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(35, 35), new Bounds(5, 5))
						)
		);
		spatialRelations.add(r13);

		
//		PlacingAreaNorthLeft During[30, 30][5, 5], During[35, 35][5, 5] VerticalTable (27)
		SpatialRule2 r14 = new SpatialRule2("PlacingAreaNorthLeft", "VerticalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(30, 30), new Bounds(5, 5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(35, 35), new Bounds(5, 5))
						)
		);
		spatialRelations.add(r14);

		
		
//		PlacingAreaSouthRight During[30, 30][5, 5], During[5, 5][35, 35] VerticalTable 
		SpatialRule2 r15 = new SpatialRule2("PlacingAreaSouthRight", "VerticalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(30, 30), new Bounds(5, 5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(35, 35))
						)
		);
		spatialRelations.add(r15);

		
//		PlacingAreaSouthLeft During[5, 5][30, 30], During[5, 5][35, 35] TableVertical (29)
		SpatialRule2 r16 = new SpatialRule2("PlacingAreaSouthLeft", "VerticalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(30, 30)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(5, 5), new Bounds(35, 35))
						)
		);
		spatialRelations.add(r16);
		
//		the y axis is manipulated in order to close to hard code version! This number is not consistent with D1.3
		SpatialRule2 r17 = new SpatialRule2("ManipulationAreaEast", "Counter", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(maSlack - 9,maSlack - 9)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds())
						)
		);
		spatialRelations.add(r17);

		
//		PlacingAreaSouth During[35, 35][5, 5], During[15, 15][55, 55] Counter
		SpatialRule2 r18 = new SpatialRule2("PlacingAreaEastLeft", "Counter", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(35, 35), new Bounds(5, 5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(15, 15), new Bounds(55, 55))
						)
		);
		spatialRelations.add(r18);

		
//		PlacingAreaNorth During[35, 35][5, 5], During[55, 55][15, 15] Counter (34)
		SpatialRule2 r19 = new SpatialRule2("PlacingAreaEastRight", "Counter", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(35, 35), new Bounds(5, 5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(55, 55), new Bounds(15, 15))
						)
		);
		spatialRelations.add(r19);
		
		
		SpatialRule2 r20 = new SpatialRule2("ManipulationAreaNorth", "ManipulationAreaNorth", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(manipulationAreaSize,manipulationAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(manipulationAreaSize,manipulationAreaSize))));
		spatialRelations.add(r20);

		SpatialRule2 r21 = new SpatialRule2("ManipulationAreaSouth", "ManipulationAreaSouth", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(manipulationAreaSize,manipulationAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(manipulationAreaSize,manipulationAreaSize))));
		spatialRelations.add(r21);
		
		SpatialRule2 r22 = new SpatialRule2("ManipulationAreaWest", "ManipulationAreaWest", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(manipulationAreaSize,manipulationAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(manipulationAreaSize,manipulationAreaSize))));
		spatialRelations.add(r22);
		
		SpatialRule2 r23 = new SpatialRule2("ManipulationAreaEast", "ManipulationAreaEast", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(manipulationAreaSize,manipulationAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(manipulationAreaSize,manipulationAreaSize))));
		spatialRelations.add(r23);
		
		SpatialRule2 r24 = new SpatialRule2("SittingAreaWest", "SittingAreaWest", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(85,85)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(60,60))));
		spatialRelations.add(r24);
		
		SpatialRule2 r25 = new SpatialRule2("SittingAreaEast", "SittingAreaEast", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(85,85)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(60,60))));
		spatialRelations.add(r25);
		
		
		SpatialRule2 r26 = new SpatialRule2("SittingAreaNorth", "SittingAreaNorth", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(60,60)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(85,85))));
		spatialRelations.add(r26);
		
		SpatialRule2 r27 = new SpatialRule2("SittingAreaSouth", "SittingAreaSouth", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(60,60)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(85,85))));
		spatialRelations.add(r27);

		//..................................................................
		SpatialRule2 r28 = new SpatialRule2("PreManipulationAreaNorth", "PreManipulationAreaNorth", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(premanipulationAreaSize,premanipulationAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(premanipulationAreaSize,premanipulationAreaSize))));
		spatialRelations.add(r28);

		SpatialRule2 r29 = new SpatialRule2("PreManipulationAreaSouth", "PreManipulationAreaSouth", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(premanipulationAreaSize,premanipulationAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(premanipulationAreaSize,premanipulationAreaSize))));
		spatialRelations.add(r29);
		
		SpatialRule2 r30 = new SpatialRule2("PreManipulationAreaWest", "PreManipulationAreaWest", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(premanipulationAreaSize,premanipulationAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(premanipulationAreaSize,premanipulationAreaSize))));
		spatialRelations.add(r30);
		
		SpatialRule2 r31 = new SpatialRule2("PreManipulationAreaEast", "PreManipulationAreaEast", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(premanipulationAreaSize,premanipulationAreaSize)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(premanipulationAreaSize,premanipulationAreaSize))));
		spatialRelations.add(r31);

		SpatialRule2 r32 = new SpatialRule2("PreManipulationAreaSouth", "HorizontalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(manipulationAreaSize + pmaSlack + 20 ,manipulationAreaSize + pmaSlack + 20))
						)
		);
		spatialRelations.add(r32);
		
		SpatialRule2 r33 = new SpatialRule2("PreManipulationAreaNorth", "HorizontalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(manipulationAreaSize + pmaSlack + 20 ,manipulationAreaSize + pmaSlack + 20))
						)
		);
		spatialRelations.add(r33);


	//		ManipulationAreaWest Meets, Equals VerticalTable
		SpatialRule2 r34 = new SpatialRule2("PreManipulationAreaWest", "VerticalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(manipulationAreaSize + pmaSlack,manipulationAreaSize + pmaSlack)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds())
						)
		);
		spatialRelations.add(r34);
		
//		ManipulationAreaEast Meets , Equals VerticalTable
		SpatialRule2 r35 = new SpatialRule2("PreManipulationAreaEast", "VerticalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(manipulationAreaSize + pmaSlack,manipulationAreaSize + pmaSlack)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds())
						)
		);
		spatialRelations.add(r35);

//		ManipulationAreaEast Meets , During[35, 35][35, 35] Counter
		SpatialRule2 r36 = new SpatialRule2("PreManipulationAreaEast", "Counter", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(manipulationAreaSize + pmaSlack,manipulationAreaSize + pmaSlack)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds())
						)
		);
		spatialRelations.add(r36);
		
		
		//NearAreaCounter
		SpatialRule2 r37 = new SpatialRule2("NearAreaCounter", "Counter", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Contains, new Bounds(100, 100), new Bounds(100, 100)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Contains, new Bounds(100, 100), new Bounds(100, 100))
						)
		);
		spatialRelations.add(r37);
		
		//NearAreaTable
		SpatialRule2 r38 = new SpatialRule2("NearAreaTable", "VerticalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Contains, new Bounds(100, 100), new Bounds(100, 100)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Contains, new Bounds(100, 100), new Bounds(100, 100))
						)
		);
		spatialRelations.add(r38);
		
		//NearAreaTable
		SpatialRule2 r39 = new SpatialRule2("NearAreaTable", "HorizontalTable", 
				new RectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Contains, new Bounds(100, 100), new Bounds(100, 100)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Contains, new Bounds(100, 100), new Bounds(100, 100))
						)
		);
		spatialRelations.add(r39);
		
		long table_half_size = 35;
		long table_ful_size = 70;
		
		SpatialRule2 r40 = new SpatialRule2("EatingAreaWest", "EatingAreaWest", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(table_half_size,table_half_size)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(table_ful_size,table_ful_size))));
		spatialRelations.add(r40);
		
		SpatialRule2 r41 = new SpatialRule2("EatingAreaEast", "EatingAreaEast", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(table_half_size,table_half_size)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(table_ful_size,table_ful_size))));
		spatialRelations.add(r41);
		
		
		SpatialRule2 r42 = new SpatialRule2("EatingAreaNorth", "EatingAreaNorth", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(table_ful_size,table_ful_size)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(table_half_size,table_half_size))));
		spatialRelations.add(r42);
		
		SpatialRule2 r43 = new SpatialRule2("EatingAreaSouth", "EatingAreaSouth", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(table_ful_size,table_ful_size)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(table_half_size,table_half_size))));
		spatialRelations.add(r43);
		
		return spatialRelations;
		
	}

	
	
}
