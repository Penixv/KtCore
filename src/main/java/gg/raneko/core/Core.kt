package gg.raneko.core

import gg.raneko.core.command.base.HealCommand
import gg.raneko.core.command.base.PingCommand
import gg.raneko.core.listener.BasicListener
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.plugin.java.JavaPlugin

class Core : JavaPlugin() {

    companion object {
        lateinit var instance: Core
    }

    val cooldownListener = CooldownListener()

    override fun onEnable() {
        instance = this
        registerCommands()
        registerListeners()
    }

    private fun registerListeners() {
        Bukkit.getServer().pluginManager.registerEvents(BasicListener(), this)
    }

    private fun registerCommands() {
        regC(PingCommand())
        regC(HealCommand())
    }

    private fun regC(command: Command) {
        Bukkit.getServer().commandMap.register(command.name, command)
    }

}
