package com.ssm.pnas.userSetting;

/**
 * Created by glory on 2015-08-24.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.ssm.pnas.C;
import com.ssm.pnas.R;

public class OnGoingNotification{

    private volatile static OnGoingNotification instance;
    private static final String onGoingNoti_close_btn = "btn_close";
    private int onGoingNotiId = 01;
    private NotificationManager notificationmanager;

    private Context mContext;

    public static OnGoingNotification getInstance(Context mContext) {
        if (instance == null) {
            synchronized (OnGoingNotification.class) {
                if (instance == null) {
                    instance = new OnGoingNotification(mContext);
                }
            }
        }
        instance.mContext = mContext;
        return instance;
    }

    private OnGoingNotification(Context mContext){
        this.mContext = mContext;
        notificationmanager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, PnasReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void openNotification() {

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.ongoingnoti);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_action_about)
                .setTicker("공유 시작")
                .setAutoCancel(true)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setContent(remoteViews);

        remoteViews.setOnClickPendingIntent(R.id.btn_close, getPendingSelfIntent(mContext, onGoingNoti_close_btn));
        notificationmanager.notify(onGoingNotiId, builder.build());
    }

    public void closeNotification(){
        notificationmanager.cancel(onGoingNotiId);
    }
}