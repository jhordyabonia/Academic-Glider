package util;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.jhordyabonia.ag.R;

import java.util.HashMap;

public class Validate {
    private View lienzo;
    private Context C;
    private int ids[];
    private String titles[];
    public HashMap<String, String> datos=new HashMap();
    public Validate(View l) {
        lienzo = l;
        C = l.getContext();
    }

    public Validate setTitles(String... t) {
        titles = t;
        return this;
    }
    public Validate setIds(int... i) {
        ids = i;
        return this;
    }

    private Validate setError(int id, String msj) {
        ((EditText) lienzo.findViewById(id))
                .setError(msj);
        return this;
    }

    public boolean run() {

        if(ids.length!=titles.length)
            return false;

        for (int y = 0; y < ids.length; y++) {
            View e= lienzo.findViewById(ids[y]);
            if(!( e instanceof EditText))
                return false;

            String t = titles[y],
                    d = ((EditText) e).getText().toString();

            if (t.equals("nombre")) if (d.trim().isEmpty()) {
                setError(ids[y], C.getString(R.string.name_noEmpty));
                return false;
            }
            if (t.equals("ubicacion")||t.equals("duracion")) if (d.trim().isEmpty()) {
                setError(ids[y], C.getString(R.string.noEmpty));
                return false;
            }
            if (t.equals("porcentaje")) {
                int porcentaje;
                try {
                    porcentaje=Integer.parseInt(d.trim());
                }catch (NumberFormatException ee){
                    setError(ids[y], C.getString(R.string.onlyValues0_100));
                    return false;
                }
                if (porcentaje < 0 || porcentaje > 100 ) {
                    setError(ids[y], C.getString(R.string.onlyValues0_100));
                    return false;
                }
            }
            if (t.equals("creditos")) {
                int credits =0;
                try {
                    credits = Integer.parseInt(d.trim());
                } catch (NumberFormatException ee) {
                    setError(ids[y], C.getString(R.string.creditsShort));
                    return false;
                }
                if (credits<1 && credits>20) {
                    setError(ids[y], C.getString(R.string.creditsShort));
                    return false;
                }
            }
            if (t.equals("codigo")) if (d.trim().length() < 5) {
                setError(ids[y], C.getString(R.string.codeShort));
                return false;
            }
            if (t.equals("descripcion")) if (d.trim().length() < 10) {
                setError(ids[y], C.getString(R.string.descriptionShort));
                return false;
            }
            if (t.equals("nota")) {
                int nota = 101;
                try {
                    nota = Integer.parseInt(d.trim());
                } catch (NumberFormatException ee) {
                    setError(ids[y], C.getString(R.string.onlyValues0_5));
                    return false;
                }
                if (nota<0 && nota>5) {
                    setError(ids[y], C.getString(R.string.onlyValues0_5));
                    return false;
                }
            }
            if (t.equals("codigo")) if (d.trim().length() < 5) {
                setError(ids[y], C.getString(R.string.codeShort));
                return false;
            }

            datos.put(titles[y],d);
        }

        return true;
    }
}