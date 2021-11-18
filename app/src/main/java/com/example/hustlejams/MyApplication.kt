package com.example.hustlejams

import android.app.Application
import android.content.Context
import android.content.Intent




class MyApplication:Application() {
    override fun attachBaseContext(context: Context?) {
        super.attachBaseContext(context)
    }


    override fun onCreate() {
        super.onCreate()
        val intent = Intent(this, FinalizingOperationsService::class.java)
        startService(intent)
    }
}