package crud;

import java.util.HashMap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.R;

import models.DB;
import util.Validate;

import static com.jhordyabonia.ag.HomeActivity.ALERTAS;

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
		Validate v=new Validate(findViewById(R.id.crud));
		v.setIds(R.id.nombre);
		v.setTitles("nombre");
		HashMap<String, String> datos;
		if(v.run())
			datos=v.datos;
		else return null;
		datos.put("asignatura", HomeActivity.idAsignaturaActual());
		datos.put("fecha", getFecha());
		datos.put("hora", getHora());
		if(action==Actions.Edit)
			datos.put("alerta", set("alerta", 0));

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