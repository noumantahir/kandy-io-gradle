# kandy-io-gradle

Purpose of this project will be to develop an easily integratable library for Android. Although there is already a Kandy SDK for android but the purpose of this version will be to make the integration process seamless using Gradle Build system.

Once its done, I ll either try for writing a tutorial either using Android Things OS for Raspberry Pi 3, that ll be integration with HomeBot (another of my project on Collaborizm), or atleast a tutorial and sample app using simple Android OS for phones

Usage Android Studio
--------

Add this to dependencies of your app level build.gradle file and thats it, you have Kandy SDK + wrapper library integrated.
```
  compile 'org.hawkdev.libs:kandy-io-gradle:0.5-alpha'
    
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

##**Logging user into Kandy**

Once initialized first thing to do will be loggin into user, for this take username and password
use this simple method to login. for making is easy to handle state changes, implementing class with state change listeners is good idea

```

    private CallingService mCallingService;
    .
    .
    .
    public class MainActivity extends AppCompatActivity implements Auth.AuthStateListener {
    
     .
     .
     .
     
    }
```
This will make you add simple callback for login state change to your class to your class

```
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
```  

Then you can use this simple method to log user into kandy

    KandyGradle.getAuth().login("username", "password", this);

To logout use this method

```
    KandyGradle.getAuth().logout(this);
```

this ll also throw a callback to state change listener

Its a good practise to check for Kandy Connection state before making any calls, for this you can use
this simple check

```
    if(KandyGradle.getAuth().isKandyConnected()){
        //TODO: do you stuf like calls inside
    }
```


##**Sending an SMS**

After setting up and logging in, the simplest thing to do can be sending an SMS to a phoneNumber using 
Kandy

```
        KandyGradle.getSmsService().sendSMS("phone number", "messageText", new SmsService.SmsResponseListener() {
             @Override
             public void response(int responseCode, String responseMsg) {
                //TODO: use codes from SmsSerivce class to easily analyze the reponse
             }
         });
```


##**Getting Ready for phone calls**

This wrapper makes sure the making phone calls is way more simplified than the actual sdk, one might 
argue this has limited the functionality of actual sdk but not to forget you can always access Kandy 
Legacy SDK features any time as well

First step is to initialize the CallingService and get and instance of it
 
 ```
 private void initializeCallingService() {
         KandyGradleView localView = (KandyGradleView) findViewById(R.id.localVideoView);
         KandyGradleView remoteView = (KandyGradleView) findViewById(R.id.remoteViewView);
         mCallingService = KandyGradle.newCallingService(localView, remoteView, new CallingService.CallNotificationListener() {
             @Override
             public void onIncomingVideoCall() {
                 //TODO: show relevant UI for incoming video call
             }
 
             @Override
             public void onIncomingVoiceCall() {
                 //TODO: show relevant UI for incoming voice
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
 
 ```

This takes two views of showing kandy videos for local and remote videos during calls and and simplified 
callback listener

A sample xml for video widgets is given below

```
    <org.hawkdev.libs.kandy_gradle.KandyGradleView
        android:id="@+id/localVideoView"
        android:layout_width="100dp"
        android:layout_height="100dp" />

    <org.hawkdev.libs.kandy_gradle.KandyGradleView
        android:id="@+id/remoteViewView"
        android:layout_width="100dp"
        android:layout_height="100dp" />
```

##**Making Calls**

This have become dramatically simpler, you really dont have deal with different methods to make simple call,
below is the code for making different types of calls

```
    private void makeVideoCall(){
        mCallingService.makeVideoCall("username", new CallingService.CallRequestResponseListener() {
            @Override
            public void response(int responseCode, String responseMsg, IKandyCall call) {
                //TODO: use response codes from CallingService class to easity analyze the response   }
        });
    }
    
    private void makeVoiceCall(){
        mCallingService.makeVoiceCall("username", new CallingService.CallRequestResponseListener() {
            @Override
            public void response(int responseCode, String responseMsg, IKandyCall call) {
                //TODO: use response codes from CallingService class to easity analyze the response
            }
        });
    }

    private void makePstnCall(){
        mCallingService.makePstnCall("username", new CallingService.CallRequestResponseListener() {
            @Override
            public void response(int responseCode, String responseMsg, IKandyCall call) {
                //TODO: use response codes from CallingService class to easity analyze the response
            }
        });


```

##**Receiving and end a call**

after getting call back in the initialization of calling service, you can easily use these methods,
rest of the stuff can be handled by mCallingService instance

```    
private void receiveCall(){
           mCallingService.receiveCall(new CallingService.CallRequestResponseListener() {
               @Override
               public void response(int responseCode, String responseMsg, IKandyCall call) {
                   //TODO: use response codes from CallingService class to easity analyze the response
               }
           });
       }
   
   
       private void endCall(){
           mCallingService.endCall(new CallingService.CallRequestResponseListener() {
               @Override
               public void response(int responseCode, String responseMsg, IKandyCall call) {
                   //TODO: use response codes from CallingService class to easity analyze the response
               }
           });
       }
       
```

More over you can always use method  `getiKandyIncomingCall()` and `getiKandyCall()` to access detailed information about the call

##**Releaseing Calling Service**
Its fairly important to release CallingService listeners and resources after using it

```    @Override
       protected void onStop() {
           super.onStop();
           if(mCallingService != null){
               mCallingService.onStop();
           }
       }
```


What's next
------

To follow Legacy Kandy SDK documentation user, https://developer.kandy.io/tutorials/android/1-8-25


 Collaborizm Project Link
https://www.collaborizm.com/project/Sye8rBKix 
