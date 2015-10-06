package com.example.lucas.es770_project;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;


import com.example.lucas.es770_project.util.comClient;

import java.util.Timer;
import java.util.TimerTask;


public class ES770_control extends Activity {

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    Timer timer;
    TimerTask timerTask;





    static public String v1, v2;
    /* Accel */
    private boolean b_accel_stream = false;
    private String sDestIp,sDestPort;
    private String sCtrLY,sCtrRY;
    private View ctrL, ctrR;
    private static final int iY_OFFSET = 500;
    private static final int iY_SIZE = 530;
    private static final double dGRAVITY = 9.9;
    private int iV1,iV2;
    double dSensorOffset, dRotRef;
    TextView t_accel_data;
    TextView tCtrL_data ;
    TextView tRespField ;
    Switch swiMode  ;
    TextView tCtrR_data ;
    comClient mServerClient;

    int iTimerPeriod = 100;

    EditText edIp ;
    EditText edPort ;
    ToggleButton b1 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // accel.registerListener()
        setContentView(R.layout.activity_es770_control);

        StrictMode.setThreadPolicy(policy);
        t_accel_data = (TextView) findViewById(R.id.accel_data);
        tCtrL_data = (TextView) findViewById(R.id.ctrL_data);
        swiMode  = (Switch) findViewById(R.id.switch1);
        tCtrR_data = (TextView) findViewById(R.id.ctrR_data);
        ctrL = findViewById(R.id.ctrL);
        ctrR = findViewById(R.id.ctrR);
        tRespField = (TextView) findViewById(R.id.textResp);
        sCtrRY ="0";
        iV1 = 0;
        iV2 = 0;
        sCtrLY = "0";

        edIp = (EditText) findViewById(R.id.etIp);
        edPort = (EditText)  findViewById(R.id.etPort);
        b1 = (ToggleButton) findViewById(R.id.toggleButton);
        b1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked == true) {
                    b_accel_stream = true;
                    sDestIp = edIp.getText().toString();
                    edIp.setEnabled(false);
                    sDestPort = edPort.getText().toString();
                    edPort.setEnabled(false);
                    if(mServerClient.getStatus() == AsyncTask.Status.RUNNING) {
                        Log.d("ES770","Ainda rodando...");
                        mServerClient.cancel(true);
                    }
                    mServerClient = new comClient();
                    mServerClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, edIp.getText().toString(), edPort.getText().toString(), "550\n");
                } else {
                    b_accel_stream = false;
                    edIp.setEnabled(true);
                    edPort.setEnabled(true);
                    mServerClient.cancel(true);
                }
            }
        });
        edIp.setEnabled(true);
        edPort.setEnabled(true);


        ctrL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                iV1 = (int) -(event.getY() - iY_OFFSET);
                sCtrLY = String.valueOf(-(event.getY() - iY_OFFSET) / iY_SIZE);
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    sCtrLY = String.valueOf(0.0);
                    iV1=0;
                }
                return true;
            }
        });

        ctrR.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                iV2 = (int) -(event.getY() - iY_OFFSET);
                sCtrRY = String.valueOf(-(event.getY() - iY_OFFSET) / iY_SIZE);
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    sCtrRY = String.valueOf(0.0);
                    iV2 = 0;
                }
                return true;
            }
        });




        final SensorManager accel_manag = (SensorManager) getSystemService(this.SENSOR_SERVICE);

        final Sensor s_accel = accel_manag.getDefaultSensor(Sensor.TYPE_GRAVITY);




        accel_manag.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                dRotRef = event.values[1] / dGRAVITY;

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, s_accel, 200000, 0);
//        timer = new Timer();
//        refresher = new TimerTask() {
//            public void run() {
//                handler.sendEmptyMessage(REFRESH);
//            };
//        };
// first event immediately,  following after 1 seconds each
        //timer.scheduleAtFixedRate(refresher, 0, 1000);
        mServerClient = new comClient();
    }

    @Override

    protected void onResume() {
        super.onResume();
        //onResume we start our timer so it can start when the app comes from the background
        startTimer();
    }


    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service

    }

    @Override
    protected void onStart(){
        super.onStart();

        // Bind to LocalService


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }

    public void sendData(){
        if (b_accel_stream) {
            char v1,v2,mode;
            if(swiMode.isChecked()) {
                mode = '1';
                if(iV1>0)
                    v1 = '9';
                else if(iV1==0)
                    v1= '5';
                else
                    v1 = '1';
                if(iV2>0)
                    v2 ='9';
                else if(iV2==0)
                    v2= '5';
                else
                    v2 = '1';
            }
            else {
                mode = '0';
                if(iV1>0)
                    v1 = '9';
                else if(iV1==0)
                    v1= '5';
                else
                    v1 = '1';
                if(iV2>0)
                    v2 ='9';
                else if(iV2==0)
                    v2= '5';
                else
                    v2 = '1';
            }

            String sDataString = String.valueOf(dRotRef);
            t_accel_data.setText("V: " + String.valueOf(dRotRef) );
            tCtrL_data.setText("L: " + sCtrLY);
            tCtrR_data.setText("R: " + sCtrRY);

            String output =String.valueOf(""+v1 +"" + v2+"" +mode+"\n");

            //Log.d("ES770", "Sending: " + output);
            //new comClient().execute(edIp.getText().toString(), edPort.getText().toString(), output);
            mServerClient.updateReff(output);
            tRespField.setText(mServerClient.readMeas());
         //   Socket clientSocket;
            //1tcpService.performSend(edIp.getText().toString(), edPort.getText().toString(), output);
        }



    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 200, iTimerPeriod); //
    }

    public void stoptimertask(View v) {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    Handler handler = new Handler();
    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp

                        sendData();
                    }
                });
            }
        };
    }



    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */



    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */

}

