package me.ilich.vel

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class VelApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val realmCfg = RealmConfiguration.Builder().
                deleteRealmIfMigrationNeeded().
                build()
        Realm.setDefaultConfiguration(realmCfg)
    }

}