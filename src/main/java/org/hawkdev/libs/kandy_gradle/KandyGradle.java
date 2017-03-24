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
 * this class is the primary point of intraction with kandy gradle library, it initializes the kandy sdk,
 * gives access to various kandy features like initialization, authentication, sms, and calling etc
 */

public class KandyGradle {


    /**initilizes kandy sdk with api and secret, it fetches the information from meta-data storeed
     * in the AndroidManifest.xml file
     *
     * @param context context
     * @return instanse of kandygradle
     */
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


    /**
     * returns instance for Auth, used to log in , log out and check kandy connection statis

     * @return Auth instance
     */
    public static Auth getAuth(){
        return new Auth();
    }

    /**
     * reuturns the sms service instance for sending sms using cellular network

     * @return SmsService instance
     */
    public static SmsService getSmsService(){
        return new SmsService();
    }


    /**
     * creats and returns the calling service instance, its takes in kandy gradle video view for local and remote
     * views along with incoming calls and notification listeners
     * @param localKandyView localKandyView
     * @param remoteKandyView remoteKandyView
     * @param callNotificationListener callNotificationListener
     * @return Calling Service instance
     */
    public static CallingService newCallingService(KandyView localKandyView, KandyView remoteKandyView,
                                                   CallingService.CallNotificationListener callNotificationListener){
        return new CallingService(localKandyView, remoteKandyView, callNotificationListener);
    }


}
