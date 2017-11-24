package com.oab.quizz.oquizz.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {GameResult.class}, version = 1)
public abstract class QuizzDatabase extends RoomDatabase{
    public static final String DB_NAME = "oquizz";

    public abstract GameResultDao getResultDao();

}
