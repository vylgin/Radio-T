package pro.vylgin.radiot.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*

data class TimeLabel(
        @SerializedName("topic") val topic: String,
        @SerializedName("time") val time: Date,
        @SerializedName("duration") val duration: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            Date(parcel.readLong()),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(topic)
        parcel.writeLong(time.time)
        parcel.writeInt(duration)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TimeLabel> {
        override fun createFromParcel(parcel: Parcel): TimeLabel {
            return TimeLabel(parcel)
        }

        override fun newArray(size: Int): Array<TimeLabel?> {
            return arrayOfNulls(size)
        }
    }
}