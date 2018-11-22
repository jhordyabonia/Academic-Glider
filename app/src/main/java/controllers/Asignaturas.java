package controllers;

import java.util.ArrayList;
import java.util.HashMap;

import models.DB;
import models.DB.User;

import org.json.JSONException;
import org.json.JSONObject;

import util.CompartirAsignatura;
import util.Settings;
import util.Style;
import webservice.Asynchtask;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.jhordyabonia.ag.SettingsActivity;

import controllers.Adapter.ITEM_TYPE;
import controllers.Adapter.Item;
import crud.AsignaturaActivity;
import crud.Base;
import webservice.LOG;

import static com.jhordyabonia.ag.HomeActivity.ALERTAS;
import static com.jhordyabonia.ag.HomeActivity.ASIGNATURAS;
import static com.jhordyabonia.ag.HomeActivity.HORARIOS;
import static com.jhordyabonia.ag.HomeActivity.ON_DISPLAY;

public class Asignaturas implements OnItemClickListener 
{
	public static final String WAITING_CONTENT="WAITING_CONTENT";
	public static final String RESULT="RESULT";
	private HomeActivity home;
	private ListView base;
	private Adapter base_data;

	public Asignaturas(HomeActivity fa)
	{	home = fa;}

	public void pager(View view,boolean show)
	{
		if(!show)
		{
			view.findViewById(R.id.paginator)
			.setVisibility(View.GONE);
			return;
		}
		try
		{
			JSONObject pager=(JSONObject)DB.get("pager");
			String data,before_,after_,a_buscar_;
			before_=pager.getString("before");
			after_=pager.getString("after");
			data=pager.getString("data");
			a_buscar_=pager.getString("a_buscar");
			final String after=after_;
			final String before=before_;
			final String a_buscar=a_buscar_;
			OnClickListener listener=new OnClickListener()
			{
				@Override
				public void onClick(View arg0) 
				{
					HashMap<String, String> datos=new HashMap<String, String>();
					switch(arg0.getId())
					{
					case R.id.adelante:
						datos.put("page", after);
						break;
					case R.id.atras:
						datos.put("page", before);
						break;
					}
					datos.put("a_buscar", a_buscar);
					DB.update(home, datos);
				}				
			};

			//Indicador de pagina
			((TextView)view.findViewById(R.id.textView2))
			.setText(data);
			//adelante
			view.findViewById(R.id.adelante)
			.setOnClickListener(listener);
			//atras
			view.findViewById(R.id.atras)
			.setOnClickListener(listener);
			//habilitando paginador
			view.findViewById(R.id.paginator)
			.setVisibility(View.VISIBLE);
		} catch (JSONException e) {}		
	}
	public void todas(View view)
	{
		Style.set(view);
		Base.itemSeleted = 0;
		ON_DISPLAY=HomeActivity.ASIGNATURAS;
		ImageView imageView =  view.findViewById(R.id.add);
		base =  view.findViewById(R.id.list);

		if(DB.COMUNIDAD)
		{	
			imageView.setVisibility(View.VISIBLE);
			imageView.setImageResource(R.drawable.twotone_zoom_out_white_24);
			imageView.setOnClickListener(new OnClickListener()
				{
					public void onClick(View arg0)
					{home.buscar();}
				}
			);
		}
		else
		{
			if(!Controller.addPermission(ASIGNATURAS))
				imageView.setVisibility(View.GONE);
			imageView.setImageResource(R.drawable.ic_stat_name);
			imageView.setOnClickListener(new OnClickListener()
				{
					public void onClick(View arg0)
					{Base.crud(home, Base.Actions.Add);}
				}
			);
		}


		pager(view,DB.COMUNIDAD);
		base_data = new Adapter(view.getContext(),ITEM_TYPE.asignatura,Adapter.asignaturas);

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
				showPopup(v);
				return false;
			}
		});
		base.setAdapter(base_data);
		/*try{DB.buffer();}
		catch (JSONException e ){}*/
		DB.model(DB.MODELS[ON_DISPLAY]);
		ArrayList<JSONObject> tmp = DB.find("", "");
		if(!tmp.isEmpty())
		try
		{
			base_data.clear();
			for (JSONObject v : tmp) {
				if(v.getString("id").equals("-1")) {
					base_data.add(
							new Item("",
									v.getString("nombre"),
									v.getString("nota"),
									v.getString("creditos")
								));
					break;
				}else {
					String descripcion="",img=v.getString("nombre")+".jpg";
					if(!v.isNull("imagen"))
						img=v.getString("imagen");
					if(!v.isNull("descripcion"))
						descripcion=v.getString("descripcion");

					base_data.add(
							new Item(
									v.getString("codigo"),
									v.getString("nombre"),
									v.getString("nota"),
									v.getString("creditos"),
									descripcion,
									img
								));
				}
			}
		} catch (JSONException e) {}
	}

	public void showPopup(View v) 
	{
		PopupMenu popup = new PopupMenu(home, v);
		MenuInflater inflater = popup.getMenuInflater();
		if(DB.COMUNIDAD) {
			inflater.inflate(R.menu.actions_comunidad, popup.getMenu());
			popup.getMenu().findItem(R.id.descargar).setEnabled(Settings.PERMISSION(Settings.PERMISSION_DOWNLOAD_ASIGNATURA));
		}else{
			inflater.inflate(R.menu.actions_asignatura,  popup.getMenu());
			popup.getMenu().findItem(R.id.compartir).setEnabled(Settings.PERMISSION(Settings.PERMISSION_SHARE_ASIGNATURA));
            popup.getMenu().findItem(R.id.delete).setEnabled(Controller.delPermission(ASIGNATURAS));
		}
		popup.show();
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem menu) 
			{
				switch(menu.getItemId())
				{
				case R.id.ver:
					Base.crud(home, Base.Actions.Edit);
					break;
				case R.id.horarios:
					home.horarios_asignatura();
					break;
				case R.id.share:
					items_a_compartir.clear();
					compartir(home,compartir_com,Base.itemSeleted).show(home.getSupportFragmentManager(), "missiles");
					break;
				case R.id.delete:
					if(Controller.delPermission(HORARIOS))
						delete();
					break;
				case R.id.descargar:
					try 
					{
						if(Base.itemSeleted<DB.Asignaturas.LIST_ID_ASIGNATURAS.length) {
							String tmp = DB.Asignaturas.LIST_ID_ASIGNATURAS[Base.itemSeleted];
							DB.model(DB.MODELS[ON_DISPLAY]);

							boolean b=DB.COMUNIDAD;
							DB.COMUNIDAD=false;
							JSONObject asignatura = DB.getBy("id", tmp);//.get(arg2);
							DB.COMUNIDAD=b;
							DialogFragment existe = existe(asignatura);
							if (existe != null)
								existe.show(home.getSupportFragmentManager(), "missiles");
							else agregar_asignatura(tmp, home);

						}else DB.Asignaturas.set_list();
					} catch (JSONException e) {}
					break;
				}
				return false;
			}
		});
	}
	private void delete() 
	{
		String id = DB.Asignaturas.LIST_ID_ASIGNATURAS[Base.itemSeleted];
		HashMap<String, String> data_tmp = new HashMap<>();
		data_tmp.put("id", id);
		Server.setDataToSend(data_tmp);
		Asynchtask recep = new Asynchtask()
		{
			@Override
			public void processFinish(String result) 
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
							for(String model:DB.MODELS)
								if(!model.equals(DB.MODELS[ASIGNATURAS]))
									delete(model,tmp.getString("id"));
							DB.Asignaturas.set_list();
							View view=home.findViewById(R.id.FrameLayout1);
							todas(view);
						}else Toast.makeText(home,"No se removio",Toast.LENGTH_LONG).show();
						DB.model(DB.MODELS[ON_DISPLAY]);
						DB.update();
					}
				}catch (JSONException e){}
				Toast.makeText(home, msj, Toast.LENGTH_LONG).show();
			}
		};
		String url_tmp = DB.MODELS[ON_DISPLAY]  + "/delete";
		Server.send(url_tmp, home, recep);
	}
	private void delete(String model,String id) throws JSONException {
		DB.model(model);
		ArrayList <JSONObject>data=DB.find("asignatura",id);
		for(JSONObject obj:data)
			DB.remove(obj);
	}

	@Override
	public void onItemClick(AdapterView<?> av, View v, int index_item, long arg3) 
	{	
		if(Controller.CLICK_BLOQUEADO)
		{
			Controller.CLICK_BLOQUEADO=false;
			return;
		}
		TextView tv = v.findViewById(R.id.textView1);
		if(tv!=null)
			if(tv.getText().toString().equals(v.getContext().getString(R.string.empty)))
				return;

		Base.itemSeleted = index_item;
		home.abrirAsignatura();
	}
	public static DialogFragment asignaturas_list(FragmentActivity activity, String titulo, String[] items,
			final DialogInterface.OnClickListener actions)
	{return new CompartirAsignatura.List(activity,titulo,items,actions); }
	public static DialogFragment existe(JSONObject asignatura) throws JSONException
	{
		if(asignatura==null)
			return null;

		String codigo=asignatura.getString("codigo");
		String nombre=asignatura.getString("nombre");
		boolean b=DB.COMUNIDAD;
		DB.COMUNIDAD=false;
		ArrayList<JSONObject> asignaturas_tmp = DB.Asignaturas.find("codigo", codigo);
		if(asignaturas_tmp.isEmpty())
			asignaturas_tmp = DB.Asignaturas.find("nombre", nombre);

		DB.COMUNIDAD=b;
		if(!asignaturas_tmp.isEmpty())
			return  new CompartirAsignatura.AsignaturaExist(asignaturas_tmp,asignatura.getString("id"));

		return null;			
	}
	public static void actualizar(final FragmentActivity activity,final ArrayList<JSONObject> list_in,final String descargar)
	{
		final String[]LIST_ASIGNATURAS=DB.Asignaturas.LIST_ASIGNATURAS;
		boolean b=DB.COMUNIDAD;
		DB.COMUNIDAD=false;
		final ArrayList<JSONObject> todas = DB.find("","");
		DB.COMUNIDAD=b;

		int count=0;
		String list_tmp[]=new String [list_in.size()+1];
		list_tmp[count++]=activity.getString(R.string.see_all);

		final String list[]=list_tmp;

		try
		{
			for(JSONObject obj:list_in) {
				String d = obj.getString("nombre");
				if (d != null)
					list[count++] = d;
			}
		} catch (JSONException e) {}

		DialogInterface.OnClickListener listener
		=new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				if(LIST_ASIGNATURAS.length!=list.length)
				{
				   if(util.Settings.DROP_MODE)
				   {

						   if(0==which)
						   {
							   actualizar(activity,todas,descargar);
							   return;
						   }
				   }else
				   	{
					   if(!DB.COMUNIDAD)
					   {
						   if(0==which)
						   {
							   actualizar(activity,todas,descargar);
							   return;
						   }
					   }
					}
					which-=1;
					Toast.makeText(activity, "which-1", Toast.LENGTH_SHORT).show();
				}

				 if(list_in.size()>which) try
				 {actualizar_asignatura(activity,descargar,list_in.get(which).getString("id"));}
				 catch (JSONException e) {}
				 else Toast.makeText(activity, "major", Toast.LENGTH_SHORT).show();
			}
		};

		asignaturas_list(activity,"actualizar",list,listener)
		.show(activity.getSupportFragmentManager(), "missiles");		
	}
	public static void actualizar_asignatura(final FragmentActivity activity,String de,String a)
	{
		HashMap<String, String> data= new HashMap<String, String>();
		data.put("usuario", User.get("id"));
		data.put("de", de);
		data.put("a", a);
		Server.setDataToSend(data);

        Asynchtask  recep = new Asynchtask()
        {
            @Override
            public void processFinish(String result)
            {
                JSONObject mData;
                String msj="";
                try{
                    mData= new JSONObject(result);
                    msj=mData.getString("menssage");
                    DB.insert(mData);
                }catch (JSONException e){}
                Toast.makeText(activity, msj, Toast.LENGTH_LONG).show();
                //HomeActivity.UPDATE=true;
                //DB.update();
            }
        };
		Server.send("actualizar", activity, recep);		
	}
	public static void agregar(FragmentActivity activity,String descarga)
	{
		Intent intent=new Intent(activity, AsignaturaActivity.class);
		intent.putExtra(RESULT, HomeActivity.ASIGNATURAS);
		intent.putExtra(WAITING_CONTENT, true);
		Base.action=Base.Actions.Add;
		int asignatura_fuente=Integer.valueOf(descarga);
		activity.startActivityForResult(intent,asignatura_fuente);
	}
	public static void agregar_asignatura(String descargar,final Activity activity)
	{
		HashMap<String, String> data= new HashMap();
		data.put("usuario", User.get("id"));
		data.put("asignatura", descargar);
		Server.setDataToSend(data);

        Asynchtask  recep = new Asynchtask()
        {
            @Override
            public void processFinish(String result)
            {
                JSONObject mData;
                String msj="";
                try{
                    mData= new JSONObject(result);
                    msj=mData.getString("menssage");
                    DB.insert(mData);
                }catch (JSONException e){LOG.save(result,"down.txt");}
                Toast.makeText(activity, msj, Toast.LENGTH_LONG).show();
                HomeActivity.UPDATE=true;
                DB.update();
            }
        };
		Server.send("descargar", activity, recep);		
	}

	public static String compartir_com = "";
	public static ArrayList<String> items_a_compartir = new ArrayList<>();

	public static DialogFragment compartir(Activity activity,String compartir_com,int index_asignatura)
	{return new CompartirAsignatura(activity,compartir_com,index_asignatura);}


}

