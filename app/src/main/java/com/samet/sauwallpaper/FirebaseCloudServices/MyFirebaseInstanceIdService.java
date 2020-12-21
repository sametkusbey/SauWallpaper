package com.samet.sauwallpaper.FirebaseCloudServices;

import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.samet.sauwallpaper.util.FIREBASECONSTANTS;


public class MyFirebaseInstanceIdService extends FirebaseMessagingService {

    String url = FIREBASECONSTANTS.firebase_db_url;

    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance(FirebaseApp.getInstance(url)).getToken();
        Log.d("MyFirebaseToken", "Refreshed token: " + refreshedToken);


    }

}