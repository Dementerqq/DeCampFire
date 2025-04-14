package me.dementerqq.deCampFire.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter


class DcfTabCompleter : TabCompleter {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): List<String>? {
        if (args !== null) {
            if (args.size == 1) {
                return listOf(
                  "reload"
                )
            }
        }
        return null
    }
}