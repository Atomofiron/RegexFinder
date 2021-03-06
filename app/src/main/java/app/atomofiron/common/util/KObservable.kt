package app.atomofiron.common.util

import java.util.*

// будет заменён корутинами
open class KObservable<T : Any?>(private val single: Boolean = false) {
    private var changed = false
    private val callbacks = Vector<RemoveObserverCallback>()
    private val observers = Vector<(T) -> Unit>()
    private var nullableValue: T? = null
    @Suppress("UNCHECKED_CAST")
    val value: T get() = nullableValue as T

    constructor(value: T, single: Boolean = false) : this(single) {
        nullableValue = value
    }

    @Synchronized
    fun addObserver(removeCallback: RemoveObserverCallback, observer: (T) -> Unit) {
        require(!callbacks.contains(removeCallback)) { Exception("Callback already added!") }
        addObserver(observer)
        callbacks.add(removeCallback)
        removeCallback.addOneTimeObserver {
            callbacks.remove(removeCallback)
            removeObserver(observer)
        }
    }

    private fun addObserver(observer: (T) -> Unit) {
        observers.addElement(observer)
        if (!single && nullableValue != null) {
            observer.invoke(value)
        }
    }

    @Synchronized
    private fun removeObserver(o: (T) -> Unit) = observers.removeElement(o)

    @Synchronized
    fun setAndNotify(value: T) {
        nullableValue = value
        changed = true
        notifyObservers()
    }

    @Synchronized
    fun justNotify() {
        changed = true
        notifyObservers()
    }

    private fun notifyObservers() {
        val value: T
        val arrLocal: Array<(T) -> Unit>

        synchronized(this) {
            if (!changed) {
                return
            }
            value = this.value
            arrLocal = observers.toTypedArray()
            changed = false
        }

        for (i in arrLocal.indices) {
            arrLocal[i].invoke(value)
        }
    }

    @Synchronized
    fun size(): Int = observers.size

    class RemoveObserverCallback {
        private val id = UUID.randomUUID().hashCode()
        private val observers = Vector<() -> Unit>()

        fun addOneTimeObserver(observer: () -> Unit) = observers.addElement(observer)

        fun invoke() {
            observers.forEach { it() }
            observers.clear()
        }

        override fun equals(other: Any?): Boolean = when (other) {
            !is RemoveObserverCallback -> false
            else -> other.id == id
        }

        override fun hashCode(): Int = id
    }
}