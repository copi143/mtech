package mtech.content

import mindustry.content.Blocks
import mindustry.content.TechTree
import mindustry.content.TechTree.TechNode

object ModTechTree {
    fun load() {
        val plastanium = TechTree.all.find { it.content === Blocks.plastaniumCompressor } ?: return
        val phaseWeaver = TechTree.all.find { it.content === Blocks.phaseWeaver } ?: return
        val surgeSmelter = TechTree.all.find { it.content === Blocks.surgeSmelter } ?: return

        // Carbide chain (under plastanium)
        val carbideFurnaceNode = TechNode(plastanium, ModBlocks.carbideFurnace, ModBlocks.carbideFurnace.requirements)
        TechNode(carbideFurnaceNode, ModBlocks.carbideWall, ModBlocks.carbideWall.requirements)
        TechNode(carbideFurnaceNode, ModBlocks.carbideWallLarge, ModBlocks.carbideWallLarge.requirements)
        TechNode(carbideFurnaceNode, ModBlocks.piercer, ModBlocks.piercer.requirements)

        // Crystal chain (under phase weaver)
        val crystalNode = TechNode(phaseWeaver, ModBlocks.crystalSynthesizer, ModBlocks.crystalSynthesizer.requirements)
        TechNode(crystalNode, ModBlocks.thunder, ModBlocks.thunder.requirements)

        // Alloy chain (under surge smelter)
        val alloyNode = TechNode(surgeSmelter, ModBlocks.alloyCrucible, ModBlocks.alloyCrucible.requirements)
        TechNode(alloyNode, ModBlocks.reinforcedWall, ModBlocks.reinforcedWall.requirements)
        TechNode(alloyNode, ModBlocks.reinforcedWallLarge, ModBlocks.reinforcedWallLarge.requirements)
        TechNode(alloyNode, ModBlocks.volcano, ModBlocks.volcano.requirements)
        val armorNode = TechNode(alloyNode, ModBlocks.armorCompressor, ModBlocks.armorCompressor.requirements)
        TechNode(armorNode, ModBlocks.compositeWall, ModBlocks.compositeWall.requirements)
        TechNode(armorNode, ModBlocks.compositeWallLarge, ModBlocks.compositeWallLarge.requirements)
        TechNode(armorNode, ModBlocks.annihilator, ModBlocks.annihilator.requirements)
    }
}
