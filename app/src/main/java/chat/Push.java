package chat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import models.DB;
import webservice.Asynchtask;

public class Push extends FirebaseMessagingService {

    private static final String TAG = "PUSH";
    public static void subscribe(final Context context){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e("log", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        sendRegistrationToServer(context,token);
                    }
                });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            ChatService.THERE_ARE_MESSAGE = true;
            if(!DB.LOGGED){

                if(remoteMessage.getData().containsKey("chat")) {
                    int chat=0;
                    Integer.parseInt(remoteMessage.getData().get("chat"));
                    ChatService.setChat(chat);
                }
                if(HomeActivity.HOME==null) {
                    Intent mIntent = new Intent(this, HomeActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mIntent);
                }
            }
        }
    }
    @Override
    public void onNewToken(String token) {
        Log.e(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(Push.this,token);
    }

    public static void sendRegistrationToServer(final Context context, String token) {

        HashMap<String, String> datos=new HashMap<>();
        datos.put("token",token);
        datos.put("id", DB.User.get("id"));
        Server.setDataToSend(datos);
        Asynchtask r=new Asynchtask() {
            @Override
            public void processFinish(String result) {
                try {
                    JSONObject tmp = new JSONObject(result);

                    Toast.makeText(context, tmp.getString("menssage"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                }
            }
        };

        if(DB.LOGGED)
            Server.send("setToken/",null,r);
    }
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, Push.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "fcm_default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.twotone_home_white_48)
                        .setContentTitle("ag_service_push")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 , notificationBuilder.build());
    }
}
