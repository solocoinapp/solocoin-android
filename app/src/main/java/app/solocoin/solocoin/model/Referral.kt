package app.solocoin.solocoin.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
/**
 *  Created by Karandeep Singh on 04/07/2020
 */
@Parcelize
data class Referral (
    @SerializedName("code")
    var refercode:String,
        @SerializedName("amount")
    var amount:String
): Parcelable