package pro.vylgin.radiot.presentation.global.presenter

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable


class GlobalMenuController {
    private val stateRelay = BehaviorRelay.createDefault(false)

    val state: Observable<Boolean> = stateRelay
    fun open() = stateRelay.accept(true)
    fun close() = stateRelay.accept(false)
}