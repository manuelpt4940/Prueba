package com.example.usuario.prueba;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import static android.content.ContentValues.TAG;


/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FInicio.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FInicio#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FInicio extends Fragment{
    Button button, button2;
    TextView tv;
    ProgressDialog progressBar;
    String Mess;
    AlertDialog.Builder alert_Process;
    AlertDialog alert;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finicio, container, false);
        // Inflate the layout for this fragment
        button = view.findViewById(R.id.butt);
        tv = view.findViewById(R.id.tv1);
        button2 = view.findViewById(R.id.button2);

        //progressBar = view.findViewById(R.id.progressBar);
        progressBar = new ProgressDialog(getActivity());
        progressBar.setIcon(R.drawable.ic_menu_share);
        progressBar.setCancelable(false);
        //Code to put a cancel button an avoid cancelled immediately
        progressBar.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel",(DialogInterface.OnClickListener) null);


        button.setOnClickListener(new View.OnClickListener(){
              @Override
              public void onClick(View v) {
                  Enviar("Hola","Enviando1");

              }
        });

        button2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Enviar("Hola2", "Enviando2");
            }
        });




        return view;
    }




    public void Enviar(final String data, String title) {
        progressBar.show();//(getActivity(), title, "Please wait...");  //show a progress dialog

        //This section must be after progressBar.show******************************
        progressBar.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert_Process = new AlertDialog.Builder(getActivity());
                alert_Process.setMessage("Do you want Cancel process?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressBar.dismiss();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alert = alert_Process.create();
                alert.setTitle("ALERT!");
                alert.show();
            }
        });
        //*****************************************************************
        //progressBar.setCanceledOnTouchOutside(true);   //Habilitar la opción de cancelar al oprimir por fuera


        new Thread(new Runnable() {

            @Override
            public void run()
            {
                // do the thing that takes a long time
                ((MainActivity)getActivity()).send(data);
                //This delay is to receive the data and refresh it buffer
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Mess=null;
                Mess=((MainActivity)getActivity()).messageComplete;  //Obtener una variable desde el Activity
                char tet = Mess.charAt(0);
                Log.e(TAG,"tes"+tet);
                while(tet != '2') {
                    if (!(progressBar.isShowing())){
                        ((MainActivity)getActivity()).send("Cancel");
                        ///////////////////////////////////Make an AlertDialog in a new method
                        Mess="Cancelado";
                        break;
                    }

                    if (Mess.equals("01")){ //Error en plataforma
                        break;
                    }
                    if (Mess.equals("10")){ //Error en plataforma
                        break;
                    }

                    try {
                        Mess=null;
                        Mess = ((MainActivity) getActivity()).messageComplete;
                    }catch (NullPointerException e){ //Necessary because when turn off the server BT, appear and NullPointerException

                    }

                    tet = Mess.charAt(0);


                }
                Log.e(TAG,"tes"+Mess);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        if (Mess.equals("01")){ //When the user Cancel the process, will appear a message
                            Toast.makeText((getActivity()),"Error: Se ha levantado el pie",Toast.LENGTH_LONG).show();
                        }
                        else{
                            if (Mess.equals("10")){ //When the user Cancel the process, will appear a message
                                Toast.makeText((getActivity()),"Error: Se ha movido el brazo",Toast.LENGTH_LONG).show();
                            }
                            else{
                                if (Mess=="Cancelado"){ //When the user Cancel the process, will appear a message
                                    Toast.makeText((getActivity()),"You are cancelled the process",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText((getActivity()),"Process execute successfully.",Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        Log.e(TAG,"te1"+Mess);
                        tv.setText(Mess);
                        alert.dismiss(); //When process finish, and I have alert showing, automatically it is closed.
                        progressBar.dismiss();

                    }
                });
            }
        }).start();
    }
}