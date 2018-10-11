package chat;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import models.DB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import webservice.Asynchtask;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

public class DBChat 
{
	public static final int ON_CHAT=100;
	public static long LOCAL_HOUR = 0;
	public static int LAST_MSJ = 0;
	public static boolean FECHA_WS=true;
	public static String FILE_CHATS = "chats.json";
	public static String FILE_CONTACTS = "contacts.json";
	private static JSONArray chats;
	private static JSONArray contactos;

	static {init();}
	public static void init()
	{ init(null);}
	public static void init(final Activity a)
	{
		if(!DB.LOGGED)
			return;
		try 
		{
			//DB Chat
			String d=DB.load(FILE_CHATS);
			if(d.startsWith("[")&&d.endsWith("]"))
				chats= new JSONArray(d);
			else chats= new JSONArray();
			//DB contacts
			String c=DB.load(DBChat.FILE_CONTACTS);
			if(c.startsWith("[")&&c.endsWith("]"))
				contactos= new JSONArray(c);
			else contactos= new JSONArray();
		} catch (JSONException e) {}
		if(FECHA_WS)
			setFecha(a,"");
	}

	public static JSONArray get() 
	{ return chats;	}
	public static JSONObject  get(int index)
	{	return chats.optJSONObject(index);	}	
	public static void insert(JSONObject chat) throws JSONException 
	{	
		int count = 0;
		int id= chat.getInt("id");
		JSONObject tmp = get(count);
		JSONArray msjs=chat.getJSONArray("mensajes");
		 while (tmp != null)
		{
			if(tmp.getInt("id")==(id))
			{
				if(!tmp.isNull("mensajes"))
					for(int t=0;t<msjs.length();t++)
					{
						JSONObject msj=msjs.getJSONObject(t);	
						if(msj!=null)
							if(msj.getInt("id")>DBChat.LAST_MSJ)
								tmp.getJSONArray("mensajes")						
									.put(msj);
					}
				else tmp.put("mensajes", msjs);
				try 
				{
					tmp.put("descripcion",chat.getString("descripcion"));
					tmp.put("nombre",chat.getString("nombre"));
					tmp.put("fecha",chat.getString("fecha"));
				} catch (JSONException e){}
				return;
			}			
			tmp = get(++count);
		} 	
		chats.put(chat);
	}
	public static String find(String by, Object where_is) 
	{return find(null,by,where_is);}
	public static String find(String get,String by, Object where_is) 
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
						if(get==null)
							return tmp.toString();
						else if(get.equals("index")) return ""+count;
						else return tmp.getString(get);
				} catch (JSONException e){}
				tmp = get(++count);
			}
		} 
		return "";
	}
	
	public static void save()
	{DB.save(null, chats.toString(),FILE_CHATS);}
	public static String get_contact(String get,String by,String where) //throws JSONException
	{
		int count = 0;

		JSONObject tmp = contactos.optJSONObject(count);

		if (!by.isEmpty())
			if (!where.isEmpty())
			{
				while (tmp != null)
				{
					try 
					{
						if (tmp.get(by).equals(where))
							return tmp.getString(get);			
					} catch (JSONException e) {}						
					tmp = contactos.optJSONObject(++count);
				}
			} 
		return "";
	}

	public static void setFecha(final Activity a,String in)
	{
		if(in.isEmpty())
		{
			Asynchtask recep = new Asynchtask() 		
			{
				@Override
				public void processFinish(String result) 
				{
					if(result.contains("-"))
					{
						if(result.contains(":"))					
							setFecha(a,result);
						else if(a!=null)
							Toast.makeText(a, R.string.network_date,
									Toast.LENGTH_LONG).show();
					}					
				}
			};
			Server.send("fecha", a, recep);
		}else
		{
			long local=Calendar.getInstance().getTimeInMillis();
			LOCAL_HOUR=local-fechaInMillis(in);
		}
	}
	private static long fechaInMillis(String in)
	{
		String tmp_fecha[];
		if(in.contains(" "))
			tmp_fecha=in.substring(0,in.indexOf(" ")).split("-");
		else tmp_fecha=(in+" 00:00:00").substring(0,
				in.indexOf(" ")).split("-");

		if(tmp_fecha.length<2)
			tmp_fecha[2]="1";
		if(tmp_fecha.length<1)
			tmp_fecha[1]="1";

		int anno=Integer.valueOf(tmp_fecha[0]);
		int mes=Integer.valueOf(tmp_fecha[1]);
		int dia=Integer.valueOf(tmp_fecha[2]);
		int hora=0,minutos=0,segundos=0;
		String []tmp_hora=in.substring(
						in.indexOf(" ")+1,
						in.length()-1).split(":");
		hora=Integer.valueOf(tmp_hora[0]);
		minutos=Integer.valueOf(tmp_hora[1]);
		segundos=Integer.valueOf(tmp_hora[2]);
		Calendar c=Calendar.getInstance();
		c.set(anno,mes,dia,
				hora,minutos,segundos);
		return c.getTimeInMillis();
	}
	public static String fechaSynchronized(String in)
	{
		Calendar tmp=Calendar.getInstance(Locale.ENGLISH);
		//tmp.setTimeInMillis(fechaInMillis(in)+LOCAL_HOUR);
		
		String mes=""+tmp.get(Calendar.MONTH);
		mes=mes.length()<2?"0"+mes:mes;
		String dia=""+tmp.get(Calendar.DAY_OF_MONTH);
		mes=mes.length()<2?"0"+mes:mes;
		mes=""+(Integer.valueOf(mes)+1);
		String anno=(""+tmp.get(Calendar.YEAR))
				.substring(2);
		mes=mes.length()<2?"0"+mes:mes;
		dia=dia.length()>1?dia:"0"+dia;
		int hora=tmp.get(Calendar.HOUR_OF_DAY);
		int minutos=tmp.get(Calendar.MINUTE);
		int segundos=tmp.get(Calendar.SECOND);
		return anno+"-"+mes+"-"+dia+" "+hora+":"+minutos+":"+segundos;
	}
	public static String fecha(String in)
	{				
		String fecha=fechaSynchronized(in);
		Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
		String mes=""+calendar.get(Calendar.MONTH);
		mes=""+(Integer.valueOf(mes)+1);
		mes=mes.length()<2?"0"+mes:mes;
		String dia=""+calendar.get(Calendar.DAY_OF_MONTH);
		mes=mes.length()<2?"0"+mes:mes;
		String anno=(""+calendar.get(Calendar.YEAR))
				.substring(2);
		mes=mes.length()<2?"0"+mes:mes;
		dia=dia.length()>1?dia:"0"+dia;
		fecha=fecha.substring(0,fecha.lastIndexOf(":"));
		fecha=fecha.replace(anno+"-"+mes+"-"+dia,"");
		fecha=fecha.replace(anno+"-","");
		return fecha;
	}

    public static AsyncTask<String, Void, String> getContactCheker(final Activity activity)
    {
        return new AsyncTask<String, Void, String>()
        {
            @Override
            protected String doInBackground(String... arg0)
            {
                Cursor cursor = getContacts(activity.getBaseContext());
                JSONArray data= new JSONArray();
                if(cursor.moveToFirst())
                    do try
                    {
                        for(String cel:celular(cursor.getString(0),activity).split(","))
                        {
                            String nombre=cursor.getString(1);
                            if(cel.isEmpty()||data.toString().contains(cel))
                                continue;

                            JSONObject _data=new JSONObject();
                            _data.put("nombre", nombre.isEmpty()?cel:nombre);
                            _data.put("cel", cel);

                            data.put(_data);
                        }
                    } catch (JSONException e) {}
                    while(cursor.moveToNext());
                return data.toString();
            }
            protected void onPostExecute(String contacts)
            {
                HashMap<String, String> datos=new HashMap<String, String>();
                datos.put("contactos", contacts);
                Server.setDataToSend(datos);
                Asynchtask recep = new Asynchtask()
                {
                    @Override
                    public void processFinish(String result)
                    {
                        if(!result.isEmpty()&&result.startsWith("[{"))
                             DB.save(activity, result, FILE_CONTACTS);
                    }
                };
                Server.send("contactos", null, recep);
            }
        };
    }

    private static Cursor getContacts(Context c)
	{
		// Run query
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] {
		ContactsContract.Contacts._ID,
		ContactsContract.Contacts.DISPLAY_NAME,
		};
		String selection = "";
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

		return c.getContentResolver().query(
						uri,
						projection,
						selection,
						selectionArgs,
						sortOrder);
	}

    private static String celular(String id, Context context)
     {
         Cursor c = context.getContentResolver().query(
                 ContactsContract.Data.CONTENT_URI,
                 new String[]
                     {
                         ContactsContract.Data._ID,
                         ContactsContract.CommonDataKinds.Phone.NUMBER,
                         ContactsContract.CommonDataKinds.Phone.TYPE,
                         ContactsContract.CommonDataKinds.Phone.LABEL
                     },
                ContactsContract.Data.CONTACT_ID +"=?"+" AND "+ ContactsContract.Data.MIMETYPE +"='"+ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE +"'",
                new String[] { String.valueOf(id) },
                null);
         String out="";
         if(c.moveToFirst())
             do
             {
                 out+=","+c.getString(1);
             }while(c.moveToNext());
         return out;
     }
}
