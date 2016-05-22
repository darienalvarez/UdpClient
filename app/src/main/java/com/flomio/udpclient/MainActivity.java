package com.flomio.udpclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final int SERVER_PORT = 10001;

    public static final String MESSAGE_RECEIVED = "message_received";
    public static final String MESSAGE_STRING = "message_string";

    public static Context CONTEXT;

    public UdpClient udpClient;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(MainActivity.MESSAGE_STRING);
            Log.i("MESSAGE", message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.CONTEXT = this;
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(receiver, new IntentFilter(MainActivity.MESSAGE_RECEIVED));

        UdpClient udpClient = new UdpClient();
        udpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, SERVER_PORT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (udpClient != null) {
            udpClient.stopUdpClient();
        }
        this.udpClient = null;

        unregisterReceiver(receiver);
    }

}
