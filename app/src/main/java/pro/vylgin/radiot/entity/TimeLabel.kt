package pro.vylgin.radiot.entity

import com.google.gson.annotations.SerializedName
import java.util.*

data class TimeLabel(
        @SerializedName("topic") val topic: String,
        @SerializedName("time") val time: Date,
        @SerializedName("duration") val duration: Int
)