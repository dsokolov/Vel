package me.ilich.vel.computer

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.jakewharton.rxbinding.view.clicks
import me.ilich.vel.ActivityColor
import me.ilich.vel.R
import me.ilich.vel.model.Theme
import rx.Observable


class ComputerView(val activity: ComputerActivity) : ComputerContracts.View {

    override var resetSpeedDialogObservable: Observable<Boolean>? = null

    private val activityColor = ActivityColor(activity)

    @BindView(R.id.drawer) lateinit var drawerLayout: DrawerLayout
    @BindView(R.id.navigation) lateinit var navigationView: NavigationView
    private lateinit var menuSettings: MenuItem
    private lateinit var menuAbout: MenuItem
    private lateinit var menuResetSpeed: MenuItem

    @BindView(R.id.menu) lateinit var menuImageView: ImageView
    @BindView(R.id.time) lateinit var timeTextView: TextView
    @BindView(R.id.speed_value) lateinit var speedTextView: TextView
    @BindView(R.id.speed_unit) lateinit var speedUnitTextView: TextView
    @BindView(R.id.speed_max) lateinit var maxSpeedTextView: TextView
    @BindView(R.id.speed_avg) lateinit var avgSpeedTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity.setContentView(R.layout.activity_computer)
        ButterKnife.bind(this, activity)
        menuSettings = navigationView.menu.findItem(R.id.menu_settings)
        menuAbout = navigationView.menu.findItem(R.id.menu_about)
        menuResetSpeed = navigationView.menu.findItem(R.id.menu_reset_speed)

        activityColor.created()
    }

    override fun onDestroy() {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun updateTheme(theme: Theme) {
        activityColor.changeTheme(theme)
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

    override fun userResetSpeed() = menuResetSpeed.clicks()

    override fun menuShow() {
        drawerLayout.openDrawer(Gravity.START)
    }

    override fun menuHide() {
        drawerLayout.closeDrawer(Gravity.START)
    }

    override fun isMenuVisible() = drawerLayout.isDrawerOpen(Gravity.START)

/*    override fun configureLogger() {
        activity.configureLoggerPath()
    }*/

/*    override fun updateGpsStatus(gpsStatus: GpsStatus) {
        when (gpsStatus) {
            GpsStatus.OK -> {
                gpsStatusImageView.setImageResource(R.drawable.ic_location_on)
                val color = activity.getColorByAttrId(R.attr.velColorNormal)
                gpsStatusImageView.setColorFilter(color)
            }
            GpsStatus.ERROR -> {
                gpsStatusImageView.setImageResource(R.drawable.ic_location_off)
                val color = activity.getColorByAttrId(R.attr.velColorLevelYellow)
                gpsStatusImageView.setColorFilter(color)
            }
            GpsStatus.NEED_PERMISSION -> {
                gpsStatusImageView.setImageResource(R.drawable.ic_location_off)
                val color = activity.getColorByAttrId(R.attr.velColorLevelRed)
                gpsStatusImageView.setColorFilter(color)
            }
        }
    }*/

/*    override fun updateBatteryStatus(batteryStatus: BatteryStatus) {
        when (batteryStatus.level) {
            in 0..20 -> {
                batteryStatusImageView.setImageResource(R.drawable.ic_battery_20)
                batteryStatusImageView.setColorFilter(activity.getColorByAttrId(R.attr.velColorLevelRed))
            }
            in 20..30 -> {
                batteryStatusImageView.setImageResource(R.drawable.ic_battery_30)
                batteryStatusImageView.setColorFilter(activity.getColorByAttrId(R.attr.velColorLevelYellow))
            }
            in 30..50 -> {
                batteryStatusImageView.setImageResource(R.drawable.ic_battery_50)
                batteryStatusImageView.setColorFilter(activity.getColorByAttrId(R.attr.velColorLevelGreen))
            }
            in 50..60 -> {
                batteryStatusImageView.setImageResource(R.drawable.ic_battery_60)
                batteryStatusImageView.setColorFilter(activity.getColorByAttrId(R.attr.velColorLevelGreen))
            }
            in 60..80 -> {
                batteryStatusImageView.setImageResource(R.drawable.ic_battery_80)
                batteryStatusImageView.setColorFilter(activity.getColorByAttrId(R.attr.velColorLevelGreen))
            }
            in 80..90 -> {
                batteryStatusImageView.setImageResource(R.drawable.ic_battery_90)
                batteryStatusImageView.setColorFilter(activity.getColorByAttrId(R.attr.velColorLevelGreen))
            }
            in 90..100 -> {
                batteryStatusImageView.setImageResource(R.drawable.ic_battery_full)
                batteryStatusImageView.setColorFilter(activity.getColorByAttrId(R.attr.velColorLevelGreen))
            }
            else -> {
                batteryStatusImageView.setImageResource(R.drawable.ic_battery_unknown)
                batteryStatusImageView.setColorFilter(activity.getColorByAttrId(R.attr.velColorLevelGray))
            }
        }
    }*/

    override fun showDialogSpeedReset() {
        ResetSpeedDialogFragment.show(activity.supportFragmentManager)
    }
}