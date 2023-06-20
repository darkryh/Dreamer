package com.ead.project.dreamer.domain.apis.monos_chinos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.models.monos_chinos.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class Login @Inject constructor(private val repository: AnimeRepository) {

    fun liveData(username: String,password: String) : LiveData<LoginResponse?> = login(username, password)

    private var loginResponse : MutableLiveData<LoginResponse?>? = null

    private fun login(username: String,password : String) : MutableLiveData<LoginResponse?> {
        loginResponse = MutableLiveData()
        val response : Call<LoginResponse?> = repository.getMonosChinosService().login(username, password)

        response.enqueue(object : Callback<LoginResponse?>{
            override fun onResponse(call: Call<LoginResponse?>, response: Response<LoginResponse?>) {
                try { if (response.isSuccessful) loginResponse?.value = response.body() }
                catch ( e : Exception) { e.printStackTrace() }
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                Log.e("error", "onFailure: ${t.cause?.message.toString()}")
            }

        })

        return loginResponse?: MutableLiveData<LoginResponse?>().also { loginResponse = it }
    }
}