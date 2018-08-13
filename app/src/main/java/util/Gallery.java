package util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import java.util.ArrayList;

import crud.ApunteActivity;

public class Gallery extends FragmentPagerAdapter
{
    private ArrayList<String> names= new ArrayList<>();
    ApunteActivity base;
    private ViewPager galery;

    public Gallery(FragmentManager fm, ApunteActivity b, ViewPager g) {
        super(fm);  base=b; galery=g; names.add("");
    }

    public void loadItem(String img)
    {
        names.add(img);
        notifyDataSetChanged();
        galery.setCurrentItem(names.size()-1);
        Toast.makeText(base, "Apunte "+(names.size()-1)
                +" agregado!",Toast.LENGTH_SHORT).show();
    }
    @Override
    public Fragment getItem(int i)
    {
        String name=names.get(i);
        Bundle args = new Bundle();
        args.putString("name",name);

        Fragment fragment = new Image(name.isEmpty(),base);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public int getCount() {return names.size();}
    @Override
    public CharSequence getPageTitle(int position)
    {
        if(position==0)return "Agregar apunte";
        return "apunte " + position;
    }
}