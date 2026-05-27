package mtech

import arc.*
import arc.util.*
import mindustry.game.EventType.*
import mindustry.mod.Mod
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
}
