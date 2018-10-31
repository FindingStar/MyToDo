package com.example.acer.todo.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.todo.R;
import com.example.acer.todo.adapter.RvAdapter;
import com.example.acer.todo.model.Affair;
import com.example.acer.todo.model.AffairDao;

import com.example.acer.todo.stroge.AffairDaoDao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RvFragment extends Fragment {

    private static final String TAG = "RvFragment";
    private static final String ARG_ID="fragmentId";

    private RecyclerView recyclerview;
    private ItemTouchHelper itemTouchHelper;
    private List<Affair>affairs=new ArrayList();
    private RvAdapter adapter;
    private int fragmentTag;

    private MainFragment mainFragment;
    private AddFragment addFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private Affair affair;
    /**
     * 一般地  就这么写，，在其他地方不需要直接new
     * activity调用创建实例，并且向该实例放置 信息，等待 该实例 获取
     */
    public static RvFragment newInstance(String fragmentId){
        Bundle args=new Bundle();
        args.putString(ARG_ID,fragmentId);

        RvFragment fragment=new RvFragment();
        fragment.setArguments(args);

        return fragment;
    }


    public void setFragmentTag(int fragmentTag) {
        this.fragmentTag = fragmentTag;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_rv,container,false);

        recyclerview=(RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerview.setLayoutManager(new GridLayoutManager(getActivity(),2));

        affairs= AffairDaoDao.getInstance().getAffairDao().queryBuilder().where(AffairDao.Properties.Belong.eq(fragmentTag)).build().list();
        Log.d(TAG, "onCreateView: 大小"+affairs.size());
        adapter=new RvAdapter(getContext(),affairs);
        recyclerview.setAdapter(adapter);



        itemTouchHelper=new ItemTouchHelper(new MyItemTouchHelperCallBack());
        itemTouchHelper.attachToRecyclerView(recyclerview);


        return view;
    }



    public class MyItemTouchHelperCallBack extends ItemTouchHelper.Callback{
//       滑动时间和拖动方向
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            } else {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }
//移动过程中调用
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//            获取原来的位置
             int fromPosition=viewHolder.getAdapterPosition();
//             获取目标位置
             int targetPosition=target.getAdapterPosition();
//             交换数据
             if (fromPosition>targetPosition){
                 for (int i = fromPosition; i >targetPosition ; i--) {
                     Collections.swap(affairs,i,i-1);
                 }
             }else {
                 for (int i = fromPosition; i <targetPosition ; i++) {
                     Collections.swap(affairs,i,i+1);
                 }
             }
             adapter.notifyItemMoved(fromPosition,targetPosition);
            return false;
        }
//这是滑动时调用的，这没有
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }
//    长按item时调用
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//            itemTouchHelper 未处于空闲状态
            if (actionState!=ItemTouchHelper.ACTION_STATE_IDLE){
//                选中的时候，设置背景色 高亮
                //viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            }
            super.onSelectedChanged(viewHolder,actionState);
        }
//手指松开时还原
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        affairs.clear();
    }


    /**
     * 根据view分发 实现item 点击。  官方没有提供直接的item点击方法，，因为很多时候 我们使用的是item内部控件的点击
     */
    public class MyRecyclerViewItemClickListener implements RecyclerView.OnItemTouchListener{

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
