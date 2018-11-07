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

import chat.ChatService;
import chat.DBChat;
import webservice.LOG;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static util.Style.STYLE;

public class Settings implements AdapterView.OnItemClickListener, View.OnClickListener {

    public static boolean DROP_MODE=false,SOUND=true,VIBRATE=true;
    public static String _DROP_MODE="dromp_mode",_SOUND="sound",_VIBRATE="vibrate";
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
        GridView gridview = rootView.findViewById(R.id.gridview);
        gridview.setAdapter(collection);
        gridview.setOnItemClickListener(this);
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
    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
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
        if (mensaje.contains("SYSTEM_FECHA")) {
            out= true;
            if (mensaje.contains("_ON"))
                DBChat.FECHA_WS = out = true;
            else if (mensaje.contains("_OFF"))
                DBChat.FECHA_WS = false;
            else out= false;
        } else if (mensaje.contains("SYSTEM_LOG")) {
            out= true;
            if (mensaje.contains("_ON"))
                LOG.ACTIVE = true;
            else if (mensaje.contains("_OFF"))
                LOG.ACTIVE = false;
            else out= false;
        } else  if (mensaje.contains("SYSTEM_CHATSERVICE")) {
            out= true;
            if (mensaje.contains("_ON"))
                ChatService.ACTIVE = true;
            else if (mensaje.contains("_OFF"))
                ChatService.ACTIVE = false;
            else out= false;
        }
        return out;
    }
}