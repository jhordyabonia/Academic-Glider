package util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jhordyabonia.ag.R;

import java.util.ArrayList;

import models.DB;


public  class  ImageAdapter extends BaseAdapter
{
    private Context mContext;
    // references to our images
    private ArrayList<String> mThumbIds = new ArrayList();

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public void addItem(String img){
        mThumbIds.add(img);
        notifyDataSetChanged();
    }

    public int getCount() {
        return mThumbIds.size();
    }

    public Object getItem(int position) {
        return mThumbIds.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(180, 300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }
        if(position==0) {
            imageView.setImageResource(R.drawable.ic_menu_name);
        }else{
            Image.Loader loader= new Image.Loader(imageView);
            loader.execute(mThumbIds.get(position));
        }
        return imageView;
    }

}
