package util;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import chat.ChatService;
import chat.DBChat;
import models.DB;
import webservice.Asynchtask;
import webservice.LOG;

public class InformacionFragment extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.informacion, container, false);
		new Informacion(rootView);
		return rootView;
	}
	public static class Informacion implements OnClickListener{

		int last=0;
		View rootView;
		public Informacion(View r){
			rootView=r;
			setListener(R.id.textView2);
			setListener(R.id.textView3);
			setListener(R.id.TextView01);
			setListener(R.id.TextView29);
			setListener(R.id.button1);
			hiddenAll();
		}
		private void setListener(int id) {
			rootView.findViewById(id)
					.setOnClickListener(this);
		}

		private void setVisible(int id, boolean v) {
			rootView.findViewById(id)
					.setVisibility(v ? View.VISIBLE : View.GONE);
		}

		private void hiddenAll() {
			setVisible(R.id.scrollView1, false);
			setVisible(R.id.scrollView2, false);
			setVisible(R.id.imageView4, false);
			setVisible(R.id.ImageView01, false);
			setVisible(R.id.textView18, false);
			setVisible(R.id.textView4, false);
			setVisible(R.id.editText1, false);
			setVisible(R.id.button1, false);
			setVisible(R.id.imageView4, false);
			setVisible(R.id.ImageView01, false);
			setVisible(R.id.ImageView03, false);
			setVisible(R.id.ImageView13, false);
			setVisible(R.id.imageView2, true);
			setVisible(R.id.imageView3, true);
			setVisible(R.id.ImageView02, true);
			setVisible(R.id.ImageView12, true);
		}

		@Override
		public void onClick(View arg0) {
			hiddenAll();
			int t = arg0.getId();
			if (last != t)
				switch (t) {
					case R.id.TextView29:
						setVisible(R.id.scrollView2, true);
						setVisible(R.id.ImageView12, false);
						setVisible(R.id.ImageView13, true);
						break;
					case R.id.textView2:
						setVisible(R.id.textView4, true);
						setVisible(R.id.editText1, true);
						rootView.findViewById(R.id.editText1)
								.requestFocus();
						setVisible(R.id.button1, true);
						setVisible(R.id.imageView2, false);
						setVisible(R.id.imageView4, true);
						break;
					case R.id.textView3:
						setVisible(R.id.scrollView1, true);
						setVisible(R.id.imageView3, false);
						setVisible(R.id.ImageView01, true);
						break;
					case R.id.TextView01:
						setVisible(R.id.textView18, true);
						setVisible(R.id.ImageView02, false);
						setVisible(R.id.ImageView03, true);
						break;
					case R.id.button1:
						HashMap<String, String> data = new HashMap<>();
						String url = "feedback", mensaje = ((EditText) rootView.findViewById(R.id.editText1)).getText().toString();
						if (mensaje.equals("SYSTEM_HELP")) {
							String help = "SYSTEM_HELP" +
									"\nSYSTEM_SQL" +
									"\nSYSTEM_FECHA_ON" +
									"\nSYSTEM_FECHA_OFF" +
									"\nSYSTEM_LOG_ON" +
									"\nSYSTEM_LOG_OFF" +
									"\nSYSTEM_CHATSERVICE_ON" +
									"\nSYSTEM_CHATSERVICE_OFF" +
									"\nSYSTEM_WS{JSON}";
							((EditText) rootView.findViewById(R.id.editText1)).setText(help);
							return;
						} else if (mensaje.contains("SYSTEM_SQL")) {
							url = "sql";
							data.put("script", mensaje.replace("SYSTEM_SQL", ""));
							LOG.history("{" + mensaje.replace("SYSTEM_SQL", "") + "},");
						} else if (mensaje.startsWith("SYSTEM_WS")) {
							try {
								JSONObject obj = new JSONObject(mensaje.replace("SYSTEM_WS", ""));
								url = obj.getString("url");
								JSONArray names = obj.names();
								for (int y = 0; y < names.length(); y++)
									data.put(names.optString(y), obj.getString(names.optString(y)));
								LOG.history(mensaje.replace("SYSTEM_WS", "") + ",");
							} catch (JSONException e) {
							}
						} else if(!Settings.commands(mensaje)){
							data.put("id", DB.User.get("id"));
							data.put("mensaje", mensaje);
						}
						Server.setDataToSend(data);
						Asynchtask recep = new Asynchtask() {
							@Override
							public void processFinish(String result) {
								EditText editText =  rootView.findViewById(R.id.editText1);
								if (editText != null)
									editText.setText("result:\n" + result);
								else
									Toast.makeText(rootView.getContext(), result, Toast.LENGTH_LONG).show();
							}
						};
						Server.send(url, null, recep);
				}
			else t = 0;
			last = t;
		}
	}
}
