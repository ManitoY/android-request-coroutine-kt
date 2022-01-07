package com.yshen.android.retrofit_coroutine_kt

import com.yshen.android.retrofit_coroutine_kt.coroutine.FlowCallAdapterFactory
import com.yshen.android.retrofit_coroutine_kt.extensions.converter.CustomGsonConverterFactory
import com.yshen.android.retrofit_coroutine_kt.extensions.interceptor.MethodInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * Created by Yshen
 * On 2021/12/30
 */
class RetrofitBuilder(r: (OkHttpClient.Builder) -> Retrofit.Builder?) {
    internal val retrofit = r(OkHttpClient.Builder().addInterceptor(MethodInterceptor()))?.addCallAdapterFactory(
        FlowCallAdapterFactory.create())?.addConverterFactory(
        CustomGsonConverterFactory.create())?.build()
}