package com.zsqw123.demo.gifmaker

import android.app.Application
import android.content.Context

lateinit var app: App

class App : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        app = this
    }
}