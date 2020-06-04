package ru.atomofiron.regextool.injectable.service

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.atomofiron.regextool.injectable.channel.TextViewerChannel
import ru.atomofiron.regextool.injectable.store.PreferenceStore
import ru.atomofiron.regextool.logE
import ru.atomofiron.regextool.model.finder.FinderQueryParams
import ru.atomofiron.regextool.model.textviewer.TextLine
import ru.atomofiron.regextool.utils.Const
import ru.atomofiron.regextool.utils.Shell

class TextViewerService(
        private val textViewerChannel: TextViewerChannel,
        private val preferenceStore: PreferenceStore
) {
    companion object {
        private const val UNKNOWN = -1L
    }
    private class Match(
            val byteOffset: Long,
            val text: String
    )
    private val matches = HashMap<Int, MutableList<Match>>()
    private val lines = ArrayList<TextLine>()
    private val useSu: Boolean get() = preferenceStore.useSu.value
    private var lock = false
    private val mutex = Mutex()

    private lateinit var path: String
    private var textOffset = 0L
    private var isEndReached = false
    private var fileSize = UNKNOWN

    init {
        textViewerChannel.textFromFile.setAndNotify(lines)
    }

    suspend fun loadFile(path: String, params: FinderQueryParams?) {
        this.path = path
        fileSize = getFileSize()
        when (params) {
            null -> onLineVisible(0)
            else -> searchInFile(params)
        }
    }

    suspend fun onLineVisible(index: Int) {
        if (!isEndReached && index > lines.size - Const.TEXT_FILE_PAGINATION_STEP_OFFSET) {
            mutex.withLock(lock) {
                if (lock) {
                    return
                }
                lock = true
            }

            textViewerChannel.textFromFileLoading.setAndNotify(true)
            loadNext()
            textViewerChannel.textFromFileLoading.setAndNotify(false)

            lock = false
        }
    }

    private fun loadNext() {
        val fileSize = getFileSize()
        if (this.fileSize != fileSize) {
            logE("File size was changed! $path")
            isEndReached = true
            return
        }
        val offset = lines.size
        val cmd = Shell[Shell.HEAD_TAIL].format(path, offset + Const.TEXT_FILE_PAGINATION_STEP, Const.TEXT_FILE_PAGINATION_STEP)
        Shell.exec(cmd, useSu) { line ->
            // todo next map text lines with matches
            val textLine = TextLine(line)
            lines.add(textLine)
            textOffset += line.toByteArray(Charsets.UTF_8).size.inc()
        }
        textViewerChannel.textFromFile.setAndNotify(ArrayList(lines))

        isEndReached = lines.size - offset < Const.TEXT_FILE_PAGINATION_STEP
        isEndReached = isEndReached || textOffset >= fileSize
    }

    private fun getFileSize(): Long {
        val lsLong = Shell[Shell.LS_LOG].format(path)
        val output = Shell.exec(lsLong, useSu)
        return when {
            output.success -> output.output.split(Const.SPACE)[2].toLong()
            else -> UNKNOWN
        }
    }

    private suspend fun searchInFile(params: FinderQueryParams) {
        val template = when {
            params.useRegex && params.ignoreCase -> Shell.GREP_IE
            params.useRegex -> Shell.GREP_E
            params.ignoreCase -> Shell.GREP_I
            else -> Shell.GREP
        }
        val cmd = Shell[template].format(params.query, path)
        Shell.exec(cmd, useSu) {
            val lineByteOffset = it.split(':')
            val lineIndex = lineByteOffset[0].toInt()
            val byteOffset = lineByteOffset[1].toLong()
            val text = lineByteOffset[2]
            val match = Match(byteOffset, text)
            var list = matches[lineIndex]
            if (list == null) {
                list = ArrayList()
                matches[lineIndex] = list
            }
            list.add(match)
        }
        onLineVisible(0)
    }
}