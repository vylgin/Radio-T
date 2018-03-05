package pro.vylgin.radiot.model.interactor.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.entity.TimeLabel
import pro.vylgin.radiot.extension.positionInMillis
import pro.vylgin.radiot.model.data.player.PlayerService
import pro.vylgin.radiot.model.repository.player.PlayerRepository
import timber.log.Timber
import javax.inject.Inject

class PlayerInteractor @Inject constructor(
        private val context: Context,
        private val playerRepository: PlayerRepository
) {
    lateinit var playerServiceBinder: PlayerService.PlayerServiceBinder
    lateinit var mediaController: MediaControllerCompat
    var statePlaying: Boolean = false

    private val playerServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            playerServiceBinder = service as PlayerService.PlayerServiceBinder
            try {
                mediaController = MediaControllerCompat(context, playerServiceBinder.mediaSessionToken)
                mediaController.registerCallback(object : MediaControllerCompat.Callback() {
                    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                        if (state == null) {
                            return
                        }
                        statePlaying = state.state == PlaybackStateCompat.STATE_PLAYING
                    }
                })
            } catch (e: RemoteException) {
                Timber.e(e)
            }

        }

        override fun onServiceDisconnected(name: ComponentName) {
        }
    }

    fun bindPlayerService() {
        context.bindService(Intent(context, PlayerService::class.java), playerServiceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindPlayerService() {
        context.unbindService(playerServiceConnection)
    }

    fun playEpisode(episode: Entry) {
        playerRepository.currentEpisode = episode
        mediaController.transportControls.stop()
        mediaController.transportControls.play()
    }

    fun playCurrentEpisode() {
        mediaController.transportControls.play()
    }

    fun pauseEpisode() {
        mediaController.transportControls.pause()
    }

    fun stopEpisode() {
        mediaController.transportControls.stop()
    }

    fun seekTo(episode: Entry, timeLabel: TimeLabel) {
        if (playerRepository.currentEpisode != episode) {
            playEpisode(episode)
        }
        Handler().postDelayed({playerServiceBinder.seekTo(timeLabel.positionInMillis())}, 150)
    }

    fun seekTo(positionMs: Long) {
        playerServiceBinder.seekTo(positionMs)
    }

    fun getProgress(): Int {
        return playerServiceBinder.getProgress()
    }

    fun getBuffered(): Int {
        return playerServiceBinder.getBuffered()
    }

    fun getCurrentPosition(): String {
        return playerServiceBinder.getCurrentPosition()
    }

    fun getDuration(): String {
        return playerServiceBinder.getDuration()
    }

    fun getDurationSec(): Int {
        return playerServiceBinder.getDurationSec()
    }

    fun getCurrentEpisode(): Entry? {
        return playerRepository.currentEpisode
    }

    fun getCurrentTimeLabel(): TimeLabel? {
        return playerServiceBinder.getCurrentTimeLabel()
    }

    fun getCurrentTimeLabelPosition(): Int {
        return playerServiceBinder.getCurrentTimeLabelPosition()
    }

    fun playNextTimeLabel() {
        playerServiceBinder.playNextTimeLabel()
    }

    fun playPrevTimeLabel() {
        playerServiceBinder.playPrevTimeLabel()
    }


}