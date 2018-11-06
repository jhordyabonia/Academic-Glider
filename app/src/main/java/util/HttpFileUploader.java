package util;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
  
public class HttpFileUploader implements Runnable
{ 
	private URL connectURL;
    private String fileName;
    private FileInputStream fileInputStream = null;
	private String urlString;
	private String response;
 
    public HttpFileUploader(String urlString, String params, String fileName ) throws FileNotFoundException
    {
    	this.urlString=urlString;
        this.fileName = fileName;   
    	fileInputStream = new FileInputStream(fileName);
    } 
    public String response()
    {	return response;}
    private void thirdTry() 
    {
        String exsistingFileName = fileName;
 
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try
        {
            //------------------ CLIENT REQUEST
 
            // Abrimos una conexión http con la URL
 
            HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();
 
            // Permitimos Inputs
            conn.setDoInput(true);
 
            // Permitimos Outputs
            conn.setDoOutput(true);
 
            // Deshabilitamos el uso de la copia cacheada.
            conn.setUseCaches(false);
 
            // Usamos el método post esto podemos cambiarlo.
            conn.setRequestMethod("POST");
 
            conn.setRequestProperty("Connection", "Keep-Alive");
 
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
 
            DataOutputStream dos = new DataOutputStream( conn.getOutputStream() );
 
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + exsistingFileName +"\"" + lineEnd);
            dos.writeBytes(lineEnd);
 
            // creamos un buffer con el tamaño maximo de archivo, lo pondremos en 1MB
 
            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];
 
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
 
            while (bytesRead > 0)
            {
	            dos.write(buffer, 0, bufferSize);
	            bytesAvailable = fileInputStream.available();
	            bufferSize = Math.min(bytesAvailable, maxBufferSize);
	            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
 
            // enviar multipart form data
 
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
 
            // cerramos
            fileInputStream.close();
            dos.flush();
 
            InputStream is = conn.getInputStream();
            // retrieve the response from server
            int ch;
 
            StringBuffer b =new StringBuffer();
            while( ( ch = is.read() ) != -1 )
            {b.append( (char)ch );}
            response=b.toString();
            dos.close();
         }
        catch (MalformedURLException ex){}
        catch (IOException ioe){}
    } 
	@Override
	public void run() 
	{
		try 
		{
			connectURL = new URL(urlString);
			thirdTry();
		} catch (MalformedURLException e) {}
		
	}
	public static void uploadFile(String url,String filename)
	{
		try
		{
		    HttpFileUploader htfu = 
					new HttpFileUploader(url,"noparamshere", filename);
			Thread t=new Thread(htfu);
			t.start();
		}catch (FileNotFoundException e){}
	}
}