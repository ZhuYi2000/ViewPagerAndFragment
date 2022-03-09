package com.example.viewpagerandfragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    public void testOnlineDatabase(View view) {
        //不能在主线程中调用，必须要在新的线程中申请数据库
        new Thread(){
            @Override
            public void run(){
                String url = "jdbc:mysql://rm-bp1bnehb104c22283fo.mysql.rds.aliyuncs.com/pokemon?useUnicode=true&amp;characterEncoding=utf-8";
                String user = "p_database";
                String password = "Yige1234";
                String driver = "com.mysql.jdbc.Driver";
                try {
                    Class.forName(driver);
                    Connection conn = DriverManager.getConnection(url,user,password);
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("select * from user_base");
                    while (rs.next()){
                        //解决在子线程中调用Toast的异常情况处理
                        Looper.prepare();
                        Toast.makeText(MainActivity.this, "你好，"+rs.getString("user_name"), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    rs.close();
                    st.close();
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}