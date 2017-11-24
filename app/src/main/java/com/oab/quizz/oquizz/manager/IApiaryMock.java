package com.oab.quizz.oquizz.manager;

import com.oab.quizz.oquizz.model.QuizzQuestions;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * http://private-f7f742-orangequizz.apiary-mock.com/questions_expert
 * http://private-f7f742-orangequizz.apiary-mock.com/questions_normal
 * http://private-f7f742-orangequizz.apiary-mock.com/questions_simple
 */
public interface IApiaryMock {
    @GET("questions_{difficulty}")
    Call<QuizzQuestions> getQuizz(@Path("difficulty") String difficulty);
}
