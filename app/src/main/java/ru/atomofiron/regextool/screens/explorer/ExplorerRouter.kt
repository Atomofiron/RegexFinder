package ru.atomofiron.regextool.screens.explorer

import android.content.Intent
import androidx.fragment.app.Fragment
import app.atomofiron.common.base.BaseRouter
import ru.atomofiron.regextool.iss.service.model.XFile
import ru.atomofiron.regextool.iss.store.SettingsStore
import ru.atomofiron.regextool.screens.finder.FinderFragment
import ru.atomofiron.regextool.screens.preferences.PreferencesFragment
import ru.atomofiron.regextool.utils.Util

class ExplorerRouter : BaseRouter() {
    private val openedFiles = ArrayList<Fragment>()

    fun showFinder() {
        switchScreen(addToBackStack = false) {
            it is FinderFragment
        }
    }

    fun showSettings() = startScreen(PreferencesFragment())

    fun showFile(item: XFile) {
        val extraFormats = SettingsStore.extraFormats.entity
        if (Util.isTextFile(item.completedPath, extraFormats)) {
            // todo open file
        } else {
            activity {
                val intent = Intent(Intent.ACTION_VIEW)

            }
        }
    }
}