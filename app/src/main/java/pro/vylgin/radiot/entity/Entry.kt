package pro.vylgin.radiot.entity

import com.google.gson.annotations.SerializedName
import java.util.*

data class Entry(
        @SerializedName("title") val title: String,
        @SerializedName("url") val url: String,
        @SerializedName("date") val date: Date,
        @SerializedName("categories") val categories: List<Category>,
        @SerializedName("image") val image: String?,
        @SerializedName("file_name") val fileName: String?,
        @SerializedName("show_notes") val showNotes: String?,
        @SerializedName("audio_url") val audioUrl: String?,
        @SerializedName("time_labels") val timeLabels: List<TimeLabel>?
)