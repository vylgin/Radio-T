package pro.vylgin.radiot.presentation.allpodcasts

import javax.inject.Inject

class AllEpisodesPresenterCache @Inject constructor() {
    private lateinit var episodeNumbers: List<Int>

    fun updateEpisodeNumbers(episodeNumbers: List<Int>) {
        this.episodeNumbers = episodeNumbers
    }

    fun getEpisodeNumbers(): List<Int> {
        return if (::episodeNumbers.isInitialized) {
            episodeNumbers
        } else {
            listOf()
        }
    }
}