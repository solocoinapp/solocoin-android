package app.solocoin.solocoin.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Profile(
    @SerializedName("id")
    var id:String,
    @SerializedName("name")
    var name:String,
    @SerializedName("redeemed_rewards")
    var redeemed_rewards: ArrayList<RedeemedRewards>
):Parcelable