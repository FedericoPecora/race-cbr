package test.multi;

import multi.allenInterval.AllenInterval;
import multi.allenInterval.AllenIntervalConstraint;
import multi.allenInterval.AllenIntervalNetworkSolver;
import time.APSPSolver;
import time.Bounds;
import framework.ConstraintNetwork;

public class TestAllenIntervalNetworkSolver {
	
	public static void main(String[] args) {
		
		// this solver is derived from MultiConstraintSolver, but pay attention ...
		AllenIntervalNetworkSolver solver = new AllenIntervalNetworkSolver(0, 100);
		
		
		// MultiConstraintSolver extends ConstraintSolver, and the createVariable method is 
		// implemented in the latter...
		// this method executes the createVariableSub method that is specific of the particular
		// instantiation: for AllenInterval the variable takes a reference to the constraintSolver of the Solver, therefore the APSPSolver
		// APSPSolver is called through the createVariable which couples the constraintSolver with the constraint references... 
		// here constraints are taken into account, i.e. they are not empty.
		// REMEMBER: when cloning a MultiConstraint, the field constraints will be empty since the cloned MC
		// will be used to represent the second Layer: in this layer the constraint are just references to the UPPER (AllenIntervalNetwork)
		// or LOWER (STP network)
		// this approach reflects the structure for which there is a META-CSP where underneath activity networks exist. These activity networks
		// exploit a deeper level, the STP network.
		// The META-CSP own explicit constraints. The Activity network, initially, has just references to these constraints which are re-mapped (and trasformed)
		// accordingly in the lower layer, the temporal one. 
		// The activity network doesn't need to replicate constraint (and it also relies on the fact that, implementatively, it is easier) but it could
		// host successive constraints which are not represented in the upper layer (for example they are constraint added during the execution of a plan to 
		// fulfill some pre-conditions but it is not needed to be represented on the upper layer )
		AllenInterval act0 = (AllenInterval)solver.createVariable();
		AllenInterval act1 = (AllenInterval)solver.createVariable();
		AllenInterval act2 = (AllenInterval)solver.createVariable();
		
		
		ConstraintNetwork.draw(solver.getConstraintNetwork());
		
		// Initially the constraint is created with a reference to the solver
		// Let's say that we have this hierarchy:
		//	constraint
		//		->	multiConstraint
		//			->	MultiBinaryConstraint
		//				->	AllenIntervalConstraint
		//
		//	initially a constraint has basically a reference to the solver and a scope (typically a star and end nodes )
		// 	a multiConstraint is then equipped with an array of constraints
		//	a multiBinary constraint essentially focuses on the scope, establishing that has scope has 2 variables, the "from" and "to" variables
		//  the AllenIntervalConstraint is endowed with a type (related to the Allen Interval Algebra) and bounds
		//  this class has the method createInternalConstraints: this function creates a set of Constraints which are then returned by the function
		//  itself. In this particular case, the set of constraints is constituted by one or two elements
		//	setFrom and setTo act just as a linking function between variables and the scope of the constraint
		
		
		
		
		AllenIntervalConstraint dur0 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(10, APSPSolver.INF));
		dur0.setFrom(act0);
		dur0.setTo(act0);
		//if (!solver.addConstraint(dur0)) System.out.println("Failed to add constraint " + dur0);
		
		AllenIntervalConstraint dur1 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(20, APSPSolver.INF));
		dur1.setFrom(act1);
		dur1.setTo(act1);
		//if (!solver.addConstraint(dur1)) System.out.println("Failed to add constraint " + dur1);

		AllenIntervalConstraint dur2 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration,  new Bounds(30, APSPSolver.INF));
		dur2.setFrom(act2);
		dur2.setTo(act2);
		//if (!solver.addConstraint(dur2)) System.out.println("Failed to add constraint " + dur2);
		
		AllenIntervalConstraint con0 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.After,  new Bounds(0, APSPSolver.INF));
		con0.setFrom(act0);
		con0.setTo(act1);
		//if (!solver.addConstraint(con0)) System.out.println("Failed to add constraint " + con0);
		
		AllenIntervalConstraint con1 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Contains,  new Bounds(10, APSPSolver.INF), new Bounds(10, APSPSolver.INF));
		con1.setFrom(act1);
		con1.setTo(act2);
		//if (!solver.addConstraint(con1)) System.out.println("Failed to add constraint " + con1);
		
		
		// At the moment, each constraint has just a reference to the ConstraintSolver and proper bounds (currently, no 
		// calls to the createInternalConstraints) has been performed
		

		AllenIntervalConstraint[] allConstraints = {dur0,dur1,dur2,con0,con1};
		
		System.out.println(solver.getDescription());
		System.out.println(act1.getDescription());
		
		// HERE WE GO: the solver calls the "addConstraints" function: go to the function to see details
		
		
		
		
		// The insertion has the following dynamic:		
		// 1	-	Initially we have MultiConstraints. These MCs have to be inserted in a multiConstraintSolver
		// 2	-	The MCS is equipped with a ConstraintNetwork and a series of internalSolvers. For the AllenInternvalNetworkSolver
		//			we have just one internalSolver that is the APSPSolver. 
		// 3	-	Each MC refers to variables which have already been inserted in the MCS and these variables point toward the internalConstraints
		//			ConstraintSolvers too
		//			For each source node of the MC, the internalSolvers are considered; for each internalSolver pointed by the 
		//			source MultiVariable (i.e. in our case the APSPSolver ) the internalConstraints of the MC are put in this solver
		//			and this appens in two steps:
		//				A-	the first is a recursive call to the addConstraints itself since the APSPSolver extends MultiConstraintSolver
		//				B- 	this recursive call is characterized by the fact that, in this case, internalConstraints are not "Multi" but just simple constraints,
		//					in this specific case SimpleDistanceConstraint. Therefore, in this second call I have my APSPSolver that acts as my ConstraintSolver
		//					that has to manage simpleConstraints. In particular, the part of calling the source nodes is skipped and we can directly consider
		//					the part which exploits the addConstraintsSub part. This routine is specific of the particular solver, so we have to 
		//					look at APSPSolver. The addConstraintsSub calls internally, for each constraint, the cCreate method which links the 
		//					SimpleDistanceConstraint to the timepoint of the solver. THE FIRST GOAL IS DONE!
		//	4	-	Now we have just finished the recursive call, but we have still at the middle of the first addConstraints call that has received 
		//			a bunch (5) of MultiConstraint objects as input. Now the first important call that we find is again the addConstraintsSub: though this time
		//			we are in presence of a MultiConstraintSolver (the Allen blabla) since it is NOT the APSPSolver: therefore we have to look at the code 
		//			in MultiConstraintSolver
		//	5-		The addConstraintsSub proceeds like the following: 
		//			A-	two vectors of vectors, of length equal to the number of typologies of constraint, are instantiated.
		//			B-	Each MultiConstraint (we have 5) is cloned: THIS IS A FUNDAMENTAL STEP
		//				the cloned objects differ from the original in two things:
		//					*	the internal field "constraints" if not populated
		//					**	the scope of the cloned object is a little bit different:
		//							in the original one we had a scope of MVs, in the cloned object we have a scope made by the union of the internalVariables 
		//							of MV
		//			C-	Now we have the cloned objects which do not have constraints
		//				let's go on... remember: we are in the middle of addConstraintsSub of the MultiConstraintSolver object... this means
		//				that the caller is the MultiConstrintSolver itself
		//				In the middle of this procedure, it deliberately calls again this.constraintSolvers[i].addConstraints:
		//				now we have another recursive call whose input arguments are cloned constraints that have the 'constraints' field empty (remember
		//				that in the case of simple constraints we had a different addConstraintsSub call and, still, remember that now we are in 
		//				the middle of the addConstraintsSub routine imlemented in the MultiConstraintSolver)
		//				Now, there are a couple or more of recursive calls, but fortunately we can summarize by the fact that nothing can be added 
		//				and thus these routines act like empty blocks (I guess... I hople ... PLEAZZZZEEEEE!!!!!!!)	
		//				At this point the recursive calls are passed, and we can exit with a 'true' as a result (OH YEAH!!!)
		//			D-	Almost done, we are now back with our 5, fully istantiated constraints, and with an addConstraintsSub that  has returned a true...
		//				After this, we have the if block 
		//					if (addConstraintsSub(toAddArray)) 
		//				
		//				asserted, therefore
		//				
		//					our fully instantiated constraints are put in the AllenIntervalNetworkSolver.theNetwork
		//						for (Constraint con : toAddArray) this.theNetwork.addConstraint(con);
		//				WAIIIIIT: I missed a step, also the constraints empty object should be placed in the related network.... MMMMMMMMM!!!!!!!

		
		
		// HERE WE HAVE THE addConstraints of the MultiConstraintSolver
		if (!solver.addConstraints(allConstraints)) { 
			System.out.println("Failed to add constraints!");
			System.exit(0);
		}
		
		/**/
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		solver.removeConstraint(con1);
		
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		solver.removeVariable(act0);
		/**/
		
		System.out.println(act0.getDomain().chooseValues());
		
	}
	

}
