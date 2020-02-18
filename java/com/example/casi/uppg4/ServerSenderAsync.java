package com.example.casi.uppg4;

import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;

public class ServerSenderAsync extends AsyncTask<String, Void, String> {
    private WeakReference<ChatActivity> _weakActivityContext;

    ServerSenderAsync(ChatActivity strongRef){
        _weakActivityContext = new WeakReference<ChatActivity>(strongRef);
    }


    @Override
    protected String doInBackground(String... strings) {
        ChatActivity strongRef = _weakActivityContext.get();
        if(strongRef == null || strongRef.isFinishing()){
            return null;
        }
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(strongRef.socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String message = strings[0];
        pw.println(message);
        return message;
    }

    @Override
    protected void onPostExecute(String message) {
        super.onPostExecute(message);

        ChatActivity strongRef = _weakActivityContext.get();
        if(strongRef == null || strongRef.isFinishing()){
            return;
        }
        strongRef.onSent(message);
    }
}
