package ru.art2000.calculator.unit_converter;

import android.util.Log;

import java.util.HashMap;

import ru.art2000.calculator.calculator.CalculationClass;

public class FormulaItem {

    private String tag;
    private HashMap<String, String> forms = new HashMap<>();

    public FormulaItem(String tag){
        this.tag = tag;
    }

    /*
    Formula should be like this:
    20 * [[[X]]] - 4
    where [[[X]]] will be replaced with the value you want to convert into this unit
     */
    public void addFormula(String unit, String formula){
        forms.put(unit, formula);
    }

    public String getFormula(String unit){
        return forms.get(unit);
    }

    public String getTag() {
        return tag;
    }

    public double convert(String unit, double value){
        String f = forms.get(unit);
        f = f.replace("X", String.valueOf(value));
        Log.d("res formuls", f);
//        Log.d("brs formuls", calc.brackets(f));
//        Log.d("no brs formuls", calc.noBracketsWork(calc.brackets(f)));
        return CalculationClass.calculateDbl(f);
    }
}
