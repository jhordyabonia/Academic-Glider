package crud;

import java.util.HashMap;

import android.os.Bundle;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.R;

public class LecturaActivity extends Base {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVisible(R.id.nombre);
		setVisible(R.id.descripcion);
	}

	@Override
	protected HashMap<String, String> getData() {
		HashMap<String, String> datos = new HashMap<String, String>();
		datos.put("asignatura", HomeActivity.idAsignaturaActual());
		datos.put("nombre", get(R.id.nombre));
		datos.put("descripcion", get(R.id.descripcion));
		return datos;
	}

	@Override
	protected void fill() {
		super.fill();
		set("nombre", R.id.nombre);
		set("descripcion", R.id.descripcion);
	}
}
