package gg.raneko.core

import gg.raneko.core.command.base.HealCommand
import gg.raneko.core.command.base.PingCommand
import gg.raneko.core.listener.BasicListener
import gg.raneko.core.listener.DeathListener
import gg.raneko.core.util.ProfileMaker
import gg.raneko.core.util.ProfileRunnable
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.plugin.java.JavaPlugin

class Core : JavaPlugin() {

    companion object {
       var instance: Core? = null
    }

    lateinit var cooldownListener: CooldownListener
    lateinit var profileMaker: ProfileMaker

    override fun onEnable() {
        instance = this
        profileMaker = ProfileMaker()
        cooldownListener = CooldownListener()
        registerCommands()
        registerListeners()
        Bukkit.getScheduler().runTaskTimer(this, ProfileRunnable(), 20, 20)
    }

    private fun registerListeners() {
        Bukkit.getServer().pluginManager.registerEvents(BasicListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(DeathListener(), this)
    }

    private fun registerCommands() {
        regC(PingCommand())
        regC(HealCommand())
    }

    private fun regC(command: Command) {
        Bukkit.getServer().commandMap.register(command.name, command)
    }

}
