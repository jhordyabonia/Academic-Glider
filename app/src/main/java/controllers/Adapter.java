package controllers;

import java.util.ArrayList;

import models.DB;
import util.Image;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jhordyabonia.ag.R;

public class Adapter extends ArrayAdapter{

    public enum ITEM_TYPE{asignatura,alerta,lectura,horario,calificable,apunte, notificaciones};
	public  static ArrayList<Item> asignaturas= new ArrayList<>();
	public  static ArrayList<Item> alertas= new ArrayList<>();
	public  static ArrayList<Item> apuntes= new ArrayList<>();
	public  static ArrayList<Item> horarios= new ArrayList<>();
	public  static ArrayList<Item> calificables= new ArrayList<>();
	public  static ArrayList<Item> lecturas= new ArrayList<>();
	public  static ArrayList<Item> notificaciones = new ArrayList<>();
	
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
		add(new Item());
	}
 
	public void add(Item a)
	{
		locale.add(a);
		notifyDataSetChanged();
	}
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		
		final View root;
		if(type==ITEM_TYPE.asignatura)
			root = inflater.inflate(R.layout.item_asignatura,null);
		else root = inflater.inflate(R.layout.item,null);

		Item tmp = locale.get(position);
		if(tmp.empty)
			return inflater.inflate(R.layout.empty,null);

		switch(type)
		{
			case asignatura:
				((TextView)root.findViewById(R.id.textView1))
				.setText(tmp.codigo);
				((TextView)root.findViewById(R.id.textView2))
				.setText(tmp.nombre);
				((TextView)root.findViewById(R.id.textView3))
				.setText(tmp.creditos);
				if(tmp.descripcion!=null)
					if(!tmp.descripcion.isEmpty())
						((TextView)root.findViewById(R.id.loremipsum))
								.setText(tmp.descripcion);

				if(tmp.nota.isEmpty()){
					root.findViewById(R.id.textView48).setVisibility(View.GONE);
					tmp.nota="Sin Calificar";
				}
				((TextView)root.findViewById(R.id.textView4))
				.setText(tmp.nota);
				/*if(tmp.image!=null&&!DB.COMUNIDAD) {
                    root.findViewById(R.id.imageView1).setVisibility(View.VISIBLE);
					Image.Loader loader = new Image.Loader(tmp.nombre, (ImageView) root.findViewById(R.id.imageView1));
					loader.execute(tmp.image);
				}*/
				if(!DB.COMUNIDAD)
					Image.findImg(tmp.nombre, (ImageView) root.findViewById(R.id.imageView1));
				if(DB.COMUNIDAD)
					root.findViewById(R.id.textView48).setVisibility(View.GONE);

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
		public String image;
		public String descripcion;
       	public String codigo;
        public String nombre;
        public String nota;
        public String creditos;
        public boolean active= true,empty=true;

		public Item() {	}

		public Item(String n, String cr)
        {            
            this.nombre = DB.titulo(n,"",70);
            this.nota = DB.titulo(cr,"",104);
            this.empty=false;
        }
        public Item( String n, String nt,boolean t) {            
            this.nombre = DB.titulo(n,"",70);
            this.nota = DB.titulo(nt,"",104);
            this.active = t;
			this.empty=false;
        }
        public Item(String c, String n, String nt, String cr) {
        	this.codigo = c.toUpperCase();
            this.nombre = DB.titulo(n.toUpperCase(),"",30);
            this.nota = DB.titulo(nt,"",20);
            this.creditos = cr;
			this.empty=false;
        }
		public Item(String c, String n, String nt, String cr, String des,String img) {
			this.codigo = c.toUpperCase();
			this.nombre = DB.titulo(n.toUpperCase(),"",104);
			this.nota = DB.titulo(nt,"",20);
			this.creditos = cr;
			this.descripcion=DB.titulo(des,"",110);
			this.image=img;
			this.empty=false;
		}
		@Override
		public String toString() {
			return nombre;
		}
	}
}
