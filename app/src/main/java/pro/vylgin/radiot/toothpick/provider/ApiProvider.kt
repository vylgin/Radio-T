package pro.vylgin.radiot.toothpick.provider

import com.google.gson.Gson
import okhttp3.OkHttpClient
import pro.vylgin.radiot.model.data.server.RadioTApi
import pro.vylgin.radiot.toothpick.qualifier.ServerPath
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Provider

class ApiProvider @Inject constructor(
        private val okHttpClient: OkHttpClient,
        private val gson: Gson,
        @ServerPath private val serverPath: String
) : Provider<RadioTApi> {

    override fun get() =
            Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .baseUrl(serverPath)
                    .build()
                    .create(RadioTApi::class.java)
}