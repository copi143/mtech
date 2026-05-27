package mtech.logic

import arc.func.Cons
import arc.func.Cons2
import mindustry.Vars
import mindustry.gen.Building
import mindustry.logic.LAssembler
import mindustry.world.blocks.logic.LogicBlock
import mindustry.world.blocks.logic.LogicBlock.LogicLink
import mindustry.world.meta.BlockGroup
import mtech.logic.asm.AsmTranspiler

class CustomProcessor(name: String) : LogicBlock(name) {

    init {
        update = true
        solid = true
        configurable = true
        group = BlockGroup.logic
        schematicPriority = 5
        hasItems = false
        hasPower = true
        consumesPower = true
        consumePower(1f)

        buildType = { CustomBuild() }

        configurations.put(ByteArray::class.java, Cons2<Building, ByteArray> { build, data ->
            if (build is CustomBuild) {
                build.readCompressed(data, true)
            }
        })

        configurations.put(String::class.java, Cons2<Building, String> { build, data ->
            if (build is CustomBuild && data.length < 32) {
                build.tag = data
            }
        })

        configurations.put(Character::class.java, Cons2<Building, Character> { build, data ->
            if (build is CustomBuild) {
                build.iconTag = data.charValue()
            }
        })

        configurations.put(Integer::class.java, Cons2<Building, Integer> { build, data ->
            if (accessible() && build is CustomBuild) {
                val b = Vars.world.build(data.toInt())
                if (build.validLink(b)) {
                    val tileX = b.tileX()
                    val tileY = b.tileY()
                    val size = build.links.size
                    build.links.removeAll { link: LogicLink -> Vars.world.build(link.x, link.y) == b }
                    if (size > build.links.size) {
                        if (b.block.autoResetEnabled && b.lastDisabler == build) {
                            b.enabled = true
                        }
                    } else {
                        build.links.add(LogicLink(tileX, tileY, build.findLinkName(b.block), true))
                    }
                    build.updateCode(build.code, true, null)
                }
            }
        })
    }

    inner class CustomBuild : LogicBuild() {
        var logicMode: LogicMode = LogicMode.CUSTOM_ASM

        override fun updateCode(code: String, preserve: Boolean, assembler: Cons<LAssembler>?) {
            try {
                if (logicMode == LogicMode.CUSTOM_ASM) {
                    this.code = code
                    if (code.isBlank()) {
                        executor = mindustry.logic.LExecutor()
                        return
                    }
                    val transpiler = AsmTranspiler()
                    val mlog = transpiler.transpile(code)
                    super.updateCode(mlog, preserve, assembler)
                } else {
                    super.updateCode(code, preserve, assembler)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                executor = mindustry.logic.LExecutor()
            }
        }
    }
}
