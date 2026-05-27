package mtech.content

import arc.graphics.Color
import mindustry.type.Item

object ModItems {
    val tungstenCarbide = Item("tungsten-carbide", Color.valueOf("8c9ba8")).apply {
        cost = 3f
        hardness = 3
        explosiveness = 0f
        flammability = 0f
        radioactivity = 0f
    }

    val reinforcedAlloy = Item("reinforced-alloy", Color.valueOf("cd853f")).apply {
        cost = 5f
        hardness = 4
        explosiveness = 0.1f
        flammability = 0f
        radioactivity = 0.1f
    }

    val compositeArmor = Item("composite-armor", Color.valueOf("556b2f")).apply {
        cost = 8f
        hardness = 5
        explosiveness = 0f
        flammability = 0.2f
        radioactivity = 0f
    }

    val energyCrystal = Item("energy-crystal", Color.valueOf("7fffd4")).apply {
        cost = 6f
        hardness = 2
        explosiveness = 0.3f
        flammability = 0f
        radioactivity = 0.2f
    }

    fun load() {
        tungstenCarbide.load()
        reinforcedAlloy.load()
        compositeArmor.load()
        energyCrystal.load()
    }
}
