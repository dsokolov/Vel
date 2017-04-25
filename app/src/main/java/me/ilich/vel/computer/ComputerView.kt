package me.ilich.vel.computer

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.jakewharton.rxbinding.view.longClicks
import me.ilich.vel.R
import me.ilich.vel.model.sources.OrientationEntity
import rx.Observable

class ComputerView(val activity: Activity) : ComputerContracts.View {

    private lateinit var timeTextView: TextView
    private lateinit var speedTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var angelTextView: TextView
    private lateinit var accelerationTextView: TextView

    private lateinit var stateAscentTextView: TextView
    private lateinit var stateDescentTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
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

    override fun onDestroy() {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun updateTime(time: String) {
        timeTextView.text = time
    }

    override fun updateAngel(angel: String) {
        angelTextView.text = angel
    }

    override fun updateSpeed(speed: String) {
        speedTextView.text = speed
    }

    override fun updatePermissionsError(visible: Boolean) {
        errorTextView.visibility = if (visible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun updateAcceleration(acceleration: String) {
        accelerationTextView.text = acceleration
    }

    fun showAngel(orientationData: OrientationEntity) {
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

    override fun userCalibrate(): Observable<Unit> = angelTextView.longClicks()


}