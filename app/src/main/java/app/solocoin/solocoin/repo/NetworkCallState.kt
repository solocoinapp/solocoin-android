package app.solocoin.solocoin.repo

sealed class NetworkCallState<T> {
    class Loading<T> : NetworkCallState<T>()
    data class Success<T>(val data: T) : NetworkCallState<T>()
    data class Error<T>(val message: String) : NetworkCallState<T>()

    companion object {
        fun <T> loading() = Loading<T>()
        fun <T> success(data: T) = Success(data)
        fun <T> error(message: String) = Error<T>(message)
    }
}