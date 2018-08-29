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
    TextView tv, tv2,tv3;
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
        tv2 = view.findViewById(R.id.textView2);
        tv3 = view.findViewById(R.id.textView3);
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
                /*//Se busca el Enum a partir del int Número
                ESPPB_events type = ESPPB_events.fromInt(3);
                //Se busca el Mensaje a partir del Enum obtenido anteriormente
                String Mensaje = ESPPB_events.valueOf(type.toString()).getStringMessage();
                //Se muestra
                System.out.println(type);
                System.out.println(Mensaje);*/
                Enviar1("Hola2");
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
                final int Tet=Integer.parseInt(String.valueOf(tet)); //Se convierte el char tet en int para buscar en el archivo enum
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
                                    //Se busca el Enum a partir del int Número
                                    ESPPB_events type = ESPPB_events.fromInt(Tet);
                                    //Se busca el Mensaje a partir del Enum obtenido anteriormente
                                    String Mensaje = ESPPB_events.valueOf(type.toString()).getStringMessage();
                                    //Se muestra
                                    System.out.println(type);
                                    System.out.println(Mensaje);
                                }
                            }
                        }

                        Log.e(TAG,"te1"+Mess);
                        tv.setText(Mess);
                        if (alert!=null) {
                            alert.dismiss(); //When process finish, and I have alert showing, automatically it is closed.
                        }
                        progressBar.dismiss();

                    }
                });
            }
        }).start();
    }

    public void Enviar1(final String data) {
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
        new Thread(new Runnable() {

            @Override
            public void run() {
                // do the thing that takes a long time
                ((MainActivity)getActivity()).send(data);
                //This delay is to receive the data and refresh it buffer
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String [] informacion;
                while(true) { //Este ciclo se repetirá hasta que el celular reciba un mensaje, que para este caso es el de AF1
                    Mess=null;
                    Mess=((MainActivity)getActivity()).messageComplete;  //Obtener una variable desde el Activity
                    informacion = Mess.split("-"); //Se separa el mensaje conrespecto al caractér -
                    if (informacion[0].equals("AF1")){
                        break;
                    }
                }
                Log.e(TAG,"te2:"+informacion[0]);
                Log.e(TAG,"te2:"+informacion[1]);
                if (informacion[1].equals("0")){
                    Log.e(TAG,"Distancia:"+informacion[2]);
                    //De esa manera se concatena strings para el setText, pero se debe crear una linea en el archivo String
                    tv3.setText(getString(R.string.distancia_Finicio,informacion[2]));
                }else{
                    //Se busca el Enum a partir del int Número
                    ESPPB_events type = ESPPB_events.fromInt(Integer.parseInt(String.valueOf(informacion[1])));
                    //Se busca el Mensaje a partir del Enum obtenido anteriormente
                    String Mensaje = ESPPB_events.valueOf(type.toString()).getStringMessage();
                    System.out.println(Mensaje);
                    tv2.setText(getString(R.string.errores_Finicio,Mensaje));
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (alert!=null) {
                            alert.dismiss(); //When process finish, and I have alert showing, automatically it is closed.
                        }
                        progressBar.dismiss();
                    }
                });
            }
        }).start();
    }
}