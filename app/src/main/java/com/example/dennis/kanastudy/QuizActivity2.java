package com.example.dennis.kanastudy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dennis on 17/04/15.
 */
public class QuizActivity2 extends Activity {
    private Map<String, String[]> kanaMap;
    protected int quizType;
    protected boolean easy;
    protected boolean voiced;
    private TextView score;
    private TextView question;
    private int attempted = 0;
    private int correctAnswers = 0;
    private EditText editText;
    private static Toast answerPrompt;
    private String ans;
    private String prevAns = "";
    protected Object[] keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.quiz2_layout);

        Intent activityQuizType = getIntent();
        quizType = activityQuizType.getExtras().getInt("QuizType");
        easy = activityQuizType.getExtras().getBoolean("EasyMode");
        voiced = activityQuizType.getExtras().getBoolean("IncludeVoiced");

        //get buttons and question label from the view
        question = (TextView)findViewById(R.id.questionLabel);
        score = (TextView)findViewById(R.id.scoreLabel);
        editText = (EditText) findViewById(R.id.editText);

        //load map
        kanaMap = new kanaMap(this.getApplicationContext(), voiced).getMap();
        //load keys
        keys = kanaMap.keySet().toArray();

        newQuestion();
    }
    public void newQuestion(){
        // get random syllable for the question
        Random rand = new Random();
        do {
            ans = keys[rand.nextInt(kanaMap.size())].toString();
        } while (prevAns.equals(ans));
        prevAns = ans;

        question.setText(kanaMap.get(ans)[quizType]);

        if(easy){
            score.setText("");
        } else {
            score.setText(correctAnswers + " / " + attempted);
        }


        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //when input happens
                    onAnswer(editText.getText().toString());
                    editText.setText(""); //clear the text field
                    return true;
                }
                return false;
            }
        });
    }
    public void onAnswer(String a) {
        if (answerPrompt != null) // if there is a response being shown from previous question...
            answerPrompt.cancel(); // cancel it

        String[] answer = kanaMap.get(ans);
        boolean correct = false;
        String promptText = "?";
        // filter user input
        Pattern inputp = Pattern.compile("^([a-zA-Z\\-]+)");
        Matcher m1 = inputp.matcher(a);
        while(m1.find()){
            a = m1.group(1).toLowerCase();
        }

        // check if answer is correct
        if(a.trim().equals(ans)){
            correct = true;
        }


        if(!easy) { //regular mode
            if(correct){
                promptText = "Correct! :)";
                correctAnswers = correctAnswers + 1;
            } else {
                promptText = "False. :(";
            }
            attempted = attempted + 1;
            answerPrompt = Toast.makeText(getBaseContext(), promptText, Toast.LENGTH_SHORT);
            answerPrompt.show();
            newQuestion();
        } else { //easy mode
            if(correct){
                newQuestion();
            }
        }
    }

}
