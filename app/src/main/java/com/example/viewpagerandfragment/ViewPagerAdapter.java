package com.example.viewpagerandfragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

//记得泛型中指定自己的类  比如 ViewPagerAdapter.ViewPagerViewHolder
//这个类中@override的函数都是只需要定义，并不关心程序是如何调用的，只需要实现每个函数对应的功能即可
public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder> {

    private List<String> titles = new ArrayList<>();
    private List<Integer> backgrounds = new ArrayList<>();

    //构造函数
    public ViewPagerAdapter(){
        titles.add("zero");
        titles.add("one");
        titles.add("two");
        titles.add("three");
        titles.add("four");
        titles.add("five");
        titles.add("six");
        titles.add("seven");
        titles.add("eight");
        titles.add("nine");

        backgrounds.add(R.color.white);
        backgrounds.add(R.color.red);
        backgrounds.add(R.color.yellow);
        backgrounds.add(R.color.white);
        backgrounds.add(R.color.red);
        backgrounds.add(R.color.yellow);
        backgrounds.add(R.color.white);
        backgrounds.add(R.color.red);
        backgrounds.add(R.color.yellow);
        backgrounds.add(R.color.white);
    }


    //创建viewholder：渲染UI，将xml视图作为参数传递给viewholder
    @NonNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewPagerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pager,parent,false));
        //通过一系列操作获取item_pager，以及与其相对应的viewHolder
    }

    //绑定数据，将viewholder中的数据进行具体的指定，position是具体的位置
    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder holder, int position) {
        holder.mTv.setText(titles.get(position));

        //记住是设置resource
        holder.mContainer.setBackgroundResource(backgrounds.get(position));

        String url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/";
        RequestOptions options = new RequestOptions()
                .skipMemoryCache(false)  //用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存所有图片(原图,转换图)
                .fitCenter();
        int p_id = position+20;
        Glide.with(holder.mContainer).load(url+p_id+".png").apply(options).
                thumbnail(Glide.with(holder.mImageView).load(R.drawable.loading)).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return 4;  //是item的个数，即左右滑动共有几个页面
    }


    //自定义viewHolder
    class ViewPagerViewHolder extends RecyclerView.ViewHolder {

        //处理item_pager
        TextView mTv;
        RelativeLayout mContainer;
        ImageView mImageView;

        public ViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            mContainer = itemView.findViewById(R.id.container);
            mTv = itemView.findViewById(R.id.tvTitle);
            mImageView = itemView.findViewById(R.id.testImg);
        }
    }
}
