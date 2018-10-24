package chat;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import models.DB;
import models.DB.User;
import util.Style;
import webservice.Asynchtask;

import chat.ChatAdapter.Mensaje;
import chat.ChatService.Inbox;

import controllers.Asignaturas;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import crud.Base;

import static chat.DBChat.ON_CHAT;
import static com.jhordyabonia.ag.HomeActivity.ASIGNATURAS;

public class ChatActivity  extends FragmentActivity implements Inbox,OnItemClickListener,OnItemLongClickListener
{
	private int CHAT=0;
	private int POS=0;
	private ListView base;
	private ChatAdapter base_data;
	private ArrayList<String> contactos=new ArrayList<String>();
	private ArrayList<JSONObject> asignaturas=new ArrayList<JSONObject>();
	private boolean grupo;
	private JSONObject chat;
	private EditText msj;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Style.bar(this);

		if(!DB.LOGGED)
		{
			String result= DB.load(DB.FILE_DB);
			if(!result.isEmpty())
			{
				DB.set(result);
				DB.Asignaturas.set_list();
			}
			if(!DB.LOGGED)
			{
				startActivity(new Intent(this,HomeActivity.class));
				finish();
				return;
			}
		}
		setContentView(R.layout.chat);

		getActionBar().setLogo(R.drawable.ic_chat_menu);
		getActionBar().setHomeButtonEnabled(false);

		base =  findViewById(R.id.listView1);
		base.setDividerHeight(0);
		base.setDrawSelectorOnTop(true);
		base.setVerticalScrollBarEnabled(false);
		base_data = new ChatAdapter(this,new  ArrayList<Mensaje>());

		base.setOnItemClickListener(this);
		base.setOnItemLongClickListener(this);

		base.setAdapter(base_data);
		msj= findViewById(R.id.editText1);
		
		findViewById(R.id.imageView2)
		.setOnClickListener
			(
				new OnClickListener() 
				{
					@Override
					public void onClick(View arg0) 
					{
						Asignaturas.asignaturas_list(ChatActivity.this,
							getString(R.string.share),
							DB.Asignaturas.LIST_ASIGNATURAS,
							new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									Asignaturas.compartir(
											ChatActivity.this,
											""+CHAT,
											which
											).show(getSupportFragmentManager(), "missiles");
								}
							}).show(getSupportFragmentManager(), "missiles");
					}				
				}
			);
	
		 findViewById(R.id.imageView1)
			.setOnClickListener
				(
					new OnClickListener() 
					{
						@Override
						public void onClick(View arg0) 
						{ send();}				
					}
				);
		load();
		ChatService.updater(ChatActivity.this,CHAT);
		}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{		
		getMenuInflater().inflate(R.menu.chat, menu);	
		return true;
	}
	@Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
		if(grupo)
		{
			menu.findItem(R.id.ver_perfil).setIcon(R.drawable.ic_perfil_grupo);
			menu.findItem(R.id.add_to_group).setVisible(true);
			menu.findItem(R.id.ver_perfil).setVisible(true);
			menu.findItem(R.id.salir).setVisible(true);
		}
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {           
        case R.id.borrar_chat:
        	try 
        	{
        		JSONObject tmp_=null;
        		tmp_ = DBChat.get(Integer.valueOf(DBChat.find("index","id",""+CHAT)));
        		if(tmp_!=null)
        		{	
        			tmp_.put("mensajes", null);        		
        			finish();
        		}
        	}catch (JSONException e) {}
        	return true;
        case R.id.add_to_group:
        	add.show(getSupportFragmentManager(), "missiles");
        	return true;
        case R.id.ver_perfil:        	
    		try 
    		{
        		Intent intent_tmp= new Intent(this,ProfileActivity.class);
        		intent_tmp.putExtra("CHAT",CHAT);
        		intent_tmp.putExtra("DESCRIPCION",chat.getString("descripcion"));
        		intent_tmp.putExtra("NOMBRE",chat.getString("nombre"));
        		intent_tmp.putExtra("WONNER",chat.getString("usuario"));
        		startActivity(intent_tmp);
    		}catch (JSONException e) 
    		{
    			Toast.makeText(this, getString(R.string.fail_load_caht),
    					Toast.LENGTH_LONG).show();
    		}return true;
        case R.id.salir:
        	HashMap<String, String> datos=new HashMap<String, String>();		
    		datos.put("chat",""+CHAT);
    		datos.put("celular",User.get("celular"));
    		Server.setDataToSend(datos);
    		Server.send("chat/delete", null, null);
    		finish();
        	return true;
        }
        return true;
    }
	private void load()
	{
		Intent mIntent=getIntent();
		CHAT=Integer.valueOf(mIntent.getStringExtra("CHAT"));
		add = new ChatNewDialog.ContactList(contactos,CHAT);
		try 
		{
			chat = new JSONObject(DBChat.find("id",""+CHAT));
			grupo = chat.getInt("tipo")== HomeActivity.GRUPOS-ON_CHAT;
			CHAT=chat.getInt("id");
			String nombre=chat.getString("nombre");
			String id= chat.getString("nombre")
					.replace("_"+User.get("celular")+"_","")
					.replace("_","");
			nombre=DBChat.get_contact("nombre","cel_",id);
			if(nombre.isEmpty())
				nombre=id;
			
			getActionBar().setTitle(nombre);

			JSONArray msjs = chat.getJSONArray("mensajes");
			POS=msjs.length();
			for(int i=0;i<POS;i++)
				try{add_msj(msjs.getJSONObject(i));}
				catch (JSONException e){}
			if(POS>1)
				base.smoothScrollToPosition(POS-1);
			read();	
			invalidateOptionsMenu();
			
		} catch (JSONException e) 
		{
			Toast.makeText(this, getString(R.string.fail_load_caht),
					Toast.LENGTH_LONG).show();
		}
	  }
	private void send()
	{
		final String dato=msj.getText().toString().trim();
		if(dato.isEmpty())return;
		//base_data.add(new Mensaje(getHora(),dato,true));
		//base.smoothScrollToPosition(POS++);
		msj.setText("");
		
		HashMap<String, String> datos=new HashMap<String, String>();
		datos.put("usuario", User.get("id"));
		datos.put("dato",dato);
		datos.put("chat",""+CHAT);
		datos.put("tipo", "mensaje");
		Server.setDataToSend(datos);
		Asynchtask recep = new Asynchtask() 
		{
			@Override
			public void processFinish(String result) 
			{
				if(result.isEmpty()||result.startsWith("Sin"))
					Toast.makeText(ChatActivity.this,
							getString(R.string.no_sent),
							Toast.LENGTH_LONG).show();
				else
				{
					try 
					{
						JSONObject chat_tmp = new JSONObject();
						JSONArray msjs_tmp = new JSONArray();
						JSONObject msj_tmp = new JSONObject(result);
						msjs_tmp.put(msj_tmp);
						chat_tmp.put("id", msj_tmp.getInt("chat"));
						chat_tmp.put("mensajes",msjs_tmp);
						DBChat.insert(chat_tmp);
						add_msj(msj_tmp,true);
					} catch (JSONException e) 
					{
						Toast.makeText(ChatActivity.this,
								getString(R.string.no_singup),
							Toast.LENGTH_LONG).show();

					}
				}
			}
		};
		Server.send("mensaje", null, recep);
	}
	private void add_msj(JSONObject msj_t) throws JSONException
	{add_msj(msj_t,false);}
	@Override
	public void add_msj(JSONObject msj_t,boolean move) throws JSONException
	{
		if(msj_t.getString("tipo").equals(DB.MODELS[ASIGNATURAS]))
		{		
			JSONObject asignatura= new JSONObject(msj_t.getString("dato"));
			if(msj_t.getString("usuario")
				.equals(User.get("id")))					
			base_data.add(new Mensaje(asignatura,"",
					DBChat.get_contact("nombre","id",msj_t.getString("usuario")),
					DBChat.fecha(msj_t.getString("fecha")),
					DB.titulo("("+asignatura.getString("codigo")+") "
					+asignatura.getString("nombre"),50),true,false));
			else if(!grupo)			
				base_data.add(new Mensaje(asignatura,"",
						DBChat.get_contact("nombre","id",msj_t.getString("usuario")),
						DBChat.fecha(msj_t.getString("fecha")),
						DB.titulo("("+asignatura.getString("codigo")+") "
						+asignatura.getString("nombre"),50),false,false));
			else			
				base_data.add(new Mensaje(asignatura,"",
						DBChat.get_contact("nombre","id",msj_t.getString("usuario")),
						DBChat.fecha(msj_t.getString("fecha")),
						DB.titulo("("+asignatura.getString("codigo")+") "
						+asignatura.getString("nombre"),50),false,true));	
			asignaturas.add(asignatura);
		}else if(msj_t.getString("tipo").equals("mensaje"))	
		{
			if(msj_t.getString("usuario")
					.equals(User.get("id")))					
				base_data.add(new Mensaje(
						DBChat.fecha(msj_t.getString("fecha")),
						msj_t.getString("dato"),true));
			else if(!grupo)
				base_data.add(new Mensaje(
						DBChat.fecha(msj_t.getString("fecha")),
						msj_t.getString("dato")));
			else
				base_data.add(new Mensaje("",
						DBChat.get_contact("nombre","id",msj_t.getString("usuario")),
						DBChat.fecha(msj_t.getString("fecha")),
						msj_t.getString("dato")));
			
			asignaturas.add(null);
		}else //if(msj_t.getString("tipo").equals("mensaje"))	
		{
			int icon=-1;
			if(msj_t.getString("tipo").equals("salir"))
				icon=R.drawable.ic_out_group;
			if(msj_t.getString("tipo").equals("inicio"))
				icon=R.drawable.ic_dialogo_add_togroup;
			base_data.add(new Mensaje(icon,
					DBChat.fecha(msj_t.getString("fecha")),
					msj_t.getString("dato")));
		
			asignaturas.add(null);
		}
		if(chat.getInt("tipo")!= HomeActivity.GRUPOS)
			read();
		
		if(move)
			base.smoothScrollToPosition(POS++);
	}					
	private void read()
	{		
		HashMap<String, String> datos=new HashMap<String, String>();		
		datos.put("chat",""+CHAT);
		datos.put("usuario",User.get("id"));
		Server.setDataToSend(datos);
		Server.send("mensaje/read", null, null);
	    ((NotificationManager) 
	    		getSystemService(Context.NOTIFICATION_SERVICE))
	    		.cancel(CHAT);
	}
	private DialogFragment add;

	protected String getHora()
	{
		TimePicker _hora = new TimePicker(this);
		String hora=_hora.getCurrentHour()+":"+
						_hora.getCurrentMinute();
		return hora;
	}
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int arg2, long arg3) {
		//(dw)Icono, que indica posibilidad de descarga
		ImageView dw=arg1.findViewById(R.id.imageView2);
		if(dw==null)
		{
			TextView d=arg1.findViewById(R.id.textView1);
			if(d==null)
				d=arg1.findViewById(R.id.textView4);
			if(d==null)return true;
			ClipData clip = ClipData.newPlainText("text", d.getText());
			ClipboardManager clipboard = (ClipboardManager)this.getSystemService(CLIPBOARD_SERVICE);
			clipboard.setPrimaryClip(clip);
			Toast.makeText(ChatActivity.this, getString(R.string.text_copy), Toast.LENGTH_LONG).show();
		}else
		{			
			try 
			{
				JSONObject asignatura = asignaturas.get(arg2);
				if(asignatura==null)return true;
				DialogFragment existe = Asignaturas.existe(asignatura);
				if(existe!=null)
					existe.show(getSupportFragmentManager(), "missiles");
				else Asignaturas.agregar_asignatura(asignatura.getString("id"),this);
			} catch (JSONException e) {}			
		}
		return true;
	}
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		ImageView dw=arg1.findViewById(R.id.imageView2);
		if(dw!=null)
			Toast.makeText(this, getString(R.string.keep_press_to_download), Toast.LENGTH_LONG).show();
		else 
			Toast.makeText(this, getString(R.string.keep_press_to_copy), Toast.LENGTH_LONG).show();
	}
}
