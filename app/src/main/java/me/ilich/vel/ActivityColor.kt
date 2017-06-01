package me.ilich.vel

import android.app.Activity
import android.os.Build
import me.ilich.vel.model.Theme

class ActivityColor(val activity: Activity) {

    private var created = false

    fun created() {
        created = true
    }

    fun changeTheme(theme: Theme) {
        if (created) {
            activity.recreate()
        } else {
            activity.setTheme(theme.themeResId)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = activity.window
                window.statusBarColor = activity.getColorByAttrId(R.attr.colorPrimaryDark)
                window.navigationBarColor = activity.getColorByAttrId(R.attr.colorPrimaryDark)
            }
        }
    }

}