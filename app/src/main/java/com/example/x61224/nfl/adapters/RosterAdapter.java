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
import com.example.x61224.nfl.model.Roster;

import java.util.List;

/**
 * Created by x61221 on 12/3/2015.
 */
public class RosterAdapter extends BaseAdapter {
    private final Context mContext;
    private final LayoutInflater mInflater;

    private final Roster mLock = new Roster();

    public RosterAdapter(Context context,Roster roster) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public Context getContext() {
        return mContext;
    }

    //public void add(Roster object) {
    //    synchronized (mLock) {
    //        mObjects.add(object);
    //    }
        //notifyDataSetChanged();
    //}

    //public void clear() {
     //   synchronized (mLock) {
     //       mObjects.clear();
     //   }
        //notifyDataSetChanged();
   // }

    public void setData(Roster data) {
        //clear();
        //add(data);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    //@Override
    //public int getCount() {
    //    return mObjects.size();
    //}

    //@Override
    //public Roster getItem(int position) {
    //    return mObjects.get(position);
    //}

    //@Override
    //public long getItemId(int position) {
    //    return position;
    //}

    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate(R.layout.fragment_roster, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        final Roster roster = getItem(position);

        String image_url = "http://image.tmdb.org/t/p/w185" + movie.getImage();

        viewHolder = (ViewHolder) view.getTag();

        Glide.with(getContext()).load(image_url).into(viewHolder.imageView);
        viewHolder.titleView.setText(movie.getTitle());

        return view;
    }*/

    public static class ViewHolder {
        //public final TextView jersey_number;
        public final TextView name;
        public final TextView market;


        public ViewHolder(View view) {
        //    rank = (TextView) view.findViewById(R.id.team_rank);
            name = (TextView) view.findViewById(R.id.team_name);
            market = (TextView) view.findViewById(R.id.team_market);
        }
    }

}
