package meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import multi.allenInterval.AllenIntervalConstraint;
import multi.allenInterval.AllenIntervalNetworkSolver;
import orbital.algorithm.Combinatorical;

import sandbox.spatial.rectangleAlgebra2.RectangleConstraint2;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraintNetwork2;
import sandbox.spatial.rectangleAlgebra2.RectangleConstraintSolver2;
import sandbox.spatial.rectangleAlgebra2.RectangularRegion2;
import sandbox.spatial.rectangleAlgebra2.SpatialAssertionalRelation2;
import sandbox.spatial.rectangleAlgebra2.SpatialFluent;
import sandbox.spatial.rectangleAlgebra2.SpatialFluentSolver;
import sandbox.spatial.rectangleAlgebra2.SpatialRule2;
import sandbox.spatial.rectangleAlgebra2.UnaryRectangleConstraint2;
import time.APSPSolver;
import time.Bounds;
import utility.logging.MetaCSPLogging;
import framework.Constraint;
import framework.ConstraintNetwork;
import framework.ConstraintSolver;
import framework.meta.MetaConstraint;
import framework.meta.MetaConstraintSolver;
import framework.meta.MetaVariable;
import framework.multi.MultiBinaryConstraint;

public class MetaSpatialFluentConstraint extends MetaConstraint {
	


	private SpatialAssertionalRelation2[] sAssertionalRels;
	private SpatialRule2[] rules;
	//	private RectangleConstraintSolver2 solver;

	private long origin = 0, horizon = 1000;

	private boolean markMetaVar = false;
	private HashMap<HashMap<String, Bounds[]>, Integer> permutation = new HashMap<HashMap<String,Bounds[]>, Integer>();
	private Vector<String> initialUnboundedObjName = new Vector<String>();
	private Vector<String> potentialCulprit = new Vector<String>();
	public Logger logger = MetaCSPLogging.getLogger(this.getClass());
	

	public MetaSpatialFluentConstraint() {
		//for now!
		super(null, null);
		//		solver = new RectangleConstraintSolver2(0,1000);

	}

	public void setSpatialRules(SpatialRule2 ...rules){
		this.rules = new SpatialRule2[rules.length];
		this.rules = rules;
	}

	public void setSpatialAssertionalRelations(SpatialAssertionalRelation2 ...sAssertionalRels){
		this.sAssertionalRels = new SpatialAssertionalRelation2[sAssertionalRels.length];
		this.sAssertionalRels  = sAssertionalRels;
	}
	
	public Vector<String> getPotentialCulprit(){
		return potentialCulprit;
	}
	public Vector<String> getInitialUnboundedObject(){
		return initialUnboundedObjName;
	}
	
	public Vector<HashMap<String, Bounds[]>> generateAllAlternativeSet(Vector<RectangularRegion2> targetRecs){

		class ConstraintNetworkSortingCritera{

			public double rigidityNumber = 0;
			public int culpritLevel = 0;

			ConstraintNetworkSortingCritera(double rigidityNumber, int culpritLevel){
				this.culpritLevel = culpritLevel;
				this.rigidityNumber = rigidityNumber;
			}
		}

		final HashMap<ConstraintNetwork, ConstraintNetworkSortingCritera> sortingCN = new HashMap<ConstraintNetwork, ConstraintNetworkSortingCritera>();
		HashMap< ConstraintNetwork,HashMap<String, Bounds[]>> cnToInitPose = new HashMap<ConstraintNetwork, HashMap<String,Bounds[]>>();
		for (HashMap<String, Bounds[]> iterCN : permutation.keySet()) {
//			for (String t : iterCN.keySet()) {
//				System.out.println(iterCN.get(t)[0] + " " + iterCN.get(t)[1] + " " + iterCN.get(t)[2] + iterCN.get(t)[3]);
//			}

			RectangleConstraintSolver2 iterSolver = new RectangleConstraintSolver2(origin,horizon);
			HashMap<String, RectangularRegion2> getVariableByName = new HashMap<String, RectangularRegion2>();
			
			
			Vector<MultiBinaryConstraint> addedGeneralKn = new Vector<MultiBinaryConstraint>();
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
					addedGeneralKn.add(uConsBinary);
				}
			}

			
			if(!iterSolver.addConstraints(addedGeneralKn.toArray(new MultiBinaryConstraint[addedGeneralKn.size()])))
				System.out.println("Failed to general knowledge add");

			//Att At cpnstraint
			Vector<RectangularRegion2> metaVaribales = new Vector<RectangularRegion2>();

			for (RectangularRegion2 Metavar : targetRecs) {
				RectangularRegion2 var = (RectangularRegion2)iterSolver.createVariable();
				var.setName(Metavar.getName());

				Bounds[] atBounds = new Bounds[iterCN.get(Metavar.getName()).length];
				for (int j = 0; j < atBounds.length; j++) {
					Bounds at = new Bounds(iterCN.get(Metavar.getName())[j].min, iterCN.get(Metavar.getName())[j].max);
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


			boolean isConsistent = true;

			//				MetaCSPLogging.setLevel(Level.FINE);
			if(!iterSolver.addConstraints(assertionList.toArray(new RectangleConstraint2[assertionList.size()]))){
				isConsistent = false;
				logger.fine("Failed to add Assertinal Constraint in first generation of all culprit..alternatives generate later...");
			}



			double rigidityavg = ((double)(((AllenIntervalNetworkSolver)iterSolver.getConstraintSolvers()[0]).getRigidityNumber()) 
					+ (double)(((AllenIntervalNetworkSolver)iterSolver.getConstraintSolvers()[1]).getRigidityNumber())) / 2;

			if(isConsistent){
				sortingCN.put(iterSolver.getConstraintNetwork(), new ConstraintNetworkSortingCritera(rigidityavg, permutation.get(iterCN)));
				cnToInitPose.put(iterSolver.getConstraintNetwork(), iterCN);
			}

//			System.out.println(iterSolver.extractBoundingBoxesFromSTPs("cup1").getAlmostCentreRectangle());
//
//			System.out.println("_______________________________________________________________________________");

		}		

		ArrayList as = new ArrayList( sortingCN.keySet() );          
		Collections.sort( as , new Comparator() {  
			public int compare( Object o1 , Object o2 )  
			{  
				RectangleConstraintNetwork2 l1 = (RectangleConstraintNetwork2)o1 ;  
				RectangleConstraintNetwork2 l2 = (RectangleConstraintNetwork2)o2 ;  
				Integer first = (Integer)sortingCN.get(l1).culpritLevel;  
				Integer second = (Integer)sortingCN.get(l2).culpritLevel;
				int i = first.compareTo(second);				
				if(i != 0 ) return i;

				RectangleConstraintNetwork2 r1 = (RectangleConstraintNetwork2)o1 ;  
				RectangleConstraintNetwork2 r2 = (RectangleConstraintNetwork2)o2 ;  
				Double firstRig = (Double)sortingCN.get(r1).rigidityNumber;  
				Double secondRig = (Double)sortingCN.get(r2).rigidityNumber;

				i = firstRig.compareTo(secondRig);
				if (i != 0) return i;
				return -1;
			}  
		}); 
		Vector<HashMap<String, Bounds[]>> alternativeSets = new Vector<HashMap<String,Bounds[]>>();
		Iterator i = as.iterator();  
		while ( i.hasNext() )  
		{  
			ConstraintNetwork ct = new RectangleConstraintNetwork2(null); 
			ct = (RectangleConstraintNetwork2)i.next();
//			System.out.println("======================================================================");
//			System.out.println(ct);
//			System.out.println(cnToInitPose.get(ct));
//			for (String t : cnToInitPose.get(ct).keySet()) {
//				System.out.println(cnToInitPose.get(ct).get(t)[0] + " " + cnToInitPose.get(ct).get(t)[1] + " " + cnToInitPose.get(ct).get(t)[2] + cnToInitPose.get(ct).get(t)[3]);
//			}
//			System.out.println(sortingCN.get(ct).rigidityNumber);
//			System.out.println(sortingCN.get(ct).culpritLevel);
//			System.out.println("======================================================================");
			alternativeSets.add(cnToInitPose.get(ct));

		} 		
		return alternativeSets;

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

				potentialCulprit.add(((RectangularRegion2)((UnaryRectangleConstraint2)atConstraints.get(i)).getFrom()).getName());
				logger.fine("one potential culprit can be: " + ((RectangularRegion2)((UnaryRectangleConstraint2)atConstraints.get(i)).getFrom()).getName());
				boundedUnaryCons.add((UnaryRectangleConstraint2)atConstraints.get(i));			
			}
			else{			
				if(((RectangularRegion2)((UnaryRectangleConstraint2)atConstraints.get(i)).getFrom()).getOntologicalProp().isMovable())
					initialUnboundedObjName.add(((RectangularRegion2)((UnaryRectangleConstraint2)atConstraints.get(i)).getFrom()).getName());
				unboundedUnaryCons.add((UnaryRectangleConstraint2)atConstraints.get(i));
			}
		}
		

		Combinatorical c = Combinatorical.getPermutations(boundedUnaryCons.size(), 2,  true);
		//		System.out.println(c.count());
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

	public boolean isUnboundedBoundingBox(Bounds xLB, Bounds xUB,
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

		if(markMetaVar)
			return null;
		Vector<SpatialFluent> targetRecs = new Vector<SpatialFluent>();
		Vector<UnaryRectangleConstraint2> atConstraints = new Vector<UnaryRectangleConstraint2>();
		HashMap<String,SpatialFluent> currentFluent = new HashMap<String,SpatialFluent>();
		
		for (int j = 0; j < ((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables().length; j++) 
			currentFluent.put(((SpatialFluent)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables()[j]).getName(), (SpatialFluent)((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getVariables()[j]);		
				
		for (int i = 0; i < sAssertionalRels.length; i++){
			
			SpatialFluent sf = currentFluent.get(sAssertionalRels[i].getFrom());
//			SpatialFluent sf = sAssertionalRels[i].getSpatialFleunt();

			//Add at constraint of indivisuals
			if(sAssertionalRels[i].getUnaryAtRectangleConstraint() != null){
				Bounds[] atBounds = new Bounds[sAssertionalRels[i].getUnaryAtRectangleConstraint().getBounds().length];
				for (int j = 0; j < atBounds.length; j++) {
					Bounds b = new Bounds(sAssertionalRels[i].getUnaryAtRectangleConstraint().getBounds()[j].min,
							sAssertionalRels[i].getUnaryAtRectangleConstraint().getBounds()[j].max) ;
					atBounds[j] = b;
				}

				UnaryRectangleConstraint2 atCon = new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, atBounds);
				atCon.setFrom(sf.getRectangularRegion());
				atCon.setTo(sf.getRectangularRegion());


				atConstraints.add(atCon);
			}

			if(sAssertionalRels[i].getOntologicalProp() != null)
				sf.getRectangularRegion().setOntologicalProp(sAssertionalRels[i].getOntologicalProp());
			targetRecs.add(sf);			
		}

		generateCombinantion(atConstraints);

		RectangleConstraintNetwork2 raNetwork = new RectangleConstraintNetwork2(null);
		for (int i = 0; i < targetRecs.size(); i++) {
			raNetwork.addVariable(targetRecs.get(i));
		}

		return new ConstraintNetwork[]{raNetwork};
		//		return ret.toArray(new ConstraintNetwork[ret.size()]);


	}

	//meta value essentially is the position of meta values
	@Override
	public ConstraintNetwork[] getMetaValues(MetaVariable metaVariable) {

		if(metaVariable == null)
			return null;
		ConstraintNetwork conflict = metaVariable.getConstraintNetwork();
		Vector<SpatialFluent> conflictvars = new Vector<SpatialFluent>();
		Vector<RectangularRegion2> conflictRecvars = new Vector<RectangularRegion2>();
		HashMap<String, RectangularRegion2> getVariableByName = new HashMap<String, RectangularRegion2>();
		Vector<ConstraintNetwork> ret = new Vector<ConstraintNetwork>();

		for (int j = 0; j < conflict.getVariables().length; j++){ 
			conflictvars.add((SpatialFluent)conflict.getVariables()[j]);
			conflictRecvars.add(((SpatialFluent)conflict.getVariables()[j]).getRectangularRegion());
		}
		
		Vector<HashMap<String, Bounds[]>> alternativeSets = generateAllAlternativeSet(conflictRecvars);
		for (int k = 0; k < alternativeSets.size(); k++) {
			
			RectangleConstraintNetwork2 mvalue = new RectangleConstraintNetwork2(this.metaCS.getConstraintSolvers()[0]);
			HashMap<String, Bounds[]> alternativeSet = alternativeSets.get(k);

			mvalue.join(createTBOXspatialNetwork(((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[0], getVariableByName)); //TBOX general knowledge in RectangleCN
			

			//Att At cpnstraint
			Vector<RectangularRegion2> metaVaribales = new Vector<RectangularRegion2>();

			for (SpatialFluent var : conflictvars) {
				Bounds[] atBounds = new Bounds[alternativeSet.get(var.getRectangularRegion().getName()).length];
				for (int j = 0; j < atBounds.length; j++) {
					Bounds at = new Bounds(alternativeSet.get(var.getName())[j].min, alternativeSet.get(var.getName())[j].max);
					atBounds[j] = at;
				}
				UnaryRectangleConstraint2 atCon = new UnaryRectangleConstraint2(UnaryRectangleConstraint2.Type.At, atBounds);
				atCon.setFrom(var.getRectangularRegion());
				atCon.setTo(var.getRectangularRegion());
				
				
				metaVaribales.add(var.getRectangularRegion());
				mvalue.addConstraint(atCon);
				mvalue.addVariable(var.getRectangularRegion());
				
//				if(!this.metaCS.getConstraintSolvers()[0].addConstraint(atCon))
//					System.out.println("Failed to add AT constraint");			
			}
			
			

			Vector<RectangleConstraint2> assertionList = new Vector<RectangleConstraint2>();
			for (int i = 0; i < sAssertionalRels.length; i++) {
				for (int j = 0; j < metaVaribales.size(); j++) {
					if(sAssertionalRels[i].getFrom().compareTo(((metaVaribales.get(j))).getName()) == 0){
						RectangleConstraint2 assertion = new RectangleConstraint2(
								new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals, AllenIntervalConstraint.Type.Equals.getDefaultBounds()), 
								new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals, AllenIntervalConstraint.Type.Equals.getDefaultBounds()));

						assertion.setFrom(((RectangularRegion2)metaVaribales.get(j)));
						assertion.setTo(getVariableByName.get(sAssertionalRels[i].getTo()));
						//						System.out.println(assertion);
						assertionList.add(assertion);
						mvalue.addConstraint(assertion);
					}
				}
			}
			
			
			logger.finest("pregenerated meta value for scoring: " + mvalue);
			ret.add(mvalue);

//			if(!this.metaCS.getConstraintSolvers()[0].addConstraints(assertionList.toArray(new RectangleConstraint2[assertionList.size()])))
//				System.out.println("Failed to add Assertinal Constraint");

		}		
		return  ret.toArray(new ConstraintNetwork[ret.size()]);
	}


	private RectangleConstraintNetwork2 createTBOXspatialNetwork(ConstraintSolver solver, HashMap<String, RectangularRegion2> getVariableByName) {

		//general knowledge
		RectangleConstraintNetwork2 ret = new RectangleConstraintNetwork2(solver);
//		Vector<MultiBinaryConstraint> addedGeneralKn = new Vector<MultiBinaryConstraint>();
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
					RectangularRegion2 var = (RectangularRegion2)solver.createVariable();
					var.setName(this.rules[i].getFrom());
					uConsSize.setFrom(var);
					getVariableByName.put(this.rules[i].getFrom(), var);
				}
				if(getVariableByName.get(this.rules[i].getTo()) != null )
					uConsSize.setTo(getVariableByName.get(this.rules[i].getTo()));
				else{
					RectangularRegion2 var = (RectangularRegion2)solver.createVariable();
					var.setName(this.rules[i].getTo());
					uConsSize.setTo(var);
					getVariableByName.put(this.rules[i].getTo(), var);
				}
				//				System.out.println(tmpRule[i].getRAConstraint());
				ret.addConstraint(uConsSize);
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
					RectangularRegion2 var = (RectangularRegion2)solver.createVariable();
					var.setName(this.rules[i].getFrom());
					uConsBinary.setFrom(var);
					getVariableByName.put(this.rules[i].getFrom(), var);
				}
				if(getVariableByName.get(this.rules[i].getTo()) != null )
					uConsBinary.setTo(getVariableByName.get(this.rules[i].getTo()));
				else{
					RectangularRegion2 var = (RectangularRegion2)solver.createVariable();
					var.setName(this.rules[i].getTo());
					uConsBinary.setTo(var);
					getVariableByName.put(this.rules[i].getTo(), var);
				}
				ret.addConstraint(uConsBinary);
			}
		}

		return ret;
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
		markMetaVar = true;


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
		
//	public RectangleConstraintSolver2 getGroundSolver(){
//		return this.metaCS.getConstraintSolvers()[0];
//	}
	
	
	//	public Rectangle getRectangle(String st){
	//		return solver.extractBoundingBoxesFromSTPs(st).getAlmostCentreRectangle();
	//	}

	




}
