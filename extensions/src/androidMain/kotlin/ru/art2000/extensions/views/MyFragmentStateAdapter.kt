package ru.art2000.extensions.views

import android.os.Parcelable
import android.view.ViewGroup
import androidx.collection.LongSparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.adapter.StatefulAdapter
import java.lang.reflect.Field

abstract class MyFragmentStateAdapter<T : Fragment>(
    private val fragmentManager: FragmentManager,
    private val lifecycle: Lifecycle,
) : RecyclerView.Adapter<FragmentViewHolder>(), StatefulAdapter {

    private val stateAdapter = object : FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int {
            return this@MyFragmentStateAdapter.itemCount
        }

        override fun createFragment(position: Int): Fragment {
            return this@MyFragmentStateAdapter.createFragmentInternal(position)
        }

        override fun getItemId(position: Int): Long {
            return this@MyFragmentStateAdapter.getItemId(position)
        }

        override fun containsItem(itemId: Long): Boolean {
            return this@MyFragmentStateAdapter.containsItem(itemId)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val fragments: LongSparseArray<T> by lazy {
        stateAdapter.mFragments as LongSparseArray<T>
    }

    constructor(fragmentActivity: FragmentActivity) : this(
        fragmentActivity.supportFragmentManager,
        fragmentActivity.lifecycle
    )

    constructor(fragment: Fragment) : this(fragment.childFragmentManager, fragment.lifecycle)

    init {
        super.setHasStableIds(true)
    }

    abstract fun createFragment(position: Int): T

    private fun createFragmentInternal(position: Int): T {
        val fragment = createFragment(position)
        onFragmentFetched(position, fragment)
        return fragment
    }

    private var shouldRestore = true

    fun clearState() {
        shouldRestore = false
    }

    private val onCreateOrRestoreListeners = mutableListOf<OnCreateOrRestoreListener<T>>()

    fun addOnCreateOrRestoreListener(listener: OnCreateOrRestoreListener<T>) {
        if (listener in onCreateOrRestoreListeners) return

        onCreateOrRestoreListeners += listener
    }

    fun removeOnCreateOrRestoreListener(listener: OnCreateOrRestoreListener<T>) {
        onCreateOrRestoreListeners -= listener
    }

    fun getFragment(position: Int): T? = fragments[getItemId(position)]

    private fun onFragmentFetched(position: Int, fragment: T) {
        for (i in onCreateOrRestoreListeners.indices.reversed()) {
            onCreateOrRestoreListeners[i].onFragment(position, fragment)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FragmentViewHolder {
        return stateAdapter.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: FragmentViewHolder, position: Int) {
        stateAdapter.onBindViewHolder(holder, position)
    }

    override fun saveState(): Parcelable {
        return stateAdapter.saveState()
    }

    override fun restoreState(savedState: Parcelable) {
        if (!shouldRestore) {
            shouldRestore = true
            return
        }

        stateAdapter.apply {
            restoreState(savedState)
            for (index in 0 until itemCount) {
                getFragment(index)?.also { onFragmentFetched(index, it) }
            }
        }
    }

    override fun onBindViewHolder(
        holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>
    ) = onBindViewHolder(holder, position)

    override fun findRelativeAdapterPositionIn(
        adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
        viewHolder: RecyclerView.ViewHolder,
        localPosition: Int
    ) = stateAdapter.findRelativeAdapterPositionIn(adapter, viewHolder, localPosition)

    override fun getItemViewType(position: Int) = stateAdapter.getItemViewType(position)

    override fun setHasStableIds(hasStableIds: Boolean) = stateAdapter.setHasStableIds(hasStableIds)

    override fun getItemId(position: Int) = position.toLong()

    open fun containsItem(itemId: Long) = itemId in 0 until itemCount

    override fun onViewRecycled(holder: FragmentViewHolder) = stateAdapter.onViewRecycled(holder)

    override fun onFailedToRecycleView(holder: FragmentViewHolder) =
        stateAdapter.onFailedToRecycleView(holder)

    override fun onViewAttachedToWindow(holder: FragmentViewHolder) =
        stateAdapter.onViewAttachedToWindow(holder)

    override fun onViewDetachedFromWindow(holder: FragmentViewHolder) =
        stateAdapter.onViewDetachedFromWindow(holder)

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) =
        stateAdapter.registerAdapterDataObserver(observer)

    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) =
        stateAdapter.unregisterAdapterDataObserver(observer)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) =
        stateAdapter.onAttachedToRecyclerView(recyclerView)

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) =
        stateAdapter.onDetachedFromRecyclerView(recyclerView)

    override fun setStateRestorationPolicy(strategy: StateRestorationPolicy) {
        stateAdapter.stateRestorationPolicy = strategy
    }

    fun interface OnCreateOrRestoreListener<T> {

        fun onFragment(position: Int, fragment: T)
    }

    private companion object {

        @Suppress("UNCHECKED_CAST")
        val FragmentStateAdapter.mFragments: LongSparseArray<Fragment>
            get() = mFragmentsField.get(this) as LongSparseArray<Fragment>

        private val mFragmentsField: Field by lazy {
            FragmentStateAdapter::class.java.getDeclaredField("mFragments").apply {
                isAccessible = true
            }
        }
    }
}