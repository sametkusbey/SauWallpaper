package com.samet.sauwallpaper.FirebaseCloudServices;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;

import com.samet.sauwallpaper.HomeActivity;
import com.samet.sauwallpaper.R;

public class MyNotificationManager {
    private Context mCtx;
    private static MyNotificationManager mInstance;


    private MyNotificationManager (Context context){
        mCtx =context;
    }

    public  static  synchronized MyNotificationManager getmInstance(Context context){
        if (mInstance == null){
            mInstance = new MyNotificationManager(context);
        }
        return  mInstance;
    }

    public  void displayNotification(String title,String body){
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx,Constants.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);


        Intent intent =new Intent(mCtx,HomeActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager !=null){
            mNotificationManager.notify(1,mBuilder.build());
        }


    }

}
