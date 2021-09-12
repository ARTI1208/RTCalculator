package ru.art2000.extensions.fragments

interface IBackPressAware {

    /**
     * Called from onBackPressed of activity. Returns `true` if processing of event should be
     * handled by parent, `false` otherwise
     */
    fun onBackPressed(): Boolean = true

}