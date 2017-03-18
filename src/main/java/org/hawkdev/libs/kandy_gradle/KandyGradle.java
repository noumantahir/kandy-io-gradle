package org.hawkdev.libs.kandy_gradle;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.genband.kandy.api.Kandy;

import static android.Manifest.permission;



/**
 * Created by nomo on 18/03/2017.
 */

public class KandyGradle {



    @RequiresPermission(allOf = {permission.READ_PHONE_STATE, permission.CAMERA, permission.RECORD_AUDIO,
            permission.WAKE_LOCK, permission.ACCESS_NETWORK_STATE, permission.INTERNET })
    public static void init(Context context) {

        //look for meta data
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            if(info != null && info.metaData != null) {
                //pull kandy meta information
                String apiKey = (String)info.metaData.get("KANDY_API_KEY");
                String apiSecret = (String)info.metaData.get("KANDY_API_SECRET");

                //initialize kandy
                Kandy.initialize(context, apiKey, apiSecret);

            }
        } catch (PackageManager.NameNotFoundException var4) {
            //throw exception if data not found
            var4.printStackTrace();
        }

    }


}
