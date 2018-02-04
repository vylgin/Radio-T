package pro.vylgin.radiot.model.data.player

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.app.NotificationCompat.MediaStyle
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import okhttp3.OkHttpClient
import pro.vylgin.radiot.R
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.entity.TimeLabel
import pro.vylgin.radiot.extension.positionInMillis
import pro.vylgin.radiot.model.repository.player.PlayerRepository
import pro.vylgin.radiot.toothpick.DI
import pro.vylgin.radiot.ui.launch.LaunchActivity
import timber.log.Timber
import toothpick.Toothpick
import java.io.File
import javax.inject.Inject

class PlayerService : Service() {

    private val NOTIFICATION_ID = 404
    private val NOTIFICATION_DEFAULT_CHANNEL_ID = "radiot_channel"

    private val seekDelay: Long = 1000

    private val metadataBuilder = MediaMetadataCompat.Builder()

    private val stateBuilder = PlaybackStateCompat.Builder().setActions(
            PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
    )

    private lateinit var mediaSession: MediaSessionCompat

    private lateinit var audioManager: AudioManager
    private lateinit var audioFocusRequest: AudioFocusRequest
    private var audioFocusRequested = false

    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var extractorsFactory: ExtractorsFactory
    private lateinit var dataSourceFactory: DataSource.Factory

    @Inject
    lateinit var playerRepository: PlayerRepository

    private val seekHandler = Handler()
    private val seekRunnable = object : Runnable {
        override fun run() {
            updateNotificationTimeLabel()
            NotificationManagerCompat.from(this@PlayerService).notify(NOTIFICATION_ID, getNotification(exoPlayer.playbackState))
            seekHandler.postDelayed(this, seekDelay)
        }
    }

    private val mediaSessionCallback: MediaSessionCompat.Callback = object : MediaSessionCompat.Callback() {
        private var currentUri: Uri? = null
        internal var currentState = PlaybackStateCompat.STATE_STOPPED

        override fun onPlay() {
            val podcast: Entry = playerRepository.currentPodcast ?: return

            updateMetadataFromTrack(podcast) {
                if (!exoPlayer.playWhenReady) {
                    startService(Intent(applicationContext, PlayerService::class.java))

                    prepareToPlay(podcast.audioUrl ?: "")

                    if (!audioFocusRequested) {
                        audioFocusRequested = true

                        val audioFocusResult: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            audioManager.requestAudioFocus(audioFocusRequest)
                        } else {
                            audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                        }
                        if (audioFocusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                            return@updateMetadataFromTrack
                    }

                    mediaSession.isActive = true

                    registerReceiver(becomingNoisyReceiver, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))

                    exoPlayer.playWhenReady = true
                }

                mediaSession.setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f).build())
                currentState = PlaybackStateCompat.STATE_PLAYING

                refreshNotificationAndForegroundStatus(currentState)

                seekHandler.removeCallbacks(seekRunnable)
                seekHandler.post(seekRunnable)
            }
        }

        override fun onPause() {
            if (exoPlayer.playWhenReady) {
                exoPlayer.playWhenReady = false
                unregisterReceiver(becomingNoisyReceiver)
            }

            mediaSession.setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f).build())
            currentState = PlaybackStateCompat.STATE_PAUSED

            refreshNotificationAndForegroundStatus(currentState)

            seekHandler.removeCallbacks(seekRunnable)
        }

        override fun onStop() {
            if (exoPlayer.playWhenReady) {
                exoPlayer.playWhenReady = false
                unregisterReceiver(becomingNoisyReceiver)
            }

            if (audioFocusRequested) {
                audioFocusRequested = false

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    audioManager.abandonAudioFocusRequest(audioFocusRequest)
                } else {
                    audioManager.abandonAudioFocus(audioFocusChangeListener)
                }
            }

            mediaSession.isActive = false

            mediaSession.setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f).build())
            currentState = PlaybackStateCompat.STATE_STOPPED

            refreshNotificationAndForegroundStatus(currentState)

            stopSelf()

            seekHandler.removeCallbacks(seekRunnable)
        }

        override fun onSkipToNext() {
            playNextTimeLabel()
        }

        override fun onSkipToPrevious() {
            playPrevTimeLabel()
        }

        private fun prepareToPlay(srcUrl: String) {
            val uri = Uri.parse(srcUrl)

            if (uri != currentUri) {
                currentUri = uri
                val mediaSource = ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null)
                exoPlayer.prepare(mediaSource)
            }
        }

        private fun updateMetadataFromTrack(podcast: Entry, callback: () -> Unit) {
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, podcast.title)
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, resources.getString(R.string.app_name))

            if (podcast.image == null) {
                metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART,
                        BitmapFactory.decodeResource(resources, R.drawable.exo_controls_play))
                mediaSession.setMetadata(metadataBuilder.build())
                callback.invoke()
            } else {
                Glide.with(this@PlayerService)
                        .asBitmap()
                        .load(podcast.image)
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>) {
                                metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, resource)
                                mediaSession.setMetadata(metadataBuilder.build())
                                callback.invoke()
                            }
                        })
            }
        }
    }

    private fun updateNotificationTimeLabel() {
        val podcast = playerRepository.currentPodcast ?: return

        if (podcast.timeLabels != null) {
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, getCurrentTimeLabel()?.topic)
            mediaSession.setMetadata(metadataBuilder.build())
        }
    }

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> mediaSessionCallback.onPlay()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> mediaSessionCallback.onPause()
            else -> mediaSessionCallback.onPause()
        }
    }

    private val becomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Disconnecting headphones - stop playback
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action) {
                mediaSessionCallback.onPause()
            }
        }
    }

    private val exoPlayerListener = object : ExoPlayer.EventListener {
        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
            Timber.d("timeline = $timeline")
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {}

        override fun onLoadingChanged(isLoading: Boolean) {}

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playWhenReady && playbackState == ExoPlayer.STATE_ENDED) {
                mediaSessionCallback.onSkipToNext()
            }
        }

        override fun onPlayerError(error: ExoPlaybackException) {}

        override fun onPositionDiscontinuity() {}

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
    }

    override fun onCreate() {
        super.onCreate()
        Toothpick.inject(this, Toothpick.openScope(DI.APP_SCOPE))
        initPlayer()
    }

    private fun initPlayer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(NOTIFICATION_DEFAULT_CHANNEL_ID, getString(R.string.notification_channel_name), NotificationManagerCompat.IMPORTANCE_DEFAULT)
            notificationChannel.enableVibration(false)
            notificationChannel.enableLights(false)
            notificationChannel.setSound(null, null)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

            val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(audioFocusChangeListener)
                    .setAcceptsDelayedFocusGain(false)
                    .setWillPauseWhenDucked(true)
                    .setAudioAttributes(audioAttributes)
                    .build()
        }

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        mediaSession = MediaSessionCompat(applicationContext, "PlayerService")
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession.setCallback(mediaSessionCallback)

        val activityIntent = Intent(applicationContext, LaunchActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, activityIntent, 0)
        mediaSession.setSessionActivity(pendingIntent)

        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON, null, applicationContext, MediaButtonReceiver::class.java)
        mediaSession.setMediaButtonReceiver(PendingIntent.getBroadcast(applicationContext, 0, mediaButtonIntent, 0))

        exoPlayer = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(this), DefaultTrackSelector(), DefaultLoadControl())
        exoPlayer.addListener(exoPlayerListener)
        val httpDataSourceFactory = OkHttpDataSourceFactory(OkHttpClient(), Util.getUserAgent(this, getString(R.string.app_name)), null)
        val cache = SimpleCache(File(this.cacheDir.absolutePath + "/exoplayer"), LeastRecentlyUsedCacheEvictor((1024 * 1024 * 100).toLong())) // 100 Mb max
        this.dataSourceFactory = CacheDataSourceFactory(cache, httpDataSourceFactory, CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        this.extractorsFactory = DefaultExtractorsFactory()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        seekHandler.removeCallbacks(seekRunnable)
        mediaSession.release()
        exoPlayer.release()
    }

    override fun onBind(intent: Intent): IBinder? {
        return PlayerServiceBinder()
    }

    inner class PlayerServiceBinder : Binder() {
        val mediaSessionToken: MediaSessionCompat.Token
            get() = mediaSession.sessionToken

        fun seekTo(positionMs: Long) {
            exoPlayer.seekTo(positionMs)
        }

        fun getCurrentPosition(): String {
            val millis = exoPlayer.currentPosition

            val seconds = (millis / 1000).toInt() % 60
            val minutes = (millis / (1000 * 60) % 60).toInt()
            val hours = (millis / (1000 * 60 * 60) % 24).toInt()

            return String.format("%d:%02d:%02d", hours, minutes, seconds)
        }

        fun getBufferedPercentage(): Int {
            return exoPlayer.bufferedPercentage
        }

        fun getProgress(): Int {
            return (exoPlayer.currentPosition / 1000).toInt()
        }

        fun getBuffered(): Int {
            return (exoPlayer.bufferedPercentage / 100.0 * exoPlayer.duration / 1000.0).toInt()
        }

        fun getDuration(): String {
            val millis = exoPlayer.duration

            val seconds = (millis / 1000).toInt() % 60
            val minutes = (millis / (1000 * 60) % 60).toInt()
            val hours = (millis / (1000 * 60 * 60) % 24).toInt()

            return String.format("%d:%02d:%02d", hours, minutes, seconds)
        }

        fun getDurationSec(): Int {
            return (exoPlayer.duration / 1000).toInt()
        }

        fun getCurrentPositionSec(): Int {
            return this@PlayerService.getCurrentPositionSec()
        }

        fun getCurrentTimeLabel(): TimeLabel? {
            return this@PlayerService.getCurrentTimeLabel()
        }

        fun getCurrentTimeLabelPosition(): Int {
            return this@PlayerService.getCurrentTimeLabelPosition()
        }

        fun playNextTimeLabel() {
            this@PlayerService.playNextTimeLabel()
        }

        fun playPrevTimeLabel() {
            this@PlayerService.playPrevTimeLabel()
        }
    }

    private fun playNextTimeLabel() {
        val nextTimeLabel = getNextTimeLabel() ?: return
        exoPlayer.seekTo(nextTimeLabel.positionInMillis())
    }

    private fun playPrevTimeLabel() {
        val prevTimeLabel = getPrevTimeLabel() ?: return
        exoPlayer.seekTo(prevTimeLabel.positionInMillis())
    }

    private fun getCurrentPositionSec(): Int {
        return (exoPlayer.currentPosition / 1000).toInt()
    }

    private fun getCurrentTimeLabel(): TimeLabel? {
        val timeLabels = playerRepository.currentPodcast?.timeLabels ?: return null
        val currentPosition = getCurrentPositionSec()

        for (timeLabel in timeLabels) {
            val currentPositionSec = timeLabel.positionInMillis() / 1000
            if (currentPosition >= currentPositionSec && currentPosition < currentPositionSec + timeLabel.duration) {
                return timeLabel
            }
        }

        return null
    }

    private fun getNextTimeLabel(): TimeLabel? {
        val timeLabels = playerRepository.currentPodcast?.timeLabels ?: return null
        val currentTimeLabel = getCurrentTimeLabel() ?: return null

        val currentIndex = timeLabels.indexOf(currentTimeLabel)
        return if (currentIndex == timeLabels.size - 1) {
            timeLabels[0]
        } else {
            timeLabels[currentIndex + 1]
        }
    }

    private fun getPrevTimeLabel(): TimeLabel? {
        val timeLabels = playerRepository.currentPodcast?.timeLabels ?: return null
        val currentTimeLabel = getCurrentTimeLabel() ?: return null

        val currentIndex = timeLabels.indexOf(currentTimeLabel)
        return if (currentIndex == 0) {
            timeLabels[timeLabels.size - 1]
        } else {
            timeLabels[currentIndex - 1]
        }
    }

    private fun getCurrentTimeLabelPosition(): Int {
        val currentPodcast = playerRepository.currentPodcast ?: return -1
        val timeLabels = currentPodcast.timeLabels ?: return -1
        val currentTimeLabel = getCurrentTimeLabel()
        return timeLabels.indexOf(currentTimeLabel)
    }

    private fun refreshNotificationAndForegroundStatus(playbackState: Int) {
        when (playbackState) {
            PlaybackStateCompat.STATE_PLAYING -> {
                startForeground(NOTIFICATION_ID, getNotification(playbackState))
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                NotificationManagerCompat.from(this@PlayerService).notify(NOTIFICATION_ID, getNotification(playbackState))
                stopForeground(false)
            }
            else -> {
                stopForeground(true)
            }
        }
    }

    private fun getNotification(playbackState: Int): Notification {
        val builder = MediaStyleHelper.from(applicationContext, mediaSession, NOTIFICATION_DEFAULT_CHANNEL_ID)

        if (playerRepository.currentPodcast?.timeLabels == null) {
            builder.addAction(NotificationCompat.Action(R.color.transparent, getString(R.string.previous), MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))
        } else {
            builder.addAction(NotificationCompat.Action(R.drawable.exo_controls_previous, getString(R.string.previous), MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))
        }

        if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
            builder.addAction(NotificationCompat.Action(R.drawable.exo_controls_pause, getString(R.string.pause), MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_PLAY_PAUSE)))
        } else {
            builder.addAction(NotificationCompat.Action(R.drawable.exo_controls_play, getString(R.string.play), MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_PLAY_PAUSE)))
        }

        if (playerRepository.currentPodcast?.timeLabels == null) {
            builder.addAction(NotificationCompat.Action(R.color.transparent, getString(R.string.next), MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)))
        } else {
            builder.addAction(NotificationCompat.Action(R.drawable.exo_controls_next, getString(R.string.next), MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)))
        }

        builder.setStyle(MediaStyle()
                .setShowActionsInCompactView(1)
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_STOP))
                .setMediaSession(mediaSession.sessionToken)) // setMediaSession требуется для Android Wear
        builder.setSmallIcon(R.drawable.ic_queue_music)
        builder.color = ContextCompat.getColor(applicationContext, R.color.colorPrimaryDark) // The whole background (in MediaStyle), not just icon background
        builder.setShowWhen(false)
        builder.priority = NotificationCompat.PRIORITY_HIGH
        builder.setOnlyAlertOnce(true)
        builder.setChannelId(NOTIFICATION_DEFAULT_CHANNEL_ID)

        return builder.build()
    }

}
