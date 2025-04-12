package me.dementerqq.deCampFire.utils

import net.kyori.adventure.text.Component
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object ColorUtil {

    fun format(text: String, vararg args: Pair<String, String>): String {
        return ChatColor.translateAlternateColorCodes('&', applyArgs(text, *args))
    }

    fun CommandSender.message(msg: String, vararg args: Pair<String, String>) {
        sendMessage(format(msg, *args))
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