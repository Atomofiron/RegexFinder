package app.atomofiron.common.arch

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import app.atomofiron.common.util.permission.PermissionResultListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import ru.atomofiron.regextool.App
import ru.atomofiron.regextool.log2

abstract class BasePresenter<M : BaseViewModel<*,*>, R : BaseRouter>(
        protected val viewModel: M,
        private val coroutineScope: CoroutineScope? = null,
        private val permissionResultListener: PermissionResultListener? = null
) : PermissionResultListener {
    protected val context: Context get() = viewModel.getApplication<App>().applicationContext
    protected abstract val router: R
    protected val scope: CoroutineScope get() = coroutineScope!!

    protected val onClearedCallback = viewModel.onClearedCallback

    init {
        log2("init")

        viewModel.onClearedCallback.addOneTimeObserver(::onCleared)
    }

    fun onCreate(context: Context, intent: Intent) = Unit

    open fun onSubscribeData() = Unit

    open fun onVisibleChanged(visible: Boolean) = Unit

    protected open fun onCleared() {
        log2("onCleared")
        coroutineScope?.cancel("${this.javaClass.simpleName}.onCleared()")
    }

    fun onAttachChildFragment(childFragment: Fragment) = router.onAttachChildFragment(childFragment)

    open fun onBackButtonClick(): Boolean = router.onBack()

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionResultListener?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}