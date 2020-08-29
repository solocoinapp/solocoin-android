package app.solocoin.solocoin.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
/**
 * Created by Karandeep Singh on 15/07/2020
 */
@Parcelize
data class Profile(
    @SerializedName("id")
    var id:String,
    @SerializedName("name")
    var name:String,
    @SerializedName("mobile")
    var mobile:String,
    @SerializedName("wallet_balance")
    var wallet_balance:String,
    @SerializedName("home_duration_in_seconds")
    var home_duration_in_seconds:String,
    @SerializedName("redeemed_rewards")
    var redeemed_rewards: ArrayList<RedeemedRewards>,
    @SerializedName("referral")
    var referral: Referral
):Parcelable