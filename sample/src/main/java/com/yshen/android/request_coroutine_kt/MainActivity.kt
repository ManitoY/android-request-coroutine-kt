package com.yshen.android.request_coroutine_kt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*
import org.koin.android.ext.android.get
import org.koin.core.qualifier.named

class MainActivity : AppCompatActivity() {

    private val http: Request = get(named("request"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch {
            http.get(Model::class.java, "4f64eb8d-050f-4ba3-983a-f0686aed8d28", Query("1"), error = {
                Log.e("test", it.toString())
            }) {
                Log.e("test", it.Test ?: "error")
            }
        }
    }
}