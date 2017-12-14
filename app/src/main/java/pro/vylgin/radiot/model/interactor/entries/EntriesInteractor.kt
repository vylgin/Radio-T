package pro.vylgin.radiot.model.interactor.entries

import pro.vylgin.radiot.model.repository.entry.EntryRepository
import javax.inject.Inject

class EntriesInteractor @Inject constructor(
        private val entryRepository: EntryRepository
) {
    fun getEntries() = entryRepository.getEntries()
}