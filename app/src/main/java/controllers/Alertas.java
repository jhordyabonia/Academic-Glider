package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import models.DB;

import org.json.JSONException;
import org.json.JSONObject;

import util.Alarma;
import webservice.Asynchtask;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.Notificaciones;
import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import controllers.Adapter.ITEM_TYPE;
import controllers.Adapter.Item;

import crud.Base;

import static com.jhordyabonia.ag.HomeActivity.ALERTAS;
import static com.jhordyabonia.ag.HomeActivity.ON_DISPLAY;

public class Alertas extends Controller
{
  public static void eliminar_alama(Context t,int id)
  {
	  int ALARM_REQUEST_CODE = id;
	  
	  AlarmManager manager = 
				(AlarmManager) t.getSystemService(Context.ALARM_SERVICE);  
	  
	  Intent intent = new Intent(t, Alarma.class);  
	  PendingIntent pIntent = 
	       	PendingIntent.getBroadcast(t, ALARM_REQUEST_CODE,
	    			   intent,  PendingIntent.FLAG_CANCEL_CURRENT);  
	  manager.cancel(pIntent);
  }

  public static void fijar_alarmas(Context t)
  { fijar_alarmas(t, false);}
  
  public static void fijar_alarmas(Context t,boolean cancel)
  {
	  DB.model(DB.MODELS[ALERTAS]);
	  ArrayList<JSONObject> alarmas=DB.find("alerta", "1");

	  try
	  {
		  for(JSONObject a:alarmas)
		  {
			  String result=fijar_alarma(a,t,cancel);
			  //if(result.contains("Vencida"))
			//	  Toast.makeText(t,result, Toast.LENGTH_SHORT).show();

			  Notificaciones.add("",DB.MODELS[ALERTAS],"",result);
		  }
	  }
	  catch (NumberFormatException e) {} 
	  catch (JSONException e) {}
  }

  public static String fijar_alarma(JSONObject a,Context t) throws NumberFormatException, JSONException
  {return fijar_alarma(a,t,false);}
  public static String fijar_alarma(JSONObject a,Context t,boolean cancel) throws NumberFormatException, JSONException
  {
	int ALARM_REQUEST_CODE = Integer.valueOf(DB.User.get("id")+
			 a.get("asignatura")+a.get("id"));
	  
	AlarmManager manager = 
			(AlarmManager) t.getSystemService(Context.ALARM_SERVICE);  
	  
	Intent intent = new Intent(t, Alarma.class);  
	String titulo = DB.Asignaturas.getName(a.getString("asignatura"));

	intent.putExtra("id", ALARM_REQUEST_CODE);
	intent.putExtra("titulo", titulo);  
	intent.putExtra("msj", a.getString("nombre"));
	PendingIntent pIntent = 
	    	PendingIntent.getBroadcast(t, ALARM_REQUEST_CODE,
	    			   intent,  PendingIntent.FLAG_CANCEL_CURRENT);  
	manager.cancel(pIntent);
	
	if(cancel) 
		return "Alerta "+a.getString("nombre")+" de "+titulo+" Cancelada";
	String fecha_[]=a.getString("fecha").split("-");
	String hora_[]=a.getString("hora").split(":");
	int dayOfMonth=Integer.valueOf(fecha_[0]);
	int monthOfYear=Integer.valueOf(fecha_[1])-1;
	int year=Integer.valueOf(fecha_[2]);
	int hour=Integer.valueOf(hora_[0]);
	int minutes=Integer.valueOf(hora_[1]);
	Calendar c=Calendar.getInstance();
	c.set(year ,monthOfYear,dayOfMonth,hour,minutes);
	Date d=c.getTime();
	
	if(d.getTime()<System.currentTimeMillis())
		return "Alerta "+a.getString("nombre")+" de "+titulo+" Vencida";
	
	manager.set(AlarmManager.RTC_WAKEUP,d.getTime(), pIntent); 
	return  "Establecida para:\n"+d.toString();	
  }
  private void establecerAlarma(JSONObject a)
  {  
	try 
	{
	  int ALARM_REQUEST_CODE = Integer.valueOf(
			  DB.User.get("id")+HomeActivity.idAsignaturaActual()+a.get("id"));
	  	    
	  if(a.get("alerta").equals("1"))
	  {
		  eliminar_alama(getActivity(),ALARM_REQUEST_CODE);
		  return;
	  }
	  String dd = fijar_alarma(a,getActivity());
	  //Toast.makeText(t,dd, Toast.LENGTH_SHORT).show();
		Notificaciones.add("","Alerta",a.getString("id"),dd);
	} 
	catch (NumberFormatException e) {}
	catch (JSONException e) {}	  
  } 
	private void alarma(final boolean active) throws JSONException
	{
		final JSONObject item = LOCAL_DB.get(Base.itemSeleted);
		final String id = item.getString("id");
		HashMap<String, String> data_tmp = new HashMap();
		data_tmp.put("id", id);
		if(active)
			data_tmp.put("alerta", "1");
		else data_tmp.put("alerta", "0");
		Server.setDataToSend(data_tmp);
		Asynchtask recep = new Asynchtask() 
		{
			@Override
			public void processFinish(String result) 
			{
				try {
					JSONObject mData = new JSONObject(result);
					String msj = mData.getString("menssage");
					JSONObject tmp = mData.getJSONObject("data");
					item.put("alerta",tmp.get("alerta"));
					DB.insert(ON_DISPLAY,item);
					if(msj.contains("Error"))
					{
						Toast.makeText(getActivity(), msj, Toast.LENGTH_SHORT).show();
						Notificaciones.add("",ALERTAS,id,result);
					}else if(msj.contains("activada"))
					{
						Toast.makeText(getActivity(), msj, Toast.LENGTH_SHORT).show();
						establecerAlarma(item);
						Notificaciones.add("",ALERTAS,id,result);

						HomeActivity home = (HomeActivity)getActivity();
						if(home!=null)
							DB.update(home);
						else DB.update();
					}else
						Toast.makeText(getActivity(), R.string.network_err, Toast.LENGTH_SHORT).show();

				}catch (JSONException e){}
			}
		};
		Server.send("alertas/alerta", getActivity(), recep);
	}
	@Override
	public void showPopup(View v) 
	{
		PopupMenu popup = new PopupMenu(getActivity(), v);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.actions_alerta, popup.getMenu());
		boolean alerta = false;
		try 
		{alerta = LOCAL_DB.get(Base.itemSeleted).getString("alerta").equals("1");}
		catch (JSONException e) {}
		popup.getMenu().findItem(R.id.alarma).setChecked(alerta);
		popup.show();
		popup.setOnMenuItemClickListener
		(new OnMenuItemClickListener() 
			{
				@Override
				public boolean onMenuItemClick(MenuItem arg0) 
				{
					if(arg0.getItemId()==R.id.alarma)
					{
						try 
						{alarma(!arg0.isChecked());} 
						catch (JSONException e) {}
					}else delete();
					return false;
				}
			}
		);
	}
	@Override
	protected void delete()
	{
		try
		{
			JSONObject item = LOCAL_DB.get(Base.itemSeleted);
			item.put("alerta","1");
			establecerAlarma(item);
		}catch (JSONException e) {}
		super.delete();
	}
	@Override
	public void show() 
	{
		if(base_data!=null)
			Toast.makeText(this.getContext(),"Updating...",Toast.LENGTH_LONG).show();

		DB.model(DB.MODELS[ALERTAS]);
		LOCAL_DB = DB.find("asignatura", HomeActivity.idAsignaturaActual());

		base_data = new Adapter(rootView.getContext(),ITEM_TYPE.alerta, Adapter.alertas);
		base.setAdapter(base_data);
		if(!LOCAL_DB.isEmpty())
		try 
		{
			base_data.clear();
			for (JSONObject v : LOCAL_DB)
			{
				base_data.add(new Item(v.getString("nombre")
						 ,v.getString("fecha")+" "+ v.getString("hora")
						,v.getString("alerta").equals("1")));
			}
		} catch (JSONException e) {}
	}
}
