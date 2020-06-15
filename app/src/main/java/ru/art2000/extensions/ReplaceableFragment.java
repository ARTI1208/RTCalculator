package ru.art2000.extensions;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

public abstract class ReplaceableFragment extends Fragment implements INavigationFragment {

    private IReplaceable previousReplaceable;
    private boolean callOnShownOnViewCreated;

    private boolean canBeShown() {
        return getView() != null;
    }

    /**
     * Called when current fragment becomes primary in the collection and shown
     *
     * @param previousReplaceable object that was previously shown or whatever
     */
    protected void onShown(@Nullable IReplaceable previousReplaceable) {

    }

    /**
     * Called when current fragment becomes primary in the collection. Fragment may not be fully
     * initialized by this time, so override this only if you don't bother that view may not be created,
     * otherwise use {@link ReplaceableFragment#onShown(IReplaceable)}
     * to be sure fragment view was already created
     *
     * @param previousReplaceable object that was previously shown or whatever
     */
    @Override
    @CallSuper
    public void onReplace(@Nullable IReplaceable previousReplaceable) {
        Log.d("onReplace", String.valueOf(canBeShown()));
        if (canBeShown()) {
            onShown(previousReplaceable);
        } else {
            this.previousReplaceable = previousReplaceable;
            callOnShownOnViewCreated = true;
        }
    }

    /**
     * Called when current fragment becomes secondary in the collection
     *
     * @param nextReplaceable object that has replaced current
     */
    @Override
    public void onReplaced(@Nullable IReplaceable nextReplaceable) {

    }

    @Override
    @CallSuper
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("onCreated", String.valueOf(callOnShownOnViewCreated));
        if (callOnShownOnViewCreated) {
            onShown(previousReplaceable);
            previousReplaceable = null;
            callOnShownOnViewCreated = false;
        }
    }

    @StringRes
    public abstract int getTitle();

    public abstract int getOrder();
}
