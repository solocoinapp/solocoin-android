package app.solocoin.solocoin.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 *  Created by Karandeep Singh on 04/07/2020
 */
@Parcelize
data class User (
    @SerializedName("id")
    var id:String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("country_code")
    var countryCode:String?,
    @SerializedName("wallet_balance")
    var wallet_balance:String?,
    @SerializedName("wb_rank")
    var rank:String?
): Parcelable

//{
//    "id": 11,
//    "name": "Mishaal Testing",
//    "country_code": null,
//    "wallet_balance": "14682.0",
//    "wb_rank": 2
//}