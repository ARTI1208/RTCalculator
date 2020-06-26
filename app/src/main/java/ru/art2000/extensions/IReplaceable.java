package ru.art2000.extensions;

import androidx.annotation.Nullable;

/**
 * Interface, showing that object can be treated as replaceable(changeable) with other objects,
 * implementing this interface
 */
public interface IReplaceable {
    /**
     * Called when current object becomes primary in the collection. Object may not be fully
     * initialized by this time, as in {@link ReplaceableFragment}, where you must override
     * {@link ReplaceableFragment#onShown(IReplaceable)} to be sure fragment view was already created
     *
     * @param previousReplaceable object that was previously shown or whatever
     * @see ReplaceableFragment
     * @see PreferenceNavigationFragment
     */
    void onReplace(@Nullable IReplaceable previousReplaceable);

    /**
     * Called when current object becomes secondary in collection
     *
     * @param nextReplaceable object that has replaced current
     */
    void onReplaced(@Nullable IReplaceable nextReplaceable);


    /**
     * Called when current object is reselected as primary
     *
     */
    void onReselected();
}
