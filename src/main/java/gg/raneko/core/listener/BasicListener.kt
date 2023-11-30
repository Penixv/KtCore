package gg.raneko.core.listener

import gg.raneko.core.Core
import gg.raneko.core.profile.Profile
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.math.BigDecimal
import java.math.RoundingMode

class BasicListener: Listener {

    val core = Core.instance!!

    @EventHandler
    fun onPreLogin(event: AsyncPlayerPreLoginEvent) {
        var profile = core.profileMaker.loadProfile(event.uniqueId)
        if (profile == null) {
            profile = core.profileMaker.makeProfile(event.name, event.uniqueId)
        }
        Profile.addProfile(profile)
    }

    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        val player = event.player
        val profile = Profile.getProfile(player.uniqueId)!!
        if (profile.death && profile.deathTime + 5 * 60 * 1000 < System.currentTimeMillis()) {
            profile.death = false
            profile.saveProfile()
            player.gameMode = GameMode.SURVIVAL
            player.allowFlight = false
        }
        if (profile.death) {
            player.gameMode = GameMode.ADVENTURE
            player.allowFlight = true
            Bukkit.getOnlinePlayers().forEach {p ->
                p.hidePlayer(core, player)
            }
        }
        Bukkit.getOnlinePlayers().forEach{p ->
            val pp = Profile.getProfile(p.uniqueId)!!
            if (pp.death) {
                player.hidePlayer(core, p)
            }
        }
    }

    @EventHandler
    fun onLogout(event: PlayerQuitEvent) {
        val profile = Profile.getProfile(event.player.uniqueId)!!
        Profile.removeProfile(profile)
        profile.saveProfile()
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
        if (victim is Player) {
            var profile = Profile.getProfile(victim.uniqueId)!!
            profile.death = true
            profile.deathTime = System.currentTimeMillis()
            profile.saveProfile()
            victim.health = 20.0
            victim.foodLevel = 20
            victim.gameMode = GameMode.ADVENTURE
            victim.allowFlight = true
            Bukkit.getOnlinePlayers().forEach {
                player ->
                player.hidePlayer(core, victim)
            }
            var nearest = Bukkit.getOnlinePlayers().stream().filter { player ->
                !player.uniqueId.equals(victim.uniqueId)
            }.min(Comparator.comparingDouble { player ->
                player.location.distanceSquared(victim.location)
            }).get()
            object : BukkitRunnable() {
                override fun run() {
                    var skull = ItemStack(Material.SKULL_ITEM,1,3)
                    var skullMeta = skull.itemMeta as SkullMeta
                    skullMeta.setOwningPlayer(victim)
                    skull.itemMeta = skullMeta
                    nearest.inventory.addItem(skull)
                    nearest.sendMessage(ChatColor.RED.toString() + "You have got " + victim.name + "'s head. Respawn them if you need.")
                }
            }.runTaskAsynchronously(core)
            victim.world.strikeLightningEffect(victim.location)
        }
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (!core.server.isHardcore) {
            return
        }
        val victim = event.entity
        val profile = Profile.getProfile(victim.uniqueId)
        profile!!.death = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onAttackEntity(event: EntityDamageByEntityEvent) {
        var attacker = event.damager
        if (attacker !is Player || event.entity !is LivingEntity) {
            return
        }
        var attackerProfile = Profile.getProfile(attacker.uniqueId)!!
        if (attackerProfile.death) return
        var target = event.entity as LivingEntity
        val finalHealth = target.health - event.finalDamage
        var textHealth = BigDecimal(finalHealth).setScale(2, RoundingMode.HALF_UP).toDouble()
        if (finalHealth > 0) {
            attacker.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(ChatColor.YELLOW.toString() + "[" + target.name + "] " + ChatColor.RED + textHealth/2 + " ♥"))
            attacker.sendMessage(ChatColor.RED.toString() + target.name + ChatColor.YELLOW + " is now at health: " + ChatColor.RED + textHealth + "/" + target.maxHealth)
        }
    }
}