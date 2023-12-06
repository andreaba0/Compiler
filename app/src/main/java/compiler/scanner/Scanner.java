package scanner;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

import token.*;

public class Scanner {
	final char EOF = (char) -1; 
	private int row;
	private int col;
	private PushbackReader buffer;
	private String log;
	private char ignoreCharList[] = { ' ', '\t', '\n', '\r' };

	// skpChars: insieme caratteri di skip (include EOF) e inizializzazione
	// letters: insieme lettere e inizializzazione
	// digits: cifre e inizializzazione

	// char_type_Map: mapping fra caratteri '+', '-', '*', '/', ';', '=', ';' e il
	// TokenType corrispondente

	// keyWordsMap: mapping fra le stringhe "print", "float", "int" e il
	// TokenType  corrispondente

	private boolean isDigit(char c) {
		return (c >= '0' && c <= '9');
	}

	private boolean isLetter(char c) {
		return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
	}
	
	private TokenType charTypeMap(char c) {
		switch (c) {
		case '+':
			return TokenType.PLUS;
		case '-':
			return TokenType.MINUS;
		case '*':
			return TokenType.TIMES;
		case '/':
			return TokenType.DIV;
		case ';':
			return TokenType.SEMICOLON;
		case '=':
			return TokenType.OP_ASSIGN;
		case EOF:
			return TokenType.EOF;
		default:
			return null;
		}
	}

	private boolean isOperator(char c) {
		return (c == '+' || c == '-' || c == '*' || c == '/' || c == ';' || c == '=');
	}

	private Token operatorWordMap(char c) {
		String op = "";
		try {
			op += readChar();
			if(op.equals("=")) {
				return new Token(TokenType.OP_ASSIGN, row, col, op);
			}
			
		}
	}

	private TokenType keyWordsMap(String s) {
		switch (s) {
		case "print":
			return TokenType.PRINT;
		case "int":
			return TokenType.TYINT;
		case "float":
			return TokenType.TYFLOAT;
		case "+=":
			return TokenType.OP_ASSIGN;
		case "-=":
			return TokenType.OP_ASSIGN;
		case "*=":
			return TokenType.OP_ASSIGN;
		case "/=":
			return TokenType.OP_ASSIGN;
		default:
			return null;
		}
	}

	private boolean isToBeIgnored(char c) {
		for (int i = 0; i < ignoreCharList.length; i++) {
			if (c == ignoreCharList[i])
				return true;
		}
		return false;
	}

	private void nextRow() {
		row+=1;
		col = 1;
	}

	private void nextCol() {
		col+=1;
	}

	public Scanner(String fileName) throws FileNotFoundException, LexicalException {

		this.buffer = new PushbackReader(new FileReader(fileName));
		this.row = 1;
		this.col = 1;
		// inizializzare campi che non hanno inizializzazione
	}

	private Token scanId() {
		String id = "";

		try {
			id += readChar();
			while(isLetter(peekChar()) || isDigit(peekChar())) {
				id += readChar();
				nextCol();
			}
			if(keyWordsMap(id) != null) {
				return new Token(keyWordsMap(id), row, col, id);
			}
			return new Token(TokenType.ID, row, col, id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Token scanNumber() {
		String number = "";
		TokenType type = TokenType.INT;
		int state = 0;
		try {
			state = (peekChar() == '0') ? 1 : 2;
			col += 1;
			while(true) {
				number += readChar();
				switch(state) {
					case 1:
						if(peekChar()=='.') {
							state =3;
							type = TokenType.FLOAT;
							continue;
						}
						if(isToBeIgnored(peekChar())) {
							return new Token(TokenType.INT, row, col, number);
						}
						throw new LexicalException("At row " + row + ", column: " + col + " found illegal character: " + peekChar());
					case 2:
						if(isDigit(peekChar())) {
							continue;
						}
						if(peekChar()=='.') {
							state = 3;
							type = TokenType.FLOAT;
							continue;
						}
						if(isToBeIgnored(peekChar())) {
							return new Token(TokenType.INT, row, col, number);
						}
						throw new LexicalException("At row " + row + ", column: " + col + " found illegal character: " + peekChar());
					case 3:
						if(isDigit(peekChar())) {
							state = 4;
							continue;
						}
						throw new LexicalException("At row " + row + ", column: " + col + " found illegal character: " + peekChar());
					case 4:
						if(isDigit(peekChar())) {
							continue;
						}
						if(isToBeIgnored(peekChar())) {
							return new Token(TokenType.FLOAT, row, col, number);
						}
						throw new LexicalException("At row " + row + ", column: " + col + " found illegal character: " + peekChar());
				}
				nextCol();
			}
			return new Token(type, row, col, number);
		}
	}

	public Token nextToken()  {

		// nextChar contiene il prossimo carattere dell'input (non consumato).
		char nextChar = peekChar(); //Catturate l'eccezione IOException e 
		       // ritornate una LexicalException che la contiene
		
		if(nextChar == EOF) {
			return new Token(TokenType.EOF, row, col, "");
		}
		if(isToBeIgnored(nextChar)) {
			try {
				if(nextChar == '\n') {
					nextRow();
				} else {
					nextCol();
				}
				readChar();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return nextToken();
		}

		if(isDigit(nextChar)) {
			return scanNumber();
		}

		if(isLetter(nextChar)) {
			return scanId();
		}

		throw new LexicalException("At row " + row + ", column: " + col + " found illegal character: " + nextChar);

		// Avanza nel buffer leggendo i carattere in skipChars
		// incrementando riga se leggi '\n'.
		// Se raggiungi la fine del file ritorna il Token EOF


		// Se nextChar e' in letters
		// return scanId()
		// che legge tutte le lettere minuscole e ritorna un Token ID o
		// il Token associato Parola Chiave (per generare i Token per le
		// parole chiave usate l'HaskMap di corrispondenza

		// Se nextChar e' o in operators oppure 
		// ritorna il Token associato con l'operatore o il delimitatore

		// Se nextChar e' in numbers
		// return scanNumber()
		// che legge sia un intero che un float e ritorna il Token INUM o FNUM
		// i caratteri che leggete devono essere accumulati in una stringa
		// che verra' assegnata al campo valore del Token

		// Altrimenti il carattere NON E' UN CARATTERE LEGALE sollevate una
		// eccezione lessicale dicendo la riga e il carattere che la hanno
		// provocata. 
		return null;

	}

	// private Token scanNumber()

	// private Token scanId()

	private char readChar() throws IOException {
		return ((char) this.buffer.read());
	}

	private char peekChar() throws IOException {
		char c = (char) buffer.read();
		buffer.unread(c);
		return c;
	}
}
