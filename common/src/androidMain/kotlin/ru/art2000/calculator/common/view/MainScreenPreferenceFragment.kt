package ru.art2000.calculator.common.view

import android.content.Context
import android.os.Bundle
import android.view.View
import ru.art2000.extensions.fragments.PreferenceReplaceableFragment

abstract class MainScreenPreferenceFragment : PreferenceReplaceableFragment(), AppFragmentMixin {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateViewOnCreated(view)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onAttachedToWindow(requireActivity().window)
    }

}