package util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import controllers.Asignaturas;
import models.DB;
import webservice.Asynchtask;

public class CompartirAsignatura extends DialogFragment {

    private Activity activity;
    private String compartir_com;
    private  int index_asignatura;

    String[]list;
    final ArrayList<String> items_a_compartir= new ArrayList();

    public CompartirAsignatura(Activity a, String c, int i) {
        activity=a;compartir_com=c;index_asignatura=i;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
     {
         list=getResources().getStringArray(R.array.items_subject);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        android.content.DialogInterface.OnClickListener dialogListener
                    = dialogListener(activity,items_a_compartir,compartir_com,index_asignatura);
        // Set the dialog title
        builder.setTitle(getString(R.string.share))
            .setIcon(android.R.drawable.ic_menu_share)
            .setMultiChoiceItems(list, null,
                new DialogInterface.OnMultiChoiceClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which,boolean isChecked)
                        {
                             if(which==0&&isChecked)
                             {
                                    items_a_compartir.clear();
                                    for(String item:list)
                                        items_a_compartir.add(item);
                              }else if (isChecked)
                                items_a_compartir.add(list[which]);
                              else if (items_a_compartir.contains(list[which]))
                                items_a_compartir.remove(list[which]);
                        }
                    }
          )
        .setPositiveButton(getString(R.string.share), dialogListener)
        .setNegativeButton(getString(R.string.cancel), dialogListener);

        return builder.create();
        }
    private static DialogInterface.OnClickListener
    dialogListener(final Activity activity,final ArrayList<String> items_a_compartir,final String compartir_com,final int index_asignatura)
    {
        return new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch(which)
                {
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();break;
                    case DialogInterface.BUTTON_POSITIVE:
                    {
                        HashMap<String, String> data= new HashMap<String, String>();
                        for(String c:items_a_compartir)
                            data.put(c,"true");
                        data.put("compartir_con",compartir_com);
                        String compartir = DB.Asignaturas.LIST_ID_ASIGNATURAS[index_asignatura];
                        compartirApuntes(activity,compartir);
                        data.put("compartir", compartir);
                        Server.setDataToSend(data);
                        Asynchtask recep = new Asynchtask()
                        {
                            @Override
                            public void processFinish(String result)
                            {
                                Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
                            }
                        };
                        Server.send("compartir", activity, recep);
                    }
                }
            }
        };
    }
    private static void compartirApuntes(Activity activity,String id)
    {
        DB.model("apuntes");
        ArrayList<JSONObject> apuntes = DB.find("asignatura", id);
        String list="";
        for(JSONObject ap:apuntes)
            try {list+=","+ap.getString("apunte");}
            catch (JSONException e) {continue;}
        if(list.startsWith(","))
            list=list.substring(1);
        Intent intent = new Intent(activity, UploadService.class);
        intent.putExtra(UploadService.LIST_TO_UPLOAD, list);
        activity.startService(intent);
    }
    public static class List extends DialogFragment
    {
        private FragmentActivity activity;
        private String titulo,items[];
        private  DialogInterface.OnClickListener  actions;
        public List(FragmentActivity a,String t, String[] i,DialogInterface.OnClickListener at)
        {activity=a;titulo=t;items=i;actions=at;}
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(activity);
            builder.setTitle(titulo)
                    .setIcon(android.R.drawable.ic_menu_share)
                    .setItems(items,actions);
            return builder.create();
        }
    }

    public static class AsignaturaExist extends DialogFragment {
        ArrayList<JSONObject> list_asignaturas_tmp;
        String descargar="";
        boolean alt;
        public AsignaturaExist(ArrayList<JSONObject> l,boolean a,String d)
        {list_asignaturas_tmp=l;alt=a;descargar=d;}
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            DialogInterface.OnClickListener dialogListener
                    = new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    switch(which)
                    {
                        case DialogInterface.BUTTON_NEUTRAL:
                            Asignaturas.actualizar(alt,getActivity(),list_asignaturas_tmp,descargar);break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();break;
                        case DialogInterface.BUTTON_POSITIVE:
                            Asignaturas.agregar(getActivity(),descargar);
                    }
                }
            };
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setTitle(getString(R.string.subject_already))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(getString(R.string.subject_already_long))
                    .setPositiveButton(getString(R.string.action_settings), dialogListener)
                    .setNegativeButton(getString(R.string.cancel), dialogListener)
                    .setNeutralButton(getString(R.string.update), dialogListener);
            return builder.create();
        }
    }
}