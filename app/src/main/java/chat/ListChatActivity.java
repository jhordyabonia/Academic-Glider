package chat;

import java.util.ArrayList;
import java.util.HashMap;

import models.DB;
import models.DB.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import webservice.Asynchtask;

import chat.ChatAdapter.Mensaje;
import chat.ChatService.Inbox;
import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.InformacionActivity;
import com.jhordyabonia.ag.ListChat;
import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import controllers.Alertas;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Messenger;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

public class ListChatActivity extends FragmentActivity implements Inbox,
		ActionBar.TabListener {

	public static final int CONTACTOS = 2;
	public static final int CHATS = 1;
	public static final int GRUPOS = 0;
	public static int ON_DISPLAY = CONTACTOS;

	private final ListChat listChat[]=new ListChat[3];
	public final ArrayList<String> chats=new ArrayList<String>();
	public final ArrayList<String> grupos=new ArrayList<String>();
	public final ArrayList<String> contactos=new ArrayList<String>();
	public final ArrayList<Integer> contactos_id=new ArrayList<Integer>();
	
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private boolean out = false;
	public void setPage(int i,boolean b)
	{mViewPager.setCurrentItem(i,b);}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
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
		DBChat.init(this);
		
		setContentView(R.layout.activity_list_chat);
		updater();
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		for(int i=0;i<3;i++)
		{
			listChat[i] = new ListChat();
			Bundle args = new Bundle();
			args.putInt("ON_DISPLAY", i);
			listChat[i].setArguments(args);
		}

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		mViewPager = findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
/*
		mViewPager.setOnPageChangeListener
		(
			new ViewPager.SimpleOnPageChangeListener()
			{
				@Override
				public void onPageSelected(int position) {
					actionBar.setSelectedNavigationItem(position);
					ON_DISPLAY=position;
				}
			}
		);

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		};
		
		Intent mIntent=getIntent();
		if(mIntent!=null)
			ON_DISPLAY=mIntent.getIntExtra("ON_DISPLAY", CONTACTOS);

		mViewPager.setCurrentItem(ON_DISPLAY, false);*/
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list_chat, menu);
		return true;
	}
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
		ON_DISPLAY=tab.getPosition();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {}
	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {}
	public class SectionsPagerAdapter extends FragmentPagerAdapter 
	{
		public SectionsPagerAdapter(FragmentManager fm) 
		{super(fm);}

		@Override
		public Fragment getItem(int position)
//		{	return new Fragment();	}
		{	return listChat[position];	}
//		{	return listChat[0];	}

		@Override
		public int getCount() 
		{	return listChat.length;	}

		@Override
		public CharSequence getPageTitle(int position) 
		{
			switch (position) 
			{
				case CONTACTOS:
					return "COMPAÑEROS";
				case CHATS:
					return "CHATS";
				case GRUPOS:
					return "GRUPOS";
			}
			return "";
		}
	}
	public AsyncTask<String, Void, String> getContactCheker()
	{
		return new AsyncTask<String, Void, String>()
		{
			@Override
			protected String doInBackground(String... arg0) 
			{
				Cursor cursor = getContacts();
				JSONArray data= new JSONArray();
		        if(cursor.moveToFirst())
		        	do try 
					{					
						for(String cel:celular(cursor.getString(0)).split(","))
						{	
							String nombre=cursor.getString(1);
							if(cel.isEmpty()||data.toString().contains(cel))
								continue;
							
			        		JSONObject _data=new JSONObject();
			        		_data.put("nombre", nombre.isEmpty()?cel:nombre);
							_data.put("cel", cel);
						
							data.put(_data);
						}
					} catch (JSONException e) {}
					while(cursor.moveToNext());	
				return data.toString();
			}
			protected void onPostExecute(String contacts) 
		    {
				HashMap<String, String> datos=new HashMap<String, String>();
				datos.put("contactos", contacts);
				Server.setDataToSend(datos);
				Asynchtask recep = new Asynchtask() 
				{
					@Override
					public void processFinish(String result) 
					{
						if(!result.isEmpty()&&result.startsWith("[{"))
						     DB.save(ListChatActivity.this, result, DBChat.FILE_CONTACTS);
					}
				};
				Server.send("contactos", null, recep);
		    }			
		};	
	}
		
	private Cursor getContacts()
    {
        // Run query
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
               };
        String selection = "";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        
        return getContentResolver().query(
        		uri,
        		projection,
        		selection,
        		selectionArgs,
        		sortOrder);
    }
	private String celular(String id)
	 {
		 Cursor c = getContentResolver().query(
				 ContactsContract.Data.CONTENT_URI,
				 new String[] 
					 { 
					 	ContactsContract.Data._ID,
					 	ContactsContract.CommonDataKinds.Phone.NUMBER,
					 	ContactsContract.CommonDataKinds.Phone.TYPE,
					 	ContactsContract.CommonDataKinds.Phone.LABEL
					 },
				ContactsContract.Data.CONTACT_ID +"=?"+" AND "+ ContactsContract.Data.MIMETYPE +"='"+ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE +"'",
				new String[] { String.valueOf(id) },
				null);
		 String out="";
		 if(c.moveToFirst())
			 do
			 {
				 out+=","+c.getString(1);
			 }while(c.moveToNext());
		 return out;
	 }
	public DialogFragment newChat = new DialogFragment()
	{
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			View view=getActivity().getLayoutInflater()
	    			.inflate(R.layout.new_group, null);
			
			final EditText nombre=(EditText)view.findViewById(R.id.editText1);
			final EditText descripcion=(EditText)view.findViewById(R.id.editText2);
			    
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
							String n=nombre.getText().toString().trim();
							if(n.isEmpty())
							{
								Toast.makeText(ListChatActivity.this,
										"Nombre no debe estar vacio",
										Toast.LENGTH_LONG).show();
								return;
							}
							chat_new(ListChatActivity.this,
									nombre.getText().toString(),
									descripcion.getText().toString());
						}
					}
				}
			};
			AlertDialog.Builder builder = 
					new AlertDialog.Builder(ListChatActivity.this);
			builder.setTitle("Nuevo Grupo")
			.setIcon(R.drawable.ic_dialogo_nuevo_grupo)
		       .setPositiveButton("Aceptar", dialogListener)
		       .setNegativeButton("Cancelar", dialogListener);
			
		    builder.setView(view);
			return builder.create();
		}
	};
	public  void chat_new(final Activity a,String nombre,String descripcion)
	{ chat_new(a,nombre,descripcion,"");}
	public  void chat_new(final Activity a,String nombre,String descripcion,String u2)
	{
		HashMap<String, String> datos=new HashMap<String, String>();
		datos.put("usuario", User.get("id"));
		if(!u2.isEmpty())
			datos.put("usuario2", u2);
		datos.put("nombre",nombre);
		datos.put("descripcion",descripcion);
		datos.put("tipo", ""+(u2.isEmpty()?GRUPOS:CHATS));
		Server.setDataToSend(datos);
		Asynchtask recep = new Asynchtask() 
		{
			@Override
			public void processFinish(String result) 
			{
				try 
				{
					JSONObject chat_tmp = new JSONObject(result);
					DBChat.insert(chat_tmp);
					add_msj(null,false);
				} catch (JSONException e) 
				{
					Toast.makeText(ListChatActivity.this,"new _Chat: "+result,
						Toast.LENGTH_LONG).show();
				}
			}
		};
		Server.send("chat/add", a, recep);
	}

	@Override
	public void add_msj(JSONObject msj, boolean move) throws JSONException {
		try 
    	{
    		listChat[GRUPOS].load();
    		listChat[CHATS].load();
			throw new JSONException("");
    	}catch (JSONException e){}
	}
	
	protected void onPause()
	{
		super.onPause();
		if(!out)
			stopService(new Intent(this,ChatService.class));
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		out = false;
		updater();
    	try 
    	{
    		listChat[GRUPOS].load();
    		listChat[CHATS].load();
    	}catch (JSONException e){}
	}
	private void updater()    
    {
    	Messenger messenger= new Messenger(new ChatService.MHandler(this));
    	Intent intent = new Intent(this,ChatService.class);
    	intent.putExtra(ChatService.MESSENGER, messenger);
    	intent.putExtra("CHAT", 0);
    	startService(intent);		
    }
	@Override
	public boolean onKeyDown( int arg1, KeyEvent arg2) 
	{
		if(arg1!=KeyEvent.KEYCODE_BACK)
			out=true;
		return super.onKeyDown(arg1,arg2);
	} 
	public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        { 
	        case R.id.informacion:
		    	startActivity(new Intent(this,InformacionActivity.class));
		    	return true;   
			case R.id.salir:
				Alertas.fijar_alarmas(this,true);
            	DB.delete(DBChat.FILE_CHATS);
            	DB.delete(DB.FILE_DB);
            	DBChat.init();
            	DB.set("");
	         	DB.LOGGED=false;
			 case R.id.mi_glider:
				startActivity(new Intent(this,HomeActivity.class));
				finish();
				return true;
        }
		return true;
 }
}
