package com.example.messanger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChooseFriend extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<String> mFriends;
    private RecyclerView.Adapter mAdapter;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_friend);
        logoutButton = findViewById(R.id.logout_button);

        recyclerView = (RecyclerView) findViewById(R.id.friends_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFriends = new ArrayList<String>();

        /*Get the current users from the server*/
        new PopulateUsers(mFriends).start();

        mAdapter = new FriendsAdapter(mFriends, this, new ClickListener() {
            @Override
            public void onPositionClicked(int position) {
                Constants.setCurrentFriend(mFriends.get(position));
                Intent i = new Intent(getApplicationContext(), MessageActivity.class);
                startActivity(i);
            }
        });

        recyclerView.setAdapter(mAdapter);

        /*Logout button will logout the user from the server and go back to the login page*/
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LogoutThread().start();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                Constants.setCurrentFriend(null);
                Constants.setCurrentUser(null);
                startActivity(i);
            }
        });
    }

    public class LogoutThread extends Thread {
        @Override
        public void run() {
            HttpURLConnection connection;
            String urlString = "http://" + Constants.SERVER_IP + ":" + Constants.SERVER_PORT +
                    Constants.LOGOUT_USER + "?username=" + Constants.getCurrentUser();
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.getInputStream();
                connection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class PopulateUsers extends Thread {
        List<String> userNames;

        PopulateUsers(List<String> userNames) {
            this.userNames = userNames;
        }

        @Override
        public void run() {
            HttpURLConnection connection;
            String urlString = "http://" + Constants.SERVER_IP + ":" + Constants.SERVER_PORT +
                    Constants.GET_USERS + "?username=" + Constants.getCurrentUser();
            try {
                connection = (HttpURLConnection) new URL(urlString).openConnection();
                ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
                List<String> namesReceived = (List<String>) ois.readObject();
                userNames.addAll(namesReceived);
                connection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch(EOFException e) {
                System.out.println("No other users are currently using the app.");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
