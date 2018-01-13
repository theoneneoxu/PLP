package cop5556sp17;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;


public class ParserTest_Extra1 {

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
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "  (3,5) ";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.arg();
	}

	@Test
	public void testArgerror() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,) ";
		Parser parser = new Parser(new Scanner(input).scan());
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
	public void testEmpty() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void testElem0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "ident1 * ident2 / 3345 & true % false / screenwidth * screenheight";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.elem();
	}
	
	@Test
	public void testTerm0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "ident1 * ident2 + 3783 & 2893 - true * false | screenwidth / screenheight";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.term();
	}
	
	@Test
	public void testExpression0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "ident1 * ident2 + 3783 & 2893 < true * false | screenwidth / screenheight <= true * false | screenwidth / screenheight > true * false | screenwidth / screenheight >= true * false | screenwidth / screenheight == true * false | screenwidth / screenheight != true * false | screenwidth / screenheight";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.term();
	}
	
	@Test
	public void testArg0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "  ( ident1 * ident2 + 3783 & 2893 < true * false | screenwidth / screenheight <= true * false | screenwidth / screenheight > true * false | screenwidth / screenheight >= true * false | screenwidth / screenheight == true * false | screenwidth / screenheight != true * false | screenwidth / screenheight , ident1 * ident2 + 3783 & 2893 < true * false | screenwidth / screenheight <= true * false | screenwidth / screenheight > true * false | screenwidth / screenheight >= true * false | screenwidth / screenheight == true * false | screenwidth / screenheight != true * false | screenwidth / screenheight, ident1 * ident2 + 3783 & 2893 < true * false | screenwidth / screenheight <= true * false | screenwidth / screenheight > true * false | screenwidth / screenheight >= true * false | screenwidth / screenheight == true * false | screenwidth / screenheight != true * false | screenwidth / screenheight ) ";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.arg();
		String input2 = "";
		Parser parser2 = new Parser(new Scanner(input2).scan());
		//thrown.expect(Parser.SyntaxException.class);
		parser2.arg();
	}
	
	@Test
	public void testChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "ident0 -> blur |-> gray |-> convolve |-> show ( id1 * id2 + 3 & 2 < true * false | screenwidth / screenheight, id1 * id2 + 3 & 2 < true * false | screenwidth / screenheight) |-> hide -> move -> xloc -> yloc -> width -> height ( id1 * id2 + 3 & 2 < true * false | screenwidth / screenheight) |-> scale";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.chain();
	}
	
	@Test
	public void testWhileStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "while(id1 * id2 + 3 & 2 < true * false | screenwidth / screenheight) {}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.statement();
	}
	
	@Test
	public void testIfStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "if(id1 * id2 + 3 & 2 < true * false | screenwidth / screenheight) {integer ident0 boolean ident1 image ident2 frame ident3}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.statement();
	}
	
	@Test
	public void testAssign() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "ident1 <- ident1 * ident2 + 3783 & 2893 < true * false | screenwidth / screenheight <= true * false | screenwidth / screenheight > true * false | screenwidth / screenheight >= true * false | screenwidth / screenheight == true * false | screenwidth / screenheight != true * false | screenwidth / screenheight;";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.statement();
	}
	
	@Test
	public void testSleep() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "sleep ident1 * ident2 + 3783 & 2893 < true * false | screenwidth / screenheight <= true * false | screenwidth / screenheight > true * false | screenwidth / screenheight >= true * false | screenwidth / screenheight == true * false | screenwidth / screenheight != true * false | screenwidth / screenheight;";
        //String input = "sleep id2 + 23;";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.statement();
	}
	
	@Test
	public void testChainInStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "ident0 -> blur |-> gray |-> convolve |-> show ( id1 * id2 + 3 & 2 < true * false | screenwidth / screenheight, id1 * id2 + 3 & 2 < true * false | screenwidth / screenheight) |-> hide -> move -> xloc -> yloc -> width -> height ( id1 * id2 + 3 & 2 < true * false | screenwidth / screenheight) |-> scale;";
        //String input = "blur->gray;";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.statement();
	}
	
	@Test
	public void testDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "integer ident0";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.dec();
	}
	
	@Test
	public void testBlock() throws IllegalCharException, IllegalNumberException, SyntaxException {
        //String input = "{ident0 -> blur ( id1 * id2 + 3 & 2 < true * false | screenwidth / screenheight) |-> gray |-> convolve ( id1 * id2 + 3 & 2 < true * false | screenwidth / screenheight) |-> show; integer ident0 boolean ident1 image ident2 frame ident3 sleep ident1 * ident2 + 3783 & 2893 < true * false | screenwidth / screenheight <= true * false | screenwidth / screenheight > true * false | screenwidth / screenheight >= true * false | screenwidth / screenheight == true * false | screenwidth / screenheight != true * false | screenwidth / screenheight;}";
		String input = "{ident0 -> blur ( id1 * id2 + 3 & 2 < true * false | screenwidth / screenheight) |-> gray |-> convolve ( id1 * id2 + 3 & 2 < true * false | screenwidth / screenheight) |-> show; \n integer ident0 boolean ident1 image ident2 frame ident3 \n sleep ident1 * ident2 + 3783 & 2893 < true * false | screenwidth / screenheight <= true * false | screenwidth / screenheight > true * false | screenwidth / screenheight >= true * false | screenwidth / screenheight == true * false | screenwidth / screenheight != true * false | screenwidth / screenheight;}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.block();
	}
	
	@Test
	public void testBlockError() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.block();
	}
	
	@Test
	public void testParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "url ident321";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.paramDec();
	}
	
	@Test
	public void testParamDecError() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "url file iden55";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.paramDec();
	}
	
	@Test
	public void testProgram() throws IllegalCharException, IllegalNumberException, SyntaxException {
		//String input = "program0 url ident321, file ident493, integer idnet8484, boolean ident333 {}";
		//String input = "program0 {}";
		String input = "program0 url ident321, file ident493, integer idnet8484, boolean ident333 {\n integer num \n boolean isGood \n sleep id2 >= 23; \n image imageofGod \n frame f3 \n if( id3*23+5 != 25%id6) { isGood <- 25/8+66*id4+yyu%4;} \n while( 45*33 ) {  \n while(isGood) { \n  integer iden35 \n iden6 -> ii48 |-> ijk66;  \n } \n } \n }";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.program();
	}
	
	@Test
	public void testProgramError() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.program();
	}
}
