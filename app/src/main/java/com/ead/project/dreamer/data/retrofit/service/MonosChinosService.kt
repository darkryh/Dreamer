package com.ead.project.dreamer.data.retrofit.service

import com.ead.project.dreamer.data.models.monos_chinos.LoginResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MonosChinosService {
    
    @POST("login")
    @FormUrlEncoded
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse?>


}