package app.solocoin.solocoin.model

class SessionPingRequest(val type: String) {
    override fun toString(): String {
        return "{ \"session\": { \"type\": \"${type}\" } }"
    }
}