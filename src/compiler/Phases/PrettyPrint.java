package compiler.Phases;

import compiler.IR.*;
import compiler.IR.support.IRElementVisitor;
import compiler.Exceptions.VisitorException;

import java.io.PrintStream;


public class PrettyPrint extends IRElementVisitor<Integer> {

	public class PrettyPrinter {

		private PrintStream ps;
		private int indent=0;
		private int width=2;
		private boolean isindented = false;
		
		public PrettyPrinter() {
			this.ps = System.out;
		}
		
		public PrettyPrinter(PrintStream ps) {
			this.ps = ps;
		}
		
		public void in() {
			indent+=width;
		}
		
		public void out() {
			indent-=width;
			if (indent<0) {
				indent=0;
			}
		}
		
		private void indent() {
			if (isindented) return;
			for (int i=0;i<this.indent;i++) ps.print(' ');
			isindented=true;
		}
		
		public void println(String s) {
			this.indent();
			ps.println(s);
			isindented=false;
		}

		public void print(String s) {
			this.indent();
			ps.print(s);
		}
	}

	private PrettyPrinter pp = new PrettyPrinter();
	
	@Override
	public Integer visitProgram(MJProgram e) throws VisitorException {
		
		for (MJClass c : e.getClasses()) {
			visitClass(c);
		}
		
		return null;
	}

	@Override
	public Integer visitClass(MJClass e) throws VisitorException {
		
		pp.print("class ");
		pp.print(e.getName());
		pp.println(" {");
		pp.in();
		for (MJVariable attribute : e.getFieldList()) {
			visitVariable(attribute);
			pp.println(";");
		}
		for (MJMethod method : e.getMethodList()) {
			visitMethod(method);
		}
		pp.out();
		pp.println("}");

		return null;
	}

	@Override
	public Integer visitVariable(MJVariable e) throws VisitorException {
		visitType(e.getType());
		pp.print(" ");
		pp.print(e.getName());

		return null;
	}

	@Override
	public Integer visitType(MJType e) throws VisitorException {
		pp.print(e.toString());
		return null;
	}

	@Override
	public Integer visitMethod(MJMethod e) throws VisitorException {
		visitType(e.getReturnType());
		pp.print(" ");
		pp.print(e.getName());
		pp.print("(");
		boolean first = true;
		for (MJVariable parameter : e.getParameters()) {
			if (!first) {
				pp.print(", ");
			} else {
				first = false;
			}
			visitVariable(parameter);
		}
		pp.print(") ");
		visitStatement(e.getBody());
		return null;
	}

	@Override
	public Integer visitStatement(MJBlock e) throws VisitorException {
		pp.println("{");
		pp.in();
		for (MJVariable variable : e.getVariables()) {
			visitVariable(variable);
			pp.println(";");
		}
		for (MJStatement statement : e.getStatements()) {
			visitStatement(statement);
		}
		pp.out();
		pp.println("}");
		return null;
	}

	@Override
	public Integer visitStatement(MJIf e) throws VisitorException {
		pp.print("if");
		pp.print(" (");
		visitExpression(e.getCondition());
		pp.print(" ) ");
		visitStatement(e.getIfBlock());
		return null;
	}

	@Override
	public Integer visitStatement(MJIfElse e) throws VisitorException {
		visitStatement((MJIf)e);
		pp.print(" else ");
		visitStatement(e.getElseBlock());
		pp.println("");
		return null;
	}

	@Override
	public Integer visitStatement(MJWhile e) throws VisitorException {
		pp.print("while");
		pp.print(" (");
		visitExpression(e.getCondition());
		pp.print(" ) ");
		visitStatement(e.getBlock());
		return null;
	}

	@Override
	public Integer visitStatement(MJAssign e) throws VisitorException {
		visitExpression(e.getLhs());
		pp.print(" = ");
		visitExpression(e.getRhs());
		pp.println(";");
		return null;
	}

	@Override
	public Integer visitStatement(MJPrint e) throws VisitorException {
		pp.print("System.out.print(");
		visitExpression(e.getParameter());
		pp.print(")");
		pp.println(";");
		return null;
	}

	@Override
	public Integer visitStatement(MJPrintln e) throws VisitorException {
		pp.print("System.out.println(");
		visitExpression(e.getParameter());
		pp.print(")");
		pp.println(";");
		return null;
	}

	@Override
	public Integer visitStatement(MJMethodCallStmt e) throws VisitorException {
		visitExpression(e.getMethodCallExpr());
		pp.println(";");
		return null;
	}

	@Override
	public Integer visitStatement(MJReturn e) throws VisitorException {
		pp.print("return(");
		visitExpression(e.getArgument());
		pp.print(")");
		pp.println(";");
		return null;
	}

	@Override
	public Integer visitExpression(MJAnd e) throws VisitorException {
		visitExpression(e.getLhs());
		pp.print(" && ");
		visitExpression(e.getRhs());
		return null;
	}

	@Override
	public Integer visitExpression(MJEqual e) throws VisitorException {
		visitExpression(e.getLhs());
		pp.print(" == ");
		visitExpression(e.getRhs());
		return null;
	}

	@Override
	public Integer visitExpression(MJLess e) throws VisitorException {
		visitExpression(e.getLhs());
		pp.print(" < ");
		visitExpression(e.getRhs());
		return null;
	}

	@Override
	public Integer visitExpression(MJPlus e) throws VisitorException {
		visitExpression(e.getLhs());
		pp.print(" + ");
		visitExpression(e.getRhs());
		return null;
	}

	@Override
	public Integer visitExpression(MJMinus e) throws VisitorException {
		visitExpression(e.getLhs());
		pp.print(" - ");
		visitExpression(e.getRhs());
		return null;
	}

	@Override
	public Integer visitExpression(MJMult e) throws VisitorException {
		visitExpression(e.getLhs());
		pp.print(" * ");
		visitExpression(e.getRhs());
		return null;
	}

	@Override
	public Integer visitExpression(MJUnaryMinus e) throws VisitorException {
		pp.print("-");
		visitExpression(e.getArgument());
		return null;
	}

	@Override
	public Integer visitExpression(MJNegate e) throws VisitorException {
		pp.print("!");
		visitExpression(e.getArgument());
		return null;
	}

	@Override
	public Integer visitExpression(MJNew e) throws VisitorException {
		pp.print("new ");
		pp.print(e.getClassName());
		pp.print("()");
		return null;
	}

	@Override
	public Integer visitExpression(MJNewArray e) throws VisitorException {
		pp.print("new ");
		pp.print("int");
		pp.print("[");
		visitExpression(e.getSize());
		pp.print("]");
		return null;
	}

	@Override
	public Integer visitExpression(MJMethodCallExpr e) throws VisitorException {
		if (e.hasObject()) {
			visitExpression(e.getObject());
			pp.print(".");
		}
		pp.print(e.getMethodName());
		pp.print("(");
		boolean first = true;
		for (MJExpression argument : e.getArguments()) {
			if (!first) {
				pp.print(", ");
			} else {
				first = false;
			}
			visitExpression(argument);
		}
		pp.print(")");
		return null;
	}

	@Override
	public Integer visitExpression(MJParentheses e) throws VisitorException {
		pp.print("(");
		visitExpression(e.getArgument());
		pp.print(")");
		return null;
	}

	@Override
	public Integer visitExpression(MJBoolean e) throws VisitorException {
		if (e.isTrue()) {
			pp.print("true");
		} else {
			pp.print("false");
		}
		return null;
	}

	@Override
	public Integer visitExpression(MJInteger e) throws VisitorException {
		pp.print(Integer.toString(e.getValue()));
		return null;
	}

	@Override
	public Integer visitExpression(MJString e) throws VisitorException {
		pp.print(e.getValue());
		return null;
	}

	@Override
	public Integer visitExpression(MJIdentifier e) throws VisitorException {
		if (e instanceof MJSelector) {
			return visitExpression((MJSelector)e);
		} else {
			pp.print(e.getName());
			return null;
		}
	}

	@Override
	public Integer visitExpression(MJArray e) throws VisitorException {
		visitExpression(e.getArray());
		pp.print("[");
		visitExpression(e.getIndex());
		pp.print("]");
		return null;
	}

	@Override
	public Integer visitExpression(MJSelector e) throws VisitorException {
		visitExpression(e.getObject());
		pp.print(".");
		visitExpression(e.getField());
		return null;
	}

	@Override
	public Integer visitExpression(MJNoExpression e) throws VisitorException {
		return null;
	}

	public static void print(IR ir) {
		try {
			new PrettyPrint().visitProgram(ir.getProgram());
		} catch (VisitorException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
