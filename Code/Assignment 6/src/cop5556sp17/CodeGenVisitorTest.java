
package cop5556sp17;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.After;
import org.junit.Before;
//import org.junit.Rule;
import org.junit.Test;
//import org.junit.rules.ExpectedException;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.Program;

public class CodeGenVisitorTest {

	@Before
	public void initLog(){
	if (devel || grade) PLPRuntimeLog.initLog();
	}

	@After
	public void printLog(){
	System.out.println(PLPRuntimeLog.getString());
	}
	
	static final boolean doPrint = true;
	static void show(Object s) {
		if (doPrint) {
			System.out.println(s);
		}
	}

	boolean devel = false;
	boolean grade = false;

	@Test
	public void emptyProg() throws Exception {
		//scan, parse, and type check the program
		String progname = "emptyProg";
		String input = progname + "  {}";		
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		show(program);
		
		//generate code
		CodeGenVisitor cv = new CodeGenVisitor(devel,grade,null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
		
		//output the generated bytecode
		CodeGenUtils.dumpBytecode(bytecode);
		
		//write byte code to file 
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		System.out.println("Wrote classfile to " + classFileName);
		
		// directly execute bytecode
		String[] args = new String[0]; //create command line argument array to initialize params, none in this case
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}
	
	//Neo
	@Test @SuppressWarnings("unused")
	public void commandLineTest() throws Exception{
		String command = "java NeoTest 5 17 true false Images/image1.jpg Images/image2.jpg Images/output1.jpg https://s3.amazonaws.com/glocal-files/image/bi+(65).jpg https://s3.amazonaws.com/glocal-files/image/bi+(100).jpg";
	}
	
	//input command line arguments are hard coded
	@Test
	public void liveTest() throws Exception{
		String file = "PLP Source Files/Live Test.plp";
		String input, lastInput = "", message = "";
		System.out.println("Live test started.");
		while (true) {
			Thread.sleep(100);
			input = readFile(file);
			if (!input.equals(lastInput)) {
				lastInput = input;
				try {
					initLog();	//clear previous log for assignment 6
					Scanner scanner = new Scanner(input);
					scanner.scan();
					Parser parser = new Parser(scanner);
					ASTNode program = parser.parse();
					TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
					program.visit(typeCheckVisitor, null);
					message = "Compilation check passed.";
					System.out.println("\n" + message);
					//generate code
					CodeGenVisitor codeGenVisitor = new CodeGenVisitor(devel, grade, null);
					byte[] bytecode = (byte[]) program.visit(codeGenVisitor, null);
					//output the generated bytecode
					//CodeGenUtils.dumpBytecode(bytecode);
					//write byte code to file 
					String programName = ((Program) program).getName();
					String classFileName = "bin/" + programName + ".class";
					OutputStream output = new FileOutputStream(classFileName);
					output.write(bytecode);
					output.close();
					System.out.println("Wrote classfile to " + classFileName);
					//command line arguments
					String[] args = new String[9];	//create command line arguments array to initialize params
					args[0] = "50";
					args[1] = "100";
					args[2] = "true";
					args[3] = "false";
					args[4] = "Images/image1.jpg";
					args[5] = "Images/image2.jpg";
					args[6] = "Images/output1.jpg";
					args[7] = "https://s3.amazonaws.com/glocal-files/image/bi+(65).jpg";
					args[8] = "https://s3.amazonaws.com/glocal-files/image/bi+(100).jpg";
					//execute bytecode
					Runnable instance = CodeGenUtils.getInstance(programName, bytecode, args);
					instance.run();
					//live test will exit when close image window
					printLog();		//print log output for assignment 6
				} catch (Exception e) {
					message = e.getClass().getSimpleName() + ": " + e.getMessage();
					System.out.println("\n" + message);
				}
			}
		}
	}

	public String readFile(String file){
		String input = "", lineText;
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			while ((lineText = reader.readLine()) != null) {
				input += lineText + "\n";
			}
        } catch (FileNotFoundException e) {
			System.out.println("Cannot find file: " + file);
        } catch (IOException e) {
			System.out.println("IO Exception while reading file: " + file);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {

            }
        }
		return input;
	}	
	
}
