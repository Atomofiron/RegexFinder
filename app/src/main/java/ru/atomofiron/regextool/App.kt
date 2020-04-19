package ru.atomofiron.regextool

import android.app.Application
import android.content.Context
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import leakcanary.AppWatcher
import ru.atomofiron.regextool.di.DaggerInjector

class App : Application() {
    companion object {
        private lateinit var appContext: Context
        val pathToybox: String get() = "${appContext.filesDir}/toybox"
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        DaggerInjector.init(this)

        AppWatcher.config = AppWatcher.config.copy(enabled = false)

        val config = YandexMetricaConfig.newConfigBuilder(BuildConfig.YANDEX_API_KEY)
                .withLocationTracking(false)
                .withCrashReporting(true)
                .build()
        YandexMetrica.activate(applicationContext, config);
    }
}