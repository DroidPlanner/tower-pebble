package org.droidplanner.pebble;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.o3dr.android.client.Drone;
import com.o3dr.android.client.ServiceManager;
import com.o3dr.android.client.interfaces.DroneListener;
import com.o3dr.android.client.interfaces.ServiceListener;
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent;
import com.o3dr.services.android.lib.drone.connection.ConnectionParameter;
import com.o3dr.services.android.lib.drone.connection.ConnectionResult;
import com.o3dr.services.android.lib.drone.connection.ConnectionType;
import com.o3dr.services.android.lib.drone.connection.DroneSharePrefs;
import com.o3dr.services.android.lib.drone.connection.StreamRates;

public class MainActivity extends ActionBarActivity implements DroneListener, ServiceListener{
    PebbleNotificationProvider pebbleNotificationProvider;
    ServiceManager serviceManager;
    Drone drone;

    //TODO use this eventFilter
    public final static IntentFilter eventFilter = new IntentFilter();
    static {
        eventFilter.addAction(AttributeEvent.STATE_CONNECTED);
        eventFilter.addAction(AttributeEvent.STATE_VEHICLE_MODE);
        eventFilter.addAction(AttributeEvent.BATTERY_UPDATED);
        eventFilter.addAction(AttributeEvent.SPEED_UPDATED);
        eventFilter.addAction(AttributeEvent.FOLLOW_UPDATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        pebbleNotificationProvider = new PebbleNotificationProvider(getApplicationContext(), this, drone);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected() {
        if(!drone.isStarted()) {
            this.drone.start();
            this.drone.registerDroneListener(this);
        }
        if(!drone.isConnected()){
            Bundle extraParams = new Bundle();
            extraParams.putInt(ConnectionType.EXTRA_USB_BAUD_RATE, 57600);
            final StreamRates streamRates = new StreamRates(10);
            DroneSharePrefs droneSharePrefs = new DroneSharePrefs("","",false,false);
            drone.connect(new ConnectionParameter(ConnectionType.TYPE_USB,extraParams,streamRates,droneSharePrefs));
        }
    }

    @Override
    public void onServiceInterrupted() {

    }

    @Override
    public void onDroneConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onDroneEvent(String event, Bundle bundle) {
        if (AttributeEvent.STATE_DISCONNECTED.equals(event) && pebbleNotificationProvider != null){
            pebbleNotificationProvider.onTerminate();
            pebbleNotificationProvider=null;
        }else if(pebbleNotificationProvider == null) {
            pebbleNotificationProvider = new PebbleNotificationProvider(getApplicationContext(), this, drone);
        }

        if(pebbleNotificationProvider != null){
            pebbleNotificationProvider.processData(new Intent(event));
        }
    }

    @Override
    public void onDroneServiceInterrupted(String s) {
        drone.destroy();
    }

    public void disconnectDroneAnd3DRServices() {
        if(drone.isConnected()){
            drone.disconnect();
        }
        if(serviceManager.isServiceConnected()){
            serviceManager.disconnect();
        }
    }

    public static class PlaceholderFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    public void installWatchapp(View view){
        OfflineWatchappInstallUtil.manualWatchappInstall(getApplicationContext());
    }

    public void connect3DRServices(){
        serviceManager = new ServiceManager(getApplicationContext());
        final Handler handler = new Handler();
        drone = new Drone(serviceManager,handler);
        serviceManager.connect(this);
        drone.registerDroneListener(this);
    }
}
