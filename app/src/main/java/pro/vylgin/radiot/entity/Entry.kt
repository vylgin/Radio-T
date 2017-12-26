package pro.vylgin.radiot.entity

import android.os.Parcel
import android.os.Parcelable
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            Date(parcel.readLong()),
            parcel.createStringArrayList().map {
                when(it) {
                    Category.PODCAST.value -> Category.PODCAST
                    Category.PREP.value -> Category.PREP
                    Category.NEWS.value -> Category.NEWS
                    Category.INFO.value -> Category.INFO
                    Category.SPECIAL.value -> Category.SPECIAL
                    else -> Category.PODCAST
                }
            },
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createTypedArrayList(TimeLabel)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(url)
        parcel.writeLong(date.time)
        parcel.writeStringList(categories.map { it.value })
        parcel.writeString(image)
        parcel.writeString(fileName)
        parcel.writeString(showNotes)
        parcel.writeString(audioUrl)
        parcel.writeTypedList(timeLabels)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Entry> {
        override fun createFromParcel(parcel: Parcel): Entry {
            return Entry(parcel)
        }

        override fun newArray(size: Int): Array<Entry?> {
            return arrayOfNulls(size)
        }
    }
}