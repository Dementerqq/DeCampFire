package me.dementerqq.deCampFire.command

import me.dementerqq.deCampFire.DeCampFire
import me.dementerqq.deCampFire.utils.ColorUtil.message
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender


class DcfCMD(plugin: DeCampFire) : CommandExecutor {
    private val dcf = plugin
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (!sender.hasPermission("decampfire.admin")) {
            sender.message("&cУ вас нету прав на использование данной команды!", true)
            return true
        }
        if (args?.size != 0) {
            when (args?.get(0)) {
                "reload" -> {
                    dcf.loadConfigs()
                    sender.message("&7Перезагружен!", true)
                    return true
                }
                else -> {
                    sender.message("&cИспользуй /decampfire reload", true)
                    return true
                }
            }
        }

        sender.message("&cИспользуй /decampfire reload", true)
        return true
    }
}