package com.example.idttesttask

import android.app.Application
import com.example.idttesttask.data.di.dataModule
import com.example.idttesttask.domain.di.domainModule
import com.example.idttesttask.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class IdtTestTaskApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@IdtTestTaskApplication)
            modules(domainModule, dataModule, presentationModule)
        }
    }
}
