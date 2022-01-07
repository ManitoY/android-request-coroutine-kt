package com.yshen.android.request_coroutine_kt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*
import org.koin.android.ext.android.get
import org.koin.core.qualifier.named
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {

    private val http: Request = get(named("request"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch {
            http.get(Model::class.java, "4f64eb8d-050f-4ba3-983a-f0686aed8d28", Query("1"), error = {
                Log.e("test", it.toString())
                null
            }) {
                Log.e("test", it.Test ?: "error")
                null
            }
        }
    }

    private var scope: CoroutineScope? = null

    private val viewModelScope: CoroutineScope?
        get() {
            val cs: CoroutineScope? = scope
            if (cs != null) {
                return cs
            }
            scope = CloseableCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
            return scope
        }

    internal class CloseableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {
        override val coroutineContext: CoroutineContext = context

        override fun close() {
            coroutineContext.cancel()
        }
    }
}