package mtech.content

import arc.scene.ui.Slider
import arc.scene.ui.layout.Table
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.world.blocks.sandbox.PowerSource

class AdjustablePowerSourceBuild(private val block: PowerSource) : AdjustablePowerSourceBuildBase(block) {
    var configuredPower = -1f

    override fun getPowerProduction(): Float {
        return if (enabled) {
            if (configuredPower >= 0f) configuredPower else block.powerProduction
        } else 0f
    }

    override fun buildConfiguration(table: Table) {
        // Mindustry uses per-tick values internally, but the UI displays per-second (×60)
        val defaultPerTick = block.powerProduction
        val defaultPerSec = defaultPerTick * 60f
        val currentPerSec = if (configuredPower >= 0f) configuredPower * 60f else defaultPerSec

        val slider = Slider(0f, defaultPerSec * 10f, defaultPerSec / 20f, false)
        slider.value = currentPerSec

        table.table { t ->
            t.add("Power:").padRight(8f)
            t.add(slider).width(250f).pad(8f)
            t.label { " ${currentPerSec.toInt()} /s" }.update { l ->
                val display = if (configuredPower >= 0f) (configuredPower * 60f).toInt() else defaultPerSec.toInt()
                l.setText(" $display /s")
            }
        }
        slider.changed {
            configuredPower = slider.value / 60f
            configure(configuredPower)
        }
    }

    override fun write(write: Writes) {
        super.write(write)
        write.f(configuredPower)
    }

    override fun read(read: Reads, revision: Byte) {
        super.read(read, revision)
        try {
            configuredPower = read.f()
        } catch (e: Exception) {
            configuredPower = -1f
        }
    }
}
