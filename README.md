# kandy-io-gradle

Purpose of this project will be to develop an easily integratable library for Android. Although there is already a Kandy SDK for android but the purpose of this version will be to make the integration process seamless using Gradle Build system.

Once its done, I ll either try for writing a tutorial either using Android Things OS for Raspberry Pi 3, that ll be integration with HomeBot (another of my project on Collaborizm), or atleast a tutorial and sample app using simple Android OS for phones

Usage Android Studio
--------

Add this to project level build.gradle file
```gradle
repositories {
  maven {
            url 'https://dl.bintray.com/noumanhawkdev/openlibs/'
        }
}
```

Add this to app level build.gradle file
```
dependencies {
    compile 'org.hawkdev.libs:kandy-gradle:1.2-alpha'
}
```

obtain api key using this link, https://developer.kandy.io/tutorials/android/1-8-25#quick-start-step-2-obtain-a-kandy-domain-key-and-secret, 

add kandy API Key and API secret to AndroidManifest.xml inside <application> tag
```
<meta-data android:name="KANDY_API_KEY" android:value="ADD API KEY HERE"/>
<meta-data android:name="KANDY_API_SECRET" android:value="ADD API SECRET HERE"/>
```

Next you need to call KandyGradle.init() inside your activity or Application class
```
KandyGradle.init(this);
```
Add this will automatically prompt to add code for necessary permission check, also this library will add all required permissions in Manifest automatically.

Boiler Plate Code
--------
This code will check for permission, ask for granting permission and initilaize kandy 

```
    private static final int PERMISSION_REQUEST = 123;
.
.
.
.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

.
.
.

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
        }
}
.
.
.
.

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
```

What's next
--------

for now follow this link, https://developer.kandy.io/tutorials/android/1-8-25#quick-start-step-3-configure-your-android-project

Skip the initilze part from page

# Collaborizm Project Link
https://www.collaborizm.com/project/Sye8rBKix 
