package app.solocoin.solocoin.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
/**
 * Created by Karandeep Singh on 04/07/2020
 */
@Parcelize
data class Category (
 @SerializedName("name")
 var name:String
):Parcelable
