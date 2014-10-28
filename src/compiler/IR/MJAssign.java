package compiler.IR;

public class MJAssign extends MJStatement {

	private MJExpression rhs;
	private MJIdentifier lhs;

	public MJAssign() {}
	
	public MJAssign(MJIdentifier lhs, MJExpression rhs) {
		this.rhs = rhs;
		this.lhs = lhs;
	}

	public MJIdentifier getLhs() {
		return this.lhs;
	}

	public MJExpression getRhs() {
		return this.rhs;
	}
	
	public void setLhs(MJIdentifier op) { this.lhs = op; }
	public void setRhs(MJExpression op) { this.rhs = op; }


}
