package com.jhordyabonia.ag;

import chat.DBChat;
import util.Buscador;
import models.DB;
import controllers.Alertas;
import controllers.Asignaturas;
import controllers.Horarios;
import crud.Base;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public final class HomeActivity extends FragmentActivity 
{
	public static final int LOGIN= 7;
	public static final int CUENTA = 6;
	public static final int ASIGNATURAS = 5;
	public static final int HORARIOS = 4;
	public static final int CALIFICABLES = 3;
	public static final int LECTURAS = 2;
	public static final int APUNTES = 1;
	public static final int ALERTAS = 0;

	public static int ON_DISPLAY = ASIGNATURAS;
	public static boolean UPDATE=false;

	private static int ASIGNATURA_ACTUAL = 0;
	
	public boolean show_menu=false;
	
	private ActionBar.Tab tabAsignaturas;
	private ActionBar.Tab tabHorario;
	private Horarios horario;
	private Asignaturas asignaturas;
	private ActionBar actionBar;
	private DialogFragment list_dias;
	private DialogFragment list_comunidad;
	
	public static final String idAsignaturaActual()
	{	
		if(ASIGNATURA_ACTUAL>=DB.Asignaturas.LIST_ID_ASIGNATURAS.length)
			ASIGNATURA_ACTUAL=0;
		return DB.Asignaturas.LIST_ID_ASIGNATURAS[ASIGNATURA_ACTUAL];
	}
	public static final String onDisplay() 
	{	return onDisplay(ON_DISPLAY);}
	public static final String onDisplay(int i) 
	{
		String out = "";
		switch (i) 
		{
			case APUNTES:out = "apuntes";break;
			case ALERTAS:out = "alertas";break;
			case LECTURAS:out = "lecturas";break;
			case CALIFICABLES:out = "calificables";break;
			case HORARIOS:out = "horarios";break;
			case ASIGNATURAS:out = "asignaturas";break;
		}
		return out;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);
		tabHorario = actionBar.newTab().setText("Horario")
				.setTabListener(tabListener);
		tabAsignaturas = actionBar.newTab().setText("Asignaturas")
				.setTabListener(tabListener);		

		list_dias = new DialogFragment() 
		{
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState)
			{
				AlertDialog.Builder builder = 
						new AlertDialog.Builder(HomeActivity.this);
				builder.setTitle("Horario")
				.setIcon(android.R.drawable.ic_menu_agenda)
				.setItems(DB.semana(), 
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{	
								Horarios.setDia(which);
								tabHorario.setText(onDisplay(HORARIOS) + "\n> "
										+ DB.DAYS[which]);
								horario.show();
							}
						} );
				return builder.create();
			}
		};
		list_comunidad= new Buscador(this);
		String result= DB.load(DB.FILE_DB);
		if(result.isEmpty())
			new Login(this);
		else	make(result,true);
	}
	@Override
	public void onResume()
	{
		Base.itemSeleted=0;
		super.onResume();
		if(DB.LOGGED==false)
			new Login(this);
		else
		{			
			if(UPDATE)
				DB.update(this);
			UPDATE=false;
		}
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {		
    	if(DB.COMUNIDAD)
      		menu.findItem(R.id.comunidad).setTitle("Mi Glider");
    	else
    		menu.findItem(R.id.comunidad).setTitle("Comunidad Glider");
    	if(ON_DISPLAY==HORARIOS)
    		menu.findItem(R.id.actions_horarios).setVisible(true);
    	else 
    		menu.findItem(R.id.actions_horarios).setVisible(false);
    	
    	if(ON_DISPLAY<HORARIOS)
    		menu.findItem(R.id.chat)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    	else 
			menu.findItem(R.id.chat)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT
						|MenuItem.SHOW_AS_ACTION_ALWAYS);
    	
    	return show_menu;
    }
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	Intent intent;
        switch (item.getItemId()) 
        {           
        	case android.R.id.home:
        		back(KeyEvent.KEYCODE_BACK);
        		return true;
        	case R.id.cuenta:
    			(new Cuenta(this)).fill();
            	return true;
            case R.id.comunidad:
            	DB.COMUNIDAD=!DB.COMUNIDAD;
        		DB.update(this);	
                return true;
            case R.id.contactos:
            	intent=new Intent(this,chat.ListChatActivity.class);
            	intent.putExtra("ON_DISPLAY", chat.ListChatActivity.CONTACTOS);
            	startActivity(intent);
            	return true;
            case R.id.chat:
            	intent=new Intent(this,chat.ListChatActivity.class);
            	intent.putExtra("ON_DISPLAY", chat.ListChatActivity.CHATS);
            	startActivity(intent);
            	return true;
            case R.id.informacion:
            	startActivity(new Intent(this,InformacionActivity.class));
            	return true;
            case R.id.actions_horarios:
            	list_dias.show(getSupportFragmentManager(), "missiles");
            	return true;	
            case R.id.salir:
				Alertas.fijar_alarmas(this,true);
            	DB.delete(DBChat.FILE_CHATS);
            	DB.delete(DB.FILE_DB);
            	DBChat.init();
            	DB.set("");
            	DB.LOGGED=false;
            	new Login(this);
	            return true;
        }
		return true;
    }
	public void make(String result,boolean reMake) 
	{	
		if(result!=null)
		{			
			if(!DB.COMUNIDAD)			
				DB.save(this, result, DB.FILE_DB);

			DB.set(result);
			DB.Asignaturas.set_list();	
		}
		if(reMake)
		{			
			horario = new Horarios(this);
			asignaturas = new Asignaturas(this);
		}else actionBar.removeAllTabs();
		
		show_menu=false;
		actionBar.addTab(tabHorario);
		actionBar.addTab(tabAsignaturas);
		show_menu=true;

		actionBar.setTitle("Mi Glider");
		if(DB.COMUNIDAD)
		{
			actionBar.removeTab(tabHorario);
    		actionBar.setTitle("Comunidad Glider");
    		asignaturas.todas();
		}else if(ON_DISPLAY==ASIGNATURAS)
			actionBar.selectTab(tabAsignaturas);
		else if(ON_DISPLAY==HORARIOS)
			actionBar.selectTab(tabHorario);
		else
		{ 
			actionBar.setTitle(DB.titulo(DB.Asignaturas.LIST_ASIGNATURAS[ASIGNATURA_ACTUAL]));
			asignaturas.show(ON_DISPLAY);
		}	

		actionBar.show(); 
		invalidateOptionsMenu();
	}
	public void abrirAsignatura()
	{
		ASIGNATURA_ACTUAL=Base.itemSeleted;
		asignaturas.show(HomeActivity.ALERTAS);
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(DB.titulo(DB.Asignaturas.LIST_ASIGNATURAS[ASIGNATURA_ACTUAL],34));
	}
	public void horarios_asignatura()
	{
		Horarios.ASIGNATURA=DB.Asignaturas.LIST_ID_ASIGNATURAS[Base.itemSeleted];		
		actionBar.selectTab(tabHorario);
		tabHorario.setText(onDisplay(HORARIOS)+"\n>"
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
				asignaturas.todas();
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
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
				ON_DISPLAY=ASIGNATURAS;
				make(null,false);
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
				actionBar.setTitle("Comunidad Glider");
			else actionBar.setTitle("Mi Glider");
			
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionBar.selectTab(tabAsignaturas);
		}
		return true;

	}
	@Override
	public boolean onKeyDown( int arg1, KeyEvent arg2) 
	{
		if(back(arg1))
			return true;
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