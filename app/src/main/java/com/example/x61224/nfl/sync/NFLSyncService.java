package com.example.x61224.nfl.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by x61224 on 11/23/2015.
 */
public class NFLSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static NFLSyncAdapter sNFLSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("NFLSyncService", "onCreate - NFLSyncService");
        synchronized (sSyncAdapterLock) {
            if (sNFLSyncAdapter == null) {
                sNFLSyncAdapter = new NFLSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sNFLSyncAdapter.getSyncAdapterBinder();
    }
}