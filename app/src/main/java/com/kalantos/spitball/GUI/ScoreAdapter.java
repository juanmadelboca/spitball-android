package com.kalantos.spitball.GUI;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kalantos.spitball.R;
import com.kalantos.spitball.logic.Score;

import java.util.List;

/**
 * Created by Juanma on 20/3/2017.
 */

public class ScoreAdapter extends BaseAdapter {
    private Context context;
    private List<Score> scores;

    public ScoreAdapter(List<Score> scores, Context context) {
        this.scores = scores;
        this.context = context;
    }

    @Override
    public int getCount() {
        return scores.size();
    }

    @Override
    public Object getItem(int position) {
        return scores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //adapta el contenido de un objeto score a una listview
        View view=View.inflate(context, R.layout.custom_row,null);
        TextView textViewP=(TextView)view.findViewById(R.id.textView6);
        TextView textViewS=(TextView)view.findViewById(R.id.textView5);
        textViewP.setText(scores.get(position).player);
        textViewS.setText(scores.get(position).score+"");
        return view ;
    }
}
