package com.example.viewpagerandfragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

//viewpager2使用了懒加载
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager = findViewById(R.id.viewPager);//主视图里面的viewpager
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);//给这个viewpager实现一个适配器adapter
        //viewpager的具体设置都在adapter中，包括item_pager
    }

    public void openNewActivity(View view) {
        Intent second_intent = new Intent(this,SecondActivity.class);
        startActivity(second_intent);
    }

    public void openMapActivity(View view) {
        Intent map_intent = new Intent(this,MapActivity.class);
        startActivity(map_intent);
    }
}