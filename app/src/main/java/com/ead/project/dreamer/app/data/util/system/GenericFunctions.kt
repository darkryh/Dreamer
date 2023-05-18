package com.ead.project.dreamer.app.data.util.system

import com.google.gson.Gson


inline fun <reified T> getObject(string: String) : T = Gson().fromJson(string, T::class.java)