package test.ICAPS14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

public class BenckmarkParserComplete {

	/**
	 * @param args
	 */
	static String misplacedNumber = "6";
	static String PATH = "/home/iran/Desktop/benchmark/TestCase4/" + misplacedNumber +"/";
	static Vector<String> fileNameList = new Vector<String>();
	static int totalProblem = 0;
	static HashMap<Integer, Integer> timeoutNumber = new HashMap<Integer, Integer>();
	static HashMap<Integer, Double> timeoutpercentage = new HashMap<Integer, Double>();
	
	static HashMap<Integer, Double> bfAvgs = new HashMap<Integer, Double>();
	static HashMap<Integer, Double> depthAvgs = new HashMap<Integer, Double>();
	
	public static void main(String[] args) {
		
		Vector<Long> oneArmSearchTime = new Vector<Long>();
		Vector<Long> twoArmSearchTime = new Vector<Long>();
		Vector<Long> threeArmSearchTime = new Vector<Long>();
		Vector<Long> fourArmSearchTime = new Vector<Long>();
		

		Vector<Long> oneArmbf = new Vector<Long>();
		Vector<Long> twoArmbf = new Vector<Long>();
		Vector<Long> threeArmbf = new Vector<Long>();
		Vector<Long> fourArmbf = new Vector<Long>();

		Vector<Long> oneArmdepth = new Vector<Long>();
		Vector<Long> twoArmdepth = new Vector<Long>();
		Vector<Long> threeArmdepth = new Vector<Long>();
		Vector<Long> fourArmdepth = new Vector<Long>();

		
		File folder = new File(PATH);
		listFilesForFolder(folder);
		totalProblem = fileNameList.size();
		
		
		for (int i = 0; i < fileNameList.size(); i++) {
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(PATH + fileNameList.get(i)));
				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
					    
						if(line.contains("_")){
							int[] count = new int[4];
							int a = 0, f = 0;
							for(int j =0; j < line.length(); j++){
							    if(line.charAt(j) == ','){
							        count[a] = j;
							        a++;
							    }
							    if(line.charAt(j) == 'F'){
							        f = j;
							        
							    }
							}
							
							

							
//							System.out.println(count[0]);
//							System.out.println(line.substring(f + 8, f + 9));
							
							
							long searchTime = Long.valueOf(line.substring(30, count[1]));
							long bf = Long.valueOf(line.substring(f + 8, f + 9));
//							long depth = Long.valueOf(line.substring(30, line.length()));
							
							
							if(line.substring(15, 16).compareTo("1") == 0){
								oneArmSearchTime.add(searchTime);
								oneArmbf.add(bf);
//								oneArmdepth.add(depth);
							}
							if(line.substring(15, 16).compareTo("2") == 0){
								twoArmSearchTime.add(searchTime);
								twoArmbf.add(bf);
//								twoArmdepth.add(depth);

							}
							if(line.substring(15, 16).compareTo("3") == 0){
								threeArmSearchTime.add(searchTime);
								threeArmbf.add(bf);
//								threeArmdepth.add(depth);
							}
							if(line.substring(15, 16).compareTo("4") == 0)
								fourArmSearchTime.add(searchTime);
								fourArmbf.add(bf);
//								fourArmdepth.add(depth);							
						}
							
//						System.out.println(line);
						
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		int counter = 0, sumbf = 0, sumd = 0;
		for (int i = 0; i < oneArmSearchTime.size(); i++) {
			if(oneArmSearchTime.get(i) == Long.MAX_VALUE - 1) counter ++;
			sumbf += oneArmbf.get(i);
//			sumd += oneArmdepth.get(i);
		}
		timeoutNumber.put(1, counter);
		bfAvgs.put(1, (double)sumbf/oneArmSearchTime.size());
		depthAvgs.put(1, (double)sumd/oneArmSearchTime.size());
		
		counter = 0;
		for (int i = 0; i < twoArmSearchTime.size(); i++){ 
			if(twoArmSearchTime.get(i) == Long.MAX_VALUE - 1) counter ++;
			sumbf += twoArmbf.get(i);
//			sumd += twoArmdepth.get(i);
		}
		timeoutNumber.put(2, counter);
		bfAvgs.put(2, (double)sumbf/twoArmSearchTime.size());
		depthAvgs.put(2, (double)sumd/twoArmSearchTime.size());

		
		
		counter = 0; sumbf = 0; sumd = 0;
		for (int i = 0; i < threeArmSearchTime.size(); i++){ 
			if(threeArmSearchTime.get(i) == Long.MAX_VALUE - 1) counter ++;
			sumbf += threeArmbf.get(i);
//			sumd += threeArmdepth.get(i);
		}
		timeoutNumber.put(3, counter);
		bfAvgs.put(3, (double)sumbf/threeArmSearchTime.size());
		depthAvgs.put(3, (double)sumd/threeArmSearchTime.size());


		counter = 0; sumbf = 0; sumd = 0;
		for (int i = 0; i < fourArmSearchTime.size(); i++){ 
			if(fourArmSearchTime.get(i) == Long.MAX_VALUE - 1) counter ++;
			sumbf += fourArmbf.get(i);
//			sumd += fourArmdepth.get(i);
		}
		timeoutNumber.put(4, counter);
		bfAvgs.put(4, (double)sumbf/fourArmSearchTime.size());
		depthAvgs.put(4, (double)sumd/fourArmSearchTime.size());

		
		
		for (Integer arm : timeoutNumber.keySet()) {			
			timeoutpercentage.put(arm, ((double)timeoutNumber.get(arm)/totalProblem) * 100);
		}
		
		
		
		
		System.out.println("totalProblem for "+ misplacedNumber + " misplaced objects: "+ totalProblem);
		System.out.println("timeout Number: " + timeoutNumber);
		System.out.println("timeout percentage: " + timeoutpercentage);
		System.out.println("branching factor avg: " + bfAvgs);
		System.out.println("depth avg: " + depthAvgs);
		
		
//		System.out.println("oneArm: " + oneArm);
//		System.out.println("twoArm: " + twoArm);
//		System.out.println("threeArm: " + threeArm);
//		System.out.println("fourArm: " + fourArm);


	}
	
	public static void listFilesForFolder(final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	        	fileNameList.add(fileEntry.getName());
//	            System.out.println(fileEntry.getName());
	        }
	    }
	}

}
