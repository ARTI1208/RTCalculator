package ru.art2000.extensions.views

import android.os.Build
import android.util.SparseArray
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationBarView
import ru.art2000.extensions.fragments.INavigationFragment
import ru.art2000.extensions.fragments.IReplaceableFragment
import kotlin.reflect.KProperty

fun NavigationBarView.setSelectedItemId(@IdRes itemId: Int, smoothScroll: Boolean) {
    smoothScrollNext = smoothScroll
    selectedItemId = itemId
    smoothScrollNext = true
}

fun NavigationBarView.setupWithViewPager2(
    parentActivity: FragmentActivity,
    pager2: ViewPager2,
    vararg replaceableFragments: INavigationFragment,
) {
    attachedPager2 = pager2
    setReplaceableFragments(*replaceableFragments)
    pager2.offscreenPageLimit = replaceableFragments.size
    pager2.isUserInputEnabled = false
    pager2.adapter = object : FragmentStateAdapter(parentActivity) {
        override fun createFragment(position: Int): Fragment {
            return replaceableFragments[position] as Fragment
        }

        override fun getItemCount(): Int {
            return replaceableFragments.size
        }
    }
    parentActivity.lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            owner.lifecycle.removeObserver(this)
            clearMemory()
        }
    })

    if (selectedListenerSet != true) {
        setMyOnItemSelectedListener { true }
    }
}

fun NavigationBarView.setMyOnItemSelectedListener(
    listener: NavigationBarView.OnItemSelectedListener?,
) {
    selectedListenerSet = true
    setOnItemSelectedListener {
        if (listener == null) return@setOnItemSelectedListener false
        val value = listener.onNavigationItemSelected(it)
        onNavigationItemSelected(it)
        firstReplaceDone = true
        value
    }
}

@Suppress("unused")
fun NavigationBarView.setMyOnItemReselectedListener(
    listener: NavigationBarView.OnItemReselectedListener?,
) = setOnItemReselectedListener {
    onNavigationItemReselected(it)
    if (firstReplaceDone == true) {
        listener?.onNavigationItemReselected(it)
    }
}

private val replaceablesMap = hashMapOf<NavigationBarView, SparseArray<IReplaceableFragment>>()
private val currentReplaceableMap = hashMapOf<NavigationBarView, IReplaceableFragment?>()
private val firstReplaceDoneMap = hashMapOf<NavigationBarView, Boolean>()
private val pagerMap = hashMapOf<NavigationBarView, ViewPager2?>()
private val smoothScrollNextMap = hashMapOf<NavigationBarView, Boolean>()
private val selectedListenerSetMap = hashMapOf<NavigationBarView, Boolean>()

private val NavigationBarView.replaceables
    get() = replaceablesMap.getOrPut(this) { SparseArray<IReplaceableFragment>() }

private var NavigationBarView.currentReplaceable by currentReplaceableMap

private var NavigationBarView.attachedPager2 by pagerMap

private var NavigationBarView.firstReplaceDone by firstReplaceDoneMap

private var NavigationBarView.smoothScrollNext by smoothScrollNextMap

private var NavigationBarView.selectedListenerSet by selectedListenerSetMap

private fun NavigationBarView.getReplaceable(position: Int): IReplaceableFragment? {
    return replaceablesMap[this]?.get(position)
}

private fun NavigationBarView.clearMemory() {
    replaceablesMap.remove(this)
    currentReplaceableMap.remove(this)
    firstReplaceDoneMap.remove(this)
    pagerMap.remove(this)
    smoothScrollNextMap.remove(this)
    selectedListenerSetMap.remove(this)
}

private fun NavigationBarView.setReplaceableFragments(
    vararg replaceableFragments: INavigationFragment
) {
    menu.clear()
    for (order in replaceableFragments.indices) {
        val replaceableFragment = replaceableFragments[order]
        var id = replaceableFragment.getReplaceableId()
        id = if (id == -1) Menu.NONE else id
        val item = menu.add(
            Menu.NONE,
            id,
            order,
            replaceableFragment.getTitle()
        )
        val iconRes = replaceableFragment.getIcon()
        if (iconRes != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                item.setIcon(iconRes)
            } else {
                val iconDrawable = ResourcesCompat.getDrawable(
                    context.resources, iconRes, context.theme
                )
                item.icon = iconDrawable
            }
        }
        replaceables.put(order, replaceableFragment)
    }
}

private fun NavigationBarView.onNavigationItemSelected(item: MenuItem) {
    val replaceable = getReplaceable(item.order) ?: return
    sendReplaceCallback(replaceable)
    attachedPager2?.setCurrentItem(item.order, smoothScrollNext ?: false)
}

private fun NavigationBarView.onNavigationItemReselected(item: MenuItem) {
    if (firstReplaceDone != true) {
        onNavigationItemSelected(item)
        firstReplaceDone = true
    } else {
        currentReplaceable?.onReselected()
    }
}

private fun NavigationBarView.sendReplaceCallback(replaceable: IReplaceableFragment?) {
    if (replaceable != null) {
        if (replaceable === currentReplaceable) {
            replaceable.onReselected()
            return
        }
        replaceable.onReplace(currentReplaceable)
    }
    currentReplaceable?.onReplaced(replaceable)
    currentReplaceable = replaceable
}

private operator fun <K, V> MutableMap<K, V>.setValue(k: K, property: KProperty<*>, v: V?) {
    if (v != null) this[k] = v
}

private operator fun <K, V> Map<K, V>.getValue(k: K, property: KProperty<*>): V? {
    return this[k]
}
