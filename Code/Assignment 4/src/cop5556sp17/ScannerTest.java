package cop5556sp17;

import static cop5556sp17.Scanner.Kind.SEMI;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Kind;

public class ScannerTest {

	@Rule
    public ExpectedException thrown = ExpectedException.none();


	
	@Test
	public void testEmpty() throws IllegalCharException, IllegalNumberException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
	}

	@Test
	public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = ";;;";
		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(SEMI, token.kind);
		assertEquals(0, token.pos);
		String text = SEMI.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(SEMI, token1.kind);
		assertEquals(1, token1.pos);
		assertEquals(text.length(), token1.length);
		assertEquals(text, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(SEMI, token2.kind);
		assertEquals(2, token2.pos);
		assertEquals(text.length(), token2.length);
		assertEquals(text, token2.getText());
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
	}
	
	
	/**
	 * This test illustrates how to check that the Scanner detects errors properly. 
	 * In this test, the input contains an int literal with a value that exceeds the range of an int.
	 * The scanner should detect this and throw and IllegalNumberException.
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
		String input = "12345678901";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scanner.scan();		
	}
	
	@Test
	public void neoTest() throws IllegalCharException, IllegalNumberException {
		String input = "neo is \ntesting \n integer 12345+-> true";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		String text;
		Scanner.Token token;
		//test start
		text = "neo";
		token = scanner.nextToken();
		assertEquals(token.kind, Kind.IDENT);
		assertEquals(token.pos, 0);
		assertEquals(token.length, text.length());
		assertEquals(token.getText(), text);		
		assertEquals(token.getLinePos().line, 0);
		assertEquals(token.getLinePos().posInLine, 0);
		text = "is";
		token = scanner.nextToken();
		assertEquals(token.kind, Kind.IDENT);
		assertEquals(token.pos, 4);
		assertEquals(token.length, text.length());
		assertEquals(token.getText(), text);		
		assertEquals(token.getLinePos().line, 0);
		assertEquals(token.getLinePos().posInLine, 4);
		text = "testing";
		token = scanner.nextToken();
		assertEquals(token.kind, Kind.IDENT);
		assertEquals(token.pos, 8);
		assertEquals(token.length, text.length());
		assertEquals(token.getText(), text);		
		assertEquals(token.getLinePos().line, 1);
		assertEquals(token.getLinePos().posInLine, 0);
		text = "integer";
		token = scanner.nextToken();
		assertEquals(token.kind, Kind.KW_INTEGER);
		assertEquals(token.pos, 18);
		assertEquals(token.length, text.length());
		assertEquals(token.getText(), text);		
		assertEquals(token.getLinePos().line, 2);
		assertEquals(token.getLinePos().posInLine, 1);
		text = "12345";
		token = scanner.nextToken();
		assertEquals(token.kind, Kind.INT_LIT);
		assertEquals(token.pos, 26);
		assertEquals(token.length, text.length());
		assertEquals(token.getText(), text);		
		assertEquals(token.getLinePos().line, 2);
		assertEquals(token.getLinePos().posInLine, 9);
		text = "+";
		token = scanner.nextToken();
		assertEquals(token.kind, Kind.PLUS);
		assertEquals(token.pos, 31);
		assertEquals(token.length, text.length());
		assertEquals(token.getText(), text);		
		assertEquals(token.getLinePos().line, 2);
		assertEquals(token.getLinePos().posInLine, 14);
		text = "->";
		token = scanner.nextToken();
		assertEquals(token.kind, Kind.ARROW);
		assertEquals(token.pos, 32);
		assertEquals(token.length, text.length());
		assertEquals(token.getText(), text);		
		assertEquals(token.getLinePos().line, 2);
		assertEquals(token.getLinePos().posInLine, 15);
		text = "true";
		token = scanner.nextToken();
		assertEquals(token.kind, Kind.KW_TRUE);
		assertEquals(token.pos, 35);
		assertEquals(token.length, text.length());
		assertEquals(token.getText(), text);		
		assertEquals(token.getLinePos().line, 2);
		assertEquals(token.getLinePos().posInLine, 18);
	}

//TODO  more tests
	
}
