package com.example.messanger;

import android.app.ActionBar;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private Button sendButton;
    private EditText chatbox;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<UserMessage> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        System.out.printf("User: %s\nFriend: %s\nPassword: %s\n", Constants.getCurrentUser(), Constants.getCurrentFriend(), Constants.getCurrentPassword());

        messages = new ArrayList<UserMessage>();
        new PopulateMessaages(messages).start();

        sendButton = (Button) findViewById(R.id.button_chatbox_send);
        chatbox = findViewById(R.id.edittext_chatbox);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_message_list);

        mAdapter = new MessageListAdapter(this, messages);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Handler h = new Handler(Looper.getMainLooper()) {
          @Override
          public void handleMessage(Message m) {
              mAdapter.notifyItemChanged(messages.size() - 1);
              recyclerView.scrollToPosition(messages.size() - 1);
          }
        };

        Thread messageReceiver = new MessageReceive(h);
        messageReceiver.start();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messages.add(new UserMessage(
                        chatbox.getText().toString(),
                        Constants.getCurrentUser(),
                        Constants.getCurrentFriend(),
                        Calendar.getInstance().getTime().getTime()
                ));
                mAdapter.notifyItemChanged(messages.size() - 1);
                recyclerView.scrollToPosition(messages.size() - 1);
                Thread t = new MessageSend(chatbox.getText().toString(), Constants.getCurrentFriend());
                t.start();
            }
        });
    }

    public class MessageSend extends Thread {
        private String message = null;
        private String receiver = null;

        MessageSend(String message, String receiver) {
            this.message = message;
            this.receiver = receiver;
        }

        @Override
        public void run() {
            String urlString = "http://" + Constants.SERVER_IP +
                               ":" + Constants.SERVER_PORT +
                                Constants.ADD_MESSAGE + "?message=" +
                                message.replaceAll(" ", "%20") + "&sender=" + Constants.getCurrentUser() +
                                "&receiver=" + receiver +
                                "&date=" + Calendar.getInstance().getTime().getTime();
            HttpURLConnection connection = null;
           try {
               URL url = new URL(urlString);
               connection = (HttpURLConnection) url.openConnection();
               connection.setRequestMethod("GET");
               BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
               String input;
               while((input = br.readLine()) != null) {
                   System.out.println(input);
               }
           } catch(Exception e) {
               e.printStackTrace();
           } finally {
               if(connection != null) connection.disconnect();
           }

        }
    }

    public class MessageReceive extends Thread {
        Handler h;

        MessageReceive(Handler h) {
            this.h = h;
        }

        @Override
        public void run() {
            String ip;
            int port;
            ServerSocket ss;

            /*Get the ip address of the phone*/
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());


            HttpURLConnection connection = null;
            try {
                ss = new ServerSocket(0);
                port = ss.getLocalPort();

                System.out.println("IP: " + ss.getLocalSocketAddress().toString());
                System.out.println("Port: " + ss.getLocalPort());
                String urlString = "http://" + Constants.SERVER_IP + ":" + Constants.SERVER_PORT +
                        Constants.LOGIN_USER +
                        "?username=" + Constants.getCurrentUser() +
                        "&url=" + ip + ":" + port;
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.getInputStream();
                connection.disconnect();

                while(true) {
                   System.out.println("Waiting for connection");
                    Socket client = ss.accept();
                    System.out.println("******************************************************************************************Accepted a connection******************************************************");
                    ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                    while(true) {
                        JSONObject json = new JSONObject((String) ois.readObject());
                        UserMessage newMessage = new UserMessage(
                                (String) json.get("message"),
                                (String) json.get("sender"),
                                (String) json.get("receiver"),
                                (Long) json.get("timeStamp")
                        );
                        messages.add(newMessage);
                    }
                }

            } catch (EOFException e) {
                h.sendEmptyMessage(0);
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) connection.disconnect();
            }

        }
    }

    public class PopulateMessaages extends Thread {
        List<UserMessage> messages;

        public PopulateMessaages(List<UserMessage> messages) {
            this.messages = messages;
        }

        @Override
        public void run() {
            ObjectInputStream ois = null;
            HttpURLConnection connection = null;
            String urlString = "http://" + Constants.SERVER_IP + ":" + Constants.SERVER_PORT +
                    Constants.GET_MESSAGES + "?username=" + Constants.getCurrentUser() +
                    "&friendName=" + Constants.getCurrentFriend();

            try {
                connection = (HttpURLConnection) new URL(urlString).openConnection();
                connection.setRequestMethod("GET");
                ois = new ObjectInputStream(connection.getInputStream());
                while(true) {
                    System.out.println("**********************************************************************************************************************");
                    String jsonString = (String) ois.readObject();
                    System.out.println(jsonString);
                    JSONObject json = new JSONObject(jsonString);
                    UserMessage newMessage = new UserMessage(
                            (String) json.get("message"),
                            (String) json.get("sender"),
                            (String) json.get("receiver"),
                            (Long) json.get("timeStamp")
                    );
                    messages.add(newMessage);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) connection.disconnect();
                try {
                   if(ois != null) ois.close();
                } catch (IOException e) {
                    System.out.println("Don't worry about this");
                }
            }
        }

    }
}
