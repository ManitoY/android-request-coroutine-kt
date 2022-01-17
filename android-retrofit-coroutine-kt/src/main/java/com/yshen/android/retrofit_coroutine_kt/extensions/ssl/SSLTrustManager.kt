package com.yshen.android.retrofit_coroutine_kt.extensions.ssl

import android.annotation.SuppressLint
import okhttp3.internal.platform.Platform
import java.lang.AssertionError
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * Created by Yshen
 * On 2022/1/10
 */
class SSLTrustManager {
    companion object {
        @JvmStatic
        fun getTrustManager(sslSocketFactory: SSLSocketFactory): X509TrustManager {
            val trustManager = trustManager(sslSocketFactory)
            checkNotNull(trustManager) {
                ("Unable to extract the trust manager on "
                        + Platform.get()
                        + ", sslSocketFactory is "
                        + sslSocketFactory.javaClass)
            }
            return trustManager
        }

        @SuppressLint("PrivateApi")
        private fun trustManager(sslSocketFactory: SSLSocketFactory?): X509TrustManager? {
            try {
                val sslParametersClass = Class.forName("com.android.org.conscrypt.SSLParametersImpl")
                var context: Any? =
                    readFieldOrNull(sslSocketFactory, sslParametersClass, "sslParameters")
                if (context == null) {
                    // If that didn't work, try the Google Play Services SSL provider before giving up. This
                    // must be loaded by the SSLSocketFactory's class loader.
                    context = try {
                        val gmsSslParametersClass = Class.forName(
                            "com.google.android.gms.org.conscrypt.SSLParametersImpl", false,
                            sslSocketFactory?.javaClass?.classLoader
                        )
                        readFieldOrNull(
                            sslSocketFactory,
                            gmsSslParametersClass,
                            "sslParameters"
                        )
                    } catch (e: ClassNotFoundException) {
                        return trustManagerForCommon(sslSocketFactory)
                    }
                }

                val x509TrustManager = readFieldOrNull(
                    context, X509TrustManager::class.java, "x509TrustManager"
                )
                return x509TrustManager
                    ?: readFieldOrNull(
                        context,
                        X509TrustManager::class.java, "trustManager"
                    )
            } catch (ignored: ClassNotFoundException) {
                return null // Not an Android runtime.
            }
        }

        private fun trustManagerForCommon(sslSocketFactory: SSLSocketFactory?): X509TrustManager? {
            // Attempt to get the trust manager from an OpenJDK socket factory. We attempt this on all
            // platforms in order to support Robolectric, which mixes classes from both Android and the
            // Oracle JDK. Note that we don't support HTTP/2 or other nice features on Robolectric.

            // Attempt to get the trust manager from an OpenJDK socket factory. We attempt this on all
            // platforms in order to support Robolectric, which mixes classes from both Android and the
            // Oracle JDK. Note that we don't support HTTP/2 or other nice features on Robolectric.
            return try {
                val sslContextClass = Class.forName("sun.security.ssl.SSLContextImpl")
                val context =
                    readFieldOrNull(sslSocketFactory, sslContextClass, "context")
                        ?: return null
                readFieldOrNull(
                    context,
                    X509TrustManager::class.java, "trustManager"
                )
            } catch (e: ClassNotFoundException) {
                null
            }
        }

        private fun <T> readFieldOrNull(instance: Any?, fieldType: Class<T>, fieldName: String): T? {
            var c: Class<*>? = instance?.javaClass
            while (c != Any::class.java) {
                try {
                    val field = c?.getDeclaredField(fieldName)
                    field?.isAccessible = true
                    val value = field?.get(instance)
                    return if (!fieldType.isInstance(value)) null else fieldType.cast(value)
                } catch (ignored: NoSuchFieldException) {
                } catch (e: IllegalAccessException) {
                    throw AssertionError()
                }
                c = c?.superclass
            }

            // Didn't find the field we wanted. As a last gasp attempt, try to find the value on a delegate.
            if (fieldName != "delegate") {
                val delegate = readFieldOrNull(instance, Any::class.java, "delegate")
                if (delegate != null) return readFieldOrNull(delegate, fieldType, fieldName)
            }
            return null
        }
    }
}