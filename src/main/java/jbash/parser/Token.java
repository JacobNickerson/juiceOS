package jbash.parser;

public record Token(TokenType type, int position, String lexeme) {
    @Override
    public String toString() {
        return "["+this.lexeme+":"+this.type+"@"+this.position+"]";
    }
}
