import java.awt.Rectangle;
import java.util.Vector;

import meta.MetaSpatialConstraint;
import meta.MetaSpatialConstraintSolver;
import multi.allenInterval.AllenIntervalConstraint;

import spatial.rectangleAlgebra.*;
import time.APSPSolver;
import time.Bounds;




public class StaticSpatialKnowledge {
	
	//private static Vector<SpatialRule> spatialRelations = new Vector<SpatialRule>();

	public static Vector<SpatialRule> getSpatialKnowledge(Vector<SpatialRule> spatialRelations){
		
		int manipulationAreaSize = 40;
		int premanipulationAreaSize = 40;
		
		SpatialRule r1 = new SpatialRule("ManipulationAreaSouth", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Meets)
						)
		);
		spatialRelations.add(r1);
		
		SpatialRule r2 = new SpatialRule("ManipulationAreaNorth", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy)
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
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Meets),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds())
						)
		);
		spatialRelations.add(r9);
		
//		ManipulationAreaEast Meets , Equals VerticalTable
		SpatialRule r10 = new SpatialRule("ManipulationAreaEast", "VerticalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy),
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
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.MetBy),
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
						new Bounds(85,85)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(60,60))));
		spatialRelations.add(r24);
		
		SpatialRule r25 = new SpatialRule("SittingAreaEast", "SittingAreaEast", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(85,85)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(60,60))));
		spatialRelations.add(r25);
		
		
		SpatialRule r26 = new SpatialRule("SittingAreaNorth", "SittingAreaNorth", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(60,60)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(85,85))));
		spatialRelations.add(r26);
		
		SpatialRule r27 = new SpatialRule("SittingAreaSouth", "SittingAreaSouth", 
				new AugmentedRectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, 
						new Bounds(60,60)), new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(85,85))));
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
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(manipulationAreaSize,manipulationAreaSize))
						)
		);
		spatialRelations.add(r32);
		
		SpatialRule r33 = new SpatialRule("PreManipulationAreaNorth", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(manipulationAreaSize,manipulationAreaSize))
						)
		);
		spatialRelations.add(r33);


	//		ManipulationAreaWest Meets, Equals VerticalTable
		SpatialRule r34 = new SpatialRule("PreManipulationAreaWest", "VerticalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, new Bounds(manipulationAreaSize,manipulationAreaSize)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds())
						)
		);
		spatialRelations.add(r34);
		
//		ManipulationAreaEast Meets , Equals VerticalTable
		SpatialRule r35 = new SpatialRule("PreManipulationAreaEast", "VerticalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(manipulationAreaSize,manipulationAreaSize)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds())
						)
		);
		spatialRelations.add(r35);

//		ManipulationAreaEast Meets , During[35, 35][35, 35] Counter
		SpatialRule r36 = new SpatialRule("PreManipulationAreaEast", "Counter", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(manipulationAreaSize,manipulationAreaSize)),
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
	

}
