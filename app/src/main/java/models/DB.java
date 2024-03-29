package models;

import android.app.Activity;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.Notificaciones;
import com.jhordyabonia.ag.Notificaciones.Notifications;
import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import controllers.Alertas;
import webservice.Asynchtask;

import static com.jhordyabonia.ag.HomeActivity.ALERTAS;
import static com.jhordyabonia.ag.HomeActivity.APUNTES;
import static com.jhordyabonia.ag.HomeActivity.ASIGNATURAS;
import static com.jhordyabonia.ag.HomeActivity.LECTURAS;

public abstract class DB
{
	public static boolean COMUNIDAD = false;
	public static boolean LOGGED = false;
	public static String TOKEN = "0000000000";
	public static String DIRECTORY = ".Glider";
	public static String FILE_DB = ".db.json";
	public static File root = Environment.getExternalStorageDirectory();
	public static String MODELS[] = {"alertas","apuntes","lecturas","calificables","horarios","asignaturas"};
	public static int HOY = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
	public static String DAYS[] = { "Domingo", "Lunes", "Martes","Miercoles", "Jueves", "Viernes", "Sabado" };

	private static JSONObject db,comunidad;
	private static String name_model = "";

	public static String[] semana()
	{	
		String days[]=DAYS.clone();
		days[HOY]+=" (Hoy)";
		return days;
	}

	public static int indexDay(String d)
	{
		int index = 0;
		for (String n : DAYS)
		{
			if (n.equals(d))
				return index;
			index++;
		}
		return 0;
	}

	public static String fecha()
	{
		Calendar cal = Calendar.getInstance();
		String mes=""+cal.get(Calendar.MONTH);
		mes=mes.length()<2?"0"+mes:mes;
		String dia=""+cal.get(Calendar.DAY_OF_MONTH);
		mes=mes.length()<2?"0"+mes:mes;
		String anno=(""+cal.get(Calendar.YEAR))
				.substring(2);
		mes=mes.length()<2?"0"+mes:mes;
		dia=dia.length()>1?dia:"0"+dia;
		
		return dia+"-"+mes+"-"+anno;
	}

	public static void update()
	{update(null,new HashMap<String, String>());}

	public static void update(final HomeActivity home)
	{update(home,null);}

	public static void update(final HomeActivity home,HashMap<String, String> datos)
	{
		if(datos==null)
			datos=new HashMap<>();

		String celular=User.get("celular");
		datos.put("celular", celular);
		datos.put("password",User.get("password"));		
		if(!celular.isEmpty())
			TOKEN=celular;
		if(COMUNIDAD)
			datos.put("publico", "1");
		else
			datos.put("publico", "0");
		Server.setDataToSend(datos);
		Asynchtask recep = new Asynchtask() 
		{
			@Override
			public void processFinish(String result) 
			{	
				COMUNIDAD=result.contains("publico\":\"1");
				if(home!=null)
				{
					if(result.isEmpty())
						Toast.makeText(home, R.string.network_err, Toast.LENGTH_SHORT).show();
					else if(result.equals("Datos incorrectos!"))
						Toast.makeText(home, result, Toast.LENGTH_SHORT).show();
					else if(result.contains(TOKEN))
					{
						home.getActionBar().removeAllTabs();
						home.start(result);
						home.make(true);
						Alertas.fijar_alarmas(home);
					}
					else
					{
						Toast.makeText(home, R.string.network_err, Toast.LENGTH_SHORT).show();
						//home.make(false);
					}
				}else
				{
					if(result.contains(TOKEN))
					{
						set(result);	
						Asignaturas.set_list();
					}else return;
				}
			}
		};
		if(COMUNIDAD)
		    Server.send("getAll/no_encrypt", home, recep);
		else if(home!=null){
			set(load(FILE_DB,home));
			Asignaturas.set_list();
			home.make(true);
		}
	}

	public static boolean memory()
	{
		String estado = Environment.getExternalStorageState();
		return estado.equals(Environment.MEDIA_MOUNTED);// &&estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
	}

	public static String load(String file)
	{return  load(file,null);}

	public static String load(String file,Activity a)
	{
		if (!memory())
			return "";

		boolean delete=true;
		String out = "";
		try 
		{
			File ruta = new File(root, DIRECTORY);
			File f = new File(ruta, file);

			BufferedReader fin = new BufferedReader
					(new InputStreamReader(new FileInputStream(f)));
			out = fin.readLine();
			fin.close();
			delete=out.isEmpty();
		} catch (Exception ex) {}
		finally{if(delete)delete(file);}
		return out;
	}

	public static void save(Activity a, String data, String file)
	{
		if (!memory())
			return;
		try 
		{
			File ruta = new File(root, DIRECTORY);
			if (!ruta.exists()){
				ruta.mkdirs();
				(new File(ruta, ".nomedia")).mkdir();
			}
			File f = new File(ruta, file);
			OutputStreamWriter fout = new OutputStreamWriter
					(new FileOutputStream(f));
			fout.write(data);
			fout.close();
		} catch (Exception ex) 
		{
			if(a!=null)
			Toast.makeText(a, "Advertencia, Esta session es temporal",
					Toast.LENGTH_SHORT).show();
		}
	}

	public static void delete(String file)
	{
		if (!memory())
			return;
		try 
		{
			File ruta = new File(root, DIRECTORY);
			File f = new File(ruta, file);
			f.delete();			
		}catch (Exception ex) {}
	}

	public static boolean set(String core_raw)
	{		
		try 
		{
			comunidad=new JSONObject(core_raw);
			if(!COMUNIDAD)
				db = new JSONObject(core_raw);

			LOGGED=true;
		} catch (JSONException e) {LOGGED=false;}
		return LOGGED;
	}

	private static void make() throws JSONException
	{
		try {
			if(db==null)
				return;
			JSONArray r=db.getJSONArray(name_model);
			if(r==null)
				throw new JSONException("Model '"+name_model+"' null");
		}catch (JSONException e){db.put(name_model,new JSONArray());}
	}

	public static void insert(JSONObject data) throws JSONException {

		for (String model : MODELS){
			if (!data.isNull(model)) {
				JSONArray tmp = data.getJSONArray(model);
				if (tmp != null) {
					for (int t = 0; t < tmp.length(); t++)
						insert(model, tmp.getJSONObject(t));
				}
			}
		}
		save(null, db.toString(), FILE_DB);
	}
	public static void insert(int model,JSONObject data) throws JSONException{
		insert(MODELS[model],data);
	}
	public static void insert(String model,JSONObject data) throws JSONException{
		JSONObject tmp= new JSONObject();
		try {
			model(model);
			tmp = getBy("id", data.get("id"));
			if(tmp!=null)
				Log.e("insert: tmp=",tmp.toString());
			else Log.e("inser: tmp=","null");

			if (tmp == null)
				db.getJSONArray(name_model).put(data);
			else
				db.getJSONArray(name_model).put(tmp.getInt("count"), data);

			Asignaturas.set_list();
			save(null, db.toString(), FILE_DB);
		}catch (JSONException e)
		{
			if (tmp != null)
				save(null,tmp.toString(),tmp.getString("nombre"));
			throw e;
		}
	}
    public static void update(Notifications notifications, JSONObject data) throws JSONException{

		JSONArray msjs = data.getJSONArray("mensajes");
		JSONObject msj_t;
        for (int i = 0; i < msjs.length(); i++) {
            msj_t = msjs.getJSONObject(i);

            String tipo=msj_t.getString("tipo");
			try {
				JSONObject data_= new JSONObject(msj_t.getString("dato"));

				if(tipo.contains("permissions")) {
					User.set("permisos",data_);
				}else if(tipo.contains("command")) {
					util.Settings.commands(data_.toString());
				}else if(tipo.contains("file")) {
					save(null,
							data_.getString("dato"),
							data_.getString("nombre"));
				}else if(tipo.contains("notificacion")) {
					Notificaciones.add(data_);
					notifications.update(data_.getString("asignatura"),data_.getString("data"),data_.getString("type"));
				}else {

					Log.e("UPDATE",tipo.toString());
					insert(tipo, data_);

					if(!data_.isNull("nombre"))
						notifications.update(data_.getString("nombre"),tipo,tipo);
					else
						notifications.update(tipo,"Horarios",tipo);
				}
			}catch (JSONException e)
			{save(null,e.getMessage()+"\n>>>\n"+msj_t,tipo+"-"+i+"-error.json");}
        }
        save(null, db.toString(), FILE_DB);
    }
	public static void model(String name)
	{
		name_model = name;
		try{make();}
		catch (JSONException e)
		{save(null,e.getMessage(), name+"-error.txt");}
	}

	private static JSONArray clean(JSONArray data) throws JSONException
	{
		JSONArray out= new JSONArray();

		for(int r=0;r<data.length();r++)
			if(!data.isNull(r))
				out.put(data.get(r));

		return out;
	}

	public static boolean remove(JSONObject in)throws JSONException{
		boolean out= false;
		JSONObject tmp = getBy("id",in.get("id"));
		if(tmp != null) try
		{
				db.getJSONArray(name_model).put( tmp.getInt("count"),null);
				db.put(name_model,clean(db.getJSONArray(name_model)));
				save(null, db.toString(), FILE_DB);
				out = true;
		} catch (JSONException e){}

		return out;
	}

	public static Object get(String name) throws JSONException
	{return COMUNIDAD?comunidad.get(name):db.get(name);}

	public static JSONObject get(int id)
	{
		JSONObject out = null;
		try 
		{
			out = COMUNIDAD?comunidad.getJSONArray(name_model).optJSONObject(id)
						:db.getJSONArray(name_model).optJSONObject(id);
		} catch (JSONException e) {}
		return out;
	}

	public static JSONObject getBy(String by, Object where_is) 
	{
		int count = 0;
		JSONObject tmp = get(count);

		if (!by.isEmpty())
		{
			while (tmp != null)
			{
				try 
				{
					if (tmp.get(by).equals(where_is))
						break;
					tmp = get(++count);
				} catch (JSONException e) 
				{	continue; }
			}
		}
		if(tmp!=null)
			try{tmp.put("count",count);}
			catch (JSONException e ){}
		return tmp;
	}

	public static ArrayList<JSONObject> find(String by, Object where_is)
	{
		ArrayList<JSONObject> out = new ArrayList();

		int count = 0;
		JSONObject tmp = get(count);

		if (!by.isEmpty())
		{
			while (tmp != null)
			{
				try 
				{
					if (tmp.get(by).equals(where_is))
						out.add(tmp);
					tmp = get(++count);
				} catch (JSONException e) 
				{	continue; }
			}
		} else 
		{
			while (tmp != null)
			{
				out.add(tmp);
				tmp = get(++count);
			}
		}

		return out;
	}

	public static class User 
	{		
		public static String get(String key) 
		{
			if(!LOGGED) return "";

			String out = "";
			try 
			{
				out = db.getJSONObject("usuario").getString(key);
			} catch (JSONException e) {}
			return out;
		}
		private static void set(String key,JSONObject remote) throws JSONException
		{
			if(!remote.isNull(key))
				db.getJSONObject("usuario").put(key,remote.get(key));
		}
		public static void set(JSONObject  in) throws JSONException {
			String keys[] = {"nombre", "password", "celular", "correo", "universidad"};
			if(db==null)
				throw new JSONException("Data base local null");
			else for (String key : keys)
				set(key, in);
		}
	}

	public static final class Asignaturas extends DB 
	{
		public static String LIST_ASIGNATURAS[] = {};
		public static String LIST_ID_ASIGNATURAS[] = {};

		public static String getName(String id)
		{
			int index = 0;
			for (String n : LIST_ID_ASIGNATURAS)
			{
				if (n.equals(id))
					return LIST_ASIGNATURAS[index];
				index++;
			}
			return "";
		}

		public static String getId(String name) 
		{
			int index = 0;
			for (String n : LIST_ASIGNATURAS)
			{
				if (n.equals(name))
					return LIST_ID_ASIGNATURAS[index];
				index++;
			}
			return "";
		}

		public static int getIndex(String id)
		{
			int index = 0;
			for (String n : LIST_ID_ASIGNATURAS)
			{
				if (n.equals(id))
					return index;
				index++;
			}
			return 0;
		}

		public static void set_list()
		{
			int count = 0;
			model(MODELS[ASIGNATURAS]);
			ArrayList<JSONObject> tmp = find("", "");
			LIST_ASIGNATURAS = new String[tmp.size()];
			LIST_ID_ASIGNATURAS = new String[tmp.size()];
			for (JSONObject t : tmp) 
			{
				try 
				{
					LIST_ID_ASIGNATURAS[count] = t.getString("id");
					LIST_ASIGNATURAS[count++] = t.getString("nombre");
                    Asignaturas.findDescripcion(t.getString("id"),t.getString("nombre"));
				} catch (JSONException e)
				{	continue; }
			}
		}

		public static void findDescripcion(final String id,String word) {
			DB.model(DB.MODELS[ASIGNATURAS]);
			final ArrayList<JSONObject> tmp = DB.find("id", id);
			if(!tmp.isEmpty())
				try {
					if(!tmp.get(0).getString("descripcion").isEmpty())
						return;

					if(word.isEmpty())
						word = tmp.get(0).getString("nombre");
					final String word_=word;
					final String q = URLEncoder.encode(word_, "UTF-8");
					String url="https://es.wikipedia.org/w/index.php?search="+q;
					Asynchtask recep = new Asynchtask()
					{
						@Override
						public void processFinish(String result){
							String empty="Para más opciones de búsqueda, vea Ayuda:Búsqueda.";
							String descrip=result;
							int ipos=descrip.indexOf("<p");
							int start=descrip.indexOf(">",ipos)+">".length();
							int end=descrip.indexOf("</p>",start);
							descrip=descrip.substring(start,end);
							descrip= Html.fromHtml(descrip.trim()).toString();

							if(descrip.equals(empty)){
								if(word_.contains(" "))
									for(String r:word_.split(" ")){
										if(r.length()>2) {
											findDescripcion(id, r);
											try {
												Thread.sleep(500);
												if(!tmp.get(0).getString("descripcion").isEmpty())
													return;
											}
											catch (InterruptedException e) {}
											catch (JSONException e) {}
										}
									}
							}else {
								try {
									tmp.get(0).put("descripcion", descrip);
									DB.insert(MODELS[ASIGNATURAS],tmp.get(0));
								}catch (JSONException e){}
							}
						}
					};
					Server.send("",url, null, recep);

				} catch (JSONException e) {}
				catch (UnsupportedEncodingException e) {}
		}
	}

	public static final String titulo(String in) 
	{return DB.titulo(in,"Sin calificar",30,true);}

	public static final String titulo(String in,String alt)
	{return DB.titulo(in,alt,30,true);}

	public static final String titulo(String in,String alt,int limite)
	{return DB.titulo(in,alt,limite,true);}

	public static final String titulo(String in,int limite)
	{return DB.titulo(in,"Sin calificar",limite,true);}

	public static final String titulo(String in,String alt,int limite,boolean points)
	{
		if (in.equals("0")||in.isEmpty())
			return alt;
		if(limite==0) return in;
		if (in.length() > limite)
		{
			if (in.charAt(limite) == ' ') 
				return in.substring(0, limite);
			else if(points)
				return in.substring(0, limite-2) + "...";
			else return in.substring(0, limite);
		}return in;
	}
}
