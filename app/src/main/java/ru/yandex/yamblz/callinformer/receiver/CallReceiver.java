package ru.yandex.yamblz.callinformer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.Locale;

import ru.yandex.yamblz.callinformer.R;
import ru.yandex.yamblz.callinformer.ui.widget.CallingWidget;

public class CallReceiver extends BroadcastReceiver {

    private File infoFile;

    private static final String LOG_TAG = "callingtest";
    private static boolean incomingCall = false;

    private static CallingWidget widget;

    public CallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        infoFile = new File(context.getFilesDir() + "/disabled");

        if(infoFile.exists()) {
            return;
        }

        if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                String phoneNumber = PhoneNumberUtils.formatNumber(intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER), Locale.getDefault().getCountry());

                if(!incomingCall) {
                    incomingCall = true;
                    widget = new CallingWidget(context);
                    widget.show(phoneNumber);
                    Log.d(LOG_TAG, "Incoming calling from " + phoneNumber);
                }

            } else {


                if(incomingCall) {
                    widget.close();
                    Log.d(LOG_TAG, "Calling is interrupted");
                    incomingCall = false;
                }

            }
        }
    }
}
