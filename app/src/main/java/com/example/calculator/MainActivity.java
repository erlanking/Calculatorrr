package com.example.calculator;

import android.os.Bundle;
import android.util.Log;
import java.text.DecimalFormat;

import android.view.View;
import android.widget.TextView;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvExpression, tvResult;
    MaterialButton button_zero, button_one, button_two, button_three, button_four, button_five, button_six, button_seven, button_eight, button_nine;
    MaterialButton button_dot, button_plus, button_minus, button_div, button_multiply;
    MaterialButton button_open, button_back, button_equals, button_c, button_ac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvResult = findViewById(R.id.tvResult);
        tvExpression = findViewById(R.id.tvExperssion);

        // Инициализация кнопок
        initButton(button_zero, R.id.button_zero);
        initButton(button_one, R.id.button_one);
        initButton(button_two, R.id.button_two);
        initButton(button_three, R.id.button_three);
        initButton(button_four, R.id.button_four);
        initButton(button_five, R.id.button_five);
        initButton(button_six, R.id.button_six);
        initButton(button_seven, R.id.button_seven);
        initButton(button_eight, R.id.button_eight);
        initButton(button_nine, R.id.button_nine);
        initButton(button_dot, R.id.button_dot);
        initButton(button_ac, R.id.button_ac);
        initButton(button_c, R.id.button_c);
        initButton(button_equals, R.id.button_equals);
        initButton(button_div, R.id.button_div);
        initButton(button_multiply, R.id.button_multiply);
        initButton(button_plus, R.id.button_plus);
        initButton(button_minus, R.id.button_minus);
        initButton(button_open, R.id.button_open);
        initButton(button_back, R.id.button_back);
    }

    void initButton(MaterialButton button, int id) {
        button = findViewById(id);
        button.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        MaterialButton button = (MaterialButton) v;
        String btnText = button.getText().toString();
        String data = tvExpression.getText().toString();

        // Обработка кнопки AC
        if (btnText.equals("AC")) {
            tvExpression.setText("");
            tvResult.setText("");
            return;
        }

        // Обработка кнопки C
        if (btnText.equals("C")) {
            if (data.length() != 0 && !data.equals("0")) {
                data = data.substring(0, data.length() - 1);
            } else {
                data = "0";
            }
            tvExpression.setText(data);
            return;
        }

        // Предотвращение дублирования знаков
        if (isOperator(btnText)) {
            if (data.isEmpty() || isOperator(data.charAt(data.length() - 1) + "")) {
                return;
            }
        }

        // Удаление начального нуля, если вводится число
        if (data.equals("0") && !isOperator(btnText)) {
            data = "";
        }

        // Обработка кнопки "="
        if (btnText.equals("=")) {
            if (!isValidExpression(data)) {
                tvResult.setText("Error");
                return;
            }

            String finalResult = evaluateExpression(data);
            if (!finalResult.equals("Error")) {
                tvExpression.setText(tvResult.getText());
                tvResult.setText(finalResult);
            }
            return;
        }


        data += btnText;
        tvExpression.setText(data);


        if (!data.equals("") && isValidExpression(data)) {
            String finalResult = evaluateExpression(data);
            if (!finalResult.equals("Error")) {
                tvResult.setText(finalResult);
            }
        }
    }

    // Проверка на оператор
    private boolean isOperator(String input) {
        return input.equals("+") || input.equals("-") || input.equals("*") || input.equals("/");
    }

    // Проверка корректности выражения (парность скобок, отсутствие ошибок)
    private boolean isValidExpression(String expression) {
        int openBrackets = 0;

        for (int i = 0; i < expression.length(); i++) {
            char currentChar = expression.charAt(i);

            // Проверка на парность скобок
            if (currentChar == '(') {
                openBrackets++;
            } else if (currentChar == ')') {
                openBrackets--;
                if (openBrackets < 0) {
                    return false;
                }
            }

            // Проверка, чтобы не было двух операторов подряд
            if (i > 0 && isOperator(expression.charAt(i) + "") && isOperator(expression.charAt(i - 1) + "")) {
                return false;
            }
        }

        return openBrackets == 0;
    }

    private String evaluateExpression(String expression) {
        Context rhino = Context.enter();
        rhino.setOptimizationLevel(-1);
        String result;
        try {
            Scriptable scope = rhino.initStandardObjects();
            result = rhino.evaluateString(scope, expression, "JavaScript", 1, null).toString();

            DecimalFormat decimalFormat = new DecimalFormat("#.###");
            return decimalFormat.format(Double.parseDouble(result));
        } catch (Exception e) {
            result = "Error";
        } finally {
            Context.exit();
        }
        return result;
    }
}
