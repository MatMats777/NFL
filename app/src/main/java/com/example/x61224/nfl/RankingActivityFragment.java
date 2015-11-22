package com.example.x61224.nfl;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.example.x61224.nfl.adapters.RankingAdapter;
import com.example.x61224.nfl.data.NFLContract;
import com.example.x61224.nfl.data.NFLContract.TeamsEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class RankingActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Adapter used to gridView with poster images
    private RankingAdapter rankingAdapter;
    private ListView rankingListView;

    // Used when you click in an item
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final int FORECAST_LOADER = 0;

    private static final String[] TEAMS_COLUMNS = {
            TeamsEntry.TABLE_NAME + "." + TeamsEntry._ID,
            TeamsEntry.COLUMN_TEAM_ID,
            TeamsEntry.COLUMN_NAME,
            TeamsEntry.COLUMN_MARKET,
            TeamsEntry.COLUMN_RANK_CONFERENCE,
            TeamsEntry.COLUMN_RANK_DIVISION
    };

    // These indices are tied to TEAMS_COLUMNS.
    public static final int COL_ID = 0;
    public static final int COL_TEAM_ID = 1;
    public static final int COL_NAME = 2;
    public static final int COL_MARKET = 3;
    public static final int COL_RANK_CONFERENCE = 4;
    public static final int COL_RANK_DIVISION = 5;

    public RankingActivityFragment() {
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(String teamId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Menu with popularity, user_rating and favorites
        inflater.inflate(R.menu.menu_ranking, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    // Teste
    public void fakedata(){
        ContentValues movieValues = new ContentValues();
        // set each column of movies db
        movieValues.put(TeamsEntry.COLUMN_TEAM_ID, "NE");
        movieValues.put(TeamsEntry.COLUMN_NAME, "Patriots");
        movieValues.put(TeamsEntry.COLUMN_MARKET, "New England");
        movieValues.put(TeamsEntry.COLUMN_CONFERENCE, "AFC");
        movieValues.put(TeamsEntry.COLUMN_DIVISION, "AFC_EAST");
        movieValues.put(TeamsEntry.COLUMN_RANK_CONFERENCE, "1");
        movieValues.put(TeamsEntry.COLUMN_RANK_DIVISION, "1");
        getContext().getContentResolver().insert(
                NFLContract.TeamsEntry.CONTENT_URI,
                movieValues
        );

        ContentValues xmovieValues = new ContentValues();
        // set each column of movies db
        xmovieValues.put(TeamsEntry.COLUMN_TEAM_ID, "BUF");
        xmovieValues.put(TeamsEntry.COLUMN_NAME, "Bills");
        xmovieValues.put(TeamsEntry.COLUMN_MARKET, "Buffalo");
        xmovieValues.put(TeamsEntry.COLUMN_CONFERENCE, "AFC");
        xmovieValues.put(TeamsEntry.COLUMN_DIVISION, "AFC_EAST");
        xmovieValues.put(TeamsEntry.COLUMN_RANK_CONFERENCE, "6");
        xmovieValues.put(TeamsEntry.COLUMN_RANK_DIVISION, "2");
        getContext().getContentResolver().insert(
                NFLContract.TeamsEntry.CONTENT_URI,
                xmovieValues
        );
        System.out.println("INSERT COMPLETE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Teste
        fakedata();

        // Initialize adapter
        rankingAdapter = new RankingAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_ranking, container, false);

        rankingListView = (ListView) rootView.findViewById(R.id.ranking_list_view);
        rankingListView.setAdapter(rankingAdapter);
        rankingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View v,
                                    int position, long l) {
                // return a cursor with movie position data
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                // call onItemSelected in MainActivity;
                // param = uri with movie id
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(cursor.getString(COL_TEAM_ID));
                }
                mPosition = position;
            }
        });

        RadioGroup conferenceRadio = (RadioGroup) rootView.findViewById(R.id.conference_radio);
        conferenceRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                switch (checkedId) {
                    case R.id.afc_button:
                        prefs.edit().putString("conference", "AFC").apply();
                        System.out.println("AFC");
                        break;
                    case R.id.nfc_button:
                        prefs.edit().putString("conference", "NFC").apply();
                        break;
                }
                updateRanking();
            }
        });

        RadioGroup divisionRadio = (RadioGroup) rootView.findViewById(R.id.division_radio);
        divisionRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                switch(checkedId){
                    case R.id.north_button:
                        prefs.edit().putString("division", "NORTH").apply();
                        break;
                    case R.id.south_button:
                        prefs.edit().putString("division", "SOUTH").apply();
                        break;
                    case R.id.west_button:
                        prefs.edit().putString("division", "WEST").apply();
                        break;
                    case R.id.east_button:
                        prefs.edit().putString("division", "EAST").apply();
                        System.out.println("EAST");
                        break;
                    case R.id.all_button:
                        prefs.edit().putString("division", "ALL").apply();
                        break;
                }
                updateRanking();
            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The gridView probably hasn't even been populated yet.  Actually perform the
            // swap out in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    public void updateRanking(){
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to GridView.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String conference = prefs.getString("conference", "afc");
        String division = prefs.getString("division", "north");
        // Simple uri
        Uri teamsUri = NFLContract.TeamsEntry.buildTeams();
        String selection;
        String selectionArgs[] = null;

        // get cursor with data from db

        // case all conference
        if(division.equals("ALL")){
            selection = "conference";
            selectionArgs = new String[]{conference};
        }
        // case division
        else{
            selection = "division";
            selectionArgs = new String[]{conference + "_" + division};
        }
        return new CursorLoader(getActivity(),
                teamsUri,
                TEAMS_COLUMNS,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // called when a loader is created;
        // Cursor data is got from onCreateLoader

        // swap cursor with new data
        rankingAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            rankingListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        rankingAdapter.swapCursor(null);
    }
}
