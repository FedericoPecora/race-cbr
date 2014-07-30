package test.meta.reachability;
import java.io.*;
public class TestPythonCall {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try{

			//			try{
			//
			//				String prg = "import sys\nprint int(sys.argv[1])+int(sys.argv[2])\n";
			//				BufferedWriter out = new BufferedWriter(new FileWriter("test1.py"));
			//				out.write(prg);
			//				out.close();
			//				int number1 = 10;
			//				int number2 = 32;
			////				Process p = Runtime.getRuntime().exec("python test1.py "+number1+" "+number2);
			//				Process p = Runtime.getRuntime().exec("python test1.py "+number1+" "+number2);
			//				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			//				int ret = new Integer(in.readLine()).intValue();
			//				System.out.println("value is : "+ret);
			//			}catch(Exception e){}


			//			Process p = Runtime.getRuntime().exec("python ~/omplapp/ompl/demos/RigidBodyPlanning.py");
			//			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			//			System.out.println(in.readLine());
			//			String line = null;
			//			while ((line = in.readLine()) != null) {
			//				System.out.println("asj");
			//				System.out.println(line);
			//			}




			//			String prg = "import sys\nprint int(sys.argv[1])+int(sys.argv[2])\n";
			//			BufferedWriter out = new BufferedWriter(new FileWriter("test1.py"));
			//			out.write(prg);
			//			out.close();
			//			int number1 = 10;
			//			int number2 = 32;
			//
			//			ProcessBuilder pb = new ProcessBuilder("python"," ~/test1.py",""+number1,""+number2);
			//			Process p = pb.start();
			//
			//			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			//			int ret = new Integer(in.readLine()).intValue();
			//			System.out.println("value is : "+ret);

			//			String py = "RigidBodyPlanning";
			//			String cmd = "~/omplapp/ompl/demos/";


			String py = "RigidBodyPlanning";
			String cmd = "/home/iran/omplapp/ompl/demos/";

//			String py = "test1";
//			String cmd = "/home/iran/TestPython/";

			
			ProcessBuilder pb = new ProcessBuilder("python", py + ".py");
			//			ProcessBuilder pb = new ProcessBuilder("python","test1.py",""+number1,""+number2);

			pb.directory(new File(cmd));
			pb.redirectErrorStream();
			//...
			Process p = pb.start();


			InputStream is = null;
			try {
				is = p.getInputStream();
				int in = -1;
				while ((in = is.read()) != -1) {
					System.out.print((char)in);
				}
			} finally {
				try {
					is.close();
				} catch (Exception e) {
				}
			}

			//			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			//			System.out.println(in.readLine());

		}catch(Exception e){}



	}

}
