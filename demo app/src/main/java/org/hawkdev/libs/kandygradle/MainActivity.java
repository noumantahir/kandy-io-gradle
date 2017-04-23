package org.hawkdev.libs.kandygradle;

        import android.Manifest;
        import android.content.pm.PackageManager;
        import android.support.v4.app.ActivityCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.genband.kandy.api.services.calls.IKandyCall;
        import com.genband.kandy.api.services.calls.KandyCallState;

        import org.hawkdev.libs.kandy_gradle.Auth;
        import org.hawkdev.libs.kandy_gradle.CallingService;
        import org.hawkdev.libs.kandy_gradle.KandyGradle;
        import org.hawkdev.libs.kandy_gradle.KandyGradleView;
        import org.hawkdev.libs.kandy_gradle.SmsService;

public class MainActivity extends AppCompatActivity implements Auth.AuthStateListener {

    private static final int PERMISSION_REQUEST = 123;
    private CallingService mCallingService;
    private boolean isLoggedIn;

    TextView tvCallStatus;
    private TextView tvLoginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCallStatus = (TextView) findViewById(R.id.tvCallStatus);
        tvLoginStatus = (TextView) findViewById(R.id.tvLoginStatus);

        //check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {


            //code for requesting permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST);

        } else {

            //initialize kandy else wise
            KandyGradle.init(this);
            initializeCallingService();
        }
    }

    //initialize calling server
    private void initializeCallingService() {
        KandyGradleView localView = (KandyGradleView) findViewById(R.id.localVideoView);
        KandyGradleView remoteView = (KandyGradleView) findViewById(R.id.remoteViewView);
        mCallingService = KandyGradle.newCallingService(localView, remoteView, new CallingService.CallNotificationListener() {
            @Override
            public void onIncomingVideoCall() {
                //TODO: show relevant UI
                tvCallStatus.setText("incoming video call");
            }

            @Override
            public void onIncomingVoiceCall() {
                //TODO: show relevant UI
                tvCallStatus.setText("incoming voice call");
            }

            @Override
            public void onMissedCall() {
                //TODO: show relevant ui
                tvCallStatus.setText("call missed");
            }

            @Override
            public void onCallStateChanged(KandyCallState kandyCallState) {
                //TODO: handle states KandyCallState is an enum
                //tvCallStatus.setText("call status changed");
            }
        });
    }


    //login user using kandy username and password
    public void loginUser(View view){
        EditText etUsername = (EditText) findViewById(R.id.etUsername);
        EditText etPassword = (EditText) findViewById(R.id.etPassword);


        KandyGradle.getAuth().login(etUsername.getText().toString(), etPassword.getText().toString(), this);

        Toast.makeText(this, "Logging in", Toast.LENGTH_LONG).show();

    }


    //log out a logged in user
    public void logoutUser(View view){
//        if(KandyGradle.getAuth().isKandyConnected()){
        KandyGradle.getAuth().logout(this);
//        }
    }


    //send sms to phone number entered in the text field along with sms body
    public void sendSms(View view){
        EditText etPhone = (EditText) findViewById(R.id.etPhone);
        EditText etBody = (EditText) findViewById(R.id.etSms);

        KandyGradle.getSmsService().sendSMS(etPhone.getText().toString(), etBody.getText().toString(), new SmsService.SmsResponseListener() {
            @Override
            public void response(int responseCode, final String responseMsg) {
                //TODO: in library the flags for this function is accidently set to private, set them to public
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, responseMsg, Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }




    //start a video call with other specified number
    public void makeVideoCall(View view){
        EditText etCallUser = (EditText) findViewById(R.id.etCallUser);
        String username = etCallUser.getText().toString();

        mCallingService.makeVideoCall(username, new CallingService.CallRequestResponseListener() {
            @Override
            public void response(int responseCode, String responseMsg, IKandyCall call) {
                if(responseCode == CallingService.RESPONSE_REQUEST_SUCCESS)
                    tvCallStatus.setText("Making video call");
            }
        });
    }

    //start a voice call with other specified number
    public void makeVoiceCall(View view){
        EditText etCallUser = (EditText) findViewById(R.id.etCallUser);
        String username = etCallUser.getText().toString();

        mCallingService.makeVoiceCall(username, new CallingService.CallRequestResponseListener() {
            @Override
            public void response(int responseCode, String responseMsg, IKandyCall call) {
                if(responseCode == CallingService.RESPONSE_REQUEST_SUCCESS)
                    tvCallStatus.setText("Making voice call");
            }
        });
    }

    //start a pstn call with other specified number
    public void makePstnCall(View view){
        EditText etCallUser = (EditText) findViewById(R.id.etCallUser);
        String phone = etCallUser.getText().toString();

        mCallingService.makePstnCall(phone, new CallingService.CallRequestResponseListener() {
            @Override
            public void response(int responseCode, String responseMsg, IKandyCall call) {
                if(responseCode == CallingService.RESPONSE_REQUEST_SUCCESS)
                    tvCallStatus.setText("Making pstn call");
            }
        });
    }



    //receive a call if its incoming, for demo there is no dedicated checks
    public void receiveCall(View view){
        mCallingService.receiveCall(new CallingService.CallRequestResponseListener() {
            @Override
            public void response(int responseCode, String responseMsg, IKandyCall call) {
                if(responseCode == CallingService.RESPONSE_REQUEST_SUCCESS)
                    tvCallStatus.setText("Ongoing Call");
            }
        });
    }


    //end on going call, if there is an on going call
    public void endCall(View view){
        mCallingService.endCall(new CallingService.CallRequestResponseListener() {
            @Override
            public void response(int responseCode, String responseMsg, IKandyCall call) {
                if(responseCode == CallingService.RESPONSE_REQUEST_SUCCESS)
                    tvCallStatus.setText("call ended");
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


    //permission result handler
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(MainActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //initialize kandy if permission granted
                    KandyGradle.init(this);

                }
            }

        }
    }


    //callback for login status changeds
    @Override
    public void onLoginStatusChanged(int statusCode, String msg) {
        switch (statusCode){
            case Auth.STATUS_CODE_LOGGED_IN:
                isLoggedIn = true;
                tvLoginStatus.setText("Logged in");
                Toast.makeText(this, "Log in successful", Toast.LENGTH_LONG).show();
                break;

            case Auth.STATUS_CODE_LOGGED_OUT:
                isLoggedIn = false;
                tvLoginStatus.setText("Logged out");
                Toast.makeText(this, "Log out successful", Toast.LENGTH_LONG).show();

                break;

            case Auth.STATUS_CODE_FAILURE:
                tvLoginStatus.setText("Login Failed");
                Toast.makeText(this, "Log in failed", Toast.LENGTH_LONG).show();

                break;

        }
    }

}

