package chat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jhordyabonia.ag.R;

public class EditChat extends  DialogFragment
{
    private ProfileActivity profil;
    private DialogInterface.OnClickListener dialogListener;
    public EditChat(ProfileActivity p) {profil=p; }
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        View view=getActivity().getLayoutInflater()
                 .inflate(R.layout.new_group, null);

        final EditText nombre_=view.findViewById(R.id.editText1);
        final EditText descripcion_=view.findViewById(R.id.editText2);

        nombre_.setText(profil.nombre);
        descripcion_.setText(profil.descripcion);
        dialogListener = new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    switch(which)
                    {
                        case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();break;
                        case DialogInterface.BUTTON_POSITIVE:
                        {
                            String n=nombre_.getText().toString().trim();
                            if(n.isEmpty())
                            {
                                Toast.makeText(profil,getString(R.string.no_empty),Toast.LENGTH_LONG).show();
                                 return;
                            }
                        profil.chat_edit(nombre_.getText().toString(),
                        descripcion_.getText().toString());
                        }
                    }
                }
            };
        AlertDialog.Builder builder =
            new AlertDialog.Builder(profil);

        builder.setTitle(getString(R.string.edit_group)).setView(view)
            .setIcon(android.R.drawable.ic_menu_edit)
            .setPositiveButton(getString(R.string.ok), dialogListener)
            .setNegativeButton(getString(R.string.cancel), dialogListener);

        return builder.create();
    }
}