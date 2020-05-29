package ru.atomofiron.regextool.screens.result.presenter

import ru.atomofiron.regextool.R
import ru.atomofiron.regextool.custom.view.bottom_sheet_menu.BottomSheetMenuListener
import ru.atomofiron.regextool.injectable.interactor.ResultInteractor
import ru.atomofiron.regextool.model.finder.FinderResult
import ru.atomofiron.regextool.screens.result.ResultViewModel

class BottomSheetMenuListenerDelegate(
        private val viewModel: ResultViewModel,
        private val interactor: ResultInteractor
) : BottomSheetMenuListener {

    override fun onMenuItemSelected(id: Int) {
        val item = viewModel.showOptions.data!!.items[0]
        when (id) {
            R.id.menu_copy_path -> {
                interactor.copyToClipboard(item as FinderResult)
                viewModel.alerts.invoke(viewModel.context.getString(R.string.copied))
            }
            R.id.menu_remove -> interactor.deleteItems(listOf(item), viewModel.task.value.uuid)
        }
    }
}