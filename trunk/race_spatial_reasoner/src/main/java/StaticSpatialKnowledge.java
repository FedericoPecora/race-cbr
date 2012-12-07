import java.awt.Rectangle;
import java.util.Vector;

import meta.MetaSpatialConstraint;
import meta.MetaSpatialConstraintSolver;
import multi.allenInterval.AllenIntervalConstraint;

import spatial.rectangleAlgebra.*;
import time.Bounds;




public class StaticSpatialKnowledge {
	
	private static Vector<SpatialRule> spatialRelations = new Vector<SpatialRule>();

	public static Vector<SpatialRule> getSpatialKnowledge(){
		
		SpatialRule r1 = new SpatialRule("ManipulationAreaSouth", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Meets)
						)
		);
		spatialRelations.add(r1);
		
		SpatialRule r2 = new SpatialRule("ManipulationAreaNorth", "HorizontalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals),
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
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Meets),
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
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(35, 35), new Bounds(30, 30)),
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
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals)
						)
		);
		spatialRelations.add(r9);
		
//		ManipulationAreaEast Meets , Equals VerticalTable
		SpatialRule r10 = new SpatialRule("ManipulationAreaEast", "VerticalTable", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Meets),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals)
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
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Meets)
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
		
//		ManipulationAreaEast Meets , During[35, 35][35, 35] Counter
		SpatialRule r17 = new SpatialRule("ManipulationAreaEast", "Counter", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(35, 35), new Bounds(35, 35)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Meets)
						)
		);
		spatialRelations.add(r17);

		
//		PlacingAreaSouth During[35, 35][5, 5], During[15, 15][55, 55] Counter
		SpatialRule r18 = new SpatialRule("PlacingAreaSouth", "Counter", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(35, 35), new Bounds(5, 5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(15, 15), new Bounds(55, 55))
						)
		);
		spatialRelations.add(r18);

		
//		PlacingAreaNorth During[35, 35][5, 5], During[55, 55][15, 15] Counter (34)
		SpatialRule r19 = new SpatialRule("PlacingAreaNorth", "Counter", 
				new AugmentedRectangleConstraint(
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(35, 35), new Bounds(5, 5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, new Bounds(55, 55), new Bounds(15, 15))
						)
		);
		spatialRelations.add(r19);


		
		return spatialRelations;
		
	}
	

}
