package controllers;

import java.util.ArrayList;
import java.util.HashMap;

import models.DB;
import models.DB.User;

import org.json.JSONException;
import org.json.JSONObject;

import util.CompartirAsignatura;
import util.UploadService;
import webservice.Asynchtask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import com.jhordyabonia.ag.PlaceholderFragment;
import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import controllers.Adapter.ITEM_TYPE;
import controllers.Adapter.Item;
import crud.AsignaturaActivity;
import crud.Base;

import static com.jhordyabonia.ag.HomeActivity.ON_DISPLAY;

public class Asignaturas implements OnItemClickListener 
{
	private HomeActivity home;
	private ListView base;
	private Adapter base_data;
	ViewPager mViewPager;

	public Asignaturas(HomeActivity fa)
	{	home = fa;}

	public void setPager(ViewPager vPager)
	{mViewPager=vPager;}
	public void show(int display)
	{
		//home.setContentView(R.layout.fragment_collection_object);
		//home.invalidateOptionsMenu();
		CollectionPagerAdapter mCollectionPagerAdapter =
				new CollectionPagerAdapter
				(home.getSupportFragmentManager());
		//ViewPager mViewPager =  view.findViewById(R.id.pager);
		mViewPager.setAdapter(mCollectionPagerAdapter);
		mViewPager.setOnPageChangeListener
				(new ViewPager.SimpleOnPageChangeListener() 
				{
					@Override
					public void onPageSelected(int position) 
					{	
						ON_DISPLAY = position;
						Base.itemSeleted = 0;
					}
				});
		if(ON_DISPLAY==HomeActivity.ASIGNATURAS)
			ON_DISPLAY=display;
		else if(ON_DISPLAY!=HomeActivity.HORARIOS)
			mViewPager.setCurrentItem(ON_DISPLAY,true);
	}
	public void pager(View view,boolean show)
	{
		if(!show)
		{
			view.findViewById(R.id.pager)
			.setVisibility(View.GONE);
			return;
		}
		try
		{
			JSONObject pager=(JSONObject)DB.get("pager");
			String data="",before_="",after_="",a_buscar_=null;
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
			view.findViewById(R.id.pager)
			.setVisibility(View.VISIBLE);
		} catch (JSONException e) {}		
	}
	public void todas(View view)
	{
		Base.itemSeleted = 0;
		ON_DISPLAY=HomeActivity.ASIGNATURAS;
		ImageView imageView =  view.findViewById(R.id.add);

		base =  view.findViewById(R.id.list);

		if(DB.COMUNIDAD)
		{	
			imageView.setVisibility(View.VISIBLE);
			imageView.setImageResource(R.drawable.ic_buscar);
			imageView.setOnClickListener(new OnClickListener()
				{
					public void onClick(View arg0)
					{home.buscar();}
				}
			);
		}
		else
		{
			imageView.setImageResource(R.drawable.ic_tab_add);
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

		DB.model(DB.MODELS[ON_DISPLAY]);
		ArrayList<JSONObject> tmp = DB.find("", "");
		if(!tmp.isEmpty())
		try 
		{

			base_data.clear();
			String titulo=(DB.COMUNIDAD?"":home.getString(R.string.note)+" ");
			int limite=(DB.COMUNIDAD?24:0);
			for (JSONObject v : tmp)
				base_data.add(
						new Item(
								v.getString("codigo"),
								v.getString("nombre"), 
								titulo+ DB.titulo(v.getString("nota"),limite)
								,home.getString(R.string.credit)+" "+v.getString("creditos")
							));
		} catch (JSONException e) {}
	}

	public void showPopup(View v) 
	{
		PopupMenu popup = new PopupMenu(home, v);
		MenuInflater inflater = popup.getMenuInflater();
		if(DB.COMUNIDAD)
			inflater.inflate(R.menu.actions_comunidad,  popup.getMenu());
		else inflater.inflate(R.menu.actions_asignatura,  popup.getMenu());
		
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
				case R.id.compartir:
					items_a_compartir.clear();
					compartir(home,compartir_com,Base.itemSeleted).show(home.getSupportFragmentManager(), "missiles");
					break;
				case R.id.delete:
					delete();break;
				case R.id.descargar:
					try 
					{
						String tmp=DB.Asignaturas.LIST_ID_ASIGNATURAS[Base.itemSeleted];
						DB.model(DB.MODELS[ON_DISPLAY]);
						JSONObject asignatura = DB.getBy("id", tmp);//.get(arg2);
						DialogFragment existe = Asignaturas.existe(true,home,asignatura);
						if(existe!=null)
							existe.show(home.getSupportFragmentManager(), "missiles");
						else agregar_asignatura(asignatura.getString("id"),home);
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
		String url_tmp = DB.MODELS[ON_DISPLAY]  + "/delete";
		Server.send(url_tmp, home, recep);
	}

	public class CollectionPagerAdapter extends FragmentStatePagerAdapter
	{
		public CollectionPagerAdapter(FragmentManager fm)
		{super(fm);}

		@Override
		public Fragment getItem(int i) 
		{
			Fragment fragment = null;

			DB.model(DB.MODELS[i]);
			switch (i) 
			{
				case HomeActivity.ALERTAS:
					fragment = new Alertas();
					break;
				case HomeActivity.APUNTES:
					fragment = new Apuntes();
					break;
				case HomeActivity.LECTURAS:
					fragment = new Lecturas();
					break;
				case HomeActivity.CALIFICABLES:
					fragment = new Calificables();
					break;
			}
			return fragment;
		}

		@Override
		public int getCount() {return 4;}
		@Override
		public CharSequence getPageTitle(int position) 
		{return HomeActivity.onDisplay(position,home);}
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
	public static DialogFragment existe(final boolean alt,final FragmentActivity activity,JSONObject asignatura) throws JSONException
	{
		if(alt)			
		{
			DB.local();
			DB.Asignaturas.set_list();
		}
		String codigo=asignatura.getString("codigo");
		String nombre=asignatura.getString("nombre");
		ArrayList<JSONObject> asignaturas_tmp = DB.Asignaturas.find("codigo", codigo);
		if(asignaturas_tmp.isEmpty())
			asignaturas_tmp = DB.Asignaturas.find("nombre", nombre);
		if(alt)			
		{
			DB.current();
			DB.Asignaturas.set_list();
		}
		if(!asignaturas_tmp.isEmpty())
			return  new CompartirAsignatura.AsignaturaExist(asignaturas_tmp,alt,asignatura.getString("id"));

		return null;			
	}
	public static void actualizar(final boolean alt,final FragmentActivity activity,final ArrayList<JSONObject> list_in,final String descargar)
	{
		if(alt)			
		{
			DB.local();
			DB.Asignaturas.set_list();
		}
		final String[]LIST_ASIGNATURAS=DB.Asignaturas.LIST_ASIGNATURAS;
		final ArrayList<JSONObject> todas = DB.find("","");
		if(alt)			
		{
			DB.current();
			DB.Asignaturas.set_list();
		}
		int count=0;
		String list_tmp[]=new String [list_in.size()+1];
		if(!(!alt&&DB.COMUNIDAD))
		{	
			if(LIST_ASIGNATURAS.length!=list_in.size())
				list_tmp[count++]=activity.getString(R.string.see_all);
			else list_tmp = new String [list_in.size()];
		}else list_tmp = new String [list_in.size()];
		final String list[]=list_tmp;
		DialogInterface.OnClickListener listener
		=new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				if(LIST_ASIGNATURAS.length!=list.length)
				{
				   if(!(!alt&&DB.COMUNIDAD))
				   {
					   if(0==which)
					   {
						   actualizar(false,activity,todas,descargar);
						   return;
						}
					   which-=1;
				   }
				 }
				
				 try  
				 {actualizar_asignatura(activity,descargar,list_in.get(which).getString("id"));}
				 catch (JSONException e) {}
			}
		};
		
		try 
		{
			for(JSONObject obj:list_in)
				list[count++]=obj.getString("nombre");
		} catch (JSONException e) {};
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
		Asynchtask recep = new Asynchtask()
		{
			@Override
			public void processFinish(String result) 
			{				
				Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
				HomeActivity.UPDATE=true;
				DB.update();
			}
		};
		Server.send("actualizar", activity, recep);		
	}
	public static void agregar(FragmentActivity activity,String descarga)
	{
		Intent intent=new Intent(activity, AsignaturaActivity.class);
		intent.putExtra("RESULT", HomeActivity.ASIGNATURAS);
		Base.action=Base.Actions.Add;
		int asignatura_fuente=Integer.valueOf(descarga);
		activity.startActivityForResult(intent,asignatura_fuente);
	}
	public static void agregar_asignatura(String descargar,final Activity activity)
	{
		HashMap<String, String> data= new HashMap<String, String>();
		data.put("usuario", User.get("id"));
		data.put("asignatura", descargar);
		Server.setDataToSend(data);
		Asynchtask recep = new Asynchtask()
		{
			@Override
			public void processFinish(String result) 
			{				
				Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
				HomeActivity.UPDATE=true;
				DB.update();
			}
		};
		Server.send("descargar", activity, recep);		
	}

	String compartir_com = "";
	ArrayList<String> items_a_compartir = new ArrayList<>();

	public static DialogFragment compartir(Activity activity,String compartir_com,int index_asignatura)
	{return new CompartirAsignatura(activity,compartir_com,index_asignatura);}

}

