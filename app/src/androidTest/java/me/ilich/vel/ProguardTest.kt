package me.ilich.vel

import android.support.test.runner.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProguardTest {

    /**
     * Из приложения удаляется неиспользуемый класс.
     */
    @Test fun removeAppUnusedClass() {
        try {
            val cls = Class.forName("me.ilich.vel.AppUnusedClass")
            when (BuildConfig.BUILD_TYPE) {
                "debug" -> assertNotNull(cls)
                else -> fail("Unknown BUILD_TYPE ${BuildConfig.BUILD_TYPE}")
            }
        } catch (th: Throwable) {
            when (BuildConfig.BUILD_TYPE) {
                "debug" -> fail()
                else -> fail("Unknown BUILD_TYPE ${BuildConfig.BUILD_TYPE}")
            }
        }
    }

    /**
     * Используемый класс остаётся, но переименовывается.
     */
    @Test fun renameAppUsedClass() {
        try {
            val cls = Class.forName("me.ilich.vel.AppUsedClass")
            when (BuildConfig.BUILD_TYPE) {
                "debug" -> assertNotNull(cls)
                else -> fail("Unknown BUILD_TYPE ${BuildConfig.BUILD_TYPE}")
            }
        } catch (th: Throwable) {
            when (BuildConfig.BUILD_TYPE) {
                "debug" -> fail()
                else -> fail("Unknown BUILD_TYPE ${BuildConfig.BUILD_TYPE}")
            }
        }
    }

}