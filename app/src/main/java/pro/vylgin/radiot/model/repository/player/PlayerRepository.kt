package pro.vylgin.radiot.model.repository.player

import pro.vylgin.radiot.entity.Entry
import javax.inject.Inject

class PlayerRepository @Inject constructor(

) {
    var currentPodcast: Entry? = null
}