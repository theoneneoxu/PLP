package cop5556sp17;

import java.util.ArrayList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.Before;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.assertEquals;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
//import static cop5556sp17.Scanner.Kind.*;

public class ParserTest {
	private ArrayList<String> keywordAndOperatorList;
	
	@Before
	public void setUp() {
		keywordAndOperatorList = new ArrayList<String>();
		keywordAndOperatorList.add("integer");
		keywordAndOperatorList.add("boolean");
		keywordAndOperatorList.add("image");
		keywordAndOperatorList.add("url");
		keywordAndOperatorList.add("file");
		keywordAndOperatorList.add("frame");
		keywordAndOperatorList.add("while");
		keywordAndOperatorList.add("if");
		keywordAndOperatorList.add("true");
		keywordAndOperatorList.add("false");
		keywordAndOperatorList.add(";");
		keywordAndOperatorList.add(",");
		keywordAndOperatorList.add("(");
		keywordAndOperatorList.add(")");
		keywordAndOperatorList.add("{");
		keywordAndOperatorList.add("}");
		keywordAndOperatorList.add("<-");
		keywordAndOperatorList.add("screenheight");
		keywordAndOperatorList.add("screenwidth");
		keywordAndOperatorList.add("sleep");
		//arrowOp
		keywordAndOperatorList.add("->");
		keywordAndOperatorList.add("|->");
		//filterOp
		keywordAndOperatorList.add("blur");
		keywordAndOperatorList.add("gray");
		keywordAndOperatorList.add("convolve");
		//frameOp
		keywordAndOperatorList.add("show");
		keywordAndOperatorList.add("hide");
		keywordAndOperatorList.add("move");
		keywordAndOperatorList.add("xloc");
		keywordAndOperatorList.add("yloc");
		//imageOp
		keywordAndOperatorList.add("width");
		keywordAndOperatorList.add("height");
		keywordAndOperatorList.add("scale");
		//relOp
		keywordAndOperatorList.add("<");
		keywordAndOperatorList.add("<=");
		keywordAndOperatorList.add(">");
		keywordAndOperatorList.add(">=");
		keywordAndOperatorList.add("==");
		keywordAndOperatorList.add("!=");
		//weakOp
		keywordAndOperatorList.add("+");
		keywordAndOperatorList.add("-");
		keywordAndOperatorList.add("|");
		//strongOp
		keywordAndOperatorList.add("*");
		keywordAndOperatorList.add("/");
		keywordAndOperatorList.add("&");
		keywordAndOperatorList.add("%");
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}

	@Test
	public void testArg0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
	}

	@Test
	public void testArgerror() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}

	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog0 {}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void testProgram1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog1 integer v1, integer v2, url url1 {v1 <- v1 + v2; while (v1 < 100) {sleep 50; v1 <- v1 + 10; id1 -> id2;} }";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}

	//Neo
	@Test
	public void testProgram() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "p1 {} \n p1 {a <- 3;} \n p1 url id1 {} \n p1 file id1, url id2, integer id3 {a <- 10; while (a < 100) {a <- a + 1;}}";
		int elementCount = 4;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < elementCount; i++) {
			parser.program();
		}
		input = "123 file id1 {}";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.program();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(1, exceptionCount);
		input = "file id1 {}";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.program();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(2, exceptionCount);
		input = "p1 file id1 url id2 {}";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.program();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(3, exceptionCount);
		input = "p1 file id1, url {}";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.program();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(4, exceptionCount);
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.program();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(4 + keywordAndOperatorList.size() - 0, exceptionCount);
	}
	
	@Test
	public void testParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "url id1 \n file id2 \n integer id3 \n boolean id4";
		int elementCount = 4;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < elementCount; i++) {
			parser.paramDec();
		}
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.paramDec();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(keywordAndOperatorList.size() - 0, exceptionCount);
	}
	
	@Test
	public void testBlock() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "{} \n {integer id1} \n {integer id1 boolean id2 image id3 frame id4} \n {sleep (id1 + id3);} \n {while (a > b) {c <- c + 1;}}";
		int elementCount = 5;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < elementCount; i++) {
			parser.block();
		}
		input = "{{}}";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.block();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(1, exceptionCount);
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.block();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(1 + keywordAndOperatorList.size() - 0, exceptionCount);
	}
	
	@Test
	public void testDec() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "integer id1 \n boolean id1 \n image id1 \n frame id1";
		int elementCount = 4;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < elementCount; i++) {
			parser.dec();
		}
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.dec();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(keywordAndOperatorList.size() - 0, exceptionCount);
	}

	@Test
	public void testStatement() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "sleep 50; \n sleep (id1 + id3); \n while (a > b) {c <- c + 1;} \n if (a == b) {} \n id1 -> id2; \n id1 |-> gray (id2); \n id1 <- (id2 + 3) * 10;";
		int elementCount = 7;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < elementCount; i++) {
			parser.statement();
		}
		input = "id1";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.statement();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(1, exceptionCount);
		input = "123";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.statement();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(2, exceptionCount);
		input = "sleep 30";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.statement();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(3, exceptionCount);
		input = "id1 -> id2";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.statement();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(4, exceptionCount);
		input = "id1 <- 50";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.statement();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(5, exceptionCount);
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.statement();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(5 + keywordAndOperatorList.size() - 0, exceptionCount);
	}
	
	@Test
	public void testAssign() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "id1 <- 12 \n id1 <- id2 \n id1 <- id2 + id3 * 4 \n id1 <- (id2 - (id3 + 50)) \n id1 <- screenheight";
		int elementCount = 5;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < elementCount; i++) {
			parser.assign();
		}
		input = "id1";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.assign();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(1, exceptionCount);
		input = "id1 <- ;";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.assign();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(2, exceptionCount);
		input = "id1 <- blur (id2)";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.assign();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(3, exceptionCount);
		input = "id1 50";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.assign();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(4, exceptionCount);
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.assign();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(4 + keywordAndOperatorList.size() - 0, exceptionCount);
	}
	
	@Test
	public void testChain() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "id1 -> id2 \n id1 |-> id2 -> id3 |-> id4 \n id1 -> blur (id2) \n id1 -> gray (id2) -> xloc (id3) |-> height";
		int elementCount = 4;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < elementCount; i++) {
			parser.chain();
		}
		input = "id1";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.chain();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(1, exceptionCount);
		input = "id1 -> 12";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.chain();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(2, exceptionCount);
		input = "id1 + id2";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.chain();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(3, exceptionCount);
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.chain();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(3 + keywordAndOperatorList.size() - 0, exceptionCount);
	}

	@Test
	public void testWhileStatement() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "while (1) {} \n while (id1 + id2 * id3 >= 10) {id1 <- 10;} \n while (a > b) {if (true) {c <- c + 1;}} \n while (a > b) {while((c != 10 | d == false) & e) {c -> d |-> blur;}}";
		int elementCount = 4;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < elementCount; i++) {
			parser.whileStatement();
		}
		input = "while () {a <- 3;}";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.whileStatement();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(1, exceptionCount);
		input = "while (a == b;) {a <- 3;}";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.whileStatement();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(2, exceptionCount);
		input = "id1 (true) {a <- 3;}";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.whileStatement();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(3, exceptionCount);
		input = "120 (true) {a <- 3;}";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.whileStatement();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(4, exceptionCount);
		input = "while (a > b) {a <- 3}";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.whileStatement();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(5, exceptionCount);
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.whileStatement();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(5 + keywordAndOperatorList.size() - 0, exceptionCount);	
	}
	
	@Test
	public void testIfStatement() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "if (1) {} \n if (id1 + id2 * id3 >= 10) {id1 <- 10;} \n if (a > b) {if (true) {c <- c + 1;}} \n if (a > b) {while((c != 10 | d == false) & e) {c -> d |-> blur;}}";
		int elementCount = 4;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < elementCount; i++) {
			parser.ifStatement();
		}
		input = "if () {a <- 3;}";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.ifStatement();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(1, exceptionCount);
		input = "if (a == b;) {a <- 3;}";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.ifStatement();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(2, exceptionCount);
		input = "id1 (true) {a <- 3;}";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.ifStatement();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(3, exceptionCount);
		input = "120 (true) {a <- 3;}";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.ifStatement();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(4, exceptionCount);
		input = "if (a > b) {a <- 3}";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.ifStatement();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(5, exceptionCount);
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.ifStatement();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(5 + keywordAndOperatorList.size() - 0, exceptionCount);	
	}

	@Test
	public void testChainElem() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "id1 \n convolve \n blur (id1 + id2) \n gray (id1, id2, (id3 + 1) <= id4) \n show (id1) \n xloc (id1 * 3 + id2) \n width (id1) \n scale ((id1 + 5) % 3, id2 * id3, (id1 + 2) * 3 != 60 + id2)";
		int elementCount = 8;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < elementCount; i++) {
			parser.chainElem();
		}
		input = "120";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.chainElem();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(1, exceptionCount);
		input = "gray ((id1, id2, id3, 50))";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.chainElem();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(2, exceptionCount);
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.chainElem();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(2 + keywordAndOperatorList.size() - 11, exceptionCount);
	}
	
	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "(id1 + id2) \n ((id1 + 5) % 3, id2 * id3, (id1 + 2) * 3 != 60 + id2)";
		int elementCount = 2;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < elementCount; i++) {
			parser.arg();
		}
		//test ¦Å
		input = "";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		parser.arg();
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.arg();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(keywordAndOperatorList.size() - 45, exceptionCount);
	}
	
	@Test
	public void testExpression() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "(id1) \n 10 + id1 * id3 \n id1 > id2 >= id3 < 50 \n (id1 + (10)) >= (id2 * 10 - 10) \n id1 * (id2 + 3 * (id3 % id4 > 5)) + (1 + 2) == id1 + ((60) + (((id5))) * 10)";
		int elementCount = 5;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < elementCount; i++) {
			parser.expression();
		}
		input = "()";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		try {
			parser.expression();
		} catch (SyntaxException e) {
			exceptionCount++;
		}
		assertEquals(1, exceptionCount);
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.expression();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(1 + keywordAndOperatorList.size() - 4, exceptionCount);
	}
	
	@Test
	public void testTerm() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "id1 \n id1 + id2 \n 5 + 8 \n id1 * 50 + id2 - id3 | 60 \n (id1 + id2) * id3 / (20 + id4 * (id1 - id2) > id5)";
		int elementCount = 5;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < elementCount; i++) {
			parser.term();
		}
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.term();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(keywordAndOperatorList.size() - 4, exceptionCount);
	}
	
	@Test
	public void testElem() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "id1 \n 100 \n id1 * id2 \n id1 * id2 * 15 / id1 % id3 \n 0 * 8 \n 0 * id3 / 50 & 20 \n id1 * (id2 + 50 >= 60) \n (50 * 24) / (id1 % id2)";
		int elementCount = 8;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < elementCount; i++) {
			parser.elem();
		}
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.elem();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(keywordAndOperatorList.size() - 4, exceptionCount);
	}
	
	@Test
	public void testFactor() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "id1 12345 true false screenwidth screenheight (id1 + 5 * id2 + id3 % id1 > 100 >= 800)";
		int elementCount = 7;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < elementCount; i++) {
			parser.factor();
		}
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.factor();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(keywordAndOperatorList.size() - 4, exceptionCount);
	}

	//For Neo private functions
	@Test
	public void testArrowOp() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "-> |->";
		int operatorCount = 2;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < operatorCount; i++) {
			parser.arrowOp();
		}
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.arrowOp();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(keywordAndOperatorList.size() - operatorCount, exceptionCount);
	}
	
	@Test
	public void testFilterOp() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "blur gray convolve";
		int operatorCount = 3;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < operatorCount; i++) {
			parser.filterOp();
		}
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.filterOp();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(keywordAndOperatorList.size() - operatorCount, exceptionCount);
	}
	
	@Test
	public void testFrameOp() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "show hide move xloc yloc";
		int operatorCount = 5;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < operatorCount; i++) {
			parser.frameOp();
		}
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.frameOp();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(keywordAndOperatorList.size() - operatorCount, exceptionCount);
	}
	
	@Test
	public void testImageOp() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "width height scale";
		int operatorCount = 3;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < operatorCount; i++) {
			parser.imageOp();
		}
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.imageOp();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(keywordAndOperatorList.size() - operatorCount, exceptionCount);
	}
	
	@Test
	public void testRelOp() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "< <= > >= == !=";
		int operatorCount = 6;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < operatorCount; i++) {
			parser.relOp();
		}
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.relOp();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(keywordAndOperatorList.size() - operatorCount, exceptionCount);
	}
	
	@Test
	public void testWeakOp() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "+ - |";
		int operatorCount = 3;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < operatorCount; i++) {
			parser.weakOp();
		}
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.weakOp();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(keywordAndOperatorList.size() - operatorCount, exceptionCount);
	}
	
	@Test
	public void testStrongOp() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "* / & %";
		int operatorCount = 4;
		int exceptionCount = 0;
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		for (int i = 0; i < operatorCount; i++) {
			parser.strongOp();
		}
		for (int i = 0; i < keywordAndOperatorList.size(); i++){
			scanner = new Scanner(keywordAndOperatorList.get(i));
			scanner.scan();
			parser = new Parser(scanner);
			try {
				parser.strongOp();
			} catch (SyntaxException e) {
				exceptionCount++;
			}
		}
		assertEquals(keywordAndOperatorList.size() - operatorCount, exceptionCount);
	}
	
	//borrowed test cases
	@Test
	 public void testMakeABigNews() throws IllegalCharException, IllegalNumberException, SyntaxException{
	  String input = "sb00 url wwwdsbcom, file fsb, integer sb250, boolean t1{ \n"
	    + "integer sb62 boolean t2 image isb frame sb38\n"
	    + "sleep sb250+sb62; while (t1) {if(t2){sleep sb62 == sb250*2;}}\n"
	    + "fsb->blur (sb250)|->gray (3862250)->convolve (38+62)->show (sb250)->hide (sb62)->move (wwwdsbcom);\n"
	    + "xloc (2+8)->yloc (3+2)->width (100)->height (250)->scale (sb38+sb62);\n"
	    + "fsb <- 3862250+62;}";
	  Parser parser = new Parser(new Scanner(input).scan());
	  parser.parse();
	 }
}
