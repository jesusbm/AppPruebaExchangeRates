package com.pruebaexchangerates.data.provider

import com.pruebaexchangerates.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor

class RetrofitInstance {

    private val ACCESS_KEY_PARAM_NAME = "access_key"

    private val _mInstance by lazy { buildInstance() }

    val instance: Retrofit
        get() = _mInstance

    private fun buildInstance(): Retrofit {
        val okHttpClient = OkHttpClient.Builder().run {
            addInterceptor(buildAddAccessKeyInterceptor())
            addInterceptor(buildLoggingInterceptor())
            build()
        }
        return Retrofit.Builder().run {
            baseUrl(BuildConfig.API_ENDPOINT)
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            addConverterFactory(GsonConverterFactory.create())
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

    private fun buildLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}
