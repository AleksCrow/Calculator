package com.alexvoronkov.calcul;

import android.app.Activity;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.math.BigDecimal;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private static long back_pressed;
    TextView resultField;
    TextView numberField;
    String operField = "";
    BigDecimal operand = null;
    String lastOperation = "=";

    //метод для закрытия приложения кнопкой назад
    @Override
    public void onBackPressed() {
        if(back_pressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), (R.string.backExit), Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultField = (TextView) findViewById(R.id.resultField);
        numberField = (TextView) findViewById(R.id.numberField);
    }

    // обработка нажати¤ на числовую кнопку
    public void onNumberClick(View view) {

        Button button = (Button) view;
        button.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

        if (lastOperation.equals("=") && operand != null) {
            numberField.setText("");
            operand = null;
        }
        operField += button.getText().toString();
        numberField.append(button.getText());
    }

    // обработка нажати¤ на кнопку операции
    public void onOperationClick(View view) {

        Button button = (Button) view;
        button.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

        String op = button.getText().toString();
        String number = operField;
        if (number.length() > 0) {
            number = number.replace(',', '.');
            try {
                performOperation(Double.valueOf(number), op);
            } catch (NumberFormatException ex) {
                numberField.setText("");
            }
        }
        lastOperation = op;
        numberField.append(lastOperation);
    }

    public void onClearClick(View view) {
        Button button = (Button) view;
        button.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

        resultField.setText("");
        numberField.setText("");
        operField = "";
        lastOperation = "=";
        operand = null;
    }

    //метод для backspace
    public void onBackClick(View view) {
        Button button = (Button) view;
        button.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

        String text = numberField.getText().toString();
        if (text.length() > 0) {
            if (text.substring(0, text.length() - 1).equals(lastOperation)) {
                lastOperation = "";
            } else if (operField.length() > 0) {
                operField = operField.substring(0, operField.length() - 1);
                numberField.setText(text.substring(0, text.length() - 1));
            } else {
                numberField.setText(text.substring(0, text.length() - 1));
            }
        } else if (text.length() == 0) {
            numberField.setText("");
        }
    }

    //метод для нажатия на кнопку процент
    public void onAdditClick(View view) {
        Button button = (Button) view;
        button.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        String lastNumber = operField;
        if (lastNumber.length() > 0){
            try {
                lastNumber = String.valueOf(Double.parseDouble(lastNumber) / 100);
                percentOperation(Double.valueOf(lastNumber));
            } catch (NumberFormatException ex) {
                numberField.setText("");
            }
        }
        operField = lastNumber;
        numberField.append(button.getText().toString());
    }

    //метод для расчёта процентов
    private void percentOperation(Double number) {
        BigDecimal operandPercent;
        if (operand == null) {
            operand = BigDecimal.valueOf(number);
            operandPercent = BigDecimal.valueOf(number);
        } else {
            operandPercent = operand.multiply(BigDecimal.valueOf(number));
        }
        switch (lastOperation) {
            case "÷":
                if (number == 0) {
                    Toast toast = Toast.makeText(this, "Деление на ноль", Toast.LENGTH_SHORT);
                    toast.show();
                    resultField.setText("");
                    numberField.setText("");
                    operField = "";
                    operand = BigDecimal.valueOf(0);
                } else {
                    operand = operand.divide((operandPercent), 8, BigDecimal.ROUND_HALF_UP);
                }
                break;
            case "*":
                operand = operand.multiply(operandPercent);
                break;
            case "+":
                operand = operand.add(operandPercent);
                break;
            case "-":
                operand = operand.subtract(operandPercent);
                break;
        }
        resultField.setText(operType(operand.toString().replace(".", ",")));
        operField = "";
        lastOperation = "";
    }

    private void performOperation(Double number, String operation) {
        if (operand == null && lastOperation.equals("-")) {
            number = -number;
            operand = BigDecimal.valueOf(number);
        } else if (operand == null && !lastOperation.equals("√") && !lastOperation.equals("-")) {
            operand = BigDecimal.valueOf(number);
        } else {
            if (lastOperation.equals("=")){
                lastOperation = operation;
            }
            switch (lastOperation) {
                case "=":
                    operand = BigDecimal.valueOf(number);
                    break;
                case "÷":
                    if (number == 0) {
                        Toast toast = Toast.makeText(this, "Деление на ноль", Toast.LENGTH_SHORT);
                        toast.show();
                        resultField.setText("");
                        numberField.setText("");
                        operField = "";
                        operand = BigDecimal.valueOf(0);
                    } else {
                        operand = operand.divide(BigDecimal.valueOf(number), 8, BigDecimal.ROUND_HALF_UP);
                    }
                    break;
                case "*":
                    operand = operand.multiply(BigDecimal.valueOf(number));
                    break;
                case "+":
                    operand = operand.add(BigDecimal.valueOf(number));
                    break;
                case "-":
                    operand = operand.subtract(BigDecimal.valueOf(number));
                    break;
                case "√":
                    operand = BigDecimal.valueOf(Math.sqrt(number));
                    break;
            }
        }
        resultField.setText(operType(operand.toString().replace(".", ",")));
        operField = "";
    }

    //дл¤ отбрасывани¤ нул¤ с точкой если целое число
    private String operType(String text) {
        String number = "";
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            list.add(String.valueOf(text.charAt(i)));
        }
        listContains(list);
        for (String x : list) {
            number += x;
        }
        return number;
    }

    //проверка на наличие нулей после запятой и самой запятой
    public ArrayList listContains(ArrayList list) {
        if (list.contains(",")) {
            if (list.contains(",") && list.get(list.size() - 1).equals("0")) {
                list.remove(list.size() - 1);
            } else if (list.get(list.size() - 1).equals(",")) {
                list.remove(list.size() - 1);
            } else return list;
            listContains(list);
        }
        return list;
    }


    public void onClickAbout(View view) {  //метод для вызова окна о программе
        FragmentAbout about = new FragmentAbout();
        about.show(getFragmentManager(), "custom");
    }
}