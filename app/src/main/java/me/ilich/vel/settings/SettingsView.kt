package me.ilich.vel.settings

import android.os.Bundle
import android.support.v7.widget.Toolbar
import me.ilich.vel.R
import me.ilich.vel.model.Theme

class SettingsView(val activity: SettingsActivity) : SettingsContract.View {

    private var created = false

    override fun inflate(savedInstanceState: Bundle?) {
        activity.setContentView(R.layout.activity_settings)
        val toolbar = activity.findViewById(R.id.toolbar) as Toolbar
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState == null) {
            activity.fragmentManager.beginTransaction().
                    replace(R.id.content, SettingsFragment.newInstance()).
                    commit()
        }
        created = true
    }

    override fun updateTheme(theme: Theme) {
        if (created) {
            activity.recreate()
        } else {
            activity.setTheme(theme.themeResId)
        }
    }

}
