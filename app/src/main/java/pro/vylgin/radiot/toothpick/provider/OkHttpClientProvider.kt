package pro.vylgin.radiot.toothpick.provider

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pro.vylgin.radiot.BuildConfig
import pro.vylgin.radiot.model.data.server.interceptor.ErrorResponseInterceptor
import pro.vylgin.radiot.toothpick.provider.interceptor.CurlLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider


class OkHttpClientProvider @Inject constructor() : Provider<OkHttpClient> {
    private val httpClient: OkHttpClient

    init {
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.addNetworkInterceptor(ErrorResponseInterceptor())
        httpClientBuilder.readTimeout(30, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClientBuilder.addNetworkInterceptor(httpLoggingInterceptor)
            httpClientBuilder.addNetworkInterceptor(CurlLoggingInterceptor())
        }
        httpClient = httpClientBuilder.build()
    }

    override fun get() = httpClient
}