package com.example.x61224.nfl;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.x61224.nfl.sync.NFLSyncAdapter;

public class RosterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        String id = i.getStringExtra("team_id");
        Intent a = new Intent(this,RosterActivityFragment.class);
        a.putExtra("team_id",id);
        setIntent(a);

        setContentView(R.layout.activity_roster);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ranking, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //@Override
    //public void onItemSelected(String teamId) {
        // It is called from MainActivityFragment, when a movie is selected.
        // Start Detail Activity, sending the uri param.

        //Intent intent = new Intent(this, RosterActivity.class).setData(contentUri);
        //startActivity(intent);
    //}

}
