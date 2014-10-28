package compiler.Phases;

import compiler.IR.*;
import compiler.IR.support.IRElementVisitor;
import compiler.Exceptions.VisitorException;

public class AddThisArgument extends IRElementVisitor<Object> {

	public static void rewrite(IR ir) {
		AddThisArgument rewriter = new AddThisArgument();
		try {
			rewriter.visitProgram(ir.getProgram());
		} catch (VisitorException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Integer visitProgram(MJProgram e) throws VisitorException {
		
		for (MJClass c : e.getClasses()) {
			IR.currentClass = c;
			visitClass(c);
		}
		
		return null;
	}

	@Override
	public Integer visitClass(MJClass e) throws VisitorException {
		
		for (MJVariable variable : e.getFieldList()) {
			visitVariable(variable);
		}
		
		for (MJMethod method : e.getMethodList()) {
			IR.currentMethod = method;
			visitMethod(method);
		}

		return null;
	}

	@Override
	public Integer visitMethod(MJMethod e) throws VisitorException {
		// add 'this' to virtual methods
		
		if (!e.isStatic()) {
			
			MJType t = MJType.getClassType( IR.currentClass.getName() );
			MJVariable v = new MJVariable(t,"this");
			
			e.getParameters().addFirst(v);
		}
		
		visitStatement(e.getBody());

		return null;
	}

	@Override
	public Object visitVariable(MJVariable e) throws VisitorException {		
		return null;
	}

	@Override
	public Object visitType(MJType e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitStatement(MJBlock e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitStatement(MJIf e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitStatement(MJIfElse e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitStatement(MJWhile e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitStatement(MJAssign e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitStatement(MJPrint e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitStatement(MJPrintln e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitStatement(MJMethodCallStmt e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitStatement(MJReturn e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJAnd e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJEqual e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJLess e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJPlus e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJMinus e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJMult e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJUnaryMinus e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJNegate e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJNew e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJNewArray e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJMethodCallExpr e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJParentheses e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJBoolean e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJInteger e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJString e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJIdentifier e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJArray e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJSelector e) throws VisitorException {
		return null;
	}

	@Override
	public Object visitExpression(MJNoExpression e) throws VisitorException {
		return null;
	}

}
