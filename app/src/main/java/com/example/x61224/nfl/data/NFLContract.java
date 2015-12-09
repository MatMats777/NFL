package com.example.x61224.nfl.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by x61224 on 11/20/2015.
 */
public class NFLContract {

    // CONTENT_AUTHORITY is an unique name for the entire content provider.
    public static final String CONTENT_AUTHORITY = "com.example.x61224.nfl";

    // Base of all URI's which app will use to contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_TEAMS = "teams";
    public static final String PATH_ROSTER = "roster";

    /* Inner class that defines the table contents of the movies table */
    public static final class TeamsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TEAMS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TEAMS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TEAMS;

        // Table name
        public static final String TABLE_NAME = "teams";

        public static final String COLUMN_TEAM_ID = "team_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_MARKET = "market";
        public static final String COLUMN_CONFERENCE = "conference";
        public static final String COLUMN_DIVISION = "division";
        public static final String COLUMN_RANK_CONFERENCE = "rank_conference";
        public static final String COLUMN_RANK_DIVISION = "rank_division";


        public static Uri buildTeamUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTeamUriWithId(String team_id) {
            return CONTENT_URI.buildUpon().appendPath(team_id).build();
        }

        public static Uri buildTeams() {
            return CONTENT_URI;
        }

        public static String getIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static final class PlayersEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROSTER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ROSTER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ROSTER;

        // Table name
        public static final String TABLE_NAME = "roster";

        public static final String COLUMN_ROSTER_ID = "roster_id";
        public static final String COLUMN_POSITION_TYPE = "position_type";
        public static final String COLUMN_POSITION_NAME = "position_name";
        public static final String COLUMN_POSITION_DESC = "position_desc";
        public static final String COLUMN_DEFENSE_TYPE = "defense_type";
        public static final String COLUMN_PLAYER_ID = "player_id";
        public static final String COLUMN_PLAYER_NAME = "player_name";
        public static final String COLUMN_PLAYER_POSITION = "player_position";
        public static final String COLUMN_PLAYER_JERSEY_NUMBER = "player_jersey_number";
        public static final String COLUMN_PLAYER_STATUS = "player_status";
        public static final String COLUMN_PLAYER_DEPTH = "player_depth";


        public static Uri buildRosterUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildRosterUriWithId(String roster_id) {
            return CONTENT_URI.buildUpon().appendPath(roster_id).build();
        }

        public static Uri buildRosters() {
            return CONTENT_URI;
        }

        public static String getIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
}
