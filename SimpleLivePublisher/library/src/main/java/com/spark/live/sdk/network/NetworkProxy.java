package com.spark.live.sdk.network;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class NetworkProxy extends IntentService {

    public NetworkProxy() {
        super("NetworkProxy");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {


        }
    }


}
