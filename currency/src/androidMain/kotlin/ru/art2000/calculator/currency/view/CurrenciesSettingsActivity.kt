package ru.art2000.calculator.currency.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.SearchView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.art2000.calculator.currency.vm.CurrenciesSettingsModel
import ru.art2000.extensions.activities.consumeInsetsForMargin
import ru.art2000.extensions.arch.launchRepeatOnStarted
import ru.art2000.extensions.collections.LiveList.LiveListObserver
import ru.art2000.extensions.fragments.*
import ru.art2000.extensions.views.createOnTabSelectedListener
import ru.art2000.calculator.currency.preferences.CurrencyPreferenceHelper
import ru.art2000.calculator.common.view.AppActivity
import ru.art2000.calculator.common.view.createThemedSnackbar
import ru.art2000.calculator.currency.R
import ru.art2000.calculator.currency.databinding.ActivityCurrenciesEditorBinding
import ru.art2000.calculator.currency.model.CurrencyItem
import ru.art2000.extensions.kt.launchAndCollect
import ru.art2000.extensions.kt.writeAndUpdateUi
import ru.art2000.extensions.layout.isLtr
import ru.art2000.extensions.views.MyFragmentStateAdapter
import javax.inject.Inject

@AndroidEntryPoint
internal class CurrenciesSettingsActivity : AppActivity() {

    private var deselect: MenuItem? = null
    private var select: MenuItem? = null

    @DrawableRes
    private val checkDrawable = R.drawable.ic_currencies_done

    @DrawableRes
    private val deleteDrawable = ru.art2000.calculator.common.R.drawable.ic_delete

    @DrawableRes
    private var currentDrawable = checkDrawable

    private var deleteTooltip: Snackbar? = null

    private val binding by viewBinding<ActivityCurrenciesEditorBinding>(CreateMethod.INFLATE)
    private val model: CurrenciesSettingsModel by viewModels()

    @Inject
    lateinit var prefsHelper: CurrencyPreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        model.observeAndUpdateDisplayedItems(this)

        launchRepeatOnStarted {
            launchAndCollect(model.removedItems) { generateUndoSnackBar(it, false) }
            launchAndCollect(model.liveIsFirstTimeTooltipShown) { deleteTooltip?.dismiss() }
        }

        val ltr = isLtr

        binding.floatingActionButton.apply {

            addOnShowAnimationListener(object : AnimatorListenerAdapter() {

                override fun onAnimationStart(animator: Animator) {
                    binding.searchViewLayout.updateLayoutParams<MarginLayoutParams> {
                        if (ltr) {
                            rightMargin = binding.coordinator.right -
                                    binding.floatingActionButton.x.toInt()
                        } else {
                            leftMargin = binding.floatingActionButton.right
                        }
                    }
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
                    } else {
                        binding.searchViewLayout.updateLayoutParams<MarginLayoutParams> {
                            if (ltr) {
                                rightMargin = 0
                            } else {
                                leftMargin = 0
                            }
                        }
                    }
                }
            })
        }

        val pages = listOf(
            NamedReplaceableFragmentCreator(
                R.string.currencies_add,
                ::CurrenciesAddFragment
            ),
            NamedReplaceableFragmentCreator(
                R.string.currencies_edit,
                ::CurrenciesEditFragment
            ),
        )
        val pager2Adapter = CurrencyEditorPager2Adapter(pages)
        binding.pager2.apply {

            offscreenPageLimit = 2

            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {

                    val maxScroll = measuredWidth
                    val currentScroll = maxScroll * position + positionOffsetPixels
                    binding.searchViewLayout.translationX = currentScroll * if (ltr) -1f else 1f
                    deleteTooltip?.view?.translationX = maxScroll - currentScroll.toFloat()
                }
            })

            adapter = pager2Adapter

            TabLayoutMediator(binding.tabs, this) { tab, position ->
                tab.text = pages[position].getTitle(this@CurrenciesSettingsActivity)
            }.attach()
        }

        binding.tabs.addOnTabSelectedListener(pager2Adapter.createOnTabSelectedListener())
        binding.tabs.addOnTabSelectedListener(object : OnTabSelectedListener {


            override fun onTabSelected(tab: TabLayout.Tab) {
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
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    override val clearStatusBar: Boolean
        get() = false

    override val insetAsPadding: Boolean
        get() = false

    override val topViews: List<View>
        get() = listOf(binding.root)

    override val bottomViews: List<View>
        get() = listOf(binding.searchViewLayout, binding.floatingActionButton)

    override val leftViews: List<View>
        get() = listOf(binding.root)

    override val rightViews: List<View>
        get() = listOf(binding.root)

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        model.selectedTab = binding.tabs.selectedTabPosition
        modifyVisualElements(binding.tabs.selectedTabPosition)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        binding.toolbar.inflateMenu(R.menu.currencies_editor_menu)

        select = menu.findItem(R.id.select_all)
        deselect = menu.findItem(R.id.deselect_all)

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

        val liveListObserver = object : LiveListObserver<CurrencyItem>() {
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
                model.isEditSelectionMode = false
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

    private fun showDeleteTip() {
        if (!model.liveIsFirstTimeTooltipShown.value) return

        deleteTooltip?.also {
            if (!it.isShown) it.show()
            return
        }

        deleteTooltip = binding.coordinator.createThemedSnackbar(
            R.string.tooltip_remove_currency, Snackbar.LENGTH_INDEFINITE
        ).fixedSizes.apply {

            addCallback(object : Snackbar.Callback() {
                override fun onShown(sb: Snackbar) {
                    prefsHelper.setDeleteTooltipShown()
                }

                override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
                    if (event == DISMISS_EVENT_ACTION
                        || event == DISMISS_EVENT_SWIPE
                    ) {
                        model.liveIsFirstTimeTooltipShown.value = false
                    }
                }
            })

            setAction(R.string.action_tooltip_got_it) { }
            show()
        }
    }

    private fun toggleElementsVisibility() {
        if (model.selectedTab == 0) {
            val selectedCount = model.selectedHiddenItems.size
            val displayedCount = model.displayedHiddenItems.size
            if (selectedCount > 0) {
                binding.floatingActionButton.show()
            } else {
                binding.floatingActionButton.hide()
            }
            select?.isVisible = selectedCount < displayedCount
            deselect?.isVisible = selectedCount > 0

            val totalCount = model.hiddenItems.value.size
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

    private val Snackbar.fixedSizes: Snackbar
        get() {
            val cardView = findViewById<MaterialCardView>(R.id.card_wrapper)
            val cardLayoutParams = cardView.layoutParams as MarginLayoutParams
            var leftMargin = cardLayoutParams.leftMargin + cardView.paddingLeft
            var rightMargin = cardLayoutParams.rightMargin + cardView.paddingRight
            var bottomMargin = cardLayoutParams.bottomMargin + cardView.paddingBottom
            var height = cardView.getChildAt(0).measuredHeight
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                height += (cardView.paddingBottom
                        + cardView.strokeWidth
                        - cardView.cardElevation.toInt())
                leftMargin -= (cardView.cardElevation + cardView.strokeWidth).toInt()
                rightMargin -= (cardView.cardElevation + cardView.strokeWidth).toInt()
                bottomMargin -= (cardView.cardElevation + cardView.paddingBottom - cardLayoutParams.topMargin - cardView.strokeWidth).toInt()
            } else {
                view.elevation = 0f
            }
            view.minimumHeight = height

            view.updateLayoutParams<MarginLayoutParams> {
                updateMargins(
                    left = leftMargin,
                    right = rightMargin,
                    bottom = bottomMargin,
                )
            }

            view.consumeInsetsForMargin { windowInsetsCompat, _, _, _, bottom ->
                updateLayoutParams<MarginLayoutParams> {
                    val systemInsets = windowInsetsCompat.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                    )
                    updateMargins(bottom = bottom + systemInsets.bottom)
                }
            }

            return this
        }

    private fun generateUndoSnackBar(editedItems: List<CurrencyItem>, added: Boolean) {
        if (editedItems.isEmpty()) return

        val message = if (editedItems.size == 1) {
            getString(
                if (added) R.string.message_item_shown else R.string.message_item_hidden,
                editedItems[0].code
            )
        } else {
            resources.getQuantityString(
                if (added) R.plurals.message_items_shown else R.plurals.message_items_hidden,
                editedItems.size,
                editedItems.size
            )
        }

        val undoSnackBar = binding.coordinator
            .createThemedSnackbar(message, Snackbar.LENGTH_LONG).fixedSizes

        undoSnackBar.setAction(R.string.action_undo) {

            writeAndUpdateUi(
                compute = {
                    if (added) {
                        model.makeItemsHidden(editedItems)
                    } else {
                        model.makeItemsVisible(editedItems)
                    }
                },
                update = { generateUndoSnackBar(editedItems, !added) }
            )
        }
        undoSnackBar.show()
    }

    private fun modifyVisualElements(tabPos: Int) {
        if (tabPos == 0) {
            setNewFabImage(checkDrawable)
            binding.floatingActionButton.setOnClickListener {
                val selectedItems = ArrayList(model.selectedHiddenItems)
                writeAndUpdateUi(
                    compute = { model.makeItemsVisible(selectedItems) },
                    update = { generateUndoSnackBar(selectedItems, true) }
                )
            }
        } else {
            setNewFabImage(deleteDrawable)
            binding.floatingActionButton.setOnClickListener {
                val selectedItems = ArrayList(model.selectedVisibleItems)
                writeAndUpdateUi(
                    compute = { model.makeItemsHidden(selectedItems) },
                    update = { generateUndoSnackBar(selectedItems, false) }
                )
            }
        }
    }

    private inner class CurrencyEditorPager2Adapter(
        private val creators: List<INamedReplaceableCreator<CommonReplaceableFragment>>,
    ) : MyFragmentStateAdapter<CommonReplaceableFragment>(this@CurrenciesSettingsActivity) {

        override fun getItemCount(): Int = creators.size

        override fun createFragment(position: Int) = creators[position].createReplaceable()
    }
}