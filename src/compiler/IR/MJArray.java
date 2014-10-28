	package compiler.IR;

public class MJArray extends MJIdentifier {

	private MJIdentifier array;
	private MJExpression index;

	public MJArray() {}
	
	public MJArray(MJIdentifier array, MJExpression idx) {
		this.array = array;
		this.index = idx;
	}

	public MJIdentifier getArray() {
		return array;
	}

	public MJExpression getIndex() {
		return index;
	}

	public void setArray(MJIdentifier _array) {
		this.array = _array;
		
	}

	public void setIndex(MJExpression _index) {
		this.index = _index;
		
	}

}
