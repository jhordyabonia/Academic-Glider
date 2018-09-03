package chat;

import java.util.HashMap;

import static models.DB.LOGGED;
import static models.DB.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import webservice.Asynchtask;

import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Vibrator;
import android.widget.Toast;

public class ChatService extends Service implements Asynchtask 
{	
	public  static final String MESSENGER = "messenger";
	private  static final String MENSAJE_NUEVO = "mensaje_nuevo";

	public  static void updater(Activity launch, int CHAT)
    {

		if(!LOGGED) return;

        launch.stopService(new Intent(launch,ChatService.class));
    	if(!(launch instanceof Inbox))
    		return;

    	Messenger messenger= new Messenger(new MHandler((Inbox)launch));
    	Intent intent = new Intent(launch,ChatService.class);
    	intent.putExtra(MESSENGER, messenger);
    	intent.putExtra("CHAT", CHAT);
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
            Toast.makeText(this,"intent = null",Toast.LENGTH_LONG).show();
            return  super.onStartCommand(intent, flags, startId);
        }
		Bundle extras = intent.getExtras();
		
		if(extras !=null) 
			messenger = (Messenger) extras.get(MESSENGER);
		CHAT = intent.getIntExtra("CHAT", 0);	
		STOP = false;		
		get();
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
	}
	private void get()
	{
		if(!ACTIVE)return;
		datos.put("usuario", ID);
		datos.put("last_msj", ""+DBChat.LAST_MSJ);
		Server.setDataToSend(datos);
		Server.send("chat/get", null, this);
	}
	private void nuevos(JSONArray db)
	{
		if(db.length()<=0)
			return;
		
		int count = 0,last_msj=0;
		JSONObject chat = db.optJSONObject(count);
		
		while (chat != null)
		{
			try 
			{				
				DBChat.insert(chat);
				String nombre=chat.getString("nombre");
				String id= chat.getString("nombre")
						.replace("_"+User.get("celular")+"_","")
						.replace("_","");
				nombre=DBChat.get_contact("nombre","cel_",id);
				if(nombre.isEmpty())
					nombre=id;
				int chat_id=chat.getInt("id");
				boolean buffered= CHAT==chat_id;
				
				JSONArray msjs=chat.getJSONArray("mensajes");
				JSONObject msj_t=new JSONObject();
				for(int i=0;i<msjs.length();i++)
				{
					msj_t=msjs.getJSONObject(i);
					last_msj=msj_t.getInt("id");
					if(DBChat.LAST_MSJ==last_msj)
						continue;
					if(CHAT==0)
						buffer(msj_t.toString());
					if(buffered)
						buffer(msj_t.toString());
					else if(msj_t.getString("tipo").equals("asignatura"))
						notificar(nombre,"Nueva asignatura",chat_id);
					else if(msj_t.getString("tipo").equals("inicio"))
						notificar(nombre,"Usuario agregado",chat_id);
					else
						notificar(nombre,msj_t.getString("dato"),chat_id);					
				}
				DBChat.LAST_MSJ=DBChat.LAST_MSJ<last_msj?last_msj:DBChat.LAST_MSJ;	
			} catch (JSONException e){}
			chat = db.optJSONObject(++count);
		}
	}
	@SuppressLint("NewApi") 
	private void notificar(String usuario,String dato, int id) throws JSONException 
	{		
		Vibrator v= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(250);
		Notification.Builder mBuilder =
		        new Notification.Builder(this)
	     .setSmallIcon(R.drawable.ic_chat)
	     .setContentTitle(usuario)
	     .setContentText(dato);
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
	private int CHAT=-1;
	private boolean STOP;
	public static boolean ACTIVE=true;
	private Messenger messenger; 
	private final String ID=User.get("id");
	private final HashMap<String, String> datos=new HashMap();
}