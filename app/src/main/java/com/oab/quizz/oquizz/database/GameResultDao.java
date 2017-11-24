package com.oab.quizz.oquizz.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface  GameResultDao {

    @Insert
    void insertResult(GameResult rez);

    @Delete
    void deleteResult(GameResult gameRes);

    @Query("SELECT * FROM results ORDER BY score DESC")
    List<GameResult> getAll();

}
