package ru.art2000.calculator.unit_converter;

class Formulas {
    private static final int VELOCITY = 0;
    private static final int DISTANCE = 1;
    private static final int AREA = 2;
    private static final int VOLUME = 3;
    private static final int MASS = 4;
    private static final int PRESSURE = 5;
    private static final int TEMPERATURE = 6;
    private final double[] velocityMultipliers = {1, 60, 60d / 1000d, 3600d / 1000d, 3.280839, 2.2369, 0.003, 1.9438};
    private final double[] distanceMultipliers = {1000, 100, 10, 1, 1d / 1000d, 39.37, 3.2808, 1.0936, 0.00062137, 0.0005399568, 1d / 946000000d, 1d / 0.71};
    private final double[] areaMultipliers = {1000000, 10000, 100, 1};
    private final double[] volumeMultipliers = {1000000, 1};
    private final double[] massMultipliers = {1000, 1};
    private final double[] pressureMultipliers = {101325, 1};
    private double[] velocityResult = {1, 60, 60d / 1000d, 3600d / 1000d, 3.280839, 2.2369, 0.003, 1.9438};
    private double[] distanceResult = {1000, 100, 10, 1, 1 / 1000d, 39.37, 3.2808, 1.0936, 0.00062137, 0.0005399568, 1 / 946000000d, 1 / 0.71};
    private double[] areaResult = {1000000, 10000, 100, 1};
    private double[] volumeResult = {1000000, 1};
    private double[] massResult = {1000, 1};
    private double[] pressureResult = {101325, 1};
    private double[] temperatureResult = {1, 2};
    private FormulaItem celsius = new FormulaItem("Cel");
    private FormulaItem far = new FormulaItem("Far");
    private FormulaItem[] temps = new FormulaItem[]{celsius, far};

    Formulas() {
        celsius.addFormula(far.getTag(), "5÷9×(X-32)");
        far.addFormula(celsius.getTag(), "9÷5×X+32");
    }

    static int getCategoryInt(String str) {
        switch (str) {
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

    double getResult(int category, int pos) {
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

    void calc(int category, int pos, double value) {
        switch (category) {
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
                calc(volumeMultipliers, volumeResult, pos, value);
                break;
            case MASS:
                calc(massMultipliers, massResult, pos, value);
                break;
            case PRESSURE:
                calc(pressureMultipliers, pressureResult, pos, value);
                break;
            case TEMPERATURE:
                calcByFormula(temps, temperatureResult, pos, value);
                break;
        }
    }

    private void calc(double[] mults, double[] res, int pos, double value) {
        int size = mults.length;
        if (pos < size) {
            double m = value / mults[pos];
            for (int i = 0; i < size; i++) {
                res[i] = m * mults[i];

            }
        }
    }

    private void calcByFormula(FormulaItem[] massive, double[] res, int pos, double value) {
        for (int i = 0, size = res.length; i < size; i++) {
            if (pos != i) {
                res[i] = massive[i].convert(massive[pos].getTag(), value);
            } else {
                res[pos] = value;
            }
        }
    }
}
