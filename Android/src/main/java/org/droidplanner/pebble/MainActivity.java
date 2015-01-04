package org.droidplanner.pebble;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


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
        manualWatchappInstall();
    }

    /**
     * Pebble Install Button. When clicked, will check for pebble if pebble is
     * not present, error displayed. If it is, the pbw (pebble bundle) will be
     * copied from assets to external memory (makes sure to overwrite), and
     * sends pbw intent for pebble app to install bundle.
     */
    public boolean manualWatchappInstall() {
        final Context context = getApplicationContext();
        if (PebbleKit.isWatchConnected(context)) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = context.getAssets().open("DroidPlanner.pbw");
                File outFile = new File(Environment.getExternalStorageDirectory().getPath(),
                        "DroidPlanner.pbw");
                out = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.fromFile(outFile));
                intent.setClassName("com.getpebble.android",
                        "com.getpebble.android.ui.UpdateActivity");
                startActivity(intent);
            } catch (IOException e) {
                Log.e("pebble", "Failed to copy pbw asset", e);
                Toast.makeText(context, "Failed to copy pbw asset", Toast.LENGTH_SHORT)
                        .show();
            } catch (ActivityNotFoundException e) {
                Log.e("pebble", "Pebble App Not installed", e);
                Toast.makeText(context, "Pebble App Not installed", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            Toast.makeText(context, "No Pebble Connected", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

}
