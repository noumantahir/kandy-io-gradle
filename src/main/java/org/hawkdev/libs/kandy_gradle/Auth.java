package org.hawkdev.libs.kandy_gradle;

import com.genband.kandy.api.Kandy;
import com.genband.kandy.api.access.KandyConnectionState;
import com.genband.kandy.api.access.KandyLoginResponseListener;
import com.genband.kandy.api.access.KandyLogoutResponseListener;
import com.genband.kandy.api.services.calls.KandyRecord;
import com.genband.kandy.api.utils.KandyIllegalArgumentException;


/**
 * Created by nomo on 24/03/2017.
 * This class handles the authenticated related methods for Kandy SDK, it uses the standards methods
 * from the sdk itself to perform relevant actions
 */

public class Auth {

    public static final int STATUS_CODE_LOGGED_OUT = 0;
    public static final int STATUS_CODE_LOGGED_IN = 1;
    public static final int STATUS_CODE_FAILURE = 2;


    public interface AuthStateListener{
        void onLoginStatusChanged(int statusCode, String msg);
    }

    /**
     * checks if kandy instance is connected to server or not
     *
     * @return true if connected else false
     */
    public boolean isKandyConnected(){
        KandyConnectionState kandyConnectionState = Kandy.getAccess().getConnectionState();
        return kandyConnectionState.equals(KandyConnectionState.CONNECTED);
    }

    /**
     * uses kandy user name and password along with state listener to login to kandy, this step is
     * essentional before using any of kandy feature

     * @param username kandy username for this user
     * @param password password for kandy username
     * @param authStateListener response listener
     */
    public void login(String username, String password, final AuthStateListener authStateListener){
        KandyRecord kandyRecord = null;
        try {
            kandyRecord = new KandyRecord(username);
        } catch (KandyIllegalArgumentException e) {
            return;
        }

        Kandy.getAccess().login(kandyRecord, password, new KandyLoginResponseListener() {

            @Override
            public void onRequestFailed(int responseCode, String err) {
                if(authStateListener != null){
                    authStateListener.onLoginStatusChanged(responseCode, err);
                }
            }

            @Override
            public void onLoginSucceeded() {
                if(authStateListener != null){
                    authStateListener.onLoginStatusChanged(STATUS_CODE_LOGGED_IN, "Logged in successfully");
                }
            }
        });
    }


    /**
     * log use out from kandy, uses auth listener for returning status

     * @param authStateListener response listener
     */
    public void logout(final AuthStateListener authStateListener){
        Kandy.getAccess().logout(new KandyLogoutResponseListener() {

            @Override
            public void onRequestFailed(int responseCode, String err) {
                if(authStateListener != null){
                    authStateListener.onLoginStatusChanged(responseCode, err);
                }
            }

            @Override
            public void onLogoutSucceeded() {
                if(authStateListener != null){
                    authStateListener.onLoginStatusChanged(STATUS_CODE_LOGGED_OUT, "Logged out successfully");
                }
            }
        });
    }

}
