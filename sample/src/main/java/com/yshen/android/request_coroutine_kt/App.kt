package com.yshen.android.request_coroutine_kt

import android.app.Application
import com.readystatesoftware.chuck.ChuckInterceptor
import com.yshen.android.retrofit_coroutine_kt.CoroutineRetrofit
import com.yshen.android.retrofit_coroutine_kt.RetrofitBuilder
import com.yshen.android.retrofit_coroutine_kt.extensions.ssl.SSLSocketClient
import com.yshen.android.retrofit_coroutine_kt.extensions.ssl.SSLTrustManager
import okhttp3.internal.platform.Platform
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * Created by Yshen
 * On 2021/12/30
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.INFO)
            androidContext(this@App)
            modules(request {
                CoroutineRetrofit {
                    RetrofitBuilder {
                        val sslSocketFactory = SSLSocketClient.getSSLSocketFactory()
                        val trustManager = SSLTrustManager.getTrustManager(sslSocketFactory)
                        checkNotNull(trustManager) {
                            ("Unable to extract the trust manager on "
                                    + Platform.get()
                                    + ", sslSocketFactory is "
                                    + sslSocketFactory.javaClass)
                        }
                        Retrofit.Builder()
                            .client(it
                                .addInterceptor(ChuckInterceptor(applicationContext))
                                .sslSocketFactory(sslSocketFactory, trustManager)
                                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                                .connectTimeout(30, TimeUnit.SECONDS)
                                .readTimeout(30, TimeUnit.SECONDS)
                                .writeTimeout(30, TimeUnit.SECONDS).build())
                            .baseUrl("https://run.mocky.io/v3/")
                    }
                }
            })
        }
    }
}