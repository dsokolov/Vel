package me.ilich.vel.computer

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.jakewharton.rxbinding.view.RxView
import me.ilich.vel.sources.OrientationData
import me.ilich.vel.R
import me.ilich.vel.Speed
import rx.Observable
import rx.Subscription
import rx.subscriptions.CompositeSubscription
import java.text.SimpleDateFormat
import java.util.*

class ComputerView(
        val onCalibratePress: (Observable<Void>) -> Subscription
) {

    companion object {
        private val SDF = SimpleDateFormat("HH:mm", Locale.getDefault())
    }

    private val subscription = CompositeSubscription()

    private lateinit var timeTextView: TextView
    private lateinit var speedTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var angelTextView: TextView

    fun onCreate(activity: Activity, savedInstanceState: Bundle?) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.setContentView(R.layout.activity_main)
        timeTextView = activity.findViewById(R.id.time) as TextView
        speedTextView = activity.findViewById(R.id.speed) as TextView
        errorTextView = activity.findViewById(R.id.error) as TextView
        angelTextView = activity.findViewById(R.id.angel) as TextView

        subscription.add(
                onCalibratePress(RxView.longClicks(angelTextView))
        )
    }

    fun onDestroy() {
        subscription.unsubscribe()
    }

    fun showTime(time: Date) {
        val s = SDF.format(time)
        timeTextView.text = s
    }

    fun hideError() {
        errorTextView.visibility = View.GONE
    }

    fun showError(s: String) {
        errorTextView.visibility = View.VISIBLE
        errorTextView.text = s
    }

    fun showSpeed(speed: Speed) {
        val speedKmph = speed / 0.36
        speedTextView.text = String.format("%.2f", speedKmph)
    }

    fun showAngel(it: OrientationData.Values) {
        if (-1 < it.pitch && it.pitch < 1) {
            angelTextView.text = "0"
        } else {
            angelTextView.text = String.format("%.0f", it.pitch)
        }
    }

}