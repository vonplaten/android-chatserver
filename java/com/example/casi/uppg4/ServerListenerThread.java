package com.example.casi.uppg4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ServerListenerThread extends Thread {
    private WeakReference<ChatActivity> _weakActivityContext;
    private boolean quit = false;
    private final ArrayList<MemberData> members;

    public ServerListenerThread(ChatActivity strongRef) {
        _weakActivityContext = new WeakReference<ChatActivity>(strongRef);
        members = new ArrayList<MemberData>();
        members.add(new MemberData("server"));
    }

    public void run() {
        BufferedReader from_server = null;
        try {
            if (_weakActivityContext.get() != null || _weakActivityContext.get().isFinishing())
                from_server = new BufferedReader(new InputStreamReader(_weakActivityContext.get().socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (! quit) {
            ChatActivity strongRef = _weakActivityContext.get();
            if(strongRef == null || strongRef.isFinishing()){
                return;
            }

            final String line_from_server;
            try {
                line_from_server = from_server.readLine();
                if (line_from_server == null) {
                    _weakActivityContext.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            _weakActivityContext.get().onReceived(
                                    "Fick inga data från servern\n",
                                    members.get(0)
                            );
                        }
                    });
                    quit = true;
                }
                else {
                    _weakActivityContext.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MemberData member = null;
                            String name = line_from_server.substring(0, line_from_server.indexOf(":"));
                            String message = line_from_server.substring(line_from_server.indexOf(":")+1);

                            //MEMBER
                            if (name.equals("Du skrev")) {
                                member = members.get(0);
                            }
                            else {
                                for (MemberData mb : members){
                                    if (name.equals(mb.getName())) {
                                        member = mb;
                                        break;
                                    }
                                }
                                //No user found
                                if (member == null) {
                                    member = new MemberData(name);
                                    members.add(member);
                                }
                            }

                            _weakActivityContext.get().onReceived(
                                    String.format("%s\n", message),
                                    member
                            );

                        }
                    });
                }
            }
            catch (IOException e) {
                if (_weakActivityContext.get() != null || _weakActivityContext.get().isFinishing())
                    _weakActivityContext.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            _weakActivityContext.get().onReceived(
                                    "Fel vid mottagning från servern\n",
                                    members.get(0)
                            );
                        }
                    });
                quit = true;
            }

        } // while

        if (_weakActivityContext.get() != null || _weakActivityContext.get().isFinishing())
            _weakActivityContext.get().please_quit();
    } // run

    public void please_quit() {
        quit = true;
    }
}
