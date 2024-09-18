package jbash.parser;

public enum TokenType {
    Word,               // NotExpandableString
    StringLit,          // 'Single quoted expression'
    StringFormat,       // "Double quoted ${EXPR}"
    ParenExpr,          // (Parenthesized Expression)
    CurlyExpr,          // {Curly Expression}
    BrackExpr,          // [Bracket Expression]
    Dollar,             // $
    EOF,                // END OF FILE
}
