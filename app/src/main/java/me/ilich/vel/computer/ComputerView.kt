package me.ilich.vel.computer

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.jakewharton.rxbinding.view.clicks
import me.ilich.vel.R
import me.ilich.vel.configureLoggerPath
import me.ilich.vel.getColorByAttrId
import me.ilich.vel.model.BatteryStatus
import me.ilich.vel.model.GpsStatus
import me.ilich.vel.model.Theme
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION




class ComputerView(val activity: Activity) : ComputerContracts.View {


    private var created = false

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var menuSettings: MenuItem
    private lateinit var menuAbout: MenuItem

    private lateinit var menuImageView: ImageView
    private lateinit var gpsStatusImageView: ImageView
    private lateinit var batteryStatusImageView: ImageView
    private lateinit var timeTextView: TextView
    private lateinit var speedTextView: TextView
    private lateinit var speedUnitTextView: TextView
    private lateinit var maxSpeedTextView: TextView
    private lateinit var avgSpeedTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity.setContentView(R.layout.activity_main)
        val decorView = activity.window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.setSystemUiVisibility(uiOptions)
        created = true
        menuImageView = activity.findViewById(R.id.menu) as ImageView
        timeTextView = activity.findViewById(R.id.time) as TextView
        speedTextView = activity.findViewById(R.id.speed_value) as TextView
        speedUnitTextView = activity.findViewById(R.id.speed_unit) as TextView

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

    override fun updateTheme(it: Theme) {
        if (created) {
            activity.finish()
            activity.startActivity(activity.intent)
        } else {
            activity.setTheme(it.themeResId)
        }
    }

    override fun updateTime(time: String) {
        timeTextView.text = time
    }

    override fun updateSpeedCurrent(speed: String) {
        speedTextView.text = speed
    }

    override fun updateSpeedMax(speed: String) {
        maxSpeedTextView.text = speed
    }

    override fun updateSpeedAvg(speed: String) {
        avgSpeedTextView.text = speed
    }

    override fun updateSpeedUnit(unitResIt: Int) {
        speedUnitTextView.setText(unitResIt)
    }

    override fun userMenu() = menuImageView.clicks()

    override fun userToSettings() = menuSettings.clicks()

    override fun userToAbout() = menuAbout.clicks()

    override fun userLocation() = gpsStatusImageView.clicks()

    override fun menuShow() {
        drawerLayout.openDrawer(Gravity.START)
    }

    override fun menuHide() {
        drawerLayout.closeDrawer(Gravity.START)
    }

    override fun isMenuVisible() = drawerLayout.isDrawerOpen(Gravity.START)

    override fun configureLogger() {
        activity.configureLoggerPath()
    }

    override fun updateGpsStatus(gpsStatus: GpsStatus) {
        when (gpsStatus) {
            GpsStatus.OK -> {
                gpsStatusImageView.setImageResource(R.drawable.ic_location_on)
                val color = activity.getColorByAttrId(android.R.attr.text)
                gpsStatusImageView.setColorFilter(color)
            }
            GpsStatus.ERROR -> {
                gpsStatusImageView.setImageResource(R.drawable.ic_location_off)
                val color = activity.getColorByAttrId(R.attr.velColorWarning)
                gpsStatusImageView.setColorFilter(color)
            }
            GpsStatus.NEED_PERMISSION -> {
                gpsStatusImageView.setImageResource(R.drawable.ic_location_off)
                val color = activity.getColorByAttrId(R.attr.velColorWarning)
                gpsStatusImageView.setColorFilter(color)
            }
        }
    }

    override fun updateBatteryStatus(batteryStatus: BatteryStatus) {
        //TODO
    }

}