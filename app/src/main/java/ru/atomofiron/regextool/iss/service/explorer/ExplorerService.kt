package ru.atomofiron.regextool.iss.service.explorer

import android.content.SharedPreferences
import kotlinx.coroutines.sync.withLock
import ru.atomofiron.regextool.iss.service.explorer.model.MutableXFile
import ru.atomofiron.regextool.iss.service.explorer.model.XFile
import ru.atomofiron.regextool.iss.store.ExplorerStore
import ru.atomofiron.regextool.log2
import ru.atomofiron.regextool.utils.Const

class ExplorerService(
        private val preferences: SharedPreferences,
        explorerStore: ExplorerStore
) : PrivateExplorerServiceLogic(explorerStore) {

    fun persistState() {
        preferences.edit().putString(Const.PREF_CURRENT_DIR, currentOpenedDir?.completedPath).apply()
    }

    suspend fun setRoots(vararg path: String) {
        val roots = path.map { MutableXFile.byPath(it) }

        mutex.withLock {
            // todo remove old root and children
            files.clear()
            files.addAll(roots)
        }
        explorerStore.notifyItems()
        roots.forEach { updateClosedDir(it) }
    }

    fun invalidateDir(f: XFile) {
        val dir = findItem(f) ?: return
        invalidateDir(dir)
    }

    suspend fun openDir(f: XFile) {
        val dir = findItem(f) ?: return
        if (!dir.isDirectory) {
            log2("openDir return $dir")
            return
        }
        if (!dir.isCached) {
            return updateClosedDir(dir)
        }
        when {
            dir == currentOpenedDir -> closeDir(dir)
            dir.isOpened -> reopenDir(dir)
            else -> openDir(dir)
        }
    }

    suspend fun updateItem(f: XFile) {
        val file = findItem(f) ?: return
        log2("updateItem $file")
        when {
            file.isOpened && file == currentOpenedDir -> updateCurrentDir(file)
            file.isOpened -> Unit
            file.isDirectory -> updateClosedDir(file)
            else -> return updateFile(file)
        }
    }

    fun checkItem(item: XFile, isChecked: Boolean) {
        findItem(item)?.isChecked = isChecked
    }
}