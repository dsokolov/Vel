package me.ilich.vel.computer

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.jakewharton.rxbinding.view.RxView
import me.ilich.vel.R
import me.ilich.vel.Speed
import me.ilich.vel.sources.OrientationData
import rx.Observable
import rx.Subscription
import rx.subscriptions.CompositeSubscription
import java.text.SimpleDateFormat
import java.util.*

class ComputerView(
        val activity: Activity,
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

    private lateinit var stateAscentTextView: TextView
    private lateinit var stateDescentTextView: TextView

    fun onCreate(savedInstanceState: Bundle?) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity.setContentView(R.layout.activity_main)
        timeTextView = activity.findViewById(R.id.time) as TextView
        speedTextView = activity.findViewById(R.id.speed) as TextView
        errorTextView = activity.findViewById(R.id.error) as TextView
        angelTextView = activity.findViewById(R.id.angel) as TextView

        stateAscentTextView = activity.findViewById(R.id.state_ascent) as TextView
        stateDescentTextView = activity.findViewById(R.id.state_descent) as TextView

        subscription.add(
                onCalibratePress(RxView.longClicks(angelTextView))
        )
    }

    fun onDestroy() {
        subscription.unsubscribe()
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
            val unsigned = Math.abs(it.pitch)
            angelTextView.text = String.format("%.0f", unsigned)
        }
        if (it.pitch > 1) {
            stateDescentTextView.visibility = View.VISIBLE
            stateAscentTextView.visibility = View.INVISIBLE
        } else if (it.pitch < -1) {
            stateDescentTextView.visibility = View.INVISIBLE
            stateAscentTextView.visibility = View.VISIBLE
        } else {
            stateDescentTextView.visibility = View.INVISIBLE
            stateAscentTextView.visibility = View.INVISIBLE
        }
    }

}