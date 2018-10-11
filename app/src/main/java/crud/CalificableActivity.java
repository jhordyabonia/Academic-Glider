package crud;

import java.util.HashMap;

import android.os.Bundle;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.R;

import models.DB;
import util.Validate;

import static com.jhordyabonia.ag.HomeActivity.ALERTAS;
import static com.jhordyabonia.ag.HomeActivity.CALIFICABLES;
import static com.jhordyabonia.ag.HomeActivity.HORARIOS;

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
		Validate v=new Validate(findViewById(R.id.crud));
		v.setIds(R.id.nombre,R.id.porcentaje,R.id.nota,R.id.descripcion);
		v.setTitles("nombre","porcentaje","nota","descripcion");
		HashMap<String, String> datos;
		if(v.run())
			datos=v.datos;
		else return null;
		datos.put("asignatura", HomeActivity.idAsignaturaActual());
		datos.put("fecha", getFecha());
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