package ru.atomofiron.regextool.screens.explorer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.atomofiron.regextool.R
import app.atomofiron.common.recycler.GeneralAdapter
import ru.atomofiron.regextool.iss.service.model.XFile
import ru.atomofiron.regextool.screens.explorer.adapter.ItemSpaceDecorator.Divider
import ru.atomofiron.regextool.screens.explorer.adapter.ItemShadowDecorator.Shadow

class ExplorerAdapter : GeneralAdapter<ExplorerHolder, XFile>() {
    companion object {
        private const val VIEW_TYPE = 1
        private const val VIEW_POOL_MAX_COUNT = 30
    }

    private var onItemActionListener: ItemActionListener? = null
    private var viewPool: Array<View?>? = null

    private var currentDir: XFile? = null

    private val spaceDecorator = ItemSpaceDecorator { i ->
        val item = items[i]
        when {
            item.isOpened && item.files.isNullOrEmpty() -> Divider.BIG
            item.isOpened -> Divider.SMALL
            i.inc() >= items.size -> Divider.NO // не делаем отступ у последнего элемента
            i.inc() == items.size && item.completedParentPath == currentDir?.completedPath -> Divider.SMALL
            i.inc() != items.size && item.completedParentPath != items[i.inc()].completedParentPath -> Divider.SMALL
            else -> Divider.NO
        }
    }

    private val highlightDecorator = ItemShadowDecorator { position ->
        val currentDir = currentDir ?: return@ItemShadowDecorator Shadow.NO

        val item = items[position]
        val isCurrent = item.completedPath == currentDir.completedPath
        val isCurrentChild = item.completedParentPath == currentDir.completedPath
        if (!isCurrent && !isCurrentChild) {
            return@ItemShadowDecorator Shadow.NO
        }

        when {
            item.isOpened && item.files.isNullOrEmpty() -> Shadow.DOUBLE
            item.isOpened -> Shadow.TOP
            position.inc() == items.size -> Shadow.NO // не рисуем тень под последним элементом
            item.completedParentPath != items[position.inc()].completedParentPath -> Shadow.BOTTOM
            else -> Shadow.NO
        }
    }

    fun setCurrentDir(dir: XFile?) {
        currentDir = dir
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.itemAnimator = null
        recyclerView.recycledViewPool.setMaxRecycledViews(VIEW_TYPE, VIEW_POOL_MAX_COUNT)

        viewPool = arrayOfNulls(VIEW_POOL_MAX_COUNT)
        val inflater = LayoutInflater.from(recyclerView.context)
        for (i in viewPool!!.indices) {
            viewPool!![i] = inflateNewView(inflater, recyclerView)
        }

        recyclerView.addItemDecoration(spaceDecorator)
        recyclerView.addItemDecoration(highlightDecorator)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        recyclerView.removeItemDecoration(spaceDecorator)
        recyclerView.removeItemDecoration(highlightDecorator)
    }

    override fun getItemViewType(position: Int): Int = VIEW_TYPE

    override fun getItemId(position: Int): Long = items[position].hashCode().toLong()

    fun setOnItemActionListener(listener: ItemActionListener?) {
        onItemActionListener = listener
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExplorerHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = getNewView(inflater, parent)

        return ExplorerHolder(view)
    }

    override fun onBindViewHolder(holder: ExplorerHolder, position: Int) {
        holder.onItemActionListener = onItemActionListener
        super.onBindViewHolder(holder, position)
        onItemActionListener?.onItemVisible(holder.item)
    }

    override fun onViewDetachedFromWindow(holder: ExplorerHolder) {
        super.onViewDetachedFromWindow(holder)
        onItemActionListener?.onItemInvalidate(holder.item)
    }

    private fun getNewView(inflater: LayoutInflater, parent: ViewGroup): View {
        val viewPool = viewPool
        if (viewPool != null) {
            for (i in viewPool.indices) {
                val view = viewPool[i]
                if (view != null) {
                    viewPool[i] = null
                    return view
                }
            }
            this.viewPool = null
        }
        return inflateNewView(inflater, parent)
    }

    private fun inflateNewView(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.item_explorer_file, parent, false)
    }
}