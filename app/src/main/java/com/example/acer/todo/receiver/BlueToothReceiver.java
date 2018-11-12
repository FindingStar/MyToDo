package com.example.acer.todo.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.acer.todo.adapter.RvDeviceStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class BlueToothReceiver extends BroadcastReceiver {

    private RvDeviceStateAdapter mRvDeviceStateAdapter;
    private List<BluetoothDevice>devices=new ArrayList<>();
    private FinishTransformUpdateListener mFinishTransformUpdateListener;

    public void setFinishTransformUpdateListener(FinishTransformUpdateListener finishTransformUpdateListener) {
        mFinishTransformUpdateListener = finishTransformUpdateListener;
    }

    public BlueToothReceiver(RvDeviceStateAdapter stateAdapter){
        mRvDeviceStateAdapter=stateAdapter;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        switch (action){
            case BluetoothDevice.ACTION_FOUND:
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(context,"找到蓝牙设备"+device.getName(),Toast.LENGTH_SHORT).show();
                devices.add(device);
                mRvDeviceStateAdapter.addMessage(device.getName());
                mRvDeviceStateAdapter.addDevice(device);
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                Toast.makeText(context,"搜索结束",Toast.LENGTH_SHORT).show();
                mRvDeviceStateAdapter.addMessage("搜索结束");
                mFinishTransformUpdateListener.transFormUpdate(true,devices);
                break;
        }
    }

    public interface FinishTransformUpdateListener{
        void transFormUpdate(boolean isFinished,List<BluetoothDevice>devices);
    }
//    receiver  返回数据？
}
