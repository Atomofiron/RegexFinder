package ru.atomofiron.regextool.injectable.channel

import app.atomofiron.common.util.KObservable
import ru.atomofiron.regextool.model.textviewer.TextLine

class TextViewerChannel {
    val textFromFile = KObservable<List<TextLine>>()
    val textFromFileLoading = KObservable<Boolean>()
}