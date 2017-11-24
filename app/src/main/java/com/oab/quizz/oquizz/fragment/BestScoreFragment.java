package com.oab.quizz.oquizz.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oab.quizz.oquizz.R;
import com.oab.quizz.oquizz.manager.PreferencesManager;

public class BestScoreFragment extends Fragment {

    private TextView txtBestScore;
    private FragmentActivity activity;

    public BestScoreFragment() {
        // Required empty public constructor
    }

    public static BestScoreFragment newInstance() {
        BestScoreFragment fragment = new BestScoreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_best_score, container, false);
        txtBestScore = inflate.findViewById(R.id.txtBestScore);
        return inflate;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        long bestScore = PreferencesManager.getHighScore(activity);
        txtBestScore.setText(getString(R.string.bestScore, bestScore));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }
}
