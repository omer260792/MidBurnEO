package com.example.omer.midburneo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.omer.midburneo.Class.LocalData;
import com.example.omer.midburneo.Tabs.MainPageAc;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.entity.StringEntity;

import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;


public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";
    public JSONObject explrObject;
    private LocalData localData;
    private Context context;
    private JSONArray registration_ids = new JSONArray();


    @Override
    public void onReceive(Context context, Intent intent) {
        localData = new LocalData(context);

        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Set the alarm here.
                NotificationScheduler.setReminder(context, AlarmReceiver.class, localData.get_hour(), localData.get_min());



                return;
            }
        }

        Log.d(TAG, "onReceive: ");

       // Trigger the notification

        pushNotification();

        NotificationScheduler.showNotification(context, MainPageAc.class,
                localData.getTitlePush(), localData.getTitlePush());

    }

    public void pushNotification(){

//
//        JSONArray jsnobject = null;
//        String stringJson = null;
//        try {
//            stringJson = jsnobject.getJSONArray(0).toString();
//            Log.e("1",stringJson);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        try {
//            jsnobject = new JSONObject(stringJson);
//            Log.e("2",stringJson);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JSONArray jsonArray = null;
//        try {
//            jsonArray = jsnobject.getJSONArray("jsonArray");
//
//            for (int i = 0; i < jsonArray.length(); i++) {
//                explrObject = jsonArray.getJSONObject(i);
//                Log.e(TAG, String.valueOf(explrObject));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        registration_ids.put("dLszUNN2BC4:APA91bEjd9aJK2J8UYXIljSYkWivtl5lJoxSr-pMvf3pYu992CcpAo6TmceqnidqkAuBsAXoHpBQWumU0OF2PSl14jn8XdSyxacbmZad8WaS2v38Jce5ZWL7LNS-B0UreKToKHx8aM0C");

        if (explrObject.length() > 0) {

            Log.e(TAG, String.valueOf(explrObject));


            String url = "https://fcm.googleapis.com/fcm/send";
            AsyncHttpClient client = new AsyncHttpClient();

            client.addHeader(HttpHeaders.AUTHORIZATION, "key=AIzaSyDV08bsa0Cdtnzt4EJkm29qsvs-3giVFbc");
            client.addHeader(HttpHeaders.CONTENT_TYPE, RequestParams.APPLICATION_JSON);

            try {


                JSONObject params = new JSONObject();

                params.put("registration_ids", explrObject);

                JSONObject notificationObject = new JSONObject();
                notificationObject.put("body",localData.getBodyPush() );
                notificationObject.put("title",localData.getTitlePush());

                params.put("notification", notificationObject);

                StringEntity entity = new StringEntity(params.toString());

                client.post(context, url, entity, RequestParams.APPLICATION_JSON, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {

                    }
                });

            } catch (Exception e) {

            }
        }
    }
}



