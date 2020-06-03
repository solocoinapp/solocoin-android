package app.solocoin.solocoin.util


import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by Saurav Gupta 28/05/2020
 */
object EventBus {

    private val publisher = PublishSubject.create<Any>()

    /*
     * To publish a event from anywhere in the code
     */
    fun publish(event: Any) {
        publisher.onNext(event)
    }

    /*
     * Listen should return an Observable and not the publisher
     * Using ofType we filter only events that match that class type
     */
    fun <T> listen(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)
}