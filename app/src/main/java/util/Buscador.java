package util;

import java.util.HashMap;

import models.DB;
import webservice.Asynchtask;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

public class Buscador extends DialogFragment implements Asynchtask,OnClickListener
{
	private HomeActivity home;
	private View view;
	public Buscador(HomeActivity home)
	{this.home=home;}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = 
				new AlertDialog.Builder(home);
		builder.setTitle(getString(R.string.search_subject))
		.setIcon(android.R.drawable.ic_menu_search);
	    view=getActivity().getLayoutInflater()
    			.inflate(R.layout.buscar, null);

		ArrayAdapter<String> base =
				new ArrayAdapter<String>(home,R.layout.base);
		base.add(getString(R.string.search_subject));
		((Spinner) view.findViewById(R.id.universidad))
			.setAdapter(base);

	    view.findViewById(R.id.buscar).setOnClickListener(this);
	    builder.setView(view);
		Server.send("universidad", null, this);
		return builder.create();
	}
	@Override
	public void processFinish(String result) 
	{	
		if(result.startsWith("Universidad"))
		{
			ArrayAdapter<String> base =
					new ArrayAdapter<String>(home,R.layout.base);	
			result=result.replace("Universidad,","Todas,").replace(",Otra", "");
			base.addAll(result.split(","));
			((Spinner) view.findViewById(R.id.universidad))
				.setAdapter(base);
		}
	}
	@Override
	public void onClick(View arg0) 
	{
		HashMap<String, String> datos=new HashMap<String, String>();
		String universidad=(String)((Spinner)view.findViewById(R.id.universidad)).getSelectedItem();
	    String a_buscar = ((EditText)view.findViewById(R.id.a_buscar)).getText().toString();
		datos.put("universidad", universidad);
		datos.put("a_buscar",a_buscar);	
		DB.update(home,datos);
		dismiss();
	}
}