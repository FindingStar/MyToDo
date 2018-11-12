package com.example.acer.todo.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.todo.R;

import java.util.ArrayList;
import java.util.List;


public class RvDeviceStateAdapter extends RecyclerView.Adapter<RvDeviceStateAdapter.DeviceStateHolder> {

    private static final String TAG = "RvDeviceStateAdapter";
    private Context mContext;
    private List<String>stateInfoList=new ArrayList<>();
    private List<BluetoothDevice>devices=new ArrayList<>();
    private DataSocketUiUpdateListener mDataSocketUiUpdateListener;

    public RvDeviceStateAdapter(Context context) {
        mContext=context;
    }

    @NonNull
    @Override
    public DeviceStateHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater=LayoutInflater.from(mContext);

        return new DeviceStateHolder(inflater.inflate(R.layout.rv_item_state,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceStateHolder deviceStateHolder, int i) {
        deviceStateHolder.bind(stateInfoList.get(i),i);
    }
    public void addDevice(BluetoothDevice device){
        devices.add(device);
    }
    public void addMessage(String stateMessage){
        stateInfoList.add(stateMessage);
        Log.d(TAG, "addMessage: ");
        notifyItemInserted(stateInfoList.size()-1);
    }
    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+stateInfoList.size());
        return stateInfoList.size();
    }

    class DeviceStateHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView stateTv;
        private TextView dateTv;
        private int position;

        public DeviceStateHolder(View itemView) {
            super(itemView);
            stateTv=itemView.findViewById(R.id.tv_device_state);
            dateTv=itemView.findViewById(R.id.tv_state_time);
            itemView.setOnClickListener(this);
        }
        public void bind(String state,int position){
            stateTv.setText(state);
            dateTv.setText("***");
            this.position=position;
        }

        @Override
        public void onClick(View v) {
//            之后优化，弹出 选择对化框
            Toast.makeText(mContext,"你将要链接设备"+stateTv.getText(),Toast.LENGTH_SHORT).show();

            mDataSocketUiUpdateListener.updateMessageData(devices.get(position));
        }
    }

//    链接，发送/接受数据，传给Activity 接口监听，接口方法内 使用RvMessageAdapter 进行 类似add 的ui更新

    public void setDataSocketUiUpdateListener(DataSocketUiUpdateListener dataSocketUiUpdateListener){
        mDataSocketUiUpdateListener=dataSocketUiUpdateListener;
    }

    public interface DataSocketUiUpdateListener{
        void updateMessageData(BluetoothDevice bluetoothDevice);
    }
}
