package pro.vylgin.radiot.presentation.global

import pro.vylgin.radiot.extension.userMessage
import pro.vylgin.radiot.model.data.server.ServerError
import pro.vylgin.radiot.model.system.ResourceManager
import timber.log.Timber
import javax.inject.Inject


class ErrorHandler @Inject constructor(
        private val resourceManager: ResourceManager
) {

    fun proceed(error: Throwable, messageListener: (String) -> Unit = {}) {
        Timber.e("Error: $error")
        if (error is ServerError) {
            messageListener(error.userMessage(resourceManager))
        } else {
            messageListener(error.userMessage(resourceManager))
        }
    }

}
