package ru.atomofiron.regextool.common.util

import androidx.lifecycle.*
import androidx.lifecycle.Lifecycle.State

class LiveEvent<T> : LifecycleEventObserver {
    private var listener: (() -> Unit)? = null
    private var parameterizedListener: ((T) -> Unit)? = null

    operator fun invoke() = listener?.invoke()
    operator fun invoke(any: T) = parameterizedListener?.invoke(any)

    fun observeEvent(source: LifecycleOwner, listener: () -> Unit) {
        check()
        source.lifecycle.addObserver(this)
        this.listener = listener
    }

    fun observe(source: LifecycleOwner, listener: (T) -> Unit) {
        check()
        source.lifecycle.addObserver(this)
        this.parameterizedListener = listener
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (source.lifecycle.currentState == State.DESTROYED) {
            source.lifecycle.removeObserver(this)
            listener = null
            parameterizedListener = null
        }
    }

    private fun check() {
        val notObserved = listener == null && parameterizedListener == null
        require(notObserved) { IllegalStateException("Already") }
    }
}