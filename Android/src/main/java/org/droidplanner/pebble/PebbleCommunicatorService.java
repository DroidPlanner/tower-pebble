package org.droidplanner.pebble;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PebbleCommunicatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy(){

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid){
        Intent intents = new Intent(getBaseContext(),MainActivity.class);
        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intents);

        return START_STICKY;
    }
}
