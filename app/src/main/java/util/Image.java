package util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import crud.ApunteActivity;
import static crud.ApunteActivity.zoom;
import models.DB;

public class Image extends Fragment {
    private boolean camera;
    private View root;
    private ApunteActivity base;
    private Loader loader;

    public Image(boolean camera, ApunteActivity b) {
        base = b;
        zoom = false;
        this.camera = camera;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.image, container, false);

        Bundle args = getArguments();
        ImageView image = root.findViewById(R.id.image);
        ImageView imageFull = root.findViewById(R.id.imageFull);

        loader = new Loader(image,imageFull);
        if (camera && !DB.COMUNIDAD) {
            root.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            image.setImageResource(R.drawable.ic_menu_name);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    base.addImage();
                }
            });
        } else {
            View.OnClickListener list = new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    fullScream(root);
                }
            };
            image.setOnClickListener(list);
            imageFull.setOnClickListener(list);
            loader.execute(args.getString("name"));
        }
        return root;
    }

    public void fullScream(View root) {
        if (ApunteActivity.zoom) {
            root.findViewById(R.id.image)
                    .setVisibility(View.GONE);
            root.findViewById(R.id.viewImageFull)
                    .setVisibility(View.VISIBLE);
            base.fullScream();
        } else {
            root.findViewById(R.id.image)
                    .setVisibility(View.VISIBLE);
            root.findViewById(R.id.viewImageFull)
                    .setVisibility(View.GONE);
            base.fullScream();
        }
        if (!ApunteActivity.fullScream)
            zoom = !zoom;
    }

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
                imagen.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                ostream.close();
            }catch (Exception e){}
            return f.getAbsolutePath();
        } catch (Exception ex){}
        return "";
    }

    public static class Loader extends AsyncTask<String, Void, Bitmap> {

        private WeakReference imageViewReference[];
        public Loader(ImageView... imageView) {
            imageViewReference= new WeakReference[imageView.length];
            int counter=0;
            for (ImageView ima:imageView)
                    imageViewReference[counter++] = new WeakReference<>(ima);
        }

        @Override
        protected synchronized Bitmap doInBackground(String... fotos) {
            Bitmap imagen = null;
            try {
                File ruta_sd = Environment.getExternalStorageDirectory();
                File ruta = new File(ruta_sd.getAbsolutePath(), DB.DIRECTORY + "//" + fotos[0]);

                if (ruta.exists())
                    imagen = BitmapFactory.decodeFile(ruta.getAbsolutePath());

                if (imagen == null) {

                    URL imageUrl = new URL(Server.URL_SERVER.replace("pu", "uploads/fotos/") + fotos[0]);
                    HttpURLConnection urlConnection = (HttpURLConnection) imageUrl.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    //imagen = BitmapFactory.decodeStream(inputStream);
                    imagen = BitmapFactory.decodeFile(save(inputStream, fotos[0]));
                }
            } catch (IOException e) {
            }
            return imagen;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            for (WeakReference imageView:imageViewReference) {
                ImageView image = (ImageView) imageView.get();
                if(image!=null)
                   image.setImageBitmap(bitmap);
            }

        }
    }
}