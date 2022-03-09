package com.example.viewpagerandfragment;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements  AMap.OnMyLocationChangeListener, PoiSearch.OnPoiSearchListener {

    private MapView mMapView = null;
    private AMap aMap = null;
    private LatLonPoint currentLocation = new LatLonPoint(0,0);//当前位置

    private ArrayList<String> targetIdList = new ArrayList<String>();
    private PoiSearch poiSearch;
    private ArrayList<Marker> markerList = new ArrayList<Marker>();

    //注册协议，获取启动器-ActivityResultLauncher,第二个参数是回调的处理函数，将结果存储到targetIdList中
    ActivityResultLauncher launcher = registerForActivityResult(
            new ResultContract(),
            new ActivityResultCallback<ArrayList<String>>() {
        @Override
        public void onActivityResult(ArrayList<String> result) {
            //这个result是parseResult传递来的
            if(result == null){
                Toast.makeText(MapActivity.this, "关键词无结果！", Toast.LENGTH_SHORT).show();
            }else{
                targetIdList = result;
            }
//            int size = result.size();
//            for(int i=0;i<size;i++){
//                Log.e("zhu",result.get(i));
//            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //隐私接口,必须设置为true
        MapsInitializer.updatePrivacyShow(this,true,true);
        MapsInitializer.updatePrivacyAgree(this,true);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        //初始化地图控制器对象

        if (aMap == null) {
            aMap = mMapView.getMap();
        }


        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。

        myLocationStyle.interval(20000);//设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。

        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style

        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。

        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));

        aMap.setOnMyLocationChangeListener(this);

        //地图上进行点标记
        //待完善POI搜索后传参过来的结果
//        LatLng latLng = new LatLng(36.658676,117.145461);//纬度，经度
//        final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("KFC").snippet("汉峪金谷KFC"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        //Log.e("zhu","Destroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
        //清除上一次的点标记
        if(!markerList.isEmpty()){
            for(int i=0;i<markerList.size();i++){
                markerList.get(i).destroy();
            }
        }
        //非空的时候绘制点
        if(!targetIdList.isEmpty()){
            try {
                poiSearch = new PoiSearch(this, null);
                poiSearch.setOnPoiSearchListener(this);
                for(int i=0;i<targetIdList.size();i++){
                    Log.e("zhu","Resume时候异步查询ID："+targetIdList.get(0));
                    poiSearch.searchPOIIdAsyn(targetIdList.get(i));// 异步搜索
                }
                aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
            } catch (AMapException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
        //Log.e("zhu","pause");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    //打开搜索页面
    public void openSearchActivity(View view) {
        Intent search_intent = new Intent(this,SearchActivity.class);
        startActivity(search_intent);
    }

    //打开poi搜索界面，传递当前经纬度
    public void openPoiSearchActivity(View view) {
        launcher.launch(true);
        //startActivity(poi_search_intent);
    }

    //实现listener,获取经纬度
    @Override
    public void onMyLocationChange(Location location) {
        currentLocation.setLongitude(location.getLongitude());
        currentLocation.setLatitude(location.getLatitude());
    }


    //对于页面传递过来的poiId进行搜索后，返回的结果
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int rCode) {
        Log.e("zhu","查询结果如下");
        Log.e("zhu","名称："+poiItem.getTitle());
        Log.e("zhu","地址："+poiItem.getSnippet());
        Log.e("zhu","经度"+poiItem.getLatLonPoint().getLongitude());
        Log.e("zhu","纬度"+poiItem.getLatLonPoint().getLatitude());
        Log.e("zhu","————————————————————————————————————");

        LatLng latLng = new LatLng(poiItem.getLatLonPoint().getLatitude(),
                poiItem.getLatLonPoint().getLongitude());//纬度，经度
        final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).
                title(poiItem.getTitle()).snippet(poiItem.getSnippet()));
        markerList.add(marker);
    }


    class ResultContract extends ActivityResultContract<Boolean, ArrayList<String>>{

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Boolean input) {
            Intent poi_search_intent = new Intent(MapActivity.this,PoiSearchActivity.class);
            poi_search_intent.putExtra("Lon",currentLocation.getLongitude());
            poi_search_intent.putExtra("Lat",currentLocation.getLatitude());
            return poi_search_intent;
        }

        @Override
        public ArrayList<String> parseResult(int resultCode, @Nullable Intent intent) {
            if(resultCode == RESULT_OK){
                return intent.getStringArrayListExtra("poiIdList");
            }else{
                return null;
            }
        }
    }

/*
    public void openNavi(View view) {
        //构建导航组件配置类，没有传入起点，所以起点默认为 “我的位置”
        AmapNaviParams params = new AmapNaviParams(null, null, null, AmapNaviType.DRIVER, AmapPageType.ROUTE);
        //启动导航组件
        AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);
    }*/
}