package org.hawkdev.libs.kandy_gradle;

import com.genband.kandy.api.Kandy;
import com.genband.kandy.api.access.KandyConnectionState;
import com.genband.kandy.api.access.KandyLoginResponseListener;
import com.genband.kandy.api.access.KandyLogoutResponseListener;
import com.genband.kandy.api.services.calls.KandyRecord;
import com.genband.kandy.api.services.chats.KandySMSMessage;
import com.genband.kandy.api.services.common.KandyResponseListener;
import com.genband.kandy.api.utils.KandyIllegalArgumentException;

import static com.genband.kandy.api.Kandy.getAccess;

/**
 * Created by nomo on 24/03/2017.
 */

public class Auth {

    private static final int STATUS_CODE_LOGGED_OUT = 0;
    private static final int STATUS_CODE_LOGGED_IN = 1;


    public interface AuthStateListener{
        void onStatusChanged(int statusCode);
        void onFailure(int responseCode, String err);
    }

    public Auth() {
    }

    public boolean isKandyConnected(){
        KandyConnectionState kandyConnectionState = Kandy.getAccess().getConnectionState();
        return kandyConnectionState.equals(KandyConnectionState.CONNECTED);
    }

    public void login(String username, String password, final AuthStateListener authStateListener){
        KandyRecord kandyRecord = null;
        try {
            kandyRecord = new KandyRecord(username);
        } catch (KandyIllegalArgumentException e) {
            //TODO insert your code here
            return;
        }

        getAccess().login(kandyRecord, password, new KandyLoginResponseListener() {

            @Override
            public void onRequestFailed(int responseCode, String err) {
                if(authStateListener != null){
                    authStateListener.onFailure(responseCode, err);
                }
            }

            @Override
            public void onLoginSucceeded() {
                if(authStateListener != null){
                    authStateListener.onStatusChanged(STATUS_CODE_LOGGED_IN);
                }
            }
        });
    }


    public void logout(final AuthStateListener authStateListener){
        getAccess().logout(new KandyLogoutResponseListener() {

            @Override
            public void onRequestFailed(int responseCode, String err) {
                if(authStateListener != null){
                    authStateListener.onFailure(responseCode, err);
                }
            }

            @Override
            public void onLogoutSucceeded() {
                if(authStateListener != null){
                    authStateListener.onStatusChanged(STATUS_CODE_LOGGED_OUT);
                }
            }
        });
    }

}
