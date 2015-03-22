package org.droidplanner.pebble;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;

public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        //Start the service every time the activity is started and asked it to check the current connection state.
        startService(new Intent(getApplicationContext(), PebbleCommunicatorService.class).setAction
                (PebbleCommunicatorService.ACTION_CHECK_CONNECTION_STATE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_report_issue:
                openIssueTracker();
                return true;

            default:
                return super.onOptionsItemSelected(item);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_install_method)
                .setItems(R.array.install_methods_array, new DialogInterface.OnClickListener(){
                   public void onClick(DialogInterface dialog, int which){
                       switch(which){
                           case 0://app store
                               String url = "pebble://appstore/54d54fede8bb36ea9d00001f";
                               Intent i = new Intent(Intent.ACTION_VIEW);
                               i.setData(Uri.parse(url));
                               //if pebble app store is installed
                               if(getPackageManager().queryIntentActivities(
                                       i, PackageManager.MATCH_DEFAULT_ONLY).size()>0){
                                   startActivity(i);
                               }else {
                                   Toast.makeText(
                                           getApplicationContext(),
                                           R.string.pebble_app_not_installed,
                                           Toast.LENGTH_LONG)
                                           .show();
                               }
                               break;
                           case 1://offline
                               OfflineWatchappInstallUtil.manualWatchappInstall(getApplicationContext());
                               break;
                       }
                   }
                });
        builder.create();
        builder.show();
    }

    public void openIssueTracker(){
        String url = "https://github.com/DroidPlanner/dp-pebble/issues";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
