package ru.art2000.calculator.calculator;

import android.util.Log;
import org.apache.commons.math3.special.Gamma;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class CalculationClass {

    private static HashMap <String, Integer> operationsOrder;
    private static HashMap <String, Integer> operationsCount;

    static {
        operationsOrder = new HashMap<>();
        String order = "%;!;_;^_;*;×;÷;+;-;/;:_√;lg;ln;cos;sin;tg;ctg;";
        int o = 1;
        int p = -1;
        while ((p = order.indexOf(';')) != -1){
            String op = order.substring(0, p);
            if (op.equals("_"))
                ++o;
            else
                operationsOrder.put(op, o);
            order = order.substring(p + 1);
        }
    }


    static double memory = 0;
    static boolean radians = false;

    static boolean isSign(String s) {
        String signs = "*×÷+-/:^";
        return signs.contains(s);
    }

    static boolean isSign(char c) {
        return isSign(String.valueOf(c));
    }

    private static boolean containsUpClass(String s) {
        return (s.contains("*") || s.contains("/") || s.contains(":") || s.contains("×") ||
                s.contains("÷") || s.contains("^"));
    }

    private static boolean containsDiv(String s) {
        return (s.contains("/") || s.contains(":") || s.contains("÷"));
    }

    static String addRemoveBrackets(String e){
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
        Log.d("or", brFixed);
        return removeUnnecessaryBrackets(brFixed);
    }

    private static int countBrs(String expr){
        int res = 0;
        for (char c : expr.toCharArray()) {
            if (c == ')')
                res++;
        }
        return res;
    }

    static boolean isAfterUnarySign(String s){
        String unarySigns = "%!";
        return unarySigns.contains(s);
    }

    static boolean isAfterUnarySign(char c){
        return isAfterUnarySign(String.valueOf(c));
    }

    static boolean isPreUnarySign(String s){
        String unarySigns = "√lglncossintgctg";
        return unarySigns.contains(s);
    }

    static boolean isPreUnarySign(char c){
        return isPreUnarySign(String.valueOf(c));
    }

    private static int getLastUnclosedBracket(int[] open, int[] close){
        for (int i = open.length - 1; i >= 0; i--) {
            if (open[i] != -1 && close[i] == -1)
                return i;
        }
        return -1;
    }

    private static String removeUnnecessaryBrackets(String toFix){
        String brFixed = toFix;
        int j = 0;
        int obc = 0;
        int brs = countBrs(toFix);
        int [] ops2 = new int[brs];
        int [] cls2 = new int[brs];
        for (int i = 0; i < brs; i++) {
            ops2[i] = -1;
            cls2[i] = -1;
        }
        String bw = "";
        while (j < brFixed.length()){
            if (brFixed.toCharArray()[j] == '(')
                ops2[obc++] = j;
            if (brFixed.toCharArray()[j] == ')'){
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
                    brs = countBrs(toFix);
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

    public static double calculateDbl(String expression){
        String e = calculateStr(expression).replace(',', '.');
        if (e.equals("error") || e.equals("zero"))
            return 0.0;
        return Double.parseDouble(e);
    }

    static String calculateStr(String expression){
        NumberFormat nf = new DecimalFormat("#.#######");
        expression = expression.replaceAll("e", String.valueOf(Math.E));
        expression = expression.replaceAll("π", String.valueOf(Math.PI));
        expression = expression.replaceAll("φ", "1.6180339887");
        String expr = brackets(expression);
        expr = noBracketsWork(expr);
        try {
            expr = nf.format(Double.parseDouble(expr));
        } catch (Exception e){
            if (!expr.equals("zero"))
                expr = "error";
        }
        return expr;
    }

    private static String brackets(String str){
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

    static boolean isNumber(String str){
        return "0123456789".contains(str);
    }

    static boolean isNumber(char c){
        return isNumber(String.valueOf(c));
    }

    static boolean isDot(String c){
        return c.equals(".") || c.equals(",");
    }

    private static boolean isDot(char c){
        return c == '.' || c == ',';
    }

    private static boolean isNumberOrDot(char s){
        return isNumber(s) || isDot(s) || s == '-';
    }

    static int signsInExpr(String expr){
        int count = 0;
        for (char c : expr.toCharArray())
            if (isSign(c))
                count++;
        return count;
    }

    private static boolean containsPreUnary(String string){
        return string.contains("√") || string.contains("ln") || string.contains("lg") ||
                string.contains("sin") || string.contains("cos");
    }

    private static double factorial(double x) {
        return Gamma.gamma(x);
    }

    private static String aft(String ex){
        ArrayList<String> afterSigns = new ArrayList<>();
        if (ex.contains("%"))
            afterSigns.add("%");
        if (ex.contains("!"))
            afterSigns.add("!");
        int afterListSize = afterSigns.size();
        if (afterListSize == 0)
            return ex;
        String st = ex;
        char[] strch = st.toCharArray();
        int num = -1;
        int numc = -1;
        int i = st.length() - 1;

        while (i >=0 && ((isNumberOrDot(strch[i]) ||
                strch[i] == '-' ||
                num == -1))){
            if (isNumberOrDot(strch[i]) && numc == -1)
                numc = i;
            if (isNumberOrDot(strch[i]))
                num = i;
            i--;
        }

        String under = st.substring(num, numc + 1);

        switch (st.toCharArray()[numc + 1]){
            case '%':
                st = st.substring(0, num) + (Double.parseDouble(under) / 100) +
                        st.substring(numc + 2);
                break;
            case '!':
                st = st.substring(0, num) + factorial(Double.parseDouble(under) + 1) + st.substring(numc + 2);
                break;
        }
        return aft(st);
    }

    private static double stringToDegreesOrRadians(String str){
        double val = Double.valueOf(str);
        if (!radians)
            val *= Math.PI / 180;
        return val;
    }

    private static String preUnaryWork(String expr){
        ArrayList<String> preSigns = new ArrayList<>();
        if (expr.contains("√"))
            preSigns.add("√");
        if (expr.contains("lg"))
            preSigns.add("lg");
        if (expr.contains("ln"))
            preSigns.add("ln");
        if (expr.contains("sin"))
            preSigns.add("sin");
        if (expr.contains("cos"))
            preSigns.add("cos");
        if (expr.contains("tg") && (!expr.contains("c") || expr.indexOf("c") != expr.indexOf("tg") - 1))
            preSigns.add("tg");
        if (expr.contains("ctg"))
            preSigns.add("ctg");

        int preListSize = preSigns.size();

        String str = expr;
        str = aft(str);



        if (preListSize == 0)
            return str;

        int i = 0;
        int len = str.length() - 1;
        char[] strch = str.toCharArray();
        int num = -1;


        while (i < len && !isNumberOrDot(strch[i])){
            num = i++;
        }

        num++;

        String under = str.substring(num);

            int lastSign = -1;
            String op = "√";

            for (int j = 0; j < preListSize; j++) {
                String oper = preSigns.get(j);
                int pos = expr.lastIndexOf(oper);
                if (pos > lastSign) {
                    lastSign = pos;
                    op = oper;
                }
            }


            switch (op) {
                default:
                case "√":
                    str = str.substring(0, lastSign) +
                            Math.sqrt(Double.parseDouble(under));
                    break;
                case "lg":
                    str = str.substring(0, lastSign) +
                            Math.log10(Double.parseDouble(under));
                    break;
                case "ln":
                    str = str.substring(0, lastSign) +
                            Math.log(Double.parseDouble(under));
                    break;
                case "sin":
                    str = str.substring(0, lastSign) +
                            Math.sin(stringToDegreesOrRadians(under));
                    break;
                case "cos":
                    str = str.substring(0, lastSign) +
                            Math.cos(stringToDegreesOrRadians(under));
                    break;
                case "tg":
                    str = str.substring(0, lastSign) +
                            Math.tan(stringToDegreesOrRadians(under));
                    break;
                case "ctg":
                    str = str.substring(0, lastSign)
                            + String.valueOf(Math.cos(stringToDegreesOrRadians(under))
                            / Math.sin(stringToDegreesOrRadians(under)));
                    break;
            }
        return preUnaryWork(str);
    }

    private static String noBracketsWork(String expression){
        try {
            int i = 1;
            int signCount = 0;
            int lpos = 0;
            int len = expression.length();


//            int t = 1;
//            String tmp = expression;
//            while (t < tmp.length()){
//                for (String s: operationsOrder.keySet()) {
//                    if (tmp.substring(0,t).contains(s))
//
//                }
//            }
//            while ()










            while (i < len) {
//                Log.d("pr", expression.substring(lpos + 1, i));
                if (isSign(String.valueOf(expression.toCharArray()[i])) &&
                        (!String.valueOf(expression.toCharArray()[i]).equals("-") ||
                                isNumber((expression.toCharArray()[i - 1])))) {
                    signCount++;

                    lpos = i;
                    i++;
                } else if (String.valueOf(expression.toCharArray()[i]).equals("-") &&
                        isSign(String.valueOf(expression.toCharArray()[i])))
                    i += 2;
                else i++;
            }
            double result = 0;
            int pr = 0;
            int nx = expression.length() - 1;
            i = 1;



            Log.d("fuuu", String.valueOf(signCount));

            if (signCount == 0)
                return preUnaryWork(expression.replace(',', '.'));

            while (signCount > 0) {
                if (i < expression.length() &&( ((expression.toCharArray()[i] == '^') || (expression.toCharArray()[i] == '/') || (expression.toCharArray()[i] == ':') ||
                        (expression.toCharArray()[i] == '×') || (expression.toCharArray()[i] == '÷'))
                        || (!containsUpClass(expression) && ((expression.toCharArray()[i] == '+') ||
                        (expression.toCharArray()[i] == '-'))) && !isPreUnarySign(expression.substring(0, i)))) {
                    for (int j = i - 1; j >= 0; j--) {
                        pr = j;
                        Log.d("sufwe", String.valueOf(j));
                        if (isSign(String.valueOf(expression.toCharArray()[j])) && j !=  0 &&
                                !isPreUnarySign(expression.substring(0, j))) {
                            Log.d("su", expression.substring(0, j + 1));
                            pr = j + 1;
                            break;
                        }
                    }
                    for (int j = i + 1, l = expression.length(); j < l; j++) {
                        nx = j;
                        if (!isPreUnarySign(expression.substring(i + 1, j)) && isSign(String.valueOf(expression.toCharArray()[j])) ^
                                (String.valueOf(expression.toCharArray()[j]).equals("-") &&
                                        isSign(String.valueOf(expression.toCharArray()[j - 1])))) {
                            nx = j - 1;
                            break;
                        }
                    }
                    int dotl = expression.substring(pr, i).indexOf(",");
                    StringBuilder lefty = new StringBuilder(expression.substring(pr, i));
                    if (dotl != -1)
                        lefty.setCharAt(dotl, '.');
                    int dotr = expression.substring(i + 1, nx + 1).indexOf(",");
                    StringBuilder righty;
                    if (signCount > 1)
                        righty = new StringBuilder(expression.substring(i + 1, nx + 1));
                    else
                        righty = new StringBuilder(expression.substring(i + 1));
                    if (dotr != -1)
                        righty.setCharAt(dotr, '.');

                    String leftStr = lefty.toString();
                    String rightStr = righty.toString();
                    Log.d("left", leftStr);
                    Log.d("right", rightStr);

                    leftStr = preUnaryWork(leftStr);
                    rightStr = preUnaryWork(rightStr);


                    double left = Double.parseDouble(leftStr);
                    double right = Double.parseDouble(rightStr);
                    if (containsDiv(String.valueOf(expression.toCharArray()[i])) && right == 0)
                        return "zero";
                    switch (expression.toCharArray()[i]) {
                        case '+':
                            result = left + right;
                            break;
                        case '-':
                            result = left - right;
                            break;
                        case '*':
                        case '×':
                            result = left * right;
                            break;
                        case '÷':
                            result = left / right;
                            break;
                        case '/':
                            if (left / right >= 0)
                                result = Math.floor(left / right);
                            else
                                result = Math.floor(left / right) + 1;
                            break;
                        case ':':
                            result = left % right;
                            break;
                        case '^':
                            result = Math.pow(left, right);
                            break;
                    }
                    String pre = "";
                    String aft = "";
                    if (pr != 0)
                        pre = expression.substring(0, pr);
                    if (nx != expression.length() - 1)
                        aft = expression.substring(nx + 1, expression.length());
                    String cur = String.valueOf(result);
                    Log.d("cur", String.valueOf(left)+expression.toCharArray()[i]+String.valueOf(right)+"="+cur);
                    if (signCount == 1)
                        expression = cur;
                    else
                        expression = pre + cur + aft;
                    signCount--;
                    i = 1;
                    Log.d("wat3", expression);
                    continue;
                }

                Log.d("wat", "ef");
                if (expression.toCharArray()[i] == ',')
                    expression.toCharArray()[i] = '.';
                i++;
                Log.d("wat2", "ef");
            }
            expression = expression.replace(",", ".");
        } catch (Exception e) {
            e.printStackTrace();
            expression = "error";
        }
        return expression;
    }
}
