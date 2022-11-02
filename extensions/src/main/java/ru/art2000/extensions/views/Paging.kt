package ru.art2000.extensions.views

import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import ru.art2000.extensions.fragments.IReplaceableFragment

@Suppress("unused")
fun <F> MyFragmentStateAdapter<F>.createOnPageChangeCallback()
        where F : Fragment, F : IReplaceableFragment = object : OnPageChangeCallback() {

    var previousPage = -1

    override fun onPageSelected(position: Int) {
        if (previousPage != position) {
            val previous = if (previousPage >= 0) getFragment(previousPage) else null
            replace(position, previous)
        }
    }
}

fun <F> MyFragmentStateAdapter<F>.createOnTabSelectedListener()
        where F : Fragment, F : IReplaceableFragment = object : TabLayout.OnTabSelectedListener {

    var previousPage = -1

    override fun onTabSelected(tab: TabLayout.Tab) {
        val previous = if (previousPage >= 0) getFragment(previousPage) else null
        replace(tab.position, previous)
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {

    }

    override fun onTabReselected(tab: TabLayout.Tab) {
        onFragment(tab.position) {
            onReselected()
        }
    }

}

private fun <F> MyFragmentStateAdapter<F>.replace(
    position: Int, previous: F?
) where F : Fragment, F : IReplaceableFragment {
    onFragment(position) {
        onReplace(previous)
        previous?.onReplaced(this)
    }
}

private fun <F> MyFragmentStateAdapter<F>.onFragment(
    position: Int, consumer: F.() -> Unit
) where F : Fragment, F : IReplaceableFragment {
    val fragment = getFragment(position)
    if (fragment != null) {
        fragment.consumer()
        return
    }

    addOnCreateOrRestoreListener(object : MyFragmentStateAdapter.OnCreateOrRestoreListener<F> {
        override fun onFragment(position: Int, fragment: F) {
            removeOnCreateOrRestoreListener(this)
            fragment.consumer()
        }
    })
}