package com.example.x61224.nfl.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.x61224.nfl.R;
import com.example.x61224.nfl.data.NFLContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by x61221 on 12/3/2015.
 */
public class RosterAsyncTask extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = NFLSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the ranking, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private String id; // Id iniciado. Deve receber o parametro ID do roster favorito

    public RosterAsyncTask(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        // called to sync
        Log.d(LOG_TAG, "Starting sync");
        // access url and get json

        // access url and get json data
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;

        String sec = id; //-- setar com o id do time
        String third = "depthchart.json";
        // Access url
        try {
            final String NFL_BASE_URL = "http://api.sportradar.us/nfl-t1/teams";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(NFL_BASE_URL).buildUpon()
                    .appendPath(sec)//tirar comentario
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
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            jsonStr = buffer.toString();

            if (jsonStr != "")
                try {
                    // parse json to get teams data
                    getRosterDataFromJson(jsonStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            return;
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
        return;
    }

    private void getRosterDataFromJson(String jsonStr){

        getRosterDataFrom(jsonStr, id);

    }

    private void getRosterDataFrom(String jsonStr, String rosterId){
        final String POSITIONS = "positions";
        final String PLAYERS = "players";
        final String OFFENSE = "offense";
        final String DEFENSE = "defense";
        final String SPECIAL_TEAMS = "special_teams";
        final String POS_NAME = "name";
        final String POS_DESC = "desc";
        final String DEF_TYPE = "type";
        final String PLAYER_ID = "id";
        final String PLAYER_NAME_FULL = "name";
        final String PLAYER_POSITION = "position";
        final String PLAYER_JERSEY_NUMBER = "jersey_number";
        final String PLAYER_STATUS = "status";
        final String PLAYER_DEPTH = "depth";

        String defenseType;
        String positionName;
        String positionDesc;
        String playerId;
        String playerName;
        String playerPosition;
        Integer playerJerseyNumber;
        String playerStatus;
        Integer playerDepth;

        Vector<ContentValues> cVVector = new Vector<>(40);

        try{
            JSONObject rosterJson = new JSONObject(jsonStr);
            JSONObject offenseObject = rosterJson.getJSONObject(OFFENSE);
            JSONArray  offensiveLineArray = offenseObject.getJSONArray(POSITIONS);
            JSONObject defenseObject = rosterJson.getJSONObject(DEFENSE);
            defenseType = defenseObject.getString(DEF_TYPE);
            JSONArray  defensiveLineArray = defenseObject.getJSONArray(POSITIONS);
            JSONObject specialObject = rosterJson.getJSONObject(SPECIAL_TEAMS);
            JSONArray  specialLineArray = specialObject.getJSONArray(POSITIONS);

            for(int i=0; i< offensiveLineArray.length(); i++){
                JSONObject offensePositionJson = offensiveLineArray.getJSONObject(i);
                positionName = offensePositionJson.getString(POS_NAME);
                positionDesc = offensePositionJson.getString(POS_DESC);
                JSONArray playerArray = offensePositionJson.getJSONArray(PLAYERS);

                for(int j=0; j< playerArray.length(); j++){
                    JSONObject playerJson = playerArray.getJSONObject(j);
                    playerId = playerJson.getString(PLAYER_ID);
                    playerName = playerJson.getString(PLAYER_NAME_FULL);
                    playerPosition = playerJson.getString(PLAYER_POSITION);
                    playerJerseyNumber = playerJson.getInt(PLAYER_JERSEY_NUMBER);
                    playerStatus = playerJson.getString(PLAYER_STATUS);
                    playerDepth = playerJson.getInt(PLAYER_DEPTH);
                    {
                        System.out.println(positionName + " - " + positionDesc + " - " + playerId + " - " + playerName + " - " +
                                playerPosition + " - " + playerJerseyNumber + " - " + playerStatus + " - " + playerDepth);

                        ContentValues cValues = new ContentValues();
                        cValues.put(NFLContract.PlayersEntry.COLUMN_ROSTER_ID, rosterId);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_POSITION_TYPE, OFFENSE);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_DEFENSE_TYPE, "");
                        cValues.put(NFLContract.PlayersEntry.COLUMN_POSITION_NAME, positionName);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_POSITION_DESC, positionDesc);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_PLAYER_ID, playerId);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_PLAYER_NAME, playerName);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_PLAYER_POSITION, playerPosition);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_PLAYER_JERSEY_NUMBER, playerJerseyNumber);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_PLAYER_STATUS, playerStatus);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_PLAYER_DEPTH, playerDepth);

                        cVVector.add(cValues);
                    }
                }
            }

            for(int i=0; i< defensiveLineArray.length(); i++){
                JSONObject defensePositionJson = offensiveLineArray.getJSONObject(i);
                positionName = defensePositionJson.getString(POS_NAME);
                positionDesc = defensePositionJson.getString(POS_DESC);
                JSONArray playerArray = defensePositionJson.getJSONArray(PLAYERS);

                for(int j=0; j< playerArray.length(); j++){
                    JSONObject playerJson = playerArray.getJSONObject(j);
                    playerId = playerJson.getString(PLAYER_ID);
                    playerName = playerJson.getString(PLAYER_NAME_FULL);
                    playerPosition = playerJson.getString(PLAYER_POSITION);
                    playerJerseyNumber = playerJson.getInt(PLAYER_JERSEY_NUMBER);
                    playerStatus = playerJson.getString(PLAYER_STATUS);
                    playerDepth = playerJson.getInt(PLAYER_DEPTH);
                    {
                        System.out.println(positionName + " - " + positionDesc + " - " + playerId + " - " + playerName + " - " +
                                playerPosition + " - " + playerJerseyNumber + " - " + playerStatus + " - " + playerDepth);

                        ContentValues cValues = new ContentValues();
                        cValues.put(NFLContract.PlayersEntry.COLUMN_ROSTER_ID, rosterId);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_POSITION_TYPE, DEFENSE);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_DEFENSE_TYPE, defenseType);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_POSITION_NAME, positionName);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_POSITION_DESC, positionDesc);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_PLAYER_ID, playerId);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_PLAYER_NAME, playerName);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_PLAYER_POSITION, playerPosition);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_PLAYER_JERSEY_NUMBER, playerJerseyNumber);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_PLAYER_STATUS, playerStatus);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_PLAYER_DEPTH, playerDepth);

                        cVVector.add(cValues);
                    }
                }
            }

            for(int i=0; i< specialLineArray.length(); i++){
                JSONObject specialPositionJson = specialLineArray.getJSONObject(i);
                positionName = specialPositionJson.getString(POS_NAME);
                positionDesc = specialPositionJson.getString(POS_DESC);
                JSONArray playerArray = specialPositionJson.getJSONArray(PLAYERS);

                for(int j=0; j< playerArray.length(); j++){
                    JSONObject playerJson = playerArray.getJSONObject(j);
                    playerId = playerJson.getString(PLAYER_ID);
                    playerName = playerJson.getString(PLAYER_NAME_FULL);
                    playerPosition = playerJson.getString(PLAYER_POSITION);
                    playerJerseyNumber = playerJson.getInt(PLAYER_JERSEY_NUMBER);
                    playerStatus = playerJson.getString(PLAYER_STATUS);
                    playerDepth = playerJson.getInt(PLAYER_DEPTH);
                    {
                        System.out.println(positionName + " - " + positionDesc + " - " + playerId + " - " + playerName + " - " +
                                playerPosition + " - " + playerJerseyNumber + " - " + playerStatus + " - " + playerDepth);

                        ContentValues cValues = new ContentValues();
                        cValues.put(NFLContract.PlayersEntry.COLUMN_ROSTER_ID, rosterId);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_POSITION_TYPE, OFFENSE);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_DEFENSE_TYPE, "");
                        cValues.put(NFLContract.PlayersEntry.COLUMN_POSITION_NAME, positionName);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_POSITION_DESC, positionDesc);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_PLAYER_ID, playerId);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_PLAYER_NAME, playerName);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_PLAYER_POSITION, playerPosition);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_PLAYER_JERSEY_NUMBER, playerJerseyNumber);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_PLAYER_STATUS, playerStatus);
                        cValues.put(NFLContract.PlayersEntry.COLUMN_PLAYER_DEPTH, playerDepth);

                        cVVector.add(cValues);
                    }
                }
            }

            // delete old data
            getContext().getContentResolver().delete(NFLContract.PlayersEntry.CONTENT_URI, null, null);

            // add all reviews to database
            Integer inserted = 0;
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = getContext().getContentResolver().bulkInsert(NFLContract.PlayersEntry.CONTENT_URI, cvArray);
            }
            Log.d(LOG_TAG, "Sync Complete. " + inserted.toString() + " Inserted");

        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        System.out.println("End of getSyncAccount");
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        System.out.println("Begin of getSyncAccount");
        /*
         * Since we've created an account
         */
        //NFLSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}