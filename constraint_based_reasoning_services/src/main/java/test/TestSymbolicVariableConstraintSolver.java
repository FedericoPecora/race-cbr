package test;

import org.metacsp.multi.symbols.SymbolicValueConstraint;
import org.metacsp.multi.symbols.SymbolicVariable;
import org.metacsp.multi.symbols.SymbolicVariableConstraintSolver;
import org.metacsp.framework.ConstraintNetwork;
import org.metacsp.framework.Variable;

public class TestSymbolicVariableConstraintSolver {
	
	public static void main(String[] args) {
		SymbolicVariableConstraintSolver solver = new SymbolicVariableConstraintSolver(new String[] {"A","B","C","D","E","F","G"},100);
		Variable[] vars = solver.createVariables(3);
		
		SymbolicVariable var0 = (SymbolicVariable)vars[0];
		var0.setDomain("A", "B", "C");
		
		SymbolicVariable var1 = (SymbolicVariable)vars[1];
		var1.setDomain("G", "B", "C", "D");

		SymbolicVariable var2 = (SymbolicVariable)vars[2];
		var2.setDomain("alpha", "beta", "gamma");

		ConstraintNetwork.draw(solver.getConstraintNetwork());
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(var0);
		System.out.println(var1);
		System.out.println(var2);
		
		System.out.println("---------------------");
		
		SymbolicValueConstraint con1 = new SymbolicValueConstraint(SymbolicValueConstraint.Type.EQUALS);
		con1.setFrom(var0);
		con1.setTo(var1);
		solver.addConstraint(con1);
				
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(var0);
		System.out.println(var1);
		System.out.println(var2);

		System.out.println("---------------------");
		
		SymbolicValueConstraint con2 = new SymbolicValueConstraint(SymbolicValueConstraint.Type.UNARYEQUALS);
		con2.setFrom(var1);
		con2.setTo(var1);
		con2.setUnaryValue(new boolean[] {false,false,true,false,false,false});
		solver.addConstraint(con2);
				
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(var0);
		System.out.println(var1);
		System.out.println(var2);
		
		/***/
		System.out.println("---------------------");
		
		SymbolicValueConstraint con3 = new SymbolicValueConstraint(SymbolicValueConstraint.Type.EQUALS);
		con3.setFrom(var1);
		con3.setTo(var2);
		if (!solver.addConstraint(con3)) System.out.println("NO SOLUTION!");
			
		System.out.println(var0);
		System.out.println(var1);
		System.out.println(var2);
		
		System.out.println("---------------------");

		/***/
		

		solver.removeConstraint(con2);
				
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(var0);
		System.out.println(var1);
		System.out.println(var2);
		
		//solver.removeConstraint(con2);
		System.out.println(solver.getDescription());
		System.out.println(var0.getDescription());

	}

}
