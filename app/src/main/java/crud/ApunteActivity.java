package crud;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import util.Gallery;
import webservice.Asynchtask;

import models.DB;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;


import crud.Base.Actions;

import static com.jhordyabonia.ag.HomeActivity.APUNTES;

public class ApunteActivity extends FragmentActivity implements Asynchtask 
{
	private static final int CAPTURE_APUNTE = 777;
	public static String ID_ASIGNATURA = "";
	public static boolean externa= false;
	public static boolean fullScream = false,zoom=false;
	private Gallery collection;
	private ViewPager galery;
	private String APUNTE_FOCUS="";
	private boolean adding=false;
	private ArrayList<JSONObject> LOCAL_DB;
	private ArrayList<String> apuntes= new ArrayList<String>();
	private String name;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apunte);

		ID_ASIGNATURA=HomeActivity.idAsignaturaActual();
		if (externa) 
		{
			Spinner list =  findViewById(R.id.asignatura);
			ArrayAdapter<String> base =
					new ArrayAdapter<String>(this,R.layout.base);
			base.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			base.addAll(DB.Asignaturas.LIST_ASIGNATURAS);
			base.remove(getString(R.string.see_subjects));
			list.setAdapter(base);
			list.setPrompt(getString(R.string.select_subject));
			list.setVisibility(View.VISIBLE);
			list.setSelection(DB.Asignaturas
					.getIndex(set("asignatura", 0)) - 1);
		}
		galery =  findViewById(R.id.images);
		collection = new Gallery(getSupportFragmentManager(),this,galery);

		galery.setAdapter(collection);

		galery.setOnPageChangeListener
		(
			new OnPageChangeListener()
			{
				@Override
				public void onPageScrollStateChanged(int arg0) {}
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {}

				@Override
				public void onPageSelected(int arg0) 
				{
					if(arg0==0)return;
					if(!adding)
						APUNTE_FOCUS=apuntes.size()>=arg0?apuntes.get(arg0-1):"";
					else adding=false;
				}
				
			}
		);
		 findViewById(R.id.save)
			.setOnClickListener(listener);
		 findViewById(R.id.add_apunte)
			.setOnClickListener(listener);
		 findViewById(R.id.imageView2)
			.setOnClickListener(listener);

		fill();

	}
	private OnClickListener listener=new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			switch(v.getId())
			{
				case R.id.save:
					send(v);
					break;
				case R.id.add_apunte:
					Toast.makeText(ApunteActivity.this, getString(R.string.deleting_err),
							Toast.LENGTH_LONG).show();			
					break;
				case R.id.imageView2:
					File ruta_sd = Environment.getExternalStorageDirectory(); 
					File ruta=new File(ruta_sd,DB.DIRECTORY);	
					File f=new File(ruta,APUNTE_FOCUS);					
					
					MediaScannerConnection.scanFile(
							ApunteActivity.this, 
							new String[] 
							{ f.toString() }, null, 
							new MediaScannerConnection.OnScanCompletedListener() 
							{ 
								@Override							
								public void onScanCompleted(String path, Uri uri)
								{
									Intent intent = new Intent(Intent.ACTION_VIEW); 
									intent.setDataAndType(uri, "image/*");
									startActivity(intent);
								}
							});	
					break;
			}
		}
	};
	public void fullScream()
	{
		if(!zoom)
		{
			if(fullScream)
			{
				 findViewById(R.id.scrollView1).setVisibility(View.GONE);
				 findViewById(R.id.nombre).setVisibility(View.GONE);
				 findViewById(R.id.save).setVisibility(View.GONE);
				 getActionBar().hide();
				 findViewById(R.id.actions_image).setVisibility(View.VISIBLE);
				 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}else
			{	
				 findViewById(R.id.actions_image).setVisibility(View.GONE);
				 findViewById(R.id.scrollView1).setVisibility(View.VISIBLE);
				 findViewById(R.id.nombre).setVisibility(View.VISIBLE);
				if(!DB.COMUNIDAD)
					 findViewById(R.id.save).setVisibility(View.VISIBLE);
				getActionBar().show();
				getWindow().setFlags(0,WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}
			fullScream=!fullScream;
		}
	}

	//TODO launch camera
	private File createImageFile()
	{
		// Create an image file name
		File ruta_sd = Environment.getExternalStorageDirectory(); File
		ruta=new File(ruta_sd.getAbsolutePath(),DB.DIRECTORY);
		
		if(!ruta.exists()) 
			ruta.mkdir();
		String count= ""+System.currentTimeMillis();
		count=count.substring(count.length()-4);
		name = DB.User.get("id")+"_"
		  +HomeActivity.idAsignaturaActual()
		  +"_"+DB.fecha()+"_"+count+".jpg";		
				
		return new File(ruta,name);
	}
	public  void addImage()
	{		
		if(DB.COMUNIDAD)return;
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createImageFile()));
		if (intent.resolveActivity(getPackageManager()) != null)
			startActivityForResult(intent, CAPTURE_APUNTE);
		else 
			Toast.makeText(this, getString(R.string.camera_err),
						Toast.LENGTH_LONG).show();	
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	    if (requestCode == CAPTURE_APUNTE && resultCode == RESULT_OK) 
    	{
	    	collection.loadItem(name);
	    	adding=true;
	    	apuntes.add(name);
    	}else if(requestCode==777)
    	{
    		Intent intent = 
    				new Intent(Intent.ACTION_VIEW,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//,location);
			intent.setData(Uri.parse(data.getData().getPath())); 
			intent.setType("image/*");
			startActivity(intent);
    	}
	}
	protected void setEnabled(int id, boolean v)
	{
		View view = findViewById(id);
		if (view != null)
			view.setEnabled(v);
	}
	private void show(boolean show) 
	{
		setEnabled(R.id.nombre, show);
		setEnabled(R.id.descripcion, show);
	}
	private String getIdItemSeleted() 
	{
		String data = "";
		try 
		{
			data = LOCAL_DB.get(Base.itemSeleted).getString("id");
		} catch (JSONException e) {}
		return data;
	}
	private HashMap<String, String> getData() 
	{
		HashMap<String, String> datos = new HashMap<String, String>();
		
		String _apuntes="";
		for(String apunte:apuntes)
		{
			apunte=apunte.trim();
			if(!apunte.isEmpty()
					&&apunte.contains(".jpg"))
				_apuntes+=apunte+",";
		}
		if(_apuntes.endsWith(","))
			_apuntes=_apuntes.substring(0, _apuntes.length()-1);
		if(externa)
		{
			String asignatura= (String) ((Spinner) findViewById(R.id.asignatura))
								.getSelectedItem();
			ID_ASIGNATURA=DB.Asignaturas.getId(asignatura);
		}
		datos.put("asignatura", ID_ASIGNATURA);
		datos.put("nombre", get(R.id.nombre));
		datos.put("descripcion", get(R.id.descripcion));
		datos.put("apunte", _apuntes);
		datos.put("fecha", DB.fecha());
		return datos;
	}
	private String get(int id) 
	{
		return ((EditText) findViewById(id)).getText().toString();
	}
	private void send(View v) 
	{
		Button button = ((Button) v);
		if (button.getText().toString().contains(getString(R.string.edit)))
		{
			button.setText(getString(R.string.update));
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
	private String set(String value, int id)
	{
		if (Base.action == Actions.Add)
			return "";
		String data = "";
		try 
		{
			data = LOCAL_DB.get(Base.itemSeleted).getString(value);
		} catch (JSONException e) {}
		if (id == 0)
			return data;
		EditText v = findViewById(id);
		if (v != null)
			v.setText(data);
		return data;
	}
	protected void fill() 
	{
		switch (Base.action)
		{
		case Add:
			show(true);
			getActionBar().setTitle(getString(R.string.add) + HomeActivity.onDisplay(this));
			break;
		case Edit:
			show(false);
			getActionBar().setTitle(getString(R.string.see) + HomeActivity.onDisplay(this));
			Button button = findViewById(R.id.save);
			if(DB.COMUNIDAD)
				button.setVisibility(View.GONE);
			else button.setText(getString(R.string.edit));
			break;
		}
		DB.model(DB.MODELS[APUNTES]);
		LOCAL_DB = DB.find("asignatura", HomeActivity.idAsignaturaActual());
		set("nombre", R.id.nombre);
		set("descripcion", R.id.descripcion);
		
		String fotos[] = set("apunte",0).split(",");
		for(String s:fotos)
			if(!s.isEmpty())
			{
				collection.loadItem(s);
				adding=true;
				apuntes.add(s);
			}
	};
	@Override
	public void processFinish(String result)
	{
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
		if (Base.action == Actions.Add)
			finish(); 
		else if (Base.action == Actions.Edit)
		{
			Button button =  findViewById(R.id.save);
			if (button != null)
				button.setText(getString(R.string.edit));
			show(false);
		}
		HomeActivity.UPDATE=true;
	}
	@Override
	public boolean onKeyDown( int arg1, KeyEvent arg2) 
	{
		if(arg1==KeyEvent.KEYCODE_BACK)
			if(!fullScream)
			{
				fullScream();
				return true;
			}
		return super.onKeyDown(arg1,arg2);
	}
}
