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

    private static long back_pressed; //переменная для скорости нажатия кнопки назад
    TextView resultField; // текстовое поле дл¤ вывода результата
    TextView numberField;   // поле дл¤ ввода числа
    String operField = "";    // для формирования операндов
    BigDecimal operand = null;  // операнд операции
    String lastOperation = "="; // последн¤¤ операци¤

    @Override
    public void onBackPressed() {  //метод для закрытия приложения кнопкой назад
        if(back_pressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Для выхода из программы нажмите кнопку повторно", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // получаем все пол¤ по id из activity_main.xml
        resultField = (TextView) findViewById(R.id.resultField);
        numberField = (TextView) findViewById(R.id.numberField);
    }

    // обработка нажати¤ на числовую кнопку
    public void onNumberClick(View view) {

        Button button = (Button) view;
        button.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP); //для вибрации при нажатии на кнопу

        if (lastOperation.equals("=") && operand != null) {   //если последний оператор =, то очищаем поле при нажатии на следующую кнопку
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

        String op = button.getText().toString();  //добавляем оператор
        String number = operField;     //считываем второй операнд
        // если введенно что-нибудь
//        if (beComma(number)) {
        if (number.length() > 0) {
            number = number.replace(',', '.');
            try {
                performOperation(Double.valueOf(number), op);   //отправляем второй операнд на расчёт
            } catch (NumberFormatException ex) {
                numberField.setText("");
            }
        }
//        } else {
//            Toast.makeText(this, "Неверный формат числа", Toast.LENGTH_SHORT).show();
//        }
        lastOperation = op;
        numberField.append(lastOperation);   //добавляем в поле ввода последний знак
    }

    public void onClearClick(View view) {  //очистка
        Button button = (Button) view;
        button.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

        resultField.setText("");
        numberField.setText("");
        operField = "";
        lastOperation = "=";
        operand = null;
    }

    public void onBackClick(View view) {  //метод для backspace
        Button button = (Button) view;
        button.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP); //для вибрации при нажатии на кнопу

        String text = numberField.getText().toString();  //считываем данные из numberField
        if (text.length() > 0) {
            if (text.substring(0, text.length() - 1).equals(lastOperation)) {  //проверка на наличие оператора
                lastOperation = "";
            } else if (operField.length() > 0) {      //проверка на наличие второго операнда и отнятие у него последнего знака
                operField = operField.substring(0, operField.length() - 1);
                numberField.setText(text.substring(0, text.length() - 1));
            } else {
                numberField.setText(text.substring(0, text.length() - 1));   //просто отнимаем последний знак
            }
        } else if (text.length() == 0) {  //если нет знаков, то очищаем поле, чтоб не было ошибки
            numberField.setText("");
        }
    }

    public void onAdditClick(View view) {  //метод для нажатия на кнопку процент
        Button button = (Button) view;
        button.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); //для вибрации при нажатии на кнопу

        String lastNumber = operField;
        if (lastNumber.length() > 0){
            try {
                lastNumber = String.valueOf(Double.parseDouble(lastNumber) / 100);   //отправляем второй операнд на расчёт в виде процентов
                percentOperation(Double.valueOf(lastNumber));
            } catch (NumberFormatException ex) {
                numberField.setText("");
            }
        }
        operField = lastNumber;
        numberField.append(button.getText().toString());
    }

    private void percentOperation(Double number) {  //метод для расчёта процентов
        BigDecimal operandPercent;
        if (operand == null) {
            operand = BigDecimal.valueOf(number);
            operandPercent = BigDecimal.valueOf(number); //если нет второго числа, то проценты берутся из первого
        } else {
            operandPercent = operand.multiply(BigDecimal.valueOf(number));  //высчитываем процент из первого числа
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

        // если операнд ранее не был установлен (при вводе самой первой операции)
        if (operand == null && lastOperation.equals("-")) {
            number = -number;
            operand = BigDecimal.valueOf(number);
        } else if (operand == null && !lastOperation.equals("√") && !lastOperation.equals("-")) {  //!lastOperation.equals("√") добавлено, чтобы правильно высчитывался первый корень после запуска программы
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

    private String operType(String text) {  //дл¤ отбрасывани¤ нул¤ с точкой если целое число
        String number = "";
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            list.add(String.valueOf(text.charAt(i)));
        }
        listContains(list);  //проверка на наличие нулей после запятой и самой запятой, отбрасывание ненужного
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

//    public boolean beComma(String text){
//        Pattern p = Pattern.compile(".+[,]?");
//        return p.matcher(text).matches();
//    }

    public void onClickAbout(View view) {  //метод для вызова окна о программе
        FragmentAbout about = new FragmentAbout();
        about.show(getFragmentManager(), "custom");
    }
}
