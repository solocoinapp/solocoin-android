package app.solocoin.solocoin.di

import android.content.Context
import app.solocoin.solocoin.R
import app.solocoin.solocoin.repo.ApiService
import app.solocoin.solocoin.repo.NoConnectivityException
import app.solocoin.solocoin.repo.SolocoinRepository
import app.solocoin.solocoin.util.GlobalUtils.Companion.isNetworkAvailable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
val networkModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(androidContext().getString(R.string.base_url))
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
        .cache(CachingModule.mCache(context))
        .addInterceptor { chain ->
            val request = chain.request()
            if (isNetworkAvailable(context)) {
                request.newBuilder().build()
            } else {
                throw NoConnectivityException()
            }
            chain.proceed(request)
        }
        .addNetworkInterceptor(CachingModule)
        .build()
}
