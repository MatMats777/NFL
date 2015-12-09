package com.example.x61224.nfl.adapters;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.x61224.nfl.R;
import com.example.x61224.nfl.RankingActivity;
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
        Integer rank;

        if(division.equals("ALL")){
            rank = cursor.getInt(RankingActivityFragment.COL_RANK_CONFERENCE);
        }
        else
            rank = cursor.getInt(RankingActivityFragment.COL_RANK_DIVISION);

        String id = cursor.getString(RankingActivityFragment.COL_TEAM_ID);
        id = id.toLowerCase();
        int aux = context.getResources().getIdentifier(id,"drawable",context.getPackageName());

        String rankStr = rank.toString();
        String nameStr = cursor.getString(RankingActivityFragment.COL_NAME);
        String marketStr = cursor.getString(RankingActivityFragment.COL_MARKET);
        LinearLayout linear = (LinearLayout)view.findViewById(R.id.layout_col);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            linear.setBackgroundResource(aux);
        }

        viewHolder.rank.setText(rankStr);
        viewHolder.name.setText(nameStr);
        viewHolder.market.setText(marketStr);

    }
}
