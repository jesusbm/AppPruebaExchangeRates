package com.pruebaexchangerates.data.provider

import com.pruebaexchangerates.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit

class RetrofitInstance {

    private val ACCESS_KEY_PARAM_NAME = "access_key"
    
    private val _mInstance by lazy { buildInstance() }

    val instance: Retrofit
        get() = _mInstance

    fun buildInstance(): Retrofit {
        val okHttpClient = OkHttpClient.Builder().run {
            addInterceptor(buildAddAccessKeyInterceptor())
            build()
        }
        return Retrofit.Builder().run {
            baseUrl(BuildConfig.API_ENDPOINT)
            client(okHttpClient)
            build()
        }
    }

    private fun buildAddAccessKeyInterceptor(): Interceptor {
        return object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val originalRequest = chain.request()
                val originalHttpUrl = originalRequest.url

                val modifiedHttpUrl = originalHttpUrl.newBuilder().run {
                    addQueryParameter(ACCESS_KEY_PARAM_NAME, BuildConfig.API_KEY)
                    build()
                }
                val modifiedRequest = originalRequest.newBuilder().run {
                    url(modifiedHttpUrl)
                    build()
                }
                return chain.proceed(modifiedRequest)
            }
        }
    }
}
