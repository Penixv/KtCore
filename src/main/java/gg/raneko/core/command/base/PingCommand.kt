package gg.raneko.core.command.base

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player

class PingCommand : Command("ping") {
    override fun execute(sender: CommandSender, str1: String, strs2: Array<out String>): Boolean {
        if (strs2.isEmpty()) {
            if (sender is Player) {
                sender.sendMessage(ChatColor.AQUA.toString() + "Your current ping is " + ChatColor.WHITE + (sender as CraftPlayer).handle.ping + "ms")
            } else {
                sender.sendMessage(ChatColor.RED.toString() + "You must be a player !")
            }
        } else {
            if (strs2.size != 1) {
                sender.sendMessage(ChatColor.RED.toString() + "Usage: /ping <player>")
                return true
            }
            val target: Player ? = Bukkit.getPlayer(strs2[0])
            if (target == null) {
                sender.sendMessage(ChatColor.RED.toString()+ strs2[0] + " does not exist !")
                return true
            }
            sender.sendMessage(ChatColor.AQUA.toString() + target.name + " current ping is " + ChatColor.WHITE + (sender as CraftPlayer).handle.ping + "ms")
        }
        return true
    }

}