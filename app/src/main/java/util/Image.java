package util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import crud.ApunteActivity;
import static crud.ApunteActivity.zoom;
import models.DB;
import webservice.LOG;

public class Image extends Fragment {
    public static HashMap<String,Bitmap> MAP=new HashMap<>();
    private static int HEIGHT = 1836 / 3, WIDTH = 3264 / 3;
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

        loader = new Loader(image, imageFull);
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

    public static String save(InputStream data, String file) {

        if (!DB.memory())
            return "";
        try {
            File ruta = new File(DB.root, DB.DIRECTORY);

            File f = new File(ruta, file);
            Bitmap imagen = BitmapFactory.decodeStream(data);
            try {
                f.createNewFile();
                FileOutputStream ostream = new FileOutputStream(f);
                imagen.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                ostream.close();
            } catch (Exception e) {
            }
            return f.getAbsolutePath();
        } catch (Exception ex) {
        }
        return "";
    }

    public static class Loader extends AsyncTask<String, Void, Bitmap> {

        private WeakReference imageViewReference[];
        private boolean blur=false;
        private Context context=null;
        private String mURL = Server.URL_SERVER.replace("pu", "uploads/fotos/");
        private String name;
        public Loader(View... imageView) {
            make(imageView);
        }

        public Loader(String url, View... imageView) {
            make(imageView);
            mURL = url;
        }

        public void make(View... imageView) {
            imageViewReference = new WeakReference[imageView.length];
            int counter = 0;
            for (View ima : imageView)
                imageViewReference[counter++] = new WeakReference<>(ima);
        }

        public static int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) >= reqHeight
                        && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }

        @Override
        protected synchronized Bitmap doInBackground(String... fotos) {
            Bitmap imagen = null;
            try {
                name = fotos[0];
                if(MAP.containsKey(name))
                        return MAP.get(name);

                if (name.contains("http"))
                    name = mURL + ".jpg";

                File ruta = new File(DB.root, DB.DIRECTORY + "//" + name);
                final BitmapFactory.Options options = new BitmapFactory.Options();
                //buca la imagen local
                if (ruta.exists()) {
                    //Lectura de dimenciones solamenete
                    name = ruta.getAbsolutePath();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(name, options);
                    options.inSampleSize = calculateInSampleSize(options, WIDTH, HEIGHT);
                    ///Lectura de la img
                    options.inJustDecodeBounds = false;
                    imagen = BitmapFactory.decodeFile(name, options);
                }
                //Si no hay imagen la descarga
                if (imagen == null) {
                    URL imageUrl;
                    if (mURL.contains("http"))
                        imageUrl = new URL(mURL + fotos[0]);
                    else imageUrl = new URL(fotos[0]);
                    HttpURLConnection urlConnection = (HttpURLConnection) imageUrl.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    //guardado de la img
                    name = save(inputStream, name);
                    //Lectura de dimenciones solamenete
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(name, options);
                    options.inSampleSize = calculateInSampleSize(options, WIDTH, HEIGHT);
                    ///Lectura de la img
                    options.inJustDecodeBounds = false;
                    imagen = BitmapFactory.decodeFile(name, options);
                }
            } catch (IOException e) {}
            if(imagen!=null)
                MAP.put(name,imagen);
            return imagen;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            for (WeakReference imageView : imageViewReference) {
                Object v=imageView.get();
                if (v instanceof ImageView) {
                    ImageView image = (ImageView) v;
                    if(blur)
                        image.setImageBitmap(blurRenderScript(bitmap,21));
                    else image.setImageBitmap(bitmap);
                }
            }
        }

        @SuppressLint("NewApi")
        private Bitmap blurRenderScript(Bitmap smallBitmap, int radius) {

            try {smallBitmap = RGB565toARGB888(smallBitmap);}
            catch (Exception e) {e.printStackTrace();}


            Bitmap bitmap;
            if(smallBitmap!=null) {
                bitmap = Bitmap.createBitmap(
                        smallBitmap.getWidth(),
                        smallBitmap.getHeight(),
                        Bitmap.Config.ARGB_8888);
            }else {
                smallBitmap = bitmap = Bitmap.createBitmap(
                        480,
                        600,
                        Bitmap.Config.ARGB_8888);
            }
            RenderScript renderScript = RenderScript.create(context);

            Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
            Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                    Element.U8_4(renderScript));
            blur.setInput(blurInput);
            blur.setRadius(radius); // radius must be 0 < r <= 25
            blur.forEach(blurOutput);

            blurOutput.copyTo(bitmap);
            renderScript.destroy();

            return bitmap;

        }

        private Bitmap RGB565toARGB888(Bitmap img) {
            int numPixels = img.getWidth() * img.getHeight();
            int[] pixels = new int[numPixels];

            //Get JPEG pixels.  Each int is the color values for one pixel.
            img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

            //Create a Bitmap of the appropriate format.
            Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

            //Set RGB pixels.
            result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
            return result;
        }

        public void setContext(Context c) {
            context=c;
            blur = true;
        }
    }
}