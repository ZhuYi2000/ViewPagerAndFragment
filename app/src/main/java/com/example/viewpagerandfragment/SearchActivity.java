package com.example.viewpagerandfragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements TextWatcher, Inputtips.InputtipsListener, RvAdapter.OnItemClickListener {

    private EditText editText;
    private RecyclerView recyclerView;
    private Inputtips inputTips;
    private RvAdapter rvAdapter;
    private AMapNavi mAMapNavi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        editText = findViewById(R.id.my_edit_query);
        editText.addTextChangedListener(this);

        recyclerView = findViewById(R.id.tips_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rvAdapter = new RvAdapter(this,recyclerView,new ArrayList<>());
        //rvAdapter设置点击事件,还没有写
        rvAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(rvAdapter);

        inputTips = new Inputtips(this,(InputtipsQuery) null);
        inputTips.setInputtipsListener(this);

        try {
            mAMapNavi = AMapNavi.getInstance(this);
        } catch (AMapException e) {
            e.printStackTrace();
        }
        //内置语音播报
        mAMapNavi.setUseInnerVoice(true,false);

    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        InputtipsQuery inputQuery = new InputtipsQuery(String.valueOf(charSequence),"0531");
        inputQuery.setCityLimit(true);//限制在当前城市
        inputTips.setQuery(inputQuery);
        inputTips.requestInputtipsAsyn();//异步请求
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    //inputTips.requestInputtipsAsyn() 的回调接口，将response中的数据导入到adapter
    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        Log.e("zhu","条目数量："+list.size());
        rvAdapter.setData(list);
    }


    /*
    * route：服务类型
    sourceApplication：第三方调用应用名称
    dlat：终点纬度
    dlon：终点经度
    dname：终点名称
    dev：起终点是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
    t：t = 0（驾车）= 1（公交）= 2（步行）= 3（骑行）= 4（火车）= 5（长途客车）
    *
    * */

    //目前是搜索完毕后，点击条目即开始导航，可以考虑增加一个确认弹窗。以及最终效果应该是点击地图上的宝可梦，
    // 弹窗出来宝可梦的一些信息，然后有个按钮可以让用户导航至其所在地

    @Override
    public void onItemClick(RecyclerView parent, View view, int position, Tip data) {
        boolean installed = isInstallApk(this,"com.autonavi.minimap");//判断是否安装
        Toast.makeText(this,"正在导航至："+data.getName(),Toast.LENGTH_SHORT).show();

        LatLonPoint point = data.getPoint();//经纬度的点
        if(!installed){
            //如果手机未安装高德导航，那么使用内置的导航，只不过会弹窗提示要求权限
            Poi poi = new Poi(data.getName(),new LatLng(point.getLatitude(),point.getLongitude()),data.getPoiID());
            AmapNaviParams params = new AmapNaviParams(null,null,poi, AmapNaviType.DRIVER, AmapPageType.ROUTE);
            AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(),params,null);

        }else {
            StringBuffer stringBuffer = new StringBuffer("androidamap://route?sourceApplication=").append("amap");

            String address = data.getAddress();
            double Latitude = point.getLatitude();
            double Longitude = point.getLongitude();

            stringBuffer.append("&dlat=").append(Latitude)
                    .append("&dlon=").append(Longitude)
                    .append("&dname=").append(address)
                    .append("&dev=").append(0)
                    .append("&t=").append(0);
            Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse(stringBuffer.toString()));
            intent.setPackage("com.autonavi.minimap");
            startActivity(intent);
        }

    }

    //判断是否装了高德地图,需要manifest中添加query才能查询到高德地图
    public static boolean isInstallApk(Context context, String pkgname) {
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if (packageInfo.packageName.equals(pkgname)) {
                Log.e("zhu","安装了高德地图");
                return true;
            } else {
                //Log.e("zhu","应用："+packageInfo.packageName);
                continue;
            }
        }
        return false;
    }
}