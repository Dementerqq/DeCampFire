package me.dementerqq.deCampFire.utils

import net.kyori.adventure.text.Component
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object ColorUtil {

    fun format(text: String, vararg args: Pair<String, String>): String {
        return ChatColor.translateAlternateColorCodes('&', applyArgs(text, *args))
    }

    fun CommandSender.message(msg: String, prefixEnable: Boolean, vararg args: Pair<String, String>) {
        val prefix =
                "&8[&x&6&D&6&D&6&DD&x&7&6&6&3&6&3e&x&7&F&5&9&5&9C&x&8&8&4&E&4&Ea&x&9&1&4&4&4&4m&x&9&B&3&A&3&Ap&x&A&4&3&0&3&0F&x&A&D&2&5&2&5i&x&B&6&1&B&1&Br&x&B&F&1&1&1&1e&8]"
        if (prefixEnable) {
            sendMessage(format("$prefix $msg", *args))
        } else {
            sendMessage(format(msg, *args)) 
        }
    }

    fun CommandSender.actionbar(msg: String, vararg args: Pair<String, String>) {
        sendActionBar(Component.text((format(msg, *args))))
    }

    private fun applyArgs(text: String, vararg args: Pair<String, String>): String {
        var result = text
        for (arg in args) {
            result = result.replace(arg.first, arg.second)
        }

        return result
    }
}