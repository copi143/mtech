package mtech.content

import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.entities.bullet.BulletType
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.blocks.defense.Wall
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.production.GenericCrafter

object ModBlocks {
    // ====== PRODUCTION CRAFTERS ======

    val carbideFurnace = GenericCrafter("carbide-furnace").apply {
        size = 2
        health = 160
        craftTime = 60f
        craftEffect = mindustry.content.Fx.pulverize
        updateEffect = mindustry.content.Fx.pulverize
        updateEffectChance = 0.04f
        outputItem = ItemStack(ModItems.tungstenCarbide, 1)
        consumeItems(ItemStack(Items.graphite, 2), ItemStack(Items.titanium, 1))
        consumePower(1.5f)
        requirements(Category.crafting, ItemStack.with(
            Items.copper, 80, Items.lead, 50, Items.graphite, 30
        ))
        hasItems = true
        hasPower = true
    }

    val alloyCrucible = GenericCrafter("alloy-crucible").apply {
        size = 2
        health = 180
        craftTime = 90f
        craftEffect = mindustry.content.Fx.pulverizeRed
        updateEffect = mindustry.content.Fx.pulverize
        updateEffectChance = 0.05f
        outputItem = ItemStack(ModItems.reinforcedAlloy, 1)
        consumeItems(
            ItemStack(Items.titanium, 2),
            ItemStack(Items.thorium, 2),
            ItemStack(Items.surgeAlloy, 1)
        )
        consumePower(2.5f)
        requirements(Category.crafting, ItemStack.with(
            Items.titanium, 40, Items.thorium, 30, Items.silicon, 40, Items.metaglass, 30
        ))
        hasItems = true
        hasPower = true
    }

    val armorCompressor = GenericCrafter("armor-compressor").apply {
        size = 3
        health = 280
        craftTime = 120f
        craftEffect = mindustry.content.Fx.pulverizeRed
        updateEffect = mindustry.content.Fx.pulverize
        updateEffectChance = 0.03f
        outputItem = ItemStack(ModItems.compositeArmor, 1)
        consumeItems(
            ItemStack(Items.plastanium, 2),
            ItemStack(ModItems.reinforcedAlloy, 1)
        )
        consumePower(4f)
        requirements(Category.crafting, ItemStack.with(
            Items.plastanium, 40, Items.surgeAlloy, 30, Items.silicon, 50, Items.thorium, 40
        ))
        hasItems = true
        hasPower = true
    }

    val crystalSynthesizer = GenericCrafter("crystal-synthesizer").apply {
        size = 2
        health = 170
        craftTime = 75f
        craftEffect = mindustry.content.Fx.colorSpark
        updateEffect = mindustry.content.Fx.colorSpark
        updateEffectChance = 0.06f
        outputItem = ItemStack(ModItems.energyCrystal, 1)
        consumeItems(
            ItemStack(Items.phaseFabric, 2),
            ItemStack(Items.silicon, 3)
        )
        consumePower(3f)
        requirements(Category.crafting, ItemStack.with(
            Items.phaseFabric, 40, Items.silicon, 50, Items.titanium, 30
        ))
        hasItems = true
        hasPower = true
    }

    // ====== WALLS ======

    val carbideWall = Wall("carbide-wall").apply {
        size = 1
        health = 4000
        armor = 8f
        requirements(Category.defense, ItemStack.with(ModItems.tungstenCarbide, 6))
    }

    val carbideWallLarge = Wall("carbide-wall-large").apply {
        size = 2
        health = 16000
        armor = 8f
        requirements(Category.defense, ItemStack.with(ModItems.tungstenCarbide, 24))
    }

    val reinforcedWall = Wall("reinforced-wall").apply {
        size = 1
        health = 5500
        armor = 15f
        requirements(Category.defense, ItemStack.with(ModItems.reinforcedAlloy, 6))
    }

    val reinforcedWallLarge = Wall("reinforced-wall-large").apply {
        size = 2
        health = 22000
        armor = 15f
        requirements(Category.defense, ItemStack.with(ModItems.reinforcedAlloy, 24))
    }

    val compositeWall = Wall("composite-wall").apply {
        size = 1
        health = 7500
        armor = 20f
        requirements(Category.defense, ItemStack.with(ModItems.compositeArmor, 6))
    }

    val compositeWallLarge = Wall("composite-wall-large").apply {
        size = 2
        health = 30000
        armor = 20f
        requirements(Category.defense, ItemStack.with(ModItems.compositeArmor, 24))
    }

    // ====== TURRETS ======

    val piercer = ItemTurret("piercer").apply {
        size = 2
        range = 140f
        reload = 35f
        recoil = 3f
        rotateSpeed = 6f
        shake = 1.5f
        shootCone = 10f
        inaccuracy = 1f
        targetAir = true
        targetGround = true
        ammo(
            ModItems.tungstenCarbide, ModBullets.carbideShot,
            ModItems.reinforcedAlloy, ModBullets.annihilationShell
        )
        ammoPerShot = 2
        maxAmmo = 20
        requirements(Category.turret, ItemStack.with(
            Items.copper, 100, Items.graphite, 75, Items.titanium, 60, Items.silicon, 50
        ))
        consumePower(3f)
        hasPower = true
        hasItems = true
    }

    val thunder = ItemTurret("thunder").apply {
        size = 2
        range = 130f
        reload = 25f
        recoil = 1.5f
        rotateSpeed = 8f
        shake = 1f
        shootCone = 8f
        targetAir = true
        targetGround = true
        ammo(
            ModItems.energyCrystal, ModBullets.empBolt,
            Items.phaseFabric, ModBullets.energyBolt
        )
        ammoPerShot = 2
        maxAmmo = 30
        requirements(Category.turret, ItemStack.with(
            Items.silicon, 80, Items.lead, 70, Items.titanium, 50, Items.phaseFabric, 30
        ))
        consumePower(3.5f)
        hasPower = true
        hasItems = true
    }

    val volcano = ItemTurret("volcano").apply {
        size = 3
        range = 120f
        reload = 45f
        recoil = 4f
        rotateSpeed = 4f
        shake = 2f
        shootCone = 15f
        inaccuracy = 6f
        targetAir = true
        targetGround = true
        ammo(
            ModItems.reinforcedAlloy, ModBullets.plasmaShell,
            Items.pyratite, mindustry.content.Bullets.fireball
        )
        ammoPerShot = 3
        maxAmmo = 15
        requirements(Category.turret, ItemStack.with(
            Items.thorium, 100, Items.titanium, 80, Items.silicon, 60, Items.plastanium, 40
        ))
        consumePower(4f)
        consumeLiquid(Liquids.water, 0.2f)
        hasPower = true
        hasItems = true
        hasLiquids = true
    }

    val annihilator = ItemTurret("annihilator").apply {
        size = 4
        range = 160f
        reload = 70f
        recoil = 6f
        rotateSpeed = 2.5f
        shake = 4f
        shootCone = 6f
        inaccuracy = 0.5f
        targetAir = true
        targetGround = true
        ammo(
            ModItems.compositeArmor, ModBullets.annihilationShell,
            ModItems.energyCrystal, ModBullets.empBolt
        )
        ammoPerShot = 4
        maxAmmo = 10
        requirements(Category.turret, ItemStack.with(
            Items.surgeAlloy, 120, Items.thorium, 100, Items.silicon, 100,
            Items.plastanium, 80, Items.phaseFabric, 50
        ))
        consumePower(8f)
        hasPower = true
        hasItems = true
    }

    fun load() {
        carbideFurnace.load()
        alloyCrucible.load()
        armorCompressor.load()
        crystalSynthesizer.load()
        carbideWall.load()
        carbideWallLarge.load()
        reinforcedWall.load()
        reinforcedWallLarge.load()
        compositeWall.load()
        compositeWallLarge.load()
        piercer.load()
        thunder.load()
        volcano.load()
        annihilator.load()
    }
}
