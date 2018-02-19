package pro.vylgin.radiot

import io.reactivex.schedulers.Schedulers
import pro.vylgin.radiot.model.system.SchedulersProvider


class TestSchedulers : SchedulersProvider {
    override fun ui() = Schedulers.trampoline()
    override fun computation() = Schedulers.trampoline()
    override fun trampoline() = Schedulers.trampoline()
    override fun newThread() = Schedulers.trampoline()
    override fun io() = Schedulers.trampoline()
}