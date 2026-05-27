package mtech.content

import arc.graphics.Color
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.bullet.BasicBulletType
import mindustry.entities.bullet.BulletType

object ModBullets {
    val carbideShot: BulletType = BasicBulletType(6f, 38f).apply {
        width = 5f
        height = 9f
        lifetime = 35f
        shootEffect = Fx.shootBig
        smokeEffect = Fx.shootBigSmoke
        ammoMultiplier = 3f
        pierce = true
        pierceCap = 3
        hitSize = 3f
        hitEffect = Fx.hitBulletBig
        despawnEffect = Fx.hitBulletBig
        frontColor = Color.valueOf("8c9ba8")
        backColor = Color.valueOf("5c6b78")
    }

    val energyBolt: BulletType = BasicBulletType(5f, 28f).apply {
        width = 4f
        height = 7f
        lifetime = 40f
        shootEffect = Fx.shootHeal
        ammoMultiplier = 4f
        hitSize = 4f
        lightning = 2
        lightningLength = 8
        lightningDamage = 10f
        status = StatusEffects.shocked
        statusDuration = 60f
        hitEffect = Fx.hitLaser
        despawnEffect = Fx.hitLaser
        frontColor = Color.valueOf("7fffd4")
        backColor = Color.valueOf("3fbfa4")
    }

    val plasmaShell: BulletType = BasicBulletType(4f, 25f).apply {
        width = 6f
        height = 8f
        lifetime = 50f
        shootEffect = Fx.shootLiquid
        ammoMultiplier = 2f
        splashDamage = 30f
        splashDamageRadius = 24f
        hitSize = 8f
        makeFire = true
        incendAmount = 5
        hitEffect = Fx.blastExplosion
        despawnEffect = Fx.blastExplosion
        frontColor = Color.valueOf("ff6433")
        backColor = Color.valueOf("cc3300")
    }

    val annihilationShell: BulletType = BasicBulletType(3.5f, 65f).apply {
        width = 12f
        height = 16f
        lifetime = 60f
        shootEffect = Fx.shootBig
        smokeEffect = Fx.shootBigSmoke
        ammoMultiplier = 1f
        splashDamage = 120f
        splashDamageRadius = 48f
        hitSize = 14f
        pierce = true
        pierceCap = 5
        makeFire = true
        incendAmount = 10
        hitEffect = Fx.blastExplosion
        despawnEffect = Fx.blastExplosion
        frontColor = Color.valueOf("8a9b50")
        backColor = Color.valueOf("4a5b20")
    }

    val empBolt: BulletType = BasicBulletType(4f, 15f).apply {
        width = 3f
        height = 5f
        lifetime = 45f
        shootEffect = Fx.shootHeal
        ammoMultiplier = 5f
        hitSize = 3f
        lightning = 4
        lightningLength = 12
        lightningDamage = 8f
        status = StatusEffects.shocked
        statusDuration = 120f
        hitEffect = Fx.hitLaser
        despawnEffect = Fx.hitLaser
        frontColor = Color.valueOf("00e5ff")
        backColor = Color.valueOf("0088cc")
    }
}
