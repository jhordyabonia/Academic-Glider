package crud;

import java.util.HashMap;

import android.os.Bundle;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.R;

import models.DB;
import util.Validate;

import static com.jhordyabonia.ag.HomeActivity.ALERTAS;
import static com.jhordyabonia.ag.HomeActivity.LECTURAS;

public class LecturaActivity extends Base {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVisible(R.id.nombre);
		setVisible(R.id.descripcion);
	}

	@Override
	protected HashMap<String, String> getData() {
		Validate v=new Validate(findViewById(R.id.crud));
		v.setIds(R.id.nombre,R.id.descripcion);
		v.setTitles("nombre","descripcion");
		HashMap<String, String> datos;
		if(v.run())
			datos=v.datos;
		else return null;

		datos.put("asignatura", HomeActivity.idAsignaturaActual());
		return datos;
	}

	@Override
	protected void fill() {
		super.fill();
		set("nombre", R.id.nombre);
		set("descripcion", R.id.descripcion);
	}
}
