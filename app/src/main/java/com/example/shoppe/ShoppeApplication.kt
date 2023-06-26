package com.example.shoppe

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ShoppeApplication : Application() {

    companion object {
        lateinit var context: ShoppeApplication private set
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}