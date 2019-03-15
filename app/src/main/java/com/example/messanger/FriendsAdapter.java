package com.example.messanger;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter {
    private List<String> mFriendList;
    private Context mContext;
    private ClickListener mClickListener;

    FriendsAdapter(List<String> friendList, Context c, ClickListener clickListener) {
        this.mFriendList = friendList;
        mContext = c;
        mClickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return mFriendList == null ? 0 : mFriendList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        return new FriendHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_button, parent,false), mClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((FriendHolder)holder).bind(mFriendList.get(position));
    }

    private class FriendHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Button friendButton;
        private ClickListener listener;

        FriendHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            friendButton = itemView.findViewById(R.id.friend_button);
            listener = clickListener;
        }

        void bind(String friend) {
            friendButton.setText(friend);
            friendButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onPositionClicked(getAdapterPosition());
        }
    }
}
