package gg.raneko.core.util

import gg.raneko.core.Core
import gg.raneko.core.profile.Profile
import gg.raneko.core.util.config.ConfigCursor
import gg.raneko.core.util.config.ConfigUtil
import org.bukkit.entity.Player
import sun.audio.AudioPlayer.player
import java.io.File
import java.util.UUID

class ProfileMaker {

    private val core = Core.instance

    fun makeProfile(name: String, uuid: UUID): Profile {
        var configUtil = ConfigUtil(core, "players" , name + "#" + uuid.toString() + ".yml")
        var cursor = ConfigCursor(configUtil, "")
        cursor.set("death", false)
        cursor.save()
        return loadProfile(uuid)!!
    }

    private fun readConfig(profile: Profile, file: File) {
        val configCursor = ConfigCursor(ConfigUtil(file), "")
        profile.death = configCursor.getBoolean("death")
    }

    fun loadProfile(uuid: UUID): Profile? {
        val playerDataFile = File(core.dataFolder.path + File.pathSeparator + "players")
        var playerFile: File ?= null
        for (file in playerDataFile.listFiles()!!) {
            if (file.name.contains(uuid.toString())) {
                playerFile = file
                break
            }
        }
        if (playerFile == null) {
            return null
        }
        val name = playerFile.name.split("#")[0]
        val playerProfile = Profile(name, uuid)
        readConfig(playerProfile, playerFile)
        return playerProfile

    }

}