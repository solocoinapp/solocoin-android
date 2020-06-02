package app.solocoin.solocoin.worker


import android.content.Context
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import com.scottyab.rootbeer.RootBeer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/*
 * Created by Vijay Daita on 5/20
 */

class LegalChecker(var context: Context) {
    @InternalCoroutinesApi
    @ExperimentalCoroutinesApi
    fun isCheating(): Boolean {
        val rootBeer = RootBeer(context)
        var mock = false
        sharedPrefs?.let {
            mock = it.mock
        }
        return rootBeer.isRooted && mock
    }
}
