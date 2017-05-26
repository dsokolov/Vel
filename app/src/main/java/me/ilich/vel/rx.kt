package me.ilich.vel

import rx.Observable
import java.util.*
import java.util.concurrent.TimeUnit

fun <R> Observable<out R>.sampleWithTtlAndDefault(interval: Long, ttl: Long, unit: TimeUnit, default: R): Observable<out R> {

    data class SampleData<R>(
            val data: R,
            val isDefault: Boolean,
            val emittedAt: Date
    )

    val regularEmitter = this.
            map { SampleData(it, false, Date()) }
    val mergeEmitter = regularEmitter
            .buffer(interval, unit)
            .map { buffer ->
                buffer.find { !it.isDefault } ?: SampleData(default, true, Date())
            }
            .scan { total: SampleData<R>, current: SampleData<R> ->
                if (current.isDefault) {
                    val age = Date().time - total.emittedAt.time
                    val outdated = unit.convert(age, TimeUnit.MILLISECONDS) >= ttl
                    if (outdated) {
                        current
                    } else {
                        total
                    }
                } else {
                    current
                }
            }
            .map { it.data }
    return mergeEmitter
}
