package compiler.IR;

import java.util.LinkedList;

public class MJMethodCallExpr extends MJExpression {

	private boolean hasObject = false;
	private MJIdentifier object;
	private String methodName;
	private LinkedList<MJExpression> arguments;
	
	private MJMethod target;

	public MJMethodCallExpr() {}
	
	public MJMethodCallExpr(MJIdentifier object, String methodName,
			LinkedList<MJExpression> arguments) {
		if (object != null) {
			this.object = object;
			this.hasObject = true;
		}
		this.methodName = methodName;
		this.arguments = arguments;
	}

	public boolean hasObject() {
		return this.hasObject;
	}
	
	public MJIdentifier getObject() { return this.object; }
	public void setObject(MJIdentifier id) {
		this.object = id;
	}

	public String getMethodName() {
		return this.methodName;
	}
	
	public LinkedList<MJExpression> getArguments() { return this.arguments; }

	public MJMethod getTarget() {
		return this.target;
	}

	public void setTarget(MJMethod method) {
		this.target = method;
	}

	public void setArguments(LinkedList<MJExpression> _arguments) {
		this.arguments = _arguments;
		
	}
	
}
