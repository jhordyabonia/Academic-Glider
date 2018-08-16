package crud;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import models.DB;

import webservice.Asynchtask;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import controllers.Horarios;

public abstract class Base extends Activity implements Asynchtask {
	public enum Actions {Add, Edit};

	public static Actions action;
	public static int itemSeleted;
	public static String DATA="com.jhordyabonia.crud.Base.DATA";
	protected ArrayList<JSONObject> LOCAL_DB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crud);
		((Button) findViewById(R.id.save))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						send(v);
					}
				});

		DB.model(HomeActivity.onDisplay(this));
		if (HomeActivity.ON_DISPLAY == HomeActivity.HORARIOS)
		{	
			if(Horarios.ASIGNATURA!=null)
				LOCAL_DB = DB.find("asignatura", Horarios.ASIGNATURA);
			else 
				LOCAL_DB = DB.find("dia", Horarios.DIA);
		}else if (HomeActivity.ON_DISPLAY != HomeActivity.ASIGNATURAS)
			LOCAL_DB = DB.find("asignatura", HomeActivity.idAsignaturaActual());
		else
			LOCAL_DB = DB.find("", "");

		DatePicker _fecha= findViewById(R.id.fecha);
		_fecha.setCalendarViewShown(false);
		
		fill();
	}

	private void show(boolean show) 
	{
		setEnabled(R.id.alerta, show);
		setEnabled(R.id.nombre, show);
		setEnabled(R.id.recurrencia, show);
		setEnabled(R.id.descripcion, show);
		setEnabled(R.id.codigo, show);
		setEnabled(R.id.creditos, show);
		setEnabled(R.id.nota, show);
		setEnabled(R.id.fecha, show);
		setEnabled(R.id._fecha, show);
		setEnabled(R.id.porcentaje, show);
		setEnabled(R.id.hora, show);
		setEnabled(R.id._hora, show);
		setEnabled(R.id.ubicacion, show);
		setEnabled(R.id.duracion, show);
		setEnabled(R.id.dia, show);
		setEnabled(R.id.asignatura, show);
	}

	protected void setVisible(int id) 
	{
		View view = findViewById(id);
		if (view != null)
			view.setVisibility(View.VISIBLE);
	}

	protected void setEnabled(int id, boolean v)
	{
		View view = findViewById(id);
		if (view != null)
			view.setEnabled(v);
	}

	protected String set(String value, int id)
	{
	 return set(value,id,"");
	}
	protected String set(String value, int id,String titulo)
	{
		if (action == Actions.Add)
			return "";
		String data = "";
		try 
		{
			data = LOCAL_DB.get(itemSeleted).getString(value);
		} catch (JSONException e) {}
		if (id == 0)
			return data;
		EditText v =  findViewById(id);
		if (v != null)
			v.setText(titulo+data);
		return data;
	}

	private String getIdItemSeleted() 
	{
		String data = "";
		try 
		{
			data = LOCAL_DB.get(itemSeleted).getString("id");
		} catch (JSONException e) {}
		return data;
	}

	protected String getHora()
	{
		TimePicker _hora = findViewById(R.id.hora);
		String hora=_hora.getCurrentHour()+":"+
						_hora.getCurrentMinute();
		return hora;
	}
	
	protected void setHora(String hora)
	{
		if(!hora.contains(":"))
			return;
		
		String hora_[]=hora.split(":");

		TimePicker _hora = (TimePicker)findViewById(R.id.hora);
		_hora.setCurrentHour(Integer.valueOf(hora_[0]));
		_hora.setCurrentMinute(Integer.valueOf(hora_[1]));
	}
	protected String getFecha()
	{
		DatePicker _fecha= (DatePicker)findViewById(R.id.fecha);
		String fecha=_fecha.getDayOfMonth()+"-"+
				(_fecha.getMonth()+1 )+"-"+
				_fecha.getYear();
		return fecha;
	}
	protected void setFecha(String fecha)
	{
		if(!fecha.contains("-"))
			return;
		
		String fecha_[]=fecha.split("-");

		DatePicker _fehca = findViewById(R.id.fecha);
		int dayOfMonth=Integer.valueOf(fecha_[0]);
		int monthOfYear=Integer.valueOf(fecha_[1])-1;
		int year=Integer.valueOf(fecha_[2]);
		_fehca.init(year,monthOfYear,dayOfMonth,null);
	}
	protected String get(int id) 
	{
		return ((EditText) findViewById(id)).getText().toString();
	}

	protected void send(View v) 
	{
		Button button = ((Button) v);
		if (button.getText().toString().contains(getString(R.string.edit)))
		{
			button.setText("Actualizar");
			show(true);
			return;
		}
		HashMap<String, String> data_tmp = getData();
		String url_tmp = HomeActivity.onDisplay(this);
		if (button.getText().toString().contains(getString(R.string.update)))
		{
			data_tmp.put("id", getIdItemSeleted());
			url_tmp += "/edit";
		}
		Server.setDataToSend(data_tmp);
		Server.send(url_tmp, this, this);
	}

	@Override
	public void processFinish(String result)
	{
		if(!result.isEmpty())
			if(!result.equals("Sin conexion a internet"))
			{
				String data[]=result.split("::");
				Toast.makeText(this, data[0], Toast.LENGTH_LONG).show();
				Intent intent=new Intent(DATA,Uri.parse("content://result_uri"));
				intent.putExtra(DATA, data[1]);
				setResult(Activity.RESULT_OK,intent);
			}
		if (action == Actions.Add)
			finish(); 
		else if (action == Actions.Edit)
		{
			Button button = (Button) findViewById(R.id.save);
			if (button != null)
				button.setText("Editar");
			show(false);
		}
		HomeActivity.UPDATE=true;				
	}

	protected void fill() 
	{
		Button button = (Button) findViewById(R.id.save);
		switch (action)
		{
		case Add:
			show(true);
			getActionBar().setTitle(getString(R.string.add) + HomeActivity.onDisplay(this));
			break;
		case Edit:
			show(false);
			getActionBar().setTitle(getString(R.string.see) + HomeActivity.onDisplay(this));
			button.setText("Editar");
			break;
		}
		if(DB.COMUNIDAD)
			button.setVisibility(View.GONE);
	};

	public static void crud(Activity parent, Actions a) 
	{
		action = a;
		switch (HomeActivity.ON_DISPLAY) 
		{
		case HomeActivity.ALERTAS:
			parent.startActivity(new Intent(parent, AlertaActivity.class));
			break;
		case HomeActivity.APUNTES:
			parent.startActivity(new Intent(parent, ApunteActivity.class));
			break;
		case HomeActivity.LECTURAS:
			parent.startActivity(new Intent(parent, LecturaActivity.class));
			break;
		case HomeActivity.CALIFICABLES:
			parent.startActivity(new Intent(parent, CalificableActivity.class));
			break;
		case HomeActivity.HORARIOS:			
			if(!DB.COMUNIDAD)
			  if(DB.Asignaturas.LIST_ASIGNATURAS.length>1)
				parent.startActivity(new Intent(parent, HorarioActivity.class));
			break;
		case HomeActivity.ASIGNATURAS:
			parent.startActivity(new Intent(parent, AsignaturaActivity.class));
			break;
		}
	}

	protected abstract HashMap<String, String> getData();

}
