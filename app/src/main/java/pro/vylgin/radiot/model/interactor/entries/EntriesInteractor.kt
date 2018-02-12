package pro.vylgin.radiot.model.interactor.entries

import io.reactivex.Single
import pro.vylgin.radiot.entity.Category
import pro.vylgin.radiot.model.repository.entry.EntryRepository
import javax.inject.Inject

class EntriesInteractor @Inject constructor(
        private val entryRepository: EntryRepository
) {
    fun getEntries() = entryRepository.getEntries()

    fun getEpisode(episodeNumber: Int) = entryRepository.getEntry(episodeNumber)

    fun getLastEpisodeNumber() = entryRepository
            .getEntries(1, listOf(Category.PODCAST))
            .flatMap { Single.just(it[0].title.substringAfter(' ').toInt()) }

    fun search(searchQuery: String) = entryRepository.search(searchQuery)

}