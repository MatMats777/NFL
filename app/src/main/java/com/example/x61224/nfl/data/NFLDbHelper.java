package com.example.x61224.nfl.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.x61224.nfl.data.NFLContract.TeamsEntry;
import com.example.x61224.nfl.data.NFLContract.PlayersEntry;

/**
 * Created by x61224 on 11/20/2015.
 */
public class NFLDbHelper  extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "teams.db";

    public NFLDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold movies. A movie consists of the image URL, title,
        // synopsis, rating and date of creation.
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + TeamsEntry.TABLE_NAME + " (" +
                TeamsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TeamsEntry.COLUMN_TEAM_ID + " TEXT NOT NULL, " +
                TeamsEntry.COLUMN_NAME + " TEXT, " +
                TeamsEntry.COLUMN_MARKET + " TEXT NOT NULL, " +
                TeamsEntry.COLUMN_CONFERENCE + " TEXT, " +
                TeamsEntry.COLUMN_DIVISION + " TEXT NOT NULL, " +
                TeamsEntry.COLUMN_RANK_CONFERENCE + " INTEGER NOT NULL, " +
                TeamsEntry.COLUMN_RANK_DIVISION + " INTEGER NOT NULL, " +
                " UNIQUE (" + TeamsEntry.COLUMN_TEAM_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_ROSTER_TABLE = "CREATE TABLE " + NFLContract.PlayersEntry.TABLE_NAME + " (" +
                PlayersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                PlayersEntry.COLUMN_ROSTER_ID + " TEXT NOT NULL, " +
                PlayersEntry.COLUMN_POSITION_TYPE + " TEXT NOT NULL, " +
                PlayersEntry.COLUMN_POSITION_NAME + " TEXT NOT NULL, " +
                PlayersEntry.COLUMN_POSITION_DESC + " TEXT NOT NULL," +
                PlayersEntry.COLUMN_DEFENSE_TYPE + " TEXT," +

                PlayersEntry.COLUMN_PLAYER_ID + " TEXT NOT NULL, " +
                PlayersEntry.COLUMN_PLAYER_NAME + " TEXT NOT NULL, " +

                PlayersEntry.COLUMN_PLAYER_POSITION + " TEXT NOT NULL, " +
                PlayersEntry.COLUMN_PLAYER_JERSEY_NUMBER + " INTEGER NOT NULL, " +
                PlayersEntry.COLUMN_PLAYER_STATUS + " TEXT NOT NULL, " +
                PlayersEntry.COLUMN_PLAYER_DEPTH + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + PlayersEntry.COLUMN_ROSTER_ID + ") REFERENCES " +
                TeamsEntry.TABLE_NAME + " (" + TeamsEntry.COLUMN_TEAM_ID + "), " +

                " UNIQUE (" + PlayersEntry.COLUMN_ROSTER_ID + ") ON CONFLICT REPLACE);";


        sqLiteDatabase.execSQL(SQL_CREATE_ROSTER_TABLE);

                // To assure the application have different team_id for each entry



        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TeamsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlayersEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}