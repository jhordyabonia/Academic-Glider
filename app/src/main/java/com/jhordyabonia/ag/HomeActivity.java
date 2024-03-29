package com.jhordyabonia.ag;

import android.Manifest;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

import chat.ChatService;
import chat.DBChat;
import chat.ListChat;
import chat.Push;
import controllers.Asignaturas;
import controllers.AsignaturasView;
import controllers.Horarios;
import crud.Base;
import models.DB;
import util.Buscador;
import util.ListDias;
import util.NavigationDrawerFragment;
import util.Style;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static chat.DBChat.ON_CHAT;
import static com.jhordyabonia.ag.PlaceholderFragment.newInstance;
import static util.Settings.DROP_MODE;
import static util.Settings._DROP_MODE;


public class HomeActivity extends FragmentActivity
		implements NavigationDrawerFragment.NavigationDrawerCallbacks, ListChat.ChatMain, ChatService.Inbox {

    private static final int PERMISSIONS_REQUEST = 7;
    private NavigationDrawerFragment mNavigationDrawerFragment=null;

    private FirebaseAnalytics mFirebaseAnalytics;
    public static final int CONTACTOS = ON_CHAT+2;
    public static final int CHATS = ON_CHAT+1;
    public static final int GRUPOS = ON_CHAT;
	public static final int NOTIFICATION = 8;
	public static final int LOGIN= 7;
	public static final int CUENTA = 6;
	public static final int ASIGNATURAS = 5;
	public static final int HORARIOS = 4;
	public static final int CALIFICABLES = 3;
	public static final int LECTURAS = 2;
	public static final int APUNTES = 1;
	public static final int ALERTAS = 0;

	public static int ON_DISPLAY = ASIGNATURAS;
	public static boolean UPDATE = false;

	public static int ASIGNATURA_ACTUAL = 0;
	
	public boolean show_menu=false;
	
	private ActionBar.Tab tabAsignaturas;
	private ActionBar.Tab tabHorario;
	public/*private*/ Horarios horario;
	public/*private*/ Asignaturas asignaturas;
	public/*private*/ ActionBar actionBar;
	public/*private*/ DialogFragment list_dias;
	public/*private*/ DialogFragment list_comunidad;
	public static HomeActivity HOME=null;
	
	public static final String idAsignaturaActual()
	{
		if(DB.Asignaturas.LIST_ID_ASIGNATURAS.length==0)return "";
		if(ASIGNATURA_ACTUAL>=DB.Asignaturas.LIST_ID_ASIGNATURAS.length)
			ASIGNATURA_ACTUAL=0;
		return DB.Asignaturas.LIST_ID_ASIGNATURAS[ASIGNATURA_ACTUAL];
	}
	public static final String onDisplay(Context c)
	{	return onDisplay(ON_DISPLAY,c);}

	//@SuppressLint("NewApi")
	public static final String onDisplay(int i,Context c)
	{
		String out = "";
		switch (i)
		{
			case APUNTES:out = c.getString(R.string.apuntes); break;
			case ALERTAS:out = c.getString(R.string.alertas); break;
			case LECTURAS:out = c.getString(R.string.lecturas); break;
			case CALIFICABLES:out = c.getString(R.string.calificables); break;
			case HORARIOS:out = c.getString(R.string.horarios); break;
			case ASIGNATURAS:out = c.getString(R.string.asignaturas); break;
		}
		return out;
	}
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments

		boolean community=position==6||position==66;

		if(position==2&&DB.COMUNIDAD)
		{
			DB.COMUNIDAD=false;
			position=6;
		}

		if(community)
			DB.COMUNIDAD=community;

		FragmentManager fragmentManager = getFragmentManager();
		try {
			fragmentManager.beginTransaction()
					.replace(R.id.container, newInstance(position))
					.commit();
		}catch (java.lang.IllegalStateException e){
			Toast.makeText(this,R.string.haveError,Toast.LENGTH_SHORT).show();
			if(position==4){

			}
			Log.e("ESTATE ERROR",""+position);
		}
	}
	/*/new navegation
	private void setDropMode() {
		//if(!DB.LOGGED)
		//	DROP_MODE=false;

		if(mNavigationDrawerFragment!=null)
			mNavigationDrawerFragment.clearHistory();
	}*/
	public void dropMode()
	{
		if(!DB.LOGGED)
			DROP_MODE=false;

		if(DROP_MODE) {
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			setContentView(R.layout.activity_main);
			mNavigationDrawerFragment = (NavigationDrawerFragment)
					getFragmentManager().findFragmentById(R.id.navigation_drawer);

			// Set up the drawer.
			mNavigationDrawerFragment.setUp(
					R.id.navigation_drawer,
					(DrawerLayout) findViewById(R.id.drawer_layout));
			mNavigationDrawerFragment.selectItem(2);

			DBChat.init();
		}else actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	}
	private void setMyPermissions()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED
				||ContextCompat.checkSelfPermission(this,
				Manifest.permission.VIBRATE)
				!= PackageManager.PERMISSION_GRANTED
				||ContextCompat.checkSelfPermission(this,
				Manifest.permission.SET_ALARM)
				!= PackageManager.PERMISSION_GRANTED
				||ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_PHONE_STATE)
				!= PackageManager.PERMISSION_GRANTED) {


                ActivityCompat.requestPermissions(this,
                        new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
								Manifest.permission.READ_CONTACTS,
								Manifest.permission.SET_ALARM,
								Manifest.permission.READ_PHONE_STATE,
								Manifest.permission.VIBRATE},
                        PERMISSIONS_REQUEST);

        }
    }
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setMyPermissions();
        if(ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			DB.root = getCacheDir();
			Toast.makeText(this,R.string.session_tmp,Toast.LENGTH_LONG).show();
		}
		SharedPreferences sp = getDefaultSharedPreferences(this);
		Style.STYLE = sp.getInt(Style._STYLE,R.color.colorMarine);

		start(DB.load(DB.FILE_DB,this));
		actionBar=getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		tabHorario = actionBar.newTab().setText(onDisplay(HORARIOS, this))
					.setTabListener(tabListener);
		tabAsignaturas = actionBar.newTab().setText(onDisplay(ASIGNATURAS, this))
					.setTabListener(tabListener);

		list_dias = new ListDias(this);
		list_comunidad= new Buscador(this);

		horario = new Horarios(this);
		asignaturas = new Asignaturas(this);

		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
		Bundle bundle = new Bundle();
		bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "0.1");
		bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "HomeActivity");
		bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
		mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        Push.subscribe(this);
	}

	public void show_dias(int which)
	{
		Horarios.setDia(which);
		if(!DROP_MODE)
			tabHorario.setText(onDisplay(HORARIOS,this) + "\n> "+ DB.DAYS[which]);

		View view=findViewById(R.id.FrameLayout1);
		horario.show(view);
	}
	public  void start(String result)
	{
		DB.set(result);
		if(!DB.LOGGED)
			new Login(this);
		else
		{
			if(!DB.COMUNIDAD)
				DB.save(this, result, DB.FILE_DB);

			DB.Asignaturas.set_list();
		}
	}
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		SharedPreferences sp = getDefaultSharedPreferences(this);
		DROP_MODE=  sp.getBoolean(_DROP_MODE, true);
		dropMode();
		make(true);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Style.bar(this);
		Base.itemSeleted=0;
		if(ON_DISPLAY<ASIGNATURAS)
			ON_DISPLAY=ASIGNATURAS;

		if(!DB.LOGGED) {
			new Login(this);
		}else{
			Style.set(findViewById(R.id.list));
			//Style.set(findViewById(R.id.paginator));
			Style.set(findViewById(R.id.FrameLayout1));
			if(UPDATE) {
				DB.update(this);
			}
			UPDATE=false;
			if(DROP_MODE) {
				ChatService.updater(this,0);
			}
		}
		HOME=this;
	}

	@Override
	protected void onDestroy(){
		HOME=null;
		super.onDestroy();
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    { getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
		if(DB.COMUNIDAD)
      		menu.findItem(R.id.comunidad).setTitle(getString(R.string.myglider));
    	else
    		menu.findItem(R.id.comunidad).setTitle(getString(R.string.community));

    	if(ON_DISPLAY==HORARIOS)
    		menu.findItem(R.id.actions_horarios).setVisible(true);
    	else
    		menu.findItem(R.id.actions_horarios).setVisible(false);

    	if(ON_DISPLAY<HORARIOS)
    		menu.findItem(R.id.chat)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    	else
			menu.findItem(R.id.chat)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT
						|MenuItem.SHOW_AS_ACTION_ALWAYS);

		if(ON_DISPLAY==NOTIFICATION)
			menu.findItem(R.id.actions_notifications).setVisible(false);
		else
			menu.findItem(R.id.actions_notifications).setVisible(true);

		if(ON_DISPLAY==GRUPOS)
			menu.findItem(R.id.chat).setVisible(false);
		else
			menu.findItem(R.id.chat).setVisible(true);

    	if(DROP_MODE){

			menu.findItem(R.id.cuenta).setVisible(false);
			menu.findItem(R.id.comunidad).setVisible(false);
			menu.findItem(R.id.contactos).setVisible(false);
			menu.findItem(R.id.informacion).setVisible(false);
			menu.findItem(R.id.salir).setVisible(false);
			menu.findItem(R.id.settings ).setVisible(false);
		}

    	return show_menu;
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
		Intent intent;
        switch (item.getItemId())
        {
        	case android.R.id.home:
        		if(!DROP_MODE)
        			back(KeyEvent.KEYCODE_BACK);
        		else mNavigationDrawerFragment.open();
        		return true;
			case R.id.actions_notifications:
				if(!DROP_MODE)
					startActivity(new Intent(this,NotificacionesActivity.class));
				else mNavigationDrawerFragment.selectItem(0);
				return true;
			case R.id.cuenta:
				if(!DROP_MODE){
					(new Cuenta(this)).fill();
				}else mNavigationDrawerFragment.selectItem(7);
				return true;
            case R.id.comunidad:
            	DB.COMUNIDAD=!DB.COMUNIDAD;
        		DB.update(this);
                return true;
            case R.id.contactos:
            	intent=new Intent(this,chat.ListChatActivity.class);
            	intent.putExtra("ON_DISPLAY", CONTACTOS);
            	startActivity(intent);
            	return true;
            case R.id.chat:
				if(!DROP_MODE){
					intent=new Intent(this,chat.ListChatActivity.class);
					intent.putExtra("ON_DISPLAY", GRUPOS);
					startActivity(intent);
				}else mNavigationDrawerFragment.selectItem(5);
            	return true;
            case R.id.informacion:
				if(!DROP_MODE){
						startActivity(new Intent(this,InformacionActivity.class));
				}else mNavigationDrawerFragment.selectItem(8);
            	return true;
			case R.id.settings:
				if(!DROP_MODE){
					startActivity(new Intent(this, SettingsActivity.class));
				}else mNavigationDrawerFragment.selectItem(9);
				return true;
            case R.id.actions_horarios:
            	list_dias.show(getSupportFragmentManager(), "missiles");
            	return true;
            case R.id.salir:
				Login.logout(this);
	            return true;
        }
		return true;
    }
    @Override
    public void setPage(int i,boolean b)
    {
    	if(mNavigationDrawerFragment!=null)
    		mNavigationDrawerFragment.selectItem(classicToDrop_mode(i));
    }
	@Override
	public void add_msj(JSONObject msj, boolean move) {
		if(HOME!=null)
		switch(ON_DISPLAY) {
			case GRUPOS:
				mNavigationDrawerFragment.selectItem(5);
				break;
			case CHATS:
				mNavigationDrawerFragment.selectItem(4);
				break;
		}
	}

	public void make(boolean reMake)
	{
		if(reMake)
		{
			horario = new Horarios(this);
			asignaturas = new Asignaturas(this);
		}else actionBar.removeAllTabs();

		show_menu=false;
		actionBar.addTab(tabHorario);
		actionBar.addTab(tabAsignaturas);
		show_menu=true;

		actionBar.setTitle(getString(R.string.myglider));
		if(DB.COMUNIDAD&&!DROP_MODE)
		{
			actionBar.removeTab(tabHorario);
    		actionBar.setTitle(R.string.community);
			setContentView(R.layout.lienzo);
			View view=findViewById(R.id.FrameLayout1);
    		asignaturas.todas(view);
		}else if(ON_DISPLAY==ASIGNATURAS&&!DROP_MODE)
			actionBar.selectTab(tabAsignaturas);
		else if(ON_DISPLAY==HORARIOS&&!DROP_MODE)
			actionBar.selectTab(tabHorario);
		else
		{
//			if(DB.Asignaturas.LIST_ASIGNATURAS.length>ASIGNATURA_ACTUAL)
//				actionBar.setTitle(DB.titulo(DB.Asignaturas.LIST_ASIGNATURAS[ASIGNATURA_ACTUAL]));

			if(DROP_MODE) {
				if(mNavigationDrawerFragment==null)
					dropMode();
				else mNavigationDrawerFragment.selectItem(classicToDrop_mode(ON_DISPLAY));
			}
		}
		actionBar.show();
		invalidateOptionsMenu();
	}
	public int classicToDrop_mode(int i){
		int out;
		switch (i) {
			case ASIGNATURAS:
				out = DB.COMUNIDAD ? 66 : 2;
				break;
			case GRUPOS:
				out = 5;
				break;
			case CHATS:
				out = 4;
				break;
			case CONTACTOS:
				out = 3;
				break;
			case HORARIOS:
				out = 1;
				break;
			case NOTIFICATION:
				out=0;
				break;
			default:
				out = -1;
		}

		return out;
	}
	public void setAsignaturaActual(int i){ASIGNATURA_ACTUAL=i;}
	public void abrirAsignatura()
	{
		setAsignaturaActual(Base.itemSeleted);
		Intent intent = new Intent(this, AsignaturasView.class);
		intent.putExtra(Base._ITEM_SELECTED,Base.itemSeleted);
		startActivity(intent);
	}
	public void horarios_asignatura()
	{
		Horarios.ASIGNATURA=DB.Asignaturas.LIST_ID_ASIGNATURAS[Base.itemSeleted];

		if(DROP_MODE)
		{
			mNavigationDrawerFragment.selectItem(1);
			return;
		}
		actionBar.selectTab(tabHorario);
		tabHorario.setText(onDisplay(HORARIOS,this)+"\n>"
				+DB.titulo(DB.Asignaturas.LIST_ASIGNATURAS[Base.itemSeleted],11));
	}
	public void buscar()
	{list_comunidad.show(getSupportFragmentManager(), "missiles");}
	private ActionBar.TabListener tabListener = new ActionBar.TabListener()
	{
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft)
		{
			 if(tab.equals(tabHorario))
				list_dias.show(getSupportFragmentManager(), "missiles");
		}
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft){}
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft)
		{
			if(show_menu)
			if (tab.equals(tabHorario))
			{
				ON_DISPLAY = HORARIOS;
				horario.show();
			}else
			{
				ON_DISPLAY = ASIGNATURAS;
				setContentView(R.layout.lienzo);
				View view=findViewById(R.id.FrameLayout1);
				asignaturas.todas(view);
			}
			invalidateOptionsMenu();
		}
	};
	private boolean back(int arg1)
	{
		if(arg1!=KeyEvent.KEYCODE_BACK)
			return false;

		if(ON_DISPLAY==CUENTA)
		{
			if(!DB.LOGGED)
				new Login(this);
			else
			{
				if(!DROP_MODE) {
					actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
					ON_DISPLAY = ASIGNATURAS;
					make( false);
				}else {
					//mNavigationDrawerFragment.selectItem(2);
					//actionBar.show();
				}
			}
		}else if(DB.COMUNIDAD&&ON_DISPLAY==ASIGNATURAS)
		{
			DB.COMUNIDAD=false;
			DB.update(this);
		}else if(ON_DISPLAY==ASIGNATURAS||ON_DISPLAY==HORARIOS
				||ON_DISPLAY==LOGIN)
			return false;
		else
		{
			if(DB.COMUNIDAD)
				actionBar.setTitle(getString(R.string.community));
			else actionBar.setTitle(getString(R.string.myglider));

			if(DROP_MODE) {
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

			}else{
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
				actionBar.selectTab(tabAsignaturas);
			}
		}
		return true;
	}

	@Override
	public boolean onKeyDown( int arg1, KeyEvent arg2)
	{
		if(DROP_MODE)
        {
        	if(mNavigationDrawerFragment==null)
        		dropMode();

        	if(DB.LOGGED) {
				if (arg1 == KeyEvent.KEYCODE_BACK){
					if(mNavigationDrawerFragment.current()==-1) {
						mNavigationDrawerFragment.previews();
						return true;
					}else if (ON_DISPLAY!=NOTIFICATION) {
						mNavigationDrawerFragment.selectItem(0);
						return true;
					}

					return super.onKeyDown(arg1,arg2);
				}
			}
        }else  if(back(arg1)) return true;

		return super.onKeyDown(arg1,arg2);
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
}