package ru.art2000.calculator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VelocityFragment extends Fragment {

    SimpleUnitInput sui = new SimpleUnitInput();

    public VelocityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return sui.InitiateSUV(inflater, container, getActivity(), "velocity");
    }
}
