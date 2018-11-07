package com.jhordyabonia.ag;

import java.util.HashMap;


import models.DB;

import util.Image;
import webservice.Asynchtask;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static util.Settings.DROP_MODE;

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
		Toast.makeText(home,"Run...",Toast.LENGTH_LONG).show();
	}
	public void setLienzo(View l)
	{
		lienzo=l;

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

		ArrayAdapter<String>  base = new ArrayAdapter(home,R.layout.base);
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
		
		//Toast.makeText(home,R.string.insert_2_password,Toast.LENGTH_LONG).show();
		
		((Button) lienzo.findViewById(R.id.registrarme))
			.setText(R.string.update);

		Image.Loader loader= new Image.Loader((ImageView) lienzo.findViewById(R.id.imagen_usuario));
        //DownLoadImage loader = new DownLoadImage(home,R.id.imagen_usuario);
    	loader.execute(DB.User.get("foto"));

		lienzo.findViewById(R.id.textView1).setVisibility(View.INVISIBLE);
		((EditText)lienzo.findViewById(R.id.celular)).setText(DB.User.get("celular"));
		((EditText)lienzo.findViewById(R.id.nombre)).setText(DB.User.get("nombre"));
		((EditText)lienzo.findViewById(R.id.email)).setText(DB.User.get("correo"));
		//((Spinner)lienzo.findViewById(R.id.universidad)).setText
		if(!DROP_MODE) {
			home.getActionBar().removeAllTabs();
			home.getActionBar().hide();
		}
		starter();
	}	
	@Override
	public void processFinish(String result) 
	{
		if(result.startsWith("Universidad")){
			if(logged){
				result=result.replace(DB.User.get("universidad")+",", "");
				result=DB.User.get("universidad")+","+result;
			}

			ArrayAdapter<String>  base=new ArrayAdapter(home,R.layout.base);
			base.addAll(result.split(","));
			list.setAdapter(base);
			list.setPrompt(home.getString(R.string.select_university));
			return;
		}else {
			String msj = "";
			try {
				JSONObject data = new JSONObject(result);
				msj = data.getString("menssage");
				DB.User.set(data.getJSONObject("data"));
				Toast.makeText(home, msj, Toast.LENGTH_SHORT).show();
			} catch (JSONException e) {
			}

			if (msj.equals("Registro Exitoso!")) {
				Toast.makeText(home, msj, Toast.LENGTH_SHORT).show();
				Login.login(home);
				return;
			}
		}
		Toast.makeText(home, R.string.network_err, Toast.LENGTH_SHORT).show();
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

		if(nombre.length()<8||!nombre.contains(" ")){
			setError(R.id.nombre,home.getString(R.string.insert_complet_name));
			return false;
		}
		if(universidad.isEmpty()){
			otra_u.setVisibility(View.VISIBLE);
			img_u.setVisibility(View.VISIBLE);
			setError(R.id.universidad_otra,home.getString(R.string.select_name));
			return false;
		}
		if(celular.length()<10){
			setError(R.id.celular,home.getString(R.string.cel_may));
			return false;
		}
		if(!email.contains("@")&&email.lastIndexOf(".")!=-1
				&&email.length()-email.lastIndexOf(".")<=4)
		{
			setError(R.id.email,home.getString(R.string.email_format));
			return false;
		}	
		if(password.length()<6){
			setError(R.id.password,home.getString(R.string.pass_err));
			return false;
		}
		if(!password.equals(password2)){
			setError(R.id.password2,home.getString(R.string.pass_err2));
			return false;
		}

		HashMap<String, String> datos=new HashMap();
		if(url.equals("editar"))
			datos.put("id",DB.User.get("id"));
		datos.put("nombre",nombre);
		datos.put("celular",celular);
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
			Toast.makeText(home,R.string.fail_register, Toast.LENGTH_LONG).show();
			return;
		}
		
		Server.send(url, home, this);
	}
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
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
	private void starter()
	{
		String nameU=DB.User.get("universidad").replace(" ","_")+".jpg";
		Image.Loader loader= new Image.Loader(lienzo.findViewById(R.id.bg_subimage));
		loader.setContext(home);
		loader.execute(nameU);

		Image.Loader loader2= new Image.Loader(lienzo.findViewById(R.id.bg_image));
		loader2.execute(nameU);
		View bg=lienzo.findViewById(R.id.bg);
		//bg.setBackgroundResource(R.drawable.bg_0);
		ObjectAnimator in0 = ObjectAnimator
				.ofFloat(bg,"scaleX",1.5f,1.5f,1.7f,1.9f,2,2,2,2,2,1.9f,1.7f,1.5f,1.3f,1);
		in0.setDuration(30000);
		in0.setInterpolator(new LinearInterpolator());
		in0.start();
		ObjectAnimator in1 = ObjectAnimator
				.ofFloat(bg,"scaleY",1.5f,1.5f,1.7f,1.9f,2,2,2,2,2,1.9f,1.7f,1.5f,1.3f,1);
		in1.setDuration(30000);
		in1.setInterpolator(new LinearInterpolator());
		in1.start();
		ObjectAnimator in3 = ObjectAnimator
				.ofFloat(bg,"translationY",0,-30,-60,-90,-100,-100,-100,-100,-100,-90,-60,-30,0);
		in3.setDuration(30000);
		in3.setInterpolator(new LinearInterpolator());
		in3.start();
		ObjectAnimator in4 = ObjectAnimator
				.ofFloat(bg,"translationX",0,0,0,0,-50,-150,-200,-250,-250,-250,-190,-100,30,0);
		in4.setDuration(30000);
		in4.setInterpolator(new LinearInterpolator());
		in4.start();
		in4.addListener( new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationStart(Animator anim)
			{  starter();}
		});
	}
}
