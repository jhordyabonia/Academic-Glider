package com.jhordyabonia.ag;

import java.util.HashMap;

import models.DB;
import webservice.Asynchtask;
import webservice.LOG;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class Login 
{
	HomeActivity home;
	OnClickListener listener=new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			int id=v.getId();
			switch(id)
			{
				case R.id.entrar:			
					login(home); break;
				case R.id.registrarme_formulario:
					new Cuenta(home); break;
				case R.id.olvide_clave:
					home.startActivity(new Intent (home,RecuperarCuentaActivity.class));
					break;
			}
		}
	};
	public Login(final HomeActivity  home) 
	{
		HomeActivity.ON_DISPLAY=HomeActivity.LOGIN;
		DB.LOGGED=false;
		this.home=home;
		home.show_menu=false;
		home.getActionBar().hide();
		home.getActionBar().removeAllTabs();
		
		home.setContentView(R.layout.activity_login);
		
		home.findViewById(R.id.entrar)
		.setOnClickListener(listener);

		home.findViewById(R.id.olvide_clave)
		.setOnClickListener(listener);
		
		home.findViewById(R.id.registrarme_formulario)
		.setOnClickListener(listener);
		LOG.save("new Login","loging_debug.txt");
	}
	public static void login(final HomeActivity home)
	{
		HashMap<String, String> datos=new HashMap<String, String>();
		final String celular=((EditText) home.findViewById(R.id.celular)).getText().toString();
		String password=((EditText)home.findViewById(R.id.password)).getText().toString();
		if(!celular.isEmpty())
			DB.TOKEN=celular;
		datos.put("celular", celular);
		datos.put("password",password);
		datos.put("publico", "0");
		Server.setDataToSend(datos);
		Asynchtask recep = new Asynchtask() 
		{
			@Override
			public void processFinish(String result) 
			{	
				if(result.isEmpty())
					Toast.makeText(home, home.getString(R.string.network_err), Toast.LENGTH_SHORT).show();
				else if(result.contains(DB.TOKEN))
				{
					HomeActivity.ON_DISPLAY=HomeActivity.ASIGNATURAS;				
					home.make(result,true);
				}
				else if(result.equals("Datos incorrectos!"))
					Toast.makeText(home, result, Toast.LENGTH_SHORT).show();
				else 
					Toast.makeText(home,  home.getString(R.string.network_err), Toast.LENGTH_SHORT).show();
			}
		};
		
		Server.send("getAll", home, recep);
	}
}
