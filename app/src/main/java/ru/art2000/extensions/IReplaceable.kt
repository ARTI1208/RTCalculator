package ru.art2000.extensions

/**
 * Interface, showing that object can be treated as replaceable(changeable) with other objects,
 * implementing this interface
 */
interface IReplaceable<T : IReplaceable<T>> {
    /**
     * Called when current object becomes primary in the collection. Object may not be fully
     * initialized by this time, as in [CommonReplaceableFragment], where you must override
     * [CommonReplaceableFragment.onShown] to be sure fragment view was already created
     *
     * @param previousReplaceable object that was previously shown or whatever
     * @see CommonReplaceableFragment
     *
     * @see PreferenceNavigationFragment
     */
    fun onReplace(previousReplaceable: T?)

    /**
     * Called when current object becomes secondary in collection
     *
     * @param nextReplaceable object that has replaced current
     */
    fun onReplaced(nextReplaceable: T?)

    /**
     * Called when current object is reselected as primary
     *
     */
    fun onReselected()
}