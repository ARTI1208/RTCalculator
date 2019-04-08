package ru.art2000.calculator.unit_converter;

import android.util.Log;

public class Formulas {
    public static final int VELOCITY = 0;
    public static final int DISTANCE = 1;
    public static final int AREA = 2;
    public static final int VOLUME = 3;
    public static final int MASS = 4;
    public static final int PRESSURE = 5;
    public static final int TEMPERATURE = 6;
    final double[] velocityMultipliers = {1, 60, 60d/1000d, 3600d/1000d, 3.280839, 2.2369, 0.003, 1.9438};
    final double[] distanceMultipliers = {1000, 100, 10, 1, 1d/1000d, 39.37, 3.2808, 1.0936, 0.00062137, 0.0005399568, 1d/946000000d, 1d/0.71};
    final double[] areaMultipliers = {1000000, 10000, 100, 1};
    final double[] volumeMultipliers = {1000000, 1};
    final double[] massMultipliers = {1000, 1};
    final double[] pressureMultipliers = {101325, 1};
    double[] velocityResult = {1, 60, 60d/1000d, 3600d/1000d, 3.280839, 2.2369, 0.003, 1.9438};
    double[] distanceResult = {1000, 100, 10, 1, 1/1000, 39.37, 3.2808, 1.0936, 0.00062137, 0.0005399568, 1/946000000, 1/0.71};
    double[] areaResult = {1000000, 10000, 100, 1};
    double[] volumeResult = {1000000, 1};
    double[] massResult = {1000, 1};
    double[] pressureResult = {101325, 1};
    double[] temperatureResult = {1, 2};
    FormulaItem celsius = new FormulaItem("Cel");
    FormulaItem far = new FormulaItem("Far");
    FormulaItem[] temps = new FormulaItem[]{celsius, far};

    public static int getCategoryInt(String str){
        switch (str){
            default:
            case "velocity":
                return VELOCITY;
            case "distance":
                return DISTANCE;
            case "area":
                return AREA;
            case "volume":
                return VOLUME;
            case "mass":
                return MASS;
            case "pressure":
                return PRESSURE;
            case "temperature":
                return TEMPERATURE;
        }
    }

    Formulas(){
        celsius.addFormula(far.getTag(), "5÷9×(X-32)");
        far.addFormula(celsius.getTag(), "9÷5×X+32");
    }

    double getResult(int category, int pos){
        switch (category) {
            default:
            case VELOCITY:
                return velocityResult[pos];
            case DISTANCE:
                return distanceResult[pos];
            case AREA:
                return areaResult[pos];
            case VOLUME:
                return volumeResult[pos];
            case MASS:
                return massResult[pos];
            case PRESSURE:
                return pressureResult[pos];
            case TEMPERATURE:
                return temperatureResult[pos];
        }
    }

    public double calculateResult(int category, int pos, double value, int it){
        Log.d("val", String.valueOf(value));
        calc(category, pos, value);
        try {
            switch (category) {
                case VELOCITY:
                    return velocityResult[it];
                case DISTANCE:
                    return distanceResult[it];
                case AREA:
                    return areaResult[it];
                case VOLUME:
                    return volumeResult[it];
                case MASS:
                    return massResult[it];
                case PRESSURE:
                    return pressureResult[it];
                case TEMPERATURE:
                    return temperatureResult[it];
            }
        } catch (Exception e){
            return 0.0;
        }
        return 0.0;
    }

    public void calc(int category, int pos, double value){
        switch (category){
            case VELOCITY:
                calc(velocityMultipliers, velocityResult, pos, value);
                break;
            case DISTANCE:
                calc(distanceMultipliers, distanceResult, pos, value);
                break;
            case AREA:
                calc(areaMultipliers, areaResult, pos, value);
                break;
            case VOLUME:
                calc(volumeMultipliers, velocityResult, pos, value);
                break;
            case MASS:
                calc(massMultipliers, massResult, pos, value);
                break;
            case PRESSURE:
                calc(pressureMultipliers, pressureResult, pos, value);
                break;
            case TEMPERATURE:
                Log.d("val", String.valueOf(value));
                calcByFormula(temps, temperatureResult, pos, value);
                break;
        }
    }

    private void calc(double[] mults, double[] res, int pos, double value){
        Log.d("rrr", String.valueOf(pos) );
        int size = mults.length;
        if (pos < size) {
            double m = value / mults[pos];
            for (int i = 0; i < size; i++) {
                res[i] = m * mults[i];

            }
        }
    }

    private void calcByFormula(FormulaItem[] massive, double[] res, int pos, double value){
        for (int i = 0, size = res.length; i < size; i++) {
            if (pos != i){
                res[i] = massive[i].convert(massive[pos].getTag(), value);
            } else
                res[pos] = value;
//            Log.d(massive[i].getTag(), String.valueOf(value));
        }
    }
}
