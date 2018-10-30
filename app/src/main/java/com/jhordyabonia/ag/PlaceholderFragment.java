package com.jhordyabonia.ag;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import chat.ChatNewDialog;
import chat.DBChat;
import chat.ListChat;
import controllers.Alertas;
import models.DB;
import util.InformacionFragment;

import static com.jhordyabonia.ag.HomeActivity.CHATS;
import static com.jhordyabonia.ag.HomeActivity.CONTACTOS;
import static com.jhordyabonia.ag.HomeActivity.GRUPOS;
import static com.jhordyabonia.ag.HomeActivity.ASIGNATURAS;
import static com.jhordyabonia.ag.HomeActivity.HORARIOS;
import static com.jhordyabonia.ag.HomeActivity.ON_DISPLAY;


public  class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private HomeActivity home;
    private View rootView=null;

    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private View.OnClickListener chat_listener=
    new View.OnClickListener(){

        public void onClick(View view)
        {
            if(ON_DISPLAY==CHATS)
              home.setPage(CONTACTOS,false);
            else if(ON_DISPLAY==GRUPOS){
                new ChatNewDialog(home).show(home.getSupportFragmentManager(), "missiles");
            }else
            {
                String msj=getString(R.string.msj_share1) +"\n"+
                        getString(R.string.msj_share2) +Server.URL_SERVER;
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, msj);
                intent.setType("text/plain");
                Intent chooser = Intent.createChooser(intent, getString(R.string.invite));

                if (intent.resolveActivity(home.getPackageManager()) != null)
                    startActivity(chooser);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        home = (HomeActivity) getActivity();

        Bundle args = getArguments();
        int on_display = args.getInt(ARG_SECTION_NUMBER);
        if(!DB.LOGGED)
            on_display=9;

        switch (on_display) {
            case 0:
                rootView = inflater.inflate(R.layout.lienzo, container, false);
                Notificaciones noti= new Notificaciones();
                noti.paint(rootView);
                ON_DISPLAY=HomeActivity.NOTIFICATION;
                home.actionBar.setTitle(R.string.notifications);
                break;
            case 1:
                rootView = inflater.inflate(R.layout.lienzo, container, false);
                home.horario.show(rootView);
                ON_DISPLAY=HORARIOS;
                home.actionBar.setTitle(R.string.horarios);
                break;
            case 6:
                DB.update(home);
            case 66:
            case 2:
                rootView = inflater.inflate(R.layout.lienzo, container, false);
                home.asignaturas.todas(rootView);
                ON_DISPLAY=ASIGNATURAS;
                home.actionBar.setTitle(R.string.asignaturas);
                break;
            case 3:
                ON_DISPLAY=CONTACTOS;
                rootView = inflater.inflate(R.layout.lienzo_chat, container, false);
                ListChat contacts= new ListChat(CONTACTOS);
                contacts.setMain(home);
                contacts.setListener(chat_listener);
                contacts.show(rootView);
                home.actionBar.setTitle(R.string.contacts);
                break;
            case 4:
                ON_DISPLAY=CHATS;
                rootView = inflater.inflate(R.layout.lienzo_chat, container, false);
                ListChat chats= new ListChat(CHATS);
                chats.setMain(home);
                chats.setListener(chat_listener);
                chats.show(rootView);
                home.actionBar.setTitle(R.string.chats);
                break;
            case 5:
                ON_DISPLAY=GRUPOS;
                rootView = inflater.inflate(R.layout.lienzo_chat, container, false);
                ListChat groups= new ListChat(GRUPOS);
                groups.setMain(home);
                groups.setListener(chat_listener);
                groups.show(rootView);
                home.actionBar.setTitle(R.string.groups);
                break;
            case 7:
                rootView = inflater.inflate(R.layout.activity_registrarme, container, false);
                Cuenta account=new Cuenta(home);
                account.setLienzo(rootView);
                account.fill();
                break;
            case 8:
                rootView = inflater.inflate(R.layout.informacion, container, false);
                new InformacionFragment.Informacion(rootView);
                home.actionBar.setTitle(R.string.info_title);
                break;
            case 9:
                rootView = inflater.inflate(R.layout.settings, container, false);
                new SettingsActivity.Settings(home,rootView);
                home.actionBar.setTitle(R.string.settings);
                break;
            case 10:
               Login.logout(home);
                break;
            default:
                rootView = inflater.inflate(R.layout.lienzo, container, false);
        }
        home.invalidateOptionsMenu();
       return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
