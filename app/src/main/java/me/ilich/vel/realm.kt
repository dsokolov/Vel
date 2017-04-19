package me.ilich.vel

import io.realm.Realm
import io.realm.RealmModel
import rx.Observable

fun Realm.transactionObservable(transaction: (Realm) -> (Unit)): Observable<Unit> =
        Observable.create { subscriber ->
            this.executeTransactionAsync({ realm ->
                transaction(realm)
            }, {
                subscriber.onNext(Unit)
                subscriber.onCompleted()
            }, { th ->
                subscriber.onError(th)
            })
        }

fun <E : RealmModel> Realm.firstOrCreate(cls: Class<E>): E =
        where(cls).findFirst() ?: createObject(cls)