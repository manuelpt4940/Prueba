package com.example.usuario.prueba;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;


/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FInicio.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FInicio#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FInicio extends Fragment {
    Button button;
    TextView tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finicio, container, false);
        // Inflate the layout for this fragment
        button = view.findViewById(R.id.butt);
        tv = view.findViewById(R.id.tv1);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).send("Hola"); //Llamar un método del mainActivity
                ((MainActivity)getActivity()).send("Hola2");
                try {
                    ((MainActivity)getActivity()).receive();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                String Mess=((MainActivity)getActivity()).readMessage;  //Obtener una variable desde el Activity
                tv.setText(Mess);


            }
        });
        return view;
    }

}