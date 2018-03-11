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
import io.reactivex.Observer
import io.reactivex.Single
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.entity.TimeLabel
import pro.vylgin.radiot.extension.getEpisodeNumber
import pro.vylgin.radiot.extension.positionInMillis
import pro.vylgin.radiot.model.data.player.PlayerService
import pro.vylgin.radiot.model.data.player.PlayerState
import pro.vylgin.radiot.model.interactor.entries.EntriesInteractor
import pro.vylgin.radiot.model.repository.player.PlayerRepository
import pro.vylgin.radiot.model.system.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class PlayerInteractor @Inject constructor(
        private val context: Context,
        private val playerRepository: PlayerRepository,
        private val entriesInteractor: EntriesInteractor,
        private val schedulers: SchedulersProvider
) {
    lateinit var playerServiceBinder: PlayerService.PlayerServiceBinder
    lateinit var mediaController: MediaControllerCompat
    lateinit var serviceConnectionCallback: () -> Unit

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
                    }
                })
                serviceConnectionCallback.invoke()
            } catch (e: RemoteException) {
                Timber.e(e)
            }

        }

        override fun onServiceDisconnected(name: ComponentName) {
        }
    }

    fun getLastPlayedEpisode(): Single<Entry?> {
        val currentEpisode = playerRepository.currentEpisode
        return if (currentEpisode != null) {
            Single.just(currentEpisode)
        } else {
            val lastEpisodeNumber = playerRepository.getLastEpisodeNumber()
            entriesInteractor.getEpisode(lastEpisodeNumber)
                    .doOnSuccess {
                        playerRepository.currentEpisode = it
                    }
        }
    }

    fun bindPlayerService(callback: () -> Unit) {
        serviceConnectionCallback = callback
        context.bindService(Intent(context, PlayerService::class.java), playerServiceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindPlayerService() {
        context.unbindService(playerServiceConnection)
    }

    fun getCurrentEpisode(): Entry? {
        return playerRepository.currentEpisode
    }

    fun playEpisode(episode: Entry) {
        playerRepository.currentEpisode = episode
        playerRepository.saveLastEpisodeNumber(episode.getEpisodeNumber())

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
        Handler().postDelayed({ playerServiceBinder.seekTo(timeLabel.positionInMillis()) }, 150)
    }

    fun seekTo(positionMs: Long) = playerServiceBinder.seekTo(positionMs)

    fun playNextTimeLabel() = playerServiceBinder.playNextTimeLabel()


    fun playPrevTimeLabel() = playerServiceBinder.playPrevTimeLabel()

    fun getPlayerObserver() = playerServiceBinder.getPlayerObservable()
            .observeOn(schedulers.ui())

    fun addPlayerStateObserver(stateObserver: Observer<PlayerState>) = playerServiceBinder.addPlayerStateObserver(stateObserver)

}