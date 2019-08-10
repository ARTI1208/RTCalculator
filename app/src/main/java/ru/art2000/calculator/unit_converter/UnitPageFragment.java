package ru.art2000.calculator.unit_converter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.art2000.calculator.R;
import ru.art2000.helpers.GeneralHelper;
import ru.art2000.helpers.PrefsHelper;

public class UnitPageFragment extends Fragment {

    public UnitListAdapter adapter;
    boolean isCurrentPage = false;
    private Context mContext;
    private String category;
    private String type;
    private boolean dot;
    private int inputSpinnerPosition = 0;
    private int outputSpinnerPosition = 1;
    private TextView input;
    private TextView output;
    private Formulas formulas = new Formulas();
    private HorizontalScrollView inputScrollView;
    private Spinner inputSpinner;
    private Spinner outputSpinner;

    static UnitPageFragment newInstance(String category) {
        UnitPageFragment f = new UnitPageFragment();
        Bundle args = new Bundle();
        args.putString("category", category);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        Bundle arguments = getArguments();
        if (arguments == null) {
            Log.e("UnitPageFragment", "No arguments found, so can't determine category!");
        } else {
            category = arguments.getString("category", "area");
        }
        return inflateUnitView(inflater, container);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        switch (type) {
            case "simple":
                outState.putDouble("value",
                        Double.parseDouble(
                                input
                                        .getText()
                                        .toString()
                                        .replace(',', '.')));
                outState.putString("value_str", input.getText().toString());
                outState.putInt("dimension", inputSpinnerPosition);
                outState.putInt("out_dimension", outputSpinnerPosition);
                break;
            case "powerful":
                outState.putDouble("value", adapter.inp);
                outState.putInt("dimension", adapter.curDim);
                break;
            default:
                break;
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState == null)
            return;

        double value;
        int dimension;

        switch (type) {
            case "simple":
                dimension = savedInstanceState.getInt("dimension");
                int outDimension = savedInstanceState.getInt("out_dimension");
                input.setText(savedInstanceState.getString("value_str"));
                inputSpinner.setSelection(dimension);
                outputSpinner.setSelection(outDimension);
                break;
            case "halfpowered":
                break;
            case "powerful":
                value = savedInstanceState.getDouble("value");
                dimension = savedInstanceState.getInt("dimension");
                adapter.setValueAndDimension(value, dimension, true);
                break;
        }
    }

    private View inflateUnitView(LayoutInflater inflater, ViewGroup container) {
        type = PrefsHelper.getUnitViewType();
        switch (type) {
            case "simple":
                return simpleUnitView(inflater, container);
            case "powerful":
                return powerfulUnitView(inflater, container);
            default:
                return halfPoweredUnitView(inflater, container);
        }
    }

    private String[] getDimensionsArray() {
        return mContext.getResources().getStringArray(getDimensionsArrayId());
    }

    private int getDimensionsArrayId() {
        String items = "_items";
        return mContext.getResources().getIdentifier(
                category + items, "array", mContext.getPackageName());
    }

    private View powerfulUnitView(LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.unit_frag, container, false);
        RecyclerView rv = root.findViewById(R.id.unit_rv);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        rv.setLayoutManager(llm);
        rv.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        adapter = new UnitListAdapter(
                mContext,
                getDimensionsArray(),
                Formulas.getCategoryInt(category),
                true);
        adapter.fragment = this;
        rv.setAdapter(adapter);
        return root;
    }

    private View halfPoweredUnitView(LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.unit_frag_half, container, false);
        RecyclerView rv = root.findViewById(R.id.unit_rv);
        registerForContextMenu(rv);
        final Spinner spinner = root.findViewById(R.id.hpuv_spinner);
        EditText input = root.findViewById(R.id.hpuv_et);
        adapter = new UnitListAdapter(mContext,
                getDimensionsArray(), Formulas.getCategoryInt(category), false);
        adapter.fragment = this;
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    adapter.setInputValue(s.toString());
                } else {
                    adapter.setInputValue("1");
                }
            }
        });
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        rv.setLayoutManager(llm);
        ArrayAdapter<?> spadapter = ArrayAdapter.createFromResource(mContext, getDimensionsArrayId(),
                android.R.layout.simple_spinner_item);
        spadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spadapter);
        rv.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.setCurDim(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        rv.setAdapter(adapter);
        return root;
    }

    private View simpleUnitView(LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.unit_frag_simple, container, false);
        inputSpinner = root.findViewById(R.id.spinner_from);
        input = root.findViewById(R.id.value_original);
        inputScrollView = root.findViewById(R.id.or_hsv);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateResult();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        setSimpleViewButtonsClickListener(root);
        output = root.findViewById(R.id.value_converted);
        outputSpinner = root.findViewById(R.id.spinner_to);
        ArrayAdapter<?> spinnerAdapter = ArrayAdapter.createFromResource(mContext,
                getDimensionsArrayId(),
                android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        outputSpinner.setAdapter(spinnerAdapter);
        outputSpinner.setSelection(1);
        inputSpinner.setAdapter(spinnerAdapter);
        inputSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected,
                                       int selectedItemPosition,
                                       long selectedId) {
                if (selectedItemPosition == outputSpinnerPosition)
                    outputSpinner.setSelection(inputSpinnerPosition);
                inputSpinnerPosition = selectedItemPosition;
                updateResult();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        outputSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                if (selectedItemPosition == inputSpinnerPosition)
                    inputSpinner.setSelection(outputSpinnerPosition);
                outputSpinnerPosition = selectedItemPosition;
                updateResult();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return root;
    }

    private void updateResult() {
        inputScrollView.postDelayed(() ->
                inputScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT), 100L);
        formulas.calc(Formulas.getCategoryInt(category),
                inputSpinnerPosition,
                Double.parseDouble(input.getText().toString()));
        output.setText(GeneralHelper.resultNumberFormat.format(
                formulas.getResult(Formulas.getCategoryInt(category), outputSpinnerPosition)));
    }

    private void setSimpleViewButtonsClickListener(View v) {
        for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
            View child = ((ViewGroup) v).getChildAt(i);
            if (child instanceof ViewGroup)
                setSimpleViewButtonsClickListener(child);
            if (child instanceof Button)
                child.setOnClickListener(this::onButtonClick);
            if (child instanceof ImageButton) {
                child.setOnClickListener(btn -> {
                    switch (PrefsHelper.getExtraButtonAction()) {
                        default:
                        case PrefsHelper.SWAP_DIMENSIONS:
                            inputSpinner.setSelection(outputSpinnerPosition);
                            outputSpinner.setSelection(inputSpinnerPosition);
                            break;
                        case PrefsHelper.SHOW_ALL_DIMENSIONS:
                            Intent intent = new Intent(mContext, AllUnitsActivity.class);
                            intent.putExtra("value", Double.valueOf(input.getText().toString()));
                            intent.putExtra("pos", inputSpinner.getSelectedItemPosition());
                            intent.putExtra("category", category);
                            intent.putExtra("dims", getDimensionsArray());
                            mContext.startActivity(intent);
                            break;
                    }
                });
                child.setOnLongClickListener(btn -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    final String[] mChooseEvent = {"Смена местами", "Показать все"};
                    builder.setTitle("Выберите назначение кнопки")
                            .setCancelable(true);
                    builder.setSingleChoiceItems(mChooseEvent, PrefsHelper.getExtraButtonAction(), (dialog, selectedItem) -> {
                        PrefsHelper.setExtraButtonAction(selectedItem);
                        dialog.cancel();
                    });
                    builder.create().show();
                    return true;
                });
            }
        }
    }

    private void onButtonClick(View v) {
        String input = this.input.getText().toString();
        Button button = (Button) v;
        switch (button.getId()) {
            default:
                if (input.equals("0"))
                    this.input.setText(button.getText().toString());
                else
                    this.input.append(button.getText().toString());
                break;
            case R.id.buttonMinus:
                if (!input.equals("0"))
                    if (input.contains("-"))
                        this.input.setText(input.substring(1));
                    else {
                        String txt = "-" + input;
                        this.input.setText(txt);
                    }
                break;
            case R.id.buttonClear:
                this.input.setText("0");
                dot = false;
                break;
            case R.id.buttonDel:
                if (input.length() == 1 || (input.length() == 2 && input.contains("-")))
                    this.input.setText("0");
                else
                    this.input.setText(input.substring(0, input.length() - 1));
                if (input.toCharArray()[input.length() - 1] == '.')
                    dot = false;
                break;
            case R.id.buttonDot:
                if (!dot) {
                    this.input.append(".");
                    dot = true;
                }
                break;
        }
    }

}
