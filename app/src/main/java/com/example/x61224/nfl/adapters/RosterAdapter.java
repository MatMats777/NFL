package com.example.x61224.nfl.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.x61224.nfl.R;
import com.example.x61224.nfl.model.Player;

import java.util.List;


/**
 * Created by x61221 on 12/3/2015.
 */
public class RosterAdapter extends ArrayAdapter<Player> {

    private List<Player> itemList;
    private Context context;

    public RosterAdapter(List<Player> itemList, Context ctx) {
        super(ctx, android.R.layout.simple_list_item_1, itemList);
        this.itemList = itemList;
        this.context = ctx;
    }

    public int getCount() {
        if (itemList != null)
            return itemList.size();
        return 0;
    }

    public Player getItem(int position) {
        if (itemList != null)
            return itemList.get(position);
        return null;
    }

    public long getItemId(int position) {
        if (itemList != null)
            return itemList.get(position).hashCode();
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.grid_item, null);
        }

        Player c = itemList.get(position);

        TextView text2 = (TextView) v.findViewById(R.id.name);
        text2.setText(c.name);

        TextView text3 = (TextView) v.findViewById(R.id.position);
        text3.setText(c.position);

        TextView text4 = (TextView) v.findViewById(R.id.original_position);
        text4.setText(c.original_position);

        TextView text5 = (TextView) v.findViewById(R.id.number);
        text5.setText(c.number);

        TextView text6 = (TextView) v.findViewById(R.id.status);
        text6.setText(c.status);

        return v;

    }

    public List<Player> getItemList() {
        return itemList;
    }

    public void setItemList(List<Player> itemList) {
        this.itemList = itemList;
    }


}