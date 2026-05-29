package mtech

import arc.*
import arc.util.*
import arc.func.*
import mindustry.game.EventType.*
import mindustry.mod.Mod
import mindustry.content.Blocks
import mindustry.gen.Building
import mindustry.world.blocks.sandbox.PowerSource
import mtech.content.*

class MtechMod : Mod() {
    init {
        Log.info("Loading Mtech mod...")

        Events.on(ClientLoadEvent::class.java) {
            Log.info("Mtech mod loaded successfully.")
        }
    }

    override fun loadContent() {
        Log.info("Loading Mtech content...")
        ModItems.load()
        ModBlocks.load()
        ModTechTree.load()
    }

    override fun init() {
        // Multiple all vanilla drill capacities by 10
        val drills = listOf(
            Blocks.mechanicalDrill,
            Blocks.pneumaticDrill,
            Blocks.laserDrill,
            Blocks.blastDrill,
            Blocks.impactDrill,
            Blocks.eruptionDrill,
        )
        for (drill in drills) {
            val old = drill.itemCapacity
            drill.itemCapacity *= 10
            Log.info("Modified ${drill.name} capacity: $old -> ${drill.itemCapacity}")
        }

        // Swap power source's buildType to enable adjustable power output
        val ps = Blocks.powerSource
        ps.configurable = true
        ps.config(
            Float::class.java,
            Cons2<Building, Float> { building, value ->
                if (building is AdjustablePowerSourceBuild) {
                    building.configuredPower = value
                }
            }
        )
        ps.buildType = Prov<Building> {
            AdjustablePowerSourceBuild(ps as PowerSource) as Building
        }
        Log.info("Swapped power-source buildType for adjustable power output")
    }
}
