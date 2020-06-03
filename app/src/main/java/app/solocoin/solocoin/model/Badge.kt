package app.solocoin.solocoin.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Vijay Daita on 21/05/2020
 */
@Parcelize
data class Badge(
    @SerializedName("badge_image_url")
    var imageUrl: String?,
    @SerializedName("name")
    var name: String,
    @SerializedName("level")
    var level: String,
    @SerializedName("one_liner")
    var oneLiner: String,
    @SerializedName("min_points")
    var minPoints: String,
    var has: Boolean = false
) : Parcelable

//    {
//        "level": 1,
//        "name": "Infant",
//        "one_liner": "One who stays home is not common",
//        "badge_image_url": "/uploads/badge/badge_image/1/e16b4064-8cca-49c4-af66-e29f297ac5af.png"
//    }