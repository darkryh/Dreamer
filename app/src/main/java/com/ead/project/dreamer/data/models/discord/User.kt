package com.ead.project.dreamer.data.models.discord

import com.ead.project.dreamer.data.utils.DataStore
import com.google.gson.Gson

data class User(
    val accent_color: Int,
    val avatar: String?,
    val banner: String?,
    val discriminator: String,
    val email: String,
    val flags: Int,
    val id: String,
    val premium_type: Int,
    val public_flags: Int,
    val username: String,
    val verified: Boolean,
    var rank : String = "User",
    var rankLevel : Int = 99
) {

    companion object {

        private const val RANK_USER = "User"

        private const val RANK_MPV = "Mvp"

        private const val RANK_VIP = "Vip"

        private const val RANK_ADMIN = "Admin"

        private const val RANK_OWNER = "Owner"


        private const val RANK_USER_ID = "946849009262280734"

        private const val RANK_MPV_ID = "953071633239785492"

        private const val RANK_VIP_ID = "953071933971374141"

        private const val RANK_ADMIN_ID = "934336329070833674"

        private const val RANK_OWNER_ID = "952259476864499782"

        fun get(): User? = try {
            Gson().fromJson(DataStore.readString(Discord.USER_ME), User::class.java)
        } catch (e : Exception) { null }

        fun set(user: User) = DataStore.writeStringAsync(Discord.USER_ME,Gson().toJson(user))

        fun logout() { DataStore.writeStringAsync(Discord.USER_ME,null) }

        fun isVip() : Boolean = if (get() == null) false else get()?.rankLevel == 3

        fun isNotVip() : Boolean = !isVip()

        private fun idValue(id : String) : Int {
            return when (id) {
                RANK_USER_ID -> 5
                RANK_MPV_ID -> 4
                RANK_VIP_ID -> 3
                RANK_ADMIN_ID -> 2
                RANK_OWNER_ID -> 1
                else -> 99999999
            }
        }

        fun getRank(value : Int) : String {
            return when (value) {
                5 -> RANK_USER
                4 -> RANK_MPV
                3 -> RANK_VIP
                2 -> RANK_ADMIN
                1 -> RANK_OWNER
                else -> "Rango desconocido"
            }
        }

        fun getRankValue(idList: List<String>) : Int {
            if (idList.size == 1)
                return idValue(idList[0])

            var idRol = idValue(idList[0])
            for (pos in 1 until idList.size) {
                val currentRol = idValue(idList[pos])
                if (currentRol < idRol) {
                    idRol = currentRol
                }
            }
            return idRol
        }
    }
}