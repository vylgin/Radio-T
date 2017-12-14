package pro.vylgin.radiot.model.repository.entry

import pro.vylgin.radiot.model.data.server.RadioTApi
import pro.vylgin.radiot.model.system.SchedulersProvider
import pro.vylgin.radiot.toothpick.PrimitiveWrapper
import pro.vylgin.radiot.toothpick.qualifier.DefaultPageSize
import javax.inject.Inject

class EntryRepository @Inject constructor(
        private val api: RadioTApi,
        private val schedulers: SchedulersProvider,
        @DefaultPageSize private val defaultPageSizeWrapper: PrimitiveWrapper<Int>
) {
    private val defaultPageSize = defaultPageSizeWrapper.value

    fun getEntries(
            pageSize: Int = defaultPageSize
    ) = api
            .getEntries(pageSize)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
}