package mtech.logic.asm

import arc.struct.OrderedMap
import mindustry.logic.LAssembler
import mindustry.logic.LExecutor
import mindustry.logic.LVar

class AsmAssemblerResult(
    val executor: LExecutor,
    val assembler: LAssembler
)

object AsmAssembler {

    fun assemble(source: String, privileged: Boolean): AsmAssemblerResult? {
        if (source.isBlank()) return null
        try {
            val transpiler = AsmTranspiler()
            val mlogSource = transpiler.transpile(source)

            val assembler = LAssembler.assemble(mlogSource, privileged)

            val executor = LExecutor()
            executor.privileged = privileged
            executor.load(assembler)

            return AsmAssemblerResult(executor, assembler)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
