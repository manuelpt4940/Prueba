package com.example.usuario.prueba;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ////////
    String address = null;
    String deviceName = null;

    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private boolean connectionLost = false;
    final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private ProgressDialog progress;
    String readMessage;
    private double last_x = 0;
    private double last_y = 0;
    ///////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent newint = getIntent();
        deviceName = newint.getStringExtra(Devices.EXTRA_NAME);
        address = newint.getStringExtra(Devices.EXTRA_ADDRESS);


        //TextView statusView = (TextView)findViewById(R.id.status);
        //Button prender = (Button)findViewById(R.id.prender);

        //final View roundButton = (View)findViewById(R.id.roundButton);

        //statusView.setText("Connecting to " + deviceName);

        new ConnectBT().execute();

        //*********Agrega fragmento inicial **************/

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.Contenedor ,new FInicio()).commit();

        //*********Agrega fragmento inicial **************/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }




    public void  prenders(View v){
        send(buildMessage("1",0.5,0.5));
    }
    public void  apagars(View v){
        send(buildMessage("1",0,0));
    }


    private String buildMessage(String operation, double x, double y) {
        return (operation + "," + String.valueOf(x) + "," + String.valueOf(y) + "\n");
    }


    public void send(String message) {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write(message.getBytes());
            } catch (IOException e) {
                //  msg("Error : " + e.getMessage());
                if(e.getMessage().contains("Broken pipe")) Disconnect();
            }
        } else {
            //msg("Error : btSocket == null");
        }
    }

    public void receive() throws IOException {
        InputStream mmInputStream = btSocket.getInputStream();
        byte[] buffer = new byte[256];
        int bytes;

        try {
            bytes = mmInputStream.read(buffer);
            readMessage = new String(buffer, 0, bytes); //Se crea la variable global para poder acceder desde cualquier fragment
            //Log.d(TAG, "Received: " + readMessage);
            //TextView voltageLevel = (TextView) findViewById(R.id.Voltage);
            //voltageLevel.setText("Voltage level\n" + "DC " + readMessage + " V");
            //btSocket.close();
        } catch (IOException e) {
            //Log.e(TAG, "Problems occurred!");
            return;
        }
    }


    private void Disconnect() {
        if (btSocket!=null) {
            try {
                isBtConnected = false;
                btSocket.close();
            } catch (IOException e) {
                //msg("Error");
            }
        }
        Toast.makeText(getApplicationContext(),"Disconnected",Toast.LENGTH_LONG).show();
        finish();
    }

        /*private void msg(String message) {
            TextView statusView = (TextView)findViewById(R.id.status);
            statusView.setText(message);
        }*/

    //Clase que se compone de 4 momentos, pre, in, progress y post
    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainActivity.this, "Connecting", "Please wait...");  //show a progress dialog

        }

        @Override
        protected Void doInBackground(Void... devices) { //while the progress dialog is shown, the connection is done in background
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) { //after the doInBackground, it checks if everything went fine
            super.onPostExecute(result); //Super call the same method or class

            if (!ConnectSuccess) {
                Toast.makeText(getApplicationContext(), "Failed to connect", Toast.LENGTH_LONG).show();
                finish();
            } else {
                //msg("Connected to " + deviceName);
                isBtConnected = true;
                // start the connection monitor
                new MainActivity.MonitorConnection().execute();
            }
            progress.dismiss();
        }
    }

    private class MonitorConnection extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... devices) {
            while (!connectionLost) {
                try {
                    //read from the buffer, when this errors the connection is lost
                    // this was the only reliable way I found of monitoring the connection
                    // .isConnected didnt work
                    // BluetoothDevice.ACTION_ACL_DISCONNECTED didnt fire
                    btSocket.getInputStream().read();
                } catch (IOException e) {
                    connectionLost = true;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // if the bt is still connected, the connection must have been lost
            if (isBtConnected) {
                try {
                    isBtConnected = false;
                    btSocket.close();
                } catch (IOException e) {
                    // nothing doing, we are ending anyway!
                }
                Toast.makeText(getApplicationContext(), "Connection lost", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Disconnect();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            getSupportFragmentManager().beginTransaction().replace(R.id.Contenedor, new FInicio()).commit();
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public class Button extends AppCompatActivity {


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.fragment_finicio);

            Intent newint = getIntent();
            deviceName = newint.getStringExtra(Devices.EXTRA_NAME);
            address = newint.getStringExtra(Devices.EXTRA_ADDRESS);

            //TextView statusView = (TextView)findViewById(R.id.status);
            //Button prender = (Button)findViewById(R.id.prender);

            //final View roundButton = (View)findViewById(R.id.roundButton);

            //statusView.setText("Connecting to " + deviceName);

            new ConnectBT().execute();

           /* roundButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        pressed(roundButton, event);

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        released(roundButton, event);

                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        moved(roundButton, event);
                    }
                    return false;
                }
            });*/

        }

        public void  prenders(View v){
            send(buildMessage("1",0.5,0.5));
        }
        public void  apagars(View v){
            send(buildMessage("1",0,0));
        }


        private String buildMessage(String operation, double x, double y) {
            return (operation + "," + String.valueOf(x) + "," + String.valueOf(y) + "\n");
        }

        private void send(String message) {
            if (btSocket!=null) {
                try {
                    btSocket.getOutputStream().write(message.getBytes());
                } catch (IOException e) {
                    //  msg("Error : " + e.getMessage());
                    if(e.getMessage().contains("Broken pipe")) Disconnect();
                }
            } else {
                //msg("Error : btSocket == null");
            }
        }

        private void Disconnect() {
            if (btSocket!=null) {
                try {
                    isBtConnected = false;
                    btSocket.close();
                } catch (IOException e) {
                    //msg("Error");
                }
            }
            Toast.makeText(getApplicationContext(),"Disconnected",Toast.LENGTH_LONG).show();
            finish();
        }

        /*private void msg(String message) {
            TextView statusView = (TextView)findViewById(R.id.status);
            statusView.setText(message);
        }*/

        //Clase que se compone de 4 momentos, pre, in, progress y post
        private class ConnectBT extends AsyncTask<Void, Void, Void> {
            private boolean ConnectSuccess = true;

            @Override
            protected void onPreExecute() {
                progress = ProgressDialog.show(Button.this, "Connecting", "Please wait...");  //show a progress dialog
            }

            @Override
            protected Void doInBackground(Void... devices) { //while the progress dialog is shown, the connection is done in background
                try {
                    if (btSocket == null || !isBtConnected) {
                        myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                        BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                        btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        btSocket.connect();//start connection
                    }
                } catch (IOException e) {
                    ConnectSuccess = false;//if the try failed, you can check the exception here
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) { //after the doInBackground, it checks if everything went fine
                super.onPostExecute(result); //Super call the same method or class

                if (!ConnectSuccess) {
                    Toast.makeText(getApplicationContext(), "Failed to connect", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    //msg("Connected to " + deviceName);
                    isBtConnected = true;
                    // start the connection monitor
                    new Button.MonitorConnection().execute();
                }
                progress.dismiss();
            }
        }

        private class MonitorConnection extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... devices) {
                while (!connectionLost) {
                    try {
                        //read from the buffer, when this errors the connection is lost
                        // this was the only reliable way I found of monitoring the connection
                        // .isConnected didnt work
                        // BluetoothDevice.ACTION_ACL_DISCONNECTED didnt fire
                        btSocket.getInputStream().read();
                    } catch (IOException e) {
                        connectionLost = true;
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                // if the bt is still connected, the connection must have been lost
                if (isBtConnected) {
                    try {
                        isBtConnected = false;
                        btSocket.close();
                    } catch (IOException e) {
                        // nothing doing, we are ending anyway!
                    }
                    Toast.makeText(getApplicationContext(), "Connection lost", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
        /*@Override
            public void onBackPressed() {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    super.onBackPressed();
                }
            }*/
        @Override
        public void onBackPressed() {
            Disconnect();
        }
    }
}
