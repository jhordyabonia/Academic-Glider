package controllers;

import java.util.Calendar;
import java.util.HashMap;

import chat.DBChat;
import models.DB;

import org.json.JSONException;
import org.json.JSONObject;

import webservice.Asynchtask;

import android.app.Activity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.PopupMenu.OnMenuItemClickListener;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import controllers.Adapter.ITEM_TYPE;
import controllers.Adapter.Item;

import crud.Base;

import static com.jhordyabonia.ag.HomeActivity.ALERTAS;
import static com.jhordyabonia.ag.HomeActivity.CALIFICABLES;

public class Calificables extends Controller {
	@Override
	public void show() 
	{
		ImageView imageView =  rootView.findViewById(R.id.add);
		if(!addPermission(CALIFICABLES))
			imageView.setVisibility(View.GONE);

		DB.model(DB.MODELS[CALIFICABLES]);
		LOCAL_DB = DB.find("asignatura", HomeActivity.idAsignaturaActual());
		base_data = new Adapter(rootView.getContext(),ITEM_TYPE.calificable,Adapter.calificables);
		base.setAdapter(base_data);
		if(!LOCAL_DB.isEmpty())
		try {
			base_data.clear();
			for (JSONObject v : LOCAL_DB)
				base_data.add(new Item(v.getString("nombre"),
						"Nota "+DB.titulo(v.getString("nota")) 
						+ "\n"+ v.get("descripcion")));
		} catch (JSONException e) {
		}

	}
	@Override
	public void showPopup(View v) 
	{
		PopupMenu popup = new PopupMenu(getActivity(), v);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.actions_calificable, popup.getMenu());

		popup.getMenu().findItem(R.id.delete).setEnabled(delPermission(CALIFICABLES));
		popup.show();
		popup.setOnMenuItemClickListener
		(new OnMenuItemClickListener() 
			{
				@Override
				public boolean onMenuItemClick(MenuItem arg0) 
				{
					if(arg0.getItemId()==R.id.agregar_alerta)
					{
						try
						{crear_alerta(LOCAL_DB.get(Base.itemSeleted));} 
						catch (JSONException e) {}
					}else delete();
					return false;
				}
			}
		);
	}

	private void crear_alerta(JSONObject calificable) throws JSONException 
	{
		HashMap<String, String> datos = new HashMap<String, String>();
		datos.put("asignatura",  calificable.getString("asignatura"));
		String fecha_[]=calificable.getString("fecha").split("-");
		int dayOfMonth=Integer.valueOf(fecha_[0])-1;
		int monthOfYear=Integer.valueOf(fecha_[1])-(dayOfMonth==0?1:0);
		dayOfMonth=dayOfMonth==0?30:dayOfMonth;
		int year=Integer.valueOf(fecha_[2])-(monthOfYear<0?1:0);
		monthOfYear=monthOfYear<0?12:monthOfYear;
		String fecha=dayOfMonth+"-"+monthOfYear+"-"+year;

		Calendar c=Calendar.getInstance();
		String hora= c.get(Calendar.HOUR_OF_DAY)+":"+
				c.get(Calendar.MINUTE);

		DB.model("horarios");
		JSONObject horario=DB.getBy("asignatura", calificable.getString("asignatura"));
		if(horario!=null)
			 hora=horario.getString("hora");

		datos.put("nombre", calificable.getString("nombre"));
		datos.put("fecha", fecha);
		datos.put("hora", hora);
		Server.setDataToSend(datos);
		final Activity home =  getActivity();
		Asynchtask reponse =new Asynchtask()
		{
			@Override
			public void processFinish(String result)
			{
				try {
					JSONObject tmp = new JSONObject(result);
					Toast.makeText(home, tmp.getString("menssage"), Toast.LENGTH_LONG).show();
					DB.insert(DB.MODELS[HomeActivity.ALERTAS],tmp.getJSONObject("data"));
					Log.i("ALERT NEW", result);

					//throw new JSONException("delivery");
				}catch (JSONException e){}
			}
		};
		Server.send("alertas", home, reponse);
	}
}