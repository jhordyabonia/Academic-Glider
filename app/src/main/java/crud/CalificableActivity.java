package crud;

import java.util.HashMap;

import android.os.Bundle;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.R;

public class CalificableActivity extends Base {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVisible(R.id._fecha);
		setVisible(R.id.fecha);
		setVisible(R.id.nombre);
		setVisible(R.id.porcentaje);
		setVisible(R.id.nota);
		setVisible(R.id.descripcion);
	}

	@Override
	protected HashMap<String, String> getData() {
		HashMap<String, String> datos = new HashMap<String, String>();
		datos.put("asignatura", HomeActivity.idAsignaturaActual());
		datos.put("fecha", getFecha());
		datos.put("nombre", get(R.id.nombre));
		datos.put("porcentaje", get(R.id.porcentaje));
		datos.put("nota", get(R.id.nota));
		datos.put("descripcion", get(R.id.descripcion));
		return datos;
	}

	@Override
	protected void fill() {
		super.fill();
		set("nombre", R.id.nombre);
		setFecha(set("fecha", 0));
		set("porcentaje", R.id.porcentaje);
		set("nota", R.id.nota);
		set("descripcion", R.id.descripcion);
	}
}