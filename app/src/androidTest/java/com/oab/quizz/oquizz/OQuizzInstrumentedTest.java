package com.oab.quizz.oquizz;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.oab.quizz.oquizz.database.DatabaseManager;
import com.oab.quizz.oquizz.database.GameResult;
import com.oab.quizz.oquizz.model.Level;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class OQuizzInstrumentedTest {
    private static final String USER_TESTU = "testu";

    @After
    public void cleanTestData() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        final DatabaseManager instance = DatabaseManager.getInstance(appContext);
        instance.getGameResults(new DatabaseManager.IGameResultListener() {
            @Override
            public void onGameResultRetrieved(List<GameResult> results) {
                for (GameResult gr : results) {
                    if (USER_TESTU.equals(gr.getPseudo())) {
                        instance.deleteGameResult(gr);
                    }
                }
            }
        });
    }

    @Test
    public void should_db_works() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.oab.quizz.oquizz", appContext.getPackageName());

        // GIVEN
        DatabaseManager instance = DatabaseManager.getInstance(appContext);
        final GameResult grA = getGameResult(222);
        final GameResult grB = getGameResult(232);
        final List<GameResult> expectedResult = Arrays.asList(grA, grB);

        //WHEN
        instance.saveGameResult(grA);
        instance.saveGameResult(grB);

        Thread.sleep(1000);

        // THEN
        instance.getGameResults(new DatabaseManager.IGameResultListener() {
            @Override
            public void onGameResultRetrieved(List<GameResult> results) {
                assertNotNull(results);
                for (GameResult gr : expectedResult) {
                    Log.i("TESTU", gr.toString());
                    assertTrue(results.contains(gr));
                }
            }
        });
        Thread.sleep(1000);
    }

    private GameResult getGameResult(int score) {
        return new GameResult(score, USER_TESTU, Level.BEGINNER,1111);
    }
}
