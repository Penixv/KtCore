package gg.raneko.core.listener

import gg.raneko.core.Core
import gg.raneko.core.profile.Profile
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.Skull
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import sun.audio.AudioPlayer.player

class DeathListener: Listener {

    @EventHandler
    fun onOxygenConsume(event: EntityAirChangeEvent) {
        if (event.entity !is Player) return
        val player = event.entity
        val profile = Profile.getProfile(player.uniqueId)!!
        if (profile.death) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onEffectAdding(event: EntityEffectAddedEvent) {
        if (event.entity !is Player) return
        val player = event.entity
        val profile = Profile.getProfile(player.uniqueId)!!
        if (profile.death) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBeingTargeted(event: EntityTargetEvent) {
        if (event.target !is Player) return
        val player = event.target
        val profile = Profile.getProfile(player.uniqueId)!!
        if (profile.death) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onAttackEntity(event: EntityDamageByEntityEvent) {
        if (event.damager !is Player) return
        val player = event.damager
        val profile = Profile.getProfile(player.uniqueId)!!
        if (profile.death) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPickUpItem(event: PlayerPickupItemEvent) {
        val player = event.player
        val profile = Profile.getProfile(player.uniqueId)!!
        if (profile.death) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onFoodLevelChanged(event: FoodLevelChangeEvent) {
        val profile = Profile.getProfile(event.entity.uniqueId)!!
        if (profile.death) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDropItem(event: PlayerDropItemEvent) {
        val profile = Profile.getProfile(event.player.uniqueId)!!
        if (profile.death) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDeadPlayerDamaged(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        val player = event.entity as Player
        val profile = Profile.getProfile(player.uniqueId)!!
        if (profile.death) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val profile = Profile.getProfile(event.player.uniqueId)!!
        if (profile.death) {
            event.isCancelled = true
        }
    }

    inline fun <R> notNull(vararg args: Any?, block: () -> R) =
        when {
            args.filterNotNull().size == args.size -> block()
            else -> null
        }

    @EventHandler
    fun onRespawnPlayer(event: BlockPlaceEvent) {
        if (event.blockPlaced.type == Material.SKULL) {
            var skullBlock = event.blockPlaced
            val world = skullBlock.world
            var block1: Block? = world.getBlockAt(skullBlock.location.add(0.0, -1.0, 0.0))
            var block2: Block? = world.getBlockAt(skullBlock.location.add(0.0, -2.0, 0.0))
            if (block1 != null && block2 != null) {
                if (block1!!.type == Material.SOUL_SAND && block2!!.type == Material.SOUL_SAND) {
                    var skull = skullBlock.state as Skull
                    skull.hasOwner().let {
                        val toRespawn = Bukkit.getPlayer(skull.owner)
                        if (toRespawn != null) {
                            val profile = Profile.getProfile(toRespawn.uniqueId)!!
                            if (!profile.death) {
                                skullBlock.type = Material.AIR
                                block1.type = Material.AIR
                                block2.type = Material.AIR
                                Particle.BLOCK_DUST.builder().location(skullBlock.location)
                                    .data(Material.SOUL_SAND.data)
                                    .count(15)
                                    .offset(5.0, 5.0 , 5.0).spawn()
                                return
                            }
                            profile.death = false
                            profile.saveProfile()
                            toRespawn.teleport(skull.location)
                            toRespawn.allowFlight = false
                            toRespawn.gameMode = GameMode.SURVIVAL
                            toRespawn.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 5))
                            toRespawn.sendMessage(ChatColor.GREEN.toString() + "You have been respawned by " + event.player.name)
                            Bukkit.getOnlinePlayers().forEach {
                                player -> player.showPlayer(Core.instance, toRespawn)
                            }
                            skullBlock.type = Material.AIR
                            block1.type = Material.AIR
                            block2.type = Material.AIR
                            Particle.BLOCK_DUST.builder().location(skullBlock.location)
                                .data(Material.SOUL_SAND.data)
                                .count(15)
                                .offset(5.0, 5.0 , 5.0).spawn()
                        }
                    }
                }
            }
        }
    }
}