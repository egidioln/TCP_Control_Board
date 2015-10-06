package com.example.lucas.es770_project.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.nio.Buffer;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Lucas on 06/08/2015.
 */
public class comClient extends AsyncTask<String, String, Integer> {

    Socket clientSocket;
    boolean running = true;
    String reff, meas= "asd" ;

    protected Integer doInBackground(String... params) { // IP, port, buffer
        String sAux;
        try {
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(params[0], Integer.parseInt(params[1])), 1000);
            meas = "551";
            //Thread.sleep(30, 0);
            Log.d("ES770", "Sck Created");
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            Log.d("ES770", "OutputStream created");

            final BufferedReader inFromServer = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
            Thread checkInput = new Thread(){
                public void run(){
                    while(running){
                        try {
                            meas = inFromServer.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            checkInput.start();

            reff=  params[2].toString();
            while(clientSocket.isConnected() && running) {
                Log.d("ES770", "Sending: " + reff);
                outToServer.writeBytes(reff);//params[2]
                Thread.sleep(100, 0);
            }
            Log.d("ES770", "Data Sent");

            //Thread.sleep(30, 0);

            clientSocket.close();
            Log.d("ES770", "Conn closed: "+params[2]);
        } catch (IOException e) {
            Log.d("ES770", "Envio falhou");
            e.printStackTrace();
            return 1;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    protected void onCancelled(){
        super.onCancelled();
        this.cancelTask();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (integer==1)
            Log.d("ES770", "Envio falhou");
    }
    public void cancelTask(){
        running=false;
        if(clientSocket!=null && clientSocket.isConnected())
            try {
                clientSocket.close();
                Log.d("ES770", "Cancelou");
            } catch (IOException e) {
                Log.d("ES770", "Cancelamento falou");
                e.printStackTrace();
            }

    }
    public void updateReff(String input){
        reff = input;

    }

    public String readMeas(){
        return new String(meas);


    }




}
