package com.example.acer.todo.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.acer.todo.constants.MyMessageViewType;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener,
        RvDeviceStateAdapter.DataSocketUiUpdateListener,BlueToothReceiver.FinishTransformUpdateListener{

    private static final String TAG = "ContactActivity";

    private static int requestOpenBlueTooth=1;
    private BluetoothAdapter blueToothAdapter;
    private RvDeviceStateAdapter deviceStateAdapter;
    private RvMessageAdapter messageAdapter;
    private BlueToothReceiver blueToothReceiver;
    private String tempSendingMessage="";
    private ClientConnectThread mClientConnectThread;
    private ServAcceptThread mServAcceptThread;
    private BluetoothSocket clientBluetoothSocket;
    private BluetoothServerSocket serverSocket;

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
//        当时忘记写了  通过debug  会提示错误
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
//        设置自动 滚到 最下面
        linearLayoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(linearLayoutManager);
        rvMessages.setAdapter(messageAdapter);
        openBt.setOnClickListener(this);
        closeBt.setOnClickListener(this);
        searchBt.setOnClickListener(this);
        deviceStateAdapter.setDataSocketUiUpdateListener(this);
        sendBt.setOnClickListener(this);

        rvDevices.setClickable(false);


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
                                Log.d(TAG, "onClick: 1");
                                break;
                            case 0:
                                Log.d(TAG, "onClick: 0");
                                rvDevices.setClickable(true);
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
                popDialog();
                break;
            case R.id.search:
                blueToothAdapter.startDiscovery();
                registBlueToothReceiver();
                break;
            case R.id.send:
                tempSendingMessage=editTv.getText().toString();
                CommunicationMessage communicationMessage=new CommunicationMessage();
                communicationMessage.setMessage(tempSendingMessage);
                communicationMessage.setType(MyMessageViewType.SEND);
                messageAdapter.addMessage(communicationMessage);
                if (mClientConnectThread!=null){
                    mClientConnectThread.sendMessage(tempSendingMessage);
                }else
                    mServAcceptThread.sendMessage(tempSendingMessage);
                editTv.getText().clear();
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
        private PrintWriter printWriter;

        public ClientConnectThread(BluetoothDevice device){
            this.device=device;
            canConnect=true;
        }

        @Override
        public void run() {
            if (device!=null){
                try{
                    BluetoothSocket temp=device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(UUIDstr));
                    clientBluetoothSocket=temp;
                    if (clientBluetoothSocket!=null){
//                        链接 不需要端口ip？  或者其他
                        clientBluetoothSocket.connect();
                        Log.d(TAG, "run: 连接到 服务端 connect ");

                    }
                    String content=null;
                    try{
                        inputStream=clientBluetoothSocket.getInputStream();
                        outputStream=clientBluetoothSocket.getOutputStream();

                        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));

                        while ((content=reader.readLine())!=null&&content.length()!=0){
                            Log.d(TAG, "run: 收到消息："+content);
                            final CommunicationMessage recvednMessage=new CommunicationMessage();
                            recvednMessage.setMessage(content);
                            recvednMessage.setType(MyMessageViewType.RECV);
                            ContactActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageAdapter.addMessage(recvednMessage);
                                }
                            });
                        }

                    }catch (IOException e){
                        e.printStackTrace();
                    }

//                    这样的话，每次发送接受数据 都会创建实例， 每条信息 对应一个 对象

                }catch (IOException e){
                    e.printStackTrace();
                }finally {
                    try{
                        if (clientBluetoothSocket!=null){
                            clientBluetoothSocket.close();
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                }
            }
        }
        public void sendMessage(String message){
            if (outputStream!=null){

//                神了 我去 ，， outputStream.write 对面是接受不到的
                try{
                    if (printWriter==null){
                        printWriter=new PrintWriter(new OutputStreamWriter(outputStream,"UTF-8"),true);
                    }
                    printWriter.println(message);
                    Log.d(TAG, "sendMessage: 已经写入 字节流");
                }catch (IOException e){
                    e.printStackTrace();
                    printWriter.close();
                    Log.d(TAG, "sendMessage: "+e.getMessage());
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
        private PrintWriter printWriter;

        public ServAcceptThread(){
            canAccept=true;
        }
        @Override
        public void run() {

            if (blueToothAdapter==null)

                return;
            Log.d(TAG, "run: blueToothAdapter不为空");
            try{
//               核心
                BluetoothServerSocket temp=blueToothAdapter.
                        listenUsingInsecureRfcommWithServiceRecord("Mon",UUID.fromString(UUIDstr));
                serverSocket=temp;

                if (serverSocket!=null){
//                    核心2

                    Log.d(TAG, "run: 111");

                    socket=serverSocket.accept();
                    if (socket==null){
                        Log.d(TAG, "run:服务端未接受到 设备连接");
                    }

                    Log.d(TAG, "run: 接受到");
//                    这个需要在主线程中更新的   在这纠结了 一上午  没找着
                    ContactActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            deviceStateAdapter.addMessage("有客户端链接");

                        }
                    });
                    outputStream=socket.getOutputStream();
                    inputStream=socket.getInputStream();

                    if (inputStream!=null){
                        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                        String content=null;

                        while ((content=reader.readLine())!=null&&content.length()!=0){
                            Log.d(TAG, "run: 收到消息："+content);
                            final CommunicationMessage recvednMessage=new CommunicationMessage();
                            recvednMessage.setMessage(content);
                            recvednMessage.setType(MyMessageViewType.RECV);
                            ContactActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageAdapter.addMessage(recvednMessage);
                                }
                            });
                        }

                    }
                }
            }catch (IOException e){
                e.printStackTrace();
                Log.d(TAG, "run: "+e.toString());
            }finally {
                try{
                    if (serverSocket!=null){
                        serverSocket.close();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(String message){
            if (outputStream!=null){
                try{
                    if (printWriter==null){
                        printWriter=new PrintWriter(new OutputStreamWriter(outputStream,"UTF-8"),true);
                    }
                    printWriter.println(message);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(blueToothReceiver);
        blueToothAdapter.disable();
    }

//    每次点击  都会判断是否在 editText 内 ，判断是否需要 收起软键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction()==MotionEvent.ACTION_DOWN){
            View v=getCurrentFocus();
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean isShouldHideEditText(){

        return true;
    }
}
