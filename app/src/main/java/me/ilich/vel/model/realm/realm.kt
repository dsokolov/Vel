package me.ilich.vel.model.realm

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.Sort
import rx.Observable

fun Realm.transactionObservable(transaction: (Realm) -> (Unit)): Observable<Unit> =
        Observable.unsafeCreate { subscriber ->
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

fun Realm.deleteUntilCount(cls: Class<out RealmObject>, untilCount: Int, sortFiled: String? = null, sortOrder: Sort = Sort.ASCENDING) {
    val l = if (sortFiled == null) {
        this.where(cls).findAll()
    } else {
        this.where(cls).findAllSorted(sortFiled, sortOrder)
    }
    val count = l.size
    val startIndex = untilCount
    val endIndex = count - 1
    for (i in startIndex..endIndex) {
        l.deleteFromRealm(startIndex)
    }
}