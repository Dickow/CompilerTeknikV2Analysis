package compiler.IR;

import java.util.LinkedList;

public class MJProgram extends IR {

	private LinkedList<MJClass> classes;

	public MJProgram() {
		
	}
	
	public MJProgram(LinkedList<MJClass> cdl) {
		this.classes = cdl;
	}

	public LinkedList<MJClass> getClasses() {
		return classes;
	}

}
