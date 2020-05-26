package app.solocoin.solocoin.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Saurav Gupta on 14/5/2020
 */
@Parcelize
data class Reward(
    var rewardName: String?,
    var companyName: String?,
    var rewardTermsAndConditions: String?,
    var costCoins: String?,
    var costRupees: String?,
    var couponCode: String?,
    var rewardDetails: String?,
    var companyLogoUrl: String?,
    var rewardImageUrl: String?
) : Parcelable