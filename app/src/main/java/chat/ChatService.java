package chat;

import java.util.HashMap;
import java.util.Random;

import static com.jhordyabonia.ag.HomeActivity.ASIGNATURAS;
import static models.DB.LOGGED;
import static models.DB.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import crud.AlertaActivity;
import crud.ApunteActivity;
import crud.AsignaturaActivity;
import crud.CalificableActivity;
import crud.HorarioActivity;
import crud.LecturaActivity;
import crud.Main;
import models.DB;
import util.Settings;
import webservice.Asynchtask;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.Notificaciones;
import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;
import com.jhordyabonia.ag.SettingsActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Vibrator;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

public class ChatService extends Service implements Asynchtask ,Notificaciones.Notifications
{
	public  static  boolean THERE_ARE_MESSAGE = true;
	public  static final String MESSENGER = "messenger";
	private  static final String MENSAJE_NUEVO = "mensaje_nuevo";

	public  static void updater(Activity launch, int chat)
    {
        launch.stopService(new Intent(launch,ChatService.class));
    	if(!(launch instanceof Inbox))
    		return;

		if(!LOGGED) return;

    	Messenger messenger= new Messenger(new MHandler((Inbox)launch));
    	Intent intent = new Intent(launch,ChatService.class);
    	intent.putExtra(MESSENGER, messenger);
    	intent.putExtra("CHAT", chat);
        launch.startService(intent);
    }

	public interface Inbox
	{
		void add_msj(JSONObject msj,boolean move) throws JSONException;
	} 
	public static class MHandler extends Handler 
	{
		private Inbox mInbox;
		public MHandler(Inbox inbox)
		{mInbox=inbox;}
	    public void handleMessage(Message message) 
	    {
	         Bundle data = message.getData();             
	         if(data != null) 
	         {
	        	String nuevo = data.getString(MENSAJE_NUEVO);
	     		try 
	     		{	
	     			mInbox.add_msj(new JSONObject(nuevo),true);
	     		} catch (JSONException e) {}
	         }
	    }
	}
	@Override    
	public void onCreate() {super.onCreate();}    
	@Override    
	public void onDestroy()
	{
		STOP=true;	
		DBChat.save();		
		super.onDestroy();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
	    if(intent==null)
        {
            CHAT=0;
            return  super.onStartCommand(intent, flags, startId);
        }else
			CHAT = intent.getIntExtra("CHAT", 0);
		Bundle extras = intent.getExtras();
		
		if(extras !=null) 
			messenger = (Messenger) extras.get(MESSENGER);
		STOP = false;		
		get();
		Log.d("START",""+CHAT);
		return super.onStartCommand(intent, flags, startId);	
	}	
	@Override
	public IBinder onBind(Intent intent) 
	{	return null; }
	@Override
	public void processFinish(String result) 
	{
		boolean err=!result.startsWith("[")
				||!result.endsWith("]");
		
		if(!err)
		{
			try 
			{
				nuevos(new JSONArray (result));
			} catch (JSONException e) {}				
		}
		
		if(!STOP)
			get();
		Log.e("RESULT",result);
	}
	private void get()
	{
		if(!ACTIVE)return;
		if(THERE_ARE_MESSAGE) {
			THERE_ARE_MESSAGE = false;
			datos.put("usuario", ID);
			datos.put("last_msj", "" + DBChat.LAST_MSJ);
			Server.setDataToSend(datos);
			Server.send("chat/get", null, this);
		}else{
			ObjectAnimator in0 = ObjectAnimator
					.ofFloat(null,"scaleX",1,0);

			/*in0.setDuration(1000);
			in0.setInterpolator(new LinearInterpolator());
			in0.addListener( new AnimatorListenerAdapter()
								{
									@Override
									public void onAnimationEnd(Animator anim)
									{  get();}
								} );
			in0.start();*/

			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					get();
				}
			}, 1000);
		}
	}
	private void nuevos(JSONArray db)
	{
		if(db.length()<=0)
			return;
		int count = 0,last_msj=0;
		JSONObject chat = db.optJSONObject(count);
		
		while (chat != null)
		{
			try {

				if (chat.getInt("id") == -1) {
					DB.update(this, chat);
				} else {
					DBChat.insert(chat);
					Log.e("NUEVOS",chat.toString());
					String nombre;
					String id = chat.getString("nombre")
							.replace("_" + User.get("celular") + "_", "")
							.replace("_", "");
					nombre = DBChat.get_contact("nombre", "cel_", id);
					if (nombre.isEmpty())
						nombre = id;
					int chat_id = chat.getInt("id");
					boolean buffered = CHAT == chat_id;
					JSONArray msjs = chat.getJSONArray("mensajes");
					JSONObject msj_t;
					for (int i = 0; i < msjs.length(); i++) {
						msj_t = msjs.getJSONObject(i);
						last_msj = msj_t.getInt("id");
						if (DBChat.LAST_MSJ == last_msj)
							continue;
						if (CHAT == 0)
							buffer(msj_t.toString());
						if (buffered)
							buffer(msj_t.toString());
						else if (msj_t.getString("tipo").equals(DB.MODELS[ASIGNATURAS]))
							notificar(nombre, "Nueva asignatura", chat_id);
						else if (msj_t.getString("tipo").equals("inicio"))
							notificar(nombre, "Usuario agregado", chat_id);
						else
							notificar(nombre, msj_t.getString("dato"), chat_id);
					}
					DBChat.LAST_MSJ = DBChat.LAST_MSJ < last_msj ? last_msj : DBChat.LAST_MSJ;
				}
			}catch(JSONException e){}
			chat = db.optJSONObject(++count);
		}
	}
	@SuppressLint("NewApi") 
	public int notificar(String usuario,String dato, int id)
	{		
		Vibrator v= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		if(Settings.VIBRATE)
			v.vibrate(250);

		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Notification.Builder mBuilder =
		        new Notification.Builder(this)
	     .setSmallIcon(R.drawable.twotone_speaker_notes_white_48)
	     .setContentTitle(usuario)
	     .setContentText(dato);
		if(Settings.SOUND)
			mBuilder.setSound(defaultSoundUri);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, ChatActivity.class);
		resultIntent.putExtra("CHAT", ""+id);
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(ListChatActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		     stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		// mId allows you to update the notification later on.
		mNotificationManager.notify(id, mBuilder.build());
		return id;
	}
	@SuppressLint("NewApi")
	public int update(String title,String dato, String tipo)
	{
		Vibrator v= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		if(Settings.VIBRATE)
			v.vibrate(250);
		Notification.Builder mBuilder =
				new Notification.Builder(this)
						.setSmallIcon(R.drawable.twotone_home_white_48)
						.setContentTitle(title)
						.setContentText(dato);

		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		if(Settings.SOUND)
			mBuilder.setSound(defaultSoundUri);
		// Creates an explicit intent for an Activity in your app

		Intent intent=null;
		switch (tipo){
			case "alertas":
				intent= (new Intent(this, AlertaActivity.class));
				break;
			case "apuntes":
				if(util.Settings.DROP_MODE)
					intent= (new Intent(this, Main.class));
				else intent= (new Intent(this, ApunteActivity.class));
				break;
			case "lecturas":
				intent= (new Intent(this, LecturaActivity.class));
				break;
			case "calificables":
				intent= (new Intent(this, CalificableActivity.class));
				break;
			case "horarios":
				if(!DB.COMUNIDAD)
					if(DB.Asignaturas.LIST_ASIGNATURAS.length>1)
						intent= (new Intent(this, HorarioActivity.class));
				break;
			case "asignaturas":
				intent= (new Intent(this, AsignaturaActivity.class));
				break;
				default:
					intent= (new Intent(this, Notificaciones.class));
		}

		Random r=new Random();
		int id=r.nextInt();
		intent.putExtra(Notificaciones.FILE,id);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(HomeActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(intent);
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// mId allows you to update the notification later on.
		mNotificationManager.notify(id, mBuilder.build());
		return id;
	}
	private void buffer(String nuevo)
	{
		if(messenger==null)
			return;
		
		Message msg = Message.obtain();   
    	Bundle bundle =new Bundle();
    	bundle.putString(ChatService.MENSAJE_NUEVO, nuevo);
    	msg.setData(bundle);
    	try{messenger.send(msg);}
    	catch(android.os.RemoteException e1) {}	
	}
	public static void setChat(int c){
		CHAT=c;
	}
	private static int CHAT=-1;
	private boolean STOP;
	public static boolean ACTIVE=true;
	private Messenger messenger; 
	private final String ID=User.get("id");
	private final HashMap<String, String> datos=new HashMap();
}