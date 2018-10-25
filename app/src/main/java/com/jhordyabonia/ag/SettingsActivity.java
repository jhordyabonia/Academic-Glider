package com.jhordyabonia.ag;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

import util.Style;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static util.Style.STYLE;


public class SettingsActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_empty);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new SettingsView())
                .commit();
        Style.bar(this);
    }

    public static class SettingsView extends Fragment{

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.settings, container, false);
            new Settings(getActivity(),rootView);
            return rootView;
        }
    }

    public static class Settings implements AdapterView.OnItemClickListener, View.OnClickListener {

        public static boolean DROP_MODE=false,SOUND=true,VIBRATE=true;
        public static String _DROP_MODE="dromp_mode",_SOUND="sound",_VIBRATE="vibrate";
        private Activity home;
        private View before=null,rootView;
        private SharedPreferences sp;
        private SharedPreferences.Editor editor;
        private CheckBox drop,sound,vibrate;
        private int colors[] = {
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
                    out.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
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
     }
}
