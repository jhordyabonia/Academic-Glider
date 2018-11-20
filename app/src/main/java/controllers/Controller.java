package controllers;

import java.util.ArrayList;
import java.util.HashMap;

import models.DB;

import org.json.JSONException;
import org.json.JSONObject;

import util.Settings;
import util.Style;
import webservice.Asynchtask;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import crud.Base;

import static com.jhordyabonia.ag.HomeActivity.ALERTAS;
import static com.jhordyabonia.ag.HomeActivity.ON_DISPLAY;

public abstract class Controller extends Fragment implements OnItemClickListener
{
	public static boolean CLICK_BLOQUEADO=false;
	protected View rootView=null;
	protected ListView base=null;
	protected Adapter base_data=null;
	protected ArrayList<JSONObject> LOCAL_DB = new ArrayList<JSONObject>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.lienzo, container, false);

		ImageView imageView =  rootView.findViewById(R.id.add);
		if(DB.COMUNIDAD)
			imageView.setVisibility(View.GONE);

		imageView.setImageResource(R.drawable.ic_stat_name);
		imageView.setOnClickListener(new OnClickListener()
			{
				public void onClick(View arg0)
				{Base.crud(getActivity(), Base.Actions.Add);}
			}
		);
		
		base =  rootView.findViewById(R.id.list);
		base.setDividerHeight(0);
		base.setOnItemClickListener(this);
		base.setOnItemLongClickListener(new OnItemLongClickListener() 
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int index_item, long arg3)
			{
				Base.itemSeleted = index_item;
				CLICK_BLOQUEADO=true;
				if(!DB.COMUNIDAD)
					if(v.findViewById(R.id.empty)==null)
						showPopup(v);
				return false;
			}
		});
		Style.set(rootView);
		show();
		return rootView;
	}
	@Override
	final public void onResume(){
		super.onResume();
		show();
	}
	@Override
	final public void onItemClick(AdapterView<?> av, View v, int index_item, long arg3)
	{
		if(CLICK_BLOQUEADO)
		{
			CLICK_BLOQUEADO=false;
			return;
		}

		if(v.findViewById(R.id.empty)!=null)
			return;
		Base.itemSeleted = index_item;
		Base.crud(getActivity(), Base.Actions.Edit);
	}

	public void showPopup(View v)
	{
		PopupMenu popup = new PopupMenu(getActivity(), v);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.actions, popup.getMenu());
		popup.getMenu().findItem(R.id.delete).setEnabled(delPermission(ON_DISPLAY));
		popup.show();

		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() 
		{
			@Override
			public boolean onMenuItemClick(MenuItem arg0) 
			{
				delete();
				return false;
			}
		});
	}

	protected void delete()
	{
		if(!delPermission(ON_DISPLAY)){
			Toast.makeText(rootView.getContext(),R.string.noPermission,Toast.LENGTH_LONG).show();
			return;
		}

		String id = "";
		try 
		{
			id = LOCAL_DB.get(Base.itemSeleted).getString("id");
		} catch (JSONException e) {}
		HashMap<String, String> data_tmp = new HashMap<>();
		data_tmp.put("id", id);
		Server.setDataToSend(data_tmp);
		Asynchtask recep = new Asynchtask() 
		{
			@Override
			public void processFinish(String result) 
			{
				if(!result.isEmpty())
					if(!result.equals("Sin conexion a internet"))
					{
						JSONObject mData;
						String msj="";
						try{
							mData= new JSONObject(result);
							msj=mData.getString("menssage");
							JSONObject tmp=mData.getJSONObject("data");

							if(msj.contains("Eliminad")) {

                                DB.model(DB.MODELS[ON_DISPLAY]);
								if(DB.remove(tmp)) {
                                  	Toast.makeText(rootView.getContext(), msj, Toast.LENGTH_LONG).show();
                                    show();
                                }else Toast.makeText(rootView.getContext(),"No se removio",Toast.LENGTH_LONG).show();
								DB.update();
							}
						}catch (JSONException e){}

						Toast.makeText(rootView.getContext(), msj, Toast.LENGTH_LONG).show();
					}
			}
		};
		String url_tmp = DB.MODELS[ON_DISPLAY] + "/delete";
		Server.send(url_tmp, getActivity(), recep);
	}
	public static boolean addPermission(int on_diplay){
		switch (on_diplay) {
			case HomeActivity.ASIGNATURAS:
				return util.Settings.PERMISSION(Settings.PERMISSION_ADD_ASIGNATURA);
			case HomeActivity.HORARIOS:
				return util.Settings.PERMISSION(Settings.PERMISSION_ADD_HORARIO);
			case HomeActivity.ALERTAS:
				return util.Settings.PERMISSION(Settings.PERMISSION_ADD_ALERTA);
			case HomeActivity.APUNTES:
				return util.Settings.PERMISSION(Settings.PERMISSION_ADD_APUNTE);
			case HomeActivity.CALIFICABLES:
				return util.Settings.PERMISSION(Settings.PERMISSION_ADD_CALIFICABLE);
			case HomeActivity.LECTURAS:
				return util.Settings.PERMISSION(Settings.PERMISSION_ADD_LECTURA);
		}
		return false;
	}
	public static boolean delPermission(int on_diplay){
		switch (on_diplay) {
			case HomeActivity.ASIGNATURAS:
				return util.Settings.PERMISSION(Settings.PERMISSION_DEL_ASIGNATURA);
			case HomeActivity.HORARIOS:
				return util.Settings.PERMISSION(Settings.PERMISSION_DEL_HORARIO);
			case HomeActivity.ALERTAS:
				return util.Settings.PERMISSION(Settings.PERMISSION_DEL_ALERTA);
			case HomeActivity.APUNTES:
				return util.Settings.PERMISSION(Settings.PERMISSION_DEL_APUNTE);
			case HomeActivity.CALIFICABLES:
				return util.Settings.PERMISSION(Settings.PERMISSION_DEL_CALIFICABLE);
			case HomeActivity.LECTURAS:
				return util.Settings.PERMISSION(Settings.PERMISSION_DEL_LECTURA);
		}
		return false;
	}
	public static boolean editPermission(int on_diplay){
		switch (on_diplay) {
			case HomeActivity.ASIGNATURAS:
				return util.Settings.PERMISSION(Settings.PERMISSION_EDIT_ASIGNATURA);
			case HomeActivity.HORARIOS:
				return util.Settings.PERMISSION(Settings.PERMISSION_EDIT_HORARIO);
			case HomeActivity.ALERTAS:
				return util.Settings.PERMISSION(Settings.PERMISSION_EDIT_ALERTA);
			case HomeActivity.APUNTES:
				return util.Settings.PERMISSION(Settings.PERMISSION_EDIT_APUNTE);
			case HomeActivity.CALIFICABLES:
				return util.Settings.PERMISSION(Settings.PERMISSION_EDIT_CALIFICABLE);
			case HomeActivity.LECTURAS:
				return util.Settings.PERMISSION(Settings.PERMISSION_EDIT_LECTURA);
		}
		return false;
	}
	public abstract void show();
}
