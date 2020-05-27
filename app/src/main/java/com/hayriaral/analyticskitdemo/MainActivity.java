package com.hayriaral.analyticskitdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;
import static com.huawei.hms.analytics.type.HAEventType.*;
import static com.huawei.hms.analytics.type.HAParamType.*;

public class MainActivity extends AppCompatActivity {

    private char operations[] = {'*', '+', '-'};
    private String question;
    private String result;
    private TextView txtQuestion;
    private EditText editInput;
    private String input;
    private Button btnCheck;
    private Button btnSubmit;
    private int counter = 0;
    private int score = 0;

    //Declaration of Analytics Instance
    HiAnalyticsInstance instance;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public char getOperation() {
        return operations[new Random().nextInt(3)];
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public int getScore() {
        return score;
    }

    public void boostScore() {
        this.score += 10;
    }

    public int getCounter() {
        return counter;
    }

    public void boostCounter() {
        this.counter++;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Enable Analytics Kit Log
        HiAnalyticsTools.enableLog();
        //Generate the Analytics Instance
        instance = HiAnalytics.getInstance(this);

        setContent();
    }

    private String createQuestion() {
        Random r = new Random();
        int number1 = r.nextInt(10 + 1); //[1...10]
        int number2 = r.nextInt(10 + 1);
        char operation = getOperation();
        setQuestion(number1 + " " + operation + " " + number2 + " =");
        createResult(number1, number2, operation);
        boostCounter();
        return getQuestion();
    }

    private void createResult(int number1, int number2, char operation) {
        switch (operation) {
            case '*':
                setResult(Integer.toString(number1 * number2));
                break;
            case '+':
                setResult(Integer.toString(number1 + number2));
                break;
            case '-':
                setResult(Integer.toString(number1 - number2));
                break;
            default:
                setResult("");
                break;
        }
    }

    public void setContent() {
        txtQuestion = findViewById(R.id.txtQuestion);
        txtQuestion.setText(createQuestion());

        btnCheck = findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editInput = findViewById(R.id.editInput);
                setInput(editInput.getText().toString().trim());
                checkAnswer();
                editInput.getText().clear();
            }
        });

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitScore();
            }
        });
        btnSubmit.setClickable(false);
    }

    private void nextQuestion() {
        txtQuestion.setText(createQuestion());
    }

    private void checkAnswer() {
        if (getInput().equals(getResult())) {
            boostScore();
            answerEvent("correct");
            Toast.makeText(this, "The answer is correct!", Toast.LENGTH_SHORT).show();
        } else {
            answerEvent("wrong");
            Toast.makeText(this, "The answer is wrong!", Toast.LENGTH_SHORT).show();
        }
        if (getCounter() >= 5) {
            btnCheck.setClickable(false);
            btnSubmit.setClickable(true);
            txtQuestion.setText("");
        } else {
            nextQuestion();
        }
    }

    private void submitScore() {
        //Parameter definition
        Bundle bundle = new Bundle();
        bundle.putLong(SCORE, getScore());

        //Reporting event
        instance.onEvent(SUBMITSCORE, bundle);

        Toast.makeText(this, "WELL DONE!", Toast.LENGTH_SHORT).show();
    }

    private void answerEvent(String answer){
        //Parameter definitions
        Bundle bundle = new Bundle();
        bundle.putString("question", getQuestion());
        bundle.putString("answer",answer);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        bundle.putString("time", dateFormat.format(new Date()));

        //Reporting event
        instance.onEvent("checkingAnswer", bundle);
    }
}
