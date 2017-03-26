package org.hawkdev.apps.kandylibrarytest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.genband.kandy.api.services.calls.IKandyCall;
import com.genband.kandy.api.services.calls.KandyCallState;

import org.hawkdev.libs.kandy_gradle.*;

public class MainActivity extends AppCompatActivity implements Auth.AuthStateListener {

    private static final int PERMISSION_REQUEST = 122;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {


            //code for requesting permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE,
                            android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        } else {

            KandyGradle.init(this);
        }
    }

    private void loginUser(){
        if(KandyGradle.getAuth().isKandyConnected()){
            KandyGradle.getAuth().login("username", "password", this);
        }
    }

    private void logoutUser(){
        if(KandyGradle.getAuth().isKandyConnected()){
            KandyGradle.getAuth().logout(this);
        }
    }

    private void sendSms(){
        KandyGradle.getSmsService().sendSMS("phone number", "messageText", new SmsService.SmsResponseListener() {
            @Override
            public void response(int responseCode, String responseMsg) {

            }
        });
    }



    private void initializeCallingService() {
        KandyGradleView localView = (KandyGradleView) findViewById(R.id.localVideoView);
        KandyGradleView remoteView = (KandyGradleView) findViewById(R.id.remoteViewView);
        mCallingService = KandyGradle.newCallingService(localView, remoteView, new CallingService.CallNotificationListener() {
            @Override
            public void onIncomingVideoCall() {
                //TODO: show relevant UI
            }

            @Override
            public void onIncomingVoiceCall() {
                //TODO: show relevant UI
            }

            @Override
            public void onMissedCall() {
                //TODO: show relevant ui
            }

            @Override
            public void onCallStateChanged(KandyCallState kandyCallState) {
                //TODO: handle states KandyCallState is an enum
            }
        });
    }



    private void makeVideoCall(){
        mCallingService.makeVideoCall("username", new CallingService.CallRequestResponseListener() {
            @Override
            public void response(int responseCode, String responseMsg, IKandyCall call) {

            }
        });
    }


    private void receiveCall(){
        mCallingService.receiveCall(new CallingService.CallRequestResponseListener() {
            @Override
            public void response(int responseCode, String responseMsg, IKandyCall call) {

            }
        });
    }


    private void endCall(){
        mCallingService.endCall(new CallingService.CallRequestResponseListener() {
            @Override
            public void response(int responseCode, String responseMsg, IKandyCall call) {

            }
        });
    }

    private void makeVoiceCall(){
        mCallingService.makeVoiceCall("username", new CallingService.CallRequestResponseListener() {
            @Override
            public void response(int responseCode, String responseMsg, IKandyCall call) {

            }
        });
    }

    private void makePstnCall(){
        mCallingService.makePstnCall("username", new CallingService.CallRequestResponseListener() {
            @Override
            public void response(int responseCode, String responseMsg, IKandyCall call) {

            }
        });
    }



    @Override
    protected void onStop() {
        super.onStop();
        if(mCallingService != null){
            mCallingService.onStop();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(MainActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    KandyGradle.init(this);

                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onLoginStatusChanged(int statusCode, String msg) {
        switch (statusCode){
            case Auth.STATUS_CODE_LOGGED_IN:
                isLoggedIn = true;
                break;

            case Auth.STATUS_CODE_LOGGED_OUT:
                isLoggedIn = false;
                break;

            case Auth.STATUS_CODE_FAILURE:

                break;

        }
    }

}

}
