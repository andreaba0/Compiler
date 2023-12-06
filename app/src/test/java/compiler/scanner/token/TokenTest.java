package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import token.Token;
import token.TokenType;

class TokenTest {

	@Test
	void test() {
		Token token = new Token(TokenType.ID, 1, 1, "x");
		assertEquals(token.toString(), "<ID,r:1,c:1,x>");
	}

}
