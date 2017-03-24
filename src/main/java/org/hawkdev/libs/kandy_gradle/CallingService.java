package org.hawkdev.libs.kandy_gradle;

import com.genband.kandy.api.Kandy;
import com.genband.kandy.api.services.calls.IKandyCall;
import com.genband.kandy.api.services.calls.IKandyIncomingCall;
import com.genband.kandy.api.services.calls.IKandyOutgoingCall;
import com.genband.kandy.api.services.calls.KandyCallResponseListener;
import com.genband.kandy.api.services.calls.KandyCallServiceNotificationListener;
import com.genband.kandy.api.services.calls.KandyCallState;
import com.genband.kandy.api.services.calls.KandyOutgingVoipCallOptions;
import com.genband.kandy.api.services.calls.KandyOutgoingPSTNCallOptions;
import com.genband.kandy.api.services.calls.KandyRecord;
import com.genband.kandy.api.services.calls.KandyView;
import com.genband.kandy.api.services.common.KandyMissedCallMessage;
import com.genband.kandy.api.services.common.KandyWaitingVoiceMailMessage;
import com.genband.kandy.api.utils.KandyIllegalArgumentException;

/**
 * Created by nomo on 24/03/2017.
 * This class handles all operations relevant to making and receiving calls, this class also implements
 * KandyCallServiceNotificationListener and wraps it aroudn another call notificaiton listener whos primary
 * purpose is to simplify the whole call making and receieveing flow
 */

public class CallingService implements KandyCallServiceNotificationListener {

    //some response constant flags used to identify the type of response by
    private static final int RESPONSE_ERROR_ILLEGAL_USERNAME = 0;
    private static final int RESPONSE_ERROR_INVALID_KANDY_VIEWS = 1;
    public static final int RESPONSE_REQUEST_SUCCESS = 2;
    private static final int RESPONSE_REQUEST_FAILURE = 3;
    private static final int CALL_RESPONSE_ACCEPTED = 4;
    private static final int CALL_RESPONSE_FAILED_TO_ACCEPT = 5;

    //these instances help manage calls and also accessible to the parent class having the instance
    //of CallingService itself
    private IKandyIncomingCall iKandyIncomingCall;
    private IKandyCall iKandyCall;

    //local and remote kandy views for showing videos
    private KandyView mLocalVideoView;
    private KandyView mRemoteVideoView;

    //call notifications listener
    private CallNotificationListener callNotificaitonListener;


    public interface CallRequestResponseListener {
        void response(int responseCode, String responseMsg, IKandyCall call);
    }

    /**
     * interface that use original Call Notificaiton from and kind of limit its code footprint to developer
     * to kind make thing look simpler and easy
     */
    public interface CallNotificationListener{
        void onIncomingVideoCall();
        void onIncomingVoiceCall();
        void onMissedCall();
        void onCallStateChanged(KandyCallState kandyCallState);
    }


    /**
     * initilizes calling sever with basic kandy views and listener for calls status


     * @param mLocalVideoView local Video View
     * @param mRemoteVideoView remote Video view
     * @param callNotificationsListener call notification listener
     */
    public CallingService(KandyView mLocalVideoView, KandyView mRemoteVideoView,
                          CallNotificationListener callNotificationsListener) {

        this.mLocalVideoView = mLocalVideoView;
        this.mRemoteVideoView = mRemoteVideoView;
        this.callNotificaitonListener = callNotificationsListener;

        Kandy.getServices().getCallService().registerNotificationListener(this);
    }


    /**
     * checks if there is an incomming call or not , if so receives it and sends a response back on
     * success

     * @param callResponseListener callResponseListner
     */
    public void receiveCall(final CallRequestResponseListener callResponseListener) {
        if(iKandyIncomingCall != null) {
            if (iKandyIncomingCall.isVideoCall())
                iKandyIncomingCall.accept(true, new KandyCallResponseListener() {
                    @Override
                    public void onRequestSucceeded(IKandyCall iKandyCall) {
                        if (callResponseListener != null)
                            callResponseListener.response(CALL_RESPONSE_ACCEPTED, "call accepted", iKandyCall);
                    }

                    @Override
                    public void onRequestFailed(IKandyCall iKandyCall, int i, String s) {
                        if (callResponseListener != null)
                            callResponseListener.response(CALL_RESPONSE_FAILED_TO_ACCEPT, "call accept failed", iKandyCall);
                    }
                });
        }
        else
            return;

    }

    /**checks if iKandy is not null means call is under operation, and hangup the call ending the session
     * and also setting kandyCall instance to null
     *
     * @param callResponseListener callResponseListener
     */
    public void endCall(final CallRequestResponseListener callResponseListener) {
        if(iKandyCall != null){
            iKandyCall.hangup(new KandyCallResponseListener() {
                @Override
                public void onRequestSucceeded(IKandyCall iKandyCall) {
                    if(callResponseListener != null)
                        callResponseListener.response(CALL_RESPONSE_ACCEPTED, "call ended", iKandyCall);

                    iKandyCall = null;
                }

                @Override
                public void onRequestFailed(IKandyCall iKandyCall, int i, String s) {
                    if(callResponseListener != null)
                    callResponseListener.response(CALL_RESPONSE_FAILED_TO_ACCEPT, "failed to reject call", iKandyCall);
                }
            });
        }
    }


    /**
     * a simple one line call for making a video call to a user, returns a response using callRequestResponse Listener
     *
     * @param recipientUsername recipientUsername
     * @param callRequestResponseListener callRequestResponseListener
     */
    public void makeVideoCall(String recipientUsername,
                              final CallRequestResponseListener callRequestResponseListener){

        if(mLocalVideoView == null || mRemoteVideoView == null){

            if(callRequestResponseListener != null){
                callRequestResponseListener.response(RESPONSE_ERROR_INVALID_KANDY_VIEWS,
                        "Please select valid KandyViews", null);
            }

            return;
        }

        //use the generic method for making call
        makeCall(recipientUsername, callRequestResponseListener, KandyOutgingVoipCallOptions.START_CALL_WITH_VIDEO);

    }

    /**
     * a simple one line call for making a voice call to a user, returns a response using callRequestResponse Listener

     * @param recipientUsername recipientUsername
     * @param callRequestResponseListener callRequestResponseListener
     */
    public void makeVoiceCall(String recipientUsername,
                              final CallRequestResponseListener callRequestResponseListener){

        if(mLocalVideoView == null || mRemoteVideoView == null){

            if(callRequestResponseListener != null){
                callRequestResponseListener.response(RESPONSE_ERROR_INVALID_KANDY_VIEWS,
                        "Please select valid KandyViews, its required even for non video calls", null);
            }

            return;
        }

        //use the generic method for making call
        makeCall(recipientUsername, callRequestResponseListener, KandyOutgingVoipCallOptions.START_CALL_WITHOUT_VIDEO);

    }


    /**
     * a simple one line call for making a pstn telephony call to a phone number view kandy sdk,
     * returns a response using callRequestResponse Listener
     *
     * @param phoneNumber telephone Number
     * @param callRequestResponseListener callRequestResponseListener
     */
    public void makePstnCall(String phoneNumber, final CallRequestResponseListener callRequestResponseListener){

//Here possible to define the user- caller whose number/name will see the callee side
//in Case and will be null the calle will see number/username with which caller registered
        KandyRecord caller = null;
        try {
            caller = new KandyRecord("custom_name@domain");
        } catch (KandyIllegalArgumentException e) {
            if(callRequestResponseListener != null){
                callRequestResponseListener.response(RESPONSE_ERROR_ILLEGAL_USERNAME,
                        "Illegal Username type, please make sure user exist", null);
            }
            return;
        }IKandyOutgoingCall currentCall = Kandy.getServices().getCallService().createPSTNCall(caller, phoneNumber, KandyOutgoingPSTNCallOptions.NONE);

        //YOU MUST TO PASS THE NON NULL VALUE OF LOCAL VIEW TO THE KandyOutgoingCall or/and KandyIncomingCall
        currentCall.setLocalVideoView(mLocalVideoView);

        //YOU MUST TO PASS THE NON NULL VALUE OF REMOTE VIEW TO THE KandyOutgoingCall or/and KandyIncomingCall
        currentCall.setRemoteVideoView(mRemoteVideoView);

        currentCall.establish(new KandyCallResponseListener() {

            @Override
            public void onRequestSucceeded(IKandyCall call) {
                if(callRequestResponseListener != null){
                    callRequestResponseListener.response(RESPONSE_REQUEST_SUCCESS, "Call Request Generated Successfully", call);
                }
            }

            @Override
            public void onRequestFailed(IKandyCall call, int code, String arg2) {
                if(callRequestResponseListener != null){
                    callRequestResponseListener.response(RESPONSE_REQUEST_FAILURE, arg2, call);
                }
            }
        });
    }


    /**
     * a generic method to make simpel VoIP call

     * @param options to select the type of call
     */
    private void makeCall(String recipientUsername, final CallRequestResponseListener callRequestResponseListener, KandyOutgingVoipCallOptions options){
        //null for current user
        KandyRecord caller = null;
        KandyRecord callee = null;
        try {
            callee = new KandyRecord(recipientUsername);
        } catch (KandyIllegalArgumentException e) {

            if(callRequestResponseListener != null){
                callRequestResponseListener.response(RESPONSE_ERROR_ILLEGAL_USERNAME,
                        "Illegal Username type, please make sure user exist", null);
            }
            return;
        }

        IKandyOutgoingCall currentCall = Kandy.getServices().getCallService().createVoipCall(caller,
                callee, options);

//YOU MUST TO PASS THE NON NULL VALUE OF LOCAL VIEW TO THE KandyOutgoingCall or/and KandyIncomingCall
        currentCall.setLocalVideoView(mLocalVideoView);
//YOU MUST TO PASS THE NON NULL VALUE OF REMOTE VIEW TO THE KandyOutgoingCall or/and KandyIncomingCall
        currentCall.setRemoteVideoView(mRemoteVideoView);
        currentCall.establish(new KandyCallResponseListener() {



            @Override
            public void onRequestSucceeded(IKandyCall call) {

                if(callRequestResponseListener != null){
                    callRequestResponseListener.response(RESPONSE_REQUEST_SUCCESS, "Call Request Generated Successfully", call);
                }
            }

            @Override
            public void onRequestFailed(IKandyCall call, int arg1, String arg2) {
                if(callRequestResponseListener != null){
                    callRequestResponseListener.response(RESPONSE_REQUEST_FAILURE, arg2, call);
                }
            }
        });

    }


    /**
     *
     * unregister KandyCallListener to avoid leaks and crashes
     */
    public void onStop(){
        Kandy.getServices().getCallService().unregisterNotificationListener(this);
    }


    /**
     *
     * @return instance of iKandyCall
     */
    public IKandyCall getiKandyCallInstance() {
        return iKandyCall;
    }


    /**
     * @return instance of iKandyIncomingCall
     */
    public IKandyIncomingCall getiKandyIncomingCall() {
        return iKandyIncomingCall;
    }


    //this portion of code implement call notification listeners and use a custom listener available to
    //with limited but useful methods
    @Override
    public void onIncomingCall(IKandyIncomingCall iKandyIncomingCall) {
        this.iKandyIncomingCall = iKandyIncomingCall;

        if(iKandyIncomingCall.isVideoCall())
        {
            callNotificaitonListener.onIncomingVideoCall();
        }else{
            callNotificaitonListener.onIncomingVoiceCall();
        }

    }

    @Override
    public void onMissedCall(KandyMissedCallMessage kandyMissedCallMessage) {
        callNotificaitonListener.onMissedCall();
    }

    @Override
    public void onWaitingVoiceMailCall(KandyWaitingVoiceMailMessage kandyWaitingVoiceMailMessage) {

    }

    @Override
    public void onCallStateChanged(KandyCallState kandyCallState, IKandyCall iKandyCall) {
            this.iKandyCall = iKandyCall;
            callNotificaitonListener.onCallStateChanged(kandyCallState);
    }

    @Override
    public void onVideoStateChanged(IKandyCall iKandyCall, boolean b, boolean b1) {

    }

    @Override
    public void onGSMCallIncoming(IKandyCall iKandyCall, String s) {

    }

    @Override
    public void onGSMCallConnected(IKandyCall iKandyCall, String s) {

    }

    @Override
    public void onGSMCallDisconnected(IKandyCall iKandyCall, String s) {

    }


}
