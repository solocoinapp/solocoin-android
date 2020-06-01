package app.solocoin.solocoin.model

/**
 * Created by Saurav Gupta on 29/05/2020
 */
data class ErrorResponse(
    var error: String? = null,
    var errors: ArrayList<String>? = null
)