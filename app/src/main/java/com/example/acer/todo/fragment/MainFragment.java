package com.example.acer.todo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.transition.Transition;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.acer.todo.R;
import com.example.acer.todo.adapter.RvPageFragmentAdapter;
import com.example.acer.todo.annotation.Bind;
import com.example.acer.todo.util.ViewBinder;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN;

public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private List<String> tabList=new ArrayList<>();
    private List<RvFragment>childRvFragmentList=new ArrayList<>();
    private String[]tabTitles=new String[]{
            "周一", "周二", "周三", "周四", "周五", "周六", "周日",
    };
    private AddFragment addFragment;
    private MainFragment mainFragment;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    @Bind(R.id.tablayout)
    private TabLayout tabLayout;
    @Bind(R.id.float_bt_main)
    private FloatingActionButton floatBt;
    @Bind(R.id.main_toolbar)
    private Toolbar toolBar;
    @Bind(R.id.viewpager)
    private ViewPager viewPager;
    private int currentPosition;

    private RvPageFragmentAdapter adapter;


    private CommunicationCallBack mCommunicationCallBack;

    public void setCommunicationCallBack(CommunicationCallBack communicationCallBack){
        mCommunicationCallBack=communicationCallBack;
    }
    public void postPosition(){
        mCommunicationCallBack.SendPosition(currentPosition);
    }

    public static MainFragment newInstance(){

        MainFragment mFragment=new MainFragment();

        return mFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolBar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.mipmap.b3);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setLogo(R.mipmap.ic_drawer_toolbar);

        Log.d(TAG, "onActivityCreated: hhh");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentManager=getActivity().getSupportFragmentManager();
        mainFragment=(MainFragment) fragmentManager.findFragmentByTag("MainFragment");

        initChildFragment();         //放在 onActivityCreated  就无效了？？？？？  fuck

        View view=inflater.inflate(R.layout.fragment_main,container,false);

        ViewBinder.bind(this,view);

        initView(view);

        return view;
    }

    private void initChildFragment(){
        for (int i=0;i<tabTitles.length;i++){
            tabList.add(tabTitles[i]);
            RvFragment rvFragment=RvFragment.newInstance(tabTitles[i]);          //这个实际上在这儿没用
            rvFragment.setFragmentTag(i);
            childRvFragmentList.add(rvFragment);
        }
    }

    private void initView(View view){

        setUpAdapter();
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));     //tab+viewpager   1........

        /**
         * 设置TabLayout
         *          1.已经不用setListener了，， 用add
         */
        tabLayout.setupWithViewPager(viewPager);                                     //2..............3.  是配置viewpager标题       这三步注意看看源码就好


        floatBt.setOnClickListener(new MyListener());

    }

    public  void setUpAdapter(){

        if (adapter==null){
            adapter=new RvPageFragmentAdapter(getChildFragmentManager(),childRvFragmentList,tabList);
            viewPager.setAdapter(adapter);
           // viewPager.setCurrentItem(currentPosition);

        }else {

        }
    }


    private class MyListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.float_bt_main:{

                    jumpToAddFragment();
                }
                case android.R.id.home:

            }
        }
    }

    /*
     * 用于 跳到 编辑任务 或者添加任务
     *  show  true 表示是  编辑任务  ，position，只用在编辑任务时有用，点击的位置
     */
    public void jumpToAddFragment(){

        if (addFragment==null){
            addFragment=new AddFragment();
        }
        int position=tabLayout.getSelectedTabPosition();

        addFragment.setCurrentPosition(position);
//                    一个事务代表一组fragment的改变，，
//                    addToBackStack（）   之后，，返回键  对应的返回债  就是这个，
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.hide(mainFragment);
        if (addFragment.isAdded()){
            fragmentTransaction.hide(mainFragment).show(addFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

        }else {

            fragmentTransaction.add(R.id.fragment_container,addFragment,"AddFragment");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.addToBackStack("AddFragment");
            fragmentTransaction.commit();

        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            refresh();
        }
    }

    public void refresh(){
        adapter.notifyDataSetChanged();
    }
    /**
     * fragment  通信接口
     */

    public interface CommunicationCallBack{
        void SendPosition(int currentPosition);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_toolbar_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{

                try {
//                    fragment 从activity 中获取控件实例
                    DrawerLayout drawerLayout=getActivity().findViewById(R.id.drawer_layout);
                    if (drawerLayout.isDrawerOpen(Gravity.LEFT)){
                        drawerLayout.closeDrawers();
                    }else
                        drawerLayout.openDrawer(Gravity.START);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
