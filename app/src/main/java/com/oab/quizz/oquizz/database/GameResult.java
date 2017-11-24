package com.oab.quizz.oquizz.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.oab.quizz.oquizz.database.Converters;
import com.oab.quizz.oquizz.model.Level;

import java.util.Date;


@Entity(tableName = "results")
public class GameResult {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameResult that = (GameResult) o;

        if (score != that.score) return false;
        if (durationMs != that.durationMs) return false;
        if (!pseudo.equals(that.pseudo)) return false;
        return level == that.level;
    }

    @Override
    public int hashCode() {
        int result = pseudo.hashCode();
        result = 31 * result + (int) (score ^ (score >>> 32));
        result = 31 * result + (int) (durationMs ^ (durationMs >>> 32));
        result = 31 * result + level.hashCode();
        return result;
    }

    @PrimaryKey(autoGenerate = true)
    int id;

    String pseudo;

    long score;

    @ColumnInfo(name = "duration")
    long durationMs;

    @TypeConverters(Converters.class)
    @ColumnInfo(name = "date")
    Date creationDate;

    @TypeConverters(Converters.class)
    Level level;

    @Override
    public String toString() {
        return "GameResult{" +
                "pseudo='" + pseudo + '\'' +
                ", score=" + score +
                ", durationMs=" + durationMs +
                ", creationDate=" + creationDate +
                ", level=" + level +
                '}';
    }

    public GameResult() {} // Entity require empty constructor

    public GameResult(long score, String username, Level level, long durationMs) {
        this.pseudo = username;
        this.score = score;
        this.durationMs = durationMs;
        this.creationDate = new Date();
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
