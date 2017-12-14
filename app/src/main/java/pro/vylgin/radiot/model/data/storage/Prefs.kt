package pro.vylgin.radiot.model.data.storage

import android.content.Context
import pro.vylgin.radiot.model.data.server.ServerHolder
import pro.vylgin.radiot.toothpick.qualifier.DefaultServerPath
import javax.inject.Inject

class Prefs @Inject constructor(
        private val context: Context,
        @DefaultServerPath private val defaultServerPath: String
) : ServerHolder {
    private val AUTH_DATA = "auth_data"
    private val KEY_SERVER_PATH = "ad_server_path"

    private fun getSharedPreferences(prefsName: String) = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    override var serverPath: String
        get() = getSharedPreferences(AUTH_DATA).getString(KEY_SERVER_PATH, defaultServerPath)
        set(value) {
            getSharedPreferences(AUTH_DATA).edit().putString(KEY_SERVER_PATH, value).apply()
        }
}