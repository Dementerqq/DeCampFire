package me.dementerqq.deCampFire.events

import me.dementerqq.deCampFire.utils.ColorUtil.actionbar
import me.dementerqq.deCampFire.utils.ColorUtil.format
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import kotlin.random.Random
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector

class BukkitListener(plugin: Plugin) : Listener {
    private val dcf = plugin

    @EventHandler
    fun onCampFireDamage(event: EntityDamageEvent) {
        val entity = event.entity
        if (event.cause == EntityDamageEvent.DamageCause.CAMPFIRE) {
            entity.fireTicks = 60
            if (entity is Player) {
                entity.actionbar("&cВы загорелись от костра!")
            }
        }
    }
    @EventHandler
    fun onClickCampFire(event: PlayerInteractEvent) {
        val block = event.clickedBlock
        val pl = event.player
        val item = event.item
        if (event.action.isRightClick && block?.blockData?.material  == Material.CAMPFIRE)
        if (event.material == Material.STICK && item?.amount!! >= 4)
        if (!block.hasMetadata("regen")) {
            item.amount -= 4
            block.setMetadata("regen", FixedMetadataValue(dcf, true))
            block.location.world.spawnParticle(Particle.LAVA, block.location.toCenterLocation(), 20)
            val players = dcf.server.onlinePlayers.filter { player ->
                player.location.distance(block.location) <= 5
            }
            players.forEach {player -> sendTitle(player)}
            Bukkit.getScheduler().runTaskLater(dcf, Runnable {
                block.removeMetadata("regen", dcf)
            }, 1200)
        }
        else {
            pl.actionbar("&cПодождите немного перед повторным использованием!")
        }
    }
    private fun sendTitle(pl: Player) {
        if (!pl.hasMetadata("regen")) {
            pl.setMetadata("regen", FixedMetadataValue(dcf, true))
            pl.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 80, 1))
            Bukkit.getScheduler().runTaskLater(dcf, Runnable {
                pl.sendTitle(format("&aТы почувствовал уют..."), format("&7Пламя... Его мягкое тепло..."), 10, 40, 10)
            }, 15)
            Bukkit.getScheduler().runTaskLater(dcf, Runnable {
                pl.sendTitle(format("&aЭмоции переполняют"), format("&7Это прекрасно... Такая гармония..."), 10, 40, 10)
                pl.removeMetadata("regen", dcf)
            }, 80)
            for (i in 0..7) {
                val loc = pl.location
                loc.y += 1.5
                loc.add(
                    Vector(
                        Random.nextDouble(-0.7, 0.7),
                        Random.nextDouble(-0.7, 0.7),
                        Random.nextDouble(-0.7, 0.7)
                    )
                )
                pl.world.spawnParticle(Particle.HAPPY_VILLAGER, loc, 1)
            }
        }
    }
}