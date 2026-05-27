package mtech.logic.asm

data class Operand(
    val type: OperandType,
    val regIndex: Int = -1,
    val numValue: Double = 0.0,
    val stringValue: String = ""
)

enum class OperandType {
    REGISTER, IMMEDIATE, BUILTIN, LABEL_REF, STRING, MEMORY
}

data class ParsedInstr(
    val opcode: String,
    val operands: List<Operand>,
    val line: Int
)

class AsmParser(private val tokens: List<Token>) {
    private var pos = 0
    private var line = 1

    fun parse(): List<ParsedInstr> {
        val instructions = mutableListOf<ParsedInstr>()
        while (pos < tokens.size) {
            skipNewlines()
            if (pos >= tokens.size) break

            val token = tokens[pos]

            when (token.type) {
                TokenType.LABEL -> {
                    instructions.add(ParsedInstr("__label__", listOf(Operand(OperandType.LABEL_REF, stringValue = token.text)), line))
                    pos++
                }
                TokenType.IDENTIFIER -> {
                    pos++
                    val operands = parseOperands()
                    instructions.add(ParsedInstr(token.text.lowercase(), operands, line))
                }
                TokenType.BUILTIN -> {
                    pos++
                    val operands = parseOperands()
                    instructions.add(ParsedInstr(token.text.lowercase(), operands, line))
                }
                else -> {
                    pos++
                }
            }
        }
        return instructions
    }

    private fun skipNewlines() {
        while (pos < tokens.size && tokens[pos].type == TokenType.NEWLINE) {
            if (tokens[pos].type == TokenType.NEWLINE) line++
            pos++
        }
    }

    private fun parseOperands(): List<Operand> {
        val operands = mutableListOf<Operand>()
        while (pos < tokens.size && tokens[pos].type != TokenType.NEWLINE) {
            if (tokens[pos].type == TokenType.COMMA) { pos++; continue }
            if (tokens[pos].type == TokenType.LABEL) {
                operands.add(Operand(OperandType.LABEL_REF, stringValue = tokens[pos].text))
                pos++
                continue
            }
            operands.add(parseOperand())
            skipNewlines()
        }
        return operands
    }

    private fun parseOperand(): Operand {
        val token = tokens[pos]
        return when (token.type) {
            TokenType.IDENTIFIER -> {
                val text = token.text.lowercase()
                if (text.startsWith("r") && text.length > 1 && text.substring(1).all { it.isDigit() }) {
                    pos++
                    Operand(OperandType.REGISTER, regIndex = text.substring(1).toInt())
                } else if (text == "true") {
                    pos++
                    Operand(OperandType.IMMEDIATE, numValue = 1.0)
                } else if (text == "false") {
                    pos++
                    Operand(OperandType.IMMEDIATE, numValue = 0.0)
                } else if (text == "null" || text == "nan") {
                    pos++
                    Operand(OperandType.IMMEDIATE, numValue = Double.NaN)
                } else {
                    pos++
                    Operand(OperandType.LABEL_REF, stringValue = text)
                }
            }
            TokenType.NUMBER -> {
                pos++
                Operand(OperandType.IMMEDIATE, numValue = token.text.toDouble())
            }
            TokenType.BUILTIN -> {
                pos++
                Operand(OperandType.BUILTIN, stringValue = token.text)
            }
            TokenType.STRING -> {
                pos++
                Operand(OperandType.STRING, stringValue = token.text)
            }
            TokenType.LBRACKET -> {
                pos++
                val inner = parseOperand()
                if (pos < tokens.size && tokens[pos].type == TokenType.RBRACKET) {
                    pos++
                }
                Operand(OperandType.MEMORY, regIndex = inner.regIndex, stringValue = inner.stringValue)
            }
            TokenType.OPERATOR -> {
                pos++
                val next = parseOperand()
                Operand(OperandType.IMMEDIATE, numValue = -next.numValue)
            }
            else -> {
                pos++
                Operand(OperandType.IMMEDIATE, numValue = 0.0)
            }
        }
    }
}
