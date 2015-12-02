package com.example.x61224.nfl.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by x61224 on 11/23/2015.
 */
public class NFLAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private NFLAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new NFLAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
