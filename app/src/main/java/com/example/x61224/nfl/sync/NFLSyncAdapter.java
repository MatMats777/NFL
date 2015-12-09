package com.example.x61224.nfl.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.x61224.nfl.R;

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

import com.example.x61224.nfl.RankingActivity;
import com.example.x61224.nfl.RankingActivityFragment;
import com.example.x61224.nfl.data.NFLContract;

/**
 * Created by x61224 on 11/23/2015.
 */
public class NFLSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = NFLSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the ranking, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 1;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int RANKING_NOTIFICATION_ID = 3001;

    public NFLSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        // called to sync
        Log.d(LOG_TAG, "Starting sync");
        // access url and get json
        String urlStr = "http://api.sportradar.us/nfl-t1/teams/2015/rankings.json?api_key=x3j2gsqzk7bv7qnj5xjc2gbw";

        // access url and get json data
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;
        // Access url
        try {
            URL url = new URL(urlStr);

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
                    getRankingDataFromJson(jsonStr);
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

    private void getRankingDataFromJson(String jsonStr){

        getTeamsDataFrom(jsonStr, "AFC", "EAST");

    }

    private void getTeamsDataFrom(String jsonStr, String conference, String division){
        final String CONFERENCES = "conferences";
        final String DIVISIONS = "divisions";
        final String TEAMS = "teams";
        final String ID = "id";
        final String NAME = "name";
        final String MARKET = "market";
        final String RANK = "rank";
        final String CONFERENCE = "conference";
        final String DIVISION = "division";

        String conferenceName;
        String divisionName;
        String teamId;
        String teamName;
        String teamMarket;
        String teamRankConference;
        String teamRankDivision;
        Integer teamRankConferenceInt;
        Integer teamRankDivisionInt;

        Vector<ContentValues> cVVector = new Vector<>(32);

        try{
            JSONObject rankingJson = new JSONObject(jsonStr);
            JSONArray conferenceArray = rankingJson.getJSONArray(CONFERENCES);

            for(int i=0; i< conferenceArray.length(); i++){
                JSONObject conferenceJson = conferenceArray.getJSONObject(i);
                conferenceName = conferenceJson.getString(ID);
                JSONArray divisionArray = conferenceJson.getJSONArray(DIVISIONS);

                for(int j=0; j< divisionArray.length(); j++){
                    JSONObject divisionJson = divisionArray.getJSONObject(j);
                    divisionName = divisionJson.getString(ID);
                    JSONArray teamArray = divisionJson.getJSONArray(TEAMS);

                    for(int k=0; k< teamArray.length(); k++){
                        JSONObject teamJson = teamArray.getJSONObject(k);
                        teamId = teamJson.getString(ID);
                        teamName = teamJson.getString(NAME);
                        teamMarket = teamJson.getString(MARKET);
                        JSONObject rankJson = teamJson.getJSONObject(RANK);
                        teamRankConference = rankJson.getString(CONFERENCE);
                        teamRankConferenceInt = Integer.parseInt(teamRankConference);
                        teamRankDivision = rankJson.getString(DIVISION);
                        teamRankDivisionInt = Integer.parseInt(teamRankDivision);

                        System.out.println(conferenceName + " - " + divisionName + " - " + teamId + " - " + teamName + " - " +
                        teamMarket + " - " + teamRankConference + " - " + teamRankDivision);

                        ContentValues cValues = new ContentValues();
                        cValues.put(NFLContract.TeamsEntry.COLUMN_TEAM_ID, teamId);
                        cValues.put(NFLContract.TeamsEntry.COLUMN_NAME, teamName);
                        cValues.put(NFLContract.TeamsEntry.COLUMN_MARKET, teamMarket);
                        cValues.put(NFLContract.TeamsEntry.COLUMN_CONFERENCE, conferenceName);
                        cValues.put(NFLContract.TeamsEntry.COLUMN_DIVISION, divisionName);
                        cValues.put(NFLContract.TeamsEntry.COLUMN_RANK_CONFERENCE, teamRankConferenceInt);
                        cValues.put(NFLContract.TeamsEntry.COLUMN_RANK_DIVISION, teamRankDivisionInt);

                        cVVector.add(cValues);
                    }
                }
            }
            // get old ranking
            Cursor all_teams = getContext().getContentResolver().query(
                    NFLContract.TeamsEntry.CONTENT_URI,
                    RankingActivityFragment.TEAMS_COLUMNS,
                    "1",
                    null,
                    null,
                    null
            );

            // iterate all_teams
            Boolean change = false;
            String team_changed = null;
            String new_ranking = null;
            String team_changed_id = null;
            all_teams.moveToFirst();
            while(!all_teams.isAfterLast()){
                System.out.println("iteration");
                for(int i=0; i<cVVector.size(); i++){
                    if(all_teams.getString(RankingActivityFragment.COL_TEAM_ID).
                            equals(cVVector.get(i).getAsString(NFLContract.TeamsEntry.COLUMN_TEAM_ID))){

                        if(!all_teams.getString(RankingActivityFragment.COL_RANK_CONFERENCE).
                                equals(cVVector.get(i).getAsString(NFLContract.TeamsEntry.COLUMN_RANK_CONFERENCE))){
                            team_changed = all_teams.getString(RankingActivityFragment.COL_NAME);
                            new_ranking = "Conference: " + cVVector.get(i).getAsString(NFLContract.TeamsEntry.COLUMN_RANK_CONFERENCE);
                            team_changed_id = all_teams.getString(RankingActivityFragment.COL_TEAM_ID);
                            change = true;
                            break;
                        }
                        if(!all_teams.getString(RankingActivityFragment.COL_RANK_DIVISION).
                                equals(cVVector.get(i).getAsString(NFLContract.TeamsEntry.COLUMN_RANK_DIVISION))){
                            team_changed = all_teams.getString(RankingActivityFragment.COL_NAME);
                            new_ranking = "Division: " + cVVector.get(i).getAsString(NFLContract.TeamsEntry.COLUMN_RANK_DIVISION);
                            team_changed_id = all_teams.getString(RankingActivityFragment.COL_TEAM_ID);
                            change = true;
                            break;
                        }
                        System.out.println(all_teams.getString(RankingActivityFragment.COL_NAME) + " - same.");
                        break;
                    }
                }
                all_teams.moveToNext();
            }

            if(change) {
                System.out.println("CHANGED");
                // delete old data
                getContext().getContentResolver().delete(NFLContract.TeamsEntry.CONTENT_URI, null, null);

                // add all reviews to database
                Integer inserted = 0;
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = getContext().getContentResolver().bulkInsert(NFLContract.TeamsEntry.CONTENT_URI, cvArray);
                }
                Log.d(LOG_TAG, "Sync Complete. " + inserted.toString() + " Inserted");

                // notification
                notifyRanking(team_changed, new_ranking, team_changed_id);
            }
            else
                notifyRanking("Ranking", " still the same", "nfl_logo_black");

        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    private void notifyRanking(String team, String ranking, String team_id){
        System.out.println("NOTIFY");
        Context context = getContext();
        Resources resources = context.getResources();

        System.out.println("NOTIFICATION");

        // NotificationCompatBuilder is a very convenient way to build backward-compatible
        // notifications.  Just throw in some data.
        Bitmap largeIcon = BitmapFactory.decodeResource(resources,R.drawable.car);
        String title = team + " - " + ranking;
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext())
                        .setColor(resources.getColor(R.color.dark_green))
                        .setSmallIcon(R.drawable.car)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(title);

        // Make something interesting happen when the user clicks on the notification.
        // In this case, opening the app is sufficient.
        Intent resultIntent = new Intent(context, RankingActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
        mNotificationManager.notify(RANKING_NOTIFICATION_ID, mBuilder.build());

    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
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
        NFLSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

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