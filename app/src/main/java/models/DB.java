package models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONException;

import webservice.Asynchtask;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import controllers.Alertas;
import webservice.LOG;

import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

public abstract class DB 
{
	public static boolean COMUNIDAD = false;
	public static boolean LOGGED = false;
	public static String LOCAL=null;
	public static String CURRENT=null;
	public static String TOKEN = "0000000000";
	public static String DIRECTORY = "Glider";
	public static String FILE_DB = "db.json";
	public static File root = Environment.getExternalStorageDirectory();
	public static String MODELS[] = {"alertas","apuntes","lecturas","calificables","horarios","asignaturas"};
	public static int HOY = Calendar.getInstance()
            .get(Calendar.DAY_OF_WEEK)-1;
	public static String DAYS[] = 
		{ "Domingo", "Lunes", "Martes","Miercoles", "Jueves", "Viernes", "Sabado" };
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
		String mes=""+Calendar.getInstance().get(Calendar.MONTH);
		mes=mes.length()<2?"0"+mes:mes;
		String dia=""+Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		mes=mes.length()<2?"0"+mes:mes;
		String anno=(""+Calendar.getInstance().get(Calendar.YEAR))
				.substring(2);
		mes=mes.length()<2?"0"+mes:mes;
		dia=dia.length()>1?dia:"0"+dia;
		
		return dia+"-"+mes+"-"+anno;
	}
	public static void update()
	{update(null,null);}
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
						home.make(result,true);
						Alertas.fijar_alarmas(home);
					}
					else
					{
						Toast.makeText(home, R.string.network_err, Toast.LENGTH_SHORT).show();
						home.make(null,false);
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
		Server.send("getAll/no_encrypt", home, recep);
	}
	
	private static JSONObject db;
	private static String name_model = "";

	public static boolean memory()
	{
		String estado = Environment.getExternalStorageState();
		return estado.equals(Environment.MEDIA_MOUNTED);// &&estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
	}

	public static String load(String file) 
	{
		if (!memory())
			return "";

		boolean delete=true;
		String out = "";
		try 
		{
			File ruta = new File(root, DIRECTORY);
			File f = new File(ruta, file);
			if (!f.exists())
				f.mkdir();

			BufferedReader fin = new BufferedReader
					(new InputStreamReader(new FileInputStream(f)));
			out = fin.readLine();
			fin.close();
			delete=false;
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

			File f = new File(ruta, file);
			OutputStreamWriter fout = new OutputStreamWriter
					(new FileOutputStream(f));
			fout.write(data);
			fout.close();
		} catch (Exception ex) 
		{
			if(a!=null)
			Toast.makeText(a, "Advertencia, Esta sesion es temporal",
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
	public static void local()
	{set(LOCAL);}
	public static void local(String local)
	{LOCAL=local;}
	public static void current()
	{set(CURRENT);}
	public static void current(String current)
	{CURRENT=current;}
	public static boolean set(String core_raw)
	{		
		try 
		{
			if(!COMUNIDAD) local(core_raw);
			else current(core_raw);
			db = new JSONObject(core_raw);
			LOGGED=true;
			return true;
		} catch (JSONException e) {LOGGED=false;return false;}
	}

	public static void model(String name)
	{	name_model = name; }
	public static Object get(String name) throws JSONException
	{return db.get(name);}
	public static JSONObject get(int id) 
	{
		JSONObject out = null;
		try 
		{
			out = db.getJSONArray(name_model).optJSONObject(id);
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
						return tmp;
					tmp = get(++count);
				} catch (JSONException e) 
				{	continue; }
			}
		} 
		return tmp;
	}
	public static ArrayList<JSONObject> find(String by, Object where_is) 
	{
		ArrayList<JSONObject> out = new ArrayList<JSONObject>();

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
			model("asignaturas");
			ArrayList<JSONObject> tmp = find("", "");
			LIST_ASIGNATURAS = new String[tmp.size()];
			LIST_ID_ASIGNATURAS = new String[tmp.size()];
			for (JSONObject t : tmp) 
			{
				try 
				{
					LIST_ID_ASIGNATURAS[count] = t.getString("id");
					LIST_ASIGNATURAS[count++] = t.getString("nombre");
				} catch (JSONException e)
				{	continue; }
			}
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
