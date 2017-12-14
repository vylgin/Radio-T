package pro.vylgin.radiot.toothpick.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import pro.vylgin.radiot.model.data.server.RadioTApi
import pro.vylgin.radiot.model.interactor.entries.EntriesInteractor
import pro.vylgin.radiot.model.repository.entry.EntryRepository
import pro.vylgin.radiot.toothpick.provider.ApiProvider
import pro.vylgin.radiot.toothpick.provider.OkHttpClientProvider
import pro.vylgin.radiot.toothpick.qualifier.ServerPath
import toothpick.config.Module


class ServerModule(serverUrl: String) : Module() {
    init {
        //Network
        bind(String::class.java).withName(ServerPath::class.java).toInstance(serverUrl)
        bind(Gson::class.java).toInstance(GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create())
        bind(OkHttpClient::class.java).toProvider(OkHttpClientProvider::class.java).singletonInScope()
        bind(RadioTApi::class.java).toProvider(ApiProvider::class.java).singletonInScope()

        //Entry
        bind(EntryRepository::class.java)
        bind(EntriesInteractor::class.java)
    }
}