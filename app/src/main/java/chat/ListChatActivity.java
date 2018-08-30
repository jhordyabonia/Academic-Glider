package chat;

import java.util.HashMap;

import models.DB;
import models.DB.User;

import org.json.JSONException;
import org.json.JSONObject;

import webservice.Asynchtask;

import chat.ChatService.Inbox;
import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.InformacionActivity;
import com.jhordyabonia.ag.Notificaciones;
import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import controllers.Alertas;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import static chat.DBChat.ON_CHAT;
import static com.jhordyabonia.ag.HomeActivity.ON_DISPLAY;

public class ListChatActivity extends FragmentActivity implements Inbox, ListChat.ChatMain,View.OnClickListener,
		ActionBar.TabListener {

	private final ListChat.Display listChat[]=new ListChat.Display[3];

	private DialogFragment newChat;
	
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private boolean out = false;
	@Override
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
			listChat[i]=new ListChat.Display(i+ON_CHAT);

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		mViewPager = findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager.setOnPageChangeListener
		(
			new ViewPager.SimpleOnPageChangeListener()
			{
				@Override
				public void onPageSelected(int position) {
					actionBar.setSelectedNavigationItem(position);
					ON_DISPLAY=position+ON_CHAT;
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
			ON_DISPLAY=mIntent.getIntExtra("ON_DISPLAY", HomeActivity.CONTACTOS);

		mViewPager.setCurrentItem(onDisplay(ON_DISPLAY), false);
		newChat = new ChatNewDialog(this);
	}
	public static int onDisplay(int i){
		return i>=ON_CHAT?i-ON_CHAT:i;
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
		ON_DISPLAY=ON_CHAT+tab.getPosition();
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
		{	return listChat[position];	}

		@Override
		public int getCount() 
		{	return listChat.length;	}

		@Override
		public CharSequence getPageTitle(int position) 
		{
			switch (position+ON_CHAT)
			{
				case HomeActivity.CONTACTOS:
					return getString(R.string.contacts);
				case HomeActivity.CHATS:
					return getString(R.string.chats);
				case HomeActivity.GRUPOS:
					return getString(R.string.groups);
			}
			return "";
		}
	}

	public void onClick(View arg0)
	{

		if(ON_DISPLAY== HomeActivity.CHATS)
			setPage(onDisplay(HomeActivity.CONTACTOS),false);
		else if(ON_DISPLAY== HomeActivity.GRUPOS)
			newChat.show(getSupportFragmentManager(), "missiles");
		else
		{
			String msj=getString(R.string.msj_share1) +"\n"+
					getString(R.string.msj_share2) +Server.URL_SERVER;
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT, msj);
			intent.setType("text/plain");
			Intent chooser = Intent.createChooser(intent, getString(R.string.invite));

			if (intent.resolveActivity(getPackageManager()) != null)
				startActivity(chooser);
		}

	}
	public static void chat_new(final Activity a,String nombre,String descripcion)
	{ chat_new(a,nombre,descripcion,"");}
	public static  void chat_new(final Activity a,String nombre,String descripcion,String u2)
	{
		HashMap<String, String> datos=new HashMap<>();
		datos.put("usuario", User.get("id"));
		if(!u2.isEmpty())
			datos.put("usuario2", u2);
		datos.put("nombre",nombre);
		datos.put("descripcion",descripcion);
		datos.put("tipo", ""+(u2.isEmpty()? HomeActivity.GRUPOS-ON_CHAT: HomeActivity.CHATS-ON_CHAT));
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
					//if( a instanceof  Inbox)
					((Inbox)a).add_msj(null,false);
				} catch (JSONException e) 
				{Notificaciones.add("","chat","0",result);}
			}
		};
		Server.send("chat/add", a, recep);
	}

	@Override
	public void add_msj(JSONObject msj, boolean move) throws JSONException {
		try 
    	{
    		listChat[HomeActivity.GRUPOS-ON_CHAT].load();
    		listChat[HomeActivity.CHATS-ON_CHAT].load();
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
    		listChat[HomeActivity.GRUPOS-ON_CHAT].load();
    		listChat[HomeActivity.CHATS-ON_CHAT].load();
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
