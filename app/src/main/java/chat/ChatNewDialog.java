package chat;

import android.app.AlertDialog;
import android.app.Dialog;
import com.jhordyabonia.ag.R;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

class ChatNewDialog extends DialogFragment
{
        ListChatActivity main;
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            main=(ListChatActivity)getActivity();
            View view=main.getLayoutInflater().inflate(R.layout.new_group, null);

            final EditText nombre=view.findViewById(R.id.editText1);
            final EditText descripcion=view.findViewById(R.id.editText2);

            DialogInterface.OnClickListener dialogListener
                     = new DialogInterface.OnClickListener()
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
                            String n=nombre.getText().toString().trim();
                            if(n.isEmpty())
                                {
                                Toast.makeText(main,
                                "Nombre no debe estar vacio",
                                Toast.LENGTH_LONG).show();
                                return;
                                }
                            main.chat_new(main,
                                nombre.getText().toString(),
                                descripcion.getText().toString());
                             }
                    }
                }
            };
            AlertDialog.Builder builder =
                new AlertDialog.Builder(main);
                builder.setTitle("Nuevo Grupo")
                .setIcon(R.drawable.ic_dialogo_nuevo_grupo)
                ;/*.setPositiveButton("Aceptar", dialogListener)
                .setNegativeButton("Cancelar", dialogListener);*/

            //builder.setView(view);
            return builder.create();
        }
}