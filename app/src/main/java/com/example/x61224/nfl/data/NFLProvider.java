package com.example.x61224.nfl.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by x61224 on 11/20/2015.
 */
public class NFLProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private NFLDbHelper mOpenHelper;

    static final int TEAMS = 100;
    static final int TEAM_WITH_ID = 101;

    static UriMatcher buildUriMatcher() {
        // match uri (expression) with some string to facilitate
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = NFLContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        // content_authority/teams
        matcher.addURI(authority, NFLContract.PATH_TEAMS, TEAMS);
        // content_authority/teams/id
        matcher.addURI(authority, NFLContract.PATH_TEAMS + "/*", TEAM_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new NFLDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case TEAM_WITH_ID:
                return NFLContract.TeamsEntry.CONTENT_ITEM_TYPE;
            case TEAMS:
                return NFLContract.TeamsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // teams/id
            case TEAM_WITH_ID: {
                // teams.team_id = ?
                selection = NFLContract.TeamsEntry.TABLE_NAME +
                        "." + NFLContract.TeamsEntry.COLUMN_TEAM_ID + " = ?";
                // id
                selectionArgs = new String[]{NFLContract.TeamsEntry.getIdFromUri(uri)};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        NFLContract.TeamsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // teams
            case TEAMS: {
                if(selection.equals("division")) {
                    selection = NFLContract.TeamsEntry.TABLE_NAME +
                            "." + NFLContract.TeamsEntry.COLUMN_DIVISION + " = ?";
                    sortOrder = NFLContract.TeamsEntry.COLUMN_RANK_DIVISION + " ASC";
                }
                if(selection.equals("conference")) {
                    selection = NFLContract.TeamsEntry.TABLE_NAME +
                            "." + NFLContract.TeamsEntry.COLUMN_CONFERENCE + " = ?";
                    sortOrder = NFLContract.TeamsEntry.COLUMN_RANK_CONFERENCE + " ASC";
                }
                retCursor = mOpenHelper.getReadableDatabase().query(
                        NFLContract.TeamsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case TEAMS: {
                long _id = db.insert(NFLContract.TeamsEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = NFLContract.TeamsEntry.buildTeamUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case TEAMS:
                rowsDeleted = db.delete(
                        NFLContract.TeamsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case TEAMS:
                rowsUpdated = db.update(NFLContract.TeamsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount;
        switch (match) {
            case TEAMS: {
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(NFLContract.TeamsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
