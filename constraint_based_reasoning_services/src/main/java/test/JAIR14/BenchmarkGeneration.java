package test.JAIR14;


import java.awt.List;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;


import org.metacsp.meta.hybridPlanner.FluentBasedSimpleDomain;
import org.metacsp.meta.hybridPlanner.MetaOccupiedConstraint;
import org.metacsp.meta.hybridPlanner.MetaSpatialAdherenceConstraint;
import org.metacsp.meta.hybridPlanner.SimpleHybridPlanner;
import org.metacsp.meta.simplePlanner.SimpleDomain.markings;
import org.metacsp.meta.simplePlanner.SimpleOperator;


import org.metacsp.multi.activity.Activity;
import org.metacsp.multi.allenInterval.AllenIntervalConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraint;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangleConstraintSolver;
import org.metacsp.multi.spatial.rectangleAlgebra.RectangularRegion;
import org.metacsp.multi.spatial.rectangleAlgebra.UnaryRectangleConstraint;
import org.metacsp.multi.spatial.rectangleAlgebraNew.toRemove.OntologicalSpatialProperty;
import org.metacsp.multi.spatioTemporal.SpatialFluent;
import org.metacsp.multi.spatioTemporal.SpatialFluentSolver;


import org.metacsp.spatial.utility.SpatialAssertionalRelation;
import org.metacsp.spatial.utility.SpatialRule;
import org.metacsp.time.APSPSolver;
import org.metacsp.time.Bounds;
import org.metacsp.utility.logging.MetaCSPLogging;
import org.metacsp.utility.timelinePlotting.TimelinePublisher;
import org.metacsp.utility.timelinePlotting.TimelineVisualizer;
import org.metacsp.framework.Constraint;
import org.metacsp.framework.ConstraintNetwork;
import org.metacsp.framework.ValueOrderingH;
import org.metacsp.framework.VariableOrderingH;

public class BenchmarkGeneration {
	
	//the Number of Objects are the same= 6 + 1 (including cup)
	//Initial situation: Holding cup
	
	
	static int totalExp  = 5;
	static int armsCounter = 4;
	static String PATH = "/home/iran/Desktop/benchmark/testCase1/coordinateGenerator/";
	static String PATH_INIT_PLOT = "/home/iran/Desktop/benchmark/testCase1/PLOT_INIT/";
	static String PATH_FINAL_PLOT = "/home/iran/Desktop/benchmark/testCase1/PLOT_FINAL/";
	static String DOMAINPATH = "/home/iran/Desktop/benchmark/testCase1/domain/deskDomain"; 
	
	
	static int pad = 0;	
	static long duration = 1000;

	public static void main(String[] args) {
		
		
		for (int ii = 0; ii < totalExp; ii++) {
			for (int arm_resources = 1; arm_resources <= armsCounter; arm_resources++) {
				
				SimpleHybridPlanner simpleHybridPlanner = new SimpleHybridPlanner(0, 100000, 0);

				FluentBasedSimpleDomain.parseDomain(simpleHybridPlanner, DOMAINPATH + "_"+ arm_resources +".ddl", FluentBasedSimpleDomain.class);
				
				
				//Most critical conflict is the one with most activities 
				VariableOrderingH varOH = new VariableOrderingH() {
					@Override
					public int compare(ConstraintNetwork arg0, ConstraintNetwork arg1) {
						return arg1.getVariables().length - arg0.getVariables().length;
					}
					@Override
					public void collectData(ConstraintNetwork[] allMetaVariables) { }
				};
				// no value ordering
				ValueOrderingH valOH = new ValueOrderingH() {
					@Override
					public int compare(ConstraintNetwork o1, ConstraintNetwork o2) { return 0; }
				};
				MetaSpatialAdherenceConstraint metaSpatialAdherence = new MetaSpatialAdherenceConstraint(varOH, valOH);
				SpatialFluentSolver groundSolver = (SpatialFluentSolver)simpleHybridPlanner.getConstraintSolvers()[0];

//				MetaCSPLogging.setLevel(SimpleHybridPlanner.class, Level.FINEST);
//				MetaCSPLogging.setLevel(MetaSpatialAdherenceConstraint.class, Level.FINEST);
				//#################################################################################################################
				//add metaOccupiedConstraint
				MetaOccupiedConstraint metaOccupiedConstraint = new MetaOccupiedConstraint(null, null);
				metaOccupiedConstraint.setPad(pad);
				//#################################################################################################################
				//this is spatial general and assetional rule
				Vector<SpatialRule> srules = new Vector<SpatialRule>();
				Vector<SpatialAssertionalRelation> saRelations = new Vector<SpatialAssertionalRelation>();
				HashMap<String, Rectangle> observation = new HashMap<String, Rectangle>();

				getSpatialKnowledge(srules);
				observation = getAssertionalRule(saRelations, ii);
//				observation = getAssertionalRule(saRelations);
				insertCurrentStateCurrentGoal(groundSolver);
				//#################################################################################################################
				//add spatial general and assertional rule to MetaSpatialFluentConstraint
				metaSpatialAdherence.setSpatialRules(srules.toArray(new SpatialRule[srules.size()]));
				metaSpatialAdherence.setSpatialAssertionalRelations(saRelations);
				metaSpatialAdherence.setInitialGoal(new String[]{"cup1"});

				//#################################################################################################################
				//add meta constraint
				simpleHybridPlanner.addMetaConstraint(metaOccupiedConstraint);
				simpleHybridPlanner.addMetaConstraint(metaSpatialAdherence);
				//#################################################################################################################
				
				
				BufferedWriter initPlot = null;
				String initLayoutPlot = "";
				initLayoutPlot = ((RectangleConstraintSolver)((SpatialFluentSolver)simpleHybridPlanner.getConstraintSolvers()[0]).
						getConstraintSolvers()[0]).drawAlmostCentreRectangle(130, observation);				
				try{
					
					initPlot = new BufferedWriter(new FileWriter(PATH_INIT_PLOT +ii+ "_init"+".dat", false));
					initPlot.write(initLayoutPlot);
					initPlot.newLine();
					initPlot.flush();
				}				
				catch (IOException ioe) {
					ioe.printStackTrace();
				}

				long timeNow = Calendar.getInstance().getTimeInMillis();
				if(simpleHybridPlanner.backtrack())
					System.out.println("success here");
				long totalTime = (Calendar.getInstance().getTimeInMillis()-timeNow);
//				System.out.println("TOTAL TIME: " + totalTime);
				if(simpleHybridPlanner.getTimeOut())
					totalTime = Long.MAX_VALUE - 1;
				BufferedWriter bw = null;
				String strfile = "";
				int culpritNumber = metaSpatialAdherence.getNumberofMisplaced();
				strfile += "number_of_arm: " + arm_resources + ", SearchTime: " + totalTime + "\n";
				String postfix = "_"+culpritNumber+".dat";
				try{
					bw = new BufferedWriter(new FileWriter(PATH +ii+ postfix, true));
					bw.write(strfile);
					bw.newLine();
					bw.flush();
				}				
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
				
				
				HashMap<String, Rectangle> recs = new HashMap<String, Rectangle>(); 
				for (String str : ((RectangleConstraintSolver)((SpatialFluentSolver)simpleHybridPlanner.getConstraintSolvers()[0])
						.getConstraintSolvers()[0]).extractAllBoundingBoxesFromSTPs().keySet()) {
					if(str.endsWith("1")){
						recs.put( str,((RectangleConstraintSolver)((SpatialFluentSolver)simpleHybridPlanner.getConstraintSolvers()[0])
								.getConstraintSolvers()[0]).extractAllBoundingBoxesFromSTPs().get(str).getAlmostCentreRectangle());
					}
				}		
				
				BufferedWriter finalPlot = null;
				String finalLayoutPlot = "";
				finalLayoutPlot = ((RectangleConstraintSolver)((SpatialFluentSolver)simpleHybridPlanner.getConstraintSolvers()[0]).
						getConstraintSolvers()[0]).drawAlmostCentreRectangle(130, recs);				
				try{
					
					finalPlot = new BufferedWriter(new FileWriter(PATH_FINAL_PLOT +ii+ "_final"+".dat", false));
					finalPlot.write(finalLayoutPlot);
					finalPlot.newLine();
					finalPlot.flush();
				}				
				catch (IOException ioe) {
					ioe.printStackTrace();
				}

				
				System.out.println();

			}

		}

	}
	
	private static LinkedHashMap sortHashMapByValuesD(HashMap passedMap) {
		   ArrayList mapKeys = new ArrayList(passedMap.keySet());
		   ArrayList mapValues = new ArrayList(passedMap.values());
		   Collections.sort(mapValues);
		   Collections.sort(mapKeys);

		   LinkedHashMap sortedMap = 
		       new LinkedHashMap();

		   Iterator valueIt = ((java.util.List<SpatialRule>) mapValues).iterator();
		   while (valueIt.hasNext()) {
		       long val = (Long) valueIt.next();
		    Iterator keyIt = ((java.util.List<SpatialRule>) mapKeys).iterator();

		    while (keyIt.hasNext()) {
		        Activity key = (Activity) keyIt.next();
		        long comp1 = (Long) passedMap.get(key);
		        long comp2 = val;

		        if (comp1 == comp2){
		            passedMap.remove(key);
		            mapKeys.remove(key);
		            sortedMap.put(key, val);
		            break;
		        }
		    }
		}
		return sortedMap;
	}
	
	private static void setFluentintoNetwork(Vector<Constraint> cons, SpatialFluentSolver grounSpatialFluentSolver, String component, 
			String name, String symbolicDomain, markings mk, long release){
		
		SpatialFluent sf = (SpatialFluent)grounSpatialFluentSolver.createVariable(component);
		sf.setName(name);
		
		((RectangularRegion)sf.getInternalVariables()[0]).setName(name);
		((Activity)sf.getInternalVariables()[1]).setSymbolicDomain(symbolicDomain);
		((Activity)sf.getInternalVariables()[1]).setMarking(mk);
		
		if(mk.equals(markings.JUSTIFIED)){
			AllenIntervalConstraint onDuration = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
			onDuration.setFrom(sf.getActivity());
			onDuration.setTo(sf.getActivity());
			cons.add(onDuration);
			
			AllenIntervalConstraint releaseOn = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(release, release));
			releaseOn.setFrom(sf.getActivity());
			releaseOn.setTo(sf.getActivity());
			cons.add(releaseOn);					
		}

	}
	
	private static void insertCurrentStateCurrentGoal(SpatialFluentSolver grounSpatialFluentSolver) {
		
		Vector<Constraint> cons = new Vector<Constraint>();
		
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "table1", "at_table1_table1()", markings.JUSTIFIED,  8);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "book1", "at_book1_table1()", markings.JUSTIFIED, 8);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "cup1", "at_cup1_table1()", markings.UNJUSTIFIED, 8);
		
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "monitor1", "at_monitor1_table1()", markings.JUSTIFIED, 8);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "keyboard1", "at_keyboard1_table1()", markings.JUSTIFIED,  8);		
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "pen1", "at_pen1_table1()", markings.JUSTIFIED,  8);		
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "notebook1", "at_notebook1_table1()", markings.JUSTIFIED, 8);
		setFluentintoNetwork(cons, grounSpatialFluentSolver, "atLocation", "penHolder1", "at_penHolder1_table1()", markings.JUSTIFIED, 8);
		//===================================================================================================================		
		Activity two = (Activity)grounSpatialFluentSolver.getConstraintSolvers()[1].createVariable("RobotProprioception");
		two.setSymbolicDomain("holding_cup1()");
		two.setMarking(markings.JUSTIFIED);

		AllenIntervalConstraint releaseHolding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(1,1));
		releaseHolding.setFrom(two);
		releaseHolding.setTo(two);
		cons.add(releaseHolding);
		
		AllenIntervalConstraint durationHolding = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		durationHolding.setFrom(two);
		durationHolding.setTo(two);
		cons.add(durationHolding);

		
		Activity two2 = (Activity)grounSpatialFluentSolver.getConstraintSolvers()[1].createVariable("atLocation");
		two2.setSymbolicDomain("at_robot1_table1()");
		two2.setMarking(markings.JUSTIFIED);

		AllenIntervalConstraint releaseHolding2 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Release, new Bounds(1,1));
		releaseHolding2.setFrom(two2);
		releaseHolding2.setTo(two2);
		cons.add(releaseHolding2);
		
		AllenIntervalConstraint durationHolding2 = new AllenIntervalConstraint(AllenIntervalConstraint.Type.Duration, new Bounds(duration,APSPSolver.INF));
		durationHolding2.setFrom(two2);
		durationHolding2.setTo(two2);
		cons.add(durationHolding2);
		
		
		grounSpatialFluentSolver.getConstraintSolvers()[1].addConstraints(cons.toArray(new Constraint[cons.size()]));
		
	}

	

	private static void getSpatialKnowledge(Vector<SpatialRule> srules){
		
				
		//Size part
		addSizeConstraint(srules, "table", 120, 120);
		addSizeConstraint(srules, "book", 10, 10);		
		addSizeConstraint(srules, "monitor", 45, 15);
		addSizeConstraint(srules, "keyboard", 40, 20);
		addSizeConstraint(srules, "notebook",15, 20);
		addSizeConstraint(srules, "cup", 5, 5);
		addSizeConstraint(srules, "pen", 1, 18);		
		addSizeConstraint(srules, "penHolder", 10, 5);

		
		

		//Every thing should be on the table		
		addOnTableConstraint(srules, "monitor");
		addOnTableConstraint(srules, "keyboard");
		addOnTableConstraint(srules, "notebook");
		addOnTableConstraint(srules, "pen");
		addOnTableConstraint(srules, "book");
		addOnTableConstraint(srules, "cup");
		addOnTableConstraint(srules, "penHolder");
		
		
		SpatialRule pen_notebook = new SpatialRule("pen", "notebook", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, new Bounds(3,5)),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds() ))
				);
		srules.add(pen_notebook);

		SpatialRule notebook_keyboard = new SpatialRule("notebook", "keyboard", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds() ))
				);
		srules.add(notebook_keyboard);
		
		SpatialRule keyboard_monitor = new SpatialRule("keyboard", "monitor", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, AllenIntervalConstraint.Type.During.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds() ))
				);
		srules.add(keyboard_monitor);

		SpatialRule cup_keyboard= new SpatialRule("cup", "keyboard", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds() ))
				);
		srules.add(cup_keyboard);
		
		SpatialRule cup_book= new SpatialRule("cup", "book", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.Before, AllenIntervalConstraint.Type.Before.getDefaultBounds() ))
				);
		srules.add(cup_book);
		

		SpatialRule penHolder_book= new SpatialRule("penHolder", "book", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.Overlaps, AllenIntervalConstraint.Type.Overlaps.getDefaultBounds()),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.After, AllenIntervalConstraint.Type.After.getDefaultBounds() ))
				);
		srules.add(penHolder_book);

		
	}
	
	private static void addSizeConstraint(Vector<SpatialRule> srules, String str, long size_x, long size_y){
		
		SpatialRule sizeCon = new SpatialRule(str, str, 
				new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.Size, new Bounds(size_x, size_x), new Bounds(size_y, size_y)));
		srules.add(sizeCon);
		
	}

	private static void addOnTableConstraint(Vector<SpatialRule> srules, String str){
		
		Bounds withinReach_y_lower = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_y_upper = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_x_lower = new Bounds(5, APSPSolver.INF);
		Bounds withinReach_x_upper = new Bounds(5, APSPSolver.INF);
		
		SpatialRule r8 = new SpatialRule(str, "table", 
				new RectangleConstraint(new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_x_lower,withinReach_x_upper),
						new AllenIntervalConstraint(AllenIntervalConstraint.Type.During, withinReach_y_lower, withinReach_y_upper))
				);
		srules.add(r8);
		
	}
	
	private static void insertAtConstraint(HashMap<String, Rectangle> recs, Vector<SpatialAssertionalRelation> saRelations, 
			String str, long xl, long xu, long yl, long yu, boolean movable){
		
		if(xl == 0 && xu == 0 && yl == 0 && yu == 0){
			SpatialAssertionalRelation table_assertion = new SpatialAssertionalRelation(str+"1", str);
			table_assertion.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
					new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF), new Bounds(0, APSPSolver.INF)));
			OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
			tableOnto.setMovable(movable);
			table_assertion.setOntologicalProp(tableOnto);
			saRelations.add(table_assertion);			
			
		}
		else{
			SpatialAssertionalRelation table_assertion = new SpatialAssertionalRelation(str+"1", str);
			table_assertion.setUnaryAtRectangleConstraint(new UnaryRectangleConstraint(UnaryRectangleConstraint.Type.At, 
					new Bounds(xl, xl), new Bounds(xu, xu), new Bounds(yl, yl), new Bounds(yu, yu)));
			OntologicalSpatialProperty tableOnto = new OntologicalSpatialProperty();
			tableOnto.setMovable(movable);
			table_assertion.setOntologicalProp(tableOnto);
			saRelations.add(table_assertion);			
			recs.put(str+"1", new Rectangle((int)(xl), (int)(yl), (int)(xu - xl), (int)(yu - yl)));
		}
		

	}
	


	
	private static HashMap<String, Rectangle> getAssertionalRule(Vector<SpatialAssertionalRelation> saRelations, int filename){
		
		HashMap<String, Rectangle> recs = new HashMap<String, Rectangle>();

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(PATH + filename + ".dat"));
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
				    
					if(line.compareTo("++++++") == 0)
						break;
					
					String[] arr = line.split(" ");
					if(arr[0].compareTo("table") == 0){
						insertAtConstraint(recs, saRelations, arr[0], Integer.parseInt(arr[1]), Integer.parseInt(arr[2]), Integer.parseInt(arr[3]), Integer.parseInt(arr[4]), false);
					}
					else
						insertAtConstraint(recs, saRelations, arr[0], Integer.parseInt(arr[1]), Integer.parseInt(arr[2]), Integer.parseInt(arr[3]), Integer.parseInt(arr[4]), true);
					
//					System.out.println(arr[0] + " " +Integer.parseInt(arr[1])+ " " + Integer.parseInt(arr[2]) + " " + Integer.parseInt(arr[3]) + " " + Integer.parseInt(arr[4]));
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return recs;
	}
	
	private static HashMap<String, Rectangle> getAssertionalRule(Vector<SpatialAssertionalRelation> saRelations){
		
		HashMap<String, Rectangle> recs = new HashMap<String, Rectangle>();
		
		
		
		insertAtConstraint(recs, saRelations, "table", 0, 120, 0, 120, false);
		insertAtConstraint(recs, saRelations, "cup", 0, 0, 0, 0, true);
		insertAtConstraint(recs, saRelations, "monitor", 25, 70, 80, 95, false);
		
		insertAtConstraint(recs, saRelations, "book", 98, 108, 57, 67, true); //true
//		insertAtConstraint(recs, saRelations, "book", 45, 55, 20, 30, true); //false
//		insertAtConstraint(recs, saRelations, "keyboard", 50, 90, 35, 55, true); //false not overlapped with cup
		insertAtConstraint(recs, saRelations, "keyboard", 56, 106, 20, 40, true); //false overlapped with cup
//		insertAtConstraint(recs, saRelations, "keyboard", 27, 67, 45, 65, true); //true

		
		insertAtConstraint(recs, saRelations, "pen", 6, 7, 20, 38, true); //false
//		insertAtConstraint(recs, saRelations, "notebook", 100, 115, 60, 80, true); ////false 15 20		
		insertAtConstraint(recs, saRelations, "penHolder", 9, 19, 74, 79, true); //false //10, 5
		
		
//		insertAtConstraint(recs, saRelations, "pen", 28, 29, 22, 40, true); //true
		insertAtConstraint(recs, saRelations, "notebook", 9, 24, 21, 41, true); //true
//		insertAtConstraint(recs, saRelations, "penHolder", 93, 103, 68, 73, true); //true
		

		return recs;

	}
	
}


