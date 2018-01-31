package pro.vylgin.radiot.toothpick.module

import android.content.Context
import pro.vylgin.radiot.BuildConfig
import pro.vylgin.radiot.model.data.server.ServerHolder
import pro.vylgin.radiot.model.data.storage.Prefs
import pro.vylgin.radiot.model.interactor.player.PlayerInteractor
import pro.vylgin.radiot.model.repository.player.PlayerRepository
import pro.vylgin.radiot.model.system.AppSchedulers
import pro.vylgin.radiot.model.system.ResourceManager
import pro.vylgin.radiot.model.system.SchedulersProvider
import pro.vylgin.radiot.toothpick.PrimitiveWrapper
import pro.vylgin.radiot.toothpick.qualifier.DefaultPageSize
import pro.vylgin.radiot.toothpick.qualifier.DefaultServerPath
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import toothpick.config.Module


class AppModule(context: Context) : Module() {
    init {
        //Global
        bind(Context::class.java).toInstance(context)
        bind(String::class.java).withName(DefaultServerPath::class.java).toInstance(BuildConfig.ORIGIN_RADIOT_ENDPOINT)
        bind(PrimitiveWrapper::class.java).withName(DefaultPageSize::class.java).toInstance(PrimitiveWrapper(30))
        bind(SchedulersProvider::class.java).toInstance(AppSchedulers())
        bind(ResourceManager::class.java).singletonInScope()

        //Navigation
        val cicerone = Cicerone.create()
        bind(Router::class.java).toInstance(cicerone.router)
        bind(NavigatorHolder::class.java).toInstance(cicerone.navigatorHolder)

        //Server
        bind(ServerHolder::class.java).to(Prefs::class.java).singletonInScope()

        //Player
        bind(PlayerRepository::class.java).singletonInScope()
        bind(PlayerInteractor::class.java).singletonInScope()
    }
}