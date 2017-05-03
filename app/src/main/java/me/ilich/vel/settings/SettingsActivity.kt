package me.ilich.vel.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.ilich.vel.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().
                    replace(R.id.content, SettingsFragment.newInstance()).
                    commit()
        }
    }

}