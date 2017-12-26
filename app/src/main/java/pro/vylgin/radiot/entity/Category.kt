package pro.vylgin.radiot.entity

import com.google.gson.annotations.SerializedName

enum class Category(public val value: String) {

    @SerializedName("podcast") PODCAST("podcast"),
    @SerializedName("prep") PREP("prep"),
    @SerializedName("news") NEWS("news"),
    @SerializedName("info") INFO("info"),
    @SerializedName("special") SPECIAL("special");

    override fun toString() = value

}