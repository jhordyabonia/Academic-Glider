package webservice;

import java.util.Map;

import webservice.HttpRequest.HttpRequestException;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Client extends AsyncTask<String, Long, String> {
	// Variable con los datos para pasar al web service
	private Map<String, String> datos;
	// Url del servicio web
	private String url = "";

	// Actividad para mostrar el cuadro de progreso
	private Context actividad;

	private boolean progVisible = false;

	public void showProg(boolean t) {
		progVisible = t;
	}

	// Resultado
	private String json = null;

	// Clase a la cual se le retorna los datos dle ws
	private Asynchtask callback = null;

	public Asynchtask getCallback() {
		return callback;
	}

	public void setCallback(Asynchtask callback) {
		this.callback = callback;
	}

	private ProgressDialog progDailog;

	/**
	 * Crea una estancia de la clase webService para hacer consultas a ws
	 * 
	 * @param urlWebService
	 *            Url del servicio web
	 * @param data
	 *            Datos a enviar del servicios web
	 * @param activity
	 *            Actividad de donde se llama el servicio web, para mostrar el
	 *            cuadro de "Cargando"
	 * @param callback
	 *            CLase a la que se le retornara los datos del servicio web
	 */
	public Client(String urlWebService, Map<String, String> data,
			Context activity, Asynchtask callback) {
		this.url = urlWebService;
		this.datos = data;
		this.actividad = activity;
		this.callback = callback;
		progVisible = true;
	}

	public Client(String urlWebService, Map<String, String> data,
			Context activity) {
		this.url = urlWebService;
		this.datos = data;
		this.actividad = activity;
		progVisible = false;
	}

	public Client() {
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (progVisible) {
			if (progDailog == null) {
				progDailog = new ProgressDialog(actividad);
				progDailog.setMessage("Loading...");
				progDailog.setIndeterminate(false);
				progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				// progDailog.setCancelable(true);
			}
			progDailog.show();
		}
	}
	@Override
	protected String doInBackground(String... params) {
		try {	
			return HttpRequest.post(this.url).form(this.datos).body();
		} catch (HttpRequestException exception) {
			Log.e("doInBackground", exception.getMessage());
			return "Sin conexion a internet";//"Error HttpRequestException";
		} catch (Exception e) {
			Log.e("doInBackground", e.getMessage());
			return "Error Exception";
		}
	}

	@Override
	protected void onPostExecute(String response) {
		super.onPostExecute(response);
		this.json = response;
		if (progVisible)
			progDailog.dismiss();
		// Retorno los datos
		if (callback != null)
			{
				if(LOG.ACTIVE)
					LOG.save("{\"url\":\"" + this.url + "\",\"data\":\"" + this.datos.toString() + "\",\"result\":\"" + response + "\"},");

				callback.processFinish(this.json);
			}
	}

	public Map<String, String> getDatos() {
		return datos;
	}

	public void setDatos(Map<String, String> datos) {
		this.datos = datos;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Context getActividad() {
		return actividad;
	}

	public void setActividad(Context actividad) {
		progVisible = actividad != null;
		this.actividad = actividad;
	}

	public String getXml() {
		return json;
	}

	public void setXml(String xml) {
		this.json = xml;
	}

	public ProgressDialog getProgDailog() {
		return progDailog;
	}

	public void setProgDailog(ProgressDialog progDailog) {
		this.progDailog = progDailog;
	}
}
