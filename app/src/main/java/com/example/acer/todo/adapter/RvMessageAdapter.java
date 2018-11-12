package com.example.acer.todo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.acer.todo.R;
import com.example.acer.todo.constants.MyMessageViewType;
import com.example.acer.todo.model.CommunicationMessage;

import java.util.ArrayList;
import java.util.List;

public class RvMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<CommunicationMessage>messageList=new ArrayList<>();

    public RvMessageAdapter(Context context) {
        mContext=context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(mContext);
        RecyclerView.ViewHolder viewHolder=null;
        switch (viewType){
            case MyMessageViewType.RECV:
                viewHolder=new RecvedMessageHolder(inflater.inflate(R.layout.rv_message_item_recv,viewGroup,false));
                break;
            case MyMessageViewType.SEND:
                viewHolder=new SendingMessageHolder(inflater.inflate(R.layout.rv_message_item_send,viewGroup,false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()){
            case MyMessageViewType.RECV:
                ((RecvedMessageHolder)viewHolder).bindRecved(messageList.get(position).getMessage());
                break;
            case MyMessageViewType.SEND:
                ((SendingMessageHolder)viewHolder).bindSending(messageList.get(position).getMessage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getType();
    }
//  与之前不同的是，这 多了type  传入的是类对象，
    public void addMessage(CommunicationMessage communicationMessage){
        messageList.add(communicationMessage);
        notifyItemInserted(messageList.size()-1);
    }
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class RecvedMessageHolder extends RecyclerView.ViewHolder{

        private TextView recvedTv;

        public RecvedMessageHolder(View itemView) {
            super(itemView);
            recvedTv=itemView.findViewById(R.id.recved_tv);
        }
        public void bindRecved(String message){
            recvedTv.setText(message);
        }
    }
    class SendingMessageHolder extends RecyclerView.ViewHolder{
        private TextView sendingTv;

        public SendingMessageHolder(@NonNull View itemView) {
            super(itemView);
            sendingTv=itemView.findViewById(R.id.sending_tv);
        }
        public void bindSending(String message){
            sendingTv.setText(message);
        }
    }
}
