package webservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import android.os.Environment;

public class LOG 
{
	public static boolean ACTIVE=true;
	public static String DIRECTORY = "Glider";
	public static String rest="";
	public static String history="";
	public static boolean memory() 
	{
		String estado = Environment.getExternalStorageState();
		return estado.equals(Environment.MEDIA_MOUNTED);// &&estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
	}
	public static void save(String data)
	{rest+=data;save(rest,"LOG.json");}
	public static void history(String data)
	{history+=data;save(history,"HISTORY.json");}
	public static void save(String data,String file)
	{		
		if (!memory())
			return;
		try 
		{
			File ruta_sd = Environment.getExternalStorageDirectory();
			File ruta = new File(ruta_sd.getAbsolutePath(), DIRECTORY);
			if (!ruta.exists())
			{
				ruta.mkdir();
				(new File(ruta, ".nomedia")).mkdir();
			}

			File f = new File(ruta.getAbsolutePath(), file);
			OutputStreamWriter fout = new OutputStreamWriter
					(new FileOutputStream(f));
			fout.write(data);
			fout.close();
		} catch (Exception ex) {}
	}


}
