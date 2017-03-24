package org.hawkdev.libs.kandy_gradle;

import com.genband.kandy.api.Kandy;
import com.genband.kandy.api.services.chats.KandySMSMessage;
import com.genband.kandy.api.services.common.KandyResponseListener;
import com.genband.kandy.api.utils.KandyIllegalArgumentException;

/**
 * Created by nomo on 24/03/2017.
 */

public class SmsService {

    private static final int RESPONSE_ILLEGAL_ARGUMENT = 0;
    private static final int RESPONSE_SMS_FAILURE = 1;
    private static final int RESPONSE_SMS_SENT = 2;

    interface SmsResponseListener{
        void response(int responseCode, String responseMsg);
    }

    private void sendSMS(String phoneNumber, String messageText,final SmsResponseListener smsResponseListener) {

        KandySMSMessage message = null;
        try
        {
            message = new KandySMSMessage(phoneNumber, "Kandy SMS", messageText);
        }
        catch (KandyIllegalArgumentException e)
        {
            if(smsResponseListener != null){
                smsResponseListener.response(RESPONSE_ILLEGAL_ARGUMENT, "Please check your phone number");
            }
        }

        // Sending message
        Kandy.getServices().getChatService().sendSMS(message, new KandyResponseListener()
        {
            @Override
            public void onRequestFailed(int responseCode, String err)
            {
                if(smsResponseListener != null){
                    smsResponseListener.response(RESPONSE_SMS_FAILURE, err);
                }
            }

            @Override
            public void onRequestSucceded()
            {
                if(smsResponseListener != null){
                    smsResponseListener.response(RESPONSE_SMS_SENT, "Sms successfully sent");
                }
            }
        });
    }
}
