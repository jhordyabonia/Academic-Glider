package com.jhordyabonia.ag;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jhordyabonia.ag.HomeActivity;
import com.jhordyabonia.ag.MainActivity;
import com.jhordyabonia.ag.R;

import controllers.Asignaturas;
import controllers.Horarios;
import crud.Base;

import static com.jhordyabonia.ag.HomeActivity.ASIGNATURAS;
import static com.jhordyabonia.ag.HomeActivity.HORARIOS;
import static com.jhordyabonia.ag.HomeActivity.ON_DISPLAY;


public  class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    HomeActivity home;

    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        home = (HomeActivity) getActivity();
        View rootView;
        Bundle args = getArguments();
        int on_display = args.getInt(ARG_SECTION_NUMBER);

        switch (on_display) {
            case -1:
                rootView = inflater.inflate(R.layout.fragment_collection_object, container, false);
                ViewPager mViewPager = rootView.findViewById(R.id.pager);
                home.asignaturas.setPager(mViewPager);
                home.asignaturas.show(HomeActivity.ALERTAS);
                break;
            case 0:
                rootView = inflater.inflate(R.layout.lienzo, container, false);
                home.horario.show(rootView);
                ON_DISPLAY=HORARIOS;
                break;
            case 1:
                rootView = inflater.inflate(R.layout.lienzo, container, false);
                home.asignaturas.todas(rootView);
                ON_DISPLAY=ASIGNATURAS;
                break;
            case 6:
                rootView = inflater.inflate(R.layout.informacion, container, false);
                 new InformacionFragment.Informacion(rootView);
                break;
            default:
                rootView = inflater.inflate(R.layout.lienzo, container, false);
        }
       return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //((MainActivity) activity).onSectionAttached(
        //        getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
