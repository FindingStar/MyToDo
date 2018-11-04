package com.example.acer.todo.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import com.example.acer.todo.R;
import com.example.acer.todo.databinding.ActivityContactBinding;

public class ContactActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
    }

    private void initUI(){
        ActivityContactBinding activityContactBinding=DataBindingUtil.setContentView(this,R.layout.activity_contact);
        activityContactBinding
    }
}
