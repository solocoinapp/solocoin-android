package app.solocoin.solocoin.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Saurav Gupta on 14/5/2020
 */
@Parcelize
data class Reward(
    var offerName: String?,
    var costRupees: String?,
    var costCoins: String?,
    var offerTermsAndConditions: String?,
//    var offerDetails: ArrayList<String?>,
    var offerDetails: String?,
    var companyName: String?,
    var couponCode: String?,
    var companyLogoUrl: String?
) : Parcelable