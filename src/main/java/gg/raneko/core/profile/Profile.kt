package gg.raneko.core.profile

import gg.raneko.core.Core
import java.util.UUID

class Profile(name: String, uid: UUID) {

    var death = false
    var deathTime = -1L

    fun saveProfile() {
        core.profileMaker.saveProfile(this)
    }

    val playerName: String = name
    val uuid: UUID = uid

    companion object {
        val core = Core.instance!!
        val profiles = ArrayList<Profile>()

        fun getProfile(uuid: UUID): Profile? {
            for (profile in profiles) {
                if (profile.uuid == uuid) return profile
            }
            return null
        }

        fun getProfile(name: String): Profile? {
            for (profile in profiles) {
                if (profile.playerName == name) return profile
            }
            return null
        }

        fun removeProfile(profile: Profile) {
            profiles.remove(profile)
        }

        fun addProfile(profile: Profile) {
            profiles.add(profile)
        }
    }
}