package compiler.IR;

import java.util.LinkedList;

public class MJMethodCallStmt extends MJStatement {

	
	private MJMethodCallExpr methodCall;

	public MJMethodCallStmt() {}
	
	public MJMethodCallStmt(MJIdentifier object, String methodName, LinkedList<MJExpression> arguments) {

			this.methodCall = new MJMethodCallExpr(object, methodName, arguments);
	}

	public MJMethodCallExpr getMethodCallExpr() {
		return this.methodCall;
	}

	public void setMethodCallExpr(MJMethodCallExpr methodCallExpr) {
		
		this.methodCall = methodCallExpr;
		
	}
}
