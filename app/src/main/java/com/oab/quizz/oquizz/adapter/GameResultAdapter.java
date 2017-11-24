package com.oab.quizz.oquizz.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.oab.quizz.oquizz.R;
import com.oab.quizz.oquizz.database.GameResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * utilisé pour RecyclerView pour générer les tuiles de la liste
 */
public class GameResultAdapter extends RecyclerView.Adapter<GameResultAdapter.ViewHolder> {
    private List<GameResult> rez;

    public GameResultAdapter(List<GameResult> myDataset) {
        rez = myDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gameresult, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        GameResult gameResult = rez.get(position);
        holder.txtPseudo.setText(gameResult.getPseudo());
        holder.txtScore.setText(String.valueOf(gameResult.getScore()));
        Date creationDate = gameResult.getCreationDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:MM");
        holder.txtDate.setText(sdf.format(creationDate));
    }

    @Override
    public int getItemCount() {
        return rez != null ? rez.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPseudo;
        TextView txtScore;
        TextView txtDate;

        public ViewHolder(View itemGameResult) {
            super(itemGameResult);
            txtPseudo = itemGameResult.findViewById(R.id.itemPseudo);
            txtScore = itemGameResult.findViewById(R.id.itemScore);
            txtDate = itemGameResult.findViewById(R.id.itemDate);
        }
    }

}