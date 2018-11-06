package crud;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import models.DB;

import util.Style;
import webservice.Asynchtask;

import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.Notificaciones;
import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;
import com.jhordyabonia.ag.SettingsActivity;

import controllers.Horarios;
import webservice.LOG;

import static com.jhordyabonia.ag.HomeActivity.ASIGNATURAS;
import static com.jhordyabonia.ag.HomeActivity.ON_DISPLAY;

public abstract class Base extends Activity implements Asynchtask {
	public static final int FILE_SELECTED = 756;

	public enum Actions {Add, Edit};

	public static Actions action;
	public static int itemSeleted;
	public static String DATA="com.jhordyabonia.crud.Base.DATA";
	public static String _ITEM_SELECTED="Item_Selected";
	protected ArrayList<JSONObject> LOCAL_DB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//getActionBar().setIcon(R.drawable.ic_atras);
		Style.bar(this);
		setContentView(R.layout.crud);
		findViewById(R.id.save)
			.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						send(v);
					}
				});

		Intent mIntent=getIntent();
		if(mIntent!=null) {
			int noti=mIntent.getIntExtra(Notificaciones.FILE,0);
			if(noti!=0) {
				action=Actions.Edit;
				((NotificationManager)
						getSystemService(Context.NOTIFICATION_SERVICE))
						.cancel(noti);
			}
		}

		int on_display=ON_DISPLAY;
		if(ON_DISPLAY>=100)
			on_display=ASIGNATURAS;

		DB.model(DB.MODELS[on_display]);
		if (ON_DISPLAY== HomeActivity.HORARIOS)
		{
			if(Horarios.ASIGNATURA!=null)
				LOCAL_DB = DB.find("asignatura", Horarios.ASIGNATURA);
			else
				LOCAL_DB = DB.find("dia", Horarios.DIA);
		}else if (ON_DISPLAY!= HomeActivity.ASIGNATURAS)
			LOCAL_DB = DB.find("asignatura", HomeActivity.idAsignaturaActual());
		else
			LOCAL_DB = DB.find("", "");

		DatePicker _fecha= findViewById(R.id.fecha);
		_fecha.setCalendarViewShown(false);

		getActionBar().setHomeButtonEnabled(true);
		fill();
	}
	@Override
	public final boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			case R.id.attach:
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("*/*");
				Intent choose = Intent.createChooser(intent, getString(R.string.select_image));
				startActivityForResult(choose, FILE_SELECTED);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private File createFile(String type)
	{
		// Create an image file name
		File ruta_sd = Environment.getExternalStorageDirectory(); File
			ruta=new File(ruta_sd.getAbsolutePath(),DB.DIRECTORY);

		if(!ruta.exists())
			ruta.mkdir();
		String count= ""+System.currentTimeMillis();
		count=count.substring(count.length()-4);
		String name = DB.User.get("id")+"_"
				+ HomeActivity.idAsignaturaActual()
				+"_"+DB.fecha()+"_"+count+type;

		return new File(ruta,name);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){

		if(resultCode == RESULT_OK && requestCode == Base.FILE_SELECTED && data!=null) {
			Uri selectedImage = data.getData();
			String path=selectedImage.getPath();
			if (path != null) {
				try {
					String type=path.substring(path.lastIndexOf("."),path.length());
					File file=createFile(type);
					String name=file.getName();
					FileOutputStream out = new FileOutputStream(file);
					InputStream in = getContentResolver().openInputStream(
							selectedImage);

					byte[] buffer = new byte[1024];
					int c;

					while ((c = in.read(buffer)) != -1)
						out.write(buffer, 0, c);

					out.flush();
					in.close();
					out.close();
					TextView attach=findViewById(R.id.attach_view);
					attach.setText(attach.getText().toString()+"\n"+name);
					attach.setVisibility(View.VISIBLE);
				} catch (RuntimeException e) {
					e.printStackTrace();
					Toast.makeText(this,"Error",Toast.LENGTH_LONG).show();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
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

		TimePicker _hora = findViewById(R.id.hora);
		_hora.setCurrentHour(Integer.valueOf(hora_[0]));
		_hora.setCurrentMinute(Integer.valueOf(hora_[1]));
	}
	protected String getFecha()
	{
		DatePicker _fecha= findViewById(R.id.fecha);
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
			button.setText(R.string.update);
			show(true);
			return;
		}
		String url_tmp = DB.MODELS[ON_DISPLAY];
		HashMap<String, String> data_tmp = getData();
		if(data_tmp!=null){
			if (button.getText().toString().contains(getString(R.string.update)))
			{
				data_tmp.put("id", getIdItemSeleted());
				url_tmp += "/edit";
			}
			Server.setDataToSend(data_tmp);
			Server.send(url_tmp, this, this);
		}
	}

	@Override
	public void processFinish(String result)
	{
		if(!result.isEmpty())
			if(!result.equals("Sin conexion a internet"))
			{
				JSONObject mData,tmp;
				String msj="",id="";
				try{
					mData= new JSONObject(result);
					msj=mData.getString("menssage");
					tmp=mData.getJSONObject("data");
					id=tmp.getString("id");
					DB.insert(ON_DISPLAY,tmp);
				}catch (JSONException e){LOG.save(e.getMessage(),"error.txt");}

				Toast.makeText(this, msj, Toast.LENGTH_LONG).show();
				Intent intent=new Intent(DATA,Uri.parse("content://result_uri"));
				intent.putExtra(DATA, id);
				setResult(Activity.RESULT_OK,intent);
			}
		if (action == Actions.Add)
			finish(); 
		else if (action == Actions.Edit)
		{
			Button button =  findViewById(R.id.save);
			if (button != null)
				button.setText(R.string.edit);
			show(false);
		}
		HomeActivity.UPDATE=true;
	}

	protected void fill() 
	{
		Button button = findViewById(R.id.save);
		switch (action)
		{
		case Add:
			show(true);
			getActionBar().setTitle(getString(R.string.add) +" "+ HomeActivity.onDisplay(this));
			break;
		case Edit:
			show(false);
			getActionBar().setTitle(getString(R.string.see) +" "+ HomeActivity.onDisplay(this));
			button.setText(R.string.edit);
			break;
		}
		if(DB.COMUNIDAD)
			button.setVisibility(View.GONE);
	}

	public static Intent crud(Context parent, Actions a)
	{
		action = a;
		Intent  intent=null;
		switch (ON_DISPLAY)
		{
		case HomeActivity.ALERTAS:
			intent= (new Intent(parent, AlertaActivity.class));
			break;
		case HomeActivity.APUNTES:
			if(SettingsActivity.Settings.DROP_MODE)
				intent= (new Intent(parent, Main.class));
			else intent= (new Intent(parent, ApunteActivity.class));
			break;
		case HomeActivity.LECTURAS:
			intent= (new Intent(parent, LecturaActivity.class));
			break;
		case HomeActivity.CALIFICABLES:
			intent= (new Intent(parent, CalificableActivity.class));
			break;
		case HomeActivity.ASIGNATURAS:
			intent= (new Intent(parent, AsignaturaActivity.class));
			break;
		case HomeActivity.HORARIOS:
			if(!DB.COMUNIDAD)
				if(DB.Asignaturas.LIST_ASIGNATURAS.length>0)
					intent= (new Intent(parent, HorarioActivity.class));
			break;
		}
		if(parent!=null)
			if(intent!=null)
				parent.startActivity(intent);

		return intent;
	}

	protected abstract HashMap<String, String> getData();
}
