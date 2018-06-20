package com.example.usuario.prueba;

import android.app.ProgressDialog;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private ProgressDialog progressBar;


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
        progressBar = ProgressDialog.show(getActivity(), title, "Please wait...");  //show a progress dialog

        new Thread(new Runnable() {
            String Mess;
            @Override
            public void run()
            {
                // do the thing that takes a long time
                ((MainActivity)getActivity()).send(data);
                Mess=((MainActivity)getActivity()).messageComplete;  //Obtener una variable desde el Activity
                char tet = Mess.charAt(0);
                Log.e(TAG,"tes"+tet);
                while(tet != '0') {

                    try {
                        Mess = ((MainActivity) getActivity()).messageComplete;
                    }catch (NullPointerException e){ //Necessary because when turn off the server BT, appear and NullPointerException

                    }
                    tet = Mess.charAt(0);

                }
                  /*try {
                      ((MainActivity)getActivity()).receive();
                  } catch (IOException e) {
                      e.printStackTrace();
                  }*/

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {

                        Log.e(TAG,"te1"+Mess);
                        tv.setText(Mess);
                        progressBar.dismiss();

                    }
                });
            }
        }).start();
    }
}