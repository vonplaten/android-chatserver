package com.example.casi.uppg4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends MenuBase {

    TextView _error_msg;
    EditText editUsername, editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;
    ProgressBar progressBar;

    //NAMES
    public static final String ADRESS_KEY = "adress";
    public static final String PORT_KEY = "port";
    public static final String USERNAME_KEY = "username";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);


        //ELEMENTS
        editUsername = findViewById(R.id.userEditText);
        editTextAddress = (EditText) findViewById(R.id.addressEditText);
        editTextPort = (EditText) findViewById(R.id.portEditText);
        buttonConnect = (Button) findViewById(R.id.connectButton);
        buttonClear = (Button) findViewById(R.id.clearButton);
        _error_msg = (TextView) findViewById(R.id.responseTextView);
        progressBar = findViewById(R.id.progressbar);


        buttonClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                _error_msg.setText("");
            }
        });
    }

    public void connect(View v) {
        ProgressbarAsync progressbarAsync = new ProgressbarAsync(this);
        progressbarAsync.execute(2);
    }

    public void onProgressFinished() throws InterruptedException {
        Toast.makeText(this, "Finished", Toast.LENGTH_LONG).show();
        Thread.sleep(1000);
        Intent intent_chat = new Intent(this, ChatActivity.class);
        intent_chat.putExtra(MainActivity.ADRESS_KEY, editTextAddress.getText().toString());
        intent_chat.putExtra(MainActivity.PORT_KEY, Integer.parseInt(editTextPort.getText().toString()));
        intent_chat.putExtra(MainActivity.USERNAME_KEY, editUsername.getText().toString());
        this.startActivity(intent_chat);
    }


}



