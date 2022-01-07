package com.yshen.android.retrofit_coroutine_kt.extensions.interceptor

import android.text.TextUtils
import com.google.gson.Gson
import com.yshen.android.retrofit_coroutine_kt.Settings
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer

/**
 * Created by Yshen
 * On 2022/1/4
 */
class MethodInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
        val urlBuilder = request.url().newBuilder()
        request.takeIf { "POST" == it.method() && "GET" == it.header(Settings.BODY_FRO_GET_HEADER) }?.let {
            request.body().takeIf { it is RequestBody }?.let {
                val buffer = Buffer()
                it.writeTo(buffer)
                val requestStr = buffer.readUtf8()
                val httpUrl = urlBuilder.build()
                val httpUrlBuilder = httpUrl.newBuilder()
                if(!TextUtils.isEmpty(requestStr)) {
                    val map = Gson().fromJson(requestStr, Map::class.java)
                    for ((k, v) in map) {
                        v?.let {
                            httpUrlBuilder.addQueryParameter(k.toString(), v.toString())
                        }
                    }
                }
                val newHttpUrl = httpUrlBuilder.build()
                requestBuilder.url(newHttpUrl.url()).method("GET", null)
                return chain.proceed(requestBuilder.build())
            }
        }
        return chain.proceed(requestBuilder.build())
    }
}