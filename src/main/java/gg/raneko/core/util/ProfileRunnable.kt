package gg.raneko.core.util

import gg.raneko.core.Core
import gg.raneko.core.profile.Profile
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class ProfileRunnable : Runnable {
    override fun run() {
        if (Bukkit.getOnlinePlayers().isNotEmpty()) {
            Bukkit.getOnlinePlayers().forEach { player ->
                val profile = Profile.getProfile(player.uniqueId)!!
                if (profile.death && profile.deathTime + 5 * 60 * 1000 < System.currentTimeMillis()) {
                    profile.death = false
                    profile.saveProfile()
                    player.allowFlight = false
                    player.gameMode = GameMode.SURVIVAL
                    player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 5))
                    player.sendMessage(ChatColor.GREEN.toString() + "You have been respawned.")
                    Bukkit.getOnlinePlayers().forEach { other -> other.showPlayer(Core.instance, player) }
                }
            }
        }
    }
}