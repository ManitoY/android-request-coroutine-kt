package com.yshen.android.retrofit_coroutine_kt

import com.google.gson.Gson
import com.yshen.android.request_coroutine_kt.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn

/**
 * Created by Yshen
 * On 2021/12/30
 *
 */
class CoroutineRetrofit(val config: () -> RetrofitBuilder): Request {

    init {
        config().retrofit ?: throw IllegalStateException("retrofit isn't init")
    }

    override suspend fun <T, P> get(
        tClass: Class<T>,
        path: String?,
        params: P?,
        error: (e: Throwable) -> Any?,
        response: (T) -> Any?
    ) {
        if(params == null) config().retrofit?.create(Api::class.java)?.get(path) else {
            config().retrofit?.create(Api::class.java)?.get(path, params)
        }?.flowOn(Dispatchers.IO)?.catch { t: Throwable ->
            error(t)
        }?.collect {
            response(Gson().fromJson(it, tClass))
        }
    }

    override suspend fun <T, P> post(
        tClass: Class<T>,
        path: String?,
        params: P?,
        error: (e: Throwable) -> Any?,
        response: (T) -> Any?
    ) {
        if(params == null) config().retrofit?.create(Api::class.java)?.post(path) else {
            config().retrofit?.create(Api::class.java)?.post(path, params)
        }?.flowOn(Dispatchers.IO)?.catch { t: Throwable ->
            error(t)
        }?.collect {
            response(Gson().fromJson(it, tClass))
        }
    }
}