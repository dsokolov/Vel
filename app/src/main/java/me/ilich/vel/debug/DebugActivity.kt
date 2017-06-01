package me.ilich.vel.debug

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import me.ilich.vel.ActivityColor
import me.ilich.vel.R
import me.ilich.vel.model.Theme

class DebugActivity : AppCompatActivity() {

    val activityColor = ActivityColor(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityColor.changeTheme(Theme.Forest())
        setContentView(R.layout.activity_debug)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        activityColor.created()
        findViewById(R.id.button3).setOnClickListener {
            DebugDialogFragment.show(supportFragmentManager)
        }
    }

}