package com.example.casi.uppg4;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.net.Socket;

public class SocketFetcherAsync extends AsyncTask<String, Void, Socket> {

    private WeakReference<ChatActivity> _weakActivityContext;

    SocketFetcherAsync(ChatActivity strongRef){
        _weakActivityContext = new WeakReference<ChatActivity>(strongRef);
    }

    @Override
    protected Socket doInBackground(String... strings) {
        String adress = strings[0];
        Integer port = Integer.parseInt(strings[1]);
        Socket socket = null;
        try {
            socket = new Socket(adress, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return socket;
    }


    @Override
    protected void onPostExecute(Socket socket) {
        super.onPostExecute(socket);
        ChatActivity strongRef = _weakActivityContext.get();
        if(strongRef == null || strongRef.isFinishing()){
            return;
        }

        strongRef.onSocket(socket);    }

}
