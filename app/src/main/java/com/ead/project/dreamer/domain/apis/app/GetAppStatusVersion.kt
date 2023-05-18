package com.ead.project.dreamer.domain.apis.app

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ead.project.dreamer.app.model.AppBuild
import com.ead.project.dreamer.data.AnimeRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class GetAppStatusVersion @Inject constructor(
    private val repository: AnimeRepository
) {

    fun livedata () : MutableLiveData<AppBuild> = getAppStatus()

    private var appBuild : MutableLiveData<AppBuild>?= null

    private fun getAppStatus() : MutableLiveData<AppBuild> {
        val appService = repository.getAppService(repository.getAppRetrofit())
        val response : Call<AppBuild> = appService.getAppStatus()
        response.enqueue(object : Callback<AppBuild> {
            override fun onResponse(call: Call<AppBuild>, response: Response<AppBuild>) {
                try { if (response.isSuccessful) appBuild?.value = response.body() }
                catch ( e : Exception) { e.printStackTrace() }
            }
            override fun onFailure(call: Call<AppBuild>, t: Throwable) {
                Log.e("error", "onFailure: ${t.cause?.message.toString()}", )
            }
        })
        return appBuild?:MutableLiveData<AppBuild>().also { appBuild = it }
    }
}