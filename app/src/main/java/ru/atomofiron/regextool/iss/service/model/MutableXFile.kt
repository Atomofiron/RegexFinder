package ru.atomofiron.regextool.iss.service.model

import ru.atomofiron.regextool.log
import ru.atomofiron.regextool.utils.Shell
import java.io.File

class MutableXFile : XFile {
    companion object {
        private const val TOTAL = "total"
        private const val DIR_CHAR = 'd'

        var toyboxPath: String = ""
    }
    override var files: MutableList<MutableXFile>? = null

    override var isOpened: Boolean = false
    private set(value) {
        require(file.isDirectory || !value) { "$completedPath is not a directory!" }
        field = value
    }

    var isCaching: Boolean = false
        private set
    override val isCached: Boolean get() = isCaching || files != null

    override val completedPath: String by lazy {
        if (file.isFile || file.absolutePath.endsWith("/")) {
            file.absolutePath
        } else {
            file.absolutePath + "/"
        }
    }

    override val completedParentPath: String by lazy {
        completedPath.replace(Regex("(?<=/)/*[^/]+/*$"), "")
    }

    override val file: File
    override val access: String
    override val owner: String
    override val group: String
    override val size: String
    override val date: String
    override val time: String
    override val name: String

    override val isDirectory: Boolean

    constructor(file: File) {
        this.file = file
        access = ""
        owner = ""
        group = ""
        size = ""
        date = ""
        time = ""
        name = file.name
        isDirectory = file.isDirectory
    }

    constructor(parent: String, line: String) {
        val parts = line.split(Regex(" +"), 8)
        file = File(parent, parts[7])
        access = parts[0]
        owner = parts[2]
        group = parts[3]
        size = parts[4]
        date = parts[5]
        time = parts[6]
        name = parts[7]
        isDirectory = access[0] == DIR_CHAR
    }

    fun open() {
        log("open() $this")
        isOpened = files!!.isNotEmpty()
        files!!.filter { it.isOpened }.forEach { it.close() }
    }

    fun close() {
        log("close() $this")
        isOpened = false
    }

    fun clearChildren() {
        log("clearChildren() $this")
        files!!.forEach { it.clear() }
    }

    /** @return error or null */
    fun cache(su: Boolean = false): String? {
        isCaching = true
        log("cache() $this")
        require(file.isDirectory) { UnsupportedOperationException("$this is not a directory!") }
        val output = Shell.exec(Shell.LS_LAHL.format(toyboxPath, completedPath), su)
        return if (output.success) {
            val lines = output.output.split("\n")
            val dirs = ArrayList<MutableXFile>()
            val files = ArrayList<MutableXFile>()

            if (lines.isNotEmpty()) {
                var start = 0
                if (lines[start].startsWith(TOTAL)) {
                    start++
                }
                for (i in start until lines.size) {
                    if (lines[i].isNotEmpty()) {
                        val file = MutableXFile(completedPath, lines[i])
                        if (file.file.isDirectory) {
                            dirs.add(file)
                        } else {
                            files.add(file)
                        }
                    }
                }
            } else {
                log("empty $this")
            }

            dirs.sortBy { it.name }
            files.sortBy { it.name }
            files.addAll(0, dirs)

            this.files = files
            isCaching = false
            log("cached $this ${files.size} files")
            null
        } else {
            log("output.error $completedPath ${output.error}")
            this.files = ArrayList()
            isCaching = false
            output.error
        }
    }

    private fun clear() {
        if (files == null) {
            return
        }
        log("clear() $this")
        files = null
        isOpened = false
    }

    override fun equals(other: Any?): Boolean {
        return when {
            other == null -> false
            other !is MutableXFile -> false
            other.isDirectory != isDirectory -> false
            else -> other.file.absolutePath == file.absolutePath
        }
    }

    override fun hashCode(): Int = completedPath.hashCode()

    override fun toString(): String = completedPath
}