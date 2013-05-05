package meta;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import orbital.algorithm.Combinatorical;

import com.hp.hpl.jena.graph.query.Bound;
import com.hp.hpl.jena.shared.NotFoundException;

import meta.simplePlanner.SimpleDomain.markings;
import multi.allenInterval.AllenIntervalConstraint;
import multi.allenInterval.AllenIntervalNetworkSolver;

import sandbox.spatial.rectangleAlgebra2.RectangleConstraint2;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraintNetwork2;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraintSolver2;
import sandbox.spatial.rectangleAlgebra2.RectangularRegion2;
import sandbox.spatial.rectangleAlgebra2.SpatialAssertionalRelation2;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraintSolver2;
import sandbox.spatial.rectangleAlgebra2.SpatialRule2;
import sandbox.spatial.rectangleAlgebra2.UnaryRectangleConstraint2;
import spatial.rectangleAlgebra.AugmentedRectangleConstraintNetwork;
import throwables.ConstraintNotFound;
import time.APSPSolver;
import time.Bounds;
import utility.logging.MetaCSPLogging;
import framework.Constraint;
import framework.ConstraintNetwork;
import framework.meta.MetaConstraint;
import framework.meta.MetaVariable;
import framework.multi.MultiBinaryConstraint;

public class MetaSpatialConstraint2 extends MetaConstraint{


	/**
	 * 
	 */
	private static final long serialVersionUID = -6726241392958991868L;
	private SpatialAssertionalRelation2[] sAssertionalRels;
	private SpatialRule2[] rules;
	//	private RectangleConstraintSolver2 solver;

	private Vector<RectangularRegion2> targetRecs = new Vector<RectangularRegion2>();

	private HashMap<Vector<RectangularRegion2>, Boolean> mark = new HashMap<Vector<RectangularRegion2>, Boolean>();
	private HashMap<HashMap<String, Bounds[]>, Integer> permutation = new HashMap<HashMap<String,Bounds[]>, Integer>();
	private Vector<String> initialUnboundedObjName = new Vector<String>();
	private Vector<String> potentialCulorit = new Vector<String>();


	public MetaSpatialConstraint2() {
		//for now!
		super(null, null);
		//		solver = new RectangleConstraintSolver2(0,1000);

	}
	
	public void setSpatialRules(SpatialRule2 ...rules){		
		this.rules = rules;
	}



	public void setSpatialAssertionalRelations(SpatialAssertionalRelation2 ...sAssertionalRels){
		this.sAssertionalRels  = sAssertionalRels;
	}

	public void testSpagetti(){

		Vector<UnaryRectangleConstraint2> atConstraints = new Vector<UnaryRectangleConstraint2>();
		RectangleConstraintSolver2 rasolver = new RectangleConstraintSolver2(0,1000);
		for (int i = 0; i < sAssertionalRels.length; i++){

			RectangularRegion2 var = (RectangularRegion2)rasolver.createVariable();
			var.setName(sAssertionalRels[i].getFrom());

			//Add at constraint of indivisuals
			if(sAssertionalRels[i].getUnaryAtRectangleConstraint() != null){
				Bounds[] atBounds = new Bounds[sAssertionalRels[i].getUnaryAtRectangleConstraint().getBounds().length];
				for (int j = 0; j < atBounds.length; j++) {
					Bounds b = new Bounds(sAssertionalRels[i].getUnaryAtRectangleConstraint().getBounds()[j].min, sAssertionalRels[i].getUnaryAtRectangleConstraint().getBounds()[j].max) ;
					atBounds[j] = b;
				}
				
				UnaryRectangleConstraint2 atCon = new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, atBounds);
				atCon.setFrom(var);
				atCon.setTo(var);
				atConstraints.add(atCon);
			}

			if(sAssertionalRels[i].getOntologicalProp() != null)
				var.setOntologicalProp(sAssertionalRels[i].getOntologicalProp());
			targetRecs.add(var);			
		}

		generateCombinantion(atConstraints);
		generateMetaValues();
		mark.put(targetRecs, true);

	}


	private void generateCombinantion(Vector<UnaryRectangleConstraint2> atConstraints){

		Vector<UnaryRectangleConstraint2> boundedUnaryCons = new Vector<UnaryRectangleConstraint2>();
		Vector<UnaryRectangleConstraint2> unboundedUnaryCons = new Vector<UnaryRectangleConstraint2>();

		HashMap<Vector<UnaryRectangleConstraint2>, Integer> rank = new HashMap<Vector<UnaryRectangleConstraint2>, Integer>();
		for (int i = 0; i < atConstraints.size(); i++) {
			Bounds[] boundsX = 
					((AllenIntervalConstraint)((UnaryRectangleConstraint2)atConstraints.get(i)).getInternalConstraints()[0]).getBounds();
			Bounds[] boundsY = 
					((AllenIntervalConstraint)((UnaryRectangleConstraint2)atConstraints.get(i)).getInternalConstraints()[1]).getBounds();
			if(!isUnboundedBoundingBox(boundsX[0], boundsX[1], boundsY[0], boundsY[1])
					&& ((RectangularRegion2)((UnaryRectangleConstraint2)atConstraints.get(i)).getFrom()).getOntologicalProp().isMovable()){
				
//				potentialCulorit.add(((RectangularRegion2)((UnaryRectangleConstraint2)atConstraints.get(i)).getFrom()).getName());
				System.out.println(((RectangularRegion2)((UnaryRectangleConstraint2)atConstraints.get(i)).getFrom()).getName());
				boundedUnaryCons.add((UnaryRectangleConstraint2)atConstraints.get(i));			
			}
			else{				
				unboundedUnaryCons.add((UnaryRectangleConstraint2)atConstraints.get(i));
			}
		}

		Combinatorical c = Combinatorical.getPermutations(boundedUnaryCons.size(), 2,  true);
		System.out.println(c.count());
		while (c.hasNext()) {
			int[] combination = c.next();
			int culpritNumber = 0;
			Vector<UnaryRectangleConstraint2> tmpboundedUnaryCons = new Vector<UnaryRectangleConstraint2>();
			Vector<UnaryRectangleConstraint2> justCulprit = new Vector<UnaryRectangleConstraint2>();
			for (int i = 0; i < combination.length; i++) {
				if(combination[i] == 1){
					UnaryRectangleConstraint2 utmp = new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF));
					utmp.setFrom(boundedUnaryCons.get(i).getFrom());
					utmp.setTo(boundedUnaryCons.get(i).getTo());
					tmpboundedUnaryCons.add(utmp);
					justCulprit.add(utmp);
					culpritNumber++;
				}

				else
					tmpboundedUnaryCons.add(boundedUnaryCons.get(i));
				//				System.out.print(combination[i]);

			}
			rank.put(tmpboundedUnaryCons, culpritNumber);

		}
		//		System.out.println(rank);

		for (Vector<UnaryRectangleConstraint2> cc : rank.keySet()) {
			HashMap<String, Bounds[]>  culprit = new HashMap<String, Bounds[]>();

			for (int i = 0; i < cc.size(); i++) {

				Bounds[] bounds = new Bounds[4];
				bounds[0] = ((AllenIntervalConstraint)((UnaryRectangleConstraint2)cc.get(i)).getInternalConstraints()[0]).getBounds()[0];
				bounds[1] = ((AllenIntervalConstraint)((UnaryRectangleConstraint2)cc.get(i)).getInternalConstraints()[0]).getBounds()[1];
				bounds[2] = ((AllenIntervalConstraint)((UnaryRectangleConstraint2)cc.get(i)).getInternalConstraints()[1]).getBounds()[0];
				bounds[3] = ((AllenIntervalConstraint)((UnaryRectangleConstraint2)cc.get(i)).getInternalConstraints()[1]).getBounds()[1];

				culprit.put(((RectangularRegion2)cc.get(i).getFrom()).getName(), bounds);
			}
			for (int i = 0; i < unboundedUnaryCons.size(); i++) {
				Bounds[] bounds = new Bounds[4];
				bounds[0] = ((AllenIntervalConstraint)((UnaryRectangleConstraint2)unboundedUnaryCons.get(i)).getInternalConstraints()[0]).getBounds()[0];
				bounds[1] = ((AllenIntervalConstraint)((UnaryRectangleConstraint2)unboundedUnaryCons.get(i)).getInternalConstraints()[0]).getBounds()[1];
				bounds[2] = ((AllenIntervalConstraint)((UnaryRectangleConstraint2)unboundedUnaryCons.get(i)).getInternalConstraints()[1]).getBounds()[0];
				bounds[3] = ((AllenIntervalConstraint)((UnaryRectangleConstraint2)unboundedUnaryCons.get(i)).getInternalConstraints()[1]).getBounds()[1];

				culprit.put(((RectangularRegion2)unboundedUnaryCons.get(i).getFrom()).getName(), bounds);
			}
			permutation.put(culprit, rank.get(cc));
		}

//		System.out.println(permutation);
	}


	private void generateMetaValues() {

		int counter = 0;
		for (HashMap<String, Bounds[]> iterCN : permutation.keySet()) {
			for (String t : iterCN.keySet()) {
				System.out.println(iterCN.get(t)[0] + " " + iterCN.get(t)[1] + " " + iterCN.get(t)[2] + iterCN.get(t)[3]);
			}
						
			RectangleConstraintSolver2 iterSolver = new RectangleConstraintSolver2(0,1000);
			
			Vector<MultiBinaryConstraint> addedGeneralKn = new Vector<MultiBinaryConstraint>();
			HashMap<String, RectangularRegion2> getVariableByName = new HashMap<String, RectangularRegion2>();

			//general knowledge
			for (int i = 0; i < this.rules.length; i++) {
				
				if(this.rules[i].getFrom().compareTo(this.rules[i].getTo()) == 0 ){
					
					
					Bounds[] sizeBounds = new Bounds[this.rules[i].getUnaryRAConstraint().getBounds().length];
					for (int j = 0; j < sizeBounds.length; j++) {						
						Bounds bSize = new Bounds(this.rules[i].getUnaryRAConstraint().getBounds()[j].min, this.rules[i].getUnaryRAConstraint().getBounds()[j].max);
						sizeBounds[j] = bSize; 	
					}

					
					
					UnaryRectangleConstraint2 uConsSize = new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.Size, sizeBounds);
					
					if(getVariableByName.get(this.rules[i].getFrom()) != null )
						uConsSize.setFrom(getVariableByName.get(this.rules[i].getFrom()));
					else{
						RectangularRegion2 var = (RectangularRegion2)iterSolver.createVariable();
						var.setName(this.rules[i].getFrom());
						uConsSize.setFrom(var);
						getVariableByName.put(this.rules[i].getFrom(), var);
					}
					if(getVariableByName.get(this.rules[i].getTo()) != null )
						uConsSize.setTo(getVariableByName.get(this.rules[i].getTo()));
					else{
						RectangularRegion2 var = (RectangularRegion2)iterSolver.createVariable();
						var.setName(this.rules[i].getTo());
						uConsSize.setTo(var);
						getVariableByName.put(this.rules[i].getTo(), var);
					}
					//				System.out.println(tmpRule[i].getRAConstraint());
					addedGeneralKn.add(uConsSize);
				}
				else{
					
					Bounds[] allenBoundsX = new Bounds[(this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[0].getBounds().length];
					for (int j = 0; j < allenBoundsX.length; j++) {
						Bounds bx = new Bounds((this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[0].getBounds()[j].min,
								(this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[0].getBounds()[j].max);
						allenBoundsX[j] = bx;						
					}
					
					Bounds[] allenBoundsY = new Bounds[(this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[1].getBounds().length];
					for (int j = 0; j < allenBoundsY.length; j++) {						
						Bounds by = new Bounds((this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[1].getBounds()[j].min, 
								(this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[1].getBounds()[j].max);
						allenBoundsY[j] = by;						
					}

					
					
					AllenIntervalConstraint xAllenCon = new AllenIntervalConstraint((this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[0].getType(), allenBoundsX);
					AllenIntervalConstraint yAllenCon = new AllenIntervalConstraint((this.rules[i].getBinaryRAConstraint()).getInternalAllenIntervalConstraints()[1].getType(), allenBoundsY);			
					
					
					RectangleConstraint2 uConsBinary = new RectangleConstraint2(xAllenCon, yAllenCon);
					
					if(getVariableByName.get(this.rules[i].getFrom()) != null )
						uConsBinary.setFrom(getVariableByName.get(this.rules[i].getFrom()));
					else{
						RectangularRegion2 var = (RectangularRegion2)iterSolver.createVariable();
						var.setName(this.rules[i].getFrom());
						uConsBinary.setFrom(var);
						getVariableByName.put(this.rules[i].getFrom(), var);
					}
					if(getVariableByName.get(this.rules[i].getTo()) != null )
						uConsBinary.setTo(getVariableByName.get(this.rules[i].getTo()));
					else{
						RectangularRegion2 var = (RectangularRegion2)iterSolver.createVariable();
						var.setName(this.rules[i].getTo());
						uConsBinary.setTo(var);
						getVariableByName.put(this.rules[i].getTo(), var);
					}
					//				System.out.println(tmpRule[i].getRAConstraint());
					addedGeneralKn.add(uConsBinary);
					
				}
				
				
			}


			if(!iterSolver.addConstraints(addedGeneralKn.toArray(new MultiBinaryConstraint[addedGeneralKn.size()])))
				System.out.println("Failed to general knowledge add");;


			//Att At cpnstraint
			Vector<RectangularRegion2> metaVaribales = new Vector<RectangularRegion2>();
			
			for (String st : iterCN.keySet()) {
				RectangularRegion2 var = (RectangularRegion2)iterSolver.createVariable();
				var.setName(st);
				
				Bounds[] atBounds = new Bounds[iterCN.get(st).length];
				for (int j = 0; j < atBounds.length; j++) {
					Bounds at = new Bounds(iterCN.get(st)[j].min, iterCN.get(st)[j].max);
					atBounds[j] = at;
				}
				
				UnaryRectangleConstraint2 atCon = new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, atBounds);
				atCon.setFrom(var);
				atCon.setTo(var);
				metaVaribales.add(var);				
				if(!iterSolver.addConstraint(atCon))
					System.out.println("Failed to add AT constraint");
			}


			Vector<RectangleConstraint2> assertionList = new Vector<RectangleConstraint2>();
			for (int i = 0; i < sAssertionalRels.length; i++) {
				for (int j = 0; j < metaVaribales.size(); j++) {
					if(sAssertionalRels[i].getFrom().compareTo(((RectangularRegion2)(metaVaribales.get(j))).getName()) == 0){
						RectangleConstraint2 assertion = new RectangleConstraint2(
								new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals, AllenIntervalConstraint.Type.Equals.getDefaultBounds()), 
								new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals, AllenIntervalConstraint.Type.Equals.getDefaultBounds()));

						assertion.setFrom(((RectangularRegion2)metaVaribales.get(j)));
						assertion.setTo(getVariableByName.get(sAssertionalRels[i].getTo()));
//						System.out.println(assertion);
						assertionList.add(assertion);
					}
				}
			}
			
			System.out.println("");
			
//			try {
				MetaCSPLogging.setLevel(Level.FINE);
				if(!iterSolver.addConstraints(assertionList.toArray(new RectangleConstraint2[assertionList.size()])))
					System.out.println("Failed to add Assertinal Constraint");;
	
//			} catch (ConstraintNotFound e) {
//											
//			}
			
//				if (counter++ == permutation.keySet().size()-2) {
//					ConstraintNetwork.draw(iterSolver.getConstraintSolvers()[0].getConstraintNetwork());
//					ConstraintNetwork.draw(iterSolver.getConstraintSolvers()[1].getConstraintNetwork());
//					
//					
//					try {
//						Thread.sleep(100000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//				}
			double avg = ((double)(((AllenIntervalNetworkSolver)iterSolver.getConstraintSolvers()[0]).getRigidityNumber()) 
					+ (double)(((AllenIntervalNetworkSolver)iterSolver.getConstraintSolvers()[1]).getRigidityNumber())) / 2;



			System.out.println("rank: " + permutation.get(iterCN));
			System.out.println("avg: " + avg);
			System.out.println(iterSolver.extractBoundingBoxesFromSTPs("cup1").getAlmostCentreRectangle());
			
//			for (int i = 0; i < iterSolver.getConstraints().length; i++) {
//				System.out.println(iterSolver.getConstraints()[i]);
//			}
			System.out.println("_______________________________________________________________________________");

		}		
	}




	private boolean isUnboundedBoundingBox(Bounds xLB, Bounds xUB,
			Bounds yLB, Bounds yUB) {

		if(xLB.min != 0 && xLB.max != APSPSolver.INF)
			return false;
		if(xUB.min != 0 && xUB.max != APSPSolver.INF)
			return false;
		if(yLB.min != 0 && yLB.max != APSPSolver.INF)
			return false;
		if(yLB.min != 0 && yUB.max != APSPSolver.INF)
			return false;
		return true;
	}

	@Override
	public ConstraintNetwork[] getMetaVariables() {

		Vector<RectangleConstraintNetwork2> ret = new Vector<RectangleConstraintNetwork2>();
		RectangleConstraintNetwork2 raNetwork = new RectangleConstraintNetwork2(null);
		if(mark.get(targetRecs)){
			for (int i = 0; i < targetRecs.size(); i++) {				
				raNetwork.addVariable(targetRecs.get(i));			
			}
			ret.add(raNetwork);
			return ret.toArray(new ConstraintNetwork[ret.size()]);
		}
		else
			return null;
	}

	//meta value essentially is the position of meta values
	@Override
	public ConstraintNetwork[] getMetaValues(MetaVariable metaVariable) {

		////		if(metaVariable == null)
		////			return null;
		////		ConstraintNetwork conflict = metaVariable.getConstraintNetwork();
		////		Vector<RectangleConstraint2> assertionList = new Vector<RectangleConstraint2>();
		////		for (int i = 0; i < sAssertionalRels.length; i++) {
		////			for (int j = 0; j < conflict.getVariables().length; j++) {
		////				if(sAssertionalRels[i].getFrom().compareTo(((RectangularRegion2)(conflict.getVariables()[j])).getName()) == 0){
		////					RectangleConstraint2 assertion = new RectangleConstraint2(
		////							new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals), 
		////							new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals));
		////
		////					assertion.setFrom(((RectangularRegion2)conflict.getVariables()[j]));
		////					assertion.setTo(getVariableByName.get(sAssertionalRels[i].getTo()));
		////					assertionList.add(assertion);
		////					break;
		////				}
		////			}
		////		}
		////		
		////		
		////		
		////		//		MetaCSPLogging.setLevel(Level.FINEST);
		////		RectangleConstraintNetwork2 unification = new RectangleConstraintNetwork2(null);
		////		
		//////		//this for loop add (equal, equal) constraint for assertional relation 
		//////		for (int i = 0; i < assertionList.size(); i++) {
		//////			unification.addConstraint(assertionList.get(i));
		//////		}
		//////		//this for loop add gereral knowledge
		//////		for (int i = 0; i < addedGeneralKn.size(); i++) {
		//////			unification.addConstraint(addedGeneralKn.get(i));
		////		}
		//		
		//		
		//		ConstraintNetwork[] ret = new ConstraintNetwork[]{unification}; 		
		//		return ret;
		return null;
	}


	@Override
	public ConstraintNetwork[] getMetaValues(MetaVariable metaVariable,
			int initial_time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void markResolvedSub(MetaVariable metaVariable,
			ConstraintNetwork metaValue) {
		mark.put(targetRecs, false);


	}

	@Override
	public void draw(ConstraintNetwork network) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEdgeLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEquivalent(Constraint c) {
		// TODO Auto-generated method stub
		return false;
	}

	//	public Rectangle getRectangle(String st){
	//		return solver.extractBoundingBoxesFromSTPs(st).getAlmostCentreRectangle();
	//	}




}
