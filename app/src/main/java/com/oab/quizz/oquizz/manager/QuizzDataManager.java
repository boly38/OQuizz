package com.oab.quizz.oquizz.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;
import com.oab.quizz.oquizz.R;
import com.oab.quizz.oquizz.model.Level;
import com.oab.quizz.oquizz.model.Question;
import com.oab.quizz.oquizz.model.QuizzQuestions;
import com.oab.quizz.oquizz.util.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuizzDataManager {
    private static final QuizzDataManager ourInstance = new QuizzDataManager();

    private static boolean dataLoaded = false;
    private static int wsSuccess = 0;
    private static List<Question> qSimple = null;
    private static List<Question> qNormal = null;
    private static List<Question> qExpert = null;

    public static QuizzDataManager getInstance() {
        return ourInstance;
    }

    private static final int LOADED_KEY = 1234;
    private Handler dataLoaderHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOADED_KEY) {
                if (dataClient != null) {
                    // execute client callback
                    dataClient.onDataLoaded((int)msg.obj);
                }
            }
        }
    };
    private LoadDataListener dataClient = null;


    private QuizzDataManager() {
    }

    public List<Question> loadQuestions(Context context, Level difficulty) {

        int resourceId = R.raw.quizz_expert;
        switch (difficulty) {
            case BEGINNER:
                resourceId = R.raw.quizz_simple;
                break;
            case INTERMEDIATE:
                resourceId = R.raw.quizz_normal;
                break;
        }

        Gson gson = new Gson();

        /*
        String jsonString = loadRawResourceAsString(context, resourceId);
        QuizzQuestions qData = gson.fromJson(jsonString, QuizzQuestions.class);
        */

        InputStream is = context.getResources().openRawResource(resourceId);
        Reader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            QuizzQuestions qData = gson.fromJson(reader, QuizzQuestions.class);
            return qData.getQuestions();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String loadRawResourceAsString(Context context, int resourceId) {
        InputStream is = context.getResources().openRawResource(resourceId);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writer.toString();
    }

    public void loadDataFromFile(final Context context, final LoadDataListener dataClient) {
        this.dataClient = dataClient;
        // skip load thread if data already here
        if (dataLoaded == true) {
            dataClient.onDataLoaded(100);
            return;
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                sendProgress(0);
                Message progressMsg;
                qSimple = loadQuestions(context, Level.BEGINNER);
                sendProgress(10);
                qNormal = loadQuestions(context, Level.INTERMEDIATE);
                sendProgress(20);
                qExpert = loadQuestions(context, Level.EXPERT);
                sendProgress(30);
                for (int i = 30; i<= 100; i+=10)
                try {
                    Thread.sleep(300);
                    sendProgress(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dataLoaded = true;
            }

            private void sendProgress(int progress) {
                Message progressMsg = dataLoaderHandler.obtainMessage(LOADED_KEY, progress);
                progressMsg.sendToTarget();
            }
        });
        thread.start();
    }

    public void loadDataFromWebService(final Context context, final LoadDataListener dataClient) {
        this.dataClient = dataClient;
        // skip load thread if data already here
        if (dataLoaded == true) {
            dataClient.onDataLoaded(100);
            return;
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 10; i++) {
                    try {
                        Thread.sleep(50);
                        sendProgress(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                _wsContinue(context, dataClient);
            }
            private void sendProgress(int progress) {
                Message progressMsg = dataLoaderHandler.obtainMessage(LOADED_KEY, progress);
                progressMsg.sendToTarget();
            }
        });
        thread.start();

    }

    private void _wsContinue(Context context, LoadDataListener dataClient) {
        _wsLoadQuizz(context, "simple");
        _wsLoadQuizz(context, "normal");
        _wsLoadQuizz(context, "expert");
    }

    public void _wsLoadQuizz(final Context context, final String difficulty) {
        List<String> allowedDiff = Arrays.asList("simple", "normal", "expert");
        if (!allowedDiff.contains(difficulty)) {
            throw new InvalidParameterException("Invalid difficulty" + difficulty);
        }
        if ("expert".equals(difficulty)) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.APIARY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IApiaryMock service = retrofit.create(IApiaryMock.class);
        service.getQuizz(difficulty).enqueue(new Callback<QuizzQuestions>() {
            @Override
            public void onResponse(Call<QuizzQuestions> call, Response<QuizzQuestions> response) {
                _wsOnResult(difficulty, (response.body()).getQuestions());
            }

            @Override
            public void onFailure(Call<QuizzQuestions> call, Throwable t) {
                Toast.makeText(context,
                        "Unable to load " + difficulty
                                + " : " + t.getMessage(),
                         Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void _wsOnResult(String difficulty, ArrayList<Question> questions) {
        if ("simple".equals(difficulty)) {
            qSimple = questions;
        } else if ("normal".equals(difficulty)) {
            qNormal = questions;
        } else if ("expert".equals(difficulty)) {
            qExpert = questions;
        }
        this.wsSuccess += 1;
        int percent = this.wsSuccess * 33;
        if (percent == 99) {
            percent = 100;
            dataLoaded = true;
        }
        dataClient.onDataLoaded(percent);
    }
}
