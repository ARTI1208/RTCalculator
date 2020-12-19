package ru.art2000.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.R
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView

class RecyclerWithEmptyView(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
    : RecyclerView(context, attrs, defStyleAttr) {


    @SuppressLint("PrivateResource")
    constructor(context: Context, attrs: AttributeSet?) :
            this(context, attrs, R.attr.recyclerViewStyle)

    constructor(context: Context) : this(context, null)

    private val emptyAdapter = object : Adapter<EmptyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmptyViewHolder {

            val generator = emptyViewGenerator

            check(generator != null) {
                "Provide empty view layout id or view generator"
            }

            val v = generator.invoke(context, parent, viewType)

            return EmptyViewHolder(v)
        }

        override fun getItemCount(): Int {
            return if ((actualAdapter == null || actualAdapter?.itemCount == 0)
                    && emptyViewGenerator != null) 1 else 0
        }

        override fun onBindViewHolder(holder: EmptyViewHolder, position: Int) {
            emptyViewHolderBinder(holder)
        }

    }

    var actualAdapter: Adapter<*>? = null

    override fun setAdapter(adapter: Adapter<*>?) {
        actualAdapter = adapter

        adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                emptyAdapter.notifyDataSetChanged()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                emptyAdapter.notifyDataSetChanged()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                emptyAdapter.notifyDataSetChanged()
            }
        })

        super.setAdapter(ConcatAdapter(emptyAdapter, adapter))
    }

    public var emptyViewHolderBinder: (EmptyViewHolder) -> Unit = {}

    public var emptyViewGenerator: ((Context, ViewGroup, Int) -> View)? = null
        set(value) {
            field = value
            emptyAdapter.notifyDataSetChanged()
        }

    public fun setEmptyViewId(@LayoutRes viewId: Int) {
        emptyViewGenerator = { context: Context, viewGroup: ViewGroup, _: Int ->
            LayoutInflater.from(context).inflate(viewId, viewGroup)
        }
    }

    inner class EmptyViewHolder(view: View) : ViewHolder(view)

}