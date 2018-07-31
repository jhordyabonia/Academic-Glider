package util;

import java.io.File;

import com.jhordyabonia.ag.Server;

import models.DB;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;

public class UploadService  extends IntentService
{		  
	public  static final String LIST_TO_UPLOAD = "list_to_upload";

	public UploadService() 
	{super("Cargador de apuntes");}
	
	@Override
	protected void onHandleIntent(Intent intent) 
	{
		String list=intent.getExtras()
				.getCharSequence(LIST_TO_UPLOAD)
				.toString();

		File ruta_sd = Environment.getExternalStorageDirectory();
		File ruta = new File(ruta_sd.getAbsolutePath(), DB.DIRECTORY);
		if (!ruta.exists())
		{
			ruta.mkdir();
			(new File(ruta, ".nomedia")).mkdir();
		}

		for(String file:list.split(","))
		{
			File f = new File(ruta.getAbsolutePath(), file);
			HttpFileUploader.uploadFile(Server.URL_SERVER+"upload",
					f.getAbsolutePath());
		}
		  
	}
}