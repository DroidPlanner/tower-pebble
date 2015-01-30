package org.droidplanner.pebble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.o3dr.services.android.lib.drone.connection.ConnectionParameter;
import com.o3dr.services.android.lib.gcs.event.GCSEvent;

public class GCSEventsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent arg) {
        ConnectionParameter connParams = arg.getParcelableExtra(GCSEvent
                .EXTRA_VEHICLE_CONNECTION_PARAMETER);

        Intent intent = new Intent(context, PebbleCommunicatorService.class);
        intent.setAction(arg.getAction());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("extra_connection_parameter", connParams));
        context.startService(intent);
    }
}
