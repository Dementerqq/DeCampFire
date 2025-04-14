package me.dementerqq.deCampFire.events

import me.dementerqq.deCampFire.DeCampFire
import me.dementerqq.deCampFire.utils.ColorUtil.actionbar
import me.dementerqq.deCampFire.utils.ColorUtil.format
import me.dementerqq.deCampFire.utils.ColorUtil.message
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import kotlin.random.Random


class BukkitListener(plugin: DeCampFire) : Listener {
    private val dcf = plugin

    @EventHandler
    fun onCampFireDamage(event: EntityDamageEvent) {
        val pConfig = dcf.pConfig
        val messages = dcf.messages
        val campfireFire: ConfigurationSection = pConfig.getConfigurationSection("campfire_fire")!!
        if (campfireFire.getBoolean("enable")) {
            val entity = event.entity
            if (event.cause == EntityDamageEvent.DamageCause.CAMPFIRE) {
                entity.fireTicks = campfireFire.getInt("fire_tick")
                if (entity is Player) {
                    entity.actionbar(messages.getString("campfire_fire")!!)
                }
            }
        }
    }
    @EventHandler
    fun onClickCampFire(event: PlayerInteractEvent) {
        val pConfig = dcf.pConfig
        val campfireRegen: ConfigurationSection = pConfig.getConfigurationSection("campfire_regen")!!
        if (campfireRegen.getBoolean("enable")) {
            val block = event.clickedBlock
            val pl = event.player
            val item = event.item
            if (event.action.isRightClick && block?.blockData?.material == Material.CAMPFIRE)
                if (event.material.name == campfireRegen.getString("item")?.uppercase() && item?.amount!! >= campfireRegen.getInt("price"))
                    if (!block.hasMetadata("regen")) {
                        item.amount -= campfireRegen.getInt("price")
                        block.setMetadata("regen", FixedMetadataValue(dcf, true))
                        block.location.world.spawnParticle(Particle.LAVA, block.location.toCenterLocation(), campfireRegen.getInt("particle_amount"))
                        val players = dcf.server.onlinePlayers.filter { player ->
                            player.location.distance(block.location) <= campfireRegen.getInt("radius")
                        }
                        players.forEach { player -> sendTitle(player) }
                        Bukkit.getScheduler().runTaskLater(dcf, Runnable {
                            block.removeMetadata("regen", dcf)
                        }, campfireRegen.getLong("cooldown"))
                    } else {
                        pl.actionbar("&cПодождите немного перед повторным использованием!")
                    }
        }

    }
    private fun sendTitle(pl: Player) {
        if (!pl.hasMetadata("regen")) {
            val pConfig = dcf.pConfig
            val messages = dcf.messages
            val campfireRegen: ConfigurationSection = pConfig.getConfigurationSection("campfire_regen")!!
            val campfireRegeneration: ConfigurationSection = campfireRegen.getConfigurationSection("regeneration")!!
            pl.setMetadata("regen", FixedMetadataValue(dcf, true))
            pl.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, campfireRegeneration.getInt("time"), campfireRegeneration.getInt("lvl")))
            Bukkit.getScheduler().runTaskLater(dcf, Runnable {
                val prefix = messages.getString("prefix")!!
                if (campfireRegen.getString("send_type") == "title") {
                    pl.sendTitle(
                        format(messages.getString("regen_text")!!, Pair("{prefix}", prefix)),
                        format(messages.getString("regen_subtitle")!!),
                        10,
                        40,
                        10
                    )
                } else if (campfireRegen.getString("send_type") == "actionbar") {
                    pl.actionbar(messages.getString("regen_text")!!, Pair("{prefix}", prefix))
                } else {
                    pConfig.let { pl.message(messages.getString("regen_text")!!, it.getBoolean("prefix"), Pair("{prefix}", prefix)) }
                } }, 15)
            Bukkit.getScheduler().runTaskLater(dcf, Runnable {
                pl.removeMetadata("regen", dcf)
            }, 40)
            val amount = campfireRegeneration.getConfigurationSection("particle")?.getInt("amount")!!
            for (i in 1..amount) {
                val loc = pl.location
                loc.y += 1.5
                val vectorRadius = campfireRegeneration.getConfigurationSection("particle")?.getDouble("radius")!!
                loc.add(
                    Vector(
                        Random.nextDouble(-vectorRadius, vectorRadius),
                        Random.nextDouble(-vectorRadius, vectorRadius),
                        Random.nextDouble(-vectorRadius, vectorRadius)
                    )
                )
                pl.world.spawnParticle(Particle.HAPPY_VILLAGER, loc, 1)
            }
        }
    }
}