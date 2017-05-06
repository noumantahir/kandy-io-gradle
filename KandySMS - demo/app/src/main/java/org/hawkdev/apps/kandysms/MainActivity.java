package org.hawkdev.apps.kandysms;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.hawkdev.libs.kandy_gradle.Auth;
import org.hawkdev.libs.kandy_gradle.KandyGradle;
import org.hawkdev.libs.kandy_gradle.SmsService;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST);
            return;
        }

        KandyGradle.init(this);
    }


    public void loginUser(View view){
        EditText etUsername = (EditText) findViewById(R.id.etUsername);
        EditText etPassword = (EditText) findViewById(R.id.etPassword);


        KandyGradle.getAuth().login(etUsername.getText().toString(), etPassword.getText().toString(), new Auth.AuthStateListener() {
            @Override
            public void onLoginStatusChanged(int statusCode, String msg) {

                switch (statusCode) {
                    case Auth.STATUS_CODE_LOGGED_IN:
                        Toast.makeText(MainActivity.this, "Log in successful", Toast.LENGTH_LONG).show();
                        break;
                    case Auth.STATUS_CODE_FAILURE:
                        Toast.makeText(MainActivity.this, "Log in failed", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }



    public void sendSms(View view){
        EditText etPhone = (EditText) findViewById(R.id.etPhone);
        EditText etBody = (EditText) findViewById(R.id.etSms);

        String etPhoneString = etPhone.getText().toString();
        String etBodyString = etBody.getText().toString();

        KandyGradle.getSmsService().sendSMS(etPhoneString, etBodyString, new SmsService.SmsResponseListener() {
            @Override
            public void response(int responseCode, final String responseMsg) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, responseMsg, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }




    //permission result handler
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    KandyGradle.init(this);
                }
            }
        }

    }


}
