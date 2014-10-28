package compiler.Exceptions;

public class XmlParseException extends Exception {

	private static final long serialVersionUID = 1L;

	public XmlParseException() {
		super("Was not able to parse loaded xml-file");
	}
	
	public XmlParseException(String msg, int linenumber) {
		super(msg + " at line " + linenumber);
	}
}
