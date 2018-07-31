package controllers;

import java.util.ArrayList;
import java.util.HashMap;

import models.DB;

import org.json.JSONException;
import org.json.JSONObject;

import webservice.Asynchtask;

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

public abstract class Controller extends Fragment implements OnItemClickListener
{
	public static boolean CLICK_BLOQUEADO=false;
	protected View rootView;
	protected ListView base;
	protected Adapter base_data;
	protected ArrayList<JSONObject> LOCAL_DB = new ArrayList<JSONObject>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.lienzo, container, false);

		ImageView imageView = (ImageView) rootView.findViewById(R.id.add);
		if(DB.COMUNIDAD)
			imageView.setVisibility(View.GONE);

		imageView.setImageResource(R.drawable.ic_tab_add);
		imageView.setOnClickListener(new OnClickListener()
			{
				public void onClick(View arg0)
				{Base.crud(getActivity(), Base.Actions.Add);}
			}
		);
		
		base = (ListView) rootView.findViewById(R.id.list);
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
					showPopup(v);
				return false;
			}
		});
		show();
		return rootView;
	}

	@Override
	public void onItemClick(AdapterView<?> av, View v, int index_item, long arg3)
	{
		if(CLICK_BLOQUEADO)
		{
			CLICK_BLOQUEADO=false;
			return;
		}
		TextView tv = ((TextView)v.findViewById(R.id.textView4));
		if(tv!=null)
			if(tv.getText().toString().equals("Aun no hay nada aqui"))
				return;
		Base.itemSeleted = index_item;
		Base.crud(getActivity(), Base.Actions.Edit);
	}

	public void showPopup(View v) 
	{
		PopupMenu popup = new PopupMenu(getActivity(), v);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.actions, popup.getMenu());
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
		String id = "";
		try 
		{
			id = LOCAL_DB.get(Base.itemSeleted).getString("id");
		} catch (JSONException e) {}
		HashMap<String, String> data_tmp = new HashMap<String, String>();
		data_tmp.put("id", id);
		Server.setDataToSend(data_tmp);
		Asynchtask recep = new Asynchtask() 
		{
			@Override
			public void processFinish(String result) 
			{
				if(result.contains("Eliminad"))
					base_data.remove(base_data.getItem(Base.itemSeleted));
				Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
				DB.update();
			}
		};
		String url_tmp = HomeActivity.onDisplay() + "/delete";
		Server.send(url_tmp, getActivity(), recep);
	}
	public abstract void show();
}
