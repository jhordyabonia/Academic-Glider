package crud;

import java.util.HashMap;

import models.DB;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.jhordyabonia.ag.R;

public class HorarioActivity extends Base {

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		Spinner list =  findViewById(R.id.asignatura);
		ArrayAdapter<String> base = new ArrayAdapter<String>(this,
				R.layout.base);
		base.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		base.addAll(DB.Asignaturas.LIST_ASIGNATURAS);
		base.remove(getString(R.string.see_subjects));
		list.setAdapter(base);
		list.setPrompt(getString(R.string.select_subject));

		Spinner list2 =  findViewById(R.id.dia);
		ArrayAdapter<String> base2 = new ArrayAdapter<String>(this,
				R.layout.base);
		base2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		base2.addAll(DB.DAYS);
		list2.setAdapter(base2);
		list2.setPrompt(getString(R.string.select_day));

		setVisible(R.id.asignatura);
		setVisible(R.id.dia);
		setVisible(R.id._hora);
		setVisible(R.id.hora);
		setVisible(R.id.ubicacion);
		setVisible(R.id.duracion);

		((Spinner) findViewById(R.id.asignatura)).setSelection(DB.Asignaturas
				.getIndex(set("asignatura", 0)));

		((Spinner) findViewById(R.id.dia)).setSelection(DB.indexDay(set("dia",0)));
	}

	@Override
	protected HashMap<String, String> getData() {
		HashMap<String, String> datos = new HashMap<String, String>();
		String asignatura = (String) ((Spinner) findViewById(R.id.asignatura))
				.getSelectedItem();
		String dia = (String) ((Spinner) findViewById(R.id.dia))
				.getSelectedItem();
		datos.put("asignatura", DB.Asignaturas.getId(asignatura));
		datos.put("dia", dia);
		datos.put("hora",getHora());
		datos.put("ubicacion", get(R.id.ubicacion));
		datos.put("duracion", get(R.id.duracion));

		return datos;
	}

	@Override
	protected void fill() {
		super.fill();
		setHora(set("hora", 0));
		set("ubicacion", R.id.ubicacion);
		set("duracion", R.id.duracion);
	}

}
