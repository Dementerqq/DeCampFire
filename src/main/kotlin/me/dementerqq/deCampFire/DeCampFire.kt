package me.dementerqq.deCampFire

import me.dementerqq.deCampFire.events.BukkitListener
import org.bukkit.plugin.java.JavaPlugin

class DeCampFire : JavaPlugin() {
    override fun onEnable() {
        server.pluginManager.registerEvents(BukkitListener(this), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
