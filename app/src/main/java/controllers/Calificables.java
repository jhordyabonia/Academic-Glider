package controllers;

import java.util.HashMap;

import models.DB;

import org.json.JSONException;
import org.json.JSONObject;

import webservice.Asynchtask;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.PopupMenu.OnMenuItemClickListener;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import controllers.Adapter.ITEM_TYPE;
import controllers.Adapter.Item;

import crud.Base;

public class Calificables extends Controller {
	@Override
	public void show() 
	{
		DB.model(HomeActivity.onDisplay(HomeActivity.CALIFICABLES));
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
		DB.model("horarios");
		String hora=DB.getBy("asignatura", calificable.getString("asignatura")).getString("hora");
		datos.put("nombre", calificable.getString("nombre"));
		datos.put("fecha", fecha);
		datos.put("hora", hora);
		Server.setDataToSend(datos);
		final HomeActivity home = (HomeActivity) getActivity();
		Asynchtask reponse =new Asynchtask()
		{
			@Override
			public void processFinish(String result)
			{
				Toast.makeText(home, result, Toast.LENGTH_LONG).show();
				if (!result.contains("Error "))
					DB.update(home);
			}
		};
		Server.send("alertas", home, reponse);
	}
}