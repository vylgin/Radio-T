package pro.vylgin.radiot.model.data.storage

import android.content.Context
import pro.vylgin.radiot.model.data.player.PlayerHolder
import pro.vylgin.radiot.model.data.server.ServerHolder
import pro.vylgin.radiot.toothpick.qualifier.DefaultServerPath
import javax.inject.Inject

class Prefs @Inject constructor(
        private val context: Context,
        @DefaultServerPath private val defaultServerPath: String
) : ServerHolder, PlayerHolder {

    private val AUTH_DATA = "auth_data"
    private val KEY_SERVER_PATH = "ad_server_path"

    private val PLAYER_DATA = "player_data"
    private val KEY_LAST_EPISODE_NUMBER = "last_episode_number"
    private val KEY_LAST_EPISODE_POSITION_IN_SECONDS = "last_episode_position_in_seconds"
    private val KEY_LAST_EPISODE_POSITION_TEXT_FORMATTED = "last_episode_position_text_formatted"
    private val KEY_LAST_EPISODE_DURATION = "last_episode_seek_duration"
    private val KEY_LAST_EPISODE_DURATION_TEXT_FORMATTED = "last_episode_seek_duration_text_formatted"
    private val KEY_LAST_EPISODE_TIME_LABEL_POSITION = "last_episode_time_label_position"
    private val KEY_LAST_EPISODE_TIME_LABEL_TOPIC = "last_episode_time_label_topic"

    private fun getSharedPreferences(prefsName: String) = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    override var serverPath: String
        get() = getSharedPreferences(AUTH_DATA).getString(KEY_SERVER_PATH, defaultServerPath)
        set(value) {
            getSharedPreferences(AUTH_DATA).edit().putString(KEY_SERVER_PATH, value).apply()
        }

    override var lastEpisodeNumber: Int
        get() = getSharedPreferences(PLAYER_DATA).getInt(KEY_LAST_EPISODE_NUMBER, -1)
        set(value) {
            getSharedPreferences(PLAYER_DATA).edit().putInt(KEY_LAST_EPISODE_NUMBER, value).apply()
        }

    override var lastEpisodePositionInSeconds: Int
        get() = getSharedPreferences(PLAYER_DATA).getInt(KEY_LAST_EPISODE_POSITION_IN_SECONDS, -1)
        set(value) {
            getSharedPreferences(PLAYER_DATA).edit().putInt(KEY_LAST_EPISODE_POSITION_IN_SECONDS, value).apply()
        }

    override var lastEpisodePositionTextFormatted: String
        get() = getSharedPreferences(PLAYER_DATA).getString(KEY_LAST_EPISODE_POSITION_TEXT_FORMATTED, "")
        set(value) {
            getSharedPreferences(PLAYER_DATA).edit().putString(KEY_LAST_EPISODE_POSITION_TEXT_FORMATTED, value).apply()
        }

    override var lastEpisodeDuration: Int
        get() = getSharedPreferences(PLAYER_DATA).getInt(KEY_LAST_EPISODE_DURATION, -1)
        set(value) {
            getSharedPreferences(PLAYER_DATA).edit().putInt(KEY_LAST_EPISODE_DURATION, value).apply()
        }

    override var lastEpisodeDurationTextFormatted: String
        get() = getSharedPreferences(PLAYER_DATA).getString(KEY_LAST_EPISODE_DURATION_TEXT_FORMATTED, "")
        set(value) {
            getSharedPreferences(PLAYER_DATA).edit().putString(KEY_LAST_EPISODE_DURATION_TEXT_FORMATTED, value).apply()
        }

    override var lastEpisodeTimeLabelPosition: Int
        get() = getSharedPreferences(PLAYER_DATA).getInt(KEY_LAST_EPISODE_TIME_LABEL_POSITION, -1)
        set(value) {
            getSharedPreferences(PLAYER_DATA).edit().putInt(KEY_LAST_EPISODE_TIME_LABEL_POSITION, value).apply()
        }

    override var lastEpisodeTimeLabelTopic: String
        get() = getSharedPreferences(PLAYER_DATA).getString(KEY_LAST_EPISODE_TIME_LABEL_TOPIC, "")
        set(value) {
            getSharedPreferences(PLAYER_DATA).edit().putString(KEY_LAST_EPISODE_TIME_LABEL_TOPIC, value).apply()
        }
}