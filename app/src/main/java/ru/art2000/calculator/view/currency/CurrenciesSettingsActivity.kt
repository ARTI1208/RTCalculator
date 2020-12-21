package ru.art2000.calculator.view.currency

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup.*
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.ActivityCurrenciesEditorBinding
import ru.art2000.calculator.model.currency.CurrencyItem
import ru.art2000.calculator.view_model.currency.CurrenciesSettingsModel
import ru.art2000.extensions.AutoThemeActivity
import ru.art2000.extensions.LiveList.LiveListObserver
import ru.art2000.extensions.ReplaceableFragment
import ru.art2000.extensions.createThemedSnackbar
import ru.art2000.helpers.AndroidHelper
import ru.art2000.helpers.PrefsHelper
import java.util.*
import kotlin.collections.ArrayList

class CurrenciesSettingsActivity : AutoThemeActivity() {

    private lateinit var add: CurrenciesAddFragment
    private lateinit var edit: CurrenciesEditFragment

    private var deselect: MenuItem? = null
    private var select: MenuItem? = null

    @DrawableRes
    private val checkDrawable = R.drawable.ic_currencies_done

    @DrawableRes
    private val deleteDrawable = R.drawable.ic_clear_history

    @DrawableRes
    private var currentDrawable = checkDrawable

    private var deleteTooltip: Snackbar? = null

    lateinit var binding: ActivityCurrenciesEditorBinding
    val model: CurrenciesSettingsModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        super.onCreate(savedInstanceState)

        binding = ActivityCurrenciesEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        model.removedItems.observe(this) {
            generateUndoSnackbar(it, false)
        }

        model.liveIsFirstTimeTooltipShown.observe(this) {
            deleteTooltip?.dismiss()
        }

        binding.floatingActionButton.apply {

            addOnShowAnimationListener(object : AnimatorListenerAdapter() {

                override fun onAnimationStart(animator: Animator) {
                    setImageResource(currentDrawable)
                }

                override fun onAnimationCancel(animator: Animator) {
                    setImageResource(currentDrawable)
                }
            })

            addOnHideAnimationListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animator: Animator) {
                    if (currentDrawable == deleteDrawable) {
                        showDeleteTip()
                    }
                }
            })
        }

        // check if fragment manager already contains fragments or create new ones
        retrieveFragments()
        binding.pager2.apply {

            offscreenPageLimit = 2

            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                    val maxScroll = measuredWidth
                    val currentScroll = maxScroll * position + positionOffsetPixels
                    binding.searchViewLayout.translationX = -currentScroll.toFloat()
                    deleteTooltip?.view?.translationX = maxScroll - currentScroll.toFloat()
                }
            })

            val pager2Adapter = CurrencyEditorPager2Adapter()
            adapter = pager2Adapter

            TabLayoutMediator(binding.tabs, this) { tab, position ->
                tab.text = pager2Adapter.getPageTitle(position)
            }.attach()
        }

        binding.tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (model.selectedTab) {
                    1 -> {
                        edit.onReplaced(add)
                        add.onReplace(edit)
                    }
                    else -> {
                        add.onReplaced(edit)
                        edit.onReplace(add)
                    }
                }
                model.selectedTab = tab.position
                binding.floatingActionButton.hide()
                modifyVisualElements(tab.position)

                if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
                    toggleElementsVisibility()
                }
                if (tab.position == 1 && !binding.floatingActionButton.isShown) {
                    showDeleteTip()
                } else if (tab.position == 0) {
                    deleteTooltip?.dismiss()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {
                (if (tab.position == 1) edit else add).onReselected()
            }
        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        binding.searchViewLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        model.recyclerViewBottomPadding.value = binding.searchViewLayout.measuredHeight

        binding.floatingActionButton.updateLayoutParams<CoordinatorLayout.LayoutParams> {
            bottomMargin = binding.searchViewLayout.measuredHeight
        }

        model.selectedTab = binding.tabs.selectedTabPosition
        modifyVisualElements(binding.tabs.selectedTabPosition)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.currencies_editor_menu, menu)

        select = menu.findItem(R.id.select_all)
        deselect = menu.findItem(R.id.deselect_all)

        applyMenuIconTint(select?.icon)
        applyMenuIconTint(deselect?.icon)

        toggleElementsVisibility()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                model.liveQuery.value = newText
                model.filterHiddenItems(newText)
                return true
            }
        })

        val liveListObserver: LiveListObserver<CurrencyItem> = object : LiveListObserver<CurrencyItem>() {
            override fun onAnyChanged(previousList: List<CurrencyItem>) {
                toggleElementsVisibility()
            }
        }

        model.selectedHiddenItems.observe(this, liveListObserver)
        model.selectedVisibleItems.observe(this, liveListObserver)
        model.displayedHiddenItems.observe(this, liveListObserver)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deselect_all -> if (model.selectedTab == 0) {
                model.selectedHiddenItems.clear()
            } else {
                model.selectedVisibleItems.clear()
            }
            R.id.select_all -> if (model.selectedTab == 0) {
                model.completeSelectedHiddenItems()
            } else {
                model.completeSelectedVisibleItems()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    fun showDeleteTip() {
        if (!model.liveIsFirstTimeTooltipShown.value!!) {
            return
        }

        deleteTooltip?.also {
            if (!it.isShown) it.show()
            return
        }

        deleteTooltip = binding.coordinator.createThemedSnackbar(
                R.string.tooltip_remove_currency, Snackbar.LENGTH_INDEFINITE
        ).apply {

            addCallback(object : Snackbar.Callback() {
                override fun onShown(sb: Snackbar) {
                    PrefsHelper.setDeleteTooltipShown()
                }

                override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
                    if (event == DISMISS_EVENT_ACTION
                            || event == DISMISS_EVENT_SWIPE) {
                        model.liveIsFirstTimeTooltipShown.value = false
                    }
                }
            })

            setAction(R.string.action_tooltip_got_it) { }
            show()
        }
    }

    private fun retrieveFragments() {
        supportFragmentManager.fragments.forEach {
            when (it) {
                is CurrenciesAddFragment -> add = it
                is CurrenciesEditFragment -> edit = it
            }
        }

        if (!this::add.isInitialized) add = CurrenciesAddFragment()
        if (!this::edit.isInitialized) edit = CurrenciesEditFragment()
    }

    private fun applyMenuIconTint(icon: Drawable?) {
        icon?.colorFilter = PorterDuffColorFilter(
                AndroidHelper.getColorAttribute(this, R.attr.colorAccent),
                PorterDuff.Mode.SRC_ATOP)
    }

    fun toggleElementsVisibility() {
        if (model.selectedTab == 0) {
            val selectedCount = model.selectedHiddenItems.size
            val totalCount = model.displayedHiddenItems.size
            if (selectedCount > 0) {
                binding.floatingActionButton.show()
            } else {
                binding.floatingActionButton.hide()
            }
            select?.isVisible = selectedCount < totalCount
            deselect?.isVisible = selectedCount > 0
            binding.searchViewLayout.visibility = if (totalCount > 0) View.VISIBLE else View.GONE
        } else {
            val selectedCount = model.selectedVisibleItems.size
            val totalCount = model.displayedVisibleItems.size

            if (model.isEditSelectionMode && selectedCount > 0) {
                binding.floatingActionButton.show()
            } else {
                binding.floatingActionButton.hide()
            }
            select?.isVisible = model.isEditSelectionMode && selectedCount < totalCount
            deselect?.isVisible = model.isEditSelectionMode && selectedCount > 0
        }
    }

    private fun setNewFabImage(resId: Int) {
        currentDrawable = resId
        binding.floatingActionButton.hide()
    }

    private fun generateUndoSnackbar(editedItems: List<CurrencyItem>, added: Boolean) {
        if (editedItems.isEmpty()) return

        val message = if (editedItems.size == 1) {
            getString(
                    if (added) R.string.message_item_shown else R.string.message_item_hidden,
                    editedItems[0].code)
        } else {
            resources.getQuantityString(
                    if (added) R.plurals.message_items_shown else R.plurals.message_items_hidden,
                    editedItems.size,
                    editedItems.size)
        }
        val undoSnackbar = binding.coordinator.createThemedSnackbar(message, Snackbar.LENGTH_LONG)
        undoSnackbar.setAction(R.string.action_undo) {
            Completable
                    .fromRunnable {
                        if (added) {
                            model.makeItemsHidden(editedItems)
                        } else {
                            model.makeItemsVisible(editedItems)
                        }
                    }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete { generateUndoSnackbar(editedItems, !added) }.subscribe()
        }
        undoSnackbar.show()
    }

    fun modifyVisualElements(tabPos: Int) {
        if (tabPos == 0) {
            setNewFabImage(checkDrawable)
            binding.floatingActionButton.setOnClickListener {
                val selectedItems = ArrayList(model.selectedHiddenItems)
                Completable
                        .fromRunnable {
                            model.makeItemsVisible(selectedItems)
                        }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete { generateUndoSnackbar(selectedItems, true) }
                        .subscribe()
            }
        } else {
            setNewFabImage(deleteDrawable)
            binding.floatingActionButton.setOnClickListener {
                val selectedItems = ArrayList(model.selectedVisibleItems)
                Completable
                        .fromRunnable {
                            model.makeItemsHidden(selectedItems)
                        }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete { generateUndoSnackbar(selectedItems, false) }
                        .subscribe()
            }
        }
    }

    internal inner class CurrencyEditorPager2Adapter : FragmentStateAdapter(this@CurrenciesSettingsActivity) {

        private val fragments: Array<ReplaceableFragment> = arrayOf(add, edit)

        override fun getItemCount(): Int {
            return fragments.size
        }

        fun getPageTitle(position: Int): CharSequence? {
            return fragments[position].getTitle(this@CurrenciesSettingsActivity)
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }
}