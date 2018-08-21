package controllers;

import models.DB;

import org.json.JSONException;
import org.json.JSONObject;

import com.jhordyabonia.ag.HomeActivity;

import controllers.Adapter.ITEM_TYPE;
import controllers.Adapter.Item;

import static com.jhordyabonia.ag.HomeActivity.LECTURAS;

public class Lecturas extends Controller {

	@Override
	public void show() {

		DB.model(DB.MODELS[LECTURAS]);
		LOCAL_DB = DB.find("asignatura", HomeActivity.idAsignaturaActual());
		base_data = new Adapter(rootView.getContext(),ITEM_TYPE.lectura,Adapter.lecturas);
		base.setAdapter(base_data);
		if(!LOCAL_DB.isEmpty())
		try {
				base_data.clear();
			for (JSONObject v : LOCAL_DB)
				base_data.add(new Item(v.getString("nombre"), v.getString("descripcion")));
		} catch (JSONException e) {
		}
	}

}
