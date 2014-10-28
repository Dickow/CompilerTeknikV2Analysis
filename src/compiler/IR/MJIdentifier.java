package compiler.IR;

public class MJIdentifier extends MJExpression {

	private String name;
	private MJVariable declaration;

	public MJIdentifier() {
	}

	public MJIdentifier(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public MJVariable getDeclaration() { return this.declaration; }
	public void setDeclaration(MJVariable decl) {
		this.declaration = decl;
	}

}
