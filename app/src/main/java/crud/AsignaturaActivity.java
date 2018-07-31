package crud;

import java.util.HashMap;

import models.DB;

import android.os.Bundle;

import com.jhordyabonia.ag.R;

public class AsignaturaActivity extends Base {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setVisible(R.id.codigo);
		setVisible(R.id.nombre);
		setVisible(R.id.creditos);
		setVisible(R.id.nota);
		if(DB.COMUNIDAD)
			setVisible(R.id.ubicacion);
	}

	@Override
	protected HashMap<String, String> getData() {
		HashMap<String, String> datos = new HashMap<String, String>();
		datos.put("usuario", DB.User.get("id"));
		datos.put("codigo", get(R.id.codigo));
		datos.put("nombre", get(R.id.nombre));
		datos.put("creditos", get(R.id.creditos));
		datos.put("nota", get(R.id.nota));
		return datos;
	}

	@Override
	protected void fill() {
		super.fill();
		set("nombre", R.id.nombre);		
		set("nota", R.id.nota);

		if(DB.COMUNIDAD)
		{
			set("codigo", R.id.codigo,"Codigo: ");
			set("usuario", R.id.ubicacion,"Autor: ");
			set("creditos", R.id.creditos,"Creditos: ");
		}else 
		{
			set("creditos", R.id.creditos);
			set("codigo", R.id.codigo);
		}
	}
}
