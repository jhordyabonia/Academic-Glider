package com.jhordyabonia.ag;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import util.Style;

//import android.widget.LinearLayout;

public class TestA extends Activity implements AdapterView.OnItemClickListener{

    private View before=null;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
/*
        SharedPreferences sp = getSharedPreferences(Style._STYLE, Context.MODE_PRIVATE);
        Style.STYLE = sp.getInt(Style._STYLE,0);
*/
        BaseAdapter collection = new BaseAdapter() {
            public int getCount() {return colors.length;}
            public long getItemId(int position) {return 0;}
            public Object getItem(int position) {return colors[position];}

            // create a new ImageView for each item referenced by the Adapter
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView out=new TextView(TestA.this);
                out.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
                out.setBackgroundResource(colors[position]);
                out.setTextColor(Color.WHITE);
                out.setGravity(Gravity.CENTER);
                if(Style.STYLE==colors[position]) {
                    out.setText("X");
                    before=out;
                }
                return out;
            }
        };
        GridView gridview =  findViewById(R.id.gridview);
        gridview.setAdapter(collection);
        gridview.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Style._STYLE, colors[i]);
        editor.apply();

        if(view instanceof TextView)
            ((TextView)view).setText("X");
        if(before!=null)
            ((TextView)before).setText("");
        before=view;
    }
}
