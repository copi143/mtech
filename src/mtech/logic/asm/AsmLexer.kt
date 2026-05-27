package mtech.logic.asm

class AsmLexer(private val source: String) {
    private var pos = 0
    private val tokens = mutableListOf<Token>()

    fun tokenize(): List<Token> {
        while (pos < source.length) {
            val c = source[pos]
            when {
                c == '#' || (c == '/' && peek() == '/') -> skipComment()
                c == '\n' || c == '\r' -> {
                    tokens.add(Token(TokenType.NEWLINE, "\n"))
                    pos++
                    if (c == '\r' && peek() == '\n') pos++
                }
                c == ',' || c == '\t' || c == ' ' -> pos++
                c == '[' -> { tokens.add(Token(TokenType.LBRACKET, "[")); pos++ }
                c == ']' -> { tokens.add(Token(TokenType.RBRACKET, "]")); pos++ }
                c == ':' -> { tokens.add(Token(TokenType.COLON, ":")); pos++ }
                c == '"' -> readString()
                c == '-' && pos + 1 < source.length && source[pos + 1].isDigit() -> readNumber()
                c.isDigit() -> readNumber()
                c == '@' -> readBuiltin()
                c.isLetter() || c == '_' -> readIdentifier()
                else -> pos++
            }
        }
        return tokens
    }

    private fun peek(): Char = if (pos + 1 < source.length) source[pos + 1] else '\u0000'

    private fun skipComment() {
        while (pos < source.length && source[pos] != '\n' && source[pos] != '\r') pos++
    }

    private fun readString() {
        pos++ // skip opening "
        val start = pos
        while (pos < source.length && source[pos] != '"') {
            if (source[pos] == '\\') pos++
            pos++
        }
        tokens.add(Token(TokenType.STRING, source.substring(start, pos)))
        if (pos < source.length) pos++ // skip closing "
    }

    private fun readNumber() {
        val start = pos
        var isFloat = false
        while (pos < source.length && (source[pos].isDigit() || source[pos] == '.')) {
            if (source[pos] == '.') isFloat = true
            pos++
        }
        val numStr = source.substring(start, pos)
        if (numStr == "-") {
            tokens.add(Token(TokenType.OPERATOR, "-"))
            return
        }
        tokens.add(Token(TokenType.NUMBER, numStr))
    }

    private fun readBuiltin() {
        val start = pos
        pos++ // skip @
        while (pos < source.length && (source[pos].isLetterOrDigit() || source[pos] == '_' || source[pos] == '.')) pos++
        tokens.add(Token(TokenType.BUILTIN, source.substring(start, pos)))
    }

    private fun readIdentifier() {
        val start = pos
        while (pos < source.length && (source[pos].isLetterOrDigit() || source[pos] == '_')) pos++

        val word = source.substring(start, pos)
        if (pos < source.length && source[pos] == ':') {
            tokens.add(Token(TokenType.LABEL, word))
            pos++ // consume ':'
        } else {
            tokens.add(Token(TokenType.IDENTIFIER, word))
        }
    }
}

enum class TokenType {
    IDENTIFIER, NUMBER, BUILTIN, LABEL, STRING,
    COMMA, COLON, LBRACKET, RBRACKET, NEWLINE, OPERATOR, EOF
}

data class Token(val type: TokenType, val text: String)
