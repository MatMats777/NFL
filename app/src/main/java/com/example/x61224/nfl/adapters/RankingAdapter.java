package com.example.x61224.nfl.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.x61224.nfl.R;
import com.example.x61224.nfl.RankingActivityFragment;

/**
 * Created by x61224 on 11/20/2015.
 */
public class RankingAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final TextView rank;
        public final TextView name;
        public final TextView market;


        public ViewHolder(View view) {
            rank = (TextView) view.findViewById(R.id.team_rank);
            name = (TextView) view.findViewById(R.id.team_name);
            market = (TextView) view.findViewById(R.id.team_market);
        }
    }

    public RankingAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.ranking_item;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String division = prefs.getString("division", "north");
        String rankStr;
        if(division.equals("ALL")){
            rankStr = cursor.getString(RankingActivityFragment.COL_RANK_CONFERENCE);
        }
        else
            rankStr = cursor.getString(RankingActivityFragment.COL_RANK_DIVISION);
        String nameStr = cursor.getString(RankingActivityFragment.COL_NAME);
        String marketStr = cursor.getString(RankingActivityFragment.COL_MARKET);

        viewHolder.rank.setText(rankStr);
        viewHolder.name.setText(nameStr);
        viewHolder.market.setText(marketStr);
    }
}
