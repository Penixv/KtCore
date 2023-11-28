package gg.raneko.core

import java.util.UUID
import kotlin.collections.HashMap

class CooldownListener {

    var commandCoolDown = HashMap<String, HashMap<UUID, Long>>()
    val commandCoolDownMap = HashMap<String, Long>()

    init {
        registerCommand("heal", 25*1000)
    }

    private fun registerCommand(command: String, delay: Long) {
        commandCoolDown.put(command, HashMap())
        commandCoolDownMap.put(command, delay)
    }

    fun getCommandCooldownTime(command: String, player: UUID): Long {
        val commandMap = commandCoolDown.get(command) ?: return 0
        val cooldown: Long = commandCoolDownMap.get(command)!!
        val playerMap = commandMap.get(player) ?: return 0
        return (cooldown + playerMap) - System.currentTimeMillis()
    }

    fun addCommandCooldown(command: String, player: UUID) {
        val commandMap = commandCoolDown.get(command)
        commandMap?.put(player, System.currentTimeMillis())
    }

    fun hasExpiredCommand(command: String, player: UUID): Boolean {
        val commandMap = commandCoolDown.get(command)
        if (commandMap != null) {
           return getCommandCooldownTime(command, player) <= 0
        }
        return true
    }
}