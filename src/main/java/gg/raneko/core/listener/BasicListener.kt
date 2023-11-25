package gg.raneko.core.listener

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.math.BigDecimal
import java.math.RoundingMode

class BasicListener: Listener {

    @EventHandler
    fun onAttackEntity(event: EntityDamageByEntityEvent) {
        var attacker = event.damager
        if (attacker !is Player || event.entity !is LivingEntity) {
            return
        }
        var target = event.entity as LivingEntity
        val finalHealth = target.health - event.finalDamage
        var textHealth = BigDecimal(finalHealth).setScale(2, RoundingMode.HALF_UP).toDouble()
        if (finalHealth > 0) {
            attacker.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(ChatColor.YELLOW.toString() + "[" + target.name + "] " + ChatColor.RED + textHealth/2 + " ♥"))
            attacker.sendMessage(ChatColor.RED.toString() + target.name + ChatColor.YELLOW + " is now at health: " + ChatColor.RED + textHealth + "/" + target.maxHealth)
        }
    }
}