package gg.raneko.core.listener

import gg.raneko.core.Core
import gg.raneko.core.profile.Profile
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.math.BigDecimal
import java.math.RoundingMode

class BasicListener: Listener {

    val core = Core.instance

    @EventHandler
    fun onPreLogin(event: AsyncPlayerPreLoginEvent) {
        var profile = core.profileMaker.loadProfile(event.uniqueId)
        if (profile == null) {
            profile = core.profileMaker.makeProfile(event.name, event.uniqueId)
        }
        Profile.addProfile(profile)
    }

    @EventHandler
    fun onLogout(event: PlayerQuitEvent) {
        Profile.removeProfile(Profile.getProfile(event.player.uniqueId)!!)
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        var victim = event.entity
        if (victim.killer is Player) {
            var killer: Player = victim.killer
            if (victim.maxHealth <= 20) {
                killer.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION,30, 1))
                killer.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 200, 0))
            } else {
                killer.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION,50, 2))
                killer.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 200, 0))
            }
        }
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (!core.server.isHardcore) {
            return
        }
        val victim = event.entity
    }

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