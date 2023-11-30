package gg.raneko.core.command.base

import gg.raneko.core.Core
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class HealCommand: Command("heal") {

    val cooldownListener = Core.instance!!.cooldownListener

    override fun execute(sender: CommandSender, p1: String, p2: Array<out String>): Boolean {
        if (sender is Player) {
            if (!cooldownListener.hasExpiredCommand("heal", sender.uniqueId)) {
                sender.sendMessage(ChatColor.RED.toString() + "Command 'heal' is now on cooldown for "
                        + cooldownListener.getCommandCooldownTime("heal", sender.uniqueId)/1000 + "s !")
                return true
            }
            if (p2.isEmpty()) {
                sender.health = sender.maxHealth
                sender.foodLevel = 20
                sender.fireTicks = 0
                sender.sendMessage(ChatColor.GREEN.toString() + "You have been healed ~")
                cooldownListener.addCommandCooldown("heal", sender.uniqueId)
            } else {
                val target: Player? = Bukkit.getPlayer(p2[0])
                if (target == null) {
                    sender.sendMessage(ChatColor.RED.toString() + p2[0] + " does not exist !")
                    return true
                }
                target.health = target.maxHealth
                target.foodLevel = 20
                target.fireTicks = 0
                sender.sendMessage(ChatColor.GREEN.toString() + target.name + " have been healed ~")
                target.sendMessage(ChatColor.GREEN.toString() + "You have been healed by " + sender.name)
                cooldownListener.addCommandCooldown("heal", sender.uniqueId)
            }
        } else {
            sender.sendMessage(ChatColor.RED.toString() + "You must be a player !")
        }
        return true
    }

}