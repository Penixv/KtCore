package gg.raneko.core.util

import gg.raneko.core.Core
import gg.raneko.core.profile.Profile
import gg.raneko.core.util.config.ConfigCursor
import gg.raneko.core.util.config.ConfigUtil
import java.io.File
import java.util.*

class ProfileMaker {

    private val core = Core.instance!!

    fun saveProfile(profile: Profile) {
        var configUtil = ConfigUtil(core, "players" , profile.playerName + "#" + profile.uuid.toString() + ".yml")
        var cursor = ConfigCursor(configUtil, "")
        cursor.set("death", profile.death)
        cursor.set("death_time", profile.deathTime)
        cursor.save()
    }

    fun makeProfile(name: String, uuid: UUID): Profile {
        var configUtil = ConfigUtil(core, "players" , name + "#" + uuid.toString() + ".yml")
        var cursor = ConfigCursor(configUtil, "")
        cursor.set("death", false)
        cursor.set("death_time", -1)
        cursor.save()
        return loadProfile(uuid)!!
    }

    private fun readConfig(profile: Profile, file: File) {
        val configCursor = ConfigCursor(ConfigUtil(file), "")
        profile.death = configCursor.getBoolean("death")
        profile.deathTime = configCursor.getLong("death_time")
    }

    fun loadProfile(uuid: UUID): Profile? {
        val playerDataFile = File(core.dataFolder.path + File.separatorChar + "players")
        if (!playerDataFile.exists()) {
            playerDataFile.mkdirs()
        }
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