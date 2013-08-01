package meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.multi.allenInterval.AllenIntervalNetworkSolver;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraint;

import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraintSolver;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangularRegion;
import org.metacsp.multi.spatial.rectangleAlgebra.UnaryRectangleConstraint;
import org.metacsp.multi.spatioTemporal.SpatialFluent;
import org.metacsp.multi.spatioTemporal.SpatialFluentSolver;
import orbital.algorithm.Combinatorical;

import spatial.utility.SpatialAssertionalRelation2;
import spatial.utility.SpatialRule2;
import org.metacsp.time.APSPSolver;
import org.metacsp.time.Bounds;
import org.metacsp.utility.logging.MetaCSPLogging;
import org.metacsp.framework.Constraint;
import org.metacsp.framework.ConstraintNetwork;
import org.metacsp.framework.ConstraintSolver;
import org.metacsp.framework.meta.MetaConstraint;
import org.metacsp.framework.meta.MetaVariable;
import org.metacsp.framework.multi.MultiBinaryConstraint;

public class MetaSpatialFluentConstraint extends MetaConstraint {
	


	private SpatialAssertionalRelation2[] sAssertionalRels;
	private SpatialRule2[] rules;
	//	private RectangleConstraintSolver solver;

	private long origin = 0, horizon = 1000;

	private boolean markMetaVar = false;
	private HashMap<HashMap<String, Bounds[]>, Integer> permutation = new HashMap<HashMap<String,Bounds[]>, Integer>();
	private Vector<String> initialUnboundedObjName = new Vector<String>();
	private Vector<String> potentialCulprit = new Vector<String>();
	public Logger logger = MetaCSPLogging.getLogger(this.getClass());
	

	public MetaSpatialFluentConstraint() {
		//for now!
		super(null, null);
		//		solver = new RectangleConstraintSolver(0,1000);

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
	
	public Vector<HashMap<String, Bounds[]>> generateAllAlternativeSet(Vector<RectangularRegion> targetRecs){

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
			RectangleConstraintSolver iterSolver = new RectangleConstraintSolver(origin,horizon);
			HashMap<String, RectangularRegion> getVariableByName = new HashMap<String, RectangularRegion>();
			
			
			Vector<MultiBinaryConstraint> addedGeneralKn = new Vector<MultiBinaryConstraint>();
			for (int i = 0; i < this.rules.length; i++) {

				if(this.rules[i].getFrom().compareTo(this.rules[i].getTo()) == 0 ){
					Bounds[] sizeBounds = new Bounds[this.rules[i].getUnaryRAConstraint().getBounds().length];
					for (int j = 0; j < sizeBounds.length; j++) {						
						Bounds bSize = new Bounds(this.rules[i].getUnaryRAConstraint().getBounds()[j].min, this.rules[i].getUnaryRAConstraint().getBounds()[j].max);
						sizeBounds[j] = bSize; 	
					}
					UnaryRectangleConstraint uConsSize = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, sizeBounds);

					if(getVariableByName.get(this.rules[i].getFrom()) != null )
						uConsSize.setFrom(getVariableByName.get(this.rules[i].getFrom()));
					else{
						RectangularRegion var = (RectangularRegion)iterSolver.createVariable();
						var.setName(this.rules[i].getFrom());
						uConsSize.setFrom(var);
						getVariableByName.put(this.rules[i].getFrom(), var);
					}
					if(getVariableByName.get(this.rules[i].getTo()) != null )
						uConsSize.setTo(getVariableByName.get(this.rules[i].getTo()));
					else{
						RectangularRegion var = (RectangularRegion)iterSolver.createVariable();
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


					RectangleConstraint uConsBinary = new RectangleConstraint(xAllenCon, yAllenCon);

					if(getVariableByName.get(this.rules[i].getFrom()) != null )
						uConsBinary.setFrom(getVariableByName.get(this.rules[i].getFrom()));
					else{
						RectangularRegion var = (RectangularRegion)iterSolver.createVariable();
						var.setName(this.rules[i].getFrom());
						uConsBinary.setFrom(var);
						getVariableByName.put(this.rules[i].getFrom(), var);
					}
					if(getVariableByName.get(this.rules[i].getTo()) != null )
						uConsBinary.setTo(getVariableByName.get(this.rules[i].getTo()));
					else{
						RectangularRegion var = (RectangularRegion)iterSolver.createVariable();
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
			Vector<RectangularRegion> metaVaribales = new Vector<RectangularRegion>();

			for (RectangularRegion Metavar : targetRecs) {
				RectangularRegion var = (RectangularRegion)iterSolver.createVariable();
				var.setName(Metavar.getName());

				Bounds[] atBounds = new Bounds[iterCN.get(Metavar.getName()).length];
				for (int j = 0; j < atBounds.length; j++) {
					Bounds at = new Bounds(iterCN.get(Metavar.getName())[j].min, iterCN.get(Metavar.getName())[j].max);
					atBounds[j] = at;
				}

				UnaryRectangleConstraint atCon = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, atBounds);
				atCon.setFrom(var);
				atCon.setTo(var);
				metaVaribales.add(var);				
				if(!iterSolver.addConstraint(atCon))
					System.out.println("Failed to add AT constraint");			
			}

			Vector<RectangleConstraint> assertionList = new Vector<RectangleConstraint>();
			for (int i = 0; i < sAssertionalRels.length; i++) {
				for (int j = 0; j < metaVaribales.size(); j++) {
					if(sAssertionalRels[i].getFrom().compareTo(((RectangularRegion)(metaVaribales.get(j))).getName()) == 0){
						RectangleConstraint assertion = new RectangleConstraint(
								new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals, AllenIntervalConstraint.Type.Equals.getDefaultBounds()), 
								new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals, AllenIntervalConstraint.Type.Equals.getDefaultBounds()));

						assertion.setFrom(((RectangularRegion)metaVaribales.get(j)));
						assertion.setTo(getVariableByName.get(sAssertionalRels[i].getTo()));
						//						System.out.println(assertion);
						assertionList.add(assertion);
					}
				}
			}


			boolean isConsistent = true;

			//				MetaCSPLogging.setLevel(Level.FINE);
			if(!iterSolver.addConstraints(assertionList.toArray(new RectangleConstraint[assertionList.size()]))){
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
				ConstraintNetwork l1 = (ConstraintNetwork)o1 ;  
				ConstraintNetwork l2 = (ConstraintNetwork)o2 ;  
				Integer first = (Integer)sortingCN.get(l1).culpritLevel;  
				Integer second = (Integer)sortingCN.get(l2).culpritLevel;
				int i = first.compareTo(second);				
				if(i != 0 ) return i;

				ConstraintNetwork r1 = (ConstraintNetwork)o1 ;  
				ConstraintNetwork r2 = (ConstraintNetwork)o2 ;  
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
			ConstraintNetwork ct = new ConstraintNetwork(null); 
			ct = (ConstraintNetwork)i.next();
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

	private void generateCombinantion(Vector<UnaryRectangleConstraint> atConstraints){

		Vector<UnaryRectangleConstraint> boundedUnaryCons = new Vector<UnaryRectangleConstraint>();
		Vector<UnaryRectangleConstraint> unboundedUnaryCons = new Vector<UnaryRectangleConstraint>();

		HashMap<Vector<UnaryRectangleConstraint>, Integer> rank = new HashMap<Vector<UnaryRectangleConstraint>, Integer>();
		for (int i = 0; i < atConstraints.size(); i++) {
			Bounds[] boundsX = 
					((AllenIntervalConstraint)((UnaryRectangleConstraint)atConstraints.get(i)).getInternalConstraints()[0]).getBounds();
			Bounds[] boundsY = 
					((AllenIntervalConstraint)((UnaryRectangleConstraint)atConstraints.get(i)).getInternalConstraints()[1]).getBounds();
			if(!isUnboundedBoundingBox(boundsX[0], boundsX[1], boundsY[0], boundsY[1])
					&& ((RectangularRegion)((UnaryRectangleConstraint)atConstraints.get(i)).getFrom()).getOntologicalProp().isMovable()){

				potentialCulprit.add(((RectangularRegion)((UnaryRectangleConstraint)atConstraints.get(i)).getFrom()).getName());
				logger.fine("one potential culprit can be: " + ((RectangularRegion)((UnaryRectangleConstraint)atConstraints.get(i)).getFrom()).getName());
				boundedUnaryCons.add((UnaryRectangleConstraint)atConstraints.get(i));			
			}
			else{			
				if(((RectangularRegion)((UnaryRectangleConstraint)atConstraints.get(i)).getFrom()).getOntologicalProp().isMovable())
					initialUnboundedObjName.add(((RectangularRegion)((UnaryRectangleConstraint)atConstraints.get(i)).getFrom()).getName());
				unboundedUnaryCons.add((UnaryRectangleConstraint)atConstraints.get(i));
			}
		}
		Combinatorical c = Combinatorical.getPermutations(boundedUnaryCons.size(), 2,  true);
//		System.out.println("boundedUnaryCons" + boundedUnaryCons);
//		System.out.println("c.count: " + c.count());

		while (c.hasNext()) {
			int[] combination = c.next();
			int culpritNumber = 0;
			Vector<UnaryRectangleConstraint> tmpboundedUnaryCons = new Vector<UnaryRectangleConstraint>();
			Vector<UnaryRectangleConstraint> justCulprit = new Vector<UnaryRectangleConstraint>();
			for (int i = 0; i < combination.length; i++) {
				if(combination[i] == 1){
					UnaryRectangleConstraint utmp = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF));
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

		for (Vector<UnaryRectangleConstraint> cc : rank.keySet()) {
			HashMap<String, Bounds[]>  culprit = new HashMap<String, Bounds[]>();

			for (int i = 0; i < cc.size(); i++) {

				Bounds[] bounds = new Bounds[4];
				bounds[0] = ((AllenIntervalConstraint)((UnaryRectangleConstraint)cc.get(i)).getInternalConstraints()[0]).getBounds()[0];
				bounds[1] = ((AllenIntervalConstraint)((UnaryRectangleConstraint)cc.get(i)).getInternalConstraints()[0]).getBounds()[1];
				bounds[2] = ((AllenIntervalConstraint)((UnaryRectangleConstraint)cc.get(i)).getInternalConstraints()[1]).getBounds()[0];
				bounds[3] = ((AllenIntervalConstraint)((UnaryRectangleConstraint)cc.get(i)).getInternalConstraints()[1]).getBounds()[1];

				culprit.put(((RectangularRegion)cc.get(i).getFrom()).getName(), bounds);
			}
			for (int i = 0; i < unboundedUnaryCons.size(); i++) {
				Bounds[] bounds = new Bounds[4];
				bounds[0] = ((AllenIntervalConstraint)((UnaryRectangleConstraint)unboundedUnaryCons.get(i)).getInternalConstraints()[0]).getBounds()[0];
				bounds[1] = ((AllenIntervalConstraint)((UnaryRectangleConstraint)unboundedUnaryCons.get(i)).getInternalConstraints()[0]).getBounds()[1];
				bounds[2] = ((AllenIntervalConstraint)((UnaryRectangleConstraint)unboundedUnaryCons.get(i)).getInternalConstraints()[1]).getBounds()[0];
				bounds[3] = ((AllenIntervalConstraint)((UnaryRectangleConstraint)unboundedUnaryCons.get(i)).getInternalConstraints()[1]).getBounds()[1];

				culprit.put(((RectangularRegion)unboundedUnaryCons.get(i).getFrom()).getName(), bounds);
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
		Vector<UnaryRectangleConstraint> atConstraints = new Vector<UnaryRectangleConstraint>();
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

				UnaryRectangleConstraint atCon = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, atBounds);
				atCon.setFrom(sf.getRectangularRegion());
				atCon.setTo(sf.getRectangularRegion());


				atConstraints.add(atCon);
			}

			if(sAssertionalRels[i].getOntologicalProp() != null)
				sf.getRectangularRegion().setOntologicalProp(sAssertionalRels[i].getOntologicalProp());
			targetRecs.add(sf);			
		}

		generateCombinantion(atConstraints);

		ConstraintNetwork raNetwork = new ConstraintNetwork(null);
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
		Vector<RectangularRegion> conflictRecvars = new Vector<RectangularRegion>();
		HashMap<String, RectangularRegion> getVariableByName = new HashMap<String, RectangularRegion>();
		Vector<ConstraintNetwork> ret = new Vector<ConstraintNetwork>();

		for (int j = 0; j < conflict.getVariables().length; j++){ 
			conflictvars.add((SpatialFluent)conflict.getVariables()[j]);
			conflictRecvars.add(((SpatialFluent)conflict.getVariables()[j]).getRectangularRegion());
		}
		
		Vector<HashMap<String, Bounds[]>> alternativeSets = generateAllAlternativeSet(conflictRecvars);
		for (int k = 0; k < alternativeSets.size(); k++) {
			
			ConstraintNetwork mvalue = new ConstraintNetwork(this.metaCS.getConstraintSolvers()[0]);
			HashMap<String, Bounds[]> alternativeSet = alternativeSets.get(k);

			mvalue.join(createTBOXspatialNetwork(((SpatialFluentSolver)this.metaCS.getConstraintSolvers()[0]).getConstraintSolvers()[0], getVariableByName)); //TBOX general knowledge in RectangleCN
			

			//Att At cpnstraint
			Vector<RectangularRegion> metaVaribales = new Vector<RectangularRegion>();

			for (SpatialFluent var : conflictvars) {
				Bounds[] atBounds = new Bounds[alternativeSet.get(var.getRectangularRegion().getName()).length];
				for (int j = 0; j < atBounds.length; j++) {
					Bounds at = new Bounds(alternativeSet.get(var.getName())[j].min, alternativeSet.get(var.getName())[j].max);
					atBounds[j] = at;
				}
				UnaryRectangleConstraint atCon = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, atBounds);
				atCon.setFrom(var.getRectangularRegion());
				atCon.setTo(var.getRectangularRegion());
				
				
				metaVaribales.add(var.getRectangularRegion());
				mvalue.addConstraint(atCon);
				mvalue.addVariable(var.getRectangularRegion());
				
//				if(!this.metaCS.getConstraintSolvers()[0].addConstraint(atCon))
//					System.out.println("Failed to add AT constraint");			
			}
			
			

			Vector<RectangleConstraint> assertionList = new Vector<RectangleConstraint>();
			for (int i = 0; i < sAssertionalRels.length; i++) {
				for (int j = 0; j < metaVaribales.size(); j++) {
					if(sAssertionalRels[i].getFrom().compareTo(((metaVaribales.get(j))).getName()) == 0){
						RectangleConstraint assertion = new RectangleConstraint(
								new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals, AllenIntervalConstraint.Type.Equals.getDefaultBounds()), 
								new AllenIntervalConstraint(AllenIntervalConstraint.Type.Equals, AllenIntervalConstraint.Type.Equals.getDefaultBounds()));

						assertion.setFrom(((RectangularRegion)metaVaribales.get(j)));
						assertion.setTo(getVariableByName.get(sAssertionalRels[i].getTo()));
						//						System.out.println(assertion);
						assertionList.add(assertion);
						mvalue.addConstraint(assertion);
					}
				}
			}
			
			
			logger.finest("pregenerated meta value for scoring: " + mvalue);
			ret.add(mvalue);

			
//			if(!this.metaCS.getConstraintSolvers()[0].addConstraints(assertionList.toArray(new RectangleConstraint[assertionList.size()])))
//				System.out.println("Failed to add Assertinal Constraint");

		}		

		return  ret.toArray(new ConstraintNetwork[ret.size()]);
	}


	private ConstraintNetwork createTBOXspatialNetwork(ConstraintSolver solver, HashMap<String, RectangularRegion> getVariableByName) {

		//general knowledge
		ConstraintNetwork ret = new ConstraintNetwork(solver);
//		Vector<MultiBinaryConstraint> addedGeneralKn = new Vector<MultiBinaryConstraint>();
		for (int i = 0; i < this.rules.length; i++) {

			if(this.rules[i].getFrom().compareTo(this.rules[i].getTo()) == 0 ){
				Bounds[] sizeBounds = new Bounds[this.rules[i].getUnaryRAConstraint().getBounds().length];
				for (int j = 0; j < sizeBounds.length; j++) {						
					Bounds bSize = new Bounds(this.rules[i].getUnaryRAConstraint().getBounds()[j].min, this.rules[i].getUnaryRAConstraint().getBounds()[j].max);
					sizeBounds[j] = bSize; 	
				}
				UnaryRectangleConstraint uConsSize = new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, sizeBounds);

				if(getVariableByName.get(this.rules[i].getFrom()) != null )
					uConsSize.setFrom(getVariableByName.get(this.rules[i].getFrom()));
				else{
					RectangularRegion var = (RectangularRegion)solver.createVariable();
					var.setName(this.rules[i].getFrom());
					uConsSize.setFrom(var);
					getVariableByName.put(this.rules[i].getFrom(), var);
				}
				if(getVariableByName.get(this.rules[i].getTo()) != null )
					uConsSize.setTo(getVariableByName.get(this.rules[i].getTo()));
				else{
					RectangularRegion var = (RectangularRegion)solver.createVariable();
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


				RectangleConstraint uConsBinary = new RectangleConstraint(xAllenCon, yAllenCon);

				if(getVariableByName.get(this.rules[i].getFrom()) != null )
					uConsBinary.setFrom(getVariableByName.get(this.rules[i].getFrom()));
				else{
					RectangularRegion var = (RectangularRegion)solver.createVariable();
					var.setName(this.rules[i].getFrom());
					uConsBinary.setFrom(var);
					getVariableByName.put(this.rules[i].getFrom(), var);
				}
				if(getVariableByName.get(this.rules[i].getTo()) != null )
					uConsBinary.setTo(getVariableByName.get(this.rules[i].getTo()));
				else{
					RectangularRegion var = (RectangularRegion)solver.createVariable();
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
		
//	public RectangleConstraintSolver getGroundSolver(){
//		return this.metaCS.getConstraintSolvers()[0];
//	}
	
	
	//	public Rectangle getRectangle(String st){
	//		return solver.extractBoundingBoxesFromSTPs(st).getAlmostCentreRectangle();
	//	}

	




}
