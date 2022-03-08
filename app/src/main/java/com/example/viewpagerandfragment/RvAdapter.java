package com.example.viewpagerandfragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.help.Tip;

import java.util.ArrayList;
import java.util.List;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyViewHolder>{

    private RecyclerView rv;
    private List<Tip> list = new ArrayList<>();
    private Context context;
    private OnItemClickListener mOnItemClickListener;
/*
    @Override
    public void onClick(View view) {
        if(this.mOnItemClickListener != null){
            int position = 0;
            Tip tip = list.get(position);
            this.mOnItemClickListener.onItemClick(rv,view,position,tip);
        }

    }
*/
    //接口，在activity中设置具体的函数
    public interface OnItemClickListener {
        void onItemClick(RecyclerView parent, View view, int position, Tip data);
    }

    //构造函数
    public RvAdapter(Context context, RecyclerView rv, List<Tip> list) {
        this.context = context;
        this.rv = rv;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.mOnItemClickListener = clickListener;
    }

    //设置数据
    public void setData(List<Tip> list){
        if(list==null) return;
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();//RecyclerView提供的方法
    }

    //渲染UI
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item,parent,false);
        //view.setOnClickListener(this);//点击事件目前空白
        return new MyViewHolder(view);
    }

    //绑定数据，将viewholder中的数据进行具体的指定，position是具体的位置
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Tip tip = list.get(position);
        ((TextView)holder.mTv).setText(tip.getName());//设置数据
        //设置点击函数
        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnItemClickListener != null){
                    int p = holder.getAdapterPosition();//获取位置
                    Tip tip = list.get(p);
                    mOnItemClickListener.onItemClick(rv,view,p,tip);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    //ViewHolder
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mTv;
        RelativeLayout mContainer;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTv = itemView.findViewById(R.id.search_tv);
            mContainer = itemView.findViewById(R.id.search_container);
        }
    }
}
