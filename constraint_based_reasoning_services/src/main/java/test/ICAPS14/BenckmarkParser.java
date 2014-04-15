package test.ICAPS14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

public class BenckmarkParser {

	/**
	 * @param args
	 */
	static String misplacedNumber = "6";
	static String PATH = "/home/iran/Desktop/benchmark/testCase7/result/" + misplacedNumber +"/";
	static Vector<String> fileNameList = new Vector<String>();
	static int totalProblem = 0;
	static HashMap<Integer, Integer> timeoutNumber = new HashMap<Integer, Integer>();
	static HashMap<Integer, Double> timeoutpercentage = new HashMap<Integer, Double>();
	static HashMap<Integer, Double> searchAvg = new HashMap<Integer, Double>();
	
	
	
	public static void main(String[] args) {
		
		Vector<Long> oneArmSearchTime = new Vector<Long>();
		Vector<Long> twoArmSearchTime = new Vector<Long>();
		Vector<Long> threeArmSearchTime = new Vector<Long>();
		Vector<Long> fourArmSearchTime = new Vector<Long>();
		
		Vector<Long> cts = new Vector<Long>();
		
		
		
		
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
							
							
							long culpritTime = Long.valueOf(line.substring(31, line.indexOf("+")));
							cts.add(culpritTime);
							
							
							long searchTime = Long.valueOf(line.substring(line.indexOf("+") +14, line.length()));
							
							
							if(line.substring(15, 16).compareTo("1") == 0){
								oneArmSearchTime.add(searchTime);
							}
							if(line.substring(15, 16).compareTo("2") == 0){
								twoArmSearchTime.add(searchTime);
							}
							if(line.substring(15, 16).compareTo("3") == 0){
								threeArmSearchTime.add(searchTime);
							}
							if(line.substring(15, 16).compareTo("4") == 0)
								fourArmSearchTime.add(searchTime);

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
			else sumd += oneArmSearchTime.get(i); 
		}
		timeoutNumber.put(1, counter);
		searchAvg.put(1, (double) sumd/(oneArmSearchTime.size() - counter));
		
		counter = 0; sumd = 0;
		for (int i = 0; i < twoArmSearchTime.size(); i++){ 
			if(twoArmSearchTime.get(i) == Long.MAX_VALUE - 1) counter ++;
			else sumd += twoArmSearchTime.get(i);
		}
		timeoutNumber.put(2, counter);
		searchAvg.put(2, (double) sumd/(twoArmSearchTime.size() - counter));
		
		
		
		counter = 0; sumbf = 0; sumd = 0;
		for (int i = 0; i < threeArmSearchTime.size(); i++){ 
			if(threeArmSearchTime.get(i) == Long.MAX_VALUE - 1) counter ++;
			else sumd += threeArmSearchTime.get(i);
		}
		timeoutNumber.put(3, counter);
		searchAvg.put(3, (double) sumd/(threeArmSearchTime.size() - counter));

		counter = 0; sumbf = 0; sumd = 0;
		for (int i = 0; i < fourArmSearchTime.size(); i++){ 
			if(fourArmSearchTime.get(i) == Long.MAX_VALUE - 1) counter ++;
			else sumd += fourArmSearchTime.get(i);
		}
		timeoutNumber.put(4, counter);
		searchAvg.put(4, (double) sumd/(fourArmSearchTime.size() - counter));

		
		
		for (Integer arm : timeoutNumber.keySet()) {			
			timeoutpercentage.put(arm, ((double)timeoutNumber.get(arm)/totalProblem) * 100);
		}
		
		long sumCul = 0; 
		for (int i = 0; i < cts.size(); i++) {
			sumCul += cts.get(i);
		}
		
		
		System.out.println("totalProblem for "+ misplacedNumber + " misplaced objects: "+ totalProblem);
		System.out.println("avarage Culprit Time: " + ((double)sumCul/cts.size()));
		System.out.println("timeout Number: " + timeoutNumber);
		System.out.println("timeout percentage: " + timeoutpercentage);
		System.out.println("searchAvg : " + searchAvg);
		
		
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
