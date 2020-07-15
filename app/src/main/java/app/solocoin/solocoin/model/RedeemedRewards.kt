package app.solocoin.solocoin.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RedeemedRewards (
        @SerializedName("rewards_sponsor_id")
        var rewards_sponsor_id:Int,
        @SerializedName("coupon_code")
        var coupon_code:String,
        @SerializedName("offer_name")
        var offer_name:String
):Parcelable