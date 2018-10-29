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
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;
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




    public class VHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView title;
        private TextView article;
        private View deleteView;

        /*  构造方法  的目的就是 调用父类的 构造，把itemview实例 传进去，由父类操作
        public ViewHolder(View itemView) {
            super(itemView);
        }*/

        public VHolder (LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.rv_item,parent,false));

            title=(TextView)itemView.findViewById(R.id.title_item);
            article=(TextView)itemView.findViewById(R.id.article_item);
            image=itemView.findViewById(R.id.item_image);
            deleteView=itemView.findViewById(R.id.delete_view);

        }

    }
    @NonNull
    @Override
    public VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());

        Log.d(TAG, "onCreateViewHolder: 调用");
        return new VHolder(inflater,parent);
    }

    @Override
    public void onBindViewHolder(@NonNull final VHolder holder, int position) {

        Log.d(TAG, "onBindViewHolder:调用");

        holder.title.setText(affairs.get(position).getTitle());
        holder.article.setText(affairs.get(position).getArticle());
        holder.image.setImageBitmap(mBitmap);

        holder.deleteView.setTag(position);
        holder.deleteView.setOnClickListener(mOnClickListener);
        holder.itemView.setOnClickListener(mOnClickListener);
        mOnClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.delete_view:{
                        int pos= (int) holder.deleteView.getTag();
                        Affair affair=affairs.get(pos);
                        affairs.remove(pos);
                        AffairDaoDao.getInstance().getAffairDao().deleteByKey(affair.getId());
                        notifyItemRemoved(pos);
                    }
                    default:
                        Toast.makeText(mContext,"点击了itemview",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        return affairs.size();
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        mOnRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public interface OnRecyclerViewItemClickListener{
        void onClick(View view,int position);
    }
}