package com.oab.quizz.oquizz.database;

import android.arch.persistence.room.TypeConverter;

import com.oab.quizz.oquizz.model.Level;

import java.util.Date;

public class Converters {
    @TypeConverter
    public Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public Long dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        } else {
            return date.getTime();
        }
    }

    @TypeConverter
    public int fromLevel(Level level) {
        return level != null ? level.getValue() : Level.BEGINNER.getValue();
    }

    @TypeConverter
    public Level toLevel(int val) {
        return Level.fromValue(val);
    }
}
