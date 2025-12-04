package com.example.lokanala

import android.app.Application
import android.content.Context

class MyApp : Application() {

    init {
        app = this
    }

    companion object {
        private lateinit var app: MyApp
        val appContext: Context get() = app.applicationContext
    }
}
