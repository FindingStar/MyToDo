package com.example.acer.todo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.todo.R;
import com.example.acer.todo.model.Affair;
import com.example.acer.todo.model.AffairDao;
import com.example.acer.todo.stroge.AffairDaoDao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.VHolder>{

    private static final String TAG = "RvAdapter";
    private Bitmap mBitmap;
    private Context mContext;
    private List<Affair>affairs=new ArrayList<>();

    private View.OnClickListener mOnClickListener;

    public RvAdapter(Context context, List<Affair>affairs){
        mContext=context;
        this.affairs=affairs;

        try {
            InputStream inputStream=mContext.getResources().getAssets().open("bg_tree_min.jpg");
            mBitmap= BitmapFactory.decodeStream(inputStream);
            if (mBitmap==null){
                Log.d("rvadapter","bitmap 为空");
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }




    public class VHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView image;
        private TextView title;
        private TextView article;
        private View deleteView;
        private Affair affair;
        private int position;

        private RvHolderClick mRvHolderClickListener;

        /*  构造方法  的目的就是 调用父类的 构造，把itemview实例 传进去，由父类操作
        public ViewHolder(View itemView) {
            super(itemView);
        }*/

        public VHolder (LayoutInflater inflater, ViewGroup parent,RvHolderClick rvHolderClick){
            super(inflater.inflate(R.layout.rv_item,parent,false));

            mRvHolderClickListener=rvHolderClick;

            title=(TextView)itemView.findViewById(R.id.title_item);
            article=(TextView)itemView.findViewById(R.id.article_item);
            image=itemView.findViewById(R.id.item_image);
            deleteView=itemView.findViewById(R.id.delete_view);

//            和之前不易样的是 之前是在bindviewholder 注册监听
            deleteView.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        public void  bind(Affair affair,int position){
            title.setText(affair.getTitle());
            article.setText(affair.getArticle());
            image.setImageBitmap(mBitmap);
//            deleteView.setTag(affair);

            this.position=position;
            this.affair=affair;
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.delete_view:{
                    mRvHolderClickListener.deleteViewClick(affair,position);
//                    神奇的 一批！！  加了break 就 只正确，， 否则 点击deleteview的时候 这两个路都会走
                    break;
                }
                default:
                    mRvHolderClickListener.itemViewClick();
                    break;
            }
        }
    }
    @NonNull
    @Override
    public VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());

        Log.d(TAG, "onCreateViewHolder: 调用");
        return new VHolder(inflater, parent, new RvHolderClick() {
            @Override
            public void itemViewClick() {

                Toast.makeText(mContext,"点击了itemview",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deleteViewClick(Affair affair,int pos) {

                        affairs.remove(affair);
                        AffairDaoDao.getInstance().getAffairDao().deleteByKey(affair.getId());
                        notifyItemRemoved(pos);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull final VHolder holder, int position) {

        Log.d(TAG, "onBindViewHolder:调用");

        Affair affair=affairs.get(position);

        holder.bind(affair,position);

    }

    @Override
    public int getItemCount() {
        return affairs.size();
    }


    public interface RvHolderClick{
        void itemViewClick();
        void deleteViewClick(Affair affair,int pos);
    }

}