package com.example.x61224.nfl.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.x61224.nfl.R;
import com.example.x61224.nfl.model.Player;
import com.example.x61224.nfl.model.Roster;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;
import java.util.Vector;

/**
 * Created by x61221 on 12/3/2015.
 */
public class RosterAdapter extends BaseAdapter {
    private final Context mContext;
    private final LayoutInflater mInflater;
    private final Vector<Player> mLock = new Vector<Player>();

    public Vector<Vector<Player>> mObjects = new Vector<Vector<Player>>(3);

    public RosterAdapter(Context context,Roster roster) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mObjects.add(0,roster.offense);
        mObjects.add(1,roster.defense);
        mObjects.add(2,roster.special_team);
    }

    public Context getContext() {
        return mContext;
    }

    public void add(Vector<Player> object) {
        synchronized (mLock) {
            mObjects.add(object);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        synchronized (mLock) {
            mObjects.clear();
        }
        notifyDataSetChanged();
    }

    public void setData(Vector<Vector<Player>> data) {
        clear();
        for (Vector<Player> player : data)
        add(player);
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public String getItem(int position) {
        Vector<String> players= new Vector<String>();
        for(int i=0;i<3;i++)
            for(int j=0;j<mObjects.get(i).size();j++) {
                players.add(5*i, mObjects.get(i).get(j).player_name);
                players.add(5*i+1, mObjects.get(i).get(j).position_desc);
                players.add(5*i+2, mObjects.get(i).get(j).player_position);
                players.add(5*i+3, mObjects.get(i).get(j).player_jersey_number);
                players.add(5*i+4, mObjects.get(i).get(j).player_status);
            }
        return players.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate(R.layout.grid_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        final String roster = getItem(position);

        viewHolder = (ViewHolder) view.getTag();

        viewHolder.titleView.setText(roster);

        return view;
    }

    public static class ViewHolder {
        public final TextView titleView;

        public ViewHolder(View view) {
        titleView = (TextView) view.findViewById(R.id.grid_item_title);
        }
    }

}
