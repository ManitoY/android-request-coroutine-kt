# android-request-coroutine-kt
### 基于Kotlin协程统一封装的网络请求
#### Retrofit
##### Download

root project build.gradle

```
allprojects {  
   repositories {  
      maven { url "https://raw.githubusercontent.com/ManitoY/android-request-coroutine-kt/main"}  
   }  
}  
```

Retrofit requires at minimum Java 8+ or Android API 21+.

```
implementation 'com.yshen:android-retrofit-coroutine-kt:1.0.3'
```

##### Retrofit Setup

```
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
```

##### Request Apply

```
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
        
```

Class Query is request body and Class Model is response entity.
