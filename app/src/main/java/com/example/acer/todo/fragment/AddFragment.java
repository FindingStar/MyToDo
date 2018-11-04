package com.example.acer.todo.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.example.acer.todo.R;
import com.example.acer.todo.activity.MainActivity;
import com.example.acer.todo.model.Affair;
import com.example.acer.todo.model.AffairDao;
import com.example.acer.todo.service.AlarmService;
import com.example.acer.todo.stroge.AffairDaoDao;

import java.util.ArrayList;
import java.util.List;

public class AddFragment extends Fragment  {

    private static final String TAG = "AddFragment";
    private static final String TAG_ALARM_FRAGMENT="AlarmFragment";


    private CollapsingToolbarLayout collapsingTb;
    private android.support.v7.widget.Toolbar toolBar;
    private EditText titleEditText;
    private EditText articleEditText;
    private FloatingActionButton floatBt;
    private TimePicker timePicker;
    private TextInputLayout titleTextIl;
    private TextInputLayout contentTextIl;
    private int currentPosition;
    private List<Affair>mAffairs=new ArrayList<>();

    private MyClickListener myClickListener=new MyClickListener();

    private int hour;
    private int minute;

    private AlarmService.TimePickBinder mTimePickBinder;

    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mTimePickBinder= (AlarmService.TimePickBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    //    暂时无用了
    public static AddFragment newInstance(int position){
        Bundle args=new Bundle();
        args.putInt("currentPosition",position);

        AddFragment addFragment=new AddFragment();
        addFragment.setArguments(args);
        return addFragment;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*
        实现fragment 中支持 toolbar
         */
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolBar);
//        注意：只有设置了这个  ，home 返回键，和其他的自定义menu 才能起作用》
        setHasOptionsMenu(true);
        ActionBar tb=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (tb!=null){
            tb.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_add,container,false);
        initView(view);

        return view;
    }

    private void initView(View view){
        collapsingTb=(CollapsingToolbarLayout)view.findViewById(R.id.collapsing_toolbar_layout);
        collapsingTb.setTitle("添加任务");

        toolBar=view.findViewById(R.id.toolbar_fragment_add);

        titleEditText=(EditText)view.findViewById(R.id.title_edit_text) ;
        articleEditText=(EditText)view.findViewById(R.id.article_edit_text);

        floatBt=(FloatingActionButton)view.findViewById(R.id.float_bt_add);
        floatBt.setOnClickListener(myClickListener);

        titleTextIl=view.findViewById(R.id.text_il_title);
        contentTextIl=view.findViewById(R.id.text_il_content);

        timePicker=view.findViewById(R.id.time_picker);
        timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(myClickListener);
    }

    private class MyClickListener implements View.OnClickListener ,TimePicker.OnTimeChangedListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.float_bt_add:{
                    backHome();
                    Intent intent=new Intent(getContext(), AlarmService.class);
                    intent.putExtra("hour",hour);
                    intent.putExtra("minute",minute);
                    getContext().startService(intent);
                    break;
                }

            }
        }

        @Override
        public void onTimeChanged(TimePicker timePicker, int i, int i1) {
            minute=timePicker.getMinute();
            hour=timePicker.getHour();
        }
    }


    private void showAlarmFragment(){
//         加到了 主fragmentManager内，   因为fragment之间通信  规定需要经过activity，
//            所以不存在fragment的父子通信， 也就是当只是展示一个视图时，我们可以使用chidfragment
        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        AlarmFragment alarmFragment=new AlarmFragment();
        alarmFragment.show(fragmentManager,TAG_ALARM_FRAGMENT);
    }

    private boolean validateTitle(String titleText){
        return titleText.length()>2;
    }

    private boolean validateContent(String contentText){
        return contentText.length()>2;
    }

    private void backHome(){
        Affair affair=new Affair();
        affair.setTitle(titleEditText.getText().toString());
        affair.setArticle(articleEditText.getText().toString());
        affair.setBelong(currentPosition);
        AffairDaoDao.getInstance().getAffairDao().insert(affair);


        if (!validateTitle(titleEditText.getText().toString())){
            titleTextIl.setErrorEnabled(true);
            titleTextIl.setError("请输入长一点的标题啊");
        }else if (!validateContent(articleEditText.getText().toString())){
            contentTextIl.setErrorEnabled(true);
            contentTextIl.setError("内容太短了啊");
        }else {
            titleTextIl.setErrorEnabled(false);
            contentTextIl.setErrorEnabled(false);

            FragmentManager fragmentManager=getFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            MainFragment mainFragment=(MainFragment)fragmentManager.findFragmentByTag("MainFragment");
            AddFragment addFragment=(AddFragment)fragmentManager.findFragmentByTag("AddFragment");

            fragmentTransaction.hide(addFragment).show(mainFragment).commit();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            titleEditText.getText().clear();
            articleEditText.getText().clear();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                backHome();
                return true;

            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 点击item  复用 更新view
     */
    public void updateView(int position){
        collapsingTb.setTitle("编辑任务");

        mAffairs=AffairDaoDao.getInstance().getAffairDao().queryBuilder().where(AffairDao.Properties.Belong.eq(currentPosition)).build().list();

        titleEditText.setText(mAffairs.get(position).getTitle());

        articleEditText.setText(mAffairs.get(position).getArticle());
        Log.d(TAG, "updateView: "+mAffairs.get(position).getTitle());
    }


}
