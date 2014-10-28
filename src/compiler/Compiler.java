package compiler;

import compiler.Exceptions.*;
import compiler.Phases.AddThisToSelectors;
import compiler.Phases.Analysis;
import compiler.Phases.PrettyPrint;
import compiler.Phases.AddThisArgument;
import compiler.IR.*;
import compiler.IR.support.Deserializer;
import compiler.IR.support.Serializer;

public class Compiler {

	private static String filename;
	private static String outputfilename;
		
	private static boolean debug;
	
	private static String[] arguments={ "" };
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Compiler c = new Compiler(args);
			c.analyseIR();
		} 
		catch (CompilerError e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}

	public Compiler(String[] args) throws CompilerError {
		//
		// here we should inspect the arguments
		//

		if (args.length==0) {
			help();
		}

		filename = null;
		debug = false;

		for (int i=0;i<args.length;i++) {
			String arg = args[i];
			if (arg.charAt(0)!='-') {

				//
				// store the filename
				//

				filename = args[i];
				outputfilename = filename.substring(0, filename.lastIndexOf('.'))+"_analysed.xml";
			} else {
				if (arg.equals("-v")) {
					debug=true;
					continue;
				}
				if (arg.equals("-o")) {

					if (i==args.length) {
						throw new CompilerError("argument for -o is missing");
					}
					
					outputfilename = args[i++];
					continue;
				}
				if (arg.equals("--")) {
					// from here on the arguments should go to the program
					
					Compiler.arguments = new String[(args.length - i - 1)];
					for (int ai = i+1; ai<args.length; ai++) {
						Compiler.arguments[ai-i-1] = args[ai];
					}
					break;
				}
			}
		}
		
		if (filename == null) {
			throw new CompilerError("No filename specified");
		}

	}

	public void analyseIR() throws CompilerError
	{
		IR ir;
		
		System.out.println("Deserializing... ");
		try {
			Deserializer deserializer = new Deserializer();
			ir = (IR) deserializer.deserialize(filename);
		} catch (Exception e) {
			throw new CompilerError("Error while deserializing "+e.getMessage());
		}
		System.out.println("done.");

		if (isDebug()) {
			PrettyPrint.print(ir);
		}
		
		System.out.println("Rewriting 1... (add this arguments)");
		AddThisArgument.rewrite(ir);
		System.out.println("done.");

		if (isDebug()) {
			PrettyPrint.print(ir);
		}
		
		System.out.println("Analysing... ");

		try {
			Analysis.analyse(ir);
		} catch (TypeCheckerException e) {
			throw new CompilerError("TypeCheckError "+e.getMessage());
		}

		System.out.println("done.");
		
		System.out.println("Rewriting 2... (add this to selectors)");
		AddThisToSelectors.rewrite(ir);
		System.out.println("done.");

		if (isDebug()) {
			PrettyPrint.print(ir);
		}
				
		System.out.println("Serializing... ");
		try {
			Serializer serializer = new Serializer();
			serializer.serialize(ir, outputfilename);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CompilerError("Error while serializing "+e.getMessage());
		}
		System.out.println("done.");
	}
	
	private void help() {
		
		System.err.println("\n");
		System.err.println("MiniJava compiler\n");
		System.err.println("====================\n\n");
		System.err.println("Invoke with \"java -jar path/to/jar/file \" or via GUI.");
		System.err.println("arguments: [-v] filename\n\n");
		System.err.println("  -v            be verbose\n");
		System.err.println("  filename      source filename\n");
		System.exit(-1);
	}

	public static String getFilename() {
		return filename;
	}

	public static String getOutputfilename() {
		return outputfilename;
	}

	public static String[] getArguments() {
		return arguments;
	}

	public static boolean isDebug() {
		return debug;
	}
	
}
