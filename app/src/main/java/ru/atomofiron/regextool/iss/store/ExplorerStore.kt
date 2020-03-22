package ru.atomofiron.regextool.iss.store

import app.atomofiron.common.util.KObservable
import ru.atomofiron.regextool.iss.service.explorer.model.Change
import ru.atomofiron.regextool.iss.service.explorer.model.MutableXFile
import ru.atomofiron.regextool.iss.service.explorer.model.XFile
import ru.atomofiron.regextool.log2

class ExplorerStore {
    val items: MutableList<MutableXFile> = ArrayList()
    val store = KObservable<List<XFile>>(items)
    val updates = KObservable<Change>(Change.Nothing, single = true)
    val current = KObservable<XFile?>(null)

    fun notifyCurrent(item: XFile?) {
        log2("notifyCurrent $item")
        current.setAndNotify(item)
    }

    fun notifyUpdate(item: XFile) {
        log2("notifyUpdate $item")
        updates.setAndNotify(Change.Update(item))
    }

    fun notifyRemove(item: XFile) {
        log2("notifyRemove $item")
        updates.setAndNotify(Change.Remove(item))
    }

    fun notifyInsert(previous: XFile, item: XFile) {
        log2("notifyInsert $item after $previous")
        updates.setAndNotify(Change.Insert(previous, item))
    }

    fun notifyRemoveRange(items: List<XFile>) {
        log2("notifyRemoveRange ${items.size}")
        updates.setAndNotify(Change.RemoveRange(items))
    }

    fun notifyInsertRange(previous: XFile, items: List<XFile>) {
        log2("notifyInsert ${items.size} after $previous")
        updates.setAndNotify(Change.InsertRange(previous, items))
    }

    fun notifyItems() {
        log2("notifyFiles ${items.size}")
        store.setAndNotify(items)
    }
}