package jbash.parser;

public enum TokenType {
    Word,               // NotExpandableString
    StringLit,          // 'Single quoted expression'
    StringFormat,       // "Double quoted ${EXPR}"
    ParenExpr,          // (Parenthesized Expression)
    CurlyExpr,          // {Curly Expression}
    BrackExpr,          // [Bracket Expression]
    Dollar,             // $
    AndIf,
    OrIf,
    DSemi,
    DLess,
    DGreat,
    LessAnd,
    GreatAnd,
    LessGreat,
    DLessDash,
    Clobber,
    If,
    Then,
    Else,
    Elif,
    Fi,
    Do,
    Done,
    Case,
    Esac,
    While,
    Until,
    For,
    EOF,                // END OF FILE
    EOL,                // END OF LINE
    Whitespace,         // whitespace.
}
