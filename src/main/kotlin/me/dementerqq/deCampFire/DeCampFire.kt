package me.dementerqq.deCampFire

import me.dementerqq.deCampFire.command.DcfCMD
import me.dementerqq.deCampFire.command.DcfTabCompleter
import me.dementerqq.deCampFire.events.BukkitListener
import me.dementerqq.deCampFire.utils.ColorUtil.format
import me.dementerqq.deCampFire.utils.ColorUtil.message
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File


class DeCampFire : JavaPlugin() {
    lateinit var pConfig: FileConfiguration
    lateinit var messages: FileConfiguration

    private lateinit var configFile: File
    private lateinit var messagesFile: File

    override fun onEnable() {
        loadConfigs()
        server.pluginManager.registerEvents(BukkitListener(this), this)
        getCommand("decampfire")?.setExecutor(DcfCMD(this))
        getCommand("decampfire")?.tabCompleter = DcfTabCompleter()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }


    fun loadConfigs(executor: Any? = null) {
        this.configFile = File(dataFolder, "config.yml")
        if (!configFile.exists()) {
            saveResource("config.yml", false)
        }
        this.pConfig = YamlConfiguration.loadConfiguration(configFile)
        this.messagesFile = File(dataFolder, "messages.yml")
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false)
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile)

        checkConfig(executor)
    }
    private fun checkConfig(executor: Any? = null) {
        pConfig.set("temp", false)
        if (!pConfig.isBoolean("prefix")) { pConfig.dSet("prefix", true, executor) }

        if (!pConfig.isConfigurationSection("campfire_fire")) { pConfig.createSection("campfire_fire") }
        if (!pConfig.isConfigurationSection("campfire_regen")) { pConfig.createSection("campfire_regen") }
        val campfireFire: ConfigurationSection = pConfig.getConfigurationSection("campfire_fire")!!
        val campfireRegen: ConfigurationSection = pConfig.getConfigurationSection("campfire_regen")!!

        if (!campfireFire.isBoolean("enable")) { campfireFire.dSet("enable", true, executor) }
        if (campfireFire.getBoolean("enable")) {
            if (!campfireFire.isInt("fire_tick")) { campfireFire.dSet("fire_tick", 60, executor) }
        }

        if (!campfireRegen.isBoolean("enable")) { campfireRegen.dSet("enable", true, executor) }
        if (campfireRegen.getBoolean("enable")) {
            if (!campfireRegen.isInt("cooldown")) { campfireRegen.dSet("cooldown", 1200, executor) }
            if (!campfireRegen.isString("send_type")) { campfireRegen.dSet("send_type", "title", executor) }
            if (!campfireRegen.isString("item")) { campfireRegen.dSet("item", "stick", executor) }
            if (Material.getMaterial(campfireRegen.getString("item")?.uppercase()!!) == null) { campfireRegen.dSet("item", "stick", executor) }
            if (!campfireRegen.isInt("price")) { campfireRegen.dSet("price", 4, executor) }
            if (campfireRegen.getInt("price") !in 1..64) { campfireRegen.dSet("price", 4, executor) }
            if (!campfireRegen.isInt("particle_amount")) { campfireRegen.dSet("particle_amount", 20, executor) }
            if (!campfireRegen.isInt("radius")) { campfireRegen.dSet("radius", 5, executor) }

            if (!campfireRegen.isConfigurationSection("regeneration")) { campfireRegen.createSection("regeneration") }
            val campfireRegeneration = campfireRegen.getConfigurationSection("regeneration")!!
            if (!campfireRegeneration.isInt("time")) { campfireRegeneration.dSet("time", 80, executor) }
            if (!campfireRegeneration.isInt("lvl")) { campfireRegeneration.dSet("lvl", 0, executor) }

            if (!campfireRegeneration.isConfigurationSection("particle")) { campfireRegeneration.createSection("particle") }
            val campfireParticle: ConfigurationSection = campfireRegeneration.getConfigurationSection("particle")!!
            if (!campfireParticle.isInt("amount")) { campfireParticle.dSet("amount", 8, executor) }
            if (!campfireParticle.isDouble("radius")) { campfireParticle.dSet("radius", 0.7, executor) }
        }
        if (pConfig.getBoolean("temp")) {
            pConfig.set("temp", null)
            savePluginConfig()
            val configFile = File(dataFolder, "config.yml")
            pConfig = YamlConfiguration.loadConfiguration(configFile)
        }
    }

    private fun ConfigurationSection.dSet(key: String, value: Any, executor: Any? = null) {
        this.set(key, value)
        if (executor is Player) {
            executor.message("&7Не установлен &f$key&7, поэтому был сброшен", true)
        }
        logger.info(format("&cDCF Config | &7Не установлен &f$key&7, поэтому был сброшен"))
        pConfig.set("temp", true)
    }

    private fun savePluginConfig() {
        try {
            pConfig.save(configFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
