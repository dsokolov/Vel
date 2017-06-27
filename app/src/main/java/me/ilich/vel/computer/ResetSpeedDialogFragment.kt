package me.ilich.vel.computer

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import me.ilich.vel.R
import rx.Observable
import rx.subjects.PublishSubject

class ResetSpeedDialogFragment : DialogFragment() {

    companion object {

        private const val TAG = "speed reset dialog"

        fun get(fm: FragmentManager): Observable<UserAnswer>? {
            val current = fm.findFragmentByTag(TAG) as ResetSpeedDialogFragment?
            return current?.subject
        }

        fun show(fm: FragmentManager): Observable<UserAnswer> {
            val current = fm.findFragmentByTag(TAG) as ResetSpeedDialogFragment?
            return if (current == null) {
                val d = ResetSpeedDialogFragment()
                d.show(fm, TAG)
                d.subject
            } else {
                current.subject
            }
        }

    }

    val subject = PublishSubject.create<UserAnswer>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(context)
        .setTitle(R.string.dialog_speed_reset_title)
        .setMessage(R.string.dialog_speed_reset_message)
        .setPositiveButton(android.R.string.yes) { dialog, which ->
            subject.onNext(UserAnswer(true))
            subject.onCompleted()
        }
        .setNegativeButton(android.R.string.no) { dialog, which ->
            subject.onNext(UserAnswer(false))
            subject.onCompleted()
        }
        .create()

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        subject.onNext(UserAnswer(false))
        subject.onCompleted()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        subject.onNext(UserAnswer(false))
        subject.onCompleted()
    }

    data class UserAnswer(val result: Boolean)

}