package org.droidplanner.pebble;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OfflineWatchappInstallUtil {

    /**
     * Pebble Install Button. When clicked, will check for pebble if pebble is
     * not present, error displayed. If it is, the pbw (pebble bundle) will be
     * copied from assets to external memory (makes sure to overwrite), and
     * sends pbw intent for pebble app to install bundle.
     */
    public static boolean manualWatchappInstall(Context context) {
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
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
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
