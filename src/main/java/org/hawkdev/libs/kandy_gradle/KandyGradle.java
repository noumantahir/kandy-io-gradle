package org.hawkdev.libs.kandy_gradle;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.RequiresPermission;

import com.genband.kandy.api.Kandy;
import com.genband.kandy.api.services.calls.KandyCallServiceNotificationListener;
import com.genband.kandy.api.services.calls.KandyView;

import static android.Manifest.permission;



/**
 * Created by nomo on 18/03/2017.
 */

public class KandyGradle {

    @RequiresPermission(allOf = {permission.READ_PHONE_STATE, permission.CAMERA, permission.RECORD_AUDIO,
            permission.WAKE_LOCK, permission.ACCESS_NETWORK_STATE, permission.INTERNET })
    public static KandyGradle init(Context context) {

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

            return null;
        }

        return new KandyGradle();
    }

    public static Auth getAuth(){
        return new Auth();
    }

    public static SmsService getSmsService(){
        return new SmsService();
    }

    public static CallingService newCallingService(KandyView localKandyView, KandyView remoteKandyView){
        return new CallingService(localKandyView, remoteKandyView);
    }

    public static void registerCallReceiverListener( KandyCallServiceNotificationListener callListener){
        Kandy.getServices().getCallService().registerNotificationListener(callListener);
    }

    public static void unregisterCallReceiverListener( KandyCallServiceNotificationListener callListener){
        Kandy.getServices().getCallService().unregisterNotificationListener(callListener);
        callListener = null;
    }

}
