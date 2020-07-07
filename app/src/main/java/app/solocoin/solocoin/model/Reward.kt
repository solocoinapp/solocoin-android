package app.solocoin.solocoin.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Saurav Gupta on 14/5/2020
 */
@Parcelize
data class Reward(
    @SerializedName("id")
    var rewardId: String,
    @SerializedName("offer_name")
    var rewardName: String,
    @SerializedName("company_name")
    var companyName: String,
    @SerializedName("terms_and_conditions")
    var rewardTermsAndConditions: String?,
    @SerializedName("coins")
    var costCoins: String,
    @SerializedName("offer_amount")
    var costRupees: String,
    @SerializedName("coupon_code")
    var couponCode: String,
    var companyLogoUrl: String? = null,
    var rewardImageUrl: String? = null,
    var isClaimed: Boolean = false,
    @SerializedName("category")
    var category:Category

) : Parcelable

//"id": 2,
//"offer_name": "asdasdas",
//"company_name": "adasdas",
//"terms_and_conditions": "asdasdsad",
//"coins": 50,
//"rupees": 100,
//"coupon_code": "asdasd"