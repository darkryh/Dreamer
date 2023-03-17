package com.ead.project.dreamer.domain.apis.app

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ead.project.dreamer.app.model.AppStatus
import com.ead.project.dreamer.data.AnimeRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class GetAppStatusVersion @Inject constructor(
    private val repository: AnimeRepository
) {

    fun livedata () : MutableLiveData<AppStatus> = getAppStatus()

    private var appStatus : MutableLiveData<AppStatus>?= null

    private fun getAppStatus() : MutableLiveData<AppStatus> {
        val appService = repository.getAppService(repository.getAppRetrofit())
        val response : Call<AppStatus> = appService.getAppStatus()
        response.enqueue(object : Callback<AppStatus> {
            override fun onResponse(call: Call<AppStatus>, response: Response<AppStatus>) {
                try { if (response.isSuccessful) appStatus?.value = response.body() }
                catch ( e : Exception) { e.printStackTrace() }
            }
            override fun onFailure(call: Call<AppStatus>, t: Throwable) {
                Log.e("error", "onFailure: ${t.cause?.message.toString()}", )
            }
        })
        return appStatus?:MutableLiveData<AppStatus>().also { appStatus = it }
    }
}