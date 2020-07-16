package app.solocoin.solocoin.model

import com.google.gson.annotations.SerializedName
/**
*  Created by Karandeep Singh on 07/07/2020
 */

data class LeaderBoard (
        @SerializedName("top_users")
        var topUsers: ArrayList<User>,
        val user: User
)
