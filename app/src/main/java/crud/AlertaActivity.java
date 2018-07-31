package crud;

import java.util.HashMap;

import android.os.Bundle;
import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.R;

public class AlertaActivity extends Base {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setVisible(R.id.nombre);
		setVisible(R.id._fecha);
		setVisible(R.id.fecha);
		setVisible(R.id._hora);
		setVisible(R.id.hora);
	}

	@Override
	protected HashMap<String, String> getData() {
		HashMap<String, String> datos = new HashMap<String, String>();
		datos.put("asignatura", HomeActivity.idAsignaturaActual());

		datos.put("nombre", get(R.id.nombre));
		datos.put("fecha", getFecha());
		datos.put("hora", getHora());
		return datos;
	}

	@Override
	protected void fill() {
		super.fill();
		set("nombre", R.id.nombre);
		setHora(set("hora", 0));
		setFecha(set("fecha", 0));
	}
}