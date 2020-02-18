package com.example.casi.uppg4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;

public class ChatActivity extends MenuBase {


    ListView chatWindow;

    EditText inputForm;
    Button sendButton;

    String adress;
    Integer port;
    MemberData memberData;

    Socket socket;


    ServerListenerThread server_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_chat);
        super.onCreate(savedInstanceState);

        inputForm = findViewById(R.id.input_form);
        sendButton = findViewById(R.id.sendbutton);

        adress = getIntent().getExtras().getString(MainActivity.ADRESS_KEY);
        port = getIntent().getExtras().getInt(MainActivity.PORT_KEY);
        this.memberData = new MemberData(getIntent().getExtras().getString(MainActivity.USERNAME_KEY));

        this.chatWindow  = (ListView) findViewById(R.id.chat_window);
        this.chatWindow.setAdapter(new MessageAdapter(this));

        SocketFetcherAsync sf = new SocketFetcherAsync(this);
        sf.execute(adress, port.toString()); //runs onSocket()
    }


    public void onSocket(Socket socket) {
        this.socket = socket;
        String info = String.format(
                String.format("socket established to:\n %s:%s \n", adress, port));
        this.onMessage(info, this.memberData,true);
        this.onMessage(String.format("Logging in user:\n %s \n", this.memberData.getName()), this.memberData, true);


        this.server_listener = new ServerListenerThread(this);
        this.server_listener.start();

        String loginMess = "LOGIN " +this.memberData.getName();
        this.onMessage(String.format("Sending message:\n '%s'", loginMess), this.memberData, true);
        this.serverSend(loginMess);

    }

    public void onMessage(String text, MemberData memberData, boolean belongsToCurrentUser){
        final Message message = new Message(text, memberData, belongsToCurrentUser);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MessageAdapter)chatWindow.getAdapter()).add(message);
                chatWindow.setSelection(chatWindow.getCount() - 1);
            }
        });
    }
    private void serverSend(String message){
        new ServerSenderAsync(this).execute(message);
    }

    public void sendButtonClick(View view) {
        this.serverSend(this.inputForm.getText().toString());
    }

    public void please_quit(){
        Toast.makeText(this, "No answer", Toast.LENGTH_LONG).show();
        try {
            this.socket.close();
            Thread.sleep(2000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //this.finish();
    }

    public void onReceived(String message, MemberData memberData){
        this.onMessage(message, memberData,false);
    }
    public void onSent(String message) {
        this.onMessage(message, this.memberData,true);
        //Toast.makeText(this, String.format("Sent: %s",message), Toast.LENGTH_LONG).show();
        this.inputForm.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_internet:
                Intent intent_browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.se"));
                this.startActivity(intent_browser);
                break;
            case R.id.menuitem_disconnect:
                Toast.makeText(this, "Closing...", Toast.LENGTH_LONG).show();
                try {
                    this.socket.close();
                    Thread.sleep(2000);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.server_listener.please_quit();
                this.finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
