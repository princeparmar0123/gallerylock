package com.calculator.vault.lock.hide.photo.video.common.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.lock.hide.photo.video.common.utils.checkIfEmpty

abstract class BaseAdapter<VB : ViewDataBinding, T>(private var layout: Int) :
    RecyclerView.Adapter<BaseAdapter<VB, T>.ViewHolder>() {

    /**
     * This is the list which is displayed to the User
     * */
    val displayList = ArrayList<T>()

    private lateinit var mRecyclerView: RecyclerView
    private var emptyView: View? = null

    private var onClickView: ((View, Int, T) -> Unit)? = null
    private var onLongClickView: ((View, Int, T) -> Unit)? = null
    private var onSetItemPercentageWidth: (() -> Int)? = null
    private var layoutSelectorListener: ILayoutSelector? = null

    interface ILayoutSelector {
        fun selectLayout(viewType: Int): Int
    }

    fun setLayoutSelector(layoutSelectorListener: ILayoutSelector) {
        this.layoutSelectorListener = layoutSelectorListener
    }

    fun setItemClickListener(onClickView: (View, Int, T) -> Unit) {
        this.onClickView = onClickView
    }

    fun setItemLongClickListener(onLongClickView: (View, Int, T) -> Unit) {
        this.onLongClickView = onLongClickView
    }

    fun setItemPercentageWidth(block: (() -> Int)) {
        this.onSetItemPercentageWidth = block
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = DataBindingUtil.inflate<VB>(
            LayoutInflater.from(parent.context),
            layoutSelectorListener?.selectLayout(viewType) ?: layout,
            parent,
            false
        )
        val view = viewBinding.root
        /**
         * This logic is been used when user wants to display the percentage
         * width of the screen when using it in horizontal recyclerview.
         * */
        onSetItemPercentageWidth?.invoke()?.let { percentageWidth ->
            val param = view.layoutParams
            param.width =
                (parent.context.resources.displayMetrics.widthPixels * (percentageWidth.toFloat() / 100)).toInt()
            view.layoutParams = param
        }

        return ViewHolder(viewBinding, view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            onBind(holder.binding, position, displayList[position], payloads)
            onViewHolderBind(holder)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBind(holder.binding, position, displayList[position])
        onViewHolderBind(holder)
    }

    fun getItem(position: Int): T {
        return displayList[position]
    }

    fun getAllItems() = displayList

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return displayList.size
    }

    inner class ViewHolder(val binding: VB, view: View) : RecyclerView.ViewHolder(view) {

        init {
            setClickableView(binding).forEach { clickView ->
                clickView?.setOnClickListener { view ->
                    if (bindingAdapterPosition >= 0 && bindingAdapterPosition < displayList.size) {
                        onClickView?.let {
                            it(
                                view,
                                bindingAdapterPosition,
                                displayList[bindingAdapterPosition]
                            )
                        }
                    }
                }
            }

            setLongClickableView(binding).forEach { longClickView ->
                longClickView?.setOnLongClickListener { view ->
                    if (bindingAdapterPosition >= 0 && bindingAdapterPosition < displayList.size) {
                        onLongClickView?.let {
                            it(view, bindingAdapterPosition, displayList[bindingAdapterPosition])
                        }
                    }
                    true
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    fun addItemAt(index: Int, item: T) {
        displayList.add(index, item)
        notifyItemInserted(index)
    }

    fun addItem(item: T) {
        displayList.add(item)
        notifyItemInserted(displayList.size)
        if (::mRecyclerView.isInitialized) mRecyclerView.checkIfEmpty(emptyView)
    }

    fun appendAll(dataList: Collection<T>) {
        displayList.addAll(dataList)
        notifyDataSetChanged()
        if (::mRecyclerView.isInitialized) mRecyclerView.checkIfEmpty(emptyView)
    }

    open fun addAll(dataList: Collection<T>) {
        displayList.clear()
        displayList.addAll(dataList)
        notifyDataSetChanged()
        if (::mRecyclerView.isInitialized) mRecyclerView.checkIfEmpty(emptyView)
    }

    fun removeItemAt(position: Int) {
        if (0 <= position && position < displayList.size) {
            displayList.removeAt(position)
            notifyItemRemoved(position)
        }
        if (::mRecyclerView.isInitialized) mRecyclerView.checkIfEmpty(emptyView)
    }

    fun removeItem(item: T) {
        displayList.remove(item)
        notifyDataSetChanged()
        if (::mRecyclerView.isInitialized) mRecyclerView.checkIfEmpty(emptyView)
    }

    fun clearAll() {
        displayList.clear()
        notifyDataSetChanged()
        if (::mRecyclerView.isInitialized) mRecyclerView.checkIfEmpty(emptyView)
    }

    fun setEmptyView(emptyView: View?) {
        this.emptyView = emptyView
    }

    abstract fun setClickableView(binding: VB): List<View?>
    open fun setLongClickableView(binding: VB): List<View?> = listOf()

    abstract fun onBind(binding: VB, position: Int, item: T, payloads: MutableList<Any>? = null)

    open fun onViewHolderBind(viewHolder: ViewHolder) {}
}
