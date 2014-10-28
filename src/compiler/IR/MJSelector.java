package compiler.IR;

public class MJSelector extends MJIdentifier {

	private MJIdentifier object;
	private MJIdentifier field;

	public MJSelector() {}
	
	public MJSelector(MJIdentifier t, MJIdentifier field) {
		this.object = t;
		this.field = field;
	}

	public MJIdentifier getObject() {
		return object;
	}

	public MJIdentifier getField() {
		return field;
	}

	public void setField(MJIdentifier _field) {
		this.field = _field;
	}
	
	public void setObject(MJIdentifier _object) {
		this.object = _object;
	}

}
