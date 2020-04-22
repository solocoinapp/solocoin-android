package app.solocoin.solocoin.di

import android.content.Context
import app.solocoin.solocoin.repo.ApiService
import app.solocoin.solocoin.repo.SolocoinRepository
import app.solocoin.solocoin.util.isNetworkAvailable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

@ExperimentalCoroutinesApi
val networkModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient(androidContext()))
            .build()
            .create(ApiService::class.java)
    }

    single {
        SolocoinRepository(get())
    }
}

fun getOkHttpClient(context: Context): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            if (isNetworkAvailable(context)) {
                request.newBuilder().header(
                    "Cache-Control",
                    "public, max-age=" + 5
                ).build()
            } else {
                throw NoConnectivityException()
            }
            chain.proceed(request)
        }
        .build()
}

class NoConnectivityException : IOException() {
    override val message: String
        get() = "No network available, please check your WiFi or Data connection"
}