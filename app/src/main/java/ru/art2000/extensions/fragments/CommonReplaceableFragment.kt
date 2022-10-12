package ru.art2000.extensions.fragments

import androidx.annotation.CallSuper
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class CommonReplaceableFragment : Fragment(), IReplaceableFragment {

    private var previousReplaceable: IReplaceableFragment? = null
    private var callOnShownOnViewCreated = false

    private fun canBeShown(): Boolean {
        return view != null
    }

    override fun onShown(previousReplaceable: IReplaceableFragment?) {}

    /**
     * Called when current fragment becomes primary in the collection. Fragment may not be fully
     * initialized by this time, so override this only if you don't bother that view may not be created,
     * otherwise use [CommonReplaceableFragment.onShown]
     * to be sure fragment view was already created
     *
     * @param previousReplaceable object that was previously shown or whatever
     */
    @CallSuper
    override fun onReplace(previousReplaceable: IReplaceableFragment?) {
        if (canBeShown()) {
            onShown(previousReplaceable)
        } else {
            this.previousReplaceable = previousReplaceable
            callOnShownOnViewCreated = true
        }
    }

    /**
     * Called when current fragment becomes secondary in the collection
     *
     * @param nextReplaceable object that has replaced current
     */
    override fun onReplaced(nextReplaceable: IReplaceableFragment?) {}

    override fun onReselected() {}

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (callOnShownOnViewCreated) {
            onShown(previousReplaceable)
            previousReplaceable = null
            callOnShownOnViewCreated = false
        }
    }
}