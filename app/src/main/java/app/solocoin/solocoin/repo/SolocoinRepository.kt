package app.solocoin.solocoin.repo

import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import com.google.gson.JsonObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * Created by Aditya Sonel on 22/04/20.
 */

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class SolocoinRepository(private val apiService: ApiService) {
    suspend fun mobileLogin(body: JsonObject) = apiService.mobileLogin(body)
    suspend fun mobileSignUp(body: JsonObject) = apiService.mobileSignUp(body)
    suspend fun userData() = apiService.userData(sharedPrefs?.authToken!!)
    suspend fun userUpdate(body: JsonObject) = apiService.userUpdate(sharedPrefs?.authToken!!, body)
    suspend fun pingSession(body: JsonObject) =
        apiService.pingSession(sharedPrefs?.authToken!!, body)
    suspend fun getOffers() = apiService.getOffers(sharedPrefs?.authToken!!)
    suspend fun getprofile() = apiService.getProfile(sharedPrefs?.authToken!!)
    suspend fun getScratchCardOffers() = apiService.getScratchCardOffers(sharedPrefs?.authToken!!)
    suspend fun redeemRewards(body: JsonObject) =
        apiService.redeemRewards(sharedPrefs?.authToken!!, body)
    suspend fun getDailyQuiz() = apiService.getDailyQuiz(sharedPrefs?.authToken!!)
    suspend fun getWeeklyQuiz() = apiService.getWeeklyQuiz(sharedPrefs?.authToken!!)
    suspend fun submitQuizAnswer(body: JsonObject) =
        apiService.submitQuizAnswer(sharedPrefs?.authToken!!, body)
    suspend fun getBadgesLevels() = apiService.getBadgesLevels(sharedPrefs?.authToken!!)
    suspend fun getleaderboard() =apiService.getleaderboard(sharedPrefs?.authToken!!)
}