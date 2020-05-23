package app.solocoin.solocoin.app

import android.app.Application
import android.util.Log
import app.solocoin.solocoin.di.networkModule
import app.solocoin.solocoin.di.viewModelModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class SolocoinApp: Application() {

    companion object {
        var sharedPrefs: SharedPrefs? = null
     }

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = SharedPrefs(applicationContext)

        startKoin {
            androidContext(applicationContext)
            modules(networkModule, viewModelModule)
        }
    }
}