package pro.vylgin.radiot

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import pro.vylgin.radiot.model.data.server.ServerHolder
import pro.vylgin.radiot.toothpick.DI
import pro.vylgin.radiot.toothpick.module.AppModule
import pro.vylgin.radiot.toothpick.module.ServerModule
import timber.log.Timber
import toothpick.Toothpick
import toothpick.configuration.Configuration
import toothpick.registries.FactoryRegistryLocator
import toothpick.registries.MemberInjectorRegistryLocator
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initLogger()
        initToothpick()
        initAppScope()
        initCalligraphy()
        initCrashlytics()
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initToothpick() {
        if (BuildConfig.DEBUG) {
            Toothpick.setConfiguration(Configuration.forDevelopment().preventMultipleRootScopes())
        } else {
            Toothpick.setConfiguration(Configuration.forProduction().disableReflection())
            FactoryRegistryLocator.setRootRegistry(pro.vylgin.radiot.FactoryRegistry())
            MemberInjectorRegistryLocator.setRootRegistry(pro.vylgin.radiot.MemberInjectorRegistry())
        }
    }

    private fun initAppScope() {
        val appScope = Toothpick.openScope(DI.APP_SCOPE)
        appScope.installModules(AppModule(this))

        //By default we need init ServerScope for launch app
        val serverHolder = appScope.getInstance(ServerHolder::class.java)
        val serverScope = Toothpick.openScopes(DI.APP_SCOPE, DI.SERVER_SCOPE)
        serverScope.installModules(ServerModule(serverHolder.serverPath))
    }

    private fun initCalligraphy() {
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.font_main_regular))
                .setFontAttrId(R.attr.fontPath)
                .build())
    }

    private fun initCrashlytics() {
        val crashlyticsKit = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build()
        Fabric.with(this, crashlyticsKit)
    }
}