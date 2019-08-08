package ru.art2000.calculator.calculator;

import org.apache.commons.math3.special.Gamma;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import ru.art2000.helpers.GeneralHelper;

public class CalculationClass {

    static double memory = 0;
    static boolean radians = false;
    private static HashMap<String, Integer> operationsOrder;
    private static HashMap<String, Integer> operationsCount = new HashMap<>();
    private static int priorityLevels;
    private static ArrayList<String> afterUnaryOperations;
    private static ArrayList<String> preUnaryOperations;

    static {
        operationsOrder = new HashMap<>();
        afterUnaryOperations = new ArrayList<>(Arrays.asList("%", "!"));
        preUnaryOperations = new ArrayList<>(
                Arrays.asList("√", "lg", "ln", "cos", "sin", "ctg", "tan"));
        String order = GeneralHelper.joinToString(
                afterUnaryOperations, ";", "", ";_;")
                + "^;_;*;×;÷;/;:;_;+;-;"
                + GeneralHelper.joinToString(
                preUnaryOperations, ";", "_;", ";");
        int o = 1;
        int p;
        while ((p = order.indexOf(';')) != -1) {
            String op = order.substring(0, p);
            if (op.equals("_")) {
                ++o;
            } else {
                operationsOrder.put(op, o);
            }
            order = order.substring(p + 1);
        }
        priorityLevels = o;
    }

    private static boolean isOperation(String toCheck) {
        return operationsOrder.containsKey(toCheck);
    }

    private static boolean isFoundOperation(String toCheck) {
        return operationsCount.containsKey(toCheck);
    }

    @SuppressWarnings("ConstantConditions")
    private static boolean isOfUpperClass(String operation) throws Exception {
        if (!operationsOrder.containsKey(operation)) {
            throw new Exception("Unknown op " + operation);
        }

        int o = operationsOrder.get(operation);
        if (o == priorityLevels) {
            return true;
        }

        if (operationsCount == null) {
            throw new Exception("No operations");
        }

        for (String otherOperation : operationsCount.keySet()) {
            if (operationsCount.get(otherOperation) > 0 && operationsOrder.get(otherOperation) < o)
                return false;
        }

        return true;
    }

    @SuppressWarnings("ConstantConditions")
    private static void pushToOperationList(String operation) throws Exception {
        if (operationsCount == null) {
            throw new Exception("No operations");
        }
        if (!operationsOrder.containsKey(operation)) {
            throw new Exception("Unknown operation " + operation);
        }
        if (operationsCount.containsKey(operation)) {
            int val = operationsCount.get(operation) + 1;
            operationsCount.put(operation, val);
        } else {
            operationsCount.put(operation, 1);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static void removeFromOperationList(String operation) throws Exception {
        if (operationsCount == null) {
            throw new Exception("No operations");
        }
        if (!operationsOrder.containsKey(operation)) {
            throw new Exception("Unknown operation " + operation);
        }
        if (operationsCount.containsKey(operation)) {
            int val = operationsCount.get(operation) - 1;
            operationsCount.put(operation, val);
        }
    }

    private static boolean isBinaryOperation(String operation) {
        return "^*×÷/:+-".contains(operation);
    }


    static boolean isSign(String s) {
        String signs = "*×÷+-/:^";
        return signs.contains(s);
    }

    static boolean isSign(char c) {
        return isSign(String.valueOf(c));
    }

    private static boolean containsDiv(String s) {
        return (s.contains("/") || s.contains(":") || s.contains("÷"));
    }

    static String addRemoveBrackets(String e) {
        int obc = 0;
        int cbc = 0;
        for (char c : e.toCharArray()) {
            if (c == '(')
                obc++;
            if (c == ')')
                cbc++;
        }
        StringBuilder sb = new StringBuilder(e);
        while (obc != cbc) {
            sb.append(")");
            cbc++;
        }
        String brFixed = sb.toString();

        return removeUnnecessaryBrackets(brFixed);
    }

    private static int countBrackets(String expr) {
        int res = 0;
        for (char c : expr.toCharArray()) {
            if (c == ')')
                res++;
        }
        return res;
    }

    static boolean isAfterUnarySign(String s) {
        for (String val : afterUnaryOperations) {
            if (val.equals(s)) {
                return true;
            }
        }

        return false;
    }

    static boolean isAfterUnarySign(char c) {
        return isAfterUnarySign(String.valueOf(c));
    }

    static boolean isPreUnarySign(String s) {
        for (String val : preUnaryOperations) {
            if (val.equals(s)) {
                return true;
            }
        }

        return false;
    }

    static boolean isPreUnarySign(char c) {
        return isPreUnarySign(String.valueOf(c));
    }

    private static int getLastUnclosedBracket(int[] open, int[] close) {
        for (int i = open.length - 1; i >= 0; i--) {
            if (open[i] != -1 && close[i] == -1)
                return i;
        }
        return -1;
    }

    private static String removeUnnecessaryBrackets(String toFix) {
        String brFixed = toFix;
        int j = 0;
        int obc = 0;
        int brs = countBrackets(toFix);
        int[] ops2 = new int[brs];
        int[] cls2 = new int[brs];
        for (int i = 0; i < brs; i++) {
            ops2[i] = -1;
            cls2[i] = -1;
        }
        String bw = "";
        while (j < brFixed.length()) {
            if (brFixed.toCharArray()[j] == '(')
                ops2[obc++] = j;
            if (brFixed.toCharArray()[j] == ')') {
                int lastOpenBr = getLastUnclosedBracket(ops2, cls2);
                if (lastOpenBr == -1)
                    break;
                int op = ops2[lastOpenBr];
                cls2[lastOpenBr] = j;
                String betw = brFixed.substring(op + 1, j);
                if (betw.equals("(" + bw + ")")) {
                    brFixed = brFixed.substring(0, op + 1) + bw + brFixed.substring(j);
                    j = -1;
                    obc = 0;
                    brs = countBrackets(toFix);
                    ops2 = new int[brs];
                    cls2 = new int[brs];
                    for (int i = 0; i < brs; i++) {
                        ops2[i] = -1;
                        cls2[i] = -1;
                    }
                }
                bw = betw;
            }
            j++;
        }
        if (brFixed.equals("(" + bw + ")"))
            brFixed = bw;
        return brFixed;
    }

    public static double calculateDbl(String expression) {
        String e = calculateStr(expression).replace(',', '.');
        if (e.equals("error") || e.equals("zero"))
            return 0.0;
        return Double.parseDouble(e);
    }

    static String calculateStr(String expression) {
        operationsCount.clear();
        NumberFormat nf = new DecimalFormat("#.#######");
        expression = expression.replaceAll("e", String.valueOf(Math.E));
        expression = expression.replaceAll("π", String.valueOf(Math.PI));
        expression = expression.replaceAll("φ", "1.6180339887");
        String expr = bracketsWork(expression);
        expr = noBracketsWork(expr);
        try {
            expr = nf.format(Double.parseDouble(expr));
        } catch (Exception e) {
            if (!expr.equals("zero"))
                expr = "error";
        }
        return expr;
    }

    private static String bracketsWork(String str) {
        String ret = str;
        int obp = -1;
        int cbp;
        int j = 0;
        while (ret.contains("(")) {
            if (ret.toCharArray()[j] == '(')
                obp = j;
            if (ret.toCharArray()[j] == ')') {
                cbp = j;
                if (obp != -1) {
                    String nobrs = ret.substring(obp + 1, cbp);
                    ret = ret.substring(0, obp) + noBracketsWork(nobrs) + ret.substring(cbp + 1);
                    j = -1;
                }
            }
            j++;
        }
        return ret;
    }

    static boolean isNumber(String str) {
        return "0123456789".contains(str);
    }

    static boolean isNumber(char c) {
        return isNumber(String.valueOf(c));
    }

    static boolean isDot(String c) {
        return c.equals(".") || c.equals(",");
    }

    private static boolean isDot(char c) {
        return c == '.' || c == ',';
    }

    private static boolean isNumberOrDot(char s) {
        return isNumber(s) || isDot(s) || s == '-';
    }

    static int signsInExpr(String expr) {
        int count = 0;
        for (char c : expr.toCharArray()) {
            if (isSign(c)) {
                count++;
            }
        }
        return count;
    }

    private static double factorial(double x) {
        return Gamma.gamma(x + 1);
    }

    private static double stringToDegreesOrRadians(String str) {
        double val = Double.valueOf(str);
        if (!radians) {
            val *= Math.PI / 180;
        }
        return val;
    }

    private static String afterUnaryWork(String expression) {
        int pos = -1;
        String operation = null;

        for (String sign : afterUnaryOperations) {
            int newPos = expression.indexOf(sign);
            if (newPos != -1 && (newPos < pos || pos == -1)) {
                pos = newPos;
                operation = sign;
            }
        }

        if (operation == null) {
            return expression;
        }

        String under = findOperationValues(operation, pos, expression).get(0);
        int lastPositionBeforeValue = pos - under.length();
        int firstPositionAfterOperation = pos + operation.length();

        expression = expression.substring(0, lastPositionBeforeValue)
                + executeAfterUnaryOperation(operation, under)
                + expression.substring(firstPositionAfterOperation);

        return afterUnaryWork(expression);
    }

    private static String preUnaryWork(String expression) {
        int pos = -1;
        String operation = null;

        for (String sign : preUnaryOperations) {
            int newPos = expression.lastIndexOf(sign);
            if (newPos != -1 && newPos > pos) {
                pos = newPos;
                operation = sign;
            }
        }

        if (operation == null) {
            return expression;
        }

        String under = findOperationValues(operation, pos, expression).get(0);
        expression = expression.substring(0, pos)
                + executeBeforeUnaryOperation(operation, under);

        return preUnaryWork(expression);
    }

    private static String unaryOperationWork(String expression) {
        String initialValue = expression;

        expression = afterUnaryWork(expression);
        expression = preUnaryWork(expression);

        if (expression.equals(initialValue)) {
            return initialValue;
        }

        return unaryOperationWork(expression);
    }

    private static String executeBeforeUnaryOperation(String operation, String valueStr) {
        double result;
        switch (operation) {
            default:
                throw new ArithmeticException(
                        "Unknown before unary operation \"" + operation + "\"");
            case "√":
                result = Math.sqrt(Double.parseDouble(valueStr));
                break;
            case "lg":
                result = Math.log10(Double.parseDouble(valueStr));
                break;
            case "ln":
                result = Math.log(Double.parseDouble(valueStr));
                break;
            case "sin":
                result = Math.sin(stringToDegreesOrRadians(valueStr));
                break;
            case "cos":
                result = Math.cos(stringToDegreesOrRadians(valueStr));
                break;
            case "tan":
                result = Math.tan(stringToDegreesOrRadians(valueStr));
                break;
            case "ctg":
                result = Math.cos(stringToDegreesOrRadians(valueStr))
                        / Math.sin(stringToDegreesOrRadians(valueStr));
                break;
        }
        return String.valueOf(result);
    }

    private static String executeAfterUnaryOperation(String operation, String valueStr) {
        double result;
        switch (operation) {
            case "%":
                result = (Double.parseDouble(valueStr) / 100);
                break;
            case "!":
                result = factorial(Double.parseDouble(valueStr));
                break;
            default:
                throw new ArithmeticException("Unknown after unary operation \"" + operation + "\"");
        }
        return String.valueOf(result);
    }

    private static String executeBinaryOperation(String operation, String leftStr, String rightStr) {
        double result;
        double left = Double.parseDouble(leftStr);
        double right = Double.parseDouble(rightStr);
        if (containsDiv(operation) && right == 0)
            return "zero";
        switch (operation) {
            case "+":
                result = left + right;
                break;
            case "-":
                result = left - right;
                break;
            case "*":
            case "×":
                result = left * right;
                break;
            case "÷":
                result = left / right;
                break;
            case "/":
                if (left / right >= 0)
                    result = Math.floor(left / right);
                else
                    result = Math.floor(left / right) + 1;
                break;
            case ":":
                result = left % right;
                break;
            case "^":
                result = Math.pow(left, right);
                break;
            default:
                throw new ArithmeticException("Unknown binary operation \"" + operation + "\"");
        }
        return String.valueOf(result);
    }

    private static ArrayList<String> findOperationValues(
            String operation, int positionInExpression, String expression) {
        ArrayList<String> values = new ArrayList<>();
        char[] charExpression = expression.toCharArray();
        if (isBinaryOperation(operation)) {
            int valueBeforeStartPosition = 0;
            int valueAfterEndPosition = expression.length() - 1;
            for (int j = positionInExpression - 1; j >= 0; --j) {
                valueBeforeStartPosition = j;

                if (isSign(String.valueOf(charExpression[j])) && j != 0 &&
                        !isPreUnarySign(expression.substring(0, j))) {

                    valueBeforeStartPosition = j + 1;
                    break;
                }
            }
            for (int j = positionInExpression + 1, l = expression.length(); j < l; j++) {
                valueAfterEndPosition = j;
                if (!isPreUnarySign(expression.substring(positionInExpression + 1, j))
                        && isSign(String.valueOf(charExpression[j]))
                        ^ (String.valueOf(charExpression[j]).equals("-")
                        && isSign(String.valueOf(charExpression[j - 1])))) {
                    valueAfterEndPosition = j - 1;
                    break;
                }
            }

            String leftStr = expression
                    .substring(valueBeforeStartPosition, positionInExpression)
                    .replace(",", ".");
            String rightStr = expression
                    .substring(positionInExpression + 1, valueAfterEndPosition + 1)
                    .replace(",", ".");
            values.add(leftStr);
            values.add(rightStr);
        } else if (isAfterUnarySign(operation)) {
            int valueStartPosition = -1;
            int valueEndPosition = -1;
            int i = positionInExpression - 1;

            while (i >= 0 && ((isNumberOrDot(charExpression[i]) ||
                    charExpression[i] == '-' ||
                    valueStartPosition == -1))) {
                if (isNumberOrDot(charExpression[i]) && valueEndPosition == -1)
                    valueEndPosition = i;
                if (isNumberOrDot(charExpression[i]))
                    valueStartPosition = i;
                i--;
            }

            String under = expression.substring(valueStartPosition, valueEndPosition + 1);
            values.add(under);
        } else if (isPreUnarySign(operation)) {
            int valueStartPosition = 0;
            int len = expression.length() - 1;

            while (valueStartPosition < len && !isNumberOrDot(charExpression[valueStartPosition])) {
                ++valueStartPosition;
            }

            String under = expression.substring(valueStartPosition);
            values.add(under);
        }
        return values;
    }

    private static String noBracketsWork(String expression) {
        try {
            int i = 1;
            int signCount = 0;
            int len = expression.length();

            while (i < len) {
                if (isSign(String.valueOf(expression.toCharArray()[i]))
                        && (!String.valueOf(expression.toCharArray()[i]).equals("-")
                        || !isSign(expression.toCharArray()[i - 1]))) {
                    signCount++;

                    pushToOperationList(String.valueOf(expression.toCharArray()[i]));

                    i++;
                } else if (String.valueOf(expression.toCharArray()[i]).equals("-") &&
                        isSign(String.valueOf(expression.toCharArray()[i])))
                    i += 2;
                else i++;
            }

            int pr;
            int nx;
            i = 1;

            if (signCount == 0)
                return unaryOperationWork(expression.replace(',', '.'));

            while (signCount > 0) {
                if (i < expression.length()) {
                    String elem = String.valueOf(expression.toCharArray()[i]);
                    if (isOperation(elem) && isFoundOperation(elem)
                            && isOfUpperClass(elem)) {

                        String leftStr;
                        String rightStr;

                        ArrayList<String> values = findOperationValues(elem, i, expression);
                        leftStr = values.get(0);
                        rightStr = values.get(1);

                        pr = i - leftStr.length();
                        nx = i + rightStr.length();


                        leftStr = unaryOperationWork(leftStr);
                        rightStr = unaryOperationWork(rightStr);

                        String cur = executeBinaryOperation(elem, leftStr, rightStr);

                        String pre = "";
                        String aft = "";
                        if (pr != 0)
                            pre = expression.substring(0, pr);
                        if (nx != expression.length() - 1)
                            aft = expression.substring(nx + 1);


                        removeFromOperationList(elem);

                        if (signCount == 1)
                            expression = cur;
                        else
                            expression = pre + cur + aft;
                        signCount--;

                        i = 1;
                        continue;
                    }
                } else {
                    break;
                }

                if (expression.toCharArray()[i] == ',')
                    expression.toCharArray()[i] = '.';

                i++;
            }
            expression = expression.replace(",", ".");
        } catch (Exception e) {
            e.printStackTrace();
            expression = "error";
        }
        return expression;
    }
}
