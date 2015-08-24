/**
 * Created by glory on 2015-08-24.
 */

package com.ssm.pnas.userSetting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.ssm.pnas.R;
import com.ssm.pnas.nanohttpd.Httpd;

public class PnasReceiver extends BroadcastReceiver {

    public String TAG = getClass().getSimpleName();
    private SharedPreferences pref;
    private static final String onGoingNoti_close_btn = "btn_close";
    private Vibrator vibe;
    private static final int vibeTime = 50;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context.getApplicationContext();
        //prevent App killed because of null pointer exception
        if(mContext == null || intent == null) return;

        pref = mContext.getSharedPreferences("Pnas", Context.MODE_PRIVATE);
        vibe = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        String action = intent.getAction();
        //prevent App killed because of null pointer exception
        if(action == null) action="";

        if (action.equals(onGoingNoti_close_btn) && Httpd.getInstance(mContext).isAlive()) {
            Log.d(TAG, "**Stop Pnas Server**");
            if(MainActivity.sContext != null ){
                Httpd.getInstance(mContext).stop();
                vibe.vibrate(vibeTime);
                OnGoingNotification.getInstance(mContext).closeNotification();
                ((MainActivity)MainActivity.sContext).checkOnOff();
            }
            else{
                Httpd.getInstance(mContext).stop();
                vibe.vibrate(vibeTime);
                OnGoingNotification.getInstance(mContext).closeNotification();
                Toast.makeText(mContext, mContext.getString(R.string.stopserver), Toast.LENGTH_SHORT).show();
            }
        }
    }
}