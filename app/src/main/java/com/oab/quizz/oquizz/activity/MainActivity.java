package com.oab.quizz.oquizz.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oab.quizz.oquizz.R;
import com.oab.quizz.oquizz.model.Game;
import com.oab.quizz.oquizz.model.Level;
import com.oab.quizz.oquizz.model.Question;
import com.oab.quizz.oquizz.util.Constants;

public class MainActivity extends AppCompatActivity implements Game.IGameListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private Game currentGame;

    boolean questionInProgress = false;

    private TextView txtTitle;
    private TextView txtScore;
    private TextView txtOption1;
    private TextView txtOption2;
    private TextView txtOption3;
    private TextView txtOption4;
    private Button   btnNext;
    private ProgressBar pbQuestion;

    private Level difficulty;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        Log.i(TAG, "onCreate");
        currentGame = new Game(getApplicationContext());
    }

    private void initViews() {
        txtTitle = findViewById(R.id.txtTitle);
        txtScore = findViewById(R.id.txtScore);
        txtOption1 = findViewById(R.id.txtOption1);
        txtOption2 = findViewById(R.id.txtOption2);
        txtOption3 = findViewById(R.id.txtOption3);
        txtOption4 = findViewById(R.id.txtOption4);
        btnNext    = findViewById(R.id.btnNext);
        pbQuestion = findViewById(R.id.pbQuestion);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            difficulty = Level.fromValue(extras.getInt(Constants.EXTRA_DIFFICULTY));
            username = extras.getString(Constants.EXTRA_USERNAME);
        }
        startNewQuizz(difficulty);
    }

    private void startNewQuizz(Level difficulty) {
        currentGame.startQuizz(this, difficulty);
        txtScore.setText("0");
        txtOption1.setVisibility(View.VISIBLE);
        txtOption2.setVisibility(View.VISIBLE);
        txtOption3.setVisibility(View.VISIBLE);
        txtOption4.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.VISIBLE);
        pbQuestion.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onPause() {
        super.onPause();
        onClickAnswer(null);
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    public void onStartGame(View view) {
        Log.i(TAG, "onStartGame");
    }



    @Override
    public void onNewQuestion() {
        questionInProgress = true;
        Question currentQuestion = currentGame.getCurrentQuestion();
        if (currentQuestion == null) {
            Log.w(TAG, "onNewQuestion (nulle ?)");
            return;
        }
        Log.i(TAG, "onNewQuestion \n current Question:\n"
                + currentQuestion.getQuestion() + "\n");
        txtTitle.setText(currentQuestion.getQuestion());
        txtOption1.setText(currentQuestion.getOptions().get(0));
        txtOption1.setTextColor(Color.BLACK);
        txtOption2.setText(currentQuestion.getOptions().get(1));
        txtOption2.setTextColor(Color.BLACK);
        txtOption3.setText(currentQuestion.getOptions().get(2));
        txtOption3.setTextColor(Color.BLACK);
        txtOption4.setText(currentQuestion.getOptions().get(3));
        txtOption4.setTextColor(Color.BLACK);
        txtOption1.setEnabled(true);
        txtOption2.setEnabled(true);
        txtOption3.setEnabled(true);
        txtOption4.setEnabled(true);
        pbQuestion.setProgress(0);
        pbQuestion.setMax(Constants.QUESTION_DURATION_SEC);
    }

    @Override
    public void onQuestionEnded() {
        questionInProgress = false;
        Log.i(TAG, "Question ended!");
        txtScore.setText(String.valueOf(currentGame.getGameScore()));
        pbQuestion.setProgress(pbQuestion.getMax());
        _revealQuestion();
    }

    @Override
    public void onQuestionTick(long secondLeft) {
        int prog = (int) (Constants.QUESTION_DURATION_SEC - secondLeft);
        Log.i(TAG, "onQuestionTick remain:" + secondLeft + " setProgress:" + prog);
        pbQuestion.setProgress(prog);
    }

    @Override
    public void onGameEnded() {
        Log.i(TAG, "Game ended. Score:" + currentGame.getGameScore());
        txtTitle.setText("Game ended");
        txtScore.setText(String.valueOf(currentGame.getGameScore()));
        txtOption1.setVisibility(View.GONE);
        txtOption2.setVisibility(View.GONE);
        txtOption3.setVisibility(View.GONE);
        txtOption4.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        pbQuestion.setVisibility(View.GONE);
        goToScore();
    }

    private void goToScore() {
        Intent intent = new Intent(this, GameEndActivity.class);
        intent.putExtra(Constants.EXTRA_SCORE, currentGame.getGameScore());
        intent.putExtra(Constants.EXTRA_DIFFICULTY, difficulty.getValue());
        intent.putExtra(Constants.EXTRA_USERNAME, username);
        intent.putExtra(Constants.EXTRA_DURATION, currentGame.getDurationMs());

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void onClick(View view) {
        int clickId = view.getId();
        Log.i(TAG, "click on " + clickId);
        switch (clickId) {
            case R.id.btnNext:
                Log.i(TAG, "NEXT!");
                if (questionInProgress) {
                    onClickAnswer(null);
                } else if (currentGame.isRunning()) {
                     currentGame.startNextQuestion();
                }
                break;
            case R.id.txtOption1:
                Log.i(TAG, "Option1");
                onClickAnswer(txtOption1);
                break;
            case R.id.txtOption2:
                Log.i(TAG, "Option2");
                onClickAnswer(txtOption2);
                break;
            case R.id.txtOption3:
                Log.i(TAG, "Option3");
                onClickAnswer(txtOption3);
                break;
            case R.id.txtOption4:
                Log.i(TAG, "Option4");
                onClickAnswer(txtOption4);
                break;
        }
    }

    private void onClickAnswer(TextView txtAnswer) {
        String answer = txtAnswer != null ? txtAnswer.getText().toString() : null;
        _revealQuestion();
        currentGame.onAnswer(answer);
    }

    private void _revealQuestion() {
        Question currentQuestion = currentGame.getCurrentQuestion();
        if (currentQuestion == null) return; // game ended
        String correctAnswer = currentQuestion.getAnswer();
        setOptionAnswered(txtOption1, correctAnswer);
        setOptionAnswered(txtOption2, correctAnswer);
        setOptionAnswered(txtOption3, correctAnswer);
        setOptionAnswered(txtOption4, correctAnswer);
    }

    private void setOptionAnswered(TextView txtOption, String correctAnswer) {
        String answer = txtOption != null ? txtOption.getText().toString() : null;
        if (txtOption != null && answer != null && answer.equals(correctAnswer)) {
            txtOption.setTextColor(Color.GREEN);
        } else if (txtOption != null) {
            txtOption.setTextColor(Color.RED);
        }
        txtOption.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
