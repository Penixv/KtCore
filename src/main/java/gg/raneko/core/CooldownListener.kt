package gg.raneko.core

import java.util.UUID
import kotlin.collections.HashMap

class CooldownListener {

    var commandCoolDown = HashMap<String, HashMap<UUID, Long>>()
    val commandCoolDownMap = HashMap<String, Long>()

    init {
        commandCoolDownMap.put("heal", 25 * 1000)
    }

    fun getCommandCooldownTime(command: String, player: UUID): Long {
        val commandMap = commandCoolDown.get(command) ?: return -1
        val cooldown: Long = commandCoolDownMap.get(command)!!
        val playerMap = commandMap.get(player) ?: return -1
        return System.currentTimeMillis() - (cooldown + playerMap)
    }

    fun addCommandCooldown(command: String, player: UUID) {
        val commandMap = commandCoolDown.get(command)
        if (commandMap != null) {
            commandMap.put(player, System.currentTimeMillis())
        }
    }

    fun hasExpiredCommand(command: String, player: UUID): Boolean {
        val commandMap = commandCoolDown.get(command)
        if (commandMap != null) {
            val cooldown: Long = commandCoolDownMap.get(command)!!
            val oldCooldown = commandMap.get(player) ?: return true
            val current = System.currentTimeMillis() - (oldCooldown + cooldown)
            if (current >= 0) return true
        }
        return false
    }
}