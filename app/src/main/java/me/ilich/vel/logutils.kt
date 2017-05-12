package me.ilich.vel

import android.content.Context
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.util.ContextInitializer
import ch.qos.logback.core.util.StatusPrinter
import org.slf4j.LoggerFactory
import java.io.File

fun logger(clazz: Class<*>): org.slf4j.Logger {
    val className = clazz.name
    val packageName = "com.taktiklabs.skyguru"
    val loggerName = if (className.startsWith("com.taktiklabs.skyguru")) {
        className.replace(packageName, "#")
    } else {
        className
    }
    val l = LoggerFactory.getLogger(loggerName) as Logger
    if (!BuildConfig.DEBUG) {
        l.level = Level.OFF
    }
    return l
}

fun logger(name: String): org.slf4j.Logger {
    val l = LoggerFactory.getLogger(name) as Logger
    if (!BuildConfig.DEBUG) {
        l.level = Level.OFF
    }
    return l
}

fun Context.logPath(): File = getExternalFilesDir("logs") ?: File(filesDir, "logs")

fun Context.configureLoggerPath() {
    val logPath = logPath()
    System.setProperty("log_path", logPath.absolutePath)
    val lc = LoggerFactory.getILoggerFactory() as LoggerContext
    val ci = ContextInitializer(lc)
    lc.reset()
    try {
        ci.autoConfig()
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    StatusPrinter.printInCaseOfErrorsOrWarnings(lc)
}