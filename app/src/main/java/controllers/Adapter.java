package controllers;

import java.util.ArrayList;

import models.DB;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jhordyabonia.ag.R;

public class Adapter extends ArrayAdapter{

    public enum ITEM_TYPE{asignatura,alerta,lectura,horario,calificable,apunte};
	public  static ArrayList<Item> asignaturas= new ArrayList<Item>();
	public  static ArrayList<Item> alertas= new ArrayList<Item>();
	public  static ArrayList<Item> apuntes= new ArrayList<Item>();
	public  static ArrayList<Item> horarios= new ArrayList<Item>();
	public  static ArrayList<Item> calificables= new ArrayList<Item>();
	public  static ArrayList<Item> lecturas= new ArrayList<Item>();
	
	private Context context;
	private ArrayList<Item> locale;
	private ITEM_TYPE type;

	public Adapter(Context c,ITEM_TYPE t,ArrayList<Item> l)
	{	
		super(c,R.layout.item,l); 
		context=c;
		type=t;
		locale=l;
		locale.clear();
		add(new Item("  ",c.getString(R.string.empty),false));
	}
 
	public void add(Item a)
	{
		locale.add(a);
		notifyDataSetChanged();
	}
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		
		final View root = inflater.inflate(R.layout.item,null);	
		
		Item tmp = locale.get(position);
		switch(type)
		{
			case asignatura:
				((TextView)root.findViewById(R.id.textView1))
				.setText(tmp.codigo);
				((TextView)root.findViewById(R.id.textView2))
				.setText(tmp.nombre);
				((TextView)root.findViewById(R.id.textView3))
				.setText(tmp.creditos);
				((TextView)root.findViewById(R.id.textView4))
				.setText(tmp.nota);
				break;
			case alerta:
				root.findViewById(R.id.textView1)
				.setVisibility(View.GONE);
				((TextView)root.findViewById(R.id.textView2))
				.setText(tmp.nombre);
				root.findViewById(R.id.textView3)
				.setVisibility(View.GONE);
				((TextView)root.findViewById(R.id.textView4))
				.setText(tmp.nota);
				root.findViewById(R.id.imageView1)
				.setVisibility(View.VISIBLE);
				if(tmp.active)
					((ImageView)root.findViewById(R.id.imageView1))
					.setImageResource(R.drawable.ic_alarm);
				break;
			default://case apunte:
				root.findViewById(R.id.textView1)
				.setVisibility(View.GONE);
				((TextView)root.findViewById(R.id.textView2))
				.setText(tmp.nombre);
				root.findViewById(R.id.textView3)
				.setVisibility(View.GONE);
				((TextView)root.findViewById(R.id.textView4))
				.setText(tmp.nota);
				break;
			/*case calificable:
				((TextView)root.findViewById(R.id.textView1))
				.setVisibility(View.GONE);
				((TextView)root.findViewById(R.id.textView2))
				.setText(tmp.nombre);
				((TextView)root.findViewById(R.id.textView3))
				.setVisibility(View.GONE);
				((TextView)root.findViewById(R.id.textView4))
				.setText(tmp.nota);
				break;
			case horario:
				((TextView)root.findViewById(R.id.textView1))
				.setVisibility(View.GONE);
				((TextView)root.findViewById(R.id.textView2))
				.setText(tmp.nombre);
				((TextView)root.findViewById(R.id.textView3))
				.setVisibility(View.GONE);
				((TextView)root.findViewById(R.id.textView4))
				.setText(tmp.nota);
				break;
			case lectura:
				((TextView)root.findViewById(R.id.textView1))
				.setVisibility(View.GONE);
				((TextView)root.findViewById(R.id.textView2))
				.setText(tmp.nombre);
				((TextView)root.findViewById(R.id.textView3))
				.setVisibility(View.GONE);
				((TextView)root.findViewById(R.id.textView4))
				.setText(tmp.nota);
				break;*/
		}
		return root;
	}
	public static class Item {
       	public String codigo;
        public String nombre;
        public String nota;
        public String creditos;
        public boolean active= true;

        public Item(String n,String cr) 
        {            
            this.nombre = DB.titulo(n,70);
            this.nota = DB.titulo(cr,104);
        }
        public Item( String n, String nt,boolean t) {            
            this.nombre = DB.titulo(n,70);
            this.nota = DB.titulo(nt,104);
            this.active = t;
        }
        public Item(String c, String n, String nt, String cr) {
            this.codigo = c.toUpperCase();
            this.nombre = DB.titulo(n.toUpperCase(),20);
            this.nota = DB.titulo(nt,20);
            this.creditos = cr;
        }
    }  
}
