package util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import models.DB;

import com.jhordyabonia.ag.Server;

import crud.ApunteActivity.Gallery;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;

public class DownLoadImage  extends AsyncTask<String, Void, Bitmap> 
{
    final private  WeakReference<ImageView> imageViewReference;
    final int id_img;
    final Activity activity;
    boolean local=true;
    Gallery callback=null;
    public DownLoadImage(Activity activity,int id) 
    {
    	ImageView imageView=(ImageView)activity.findViewById(id);
    	imageViewReference = new WeakReference<ImageView>(imageView);
    	this.activity=activity;
    	id_img=id;
    }
    public void setCallBack(final Gallery callback)
    {this.callback=callback;}

	public static String save(InputStream data, String file)
	{
		if (!DB.memory())
			return "";
		try 
		{
			File ruta_sd = Environment.getExternalStorageDirectory();
			File ruta = new File(ruta_sd.getAbsolutePath(), DB.DIRECTORY);
			if (!ruta.exists())
			{
				ruta.mkdir();
				(new File(ruta, ".nomedia")).mkdir();
			}
			File f =  new File(ruta,file);
			Bitmap imagen = BitmapFactory.decodeStream(data);
			try                 
			{
                f.createNewFile();
                FileOutputStream ostream = new FileOutputStream(f);
                imagen.compress(CompressFormat.JPEG, 100, ostream);
                ostream.close();
            }catch (Exception e){}
			return f.getAbsolutePath();
		} catch (Exception ex){}
		return "";
	}
    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... params) 
    {
        String []fotos=params;

        Bitmap imagen=null ;
		try 
		{
			//BitmapFactory.Options options = new BitmapFactory.Options();
	        //options.inSampleSize = 2; // el factor de escala a minimizar la imagen, siempre es potencia de 2
	        if(local)
	        {
	    		File ruta_sd = Environment.getExternalStorageDirectory(); 
	    		File ruta = new File(ruta_sd.getAbsolutePath(),DB.DIRECTORY+"//"+fotos[0]);
	    		if(ruta.exists())
	    		{
	    			FileInputStream in= new FileInputStream(ruta);	    		
		    		imagen = BitmapFactory.decodeStream(in);
	    		}else local=false;
	        }
	        if(!local)
	        {
				URL imageUrl = new URL(Server.URL_SERVER+"foto/"+fotos[0]);
				HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
				conn.connect();
				BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
				imagen = BitmapFactory.decodeFile(save(in,fotos[0]));	
	        }
		}catch (IOException e)
		{/*Toast.makeText(activity,"Imagen no disponible", Toast.LENGTH_SHORT).show();*/}
				     
        return imagen;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) 
    {
        if (imageViewReference != null && bitmap != null) 
        {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) 
            	imageView.setImageBitmap(bitmap);   
            if(callback!=null)
            	callback.addItem(bitmap);
        }
    }
}

