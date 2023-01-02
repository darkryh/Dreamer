package com.ead.project.dreamer.domain.apis.app

import androidx.lifecycle.MutableLiveData
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.app.model.Publicity
import com.ead.project.dreamer.data.AnimeRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class GetApplicationAds @Inject constructor(
    private val repository: AnimeRepository
) {

    fun livedata () : MutableLiveData<List<Publicity>> = getAds()

    private var publicity : MutableLiveData<List<Publicity>>?= null

    private fun getAds() : MutableLiveData<List<Publicity>> {
        val appService = repository.getAppService(repository.getAppRetrofit())
        val response : Call<List<Publicity>> = appService.getPublicity()
        response.enqueue(object : Callback<List<Publicity>> {
            override fun onResponse(call: Call<List<Publicity>>, response: Response<List<Publicity>>) {
                try { if (response.isSuccessful) publicity?.value = response.body() }
                catch ( e : Exception) { e.printStackTrace() }
            }

            override fun onFailure(call: Call<List<Publicity>>, t: Throwable) {
                DreamerApp.showLongToast(t.cause?.message.toString())
            }
        })
        return publicity?:MutableLiveData<List<Publicity>>().also { publicity = it }
    }
}