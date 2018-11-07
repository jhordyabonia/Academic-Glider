package chat;

import java.util.ArrayList;
import java.util.Random;
import java.util.zip.Inflater;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jhordyabonia.ag.R;
import com.jhordyabonia.ag.SettingsActivity;

import util.Style;

public class ChatAdapter extends ArrayAdapter
{
	private boolean portadas=false;
	private Context context;
	private ArrayList<Mensaje> items;
	private int max=util.Settings.colors.length;
	private Random ram= new Random(max);
	public ChatAdapter(Context c,ArrayList<Mensaje> i)
	{	
		super(c,R.layout.mensaje,i); 
		context=c;
		items=i;
		items.clear();
	} 
	public ChatAdapter(Context c,ArrayList<Mensaje> i,boolean p)
	{	
		super(c,R.layout.mensaje,i); 
		context=c;
		items=i;
		items.clear();
		portadas=true;
	} 
	public void add(Mensaje a)
	{
		items.add(a);
		notifyDataSetChanged();
	}
	private View info(LayoutInflater inflater,Mensaje tmp)
	{
		View r = inflater.inflate(R.layout.mensaje_info,null);

		((TextView)r.findViewById(R.id.textView1))
		.setText(tmp.mensaje);
		((TextView)r.findViewById(R.id.textView3))
		.setText(tmp.hora);
		if(tmp.icon_info==-1)
			tmp.icon_info=R.drawable.twotone_info_white_18;
		((ImageView)r.findViewById(R.id.imageView2))
		.setImageResource(tmp.icon_info);
		return r;
	}
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		final View root ;

		Mensaje tmp = items.get(position);

		if(tmp.icon_info!=0)
			return info(inflater,tmp);

		if(portadas)
		{
			View r = inflater.inflate(R.layout.base_group,null);

			((TextView)r.findViewById(R.id.title))
					.setText(tmp.usuario);
			((TextView)r.findViewById(R.id.sub_title))
					.setText(tmp.mensaje);
			((TextView)r.findViewById(R.id.date))
					.setText(tmp.hora);
			TextView logo= r.findViewById(R.id.logo);
			logo.setText(tmp.usuario.substring(0,1));

			if(logo!=null){
				int result=ram.nextInt(max);
				if(result==7)
					result+=ram.nextBoolean()?1:-1;
				int color=util.Settings.colors[result];
				Drawable background = logo.getBackground();
				GradientDrawable gradientDrawable = (GradientDrawable) background;
				gradientDrawable.setColor(r.getResources().getColor(color));
			}

			return r;
			/*
			root = inflater.inflate(R.layout.item_chat,null);
			root.findViewById(R.id.textView1)
			.setVisibility(View.GONE);
			((TextView)root.findViewById(R.id.textView2))
			.setText(tmp.usuario);
			((TextView)root.findViewById(R.id.textView3))
			.setText(tmp.hora);
			((TextView)root.findViewById(R.id.textView4))
			.setText(tmp.mensaje);
			root.findViewById(R.id.imageView1)
			.setVisibility(View.VISIBLE);
			
			((ImageView)root.findViewById(R.id.imageView1))
			.setImageResource(tmp.icon);
			return root;
			*/
		}
		if(tmp.dato!=null&&tmp.me)
			root = inflater.inflate(R.layout.mensaje_asignatura_me,null);
		else if(tmp.dato!=null&&!tmp.me)
			root = inflater.inflate(R.layout.mensaje_asignatura,null);
		else if(tmp.me)
			root = inflater.inflate(R.layout.mensaje_me,null);	
		else root = inflater.inflate(R.layout.mensaje,null);	
		if(tmp.me)
		{
			((TextView)root.findViewById(R.id.textView4))
			.setText(tmp.mensaje);
			((TextView)root.findViewById(R.id.textView5))
			.setText(tmp.hora);
			return root;				
		}
		((TextView)root.findViewById(R.id.textView1))
		.setText(tmp.mensaje);
		((TextView)root.findViewById(R.id.textView3))
		.setText(tmp.hora);
		
		if(!tmp.grupo)
			return root;
		
		root.findViewById(R.id.imageView1)
		.setVisibility(View.VISIBLE);
		root.findViewById(R.id.textView2)
		.setVisibility(View.VISIBLE);

		((TextView)root.findViewById(R.id.textView2))
		.setText(tmp.usuario);			
		load(tmp.foto);
		
		return root;
	}
	private void load(String s){}

	public static class Mensaje
	{
		public int icon=R.drawable.twotone_phone_android_white_18;
	    public String foto;
	   	public String usuario;
	    public String hora;
	    public String mensaje;
	    public Object dato=null;
	    public boolean grupo= true;
	    public boolean me= true;
	    public int icon_info=0;
	    public boolean test=true;

	    public Mensaje(String h,String m) 
	    {            
	        this.mensaje = m;
	        this.hora = h;
	        this.me=false;
	        this.grupo=false;
	    } 
	    public Mensaje(int i,String h,String m) 
	    {            
	        this.mensaje = m;
	        this.hora = h;
	        this.icon_info=i;
	        this.me=false;
	    } 
	    public Mensaje(String h,String m,boolean me) 
	    {            
	        this.mensaje = m;
	        this.hora = h;
	        this.me=me;
	    }
	    public Mensaje(String f, String u, String h, String m) 
	    {
	        this.foto =f;
	        this.usuario = u;
	        this.hora = h;
	        this.mensaje = m;
	        this.me=false;
	    }
	    public Mensaje(String f, String u, String h, String m,int i) 
	    {
	        this.foto =f;
	        this.usuario = u;
	        this.hora = h;
	        this.mensaje = m;
	        this.me=false;
	        this.icon=i;
	    }
	    public Mensaje(Object dato,String f, String u, String h, String m, boolean me, boolean g) 
	    {
	        this.foto =f;
	        this.usuario = u;
	        this.hora = h;
	        this.mensaje = m;
	        this.me=me;
	        this.dato=dato;
	        this.grupo=g;
	    }
	} 
}