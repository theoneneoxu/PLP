/**  Important to test the error cases in case the
 * AST is not being completely traversed.
 * 
 * Only need to test syntactically correct programs, or
 * program fragments.
 */

package cop5556sp17;

import java.io.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.AST.ASTNode;

public class TypeCheckVisitorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testAssignmentBoolLit0() throws Exception{
		String input = "p {\nboolean y \ny <- false;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void testAssignmentBoolLitError0() throws Exception{
		String input = "p {\nboolean y \ny <- 3;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}		

	//Neo
	//Live Test with file
	@Test
	public void liveTest() throws Exception{
		String file = "PLP Source Files/Live Test.plp";
		String input, lastInput = "", message;
		while (true) {
			Thread.sleep(100);
			input = readFile(file);
			if (!input.equals(lastInput)) {
				lastInput = input;
				try {
					Scanner scanner = new Scanner(input);
					scanner.scan();
					Parser parser = new Parser(scanner);
					ASTNode program = parser.parse();
					TypeCheckVisitor visitor = new TypeCheckVisitor();
					program.visit(visitor, null);
					message = "Compilation succeeded.";
				} catch (Exception e) {
					message = e.getClass().getSimpleName() + ": " + e.getMessage();
				}
				System.out.println(message);
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
