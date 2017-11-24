package com.oab.quizz.oquizz.model;

import android.content.Context;
import android.os.CountDownTimer;

import com.oab.quizz.oquizz.activity.MainActivity;
import com.oab.quizz.oquizz.manager.QuizzDataManager;
import com.oab.quizz.oquizz.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Game {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final Context context;

    private long gameScore;
    private String label;
    private List<Question> questions = new ArrayList<Question>();
    private Level level;
    private int index;
    private long startQuestionTime;
    private long durationMs;
    private boolean isRunning;

    private CountDownTimer cdtimer;
    private IGameListener iGameListener;

    public Game(Context applicationContext) {
        this.context = applicationContext;
    }

    private void initGameSample() {
        Random rand = new Random();
        questions.clear();
        for(int i=0;i<Constants.QUESTION_COUNT;i++) {
            Question q = new Question();
            q.setAnecdote("anecdotique");
            q.setQuestion("Exemple de question " + i);
            List<String> options = Arrays.asList("Réponse a", "Réponse b", "Réponse c", "Réponse d");
            q.setOptions(options);
            String answer = options.get(rand.nextInt(options.size()));
            q.setAnswer(answer);

            questions.add(q);
        }
    }

    private void initGame(Level difficulty) {
        QuizzDataManager qdm = QuizzDataManager.getInstance();

        List<Question> loadedQuestions = qdm.loadQuestions(context, difficulty);
        if (loadedQuestions != null && loadedQuestions.size() > 0) {
            this.questions = loadedQuestions;
            return;
        }
        initGameSample();
    }

    public void startQuizz(IGameListener iGameListener, Level difficulty) {
        if (isRunning) {
            return;
        }
        initGame(difficulty);
        this.iGameListener = iGameListener;
        index = 0;
        durationMs = 0;
        gameScore = 0;
        isRunning = true;
        startNextQuestion();
    }

    public void startNextQuestion() {
        if (!isRunning) {
            return;
        }
        this.index = index+1;
        System.out.println("index:"+this.index);
        if (index > questions.size()) {
            isRunning = false;
            gameScore = (10000 * gameScore) / (durationMs / questions.size());
            System.out.println("score = 1000 * score / (" + durationMs  + "/" + questions.size() + ") = " + this.gameScore);
            iGameListener.onGameEnded();
            return;
        }
        startQuestionTime = System.currentTimeMillis();
        cdtimer = new CountDownTimer(Constants.QUESTION_DURATION_MS, 1000) {
            @Override
            public void onTick(long remainingTimeMs) {
                if (iGameListener != null) {
                    long remainingTimeSec = Math.round(remainingTimeMs / 1000);
                    iGameListener.onQuestionTick(remainingTimeSec);
                }
            }

            @Override
            public void onFinish() {
                onAnswer(null);
            }
        };
        cdtimer.start();
        if (iGameListener != null ) {
            iGameListener.onNewQuestion();
        }
    }

    public Question getCurrentQuestion() {
        return index != -1 && index <= questions.size() ? questions.get(index-1) : null;
    }

    public void pauseGame() {
        onAnswer(null);
    }

    public void onAnswer(String answer) {
        long answerDuration;
        if (answer == null) {
            answerDuration = Constants.QUESTION_DURATION_MS;
        } else {
            answerDuration = System.currentTimeMillis() - startQuestionTime;
        }
        if (cdtimer != null) {
            cdtimer.cancel();
        }
        durationMs += answerDuration;
        if (answer != null) {
            Question curQuestion = getCurrentQuestion();
            if (curQuestion == null) {
                return;
            }
            if (answer.equalsIgnoreCase(curQuestion.getAnswer())) {
                if (answerDuration <= Constants.QUESTION_FIRST_DURATION_MS) {
                    gameScore += 2;
                } else {
                    gameScore += 1;
                }
            }
        }
        if (iGameListener != null) {
            iGameListener.onQuestionEnded();
        }
    }

    public long getGameScore() {
        return gameScore;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public interface IGameListener {
        void onQuestionTick(long remainingTimeSec);
        void onNewQuestion();
        void onQuestionEnded();
        void onGameEnded();

    }
}
