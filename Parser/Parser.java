/* Complete all the methods.
EBNF of Mini Language
Program" --> "("Sequence State")".
Sequence --> "("Statements")".
Statements --> Stmt*
Stmt --> "(" {NullStatement | Assignment | Conditional | Loop | Block}")".
State -->  "("Pairs")".
Pairs -->  Pair*.
Pair --> "("Identifier Literal")".
NullStatement --> "skip".
Assignment --> "assign" Identifier Expression.
Conditional --> "conditional" Expression Stmt Stmt.
Loop --> "loop" Expression Stmt.
Block --> "block" Statements.
Expression --> Identifier | Literal | "("Operation Expression Expression")".
Operation --> "+" |"-" | "*" | "/" | "<" | "<=" | ">" | ">=" | "=" | "!=" | "or" | "and".

Note: Treat Identifier and Literal as terminal symbols. Every symbol inside " and " is a terminal symbol. The rest are non terminals.

*/
public class Parser {
  private Token currentToken;
  Scanner scanner;

  // Add default constructor
  public Parser() {
    // Constructor is empty since initialization happens in parse()
  }

  private void accept(byte expectedKind) {
    if (currentToken.kind == expectedKind)
      currentToken = scanner.scan();
    else {
      System.out.println("Line " + currentToken.line + ": Syntax error: " + 
                        currentToken.spelling + " is not expected.");
      System.exit(0);
    }
  }

  private void acceptIt() {
    currentToken = scanner.scan();
  }

  public void parse() {
    SourceFile sourceFile = new SourceFile();
    scanner = new Scanner(sourceFile.openFile());
    currentToken = scanner.scan();
    parseProgram();
    if (currentToken.kind != Token.EOT) {
      System.out.println("Line " + currentToken.line + 
                        ": Syntax error: Redundant characters at the end of program.");
      System.exit(0);
    }
  }

  //Program --> "("Sequence State")".
  private void parseProgram() {
    accept(Token.LPAREN);
    parseSequence();
    parseState();
    accept(Token.RPAREN);
  }

  //Sequence --> "("Statements")".
  private void parseSequence() {
    accept(Token.LPAREN);
    parseStatements();
    accept(Token.RPAREN);
  }

  //Statements --> Stmt*
  private void parseStatements() {
    while (currentToken.kind == Token.LPAREN) {
      parseStmt();
    }
  }

  //Stmt --> "(" {NullStatement | Assignment | Conditional | Loop | Block}")".
  private void parseStmt() {
    accept(Token.LPAREN);
    switch (currentToken.kind) {
      case Token.SKIP -> parseNullStatement();
      case Token.ASSIGN -> parseAssignment();
      case Token.CONDITIONAL -> parseConditional();
      case Token.LOOP -> parseLoop();
      case Token.BLOCK -> parseBlock();
      default -> {
          System.out.println("Line " + currentToken.line +
                  ": Syntax error: Invalid statement type.");
          System.exit(0);
          }
    }
    accept(Token.RPAREN);
  }

  //State --> "("Pairs")".
  private void parseState() {
    accept(Token.LPAREN);
    parsePairs();
    accept(Token.RPAREN);
  }

  //Pairs --> Pair*
  private void parsePairs() {
    while (currentToken.kind == Token.LPAREN) {
      parsePair();
    }
  }

  //Pair --> "("Identifier Literal")".
  private void parsePair() {
    accept(Token.LPAREN);
    accept(Token.IDENTIFIER);
    accept(Token.LITERAL);
    accept(Token.RPAREN);
  }

  //NullStatement --> "skip".
  private void parseNullStatement() {
    accept(Token.SKIP);
  }

  //Assignment --> "assign" Identifier Expression.
  private void parseAssignment() {
    accept(Token.ASSIGN);
    accept(Token.IDENTIFIER);
    parseExpression();
  }

  //Conditional --> "conditional" Expression Stmt Stmt.
  private void parseConditional() {
    accept(Token.CONDITIONAL);
    parseExpression();
    parseStmt();
    parseStmt();
  }

  //Loop --> "loop" Expression Stmt.
  private void parseLoop() {
    accept(Token.LOOP);
    parseExpression();
    parseStmt();
  }

  //Block --> "block" Statements.
  private void parseBlock() {
    accept(Token.BLOCK);
    parseStatements();
  }

  //Expression --> Identifier | Literal | "("Operation Expression Expression")".
  private void parseExpression() {
    if (currentToken.kind == Token.IDENTIFIER) {
      accept(Token.IDENTIFIER);
    } else if (currentToken.kind == Token.LITERAL) {
      accept(Token.LITERAL);
    } else if (currentToken.kind == Token.LPAREN) {
      accept(Token.LPAREN);
      parseOperation();
      parseExpression();
      parseExpression();
      accept(Token.RPAREN);
    } else {
      System.out.println("Line " + currentToken.line + 
                        ": Syntax error: Invalid expression.");
      System.exit(0);
    }
  }

  //Operation --> "+" |"-" | "*" | "/" | "<" | "<=" | ">" | ">=" | "=" | "!=" | "or" | "and".
  private void parseOperation() {
    if (currentToken.kind == Token.OPERATOR || 
        currentToken.kind == Token.AND || 
        currentToken.kind == Token.OR) {
      acceptIt();
    } else {
      System.out.println("Line " + currentToken.line + 
                        ": Syntax error: Invalid operator.");
      System.exit(0);
    }
  }
}