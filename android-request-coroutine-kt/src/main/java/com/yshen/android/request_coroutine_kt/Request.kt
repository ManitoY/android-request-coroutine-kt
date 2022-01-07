package com.yshen.android.request_coroutine_kt

/**
 * Created by Yshen
 * On 2022/1/6
 */
interface Request {
    suspend fun <T, P> get(tClass: Class<T>, path: String?, params: P?, error: (e: Throwable) -> Unit? = {}, response: (T) -> Unit?)

    suspend fun <T, P> post(tClass: Class<T>, path: String?, params: P?, error: (e: Throwable) -> Unit? = {}, response: (T) -> Unit?)
}