package com.yshen.android.request_coroutine_kt

import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Created by Yshen
 * On 2021/12/31
 */
fun request(name: String = "request", request: () -> Request): Module {
    return module {
        single(named(name)) {
            request()
        }
    }
}