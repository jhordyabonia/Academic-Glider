package util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Permission;

import chat.ChatService;
import chat.DBChat;
import models.DB;
import webservice.LOG;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static util.Style.STYLE;
import static util.Style._STYLE;

public class Settings implements AdapterView.OnItemClickListener, View.OnClickListener {

    public static Settings local=null;

    public static String PERMISSION_DOWNLOAD_ASIGNATURA="download_asignatura";
    public static String PERMISSION_SHARE_ASIGNATURA="share_asignatura";

    public static String PERMISSION_ADD_ASIGNATURA="add_asignatura";
    public static String PERMISSION_ADD_HORARIO="add_horario";
    public static String PERMISSION_ADD_ALERTA="add_alerta";
    public static String PERMISSION_ADD_LECTURA="add_lectura";
    public static String PERMISSION_ADD_CALIFICABLE="add_calificable";
    public static String PERMISSION_ADD_APUNTE="add_apunute";

    public static String PERMISSION_DEL_ASIGNATURA="del_asignatura";
    public static String PERMISSION_DEL_HORARIO="del_horario";
    public static String PERMISSION_DEL_ALERTA="del_alerta";
    public static String PERMISSION_DEL_LECTURA="del_lectura";
    public static String PERMISSION_DEL_CALIFICABLE="del_calificable";
    public static String PERMISSION_DEL_APUNTE="del_apunute";

    public static String PERMISSION_EDIT_ASIGNATURA="edit_asignatura";
    public static String PERMISSION_EDIT_HORARIO="edit_horario";
    public static String PERMISSION_EDIT_ALERTA="edit_alerta";
    public static String PERMISSION_EDIT_LECTURA="edit_lectura";
    public static String PERMISSION_EDIT_CALIFICABLE="edit_calificable";
    public static String PERMISSION_EDIT_APUNTE="edit_apunute";

    public static String PERMISSION_ADD_GROUP="add_group";
    public static String PERMISSION_ADD_CHAT="add_chat";

    public static String PERMISSION_CHANGE_COLOR="change_color";
    public static String PERMISSION_CHANGE_USER="change_user";
    public static String PERMISSION_CHANGE_CEL="change_cel";
    public static String PERMISSION_CHANGE_UNIVERSIDAD="change_universidad";

    public static boolean PERMISSION(String permission){
        return DB.User.get("permisos").contains(permission);
    }

    public static boolean DROP_MODE=false,SOUND=true,VIBRATE=true;
    public static final String _DROP_MODE="drop_mode",_SOUND="sound",_VIBRATE="vibrate";
    public static int colors[] = {
            R.color.colorPrimary,
            R.color.colorBase,
            R.color.colorDark,
            R.color.colorPink,
            R.color.colorBlack,
            R.color.colorTinted,
            R.color.colorRed,
            R.color.colorGreen,
            R.color.colorGreen2,
            R.color.colorBlue,
            R.color.colorViolet,
            R.color.colorMarine,
            R.color.one,
            R.color.two,
            R.color.three,
            R.color.four,
            R.color.five,
            R.color.six,
            R.color.seven,
            R.color.each,
            R.color.nine,
            R.color.ten
    };
    private Activity home;
    private View before=null,rootView;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private CheckBox drop,sound,vibrate;
    private void read() {
        STYLE = sp.getInt(Style._STYLE,R.color.colorMarine);
        DROP_MODE= sp.getBoolean(_DROP_MODE,true);
        SOUND = sp.getBoolean(_SOUND,true);
        VIBRATE = sp.getBoolean(_VIBRATE,true);
    }
    public Settings(Activity h,View view){

        home=h;
        rootView=view;
        sp = getDefaultSharedPreferences(h);
        editor = sp.edit();
        read();
        local=this;

        if(PERMISSION(PERMISSION_CHANGE_COLOR)) {
            BaseAdapter collection = new BaseAdapter() {
                public int getCount() {return colors.length;}
                public long getItemId(int position) {return 0;}
                public Object getItem(int position) {return colors[position];}

                // create a new ImageView for each item referenced by the Adapter
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView out = new TextView(rootView.getContext());
                    int _100=(int)home.getResources().getDimension(R.dimen._100);
                    out.setLayoutParams(new ViewGroup.LayoutParams(_100,_100));
                    out.setBackgroundResource(colors[position]);
                    out.setTextColor(Color.WHITE);
                    out.setGravity(Gravity.CENTER);
                    if (STYLE == colors[position]) {
                        out.setText("X");
                        before = out;
                    }
                    return out;
                }
            };
            rootView.findViewById(R.id.theme).setVisibility(View.VISIBLE);
            GridView gridview = rootView.findViewById(R.id.gridview);
            gridview.setOnItemClickListener(this);
            gridview.setAdapter(collection);
            gridview.setVisibility(View.VISIBLE);
        }

        sound=rootView.findViewById(R.id.sound);
        sound.setOnClickListener(this);
        sound.setChecked(SOUND);
        vibrate=rootView.findViewById(R.id.vibrate);
        vibrate.setOnClickListener(this);
        vibrate.setChecked(VIBRATE);
        drop=rootView.findViewById(R.id.drop_mode);
        drop.setOnClickListener(this);
        drop.setChecked(DROP_MODE);
    }
    public void set(int id){

        switch (id) {
            case R.id.drop_mode:
                DROP_MODE = drop.isChecked();
                editor.putBoolean(_DROP_MODE,DROP_MODE);
                if (home instanceof HomeActivity)
                    ((HomeActivity) home).dropMode();
                break;
            case R.id.sound:
                SOUND=sound.isChecked();
                editor.putBoolean(_SOUND, SOUND);
                break;
            case R.id.vibrate:
                VIBRATE=vibrate.isChecked();
                editor.putBoolean(_VIBRATE,VIBRATE);
                break;
        }
        editor.apply();
    }
    @Override
    public void onClick(View v){
        set(v.getId());
    }

    @Override
    public void onItemClick (AdapterView < ? > adapterView, View view,int i, long l){
        STYLE=colors[i];
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Style._STYLE, STYLE);
        editor.apply();

        if (view instanceof TextView)
            ((TextView) view).setText("X");
        if (before != null)
            ((TextView) before).setText("");
        before = view;
        Style.bar(home);
    }

    public static boolean commands(String mensaje)
    {
        boolean out= false;
        try {
            JSONObject obj= new JSONObject(mensaje);
            String msj=obj.getString("command");

            switch (msj) {
                case _DROP_MODE:
                    DROP_MODE=obj.getBoolean("value");
                    if(local != null )
                        local.set(R.id.drop_mode);
                    break;
                case _SOUND:
                    SOUND=obj.getBoolean("value");
                    if(local != null )
                        local.set(R.id.sound);
                    break;
                case _VIBRATE:
                    VIBRATE=obj.getBoolean("value");
                    if(local != null )
                        local.set(R.id.vibrate);
                    break;
                case _STYLE:
                    STYLE=colors[obj.getInt("value")];
                    if(local != null ) {
                        local.editor.putInt(Style._STYLE, STYLE);
                        local.editor.apply();
                        if(local.home!=null)
                            Style.bar(local.home);
                    }break;
            }
            return true;
        }catch (JSONException e) {
                if (mensaje.contains("SYSTEM_FECHA")) {
                    out = true;
                    if (mensaje.contains("_ON"))
                        DBChat.FECHA_WS = out = true;
                    else if (mensaje.contains("_OFF"))
                        DBChat.FECHA_WS = false;
                    else out = false;
                } else if (mensaje.contains("SYSTEM_LOG")) {
                    out = true;
                    if (mensaje.contains("_ON"))
                        LOG.ACTIVE = true;
                    else if (mensaje.contains("_OFF"))
                        LOG.ACTIVE = false;
                    else out = false;
                } else if (mensaje.contains("SYSTEM_CHATSERVICE")) {
                    out = true;
                    if (mensaje.contains("_ON"))
                        ChatService.ACTIVE = true;
                    else if (mensaje.contains("_OFF"))
                        ChatService.ACTIVE = false;
                    else out = false;
                }
            }
        return out;
    }
}