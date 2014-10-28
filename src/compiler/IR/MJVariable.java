package compiler.IR;

public class MJVariable extends IR {

	private MJType type;
	private String name;
	private boolean isField = false; 
	
	public MJVariable() {}
	
	public MJVariable(MJType t, String name) {
		this.type = t;
		this.name = name;
	}

	public MJType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public boolean isField() {
		return this.isField;
	}

	public void setIsField() {
		this.isField = true;
	}
	
}
