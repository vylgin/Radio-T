package pro.vylgin.radiot.model.repository.player

import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.entity.TimeLabel
import pro.vylgin.radiot.model.data.player.PlayerHolder
import pro.vylgin.radiot.model.data.player.SeekModel
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(
        private val playerHolder: PlayerHolder
) {
    var currentEpisode: Entry? = null

    fun saveLastEpisodeNumber(episodeNumber: Int) {
        playerHolder.lastEpisodeNumber = episodeNumber
    }

    fun getLastEpisodeNumber() = playerHolder.lastEpisodeNumber

    fun saveSeekModel(seekModel: SeekModel) {
        Timber.d("saveSeekModel(): seekModel = $seekModel")

        playerHolder.lastEpisodeDuration = seekModel.durationInSeconds
        playerHolder.lastEpisodeDurationTextFormatted = seekModel.durationTextFormatted

        playerHolder.lastEpisodePositionInSeconds = seekModel.currentPositionInSeconds
        playerHolder.lastEpisodePositionTextFormatted = seekModel.currentPositionTextFormatted

        playerHolder.lastEpisodeTimeLabelPosition = seekModel.currentTimeLabelPosition
        playerHolder.lastEpisodeTimeLabelTopic = seekModel.currentTimeLabel?.topic ?: ""
    }

    fun getSeekModel(): SeekModel = SeekModel(
            playerHolder.lastEpisodeDurationTextFormatted,
            playerHolder.lastEpisodeDuration,
            playerHolder.lastEpisodePositionTextFormatted,
            playerHolder.lastEpisodePositionInSeconds,
            TimeLabel(playerHolder.lastEpisodeTimeLabelTopic, Date(0), 0),
            playerHolder.lastEpisodeTimeLabelPosition,
            0
            )

}