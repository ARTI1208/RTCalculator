package ru.art2000.calculator;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CurrencyFragment extends Fragment {

    public TextView InputTV;
    public TextView ResultTV;
    public Button Button_pressed;
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEd;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View CurrencyView = inflater.inflate(R.layout.currency_layout, container, false);
//        InputTV = getActivity().findViewById(R.id.tv_input);
//        ResultTV = getActivity().findViewById(R.id.tv_result);
        return CurrencyView;
    }
}
