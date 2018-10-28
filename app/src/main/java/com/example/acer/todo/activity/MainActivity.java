package com.example.acer.todo.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toolbar;

import com.example.acer.todo.R;
import com.example.acer.todo.adapter.RvPageFragmentAdapter;
import com.example.acer.todo.annotation.Bind;
import com.example.acer.todo.fragment.AddFragment;

import com.example.acer.todo.fragment.MainFragment;
import com.example.acer.todo.fragment.RvFragment;
import com.example.acer.todo.model.Affair;

import com.example.acer.todo.stroge.DaoDbManager;
import com.example.acer.todo.util.PreferencesUtil;
import com.example.acer.todo.util.ViewBinder;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";
    @Bind(R.id.navigation_view)
    private NavigationView navigationView;
    @Bind(R.id.drawer_layout)
    private DrawerLayout drawerLayout;

    private boolean isNight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isNight=(boolean)PreferencesUtil.get(this,"isNight",false);

        /**
         * 这样的话， 点击item  然后调用这里 ，并不会 改变 成夜间 ，没用
         */
        getDelegate().setDefaultNightMode(isNight? Configuration.UI_MODE_NIGHT_YES:Configuration.UI_MODE_NIGHT_NO);

        if (Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_main);
        ViewBinder.bind(this);

        navigationView.setNavigationItemSelectedListener(this);

        DaoDbManager.get().init(this);


        android.support.v4.app.FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=manager.beginTransaction();
        MainFragment mainFragment=MainFragment.newInstance();


//      添加标识，，便于fragmentTransaction使用
        fragmentTransaction.add(R.id.fragment_container,mainFragment,"MainFragment");

//        fragmentTransaction.addToBackStack(null);      //    将该事务添加到返回栈,  第一个不用添加
        fragmentTransaction.commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.drawer_item_night:{
                int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//                getDelegate().setLocalNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ?
//                        AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                if (currentNightMode==Configuration.UI_MODE_NIGHT_NO){
                    isNight=true;
                }else {
                    isNight=false;
                }

                PreferencesUtil.put(this,"isNight",isNight);

                Log.d(TAG, "onNavigationItemSelected: yes"+AppCompatDelegate.MODE_NIGHT_YES);

                Log.d(TAG, "onNavigationItemSelected:当前模式 "+currentNightMode);

//                finish();
//                startActivity(new Intent(this,MainActivity.class));
//                overridePendingTransition(R.anim.animo_alph_close, R.anim.animo_alph_close);

            }
        }
        return true;
    }

}
