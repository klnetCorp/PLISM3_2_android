package com.klnet.plism3.plsim3;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";


    /* 토큰이 새로 만들어질때나 refresh 될때  */
    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        Log.e(TAG, refreshedToken);

        /* DB서버로 새토큰을 업데이트시킬수 있는 부분 */
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        //앱 실행 아이콘 개수 조절
        setBadge(1);
        //추가한것
        Log.i("CHECK", "onMessageReceived"+remoteMessage.getData().toString());
        String title = "";
        String message = "";
        if( remoteMessage.getNotification() == null   ){
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("body");
        }
        else{
            title = remoteMessage.getNotification().getTitle();
            message = remoteMessage.getNotification().getBody();
        }

        String msg = remoteMessage.getData().get("msg");
        JSONObject data = null;
        try {
            data = new JSONObject(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String add = remoteMessage.getData().get("add");
        String alert = remoteMessage.getData().get("alert");
        sendNotification(title, message, data, add, alert);
    }
    // [END receive_message]

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    public void handleIntent(Intent intent) {
        Log.i( "CHECK", "handleIntent~~~~ ");
    }


    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * messageBody FCM message body received.
     */
    private void sendNotification(String title, String body, JSONObject data, String add, String alert) {
        String push_id = "";
        String obj_id = "";
        String recv_id = "";
        String type = "";
        String badge_num = "";
        String addon = "";

        try {
            push_id = data.getString("id");       // 푸시ID
            obj_id = data.getString("obj_id");    // 푸시 연관 계시물 ID
            recv_id = data.getString("recv_id");  // 수신자 ID
            type = data.getString("type");        // 메세지 종류
            badge_num = data.getString("badge");	// 배지로 표시할 푸시 개수
            addon = data.getString("addon");	    // 추가 메세지 정보
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("push_id", push_id);
        intent.putExtra("obj_id", obj_id);
        intent.putExtra("recv_id", recv_id);
        intent.putExtra("type", type);
        intent.putExtra("badge_num", badge_num);
        intent.putExtra("msg", alert);
        intent.putExtra("addon", addon);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = null;


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.

            String channelName = getString(R.string.default_notification_channel_name);
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder = new NotificationCompat.Builder(this, channel.getId());

        } else {
            notificationBuilder = new NotificationCompat.Builder(this);
        }

        notificationBuilder.setSmallIcon(R.drawable.small_icon)
                .setContentTitle(alert)
                .setContentText("상세내용을 보려면 누르세요.")
                .setAutoCancel(true)
                .setNumber(1)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);


       // notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        Intent intent3 = new Intent(this, MainActivity.class);
        intent3.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent3.putExtra("push_id", push_id);
        intent3.putExtra("obj_id", obj_id);
        intent3.putExtra("recv_id", recv_id);
        intent3.putExtra("type", type);
        intent3.putExtra("badge_num", badge_num);
        intent3.putExtra("msg", alert);
        intent3.putExtra("addon", addon);
        //intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent3);
    }

    private String getLauncherClassName() {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        final PackageManager pm = getApplicationContext().getPackageManager();
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resolveInfos) {
            final String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }


    public void setBadge(int count) {
        String launcherClassName = getLauncherClassName();
        if (launcherClassName == null) {
            return;
        }

        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count > 0? count : null);
        intent.putExtra("badge_count_package_name", getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);

        sendBroadcast(intent);
    }

}
