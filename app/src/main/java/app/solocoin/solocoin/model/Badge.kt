package app.solocoin.solocoin.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Vijay Daita on 21/05/2020
 */
@Parcelize
data class Badge(
    var imageUrl: String?,
    var name: String?,
    var level: String?,
    var has: Boolean?
) :
    Parcelable