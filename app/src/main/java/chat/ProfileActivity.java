package chat;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import webservice.Asynchtask;

import models.DB;
import models.DB.User;

import chat.ChatAdapter.Mensaje;

import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import controllers.Asignaturas;
import crud.Base;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends FragmentActivity 
	implements Asynchtask, OnItemClickListener
{
	private static int CHAT = 0;
	private String wonner="";
	private ChatAdapter datos_integrantes;
	private ChatAdapter datos_asignaturas;
	private String descripcion="";
	private String nombre="";
	private ActionBar actionBar=null;
	private TextView descripcion_view=null;
	private JSONArray asignaturas=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_profile);
		
		Intent mIntent=getIntent();
		if(mIntent!=null)
		{
			CHAT=mIntent.getIntExtra("CHAT", 0);
			wonner=mIntent.getStringExtra("WONNER");
			descripcion=mIntent.getStringExtra("DESCRIPCION");
			nombre=mIntent.getStringExtra("NOMBRE");
			actionBar=getActionBar();
			actionBar.setTitle(nombre);
			descripcion_view=(TextView)findViewById(R.id.profile_descripcion);
			descripcion_view.setText(DB.titulo(descripcion,"Sin descripcion",140));
		}
				
		ListView  list=(ListView)findViewById(R.id.profile_members);
		datos_integrantes=new ChatAdapter(this,new  ArrayList<Mensaje>(),false);
		if(wonner.equals(DB.User.get("id")))
			list.setOnItemClickListener(this);
		list.setAdapter(datos_integrantes);		

		ListView  list2=(ListView)findViewById(R.id.list_asignaturas);
		datos_asignaturas =new ChatAdapter(this,new  ArrayList<Mensaje>(),false);
		list2.setOnItemClickListener(listenerAsignaturas);
		list2.setAdapter(datos_asignaturas);	
		load();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.profil, menu);
		return true;
	}
	@Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
		if(!wonner.equals(DB.User.get("id")))
			menu.findItem(R.id.edit).setVisible(false);
		
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {  
        	case R.id.edit:
        		editChat.show(getSupportFragmentManager(), "missiles");
        }
		return true;
    }
	private void showSolicitudes(JSONArray solicitudes)
	{
		findViewById(R.id.layout_solicitudes)
			.setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.profile_solicitudes))
			.setText(""+solicitudes.length());
	}
	private void showAsignaturas(JSONArray asignaturas) throws JSONException
	{
		findViewById(R.id.layout_asignatura)
		.setVisibility(View.VISIBLE);
		
		findViewById(R.id.layout_asignatura)
		.setOnClickListener(mostar_asignaturas);

		((TextView)findViewById(R.id.profile_asignaturas))
			.setText(""+asignaturas.length());
		
		for(int i=0;i<asignaturas.length();i++)
		{
			JSONObject m_tmp= new JSONObject(asignaturas.getJSONObject(i)
					.getString("dato"));
			datos_asignaturas.add(
					new Mensaje("",
							m_tmp.getString("nombre"),
							m_tmp.getString("codigo"),
							m_tmp.getString("creditos"),
							android.R.drawable.ic_input_get));
		}
	}
	private OnItemClickListener listenerAsignaturas=new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) 
		{ 
			if(asignaturas!=null)try 
			{
				JSONObject asignatura = 
						new JSONObject(
								asignaturas.getJSONObject(arg2).getString("dato"));

				DialogFragment existe = 
						Asignaturas.existe(false,ProfileActivity.this,asignatura);
				if(existe!=null)
					existe.show(getSupportFragmentManager(), "missiles");
				else Asignaturas.agregar_asignatura(asignatura.getString("id"),ProfileActivity.this);
			} catch (JSONException e) {}
		}		
	};
	private OnClickListener mostar_asignaturas= new OnClickListener()
	{
		@Override
		public void onClick(View arg0) 
		{
			TextView t=(TextView)findViewById(R.id.mostrar_asignaturas);
			if(t.getText().toString().equals("-"))
			{
				findViewById(R.id.list_asignaturas)
				.setVisibility(View.GONE);
				t.setText("+");
			}else
			{
				findViewById(R.id.list_asignaturas)
				.setVisibility(View.VISIBLE);
				t.setText("-");
			}
		}		
	};
	@Override
	public void onActivityResult(int code_sent, int code_result,Intent data)
	{
		if(code_result==android.app.Activity.RESULT_OK)
		{
			String d=data.getStringExtra(Base.DATA);
			Asignaturas.actualizar_asignatura(this,""+code_sent,d);
		}
	}
	@Override
	public void processFinish(String result) 
	{
		try 
		{
			JSONObject data= new JSONObject(result);
			JSONArray miembros_tmp= data.getJSONArray("integrantes");
			asignaturas= data.getJSONArray("asignaturas");
			JSONArray solicitudes= data.getJSONArray("solicitudes");
			if(asignaturas.length()>0)
				showAsignaturas(asignaturas);
			if(solicitudes.length()>0)
				showSolicitudes(solicitudes);
						
			for(int i=0;i<miembros_tmp.length();i++)
			{
				JSONObject m_tmp= miembros_tmp.getJSONObject(i);
				datos_integrantes.add(
						new Mensaje("",
								m_tmp.getString("nombre"),
								DBChat.fecha(m_tmp.getString("fecha")),
								m_tmp.getString("celular")));
			}
		}catch (JSONException e) 
		{
			Toast.makeText(this, "Error durante la carga del perfil",
					Toast.LENGTH_LONG).show();
			finish();
		}		
	}

	@Override
	public void onItemClick(AdapterView<?> av, View v, int index_item, long arg3)
	{
		PopupMenu popup = new PopupMenu(this, v);
		final String celular=((TextView)v.findViewById(R.id.textView4))
				.getText().toString();
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.actions, popup.getMenu());
		popup.show();
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() 
		{
			@Override
			public boolean onMenuItemClick(MenuItem arg0) 
			{
				delete(celular);
				return false;
			}
		});
	}
	private void delete(String celular) 
	{
		HashMap<String, String> datos=new HashMap<String, String>();		
		datos.put("chat",""+CHAT);
		datos.put("celular",celular);
		Server.setDataToSend(datos);
		Server.send("chat/delete", this, null);	
	}
	private void load()
	{
		HashMap<String, String> datos=new HashMap<String, String>();		
		datos.put("chat",""+CHAT);
		datos.put("usuario",User.get("id"));
		Server.setDataToSend(datos);
		Server.send("integrantes", this, this);
	}
	private DialogFragment editChat = new DialogFragment()
	{
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			View view=getActivity().getLayoutInflater()
	    			.inflate(R.layout.new_group, null);
			
			final EditText nombre_=(EditText)view.findViewById(R.id.editText1);
			final EditText descripcion_=(EditText)view.findViewById(R.id.editText2);

			nombre_.setText(nombre);
			descripcion_.setText(descripcion);
			DialogInterface.OnClickListener dialogListener
			= new DialogInterface.OnClickListener()	
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					switch(which)
					{
					case DialogInterface.BUTTON_NEGATIVE:
						dialog.dismiss();break;
					case DialogInterface.BUTTON_POSITIVE:
						{
							String n=nombre_.getText().toString().trim();
							if(n.isEmpty())
							{
								Toast.makeText(ProfileActivity.this,
										"Nombre no debe estar vacio",
										Toast.LENGTH_LONG).show();
								return;
							}
							chat_edit(nombre_.getText().toString(),
									descripcion_.getText().toString());
						}
					}
				}
			};
			AlertDialog.Builder builder = 
					new AlertDialog.Builder(ProfileActivity.this);
			builder.setTitle("Editar Grupo")
			.setIcon(android.R.drawable.ic_menu_edit)
		       .setPositiveButton("Aceptar", dialogListener)
		       .setNegativeButton("Cancelar", dialogListener);
			
		    builder.setView(view);
			return builder.create();
		}
	};
	private void chat_edit(final String nombre,final String descripcion)
	{
		HashMap<String, String> datos=new HashMap<String, String>();
		datos.put("usuario", User.get("id"));
		datos.put("chat", ""+CHAT);
		datos.put("nombre",nombre);
		datos.put("descripcion",descripcion);
		Server.setDataToSend(datos);
		Asynchtask recep = new Asynchtask() 
		{
			@Override
			public void processFinish(String result) 
			{	
				Toast.makeText(ProfileActivity.this,result,
					Toast.LENGTH_LONG).show();
				if(result.equals("Chat editado!"))
				{
					actionBar.setTitle(nombre);
					descripcion_view
					.setText(DB.titulo(descripcion,"Sin descripcion",140));
				}
			}
		};
		Server.send("chat_edit", this, recep);
	}
}
