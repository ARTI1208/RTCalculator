package ru.art2000.calculator;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class DistanceFragment extends Fragment {


    public DistanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.unit_frag, container, false);
//        TextView textView = new TextView(getActivity());
//        textView.setText(R.string.hello_blank_fragment);
        RecyclerView rv = root.findViewById(R.id.unit_rv);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
//        ArrayAdapter<CharSequence> list_adapter = ArrayAdapter.createFromResource(getActivity(), R.array.velocity_items, R.layout.unit_list_item);
//        lv.setAdapter(list_adapter);

//        lv.setItemsCanFocus(false);
        rv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        UnitItemAdapter adapter = new UnitItemAdapter(getActivity(), new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.distance_items))));
        rv.setAdapter(adapter);

        return root;
    }

}
