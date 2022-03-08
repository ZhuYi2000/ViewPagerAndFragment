package com.example.viewpagerandfragment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.services.cloud.CloudSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;
import java.util.List;

public class PoiSearchActivity extends AppCompatActivity implements PoiSearch.OnPoiSearchListener {

    EditText editText;
    PoiSearch.Query query;
    PoiSearch poiSearch;
    List<PoiItem> poiItemList = new ArrayList<>();
    private LatLonPoint currentLocation = new LatLonPoint(0,0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_search);
        Intent intent = getIntent();

        editText = findViewById(R.id.poi_edit_query);
        currentLocation.setLatitude(intent.getDoubleExtra("Lat",0));
        currentLocation.setLongitude(intent.getDoubleExtra("Lon",0));
        Log.e("zhu","经度:"+currentLocation.getLongitude());
        Log.e("zhu","纬度:"+currentLocation.getLatitude());
    }

    //按钮点击方法
    public void startPoiSearch(View view) throws AMapException {
        String keyword = editText.getText().toString().trim();
        query = new PoiSearch.Query(keyword,"","0531");
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);//设置查询页码

        //构造PoiSearch对象
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);

        //设置为用户定位的周边搜索，范围3公里
        poiSearch.setBound(new PoiSearch.SearchBound(currentLocation,3000));

        //send request
        poiSearch.searchPOIAsyn();
    }

    //回调接口，解析结果
    @Override
    public void onPoiSearched(PoiResult poiResult, int rCode) {
        //返回结果成功或者失败的响应码。1000为成功，其他为失败
        if(rCode == 1000){
            Toast.makeText(this,"获取成功！",Toast.LENGTH_SHORT).show();
            poiItemList = poiResult.getPois();
            int size = poiItemList.size();
            for(int i=0;i<size;i++){
                PoiItem tempItem = poiItemList.get(i);
                Log.e("zhu","名称："+tempItem.getTitle());
                Log.e("zhu","地址："+tempItem.getSnippet());
                Log.e("zhu","经度"+tempItem.getLatLonPoint().getLongitude());
                Log.e("zhu","纬度"+tempItem.getLatLonPoint().getLatitude());
            }
        }else {
            Toast.makeText(this,"获取失败！",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}