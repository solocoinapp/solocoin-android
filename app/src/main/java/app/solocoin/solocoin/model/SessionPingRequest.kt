package app.solocoin.solocoin.model

/**
 * Created by Saurav Gupta on 07/05/20
 */
class SessionPingRequest(private val type: String) {
    override fun toString(): String {
        return "{ \"session\": { \"type\": \"${type}\" } }"
    }
}