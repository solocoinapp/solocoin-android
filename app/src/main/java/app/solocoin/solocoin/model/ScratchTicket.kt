package app.solocoin.solocoin.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Vijay Daita
 */
@Parcelize
data class ScratchTicket(
    var rewardRupees: String?,
    var costRupees: String?
) : Parcelable