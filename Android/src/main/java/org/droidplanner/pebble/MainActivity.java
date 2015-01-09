package org.droidplanner.pebble;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.Toast;

import com.o3dr.android.client.Drone;
import com.o3dr.android.client.ServiceManager;
import com.o3dr.android.client.interfaces.DroneListener;
import com.o3dr.android.client.interfaces.ServiceListener;
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent;
import com.o3dr.services.android.lib.drone.connection.ConnectionResult;



public class MainActivity extends ActionBarActivity implements DroneListener, ServiceListener{
    PebbleNotificationProvider pebbleNotificationProvider;
    ServiceManager serviceManager;
    Drone drone;


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                toast("data");
            }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        serviceManager = new ServiceManager(getApplicationContext());
        serviceManager.connect(this);
        final Handler handler = new Handler();
        drone = new Drone(serviceManager,handler);
        registerReceiver(broadcastReceiver,new IntentFilter());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected() {

    }

    @Override
    public void onServiceInterrupted() {

    }

    @Override
    public void onDroneConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onDroneEvent(String event, Bundle bundle) {
        toast("drone event");
        if (AttributeEvent.STATE_DISCONNECTED.equals(event)){
            pebbleNotificationProvider.onTerminate();
            pebbleNotificationProvider=null;
        }else if(pebbleNotificationProvider == null) {
            pebbleNotificationProvider = new PebbleNotificationProvider(getApplicationContext(), drone);
        }

        if(pebbleNotificationProvider != null){
            pebbleNotificationProvider.processDroneIntent(new Intent(event));
        }
    }

    @Override
    public void onDroneServiceInterrupted(String s) {
        drone.destroy();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
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
    public void toast(String s){
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }
}
