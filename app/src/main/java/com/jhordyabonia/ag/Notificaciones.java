package com.jhordyabonia.ag;

import android.content.Intent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import controllers.Adapter;
import controllers.AsignaturasView;
import crud.Base;
import models.DB;

import static com.jhordyabonia.ag.HomeActivity.ON_DISPLAY;

public class Notificaciones  implements AdapterView.OnItemClickListener{

    public interface Notifications{
        int notificar(String usuario,String dato, int id) throws JSONException;
        int update(String title,String dato, String tipo);
    }
    public static final String FILE="com.jhordyabonia.ag.notificaciones";
    private static JSONArray db_notificaciones;
    public static Notificaciones notificaciones;
    private View display=null;
    protected ListView base;
    protected Adapter base_data;
    public Notificaciones()
    {load();notificaciones=this;}
    static {load();}
    public static void load()
    {
        try {db_notificaciones=new JSONArray(DB.load(FILE));}
        catch (JSONException e)
        {   db_notificaciones=new JSONArray();        }
    }
    public void onAdd(JSONObject item){
        try {

            base_data.add(new Adapter.Item(item.getString("asignatura"),
                    item.getString("type")+":"+item.getString("data")));
            add(item);
        }catch (JSONException e){}
    }

    public static void onView(JSONObject  item)
    {
        if(notificaciones!=null)
            notificaciones.onAdd(item);
    }

    public static boolean add(String asignatura,int type,String item,String data)
    {return add(asignatura,DB.MODELS[type],item,data);}

    public static boolean add(String asignatura,String type,String item,String data)
    {
        try {
            JSONObject tmp = new JSONObject();
            tmp.put("asignatura",asignatura);
            tmp.put("type",type);
            tmp.put("item",item);
            tmp.put("data",data);
            return add(tmp);
        }
        catch (JSONException e){return false;}
    }
    public static boolean add(JSONObject item) throws JSONException {
        //Structura de datos de la notificacion
        //item.getString("asignatura");
        //item.getString("type");//alerta,apunte,calificable,lectura,otros
        //item.getString("item");//id item
        //item.getString("data");//mensaje

        for (int y = 0; y < db_notificaciones.length(); y++) {
            JSONObject vv = db_notificaciones.getJSONObject(y);
            if(vv.toString().equals(item.toString()))
                return false;
        }

        db_notificaciones.put(item);
        onView(item);
        save();
        return true;
    }
    public static  void _Add(JSONObject item){
       if(notificaciones!=null)
           notificaciones.onAdd(item);
    }
    public static void save()
    {
        JSONArray tmp = new JSONArray();

        for(int n=0;n<db_notificaciones.length();n++) {

            if (db_notificaciones.isNull(n))
                continue;
            else try { tmp.put(db_notificaciones.get(n));}
            catch (JSONException e) {continue;}
        }
        DB.save(null,tmp.toString(),FILE);
        db_notificaciones=tmp;
    }
    public void paint(View v) {
        display=v;
        display.findViewById(R.id.add)
            .setVisibility(View.GONE);
        base_data = new Adapter(display.getContext(), Adapter.ITEM_TYPE.notificaciones, Adapter.notificaciones);
        base = display.findViewById(R.id.list);
        base.setDividerHeight(0);

        base.setAdapter(base_data);
        base.setOnItemClickListener(this);
        base.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index_item, long arg3) {
                if(!DB.COMUNIDAD)
                    showPopup(v,index_item);
                return false;
            }
        });
        base_data.clear();
        try {
            for (int y = 0; y < db_notificaciones.length(); y++) {
                JSONObject vv = db_notificaciones.getJSONObject(y);
                base_data.add(new Adapter.Item(vv.getString("asignatura"),
                        vv.getString("type")+":"+vv.getString("data")));
            }
        } catch (JSONException e) {}
    }

    public void showPopup(View v,final int id_item)
    {
        PopupMenu popup = new PopupMenu(display.getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem arg0)
            {
                try{
                base_data.remove(base_data.getItem(id_item));
                    db_notificaciones.put(id_item,null);
                    //db_notificaciones.remove(id_item);
                save();}catch(JSONException e){
                }
                return false;
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int index_item, long arg3)
    {
        TextView tv = v.findViewById(R.id.textView4);
        if(tv!=null)
            if(tv.getText().toString().equals(v.getContext().getString(R.string.empty)))
                return;
        Base.itemSeleted = index_item;
        ///QUE HACER TRAS TOCAR LA NOTIFICACION
        try {
            JSONObject tmp = db_notificaciones.getJSONObject(index_item);
            if(tmp!=null){
/*
                switch (tmp.getString("type")) {
                    case "asignatura":ON_DISPLAY=HomeActivity.ASIGNATURAS;
                        break;
                }*/
                if(display!=null)
                display.getContext().startActivity(new Intent(display.getContext(), AsignaturasView.class));
            }
        }catch (JSONException e){}
    }
}
