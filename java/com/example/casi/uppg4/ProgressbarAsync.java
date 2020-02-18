package com.example.casi.uppg4;

import android.os.AsyncTask;
import android.view.View;

import java.lang.ref.WeakReference;

public class ProgressbarAsync extends AsyncTask<Integer, Integer, String> {

    private WeakReference<MainActivity> _weakActivityContext;

    ProgressbarAsync(MainActivity strongActivityContext){
        _weakActivityContext = new WeakReference<MainActivity>(strongActivityContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        MainActivity strongRef = _weakActivityContext.get();
        if(strongRef == null || strongRef.isFinishing()){
            return;
        }
        strongRef.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(Integer... integers) {
        String err_msg = "Connecting";
        for (int i=0; i<integers[0]+1; i++){
            publishProgress((i*100)/integers[0]);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return err_msg;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        MainActivity strongRef = _weakActivityContext.get();
        if(strongRef == null || strongRef.isFinishing()){
            return;
        }
        strongRef.progressBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String err_msg) {
        super.onPostExecute(err_msg);
        MainActivity strongRef = _weakActivityContext.get();
        if(strongRef == null || strongRef.isFinishing()){
            return;
        }

        strongRef.progressBar.setProgress(0);
        strongRef.progressBar.setVisibility(View.INVISIBLE);

        try {
            strongRef.onProgressFinished();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
