package mtech.logic.asm

class AsmTranspiler {

    fun transpile(source: String): String {
        val lexer = AsmLexer(source)
        val tokens = lexer.tokenize()
        val parser = AsmParser(tokens)
        val instructions = parser.parse()

        val sb = StringBuilder()
        val labels = mutableMapOf<String, Int>()
        val targetLabels = mutableSetOf<String>()
        var instrIndex = 0

        // First pass: collect labels and identify target references
        for (instr in instructions) {
            when (instr.opcode) {
                "__label__" -> {
                    val labelName = instr.operands[0].stringValue
                    labels[labelName] = instrIndex
                }
                else -> {
                    for (op in instr.operands) {
                        if (op.type == OperandType.LABEL_REF) {
                            targetLabels.add(op.stringValue)
                        }
                    }
                    instrIndex++
                }
            }
        }

        // Second pass: emit mlog
        instrIndex = 0
        for (instr in instructions) {
            if (instr.opcode == "__label__") {
                val labelName = instr.operands[0].stringValue
                if (labelName in labels && labels[labelName] == instrIndex) {
                    if (targetLabels.contains(labelName)) {
                        sb.append(labelName).append(":\n")
                    }
                }
                continue
            }

            val mlogLine = translate(instr, labels)
            if (mlogLine != null) {
                sb.append(mlogLine).append("\n")
            }
            instrIndex++
        }

        return sb.toString()
    }

    private fun translate(instr: ParsedInstr, labels: Map<String, Int>): String? {
        val ops = instr.operands
        return when (instr.opcode) {
            // Data movement
            "mov" -> "set ${op(ops[0])} ${op(ops[1])}"
            "li" -> "set ${op(ops[0])} ${op(ops[1])}"

            // Memory
            "lw" -> "read ${op(ops[0])} ${memBase(ops[1])} ${
                if (ops.size > 2) op(ops[2]) else "0"
            }"
            "sw" -> "write ${op(ops[1])} ${memBase(ops[0])} ${
                if (ops.size > 2) op(ops[2]) else "0"
            }"

            // Arithmetic (3-op: dest, src1, src2)
            "add" -> "op add ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "sub" -> "op sub ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "mul" -> "op mul ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "div" -> "op div ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "mod" -> "op mod ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "pow" -> "op pow ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "idiv" -> "op idiv ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"

            // Bitwise
            "and" -> "op and ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "or" -> "op or ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "xor" -> "op xor ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "shl" -> "op shl ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "shr" -> "op shr ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "ushr" -> "op ushr ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"

            // Unary
            "not" -> "op not ${op(ops[0])} ${op(ops[1])} 0"
            "neg" -> "op neg ${op(ops[0])} ${op(ops[1])} 0"
            "abs" -> "op abs ${op(ops[0])} ${op(ops[1])} 0"
            "sqrt" -> "op sqrt ${op(ops[0])} ${op(ops[1])} 0"
            "floor" -> "op floor ${op(ops[0])} ${op(ops[1])} 0"
            "ceil" -> "op ceil ${op(ops[0])} ${op(ops[1])} 0"
            "round" -> "op round ${op(ops[0])} ${op(ops[1])} 0"
            "log" -> "op log ${op(ops[0])} ${op(ops[1])} 0"
            "logn" -> "op logn ${op(ops[0])} ${op(ops[1])} 0"
            "log10" -> "op log10 ${op(ops[0])} ${op(ops[1])} 0"
            "sin" -> "op sin ${op(ops[0])} ${op(ops[1])} 0"
            "cos" -> "op cos ${op(ops[0])} ${op(ops[1])} 0"
            "tan" -> "op tan ${op(ops[0])} ${op(ops[1])} 0"
            "rand" -> "op rand ${op(ops[0])} ${op(ops[1])} 0"
            "sign" -> "op sign ${op(ops[0])} ${op(ops[1])} 0"

            // Comparison (set on condition)
            "seq" -> "op equal ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "sne" -> "op notEqual ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "slt" -> "op lessThan ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "sle" -> "op lessThanEq ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "sgt" -> "op greaterThan ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "sge" -> "op greaterThanEq ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "se" -> "op strictEqual ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"

            // Extended math
            "max" -> "op max ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "min" -> "op min ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "angle" -> "op angle ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "anglediff" -> "op angleDiff ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "len" -> "op len ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"
            "noise" -> "op noise ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"

            // Control flow
            "jmp" -> "jump ${resolveLabel(ops[0], labels)} always 0 0"
            "jz" -> "jump ${resolveLabel(ops[1], labels)} equal ${op(ops[0])} 0"
            "jnz" -> "jump ${resolveLabel(ops[1], labels)} notEqual ${op(ops[0])} 0"
            "je" -> "jump ${resolveLabel(ops[2], labels)} equal ${op(ops[0])} ${op(ops[1])}"
            "jne" -> "jump ${resolveLabel(ops[2], labels)} notEqual ${op(ops[0])} ${op(ops[1])}"
            "jlt" -> "jump ${resolveLabel(ops[2], labels)} lessThan ${op(ops[0])} ${op(ops[1])}"
            "jle" -> "jump ${resolveLabel(ops[2], labels)} lessThanEq ${op(ops[0])} ${op(ops[1])}"
            "jgt" -> "jump ${resolveLabel(ops[2], labels)} greaterThan ${op(ops[0])} ${op(ops[1])}"
            "jge" -> "jump ${resolveLabel(ops[2], labels)} greaterThanEq ${op(ops[0])} ${op(ops[1])}"

            "end" -> "end"
            "stop" -> "stop"
            "noop" -> "noop"
            "wait" -> "wait ${op(ops[0])}"

            // Synchronization
            "sync" -> "sync ${op(ops[0])}"

            // World interaction
            "sense" -> "sensor ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"

            "control" -> {
                val target = op(ops[0])
                val type = op(ops[1])
                val p1 = if (ops.size > 2) op(ops[2]) else "0"
                val p2 = if (ops.size > 3) op(ops[3]) else "0"
                val p3 = if (ops.size > 4) op(ops[4]) else "0"
                val p4 = if (ops.size > 5) op(ops[5]) else "0"
                "control $target $type $p1 $p2 $p3 $p4"
            }

            "bind" -> {
                val target = if (ops[0].type == OperandType.REGISTER) op(ops[0]) else op(ops[0])
                "ubind $target"
            }

            "ucontrol" -> {
                val type = op(ops[0])
                val p1 = if (ops.size > 1) op(ops[1]) else "0"
                val p2 = if (ops.size > 2) op(ops[2]) else "0"
                val p3 = if (ops.size > 3) op(ops[3]) else "0"
                val p4 = if (ops.size > 4) op(ops[4]) else "0"
                val p5 = if (ops.size > 5) op(ops[5]) else "0"
                "ucontrol $type $p1 $p2 $p3 $p4 $p5"
            }

            "ulocate" -> {
                val builder = StringBuilder("ulocate")
                for (op in ops) builder.append(" ${op(op)}")
                builder.toString()
            }

            // Block interaction
            "getblock" -> "getblock ${op(ops[0])} ${op(ops[1])} ${op(ops[2])} ${if (ops.size > 3) op(ops[3]) else "ground"}"
            "setblock" -> "setblock ${op(ops[0])} ${op(ops[1])} ${op(ops[2])} ${if (ops.size > 3) op(ops[3]) else "@derelict"} ${if (ops.size > 4) op(ops[4]) else "0"} ${if (ops.size > 5) op(ops[5]) else "ground"}"

            // I/O
            "print" -> "print ${op(ops[0])}"
            "printflush" -> "printflush ${op(ops[0])}"
            "draw" -> "draw ${op(ops[0])} ${op(ops[1])} ${op(ops[2])} ${
                if (ops.size > 3) op(ops[3]) else "0"
            } ${if (ops.size > 4) op(ops[4]) else "0"} ${if (ops.size > 5) op(ops[5]) else "0"} ${
                if (ops.size > 6) op(ops[6]) else "0"
            }"
            "drawflush" -> "drawflush ${op(ops[0])}"

            // Linking
            "getlink" -> "getlink ${op(ops[0])} ${op(ops[1])}"

            // Content lookup
            "lookup" -> "lookup ${op(ops[0])} ${op(ops[1])} ${op(ops[2])}"

            // Flags
            "getflag" -> "getflag ${op(ops[0])} ${op(ops[1])}"
            "setflag" -> "setflag ${op(ops[0])} ${op(ops[1])}"

            // Rate
            "setrate" -> "setrate ${op(ops[0])}"

            // Effects
            "effect" -> "effect ${op(ops[0])} ${op(ops[1])} ${op(ops[2])} ${op(ops[3])} ${if (ops.size > 4) op(ops[4]) else "0"}"
            "applyeffect" -> {
                val target = op(ops[0])
                val effect = op(ops[1])
                val duration = if (ops.size > 2) op(ops[2]) else "0"
                "applyeffect $target $effect $duration"
            }

            // Explosion
            "explosion" -> {
                val builder = StringBuilder("explosion")
                for (op in ops) builder.append(" ${op(op)}")
                builder.toString()
            }

            // Cutscene
            "cutscene" -> {
                val builder = StringBuilder("cutscene")
                for (op in ops) builder.append(" ${op(op)}")
                builder.toString()
            }

            // Rules
            "setrule" -> {
                val builder = StringBuilder("setrule")
                for (op in ops) builder.append(" ${op(op)}")
                builder.toString()
            }

            // Weather
            "setweather" -> {
                val builder = StringBuilder("setweather")
                for (op in ops) builder.append(" ${op(op)}")
                builder.toString()
            }

            // Radar
            "radar" -> {
                val builder = StringBuilder("radar")
                for (op in ops) builder.append(" ${op(op)}")
                builder.toString()
            }

            // Format
            "format" -> "format ${op(ops[0])}"

            // Message
            "message" -> {
                val builder = StringBuilder("message")
                for (op in ops) builder.append(" ${op(op)}")
                builder.toString()
            }

            // Fetch
            "fetch" -> {
                val builder = StringBuilder("fetch")
                for (op in ops) builder.append(" ${op(op)}")
                builder.toString()
            }

            // Raw mlog passthrough
            "mlog" -> {
                ops.joinToString(" ") { it.stringValue }
            }

            else -> null
        }
    }

    private fun op(operand: Operand): String = when (operand.type) {
        OperandType.REGISTER -> "r${operand.regIndex}"
        OperandType.IMMEDIATE -> formatNum(operand.numValue)
        OperandType.BUILTIN -> operand.stringValue
        OperandType.LABEL_REF -> operand.stringValue
        OperandType.STRING -> "\"${operand.stringValue}\""
        OperandType.MEMORY -> "r${operand.regIndex}"
    }

    private fun memBase(operand: Operand): String = when (operand.type) {
        OperandType.MEMORY -> "r${operand.regIndex}"
        OperandType.LABEL_REF -> operand.stringValue
        OperandType.REGISTER -> "r${operand.regIndex}"
        else -> op(operand)
    }

    private fun formatNum(v: Double): String {
        if (v.isNaN()) return "NaN"
        if (v == Math.floor(v) && !v.isInfinite()) {
            return v.toLong().toString()
        }
        return v.toString()
    }

    private fun resolveLabel(operand: Operand, labels: Map<String, Int>): String {
        return operand.stringValue
    }
}
