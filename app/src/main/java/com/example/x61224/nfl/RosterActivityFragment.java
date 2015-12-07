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

import com.example.x61224.nfl.adapters.RosterAdapter;
import com.example.x61224.nfl.model.Player;
import com.example.x61224.nfl.model.Roster;

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

    private GridView mGridView;

    private RosterAdapter mRosterAdapter;

    public RosterActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roster, container, false);

        mGridView = (GridView) view.findViewById(R.id.gridview_roster);

        mRosterAdapter = new RosterAdapter(getActivity(), new Roster());

        mGridView.setAdapter(mRosterAdapter);

            updateRoster();


        return view;
    }

    private void updateRoster() {
           new FetchMoviesTask().execute();

    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Roster> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private Roster getRosterDataFromJson(String jsonStr) throws JSONException {
            JSONObject rosterJson = new JSONObject(jsonStr);

            Roster results = new Roster(rosterJson.getString("id"),rosterJson);

            return results;
        }

        @Override
        protected Roster doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonStr = null;

            String prim = "Teams";
            String sec = "ARI";
            String third = "depthchart";
            // Access url
            try {
                final String NFL_BASE_URL = "http://api.sportradar.us/nfl-t1/";
                final String PRIM_PARAM = "";
                final String SEC_PARAM = "";
                final String THIRD_PARAM = "";
                final String API_KEY_PARAM = ".json?api_key=";

                Uri builtUri = Uri.parse(NFL_BASE_URL).buildUpon()
                        .appendQueryParameter(PRIM_PARAM,prim)
                        .appendQueryParameter(SEC_PARAM,sec)
                        .appendQueryParameter(THIRD_PARAM,third)
                        .appendQueryParameter(API_KEY_PARAM,getContext().getString(R.string.own_api_key))
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

                if (jsonStr != "")
                    try {
                        // parse json to get teams data
                        getRosterDataFromJson(jsonStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                return null;
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
                return getRosterDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Roster roster) {
            if (roster != null) {
                if (mRosterAdapter != null) {
                    Vector<Vector<Player>> data=new Vector<Vector<Player>>();
                    data.add(0, roster.offense);
                    data.add(1,roster.defense);
                    data.add(2,roster.special_team);
                    mRosterAdapter.setData(data);
                }
            }
        }
    }
}
