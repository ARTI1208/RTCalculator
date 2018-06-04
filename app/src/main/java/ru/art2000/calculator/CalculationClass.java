package ru.art2000.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class CalculationClass extends MainActivity {

    String buttonText;
    Button Button_pressed;
    TextView InputTV;
    TextView ResultTV;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public void getButtonType(View v, Button b, String  txt, TextView  inpr, TextView res){
//        Button_pressed = findViewById(v.getId());

        buttonText = txt;
        Button_pressed = b;
        InputTV = inpr;
        ResultTV = res;

        if (isCButton(v))
            OnCBtnClick(v);
        else if (isSign(buttonText))
            onSignBtnClick(v);
        else if (v.getId() == R.id.buttonEQ)
            onResult(v);
        else
            onBtnClick(v);
    }

    public boolean isCButton(View v){
        return v.getId() == R.id.buttonClear || v.getId() == R.id.buttonDel;
    }

    public void OnCBtnClick(View v) {
        switch (v.getId()) {
            case R.id.buttonClear:
                ResultTV.setVisibility(View.INVISIBLE);
                InputTV.setText("0");
                break;
            case R.id.buttonDel:
                int InpLen = InputTV.length();
                String InputText = InputTV.getText().toString();
                String last = InputText.substring(InpLen - 1, InpLen);
                String prelast = "-1";
                if (InpLen > 1)
                    prelast = InputText.substring(InpLen - 2, InpLen - 1);
                if ((last.equals(".") || last.equals(",")) && prelast.equals("0")) {
                    String NewText = InputText.substring(0, InpLen - 2);
                    InputTV.setText(NewText);
                } else {
                    String NewText = InputText.substring(0, InpLen - 1);
                    InputTV.setText(NewText);
                }
                if (InpLen == 1)
                    InputTV.setText("0");
                if (ResultTV.getVisibility() == View.VISIBLE) {
                    InputTV.setText("0");
                    ResultTV.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    public boolean isSign(String s) {
        String signs = "×÷+-/:";
        return (signs.contains(s));
    }

    public boolean containsUpClass(String s) {
        return (s.contains("/") | s.contains(":") | s.contains("×") | s.contains("÷"));
    }

    public boolean containsDiv(String s) {
        return (s.contains("/") | s.contains(":") | s.contains("÷"));
    }

//    public void onCBtnClick(View v) {
//        switch (v.getId()) {
//            case R.id.buttonClear:
//                ResultTV.setVisibility(View.INVISIBLE);
//                InputTV.setText("0");
//                break;
//            case R.id.buttonDel:
//                int InpLen = InputTV.length();
//                String InputText = InputTV.getText().toString();
//                String last = InputText.substring(InpLen - 1, InpLen);
//                String prelast = "-1";
//                if (InpLen > 1)
//                    prelast = InputText.substring(InpLen - 2, InpLen - 1);
//                if (last.equals(".") && prelast.equals("0")) {
//                    String NewText = InputText.substring(0, InpLen - 2);
//                    InputTV.setText(NewText);
//                } else {
//                    String NewText = InputText.substring(0, InpLen - 1);
//                    InputTV.setText(NewText);
//                }
//                if (InpLen == 1)
//                    InputTV.setText("0");
//                if (ResultTV.getVisibility() == View.VISIBLE) {
//                    InputTV.setText("0");
//                    ResultTV.setVisibility(View.INVISIBLE);
//                }
//                break;
//        }
//    }

    public void onSignBtnClick(View v) {
        String ToAdd;
//        Button_pressed = findViewById(v.getId());
//        String buttonText = Button_pressed.getText().toString();
        int InpLen = InputTV.length();
        String InputText = InputTV.getText().toString();
        String last = InputText.substring(InpLen - 1, InpLen);
        switch (v.getId()) {
            case R.id.buttonRDiv:
                ToAdd = "/";
                break;
            case R.id.buttonMod:
                ToAdd = ":";
                break;
            default:
                ToAdd = buttonText;
                break;
        }
        if (ResultTV.getVisibility() == View.VISIBLE) {
            ResultTV.setVisibility(View.INVISIBLE);
            ToAdd = ResultTV.getText() + ToAdd;
            InputTV.setText(ToAdd);
            return;
        }
        if (!InputText.equals("0")) {
            if (isSign(last) && !ToAdd.equals("-")) {
                String Copied = InputText.substring(0, InpLen - 1) + ToAdd;
                InputTV.setText(Copied);
            } else if (last.equals(".") || last.equals(",")) {
                ToAdd = "0" + ToAdd;
                InputTV.append(ToAdd);
            } else if (ToAdd.equals("-"))
                InputTV.append(ToAdd);
            else
                InputTV.append(ToAdd);
        } else if (ToAdd.equals("-"))
            InputTV.setText(ToAdd);
    }

    public void onBtnClick(View v) {
        String ToAdd;
        int lastSign = 0;
        int InpLen = InputTV.length();
        String InputText = InputTV.getText().toString();
        String last = InputText.substring(InpLen - 1, InpLen);
        String ZeroStr = "0";
//        Button_pressed = findViewById(v.getId());
//        String buttonText = Button_pressed.getText().toString();
//        ToAdd = "f";
        if (buttonText.equals("."))
            ToAdd = ",";
        else
            ToAdd = buttonText;
        if (ResultTV.getVisibility() == View.VISIBLE) {
            ResultTV.setVisibility(View.INVISIBLE);
            if (buttonText.equals(".") || buttonText.equals(","))
                ToAdd = "0,";
            InputTV.setText(ToAdd);
            return;
        }
        switch (buttonText) {
            case ",":

            case ".":
                int i;
                for (i = InpLen - 1; i > 0; i--) {
                    if (isSign(String.valueOf(InputText.toCharArray()[i]))) {
                        lastSign = i;
                        break;
                    }
                }
                String lNum = InputText.substring(lastSign, InpLen);
                if (lNum.contains(".") || lNum.contains(","))
                    return;
                else if (isSign(last) && !InputText.equals(ZeroStr))
                    ToAdd = "0,";
                break;
            case "0":
                if (isSign(last))
                    ToAdd = "0,";
                break;
        }
        if (InputTV.getText().toString().equals(ZeroStr) && v.getId() != R.id.buttonDot)
            InputTV.setText("");
        InputTV.append(ToAdd);
//        InputTV.setText("kjc");
//        return;
    }

    public void onResult(View v) {
        String CountStr = InputTV.getText().toString();
        int CountLen = CountStr.length();
        String last = CountStr.substring(CountLen - 1, CountLen);
        if (last.equals(".") || last.equals(",") || isSign(last) || ResultTV.getVisibility() == View.VISIBLE)
            return;
        double result = 0;
        NumberFormat nf = new DecimalFormat("#.#######");
        int pr = 0;
        int nx = CountLen - 1;
        int i = 1;
        int signCount = 0;
        while (i < CountLen) {
            if (isSign(String.valueOf(CountStr.toCharArray()[i])) && !(String.valueOf(CountStr.toCharArray()[i]).equals("-") && isSign(String.valueOf(CountStr.toCharArray()[i - 1])))) {
                signCount++;
                i++;
            } else if (String.valueOf(CountStr.toCharArray()[i]).equals("-") && isSign(String.valueOf(CountStr.toCharArray()[i])))
                i += 2;
            else i++;
        }
        i = 1;
        while (signCount > 0) {
            if (((CountStr.toCharArray()[i] == '/') || (CountStr.toCharArray()[i] == ':') || (CountStr.toCharArray()[i] == '×') || (CountStr.toCharArray()[i] == '÷')) || (!containsUpClass(CountStr) && ((CountStr.toCharArray()[i] == '+') || (CountStr.toCharArray()[i] == '-')))) {
                for (int j = i - 1; j >= 0; j--) {
                    pr = j;
                    if (isSign(String.valueOf(CountStr.toCharArray()[j])) && j != 0) {
                        pr = j + 1;
                        break;
                    }
                }
                for (int j = i + 1; j < CountStr.length(); j++) {
                    nx = j;
                    if (isSign(String.valueOf(CountStr.toCharArray()[j])) ^ (String.valueOf(CountStr.toCharArray()[j]).equals("-") && isSign(String.valueOf(CountStr.toCharArray()[j - 1])))) {
                        nx = j - 1;
                        break;
                    }
                }
//                String lefty = CountStr.substring(pr, i);
                int dotl = CountStr.substring(pr, i).indexOf(",");
                StringBuilder lefty = new StringBuilder(CountStr.substring(pr, i));
                if (dotl !=-1)
                    lefty.setCharAt(dotl, '.');
                int dotr = CountStr.substring(i + 1, nx + 1).indexOf(",");
                StringBuilder righty = new StringBuilder(CountStr.substring(i + 1, nx + 1));
                if (dotr !=-1)
                    righty.setCharAt(dotr, '.');

//                String righty = ;
                double left = Double.parseDouble(String.valueOf(lefty));
                double right = Double.parseDouble(String.valueOf(righty));
                if (containsDiv(String.valueOf(CountStr.toCharArray()[i])) && (left == 0 || right == 0)) {
                    Boolean ZeroDiv = prefs.getBoolean("zero_div", false);
                    if (ZeroDiv)
                        ResultTV.setText(R.string.infinity);
                    else
                        ResultTV.setText(R.string.error);
                    ResultTV.setVisibility(View.VISIBLE);
                    return;
                }
                switch (CountStr.toCharArray()[i]) {
                    case '+':
                        result = left + right;
                        break;
                    case '-':
                        result = left - right;
                        break;
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
                }
                String pre = "";
                String aft = "";
                if (pr != 0)
                    pre = CountStr.substring(0, pr);
                if (nx != CountStr.length() - 1)
                    aft = CountStr.substring(nx + 1, CountStr.length());
                String cur = String.valueOf(result);
//                CountStr = pre + cur + aft;
                CountStr = cur;
                signCount--;
                i = 1;
                continue;
            }
            if (CountStr.toCharArray()[i] == ',')
                CountStr.toCharArray()[i] = '.';
            i++;
        }
//        if (String.valueOf(nf.format(Double.parseDouble(CountStr))).contains(",")) {
//            i = String.valueOf(nf.format(Double.parseDouble(CountStr))).indexOf(",");
//            String.valueOf(nf.format(Double.parseDouble(CountStr))).toCharArray()[i] = '.';
//            ResultTV.setText(CountStr);
//        } else
            ResultTV.setText(String.valueOf(nf.format(Double.parseDouble(CountStr))));
        ResultTV.setVisibility(View.VISIBLE);
    }

}
