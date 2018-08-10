package chat;

import android.app.AlertDialog;
import android.app.Dialog;
import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import models.DB;

public class ChatNewDialog extends DialogFragment
{
        private ListChatActivity main;
        private View view;
        public ChatNewDialog(ListChatActivity m){main=m;}

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            view=main.getLayoutInflater().inflate(R.layout.new_group, null);

            final EditText nombre=view.findViewById(R.id.editText1);
            final EditText descripcion=view.findViewById(R.id.editText2);

            DialogInterface.OnClickListener dialogListener
                     = new DialogInterface.OnClickListener()
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
                            String n=nombre.getText().toString().trim();
                            if(n.isEmpty())
                                {
                                Toast.makeText(main,
                                "Nombre no debe estar vacio",
                                Toast.LENGTH_LONG).show();
                                return;
                                }
                            main.chat_new(main,
                                nombre.getText().toString(),
                                descripcion.getText().toString());
                             }
                    }
                }
            };
            AlertDialog.Builder builder =
                new AlertDialog.Builder(main);

             builder.setTitle("Nuevo Grupo").setView(view)
                .setIcon(R.drawable.ic_dialogo_nuevo_grupo)
                .setPositiveButton("Aceptar", dialogListener)
                .setNegativeButton("Cancelar", dialogListener);

            return builder.create();
        }

    public static class ContactList extends DialogFragment {

        private int CHAT=0;
        private ListView base;
        private ChatAdapter base_data;
        private ArrayList<String> contactos;

        public ContactList(ArrayList<String> c,int chat)
        {contactos=c;CHAT=chat;}

        private AdapterView.OnItemClickListener listener =new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                String h="nada";

                TextView t= (TextView)arg1.findViewById(R.id.textView4);
                if(t!=null)
                    h=t.getText().toString();
                View l=arg1.findViewById(R.id.selected);
                if(l!=null)
                {
                    if(contactos.contains(h))
                    {
                        l.setVisibility(View.GONE);
                        contactos.remove(h);
                        return;
                    }
                    l.setVisibility(View.VISIBLE);
                    contactos.add(h);
                }
            }
        };
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
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();break;
                        case DialogInterface.BUTTON_POSITIVE:
                        {
                            String usuarios="";
                            for(String id:contactos)
                                usuarios+=id+",";
                            HashMap<String, String> datos=new HashMap<String, String>();
                            datos.put("chat",""+CHAT);
                            datos.put("usuario", DB.User.get("id"));
                            datos.put("usuarios",usuarios);
                            Server.setDataToSend(datos);
                            Server.send("mensaje/add_group", null, null);

                            DB.save(getActivity(),datos.toString(),"add_group.txt");
                        }
                    }
                }
            };
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());
            builder.setTitle("Agregar Compañeros")
                    .setIcon(R.drawable.ic_dialogo_add_togroup)
                    .setPositiveButton("Aceptar", dialogListener)
                    .setNegativeButton("Cancelar", dialogListener);
            View root=getActivity().getLayoutInflater()
                    .inflate(R.layout.list_select, null);

            base_data = new ChatAdapter(root.getContext(),new ArrayList<ChatAdapter.Mensaje>(),true);
            base =  root.findViewById(R.id.list);
            base.setAdapter(base_data);
            base.setOnItemClickListener(listener);
            base.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            base.setItemsCanFocus(false);
            try
            {
                JSONArray data= new JSONArray(DB.load(DBChat.FILE_CONTACTS));
                for(int i=0;i<data.length();i++)
                {
                    JSONObject tmp= data.getJSONObject(i);
                    base_data.add(
                            new ChatAdapter.Mensaje(
                                    tmp.getString("id"),
                                    tmp.getString("nombre"),"Glider",
                                    tmp.getString("cel")
                            ));
                }
            } catch (JSONException e) {}
            contactos.clear();
            builder.setView(root);
            return builder.create();
        }
    }
}