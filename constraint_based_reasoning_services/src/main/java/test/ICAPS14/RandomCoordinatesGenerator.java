package test.ICAPS14;

import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;



public class RandomCoordinatesGenerator {

	/**
	 * tatollexperiment parameter defines number of generated experminet
	 * also the coordinate of table handcoded, and coordinates of cup is manually undefined (0, 0, 0, 0)
	 */
	
	static int totallExperiment = 1000;
	static String PATH = "/home/iran/Desktop/benchmark/testCase14/coordinateGenerator/";
	static int Table_size_x = 120;
	static int Table_size_y = 120;	
	public static void main(String[] args) {

		HashMap<String, Rectangle> objsizes = new HashMap<String, Rectangle>();
//		objsizes.put("table", new Rectangle(120, 120));
//		objsizes.put("cup", new Rectangle(5,5));
		
		objsizes.put("book", new Rectangle(10, 10));
		objsizes.put("monitor", new Rectangle(45, 15));
		objsizes.put("keyboard", new Rectangle(40, 20));
//		objsizes.put("notebook", new Rectangle(15, 20));
//		objsizes.put("pen", new Rectangle(1,18));
		objsizes.put("penHolder", new Rectangle(10,5));

		int n = 1, accepted = 0;
		Vector<HashMap<String, Rectangle>> recSet = new Vector<HashMap<String,Rectangle>>();
		
		for (int i = 0; i < n; i++) {
			HashMap<String, Rectangle> temp  = new HashMap<String, Rectangle>();

			//generate position randomly
			for (String st : objsizes.keySet()) {
				Rectangle rec = null;
				do{
					Random random = new Random();
					rec = new Rectangle(showRandomInteger(1, 119, random), showRandomInteger(1, 119, random), objsizes.get(st).width, objsizes.get(st).height);	
				}while(!withinTheRefrenceFrame(rec));
				temp.put(st, rec);
			}
			//prune the overlapped rectangle
			if(isRectangleOverlapped(temp)){
				recSet.add(temp);
				accepted++;
			}
			if(accepted == totallExperiment)
				break;
			else
				n++;
		}
		
		for (int i = 0; i < recSet.size(); i++) {
			String strfile = "";
			BufferedWriter bw = null;

			for (String str : recSet.get(i).keySet()) {
				//xl, xu, yl, yu
				strfile += str + " " +recSet.get(i).get(str).x + " " + (recSet.get(i).get(str).x + recSet.get(i).get(str).width) + " " +
						+ recSet.get(i).get(str).y + " " +(recSet.get(i).get(str).y + recSet.get(i).get(str).height) +
						"\n";
				
			}
			strfile += "cup" + " " + 0 + " " + 0 + " " +  0 + " " + 0 +"\n";
			strfile += "table" + " " + 0 + " " + 120 + " " +  0 + " " + 120 +"\n";
//			strfile += "keyboard" + " " + 56 + " " + 106 + " " +  20 + " " + 40 +"\n";
			strfile += "++++++";
			try{

				bw = new BufferedWriter(new FileWriter(PATH+i+".dat", false));
				bw.write(strfile);
				bw.newLine();
				bw.flush();

			}				
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
			

		}

	}
	private static boolean withinTheRefrenceFrame(Rectangle rec) {

//		Table_size_y
		if(((rec.x + rec.width) < 120) && ((rec.y + rec.height) < 120))
			return true;
		
		return false;
	}
	private static boolean isRectangleOverlapped(HashMap<String, Rectangle> temp) {

		
		for (String st1 : temp.keySet()){
			for (String st2 : temp.keySet()) {
				if(st1.compareTo(st2) == 0)
					continue;
				
				if(temp.get(st1).intersects(temp.get(st2)))
					return false;
			}
		}


		return true;
	}
	private static int showRandomInteger(int aStart, int aEnd, Random aRandom){
		if (aStart > aEnd) {
			throw new IllegalArgumentException("Start cannot exceed End.");
		}
		//get the range, casting to long to avoid overflow problems
		long range = (long)aEnd - (long)aStart + 1;
		// compute a fraction of the range, 0 <= frac < range
		long fraction = (long)(range * aRandom.nextDouble());
		int randomNumber =  (int)(fraction + aStart);
		return randomNumber;

	}

}
