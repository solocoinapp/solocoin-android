package app.solocoin.solocoin.model

data class ErrorResponse(
    var error: String? = null,
    var errors: ArrayList<String>? = null
)