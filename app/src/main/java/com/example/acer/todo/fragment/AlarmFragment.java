package com.example.acer.todo.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.example.acer.todo.R;

public class AlarmFragment extends android.support.v4.app.DialogFragment {


//      fragmentmanager  会调用，  相当于之前的  fragment oncreate。。。等
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view= LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_dialog_time_pick,null);
//        第一个 参数可能有问题  -  实验了   没问题
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.date_pick_dialog_title)
//                button  第二个为监听器参数
                .setPositiveButton(android.R.string.ok,null)
                .create();
    }
}
