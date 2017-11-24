package com.oab.quizz.oquizz.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oab.quizz.oquizz.R;
import com.oab.quizz.oquizz.manager.LoadDataListener;
import com.oab.quizz.oquizz.manager.QuizzDataManager;
import com.oab.quizz.oquizz.model.Level;
import com.oab.quizz.oquizz.util.Constants;

import java.io.File;

public class WelcomeActivity extends AppCompatActivity implements LoadDataListener {
    private final static String TAG = WelcomeActivity.class.getSimpleName();

    private TextView txtWelcome;
    private Button btnStart;
    private RadioGroup rgrpGameDiff;
    private ProgressBar pbLoadData;
    private RelativeLayout pbLayout;
    private LinearLayout logonLayout;
    private TextInputEditText inputUsername;
    private LinearLayout wFragment;

    private boolean validUser = false;
    private boolean validDiff = false;

    private void initViews() {
        pbLayout     = findViewById(R.id.pbLayout);
        pbLoadData   = findViewById(R.id.pbLoadData);

        txtWelcome   = findViewById(R.id.txtWelcome);
        logonLayout  = findViewById(R.id.logonLayout);
        inputUsername= findViewById(R.id.inputUsername);
        rgrpGameDiff = findViewById(R.id.rgrpGameDiff);
        btnStart     = findViewById(R.id.btnStart);
        wFragment    = findViewById(R.id.wFragment);

        // username valid après saisie ?
        inputUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onUsernameChanged(s);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // remplir username sur double tap
        inputUsername.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(
                    WelcomeActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    inputUsername.setText("Jojo");
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        // difficulté valide après saisie ?
        rgrpGameDiff.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                validDiff = true;
                onFormChanged();
            }
        });

        int orientation = getResources().getConfiguration().orientation;
        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                wFragment.setVisibility(View.GONE);
                setTxtWelcomeWeight(2.0f);
                break;
            default: // Configuration.ORIENTATION_PORTRAIT:
                wFragment.setVisibility(View.VISIBLE);
                setTxtWelcomeWeight(4.0f);
                break;
        }
    }

    private void setTxtWelcomeWeight(float weight) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                weight
        );
        txtWelcome.setLayoutParams(param);
    }

    private void onUsernameChanged(CharSequence s) {
        validUser = !(s == null || s.toString().trim().length()==0);
        onFormChanged();
    }

    private void onFormChanged() {
        boolean formValid = validUser && validDiff;
        btnStart.setEnabled(formValid);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initViews();
        // _storageSharedPreference();
        // _storageFile();
        // _storageSQLite();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onFormChanged();
        _loadData(true);
        pbLoadData.setProgress(0);
        pbLoadData.setMax(100);
        QuizzDataManager.getInstance()
                // .loadDataFromFile(getApplicationContext(), this);
                .loadDataFromWebService(getApplicationContext(), this);
    }

    public void onDataLoaded(int percentProgress) {
        pbLoadData.setProgress(percentProgress);
        if (percentProgress == 100) {
            _loadData(false);
        }
    }


    public void onClick(View view) {
        int clickId = view.getId();
        String username = inputUsername.getText().toString();
        Log.i(TAG, "click on " + clickId);
        switch (clickId) {
            case R.id.btnScore:
                Intent mainIntent = new Intent(this, ScoreListActivity.class);
                mainIntent.putExtra(Constants.EXTRA_USERNAME, username);
                startActivity(mainIntent);
                return;
            case R.id.btnStart:
                Level level;
                int rgrpDiffId = rgrpGameDiff.getCheckedRadioButtonId();
                switch(rgrpDiffId) {
                    case R.id.radioEasy:   level = Level.BEGINNER; break;
                    case R.id.radioMedium: level = Level.INTERMEDIATE; break;
                    case R.id.radioHard:
                    default:
                            level = Level.EXPERT; break;
                }
                onStartGame(level, username);
                break;
        }
    }

    private void onStartGame(Level level, String username) {
        if (level == null) {
            Toast.makeText(this, "Veuillez choisir une difficulté", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, "START! diff:" + level.toString());
        Intent mainIntent = new Intent(this, MainActivity.class);
        // mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // mainIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mainIntent.putExtra(Constants.EXTRA_DIFFICULTY, level.getValue());
        mainIntent.putExtra(Constants.EXTRA_USERNAME, username);
        startActivity(mainIntent);
        finish();
    }


    private void _loadData(boolean inProgress) {
        pbLayout.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        logonLayout.setVisibility(inProgress ? View.GONE : View.VISIBLE);
    }
    /**
     * base de données
     */
    private void _storageSQLite() {
        // PACKAGE_DIR/databases/filename.db

        // partage de données entre application : faire un contentProvider
    }

    /**
     * requis
     * <uses-permision android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     */
    private void _storageFile() {
        File cacheDir = getCacheDir(); // fichier temporaire
        getFilesDir(); // fichier applicatif

        // création d'un dossier
        getDir("dirname", MODE_WORLD_READABLE);
            // remplacé par un XML file provider / path à partager

        // getExternalFilesDir(Environment.DIRECTORY_PICTURES); // hors du répertoire applicatif
        // Environment.getExternalStorageDirectory();

        /*
        getDir("dirname", MODE_PRIVATE);
        FileOutputStream filename = openFileOutput("filename", MODE_PRIVATE);
        filename.write("dd".getBytes());
        filename.close();
        */
    }

    private void _storageSharedPreference() {
        // shared preference (fichier)
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = settings.edit();
        edit.putString("YYead","DDDD");
        edit.commit();
    }

}
