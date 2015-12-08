package com.example.x61224.nfl;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.x61224.nfl.adapters.RosterAdapter;
import com.example.x61224.nfl.model.Player;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * A placeholder fragment containing a simple view.
 */
public class RosterActivityFragment extends Fragment {


    private RosterAdapter mRosterAdapter;
    private ListView mListView;
    private ArrayList<Player> mPlayers = null;
    private String id;
    public RosterActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //id = getArguments().getString("team_id");
        View view = inflater.inflate(R.layout.fragment_roster, container, false);
        //TextView text1 = (TextView) view.findViewById(R.id.id);
        //text1.setText(id);
        mListView = (ListView) view.findViewById(R.id.listview_roster);


        mRosterAdapter = new RosterAdapter(new ArrayList(),getActivity());


        mListView.setAdapter(mRosterAdapter);

            updateRoster();


        return view;
    }

    private void updateRoster() {
           new FetchMoviesTask().execute("");

    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Player>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private List<Player> getPlayersDataFromJson(String jsonStr) throws JSONException {
            JSONObject rosterJson = new JSONObject(jsonStr);
            JSONObject offenseJson = rosterJson.getJSONObject("offense");
            JSONObject defenseJson = rosterJson.getJSONObject("defense");
            JSONObject specialJson = rosterJson.getJSONObject("special_teams");
            JSONArray ofPositionsArray = offenseJson.getJSONArray("positions");
            JSONArray defPositionsArray = defenseJson.getJSONArray("positions");
            JSONArray spPositionsArray = specialJson.getJSONArray("positions");

            List<Player> results = new ArrayList<>();

            for(int i = 0; i < ofPositionsArray.length(); i++) {
                JSONObject position = ofPositionsArray.getJSONObject(i);
                JSONArray playersArray = position.getJSONArray("players");
                for (int j = 0; j < playersArray.length(); j++) {
                    JSONObject player = playersArray.getJSONObject(j);
                    Player playerModel = new Player(player, position);
                    results.add(playerModel);
                }
            }

            for(int i = 0; i < defPositionsArray.length(); i++) {
                JSONObject position = defPositionsArray.getJSONObject(i);
                JSONArray playersArray = position.getJSONArray("players");
                for (int j = 0; j < playersArray.length(); j++) {
                    JSONObject player = playersArray.getJSONObject(j);
                    Player playerModel = new Player(player, position);
                    results.add(playerModel);
                }
            }

            for(int i = 0; i < spPositionsArray.length(); i++) {
                JSONObject position = spPositionsArray.getJSONObject(i);
                JSONArray playersArray = position.getJSONArray("players");
                for (int j = 0; j < playersArray.length(); j++) {
                    JSONObject player = playersArray.getJSONObject(j);
                    Player playerModel = new Player(player, position);
                    results.add(playerModel);
                }
            }

            return results;
        }

        @Override
        protected List<Player> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonStr = null;

            //String sec = id;
            String sec = "ARI";
            String third = "depthchart.json";
            // Access url
            try {
                final String NFL_BASE_URL = "http://api.sportradar.us/nfl-t1/teams";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(NFL_BASE_URL).buildUpon()
                        .appendPath(sec)
                        .appendPath(third)
                        .appendQueryParameter(API_KEY_PARAM, getContext().getString(R.string.own_api_key))
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        System.out.println(e.getMessage());
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getPlayersDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(List <Player> players) {
            if (players != null) {
                if (mRosterAdapter != null) {
                    mRosterAdapter.setItemList(players);
                }
                mPlayers = new ArrayList<Player>();
                mPlayers.addAll(players);
            }
        }
    }
}
