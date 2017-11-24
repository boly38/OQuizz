package com.oab.quizz.oquizz.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oab.quizz.oquizz.R;
import com.oab.quizz.oquizz.database.DatabaseManager;
import com.oab.quizz.oquizz.database.GameResult;
import com.oab.quizz.oquizz.fragment.BestScoreFragment;
import com.oab.quizz.oquizz.manager.PreferencesManager;
import com.oab.quizz.oquizz.model.Level;
import com.oab.quizz.oquizz.util.Constants;

public class GameEndActivity extends AppCompatActivity {
    private final static String TAG = GameEndActivity.class.getSimpleName();
    private TextView txtGEScore;
    private Button btnRestart;

    private long score;
    private String username;
    private Level difficulty;

    private boolean gameSaved = false;
    private long durationMs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);
        initViews();
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            onRestart();
            return;
        }
        score = extras.getLong(Constants.EXTRA_SCORE);
        username = extras.getString(Constants.EXTRA_USERNAME);
        difficulty = Level.fromValue(extras.getInt(Constants.EXTRA_DIFFICULTY));
        durationMs = extras.getLong(Constants.EXTRA_DURATION);

        if (score == 0) {
            txtGEScore.setText(getString(R.string.score_null, username));
            return;
        }
        txtGEScore.setText(getString(R.string.score_user, username, score));
        _addBestScoreFragment();
    }
    private void initViews() {
        txtGEScore = findViewById(R.id.txtGEScore);
        btnRestart = findViewById(R.id.btnRestart);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!gameSaved) {
            PreferencesManager.updateHighScore(this, score);
            GameResult gameRes = new GameResult(score, username, difficulty, durationMs);
            DatabaseManager.getInstance(getApplicationContext()).saveGameResult(gameRes);
            Log.i(TAG, "Result saved: " + gameRes);
            gameSaved = true;
        }
    }

    private void _addBestScoreFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BestScoreFragment bestScoreFragment = BestScoreFragment.newInstance();
        fragmentTransaction.add(R.id.fragment_container, bestScoreFragment);
        fragmentTransaction.commit();
    }


    public void onClick(View view) {
        int clickId = view.getId();
        Log.i(TAG, "click on " + clickId);
        switch (clickId) {
            case R.id.btnQuit:
                Log.i(TAG, "quit");
                finish();
                break;
            case R.id.btnRestart:
                Log.i(TAG, "restart");
                startNewQuizz();
                break;
        }
    }

    private void startNewQuizz() {
        // SO SO EASY // super.onBackPressed();
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        // supprimer l'activité
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (score == 0) {
            return super.onCreateOptionsMenu(menu);
        }
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.mnu_share:
                String shareMsg = getString(R.string.score_share, username, score);
                if (score == 0) {
                    Toast.makeText(this, "Vous n'allez pas partager un score de 0 !", Toast.LENGTH_SHORT).show();
                    return true;
                }
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMsg);
                if (shareIntent.resolveActivity(getPackageManager()) == null) {
                    Toast.makeText(this, "Impossible chez vous !", Toast.LENGTH_SHORT).show();
                    return true;
                }
                startActivity(shareIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
