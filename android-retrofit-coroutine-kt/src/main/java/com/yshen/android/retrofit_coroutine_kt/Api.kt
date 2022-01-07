package com.yshen.android.retrofit_coroutine_kt

import kotlinx.coroutines.flow.Flow
import retrofit2.http.*

/**
 * Created by Yshen
 * On 2022/1/4
 */
interface Api {

    @Headers(Settings.BODY_FRO_GET_HEADER + ": GET")
    @POST("{path}")
    fun get(@Path("path") path: String?, @Body params: Any?): Flow<String>

    @GET("{path}")
    fun get(@Path("path") path: String?): Flow<String>

    @POST("{path}")
    fun post(@Path("path") path: String?, @Body params: Any?): Flow<String>

    @POST("{path}")
    fun post(@Path("path") path: String?): Flow<String>
}