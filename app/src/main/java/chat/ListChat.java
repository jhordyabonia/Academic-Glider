package chat;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import models.DB;
import util.Style;

import static chat.DBChat.ON_CHAT;
import static com.jhordyabonia.ag.HomeActivity.ASIGNATURAS;
import static com.jhordyabonia.ag.HomeActivity.CONTACTOS;
import static com.jhordyabonia.ag.HomeActivity.CHATS;
import static com.jhordyabonia.ag.HomeActivity.GRUPOS;
import static com.jhordyabonia.ag.HomeActivity.ON_DISPLAY;

public class  ListChat  implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener
{
    public final ArrayList<String> chats=new ArrayList<>();
    public final ArrayList<String> grupos=new ArrayList<>();
    public final ArrayList<String> contactos=new ArrayList<>();
    public final ArrayList<Integer> contactos_id=new ArrayList<>();

    public static class Display extends Fragment{
        final ListChat data;
        public Display(int i){this.data=new ListChat(i);}
        //public Display(ListChat data){this.data=data;}
        public void load() throws JSONException {data.load();}
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View root = inflater.inflate(R.layout.lienzo_chat,
                    container, false);
            data.setMain(getActivity());
            return data.show(root);
        }
    }
    private int VIEW;
    private ListView base;
    private ChatAdapter base_data;
    private Activity main;
    private View.OnClickListener listener;
    public void setListener(View.OnClickListener l)
    {listener=l;}
    public ListChat(int i){VIEW=i;}
    public void setMain(Activity a){main=a;}
    public View show(View root)
    {
        base_data = new ChatAdapter(root.getContext(),new ArrayList<ChatAdapter.Mensaje>(),true);
        base =  root.findViewById(R.id.list);
        base.setOnItemClickListener(this);
        base.setOnItemLongClickListener(this);
        base.setAdapter(base_data);

        ImageView imageView =  root.findViewById(R.id.add);
        imageView.setVisibility(View.VISIBLE);

        if(main instanceof View.OnClickListener)
        {
            View.OnClickListener t= (View.OnClickListener)main;
            if(t!=null)
                imageView.setOnClickListener(t);
        }else if(listener!=null)
            imageView.setOnClickListener(listener);
        else
            imageView.setVisibility(View.GONE);

        try
        {
            if(VIEW==CONTACTOS)
            {
                contactos.clear();
                contactos_id.clear();
                base_data.clear();
                JSONArray data= new JSONArray(DB.load(DBChat.FILE_CONTACTS,main));
                for(int i=0;i<data.length();i++)
                {
                    JSONObject tmp = data.getJSONObject(i);
                    base_data.add( new ChatAdapter.Mensaje("",
                                    tmp.getString("nombre"),"Glider",
                                    tmp.getString("cel"))
                                );
                    contactos.add(tmp.getString("cel_"));
                    contactos_id.add(tmp.getInt("id"));
                }
                DBChat.getContactCheker(main).execute();
                base.setOnItemLongClickListener(null);
                imageView.setImageResource(R.drawable.twotone_group_add_white_36);
                root.findViewById(R.id.textView1).setVisibility(View.GONE);
            }else load();
        }catch (JSONException e) {
            DBChat.getContactCheker(main).execute();
        }

        Style.set(root);
        return root;
    }
    public void load() throws JSONException
    {
        if(base_data==null)return;

        if(VIEW==GRUPOS)
            grupos.clear();
        else if(VIEW==CHATS)
            chats.clear();

        base_data.clear();
        for(int i = 0; i< DBChat.get().length(); i++)
        {
            JSONObject tmp = DBChat.get(i);
            if(VIEW-ON_CHAT==tmp.getInt("tipo"))
            {
                String nombre=tmp.getString("nombre");
                JSONArray msjs=new JSONArray();;
                try{msjs=tmp.getJSONArray("mensajes");}
                catch(JSONException e){}
                String dato;
                if(CHATS==VIEW)
                {
                    String id= tmp.getString("nombre")
                            .replace("_"+ DB.User.get("celular")+"_","")
                            .replace("_","");
                    nombre=DBChat.get_contact("nombre","cel_",id);
                    if(nombre.isEmpty())
                        nombre=id;
                }
                if(msjs.length()>=1)
                {
                    JSONObject msj_tmp=msjs.optJSONObject(msjs.length()-1);
                    int last_msj=msj_tmp.getInt("id");
                    DBChat.LAST_MSJ=DBChat.LAST_MSJ<last_msj?last_msj:DBChat.LAST_MSJ;
                    dato=msj_tmp.getString("dato");
                    if(DB.MODELS[ASIGNATURAS].contains(msj_tmp.getString("tipo")))
                        dato=main.getString(R.string.asignaturas)+"...";
                }else continue;
                int icon=VIEW==GRUPOS?R.drawable.ic_dialogo_nuevo_grupo:R.drawable.ic_dialogo_add_togroup;
                base_data.add(new ChatAdapter.Mensaje("",
                        DB.titulo(nombre,50),
                        DB.titulo(DBChat.fecha(tmp.getString("fecha")),"",6,false),
                        DB.titulo(dato,"",130),icon
                ));
               if(VIEW==GRUPOS)
                    grupos.add(tmp.getString("id"));
                else if(VIEW==CHATS)
                    chats.add(tmp.getString("id"));
            }//if(VIEW==tmp.getInt("tipo"))
        }//for
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                   int index_item, long arg3){showPopup(v,index_item); return true;}
    public void showPopup( View v,final int index_item)
    {
        PopupMenu popup = new PopupMenu(base.getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.chat, popup.getMenu());
        if(VIEW==GRUPOS)
        {
            popup.getMenu().findItem(R.id.ver_perfil).setVisible(true);
            popup.getMenu().findItem(R.id.salir).setVisible(true);
        }
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem arg0)
            {
                String chat = chat_id(index_item);
                switch(arg0.getItemId())
                {
                    case R.id.ver_perfil:
                        try
                        {
                            JSONObject chat_ = new JSONObject(DBChat.find("id",chat));
                            Intent intent_tmp= new Intent(main, ProfileActivity.class);
                            intent_tmp.putExtra("CHAT",chat_.getInt("id"));
                            intent_tmp.putExtra("DESCRIPCION",chat_.getString("descripcion"));
                            intent_tmp.putExtra("NOMBRE",chat_.getString("nombre"));
                            intent_tmp.putExtra("WONNER",chat_.getString("usuario"));
                            main.startActivity(intent_tmp);
                        }catch (JSONException e)
                        {
                            Toast.makeText(main, R.string.fail_load_caht,
                                    Toast.LENGTH_LONG).show();
                        }return true;
                    case R.id.borrar_chat:
                        try
                        {
                            JSONObject tmp_= DBChat.get(Integer.valueOf(DBChat.find("index","id",chat)));
                            if(tmp_!=null)
                                tmp_.put("mensajes", null);
                            Intent intent =
                                    new Intent(main,
                                            chat.ChatActivity.class);
                            intent.putExtra("CHAT", chat);
                            main.startActivity(intent);
                        }catch (JSONException e) {}
                        return true;
                    case R.id.salir:
                        HashMap<String, String> datos=new HashMap<String, String>();
                        datos.put("chat",chat);
                        datos.put("celular", DB.User.get("celular"));
                        Server.setDataToSend(datos);
                        Server.send("chat/delete", null, null);
                        return true;
                }return true;
            }
        });
    }
    private String chat_id(int arg2)
    {
        String chat = "";
        if(ON_DISPLAY==GRUPOS)
            chat = grupos.get(arg2);
        else if(ON_DISPLAY==CHATS)
            chat = chats.get(arg2);
        else if(ON_DISPLAY== CONTACTOS)
        {
            Long a=Long.valueOf(DB.User.get("celular")),
                    c, b=Long.valueOf(contactos.get(arg2));
            if(b>a)
            { c=b;b=a;a=c;}
            String nombre=
                    "_"+ DB.User.get("celular")+"_"+contactos.get(arg2)+"_";
            chat=DBChat.find("id","nombre",nombre);
            if(chat.isEmpty())
            {
                nombre="_"+a+"_"+b+"_";
                chat=DBChat.find("id","nombre",nombre);
            }
        }return chat;
    }
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1,
                            int arg2, long arg3)
    {
        String chat = chat_id(arg2);
        if(chat.isEmpty())
        {
            Long a=Long.valueOf(DB.User.get("celular")),
                    c, b=Long.valueOf(contactos.get(arg2));
            if(b>a)
            { c=b;b=a;a=c;}
            ListChatActivity.chat_new(main,"_"+a+"_"+b+"_",
                    "",""+contactos_id.get(arg2));
            ChatMain t= (ChatMain)main;
            if(t!=null)
                t.setPage(CHATS,true);
            return;
        }
        Intent intent =
                new Intent(main,
                        chat.ChatActivity.class);
        intent.putExtra("CHAT", chat);
        main.startActivity(intent);
    }
    public interface ChatMain
    {
        void setPage(int i, boolean t);
    }
}

