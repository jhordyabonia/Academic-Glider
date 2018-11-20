package controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.R;

import crud.AsignaturaActivity;
import crud.Base;
import models.DB;
import util.Style;

import static com.jhordyabonia.ag.HomeActivity.ASIGNATURAS;
import static com.jhordyabonia.ag.HomeActivity.ASIGNATURA_ACTUAL;
import static com.jhordyabonia.ag.HomeActivity.ON_DISPLAY;
import static controllers.Asignaturas.compartir_com;
import static controllers.Asignaturas.items_a_compartir;

public class AsignaturasView extends FragmentActivity {

    private int asignatura,selected=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Style.bar(this);
        Intent mIntent=getIntent();
        if(mIntent!=null) {
            asignatura = mIntent.getIntExtra(Base._ITEM_SELECTED, 0);
            if (asignatura != 0)
                Base.itemSeleted = asignatura;
        }
        if(DB.Asignaturas.LIST_ASIGNATURAS.length>ASIGNATURA_ACTUAL)
				getActionBar().setTitle(DB.titulo(DB.Asignaturas.LIST_ASIGNATURAS[ASIGNATURA_ACTUAL]));
        setContentView(R.layout.fragment_collection_object);
        ViewPager mViewPager =  findViewById(R.id.pager);
        Style.set(mViewPager);
        CollectionPagerAdapter mCollectionPagerAdapter =
        new CollectionPagerAdapter(getSupportFragmentManager(),this);
        mViewPager.setAdapter(mCollectionPagerAdapter);
        mViewPager.setOnPageChangeListener
        (new ViewPager.SimpleOnPageChangeListener(){
                @Override
                public void onPageSelected(int position){
                    ON_DISPLAY = selected = position;
                    Base.itemSeleted = 0;
                }
            }
        );

        mViewPager.setCurrentItem(ON_DISPLAY,true);
        getActionBar().setHomeButtonEnabled(true);
    }
    @Override
    public void onResume() {
        super.onResume();
        ON_DISPLAY=selected;
        Base.itemSeleted = 0;
    }
    public static class CollectionPagerAdapter extends FragmentStatePagerAdapter{
        private Activity home;
        public CollectionPagerAdapter(FragmentManager fm, Activity h)
        {super(fm);home=h;}

        @Override
        public Fragment getItem(int i)
        {
            Fragment fragment = null;

            DB.model(DB.MODELS[i]);
            switch (i){
                case HomeActivity.ALERTAS:
                    fragment = new Alertas();
                    break;
                case HomeActivity.APUNTES:
                    fragment = new Apuntes();
                    break;
                case HomeActivity.LECTURAS:
                    fragment = new Lecturas();
                    break;
                case HomeActivity.CALIFICABLES:
                    fragment = new Calificables();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {return 4;}
        @Override
        public CharSequence getPageTitle(int position)
        {return HomeActivity.onDisplay(position,home);}
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(DB.COMUNIDAD)
            menu.findItem(R.id.share).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_asignatura, menu);
        return true;
    }
    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.ver:
                ON_DISPLAY=HomeActivity.ASIGNATURAS;
                Base.itemSeleted=asignatura;
                Base.crud(this, Base.Actions.Edit);
                break;
            case R.id.share:
                items_a_compartir.clear();
                Base.itemSeleted=selected;
                Asignaturas.compartir(this,compartir_com,asignatura).show(getSupportFragmentManager(), "missiles");break;
        }
        return super.onOptionsItemSelected(item);
    }
}