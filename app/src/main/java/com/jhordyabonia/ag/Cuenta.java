package com.jhordyabonia.ag;

import java.util.HashMap;


import models.DB;

import util.Image;
import webservice.Asynchtask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static com.jhordyabonia.ag.HomeActivity.DROP_MODE;

public class Cuenta implements Asynchtask,OnItemSelectedListener
{
	HomeActivity home;
	View lienzo=null;
	Spinner list;
	EditText otra_u;
	boolean logged=false;
	String url="registrar";
	private View img_u;
	public Cuenta(final HomeActivity home)
	{
		HomeActivity.ON_DISPLAY=HomeActivity.CUENTA;
		
		this.home=home;	
		home.getActionBar().hide();
		home.show_menu=false;
		View v=null;
		if(!DROP_MODE)
		{
			home.setContentView(R.layout.activity_registrarme);
			v=home.findViewById(R.id.linearLayout);
		}
		if(v==null) return;

		setLienzo(v);
	}
	public void setLienzo(View l)
	{
		lienzo=l;

		final CheckBox drop=lienzo.findViewById(R.id.drop_mode);
		drop.setOnClickListener(new OnClickListener()
									{
										@Override
										public void onClick(View v)
										{home.setDropMode(drop.isChecked());	}
									}
				                );
		drop.setVisibility(DB.LOGGED?View.VISIBLE:View.GONE);
		drop.setChecked(DROP_MODE);

		lienzo.findViewById(R.id.registrarme)
				.setOnClickListener(new OnClickListener()
									{
										@Override
										public void onClick(View v)
										{registrarme();		}
									}
				);
		otra_u= lienzo.findViewById(R.id.universidad_otra);
		img_u= lienzo.findViewById(R.id.img_universidad2);

		ArrayAdapter<String> base =
				new ArrayAdapter<String>(home,R.layout.base);
		base.add(home.getString(R.string.university));

		list =  lienzo.findViewById(R.id.universidad);
		list.setAdapter(base);
		list.setPrompt(home.getString(R.string.select_university));
		list.setOnItemSelectedListener(this);
		Server.send("universidad", null, this);
	}
	public void fill()
	{
		url="editar";
		home.show_menu=true;
		logged=true;
		
		Toast.makeText(home,home.getString(R.string.insert_2_password),
				Toast.LENGTH_LONG).show();
		
		((Button) lienzo.findViewById(R.id.registrarme))
			.setText(home.getString(R.string.update));

		Image.Loader loader= new Image.Loader((ImageView) lienzo.findViewById(R.id.imagen_usuario));
        //DownLoadImage loader = new DownLoadImage(home,R.id.imagen_usuario);
    	loader.execute(DB.User.get("foto"));

		lienzo.findViewById(R.id.textView1).setVisibility(View.GONE);
		((EditText)lienzo.findViewById(R.id.celular)).setText(DB.User.get("celular"));
		((EditText)lienzo.findViewById(R.id.nombre)).setText(DB.User.get("nombre"));
		((EditText)lienzo.findViewById(R.id.email)).setText(DB.User.get("correo"));

		if(!DROP_MODE) {
			home.getActionBar().removeAllTabs();
			home.getActionBar().hide();
		}
	}	
	@Override
	public void processFinish(String result) 
	{	
		if(result.equals("Actualizacion Exitosa!"))
		{
			Toast.makeText(home, result,Toast.LENGTH_SHORT ).show();
		}else if(result.equals("Registro Exitoso!"))
		{			
			Toast.makeText(home, result,Toast.LENGTH_SHORT ).show();		
			Login.login(home);
			return;
		}else if(result.startsWith("Universidad"))
		{
			ArrayAdapter<String> base =
					new ArrayAdapter<String>(home,R.layout.base);
			if(logged)
			{
				result=result.replace(DB.User.get("universidad")+",", "");
				result=DB.User.get("universidad")+","+result;				
			}
			
			base.addAll(result.split(","));
			list.setAdapter(base);
			list.setPrompt(home.getString(R.string.select_university));
		}else 
		{
			Toast.makeText(home, home.getString(R.string.network_err), Toast.LENGTH_SHORT).show();
		}
	}
	private String getUniversidad()
	{
		String out=(String)list.getSelectedItem();

		otra_u.setVisibility(View.GONE);
		img_u.setVisibility(View.GONE);
		if(out.isEmpty()||out.equals("Universidad")
				||out.equals("Otra"))
			 out=otra_u.getText().toString();
		
		return out;
	}
	private void setError(int id,String msj)
	{
		((EditText)lienzo.findViewById(id))
			.setError(msj);
	}
	private boolean datos()
	{
		String celular=((EditText) lienzo.findViewById(R.id.celular)).getText().toString();
		String password=((EditText)lienzo.findViewById(R.id.password)).getText().toString();
		String password2=((EditText) lienzo.findViewById(R.id.password2)).getText().toString();
		String nombre=((EditText)lienzo.findViewById(R.id.nombre)).getText().toString();
		String email=((EditText)lienzo.findViewById(R.id.email)).getText().toString();
		String universidad=getUniversidad();

		if(nombre.length()<8||!nombre.contains(" "))
		{
			setError(R.id.nombre,home.getString(R.string.insert_complet_name));
			return false;
		}
		if(universidad.isEmpty())
		{
			otra_u.setVisibility(View.VISIBLE);
			img_u.setVisibility(View.VISIBLE);
			setError(R.id.universidad_otra,home.getString(R.string.select_name));
			return false;
		}	
		if(celular.length()<10)
		{
			setError(R.id.celular,home.getString(R.string.cel_may));
			return false;
		}
		if(!email.contains("@")&&email.lastIndexOf(".")!=-1
				&&email.length()-email.lastIndexOf(".")<=4)
		{
			setError(R.id.email,home.getString(R.string.email_format));
			return false;
		}	
		if(password.length()<6)
		{
			setError(R.id.password,home.getString(R.string.pass_err));
			return false;
		}
		if(!password.equals(password2))
		{
			setError(R.id.password2,home.getString(R.string.pass_err2));
			return false;
		}

		HashMap<String, String> datos=new HashMap<String, String>();
		if(url.equals("editar"))
			datos.put("id", DB.User.get("id"));
		datos.put("nombre", nombre);
		datos.put("celular", celular);
		datos.put("correo",email);
		datos.put("password",password);
		datos.put("universidad",universidad);
		
		Server.setDataToSend(datos);
		return true;
	}
	private void registrarme()
	{
		if(!datos())
		{
			Toast.makeText(home,home.getString(R.string.fail_register), Toast.LENGTH_LONG).show();
			return;
		}
		
		Server.send(url, home, this);
	}
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		otra_u.setVisibility(View.GONE);
		img_u.setVisibility(View.GONE);
		if(arg2==arg0.getCount()-1)
		{
			otra_u.setVisibility(View.VISIBLE);	
			img_u.setVisibility(View.VISIBLE);		
		}
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}
}
