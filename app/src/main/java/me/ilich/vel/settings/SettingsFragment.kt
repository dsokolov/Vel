package me.ilich.vel.settings

import android.os.Bundle
import android.preference.PreferenceFragment
import me.ilich.vel.R

class SettingsFragment : PreferenceFragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
    }

}