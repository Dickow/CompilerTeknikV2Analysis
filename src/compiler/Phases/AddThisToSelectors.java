package compiler.Phases;

import java.util.LinkedList;

import compiler.IR.*;
import compiler.IR.support.IRElementVisitor;
import compiler.Exceptions.VisitorException;

public class AddThisToSelectors extends IRElementVisitor<MJExpression> {

	public static void rewrite(IR ir) {
		AddThisToSelectors rewriter = new AddThisToSelectors();
		try {
			rewriter.visitProgram(ir.getProgram());
		} catch (VisitorException e) {
			e.printStackTrace();
		}
	}

	@Override
	public MJExpression visitProgram(MJProgram e) throws VisitorException {
		
		for (MJClass c : e.getClasses()) {
			IR.currentClass = c;
			visitClass(c);
		}
		
		return null;
	}

	@Override
	public MJExpression visitClass(MJClass e) throws VisitorException {
		
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
	public MJExpression visitMethod(MJMethod e) throws VisitorException {
		for (MJVariable parameter : e.getParameters() ) {
			visitVariable(parameter); 
		}
		visitStatement(e.getBody());
		return null;
	}

	@Override
	public MJExpression visitVariable(MJVariable e) throws VisitorException {
		return null;
	}

	@Override
	public MJExpression visitType(MJType e) throws VisitorException {
		return null;
	}

	@Override
	public MJExpression visitStatement(MJBlock e) throws VisitorException {

		for (MJVariable v : e.getVariables()) {
			visitVariable(v);
		}
		
		for (MJStatement s : e.getStatements()) {
			visitStatement(s);
		}
		
		return null;
	}

	@Override
	public MJExpression visitStatement(MJIf e) throws VisitorException {

		e.setCondition(visitExpression(e.getCondition()));
		visitStatement(e.getIfBlock());
		
		return null;
	}

	@Override
	public MJExpression visitStatement(MJIfElse e) throws VisitorException {

		e.setCondition(visitExpression(e.getCondition()));
		visitStatement(e.getIfBlock());
		visitStatement(e.getElseBlock());
		
		return null;
	}

	@Override
	public MJExpression visitStatement(MJWhile e) throws VisitorException {

		e.setCondition(visitExpression(e.getCondition()));
		visitStatement(e.getBlock());
		
		return null;
	}

	@Override
	public MJExpression visitStatement(MJAssign e) throws VisitorException {
		
		e.setLhs((MJIdentifier)visitExpression(e.getLhs()));
		e.setRhs(visitExpression(e.getRhs()));
		
		return null;
	}

	@Override
	public MJExpression visitStatement(MJPrint e) throws VisitorException {
		
		e.setParameter(visitExpression(e.getParameter()));
		return null;
	}

	@Override
	public MJExpression visitStatement(MJPrintln e) throws VisitorException {
		e.setParameter(visitExpression(e.getParameter()));
		return null;
	}

	@Override
	public MJExpression visitStatement(MJMethodCallStmt e) throws VisitorException {
		e.setMethodCallExpr((MJMethodCallExpr)visitExpression(e.getMethodCallExpr()));
		return null;
	}

	@Override
	public MJExpression visitStatement(MJReturn e) throws VisitorException {
		e.setArgument(visitExpression(e.getArgument()));
		return null;
	}

	@Override
	public MJExpression visitExpression(MJAnd e) throws VisitorException {
		e.setLhs(visitExpression(e.getLhs()));
		e.setRhs(visitExpression(e.getRhs()));
		return e;
	}

	@Override
	public MJExpression visitExpression(MJEqual e) throws VisitorException {
		e.setLhs(visitExpression(e.getLhs()));
		e.setRhs(visitExpression(e.getRhs()));
		return e;
	}

	@Override
	public MJExpression visitExpression(MJLess e) throws VisitorException {
		e.setLhs(visitExpression(e.getLhs()));
		e.setRhs(visitExpression(e.getRhs()));
		return e;
	}

	@Override
	public MJExpression visitExpression(MJPlus e) throws VisitorException {
		e.setLhs(visitExpression(e.getLhs()));
		e.setRhs(visitExpression(e.getRhs()));
		return e;
	}

	@Override
	public MJExpression visitExpression(MJMinus e) throws VisitorException {
		e.setLhs(visitExpression(e.getLhs()));
		e.setRhs(visitExpression(e.getRhs()));
		return e;
	}

	@Override
	public MJExpression visitExpression(MJMult e) throws VisitorException {
		e.setLhs(visitExpression(e.getLhs()));
		e.setRhs(visitExpression(e.getRhs()));
		return e;
	}

	@Override
	public MJExpression visitExpression(MJUnaryMinus e) throws VisitorException {
		e.setArgument(visitExpression(e.getArgument()));
		return e;
	}

	@Override
	public MJExpression visitExpression(MJNegate e) throws VisitorException {
		e.setArgument(visitExpression(e.getArgument()));
		return e;
	}

	@Override
	public MJExpression visitExpression(MJNew e) throws VisitorException {
		return e;
	}

	@Override
	public MJExpression visitExpression(MJNewArray e) throws VisitorException {
		e.setSize(visitExpression(e.getSize()));
		return e;
	}

	@Override
	public MJExpression visitExpression(MJMethodCallExpr e) throws VisitorException {

		if (!e.getTarget().isStatic()) {
			if (!e.hasObject()) {
				MJIdentifier id = new MJIdentifier("this");
				MJType t = MJType.getClassType(IR.currentClass.getName());
				id.setType(t);
				id.setDeclaration(IR.currentMethod.getParameters().getFirst());

				e.setObject(id);
			}
		
			MJIdentifier thisid = e.getObject();			
			e.getArguments().addFirst(thisid);
		}
		
		int position = 0;
		LinkedList<MJExpression> arguments = e.getArguments();
		LinkedList<MJExpression> newArguments = new LinkedList<MJExpression>();
		
		for (MJExpression arg : arguments) {
			arg = (MJExpression)visitExpression(arg);
			newArguments.addLast(arg);
		}
		
		e.setArguments(arguments);
		
		return e;
	}

	@Override
	public MJExpression visitExpression(MJParentheses e) throws VisitorException {
		e.setArgument(visitExpression(e.getArgument()));
		return e;
	}

	@Override
	public MJExpression visitExpression(MJInteger e) throws VisitorException {
		return e;
	}

	@Override
	public MJExpression visitExpression(MJBoolean e) throws VisitorException {
		return e;
	}

	@Override
	public MJExpression visitExpression(MJString e) throws VisitorException {
		return e;
	}

	@Override
	public MJExpression visitExpression(MJIdentifier e) throws VisitorException {

		if (!IR.currentMethod.isStatic() && e.getDeclaration().isField()) {
		
			MJIdentifier i = new MJIdentifier("this");
			MJSelector s = new MJSelector(i, e);
			
			e = s;
		}
		return e;
	}

	@Override
	public MJExpression visitExpression(MJArray e) throws VisitorException {
		e.setArray((MJIdentifier)visitExpression(e.getArray()));
		e.setIndex(visitExpression(e.getIndex()));
		return null;
	}

	@Override
	public MJExpression visitExpression(MJSelector e) throws VisitorException {
		e.setObject((MJIdentifier)visitExpression(e.getObject()));
		e.setField((MJIdentifier)visitExpression(e.getField()));
		return null;
	}

	@Override
	public MJExpression visitExpression(MJNoExpression e) throws VisitorException {
		return e;
	}

}
