package test.meta;

//import onLineMonitoring.DomainDescription;
import meta.fuzzyActivity.FuzzyActivityDomain;
import meta.fuzzyActivity.FuzzyActivityMetaSolver;
import onLineMonitoring.FuzzySensorEvent;
import onLineMonitoring.MonitoredComponent;
import onLineMonitoring.PhysicalSensor;
import onLineMonitoring.Requirement;
import onLineMonitoring.Rule;
import multi.symbols.SymbolicValueConstraint;
import framework.ConstraintNetwork;
import fuzzyAllenInterval.FuzzyAllenIntervalConstraint;

public class TestMetaFuzzyActivity {
	
	public static void main(String[] args) {

		PhysicalSensor locationSensor = new PhysicalSensor("Location", "Kitchen", "Livingroom", "Bathroom", "Bedroom");
		PhysicalSensor stoveSensor = new PhysicalSensor("Stove", "On", "Off");
		PhysicalSensor nightLightSensor = new PhysicalSensor("NightLight", "On", "Off");
		
		//MonitoredComponent
		MonitoredComponent human = new MonitoredComponent("Human", "Cooking", "Eating", "Sleeping");
		
		//Requirements and rules
		//E.g.: Cooking if in kitchen and stove on
		Requirement rule1req1 = new Requirement(locationSensor,
				new double[] {1.0, 0.0, 0.0, 0.0},
				SymbolicValueConstraint.Type.EQUALS,
				FuzzyAllenIntervalConstraint.Type.During);
		Requirement rule1req2 = new Requirement(stoveSensor,
				new double[] {1.0, 0.0},
				SymbolicValueConstraint.Type.EQUALS,
				FuzzyAllenIntervalConstraint.Type.Contains);
		Rule r1 = new Rule(human,
				new double[] {1.0, 0.0, 0.0},
				rule1req1, rule1req2);

		//E.g.: Sleeping if in bedroom and nightlight off
		Requirement rule2req1 = new Requirement(locationSensor,
				new double[] {0.0, 0.0, 0.0, 1.0},
				SymbolicValueConstraint.Type.EQUALS,
				FuzzyAllenIntervalConstraint.Type.During);
		Requirement rule2req2 = new Requirement(nightLightSensor,
				new double[] {0.0, 1.0},
				SymbolicValueConstraint.Type.EQUALS,
				FuzzyAllenIntervalConstraint.Type.Contains);
		Rule r2 = new Rule(human,
				new double[] {0.0, 0.0, 1.0},
				rule2req1, rule2req2);
		
		//E.g.: Eating if in Livingroom and After Cooking
				Requirement rule3req1 = new Requirement(locationSensor,
						new double[] {0.0, 1.0, 0.0, 0.0},
						SymbolicValueConstraint.Type.EQUALS,
						FuzzyAllenIntervalConstraint.Type.During);
				Requirement rule3req2 = new Requirement(human,
						new double[] {1.0, 0.0, 0.0},
						SymbolicValueConstraint.Type.EQUALS,
						FuzzyAllenIntervalConstraint.Type.After);
				Rule r3 = new Rule(human,
						new double[] {0.0, 1.0, 0.0},
						rule3req1, rule3req2);
		

		//Do something with the sensors
		FuzzySensorEvent loc1 = new FuzzySensorEvent(locationSensor, new double[]{0.61, 0.12, 0.21, 0.11}, 2);
		FuzzySensorEvent stove1 = new FuzzySensorEvent(stoveSensor, new double[]{0.82, 0.22}, 4);
		FuzzySensorEvent stove2 = new FuzzySensorEvent(stoveSensor, new double[]{0.13, 0.93}, 10);
		FuzzySensorEvent loc2 = new FuzzySensorEvent(locationSensor, new double[]{0.14, 0.17, 0.24, 0.94}, 15);
		FuzzySensorEvent nl1 = new FuzzySensorEvent(nightLightSensor, new double[]{0.15, 0.95}, 20);
		FuzzySensorEvent nl2 = new FuzzySensorEvent(nightLightSensor, new double[]{0.96, 0.16}, 25);
		
		
		//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		
		FuzzyActivityMetaSolver metaSolver = new FuzzyActivityMetaSolver(0);
		//ActivityNetworkSolver groundSolver = (ActivityNetworkSolver)((Scheduler)planner.getConstraintSolvers()[0]).getConstraintSolvers()[0];
		//FuzzyActivityNetworkSolver groundSolver = (FuzzyActivityNetworkSolver)metaSolver.getConstraintSolvers()[0];
		
		FuzzyActivityDomain fd = new FuzzyActivityDomain();
		fd.addFuzzySensorEvents(loc1,stove1,stove2,loc2,nl1,nl2);
		//fd.addFuzzySensorEvents(loc1,stove1,stove2,loc2);
		
		
		fd.addRule(r1);
		fd.addRule(r2);
		//fd.addRule(r3);
		
		
		fd.setRuleDependency();

		System.out.println("start");
		metaSolver.addMetaConstraint(fd);//it defines the heuristic for variable ordering
		metaSolver.branchAndBound();
		//System.out.println("Optimal Constraint Network: " + metaSolver.getOptimalConstraint());
		System.out.println("Most likely activities: " + metaSolver.getMostLiklyOccuredActivities());
		
		ConstraintNetwork.draw(metaSolver.getOptimalConstraint(), "Constraint Network");
		
		
		//ConstraintNetwork.draw(groundSolver.getConstraintNetwork(), "Constraint Network");
		
		System.out.println(metaSolver.getMostLiklyOccuredActivities());
		//metaSolver.draw();
		
	}

}
