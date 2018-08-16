package util;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

public class Alarma extends android.content.BroadcastReceiver {  
	  
	@Override  
    public void onReceive(android.content.Context context, android.content.Intent intent) 
    {  
         String titulo=context.getString(R.string.alertas)+" Glider",msj="";
         int mId=1865365764;
         Bundle args = intent.getExtras();
         if(args!=null)
         {
        	 titulo=args.getString("titulo");
        	 msj=args.getString("msj");
        	 mId=args.getInt("id");
         }
         Vibrator v= (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
         v.vibrate(250);
         Toast.makeText(context, titulo+"\n"+msj, Toast.LENGTH_LONG).show();  
         Notification.Builder mBuilder =
 		        new Notification.Builder(context)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(titulo)
        .setContentText(msj);
 		// Creates an explicit intent for an Activity in your app
 		Intent resultIntent = new Intent(context, HomeActivity.class);
 		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
 		stackBuilder.addParentStack(HomeActivity.class);
 		stackBuilder.addNextIntent(resultIntent);
 		PendingIntent resultPendingIntent =
 		     stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
 		mBuilder.setContentIntent(resultPendingIntent);
 		NotificationManager mNotificationManager =
 		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
 		
 		// mId allows you to update the notification later on.
 		mNotificationManager.notify(mId, mBuilder.build());	
    }  
}  