package test.meta.reachability;

import java.util.Vector;
import java.util.logging.Level;

import org.metacsp.framework.Constraint;
import org.metacsp.framework.ConstraintNetwork;
import org.metacsp.meta.hybridPlanner.ManipulationAreaDomain;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraintSolver;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangularRegion;
import org.metacsp.multi.spatial.rectangleAlgebra.UnaryRectangleConstraint;
import org.metacsp.multi.spatioTemporal.SpatialFluentSolver;
import org.metacsp.spatial.utility.SpatialRule;
import org.metacsp.time.Bounds;
import org.metacsp.utility.logging.MetaCSPLogging;

import com.hp.hpl.jena.sparql.pfunction.library.container;

public class TestManipulationConstraint {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
	 
		RectangleConstraintSolver recSolver = new RectangleConstraintSolver(0,1000);
	
		RectangularRegion placingRecVar = (RectangularRegion) recSolver.createVariable();
		placingRecVar.setName("placingArea");
	
		RectangularRegion manipulationAreaPrototype = (RectangularRegion) recSolver.createVariable();
		manipulationAreaPrototype.setName("manipulationArea");		

		
		RectangularRegion objectFleunt = (RectangularRegion) recSolver.createVariable();
		objectFleunt.setName("objectFleunt");		
		
		RectangularRegion supportFluent = (RectangularRegion) recSolver.createVariable();
		supportFluent.setName("supportFluent");		
		
		Vector<Constraint> allConstraints = new Vector<Constraint>();
		ManipulationAreaDomain manipulationAreaDomain = new ManipulationAreaDomain();
		Vector<SpatialRule> srules = manipulationAreaDomain.getSpatialRulesByRelation("RA_west");
		
		//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		//cup [[212, 212], [222, 222]], [[224, 229], [234, 239]]]}
		//fork 211, 211], [223, 223]], [[244, 254], [248, 259
		UnaryRectangleConstraint atobjectFleunt = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
//				new Bounds(212,212), new Bounds(222, 222), new Bounds(224,229), new Bounds(234,239));
				new Bounds(211,211), new Bounds(223, 223), new Bounds(244,254), new Bounds(248,259));
//				new Bounds(211,211), new Bounds(223, 223), new Bounds(230,230), new Bounds(234,234));

		atobjectFleunt.setFrom(objectFleunt);
		atobjectFleunt.setTo(objectFleunt);
		allConstraints.add(atobjectFleunt);
		
		//200, 270, 200, 270		
		UnaryRectangleConstraint atsupportFluent = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, new Bounds(200,200), new Bounds(270,270), new Bounds(200,200), new Bounds(270,270));
		atsupportFluent.setFrom(supportFluent);
		atsupportFluent.setTo(supportFluent);
		allConstraints.add(atsupportFluent);
		
		//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		//creating spatial constraint
		//the order is fixed and based on the fixed order which manipulation domain has defined
		//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		//"manipulationArea", "manipulationArea",
		Bounds[] sizeBounds = new Bounds[srules.get(0).getUnaryRAConstraint().getBounds().length];
		for (int j = 0; j < sizeBounds.length; j++) {
			Bounds bSize = new Bounds(
					srules.get(0).getUnaryRAConstraint().getBounds()[j].min,
					srules.get(0).getUnaryRAConstraint().getBounds()[j].max);
			sizeBounds[j] = bSize;
		}
		UnaryRectangleConstraint sizemanipulationArea = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, sizeBounds);
		sizemanipulationArea.setFrom(manipulationAreaPrototype);
		sizemanipulationArea.setTo(manipulationAreaPrototype);
		allConstraints.add(sizemanipulationArea);

		//"placingArea", "placingArea", 
		Bounds[] sizeBoundsPlacing = new Bounds[srules.get(1).getUnaryRAConstraint().getBounds().length];
		for (int j = 0; j < sizeBoundsPlacing.length; j++) {
			Bounds bSize = new Bounds(
					srules.get(1).getUnaryRAConstraint().getBounds()[j].min,
					srules.get(1).getUnaryRAConstraint().getBounds()[j].max);
			sizeBoundsPlacing[j] = bSize;
		}
		UnaryRectangleConstraint sizePlacingArea = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, sizeBoundsPlacing);
		sizePlacingArea.setFrom(placingRecVar);
		sizePlacingArea.setTo(placingRecVar);
		allConstraints.add(sizePlacingArea);
		
		
		for (int i = 2; i < srules.size(); i++) {
			
			
			//"manipulationArea", "table", 
			if(i == 4){				
				System.out.println(srules.get(4).getBinaryRAConstraint().clone());
				RectangleConstraint manipulationAreaTOtable = (RectangleConstraint)srules.get(4).getBinaryRAConstraint().clone(); 
//				RectangleConstraint manipulationAreaTOtable = new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Meets , AllenIntervalConstraint.Type.Meets.getDefaultBounds()),
//						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Meets, AllenIntervalConstraint.Type.Overlaps, AllenIntervalConstraint.Type.During, 
//								AllenIntervalConstraint.Type.OverlappedBy, AllenIntervalConstraint.Type.MetBy, AllenIntervalConstraint.Type.After));

				manipulationAreaTOtable.setFrom(manipulationAreaPrototype);
				manipulationAreaTOtable.setTo(supportFluent);
				allConstraints.add(manipulationAreaTOtable);
				continue;
			}
			
			//general rule
			Bounds[] allenBoundsX = new Bounds[(srules.get(i).getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[0].getBounds().length];
			for (int j = 0; j < allenBoundsX.length; j++) {
				Bounds bx = new Bounds(
						(srules.get(i).getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[0].getBounds()[j].min, (srules.get(i)
								.getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[0].getBounds()[j].max);
				allenBoundsX[j] = bx;
			}

			Bounds[] allenBoundsY = new Bounds[(srules.get(i).getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[1].getBounds().length];
			for (int j = 0; j < allenBoundsY.length; j++) {
				Bounds by = new Bounds(
						(srules.get(i).getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[1]
								.getBounds()[j].min, (srules.get(i).getBinaryRAConstraint())
								.getInternalAllenIntervalConstraints()[1].getBounds()[j].max);
				allenBoundsY[j] = by;
			}

			AllenIntervalConstraint xAllenCon = new AllenIntervalConstraint((srules.get(i).getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[0].getType(), allenBoundsX);
			AllenIntervalConstraint yAllenCon = new AllenIntervalConstraint(
					(srules.get(i).getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[1].getType(), allenBoundsY);


			//This part is for the Allen intervals do not have any bounds e.g., Equals
			if((srules.get(i).getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[0].getBounds().length == 0)
				xAllenCon = (AllenIntervalConstraint)(srules.get(i).getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[0].clone();
			if((srules.get(i).getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[1].getBounds().length == 0)
				yAllenCon = (AllenIntervalConstraint)(srules.get(i).getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[1].clone();
			
			
			//"placingArea", "manipulationArea",
			if(i == 2){
				RectangleConstraint placingAreaTOmanipulationArea = new RectangleConstraint(xAllenCon, yAllenCon);
				placingAreaTOmanipulationArea.setFrom(placingRecVar);
				placingAreaTOmanipulationArea.setTo(manipulationAreaPrototype);
				allConstraints.add(placingAreaTOmanipulationArea);
				
			}

			
			//"object", "placingArea", 
			else if(i == 3){
				RectangleConstraint objectToPlacingArea = new RectangleConstraint(xAllenCon, yAllenCon);
				objectToPlacingArea.setFrom(objectFleunt);
				objectToPlacingArea.setTo(placingRecVar);
				allConstraints.add(objectToPlacingArea);
				
			}
			


			
		}

		Constraint[] allConstraintsArray = allConstraints.toArray(new Constraint[allConstraints.size()]);
		if (!recSolver.addConstraints(allConstraintsArray)) { 
			System.out.println("Failed to add constraints!");
		}
		else{
			System.out.println("successfully added");
		}
		
		RectangularRegion[] rect = {objectFleunt, supportFluent, manipulationAreaPrototype, placingRecVar};
		System.out.println(recSolver.drawAlmostCentreRectangle(300, rect));
		//ConstraintNetwork.draw(recSolver.getConstraintNetwork());
//        recSolver.removeConstraint(atobjectFleunt);
		System.out.println(recSolver.getConstraintNetwork());
		

		
	}

}
