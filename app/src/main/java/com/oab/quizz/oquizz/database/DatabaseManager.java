package com.oab.quizz.oquizz.database;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

public class DatabaseManager {
    private static DatabaseManager ourInstance;
    private final QuizzDatabase db;

    public static DatabaseManager getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new DatabaseManager(context);
        }
        return ourInstance;
    }

    private DatabaseManager(Context context) {
        db = Room.databaseBuilder(context,
                QuizzDatabase.class, QuizzDatabase.DB_NAME).build();
    }

    public QuizzDatabase getDb() {
        return db;
    }

    public void saveGameResult(final GameResult gameRes) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.getResultDao().insertResult(gameRes);
            }
        }).start();
    }

    public void getGameResults(final IGameResultListener listener) {
        (new AsyncTask<Void, Void, List<GameResult>>() {

            @Override
            protected List<GameResult> doInBackground(Void... voids) {
                return getDb().getResultDao().getAll();
            }

            @Override
            protected void onPostExecute(List<GameResult> gameResults) {
                if (listener != null) {
                    listener.onGameResultRetrieved(gameResults);
                }
            }
        }).execute();
    }

    public void deleteGameResult(final GameResult gameRes) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.getResultDao().deleteResult(gameRes);
            }
        }).start();
    }

    public interface IGameResultListener {
        public void onGameResultRetrieved(List<GameResult> results);
    }
}
