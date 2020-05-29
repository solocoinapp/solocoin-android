package app.solocoin.solocoin.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Saurav Gupta on 21/05/2020
 */
data class Milestones(
    @SerializedName("total_earned_coins")
    var earnedPoints: String,
    @SerializedName("badges")
    var badgeLevel: ArrayList<Badge>
)

//{
//    "total_earned_coins": 500,
//    "badges": [
//    {
//        "level": 1,
//        "name": "Infant",
//        "one_liner": "One who stays home is not common",
//        "badge_image_url": "/uploads/badge/badge_image/1/e16b4064-8cca-49c4-af66-e29f297ac5af.png"
//    }
//    ]
//}
