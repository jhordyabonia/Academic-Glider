package com.jhordyabonia.ag;

import java.util.HashMap;

import webservice.Asynchtask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class RecuperarCuentaActivity extends Activity implements OnClickListener, Asynchtask
{

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recuperar_cuenta);
		findViewById(R.id.recuperar).setOnClickListener(this);
		getActionBar().hide();
	}

	@Override
	public void processFinish(String result) 
	{
		if(result.isEmpty())
			return;
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();		
	}

	@Override
	public void onClick(View arg0) 
	{
		String email="",cel="";
		EditText cel_=(EditText)
				findViewById(R.id.celular);
		EditText email_=(EditText)
				findViewById(R.id.email);
		email=email_.getText().toString();
		cel=cel_.getText().toString();
		if(email.isEmpty()||cel.isEmpty())
		{
			Toast.makeText(this, "Debes, ingresar ambos campos", Toast.LENGTH_LONG).show();
			return;
		}
		Server.send("recuperar", this, this);
		HashMap<String, String> data=new HashMap<String, String>();
		data.put("email", email);
		data.put("celular", cel);
		Server.setDataToSend(data);		
	}
}
