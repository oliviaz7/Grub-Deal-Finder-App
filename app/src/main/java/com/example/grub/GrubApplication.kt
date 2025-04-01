package com.example.grub

import android.app.Application
import com.example.grub.data.AppContainer
import com.example.grub.data.AppContainerImpl

class GrubApplication : Application() {
    // AppContainer instance used by the rest of classes to obtain dependencies
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}
