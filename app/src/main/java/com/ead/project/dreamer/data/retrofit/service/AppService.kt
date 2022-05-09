package com.ead.project.dreamer.data.retrofit.service

import com.ead.project.dreamer.app.model.AppStatus
import com.ead.project.dreamer.app.model.Publicity
import retrofit2.Call
import retrofit2.http.GET

interface AppService {

    @GET("app_status")
    fun getAppStatus() : Call<AppStatus>

    @GET("publicity")
    fun getPublicity() : Call<Publicity>
}