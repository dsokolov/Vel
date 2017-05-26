package me.ilich.vel.realm

import io.realm.Realm
import rx.Observable

class RealmWrapper {

    private lateinit var realm: Realm

    fun init(): Observable<Unit> =
            Observable.just(Unit)
                    .doOnNext {
                        realm = Realm.getDefaultInstance()
                    }
                    .subscribeOn(RealmSchedulers.getInstance())

    fun close(): Observable<Unit> =
            Observable.just(Unit)
                    .doOnNext {
                        realm.close()
                    }
                    .subscribeOn(RealmSchedulers.getInstance())

    fun realm(): Observable<Realm> =
            Observable.just(realm)
                    .subscribeOn(RealmSchedulers.getInstance())

    fun <R> exec(action: (realm: Realm) -> R): Observable<R> =
            Observable.just(realm)
                    .map { action(it) }
                    .subscribeOn(RealmSchedulers.getInstance())

}