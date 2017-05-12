package me.ilich.vel.settings

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import me.ilich.vel.R
import me.ilich.vel.model.Theme


class SettingsActivity : AppCompatActivity(), SettingsContract.View {

    private val interactor = SettingsInteractor(this)
    private val presenter = SettingsPresenter(this, interactor)
    private var created = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        created = true
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().
                    replace(R.id.content, SettingsFragment.newInstance()).
                    commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun updateTheme(theme: Theme) {
        if (created) {
            finish()
            startActivity(intent)
        } else {
            setTheme(theme.themeResId)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                android.R.id.home -> {
                    val upIntent = NavUtils.getParentActivityIntent(this)
                    if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                        TaskStackBuilder.create(this)
                                .addNextIntentWithParentStack(upIntent)
                                .startActivities()
                    } else {
                        startActivity(upIntent)
                        finish()
                    }
                    true
                }
                else -> {
                    super.onOptionsItemSelected(item)
                }
            }

    override fun onBackPressed() {
        val upIntent = NavUtils.getParentActivityIntent(this)
        startActivity(upIntent)
        super.onBackPressed()
    }

}