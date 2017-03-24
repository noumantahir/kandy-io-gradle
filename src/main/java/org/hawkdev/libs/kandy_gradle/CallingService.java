package org.hawkdev.libs.kandy_gradle;

import com.genband.kandy.api.Kandy;
import com.genband.kandy.api.services.calls.IKandyCall;
import com.genband.kandy.api.services.calls.IKandyOutgoingCall;
import com.genband.kandy.api.services.calls.KandyCallResponseListener;
import com.genband.kandy.api.services.calls.KandyCallServiceNotificationListener;
import com.genband.kandy.api.services.calls.KandyOutgingVoipCallOptions;
import com.genband.kandy.api.services.calls.KandyOutgoingPSTNCallOptions;
import com.genband.kandy.api.services.calls.KandyRecord;
import com.genband.kandy.api.services.calls.KandyView;
import com.genband.kandy.api.utils.KandyIllegalArgumentException;

/**
 * Created by nomo on 24/03/2017.
 */

public class CallingService {

    private static final int RESPONSE_ERROR_ILLEGAL_USERNAME = 0;
    private static final int RESPONSE_ERROR_INVALID_KANDY_VIEWS = 1;
    public static final int RESPONSE_REQUEST_SUCCESS = 2;
    private static final int RESPONSE_REQUEST_FAILURE = 3;



    private KandyView mLocalVideoView;
    private KandyView mRemoteVideoView;

    interface CallRequestResponseListener {
        void response(int responseCode, String responseMsg, IKandyCall call);
    }


    public CallingService(KandyView mLocalVideoView, KandyView mRemoteVideoView) {
        this.mLocalVideoView = mLocalVideoView;
        this.mRemoteVideoView = mRemoteVideoView;

    }

    public void makeVideoCall(String recipientUsername,
                              final CallRequestResponseListener callRequestResponseListener){

        if(mLocalVideoView == null || mRemoteVideoView == null){

            if(callRequestResponseListener != null){
                callRequestResponseListener.response(RESPONSE_ERROR_INVALID_KANDY_VIEWS,
                        "Please select valid KandyViews", null);
            }

            return;
        }

        makeCall(recipientUsername, callRequestResponseListener, KandyOutgingVoipCallOptions.START_CALL_WITH_VIDEO);

    }


    public void makeVoiceCall(String recipientUsername,
                              final CallRequestResponseListener callRequestResponseListener){

        if(mLocalVideoView == null || mRemoteVideoView == null){

            if(callRequestResponseListener != null){
                callRequestResponseListener.response(RESPONSE_ERROR_INVALID_KANDY_VIEWS,
                        "Please select valid KandyViews, its required even for non video calls", null);
            }

            return;
        }

        makeCall(recipientUsername, callRequestResponseListener, KandyOutgingVoipCallOptions.START_CALL_WITHOUT_VIDEO);

    }



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


}
