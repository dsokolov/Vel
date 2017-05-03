package me.ilich.vel.computer

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.view.*
import android.widget.TextView
import com.jakewharton.rxbinding.view.clicks
import com.jakewharton.rxbinding.view.longClicks
import me.ilich.vel.R
import rx.Observable

class ComputerView(val activity: Activity) : ComputerContracts.View {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var menuSettings: MenuItem
    private lateinit var menuAbout: MenuItem

    private lateinit var timeTextView: TextView
    private lateinit var speedTextView: TextView
    private lateinit var speedUnitTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var angelTextView: TextView
    private lateinit var angelUnitTextView: TextView
    private lateinit var accelerationTextView: TextView

    private lateinit var stateAscentTextView: TextView
    private lateinit var stateDescentTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity.setContentView(R.layout.activity_main)
        timeTextView = activity.findViewById(R.id.time) as TextView
        speedTextView = activity.findViewById(R.id.speed_unit) as TextView
        speedUnitTextView = activity.findViewById(R.id.speed_unit) as TextView
        errorTextView = activity.findViewById(R.id.error) as TextView
        angelTextView = activity.findViewById(R.id.angel) as TextView
        angelUnitTextView = activity.findViewById(R.id.angel_unit) as TextView
        accelerationTextView = activity.findViewById(R.id.acceleration) as TextView

        stateAscentTextView = activity.findViewById(R.id.state_ascent) as TextView
        stateDescentTextView = activity.findViewById(R.id.state_descent) as TextView

        drawerLayout = activity.findViewById(R.id.drawer) as DrawerLayout
        navigationView = activity.findViewById(R.id.navigation) as NavigationView
        menuSettings = navigationView.menu.findItem(R.id.menu_settings)
        menuAbout = navigationView.menu.findItem(R.id.menu_about)
    }

    override fun onDestroy() {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onBackPressed(onFinish: () -> Unit) {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START)
        } else {
            onFinish()
        }
    }

    override fun updateTime(time: String) {
        timeTextView.text = time
    }

    override fun updateAngelValue(angel: String) {
        angelTextView.text = angel
    }

    override fun updateAngelUnit(unit: String) {
        angelUnitTextView.text = unit
    }

    override fun updateAngleStateAscend() {
        stateAscentTextView.visibility = View.VISIBLE
        stateDescentTextView.visibility = View.INVISIBLE
    }

    override fun updateAngleStateDescend() {
        stateAscentTextView.visibility = View.INVISIBLE
        stateDescentTextView.visibility = View.VISIBLE
    }

    override fun updateAngleStateFlat() {
        stateAscentTextView.visibility = View.INVISIBLE
        stateDescentTextView.visibility = View.INVISIBLE
    }

    override fun updateSpeed(speed: String) {
        speedTextView.text = speed
    }

    override fun updateSpeedUnit(speedUnit: String) {
        speedUnitTextView.text = speedUnit
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

    override fun userCalibrate(): Observable<Unit> = angelTextView.longClicks()

    override fun userToSettings(): Observable<Unit> = menuSettings.clicks()

    override fun userToAbout(): Observable<Unit> = menuAbout.clicks()

}