package com.ead.project.dreamer.data.utils

import android.os.Build
import com.ead.project.dreamer.data.commons.Tools.Companion.getCatch
import com.ead.project.dreamer.data.database.model.AnimeProfile

class Categorizer {

    companion object {

        //private val dubbingList = listOf(Category("Latino") ,Category("Castellano"))

        private val primaryList = listOf(Category("Acción"),Category("Aventura"),Category("Ciencia Ficción"),
            Category("Comedia"),Category("Deportes"),Category("Drama"),Category("Gore"),
            Category("Ecchi"),Category("Fantasía"),Category("Horror"),Category("Lucha"),
            Category("Psicológico"),Category("Misterio"),Category("Recuerdos de la vida"),
            Category("Shojo"),Category("Shonen"),Category("Seinen"),Category("Romance"))

        private val secondaryList = listOf(Category("Carreras"),Category("Escolares"),Category("Militar"),
            Category("Mecha"),Category("Música"),Category("Parodias"),Category("Cyberpunk"),
            Category("Harem"),Category("Sobrenatural"),Category("Vampiros"),Category("Yaoi"),
            Category("Yuri"),Category("Espacial"),Category("Histórico"),Category("Artes Marciales"),
            Category("Magia"),Category("Demonios"),Category("Dementia"))

        private val nonValuableList = listOf(Category("Policía"),Category("Historia paralela"),Category("Aenime"),
            Category("Donghua"),Category("Blu-ray"),Category("Monogatari"),Category("Josei"))


        private fun getListFromRawGenres(string: String) : List<String> = string.replace("[","")
            .replace("]","")
            .split(",")

        fun configProfiles(animeProfileList : List<AnimeProfile>) : List<String> {
            for (animeProfile in animeProfileList) {
                val genreList = getListFromRawGenres(animeProfile.rawGenres)
                for (genre in genreList) {
                    for (i in primaryList.indices)
                        if (genre == primaryList[i].name) primaryList[i].votes++

                    for (i in secondaryList.indices)
                        if (genre == secondaryList[i].name) secondaryList[i].votes++

                    for (i in nonValuableList.indices)
                        if (genre == nonValuableList[i].name) nonValuableList[i].votes++
                }
            }
            val x = primaryList.sortedByDescending { it.votes }.toMutableList().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) removeIf { it.votes == 0  }
            }.map { it.name }

            val y = secondaryList.sortedByDescending { it.votes }.toMutableList().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) removeIf { it.votes == 0  }
            }.map { it.name }

            val z = nonValuableList.sortedByDescending { it.votes }.toMutableList().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) removeIf { it.votes == 0  }
            }.map { it.name }

            return listOf(x.getCatch(0),x.getCatch(1),x.getCatch(2),x.getCatch(3),x.getCatch(4),
                y.getCatch(0),y.getCatch(1),y.getCatch(2),z.getCatch(0))
        }
    }
}

data class Category (
    var name : String,
    var votes : Int = 0
)