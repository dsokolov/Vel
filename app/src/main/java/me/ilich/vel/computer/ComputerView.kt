package me.ilich.vel.computer

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.jakewharton.rxbinding.view.longClicks
import me.ilich.vel.R
import me.ilich.vel.Speed
import me.ilich.vel.sources.AccelerationData
import me.ilich.vel.sources.OrientationData
import rx.Observable
import java.text.SimpleDateFormat
import java.util.*

class ComputerView(val activity: Activity) {

    companion object {
        private val SDF = SimpleDateFormat("HH:mm", Locale.getDefault())
    }

    private lateinit var timeTextView: TextView
    private lateinit var speedTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var angelTextView: TextView
    private lateinit var accelerationTextView: TextView

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
        accelerationTextView = activity.findViewById(R.id.acceleration) as TextView

        stateAscentTextView = activity.findViewById(R.id.state_ascent) as TextView
        stateDescentTextView = activity.findViewById(R.id.state_descent) as TextView
    }

    fun onDestroy() {
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

    fun showAngel(orientationData: OrientationData) {
        if (-1 < orientationData.pitch && orientationData.pitch < 1) {
            angelTextView.text = "0"
        } else {
            val unsigned = Math.abs(orientationData.pitch)
            angelTextView.text = String.format("%.0f", unsigned)
        }
        if (orientationData.pitch > 1) {
            stateDescentTextView.visibility = View.VISIBLE
            stateAscentTextView.visibility = View.INVISIBLE
        } else if (orientationData.pitch < -1) {
            stateDescentTextView.visibility = View.INVISIBLE
            stateAscentTextView.visibility = View.VISIBLE
        } else {
            stateDescentTextView.visibility = View.INVISIBLE
            stateAscentTextView.visibility = View.INVISIBLE
        }
    }

    fun calibrationObservable(): Observable<Unit> = angelTextView.longClicks()

    fun showAcceleration(a: AccelerationData) {
        //val ac = Math.sqrt((a.x * a.x + a.y * a.y + a.z * a.z).toDouble()).toFloat()
        //accelerationTextView.text = String.format("%.2f", ac)
        accelerationTextView.text = String.format("%.2f", a.y)
    }

}