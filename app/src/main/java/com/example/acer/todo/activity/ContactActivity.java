package com.example.acer.todo.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.acer.todo.R;
import com.example.acer.todo.R2;
import com.example.acer.todo.adapter.RvDeviceStateAdapter;
import com.example.acer.todo.adapter.RvMessageAdapter;
import com.example.acer.todo.model.CommunicationMessage;
import com.example.acer.todo.receiver.BlueToothReceiver;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.acer.todo.constants.MyMessageViewType;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener,
        RvDeviceStateAdapter.DataSocketUiUpdateListener,BlueToothReceiver.FinishTransformUpdateListener {

    private static final String TAG = "ContactActivity";

    private static int requestOpenBlueTooth=1;
    private BluetoothAdapter blueToothAdapter;
    private RvDeviceStateAdapter deviceStateAdapter;
    private RvMessageAdapter messageAdapter;
    private BlueToothReceiver blueToothReceiver;
    private String tempSendingMessage="";
    private ClientConnectThread mClientConnectThread;
    private ServAcceptThread mServAcceptThread;

    @BindView(R2.id.close)
    public Button closeBt;
    @BindView(R2.id.search)
    public Button searchBt;
    @BindView(R2.id.open)
    public Button openBt;
    @BindView(R2.id.edit_text)
    public EditText editTv;
    @BindView(R2.id.send)
    public Button sendBt;
    @BindView(R2.id.rv_devices)
    public RecyclerView rvDevices;
    @BindView(R2.id.rv_message)
    public RecyclerView rvMessages;
    @BindView(R2.id.navigation_view)
    public NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        popDialog();
        ButterKnife.bind(this);
        checkPermission();
        initUI();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){

                }else {
                    Toast.makeText(this,"拒绝了位置权限",Toast.LENGTH_SHORT).show();
                }
                break;
                default:
        }
    }

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(ContactActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ContactActivity.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
    }

    private void initUI(){
        rvDevices.setLayoutManager(new LinearLayoutManager(this));
        deviceStateAdapter=new RvDeviceStateAdapter(this);
        rvDevices.setAdapter(deviceStateAdapter);
        messageAdapter=new RvMessageAdapter(this);
        rvMessages.setAdapter(messageAdapter);
        openBt.setOnClickListener(this);
        closeBt.setOnClickListener(this);
        searchBt.setOnClickListener(this);
        editTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tempSendingMessage=s.toString();
            }
        });
        deviceStateAdapter.setDataSocketUiUpdateListener(this);

        if (mServAcceptThread!=null){
            rvDevices.setClickable(false);
        }
////        int groupId ,  int  itemId, int order,  chars title
//        navigationView.getMenu().add(1,1,1,"hhh");
//        navigationView.getMenu().add(1,2,2,"aaa");
//        navigationView.getMenu().add(2,1,1,"qqq");
    }

    private void popDialog(){
        final String[] status={"客户端","服务端"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_action_flag)
                .setTitle("请选择一个使用身份")
                .setItems(status, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 1:
                                mServAcceptThread=new ServAcceptThread();
                                mServAcceptThread.start();
                                break;
                            case 0:
                                break;
                        }
                    }
                });
        builder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.open:
                if(blueToothAdapter==null){
                    blueToothAdapter=BluetoothAdapter.getDefaultAdapter();
                }
                if (blueToothAdapter==null){
                    return;
                }
//                *** s神他么  这里 enable 是不行的，，必须这样子
                if (!blueToothAdapter.isEnabled()){
                    Log.d(TAG, "onClick: open");
                    deviceStateAdapter.addMessage("启动蓝牙");
                    Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent,requestOpenBlueTooth);
                }
                break;
            case R.id.search:
                blueToothAdapter.startDiscovery();
                registBlueToothReceiver();
                break;
            case R.id.send:
                CommunicationMessage communicationMessage=new CommunicationMessage();
                communicationMessage.setMessage(tempSendingMessage);
                communicationMessage.setType(MyMessageViewType.SEND);
                messageAdapter.addMessage(communicationMessage);
                if (mClientConnectThread!=null){
                    mClientConnectThread.sendMessage(tempSendingMessage);
                }else
                    mServAcceptThread.sendMessage(tempSendingMessage);
                break;
        }
    }

    private void registBlueToothReceiver(){
        blueToothReceiver=new BlueToothReceiver(deviceStateAdapter);
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(blueToothReceiver,intentFilter);

        blueToothReceiver.setFinishTransformUpdateListener(this);
    }


    @Override
    public void updateMessageData(BluetoothDevice bluetoothDevice) {
//        1.start（）方法来启动线程，真正实现了多线程运行。这时无需等待run方法体代码执行完毕，可以直接继续执行下面的代码；
//        1、写一个类继承自Thread类，重写run方法。用start方法启动线程
//2、写一个类实现Runnable接口，实现run方法。用new Thread(Runnable target).start()方法来启动
        mClientConnectThread=new ClientConnectThread(bluetoothDevice);
        mClientConnectThread.start();
    }

    @Override
    public void transFormUpdate(boolean isFinished, List<BluetoothDevice> devices) {
        updateNavDevicesUi(devices);
    }
    public void updateNavDevicesUi(List<BluetoothDevice>devices){
        for (int i = 0; i < devices.size(); i++) {
            navigationView.getMenu().add(i,i,i,devices.get(i).getName());
        }
    }

//    耗时  操作 放在拎一个线程

    public class ClientConnectThread extends Thread{

//        蓝牙设备  串口通信服务 UUID
        private String UUIDstr="00001101-0000-1000-8000-00805F9B34FB";
        private BluetoothDevice device;
        private boolean canConnect;

        private InputStream inputStream;
        private OutputStream outputStream;

        public ClientConnectThread(BluetoothDevice device){
            this.device=device;
            canConnect=true;
        }

        @Override
        public void run() {
            if (device!=null){
                try{
                    BluetoothSocket bluetoothSocket=device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(UUIDstr));
                    if (bluetoothSocket!=null){
//                        链接 不需要端口ip？  或者其他
                        bluetoothSocket.connect();
                    }

                    inputStream=bluetoothSocket.getInputStream();
                    outputStream=bluetoothSocket.getOutputStream();

                    BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));

                    String content=null;
                    while (canConnect){
                        content=reader.readLine();
                    }
//                    这样的话，每次发送接受数据 都会创建实例， 每条信息 对应一个 对象
                    CommunicationMessage recvednMessage=new CommunicationMessage();
                    recvednMessage.setMessage(content);
                    recvednMessage.setType(MyMessageViewType.RECV);
                    messageAdapter.addMessage(recvednMessage);

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        public void sendMessage(String message){
            if (outputStream!=null){

                try{
                    outputStream.write(message.getBytes());
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

        }
    }

    public class ServAcceptThread extends Thread{
        private String UUIDstr="00001101-0000-1000-8000-00805F9B34FB";
        private OutputStream outputStream;
        private InputStream inputStream;
        private BluetoothSocket socket;
        private boolean canAccept;

        public ServAcceptThread(){
            canAccept=true;
        }
        @Override
        public void run() {
            try{
//               核心
                BluetoothServerSocket serverSocket=blueToothAdapter.
                        listenUsingInsecureRfcommWithServiceRecord("Mon",UUID.fromString(UUIDstr));
                if (serverSocket!=null){
//                    核心2
                    socket=serverSocket.accept();
                    deviceStateAdapter.addMessage("有客户端链接");

                    outputStream=socket.getOutputStream();
                    inputStream=socket.getInputStream();

                    BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                    String content=null;
                    while (canAccept){
                        content=reader.readLine();
                    }
                    CommunicationMessage recvednMessage=new CommunicationMessage();
                    recvednMessage.setMessage(content);
                    recvednMessage.setType(MyMessageViewType.RECV);
                    messageAdapter.addMessage(recvednMessage);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        public void sendMessage(String message){
            if (outputStream!=null){
                try{
                    outputStream.write(message.getBytes());
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
