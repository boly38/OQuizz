package com.oab.quizz.oquizz.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.oab.quizz.oquizz.R;
import com.oab.quizz.oquizz.adapter.GameResultAdapter;
import com.oab.quizz.oquizz.database.DatabaseManager;
import com.oab.quizz.oquizz.database.GameResult;
import com.oab.quizz.oquizz.model.Level;

import java.util.Arrays;
import java.util.List;

public class ScoreListActivity extends AppCompatActivity implements DatabaseManager.IGameResultListener {
    private RecyclerView rvScoreList;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private void initView() {
        rvScoreList = findViewById(R.id.rvScoreList);
        rvScoreList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        List<GameResult> rezz = Arrays.asList(new GameResult(111, "eee", Level.BEGINNER, 1122));
        DatabaseManager.getInstance(getApplicationContext()).getGameResults(this);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_list);
        initView();
    }


    @Override
    public void onGameResultRetrieved(List<GameResult> results) {
        mAdapter = new GameResultAdapter(results);
        rvScoreList.setAdapter(mAdapter);
    }
}
