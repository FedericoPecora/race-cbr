package test;

import symbols.SymbolicValueConstraint;
import symbols.SymbolicVariable;
import symbols.SymbolicVariableConstraintSolver;
import symbols.SymbolicVariableNetwork;
import framework.Constraint;
import framework.Variable;

public class IRANTestSymbolicVariableConstraintSolver {
	
	public static void main(String[] args) {
		SymbolicVariableConstraintSolver solver = new SymbolicVariableConstraintSolver();
		Variable[] vars = solver.createVariables(3);
		
		SymbolicVariable var0 = (SymbolicVariable)vars[0];
		var0.setDomain("A", "B", "C");
		
		SymbolicVariable var1 = (SymbolicVariable)vars[1];
		var1.setDomain("A", "B", "C");

		SymbolicVariable var2 = (SymbolicVariable)vars[2];
		var2.setDomain("A", "B", "C");

		SymbolicVariableNetwork.draw(solver.getConstraintNetwork());
		
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
		
		SymbolicValueConstraint con2 = new SymbolicValueConstraint(SymbolicValueConstraint.Type.DIFFERENT);
		con2.setFrom(var1);
		con2.setTo(var2);
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

		SymbolicValueConstraint con3 = new SymbolicValueConstraint(SymbolicValueConstraint.Type.UNARYEQUALS);
		con3.setFrom(var2);
		con3.setTo(var2);
		con3.setUnaryValue("B");
		solver.addConstraint(con3);
		
		Constraint[] cons = {con1, con2, con3}; 
		solver.addConstraints(cons);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(var0);
		System.out.println(var1);
		System.out.println(var2);

	}

}
