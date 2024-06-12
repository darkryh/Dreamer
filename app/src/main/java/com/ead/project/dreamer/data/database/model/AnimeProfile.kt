package com.ead.project.dreamer.data.database.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ead.project.dreamer.data.utils.ui.mechanism.EqualsDiffUtil
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "anime_profile_table")
data class AnimeProfile (
    @PrimaryKey(autoGenerate = false)
    val id : Int,
    val coverPhoto : String,
    val profilePhoto : String,
    val title : String,
    val titleAlternate : String ="null",
    val rating : Float,
    val state : String,
    val description : String,
    val date : String,
    val genres : List<String>,
    val rawGenres : String,
    val size : Int = 0,
    val lastChapterId : Int = 0,
    val reference : String? = null,
    val isFavorite : Boolean = false
) : Parcelable, EqualsDiffUtil {

    companion object {
        const val PROFILE_FINAL_STATE = "Finalizado"
        const val PROFILE_RELEASE_STATE = "Estreno"
        const val MINIMUM_RECOMMEND_VALUE = 4.6f

        const val TYPE_UNCENSORED = "Sin Censura"
        const val TYPE_ECCHI = "Ecchi"
        const val TYPE_BOYS_LOVE = "Yaoi"

        fun fake(id: Int) : AnimeProfile {
             return AnimeProfile(
                 id = id,
                 coverPhoto = "",
                 profilePhoto = "",
                 title = "",
                 titleAlternate = "",
                 rating = -1f,
                 state = "",
                 description = "",
                 date = "",
                 genres = emptyList(),
                 rawGenres = "",
                 size = 0,
             )
        }
    }

    override fun equalsHeader(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val animeProfile: AnimeProfile = other as AnimeProfile
        return title == animeProfile.title
    }

    override fun equalsContent(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val animeProfile: AnimeProfile = other as AnimeProfile
        return coverPhoto == animeProfile.coverPhoto
                && profilePhoto == animeProfile.profilePhoto
                && rating == animeProfile.rating
                && state == animeProfile.state
                && description == animeProfile.description
                && date == animeProfile.date
                && genres == animeProfile.genres
                && size == animeProfile.size
                && lastChapterId == animeProfile.lastChapterId
                && reference == animeProfile.reference

    }

    fun isAuthorizedData() = (TYPE_BOYS_LOVE !in this.rawGenres
                && (TYPE_ECCHI !in this.rawGenres && isBlacklisted()))
                || isWhiteListedTitle()

    private fun isBlacklisted() = "DxD" !in this.title && "Zero no Tsukaima" !in this.title
            && "Trinity Seven" !in this.title && "Bannou Bunka Neko-Musume" !in this.title
            && "Eromanga-sensei" !in this.title && "Isekai Maou to Shoukan Shoujo no Dorei Majutsu" !in this.title
            && "Monster Musume no Iru Nichijou" !in this.title && "IS: Infinite Stratos" !in this.title
            && "Strike the Blood" !in this.title && "Sora no Otoshimono" !in this.title
            && "Renai Boukun" !in this.title && "Kawaikereba Hentai demo Suki ni Natte Kuremasu ka?" !in this.title
            && "Yuragi-sou no Yuuna-san" !in this.title && "Love Hina" !in this.title
            && "Boku no Kanojo ga Majimesugiru Sho-bitch na Ken" !in this.title && "Princess Lover!" !in this.title
            && "Akaneiro ni Somaru Saka" !in this.title && "Nobunaga-sensei no Osanazuma" !in this.title
            && "Vandread" !in this.title && "Nogizaka Haruka no Himitsu" !in this.title
            && "Onegai Teacher" !in this.title && "Tsuujou Kougeki ga Zentai Kougeki de Ni-kai Kougeki no Okaasan wa Suki Desu ka?" !in this.title
            && "Da Capo" !in this.title && "Haiyore! Nyaruko-san" !in this.title
            && "UFO Princess Valkyrie" !in this.title && "Dororon Enma-kun Meeramera" !in this.title
            && "Upotte!!" !in this.title && "Ore ga Suki nano wa Imouto dakedo Imouto ja Nai" !in this.title
            && "Sumomomo Momomo: Chijou Saikyou no Yome" !in this.title && "Anitore!" !in this.title
            && "Juliet" !in this.title

    private fun isWhiteListedTitle() = ("Danmachi Latino" == this.title || "Kaguya-sama wa Kokurasetai: Ultra Romantic" == this.title
            ||"Mushoku Tensei: Isekai Ittara Honki Dasu Part 2" == this.title ||"Uzaki-chan wa Asobitai!" == this.title
            ||"Gleipnir" == this.title ||"No Game No Life" in this.title
            ||"Plunderer" == this.title ||"Saenai Heroine no Sodatekata" in this.title
            ||"Killing Bites" == this.title ||"Danmachi" == this.title
            ||"Yamada-kun to 7-nin no Majo" in this.title ||"Kuzu no Honkai" == this.title
            ||"Highschool of the Dead Latino" == this.title ||"Highschool of the Dead Castellano" == this.title
            ||"Tsukimonogatari" == this.title ||"Air Gear" in this.title
            ||"Nanatsu no Taizai" in this.title ||"Re:Zero kara Hajimeru Isekai Seikatsu - Memory Snow" == this.title
            ||"Kobayashi san Chi no Maid Dragon" in this.title ||"Gantz" in this.title
            ||"Highschool of the Dead" == this.title ||"Kill la Kill" == this.title
            ||"Angel Beats" == this.title && "Shokugeki no Souma" in this.title)
}