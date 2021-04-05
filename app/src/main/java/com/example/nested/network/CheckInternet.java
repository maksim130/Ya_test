package com.example.nested.network;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class CheckInternet extends AsyncTask<Void, Void, Void> {
  public   boolean  isOnline = true;

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();
            isOnline = true;

        } catch (IOException e) {
            isOnline=false;

        }
        return null;
    }

}