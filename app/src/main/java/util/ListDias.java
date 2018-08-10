package util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.jhordyabonia.ag.HomeActivity;

import models.DB;

public class ListDias extends DialogFragment
{
    private HomeActivity home ;
    public ListDias(HomeActivity h){home=h;}
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder =
        new AlertDialog.Builder(home);
        builder.setTitle("Horario")
        .setIcon(android.R.drawable.ic_menu_agenda)
        .setItems(DB.semana(),
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {   home.show_dias(which);                }
                }
            );
        return builder.create();
    }
};