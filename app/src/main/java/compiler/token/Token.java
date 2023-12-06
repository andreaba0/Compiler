package token;

public class Token {

	private int row;
	private int col;
	private TokenType token_type;
	private String val;
	
	public Token(TokenType token_type, int row, int col, String value) {
		this.token_type = token_type;
		this.row = row;
		this.col = col;
		this.val = value;
	}
	
	public Token(TokenType token_type, String value) {
		this.token_type = token_type;
		this.val = value;
	}

    // Getters per i campi
    
	public String toString() {
		return "<" + token_type + ","+"r:"+row+",c:"+col+"," + val + ">";
	}
}
