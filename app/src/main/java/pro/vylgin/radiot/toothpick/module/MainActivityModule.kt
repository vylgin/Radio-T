package pro.vylgin.radiot.toothpick.module

import pro.vylgin.radiot.presentation.global.GlobalMenuController
import toothpick.config.Module


class MainActivityModule : Module() {
    init {
        bind(GlobalMenuController::class.java).toInstance(GlobalMenuController())
    }
}