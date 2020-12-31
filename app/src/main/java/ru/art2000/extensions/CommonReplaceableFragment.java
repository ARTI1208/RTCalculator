package ru.art2000.extensions;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class CommonReplaceableFragment extends Fragment implements IReplaceableFragment {

    private IReplaceableFragment previousReplaceable;
    private boolean callOnShownOnViewCreated;

    private boolean canBeShown() {
        return getView() != null;
    }

    @Override
    public void onShown(@Nullable IReplaceableFragment previousReplaceable) {

    }

    /**
     * Called when current fragment becomes primary in the collection. Fragment may not be fully
     * initialized by this time, so override this only if you don't bother that view may not be created,
     * otherwise use {@link CommonReplaceableFragment#onShown(IReplaceableFragment)}
     * to be sure fragment view was already created
     *
     * @param previousReplaceable object that was previously shown or whatever
     */
    @Override
    @CallSuper
    public void onReplace(@Nullable IReplaceableFragment previousReplaceable) {
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
    public void onReplaced(@Nullable IReplaceableFragment nextReplaceable) {

    }

    @Override
    public void onReselected() {

    }

    @Override
    @CallSuper
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (callOnShownOnViewCreated) {
            onShown(previousReplaceable);
            previousReplaceable = null;
            callOnShownOnViewCreated = false;
        }
    }
}
