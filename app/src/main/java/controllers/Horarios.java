package controllers;

import java.util.ArrayList;
import java.util.HashMap;

import models.DB;

import org.json.JSONException;
import org.json.JSONObject;

import webservice.Asynchtask;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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

import controllers.Adapter.ITEM_TYPE;
import controllers.Adapter.Item;
import crud.Base;

import static com.jhordyabonia.ag.HomeActivity.HORARIOS;
import static com.jhordyabonia.ag.HomeActivity.ON_DISPLAY;

public class Horarios implements OnItemClickListener 
{
	public static String DIA=DB.DAYS[DB.HOY];
	public static String ASIGNATURA=null;
	private HomeActivity home;
	private ListView base;
	private Adapter base_data;
	private ArrayList<JSONObject> LOCAL_DB = new ArrayList<>();
	
	public static void setDia(int index) 
	{	ASIGNATURA=null; DIA = DB.DAYS[index];}

	public Horarios(HomeActivity fa)
	{	home = fa; }

	public void show()
	{
		home.setContentView(R.layout.lienzo);
		View view=home.findViewById(R.id.FrameLayout1);
		show(view);
	}
	public void show(final View v)
	{
		ImageView imageView = v.findViewById(R.id.add);
		if(DB.Asignaturas.LIST_ASIGNATURAS.length<=0||DB.COMUNIDAD)
			imageView.setVisibility(View.GONE);
		imageView.setImageResource(R.drawable.ic_tab_add);
		imageView.setOnClickListener(new OnClickListener()
			{
				public void onClick(View arg0)
				{Base.crud(v.getContext(), Base.Actions.Add);}
			}
		);
		base =  v.findViewById(R.id.list);
		base_data = new Adapter(v.getContext(),ITEM_TYPE.horario,Adapter.horarios);
		base.setAdapter(base_data);

		base.setDividerHeight(0);
		base.setOnItemClickListener(this);
		base.setOnItemLongClickListener(new OnItemLongClickListener() 
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int index_item, long arg3)
			{
				Base.itemSeleted = index_item;
				Controller.CLICK_BLOQUEADO=true;
				if(!DB.COMUNIDAD)
					showPopup(v);
				return false;
			}
		});
		DB.model(DB.MODELS[HORARIOS]);
		if(ASIGNATURA!=null)
			LOCAL_DB = DB.find("asignatura", ASIGNATURA);
		else LOCAL_DB = DB.find("dia", DIA);
		if(!LOCAL_DB.isEmpty())
		try 
		{
			base_data.clear();
			if(ASIGNATURA!=null)
				for (JSONObject vv : LOCAL_DB)
					base_data.add(new Item(vv.getString("dia")
							, vv.getString("hora")
							+ "  " + vv.get("ubicacion")));
			else
				for (JSONObject vv : LOCAL_DB)
					base_data.add(new Item(DB.Asignaturas.getName(vv.getString("asignatura"))
							,DB.titulo(vv.getString("hora"))
							+ "  " + vv.get("ubicacion")));
		} catch (JSONException e) {}
	}

	public void showPopup(View v) 
	{
		PopupMenu popup = new PopupMenu(home, v);
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

	private void delete()
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
				Toast.makeText(home, result, Toast.LENGTH_SHORT).show();
				DB.update(home);
			}
		};
		String url_tmp =  DB.MODELS[ON_DISPLAY]+ "/delete";
		Server.send(url_tmp, home, recep);
	}

	@Override
	public void onItemClick(AdapterView<?> av, View v, int index_item, long arg3) 
	{
		if(Controller.CLICK_BLOQUEADO)
		{
			Controller.CLICK_BLOQUEADO=false;
			return;
		}
		TextView tv = v.findViewById(R.id.textView4);
		if(tv!=null)
			if(tv.getText().toString().equals(v.getContext().getString(R.string.empty)))
				return;
		Base.itemSeleted = index_item;
		Base.crud(home, Base.Actions.Edit);
	}
}
