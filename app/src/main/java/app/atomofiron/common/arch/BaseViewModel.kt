package app.atomofiron.common.arch

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import app.atomofiron.common.util.KObservable
import app.atomofiron.common.util.property.MutableWeakProperty
import kotlinx.coroutines.CoroutineScope
import ru.atomofiron.regextool.log2

abstract class BaseViewModel<D, F : Fragment>(app: Application) : AndroidViewModel(app) {
    protected open lateinit var scope: CoroutineScope
    protected abstract val component: D

    protected val fragmentProperty = MutableWeakProperty<F>()

    val onClearedCallback = KObservable.RemoveObserverCallback()

    open fun inject(fragment: F) {
        log2("inject")
        fragmentProperty.value = fragment
    }

    override fun onCleared() {
        super.onCleared()
        onClearedCallback.invoke()
    }
}