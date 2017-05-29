package me.ilich.vel.computer

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.util.Log
import me.ilich.vel.R
import rx.Observable
import rx.Subscriber
import rx.subscriptions.Subscriptions

class ResetSpeedDialogFragment : DialogFragment() {

    companion object {

        private const val TAG = "speed reset dialog"

        fun show(fm: FragmentManager): Observable<Boolean> {
            val d = ResetSpeedDialogFragment()
            d.show(fm, TAG)
            return Observable.unsafeCreate<Boolean> { subsctiber ->
                d.subscriber = subsctiber
            }
        }

    }

    private lateinit var subscriber: Subscriber<in Boolean>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val d = AlertDialog.Builder(context)
                .setTitle(R.string.dialog_speed_reset_title)
                .setMessage(R.string.dialog_speed_reset_message)
                .setPositiveButton(android.R.string.yes) { dialog, which ->
                    subscriber.onNext(true)
                }
                .setNegativeButton(android.R.string.no) { dialog, which ->
                    subscriber.onNext(false)
                }
                .create()
        subscriber.add(Subscriptions.create {
            d.dismiss()
        })
        return d
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        Log.v("Sokolov", "onCancel")
        subscriber.onNext(false)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        Log.v("Sokolov", "onDismiss")
        subscriber.onCompleted()
    }

}