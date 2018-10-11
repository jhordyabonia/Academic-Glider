package crud;

import java.util.HashMap;

import models.DB;
import util.Validate;

import android.os.Bundle;

import com.jhordyabonia.ag.R;

import static com.jhordyabonia.ag.HomeActivity.ALERTAS;
import static com.jhordyabonia.ag.HomeActivity.ASIGNATURAS;

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
		Validate v=new Validate(findViewById(R.id.crud));
		v.setIds(R.id.nombre,R.id.creditos,R.id.nota);
		v.setTitles("nombre","creditos","nota");
		HashMap<String, String> datos;
		if(v.run())
			datos=v.datos;
		else return null;
		datos.put("usuario", DB.User.get("id"));
		datos.put("codigo", get(R.id.codigo));
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
