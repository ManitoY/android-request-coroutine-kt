package com.yshen.android.retrofit_coroutine_kt.extensions.converter

import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException

/**
 * Created by Yshen
 * In 2020/1/14
 */
internal class CustomGsonResponseBodyConverter : Converter<ResponseBody, String> {

    @Throws(IOException::class)
    override fun convert(body: ResponseBody): String? {
        val responseStr: String?
        try {
            responseStr = body.string()
            if (responseStr.isNullOrEmpty()) {
                throw Exception("服务器返回解析出错")
            }
        } catch (e: IOException) {
            throw Exception("服务器返回错误")
        } finally {
            body.close()
        }
        return responseStr
    }
}