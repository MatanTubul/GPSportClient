package com.example.matant.gpsportclient.GoogleCloudNotifications;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by matant on 10/28/2015.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getPackageName(),
                GCMIntentService.class.getName());
        context.startService((intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }

}
