package com.example.administrator.coolweather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.coolweather.db.City;
import com.example.administrator.coolweather.db.County;
import com.example.administrator.coolweather.db.Province;
import com.example.administrator.coolweather.util.HttpUtil;
import com.example.administrator.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author hyh
 * @date 2018/9/25 0025
 * @describe:
 */
public class ChooseAreaFragment extends Fragment {
    private static final String TAG = "ChooseAreaFragment";
    public  static final int LEVEL_PROVINCE=0;
    public  static final int LEVEL_CITY=1;
    public  static final int LEVEL_COUNTY=2;
    private ProgressDialog progressDialog;
    private TextView textView;
    private Button button;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dateList=new ArrayList<>();

    /**省列表*/
    private  List<Province> provinceList;
    private  List<City> cityList;
    private  List<County> countyList;

    //选中的省份
    private  Province selectedProvince;
    //选中的城市
    private  City selectedCity;
    //当前选中的级别
    private  int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.choose_area,container,false);
       textView=view.findViewById(R.id.title_text);
       button=view.findViewById(R.id.back_button);
       listView=view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dateList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(currentLevel==LEVEL_PROVINCE){
                    Log.e(TAG, "onItemClick: "+LEVEL_PROVINCE );
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    Log.e(TAG, "onItemClick: "+LEVEL_CITY );
                    selectedCity=cityList.get(position);
                    queryCounties();
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel==LEVEL_COUNTY){
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省  优先数据库 无则服务器
     */
    private void queryProvinces(){
        textView.setText("中国");
        button.setVisibility(View.GONE);
        provinceList= DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            dateList.clear();
            for (Province province:provinceList) {
                dateList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }else{
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    /**
     * 查询市
     */
    private void queryCities(){
        textView.setText(selectedProvince.getProvinceName());
        button.setVisibility(View.VISIBLE);
        cityList= DataSupport.where("provinceid=?",String.valueOf(
                selectedProvince.getId())).find(City.class);
        if(cityList.size()>0){
            dateList.clear();
            for (City city:cityList) {
                dateList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else{
            int provinceCode=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }
    /**
     * 查询县
     */
    private void queryCounties(){
        textView.setText(selectedCity.getCityName());
        button.setVisibility(View.VISIBLE);
        countyList= DataSupport.where("cityid=?",String.valueOf(
                selectedCity.getId())).find(County.class);
        if(countyList.size()>0){
            dateList.clear();
            for (County county:countyList) {
                dateList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else{
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }

    /**
     * 服务器查询
     * */
    private  void queryFromServer(String address, final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                Log.e(TAG, "onResponse: "+responseText );
                boolean result=false;
                if("province".equals(type)){
                    result= Utility.hanleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    result=Utility.hanleCityResponse(responseText,selectedProvince.getId());
                }else if("county".equals(type)){
                    result=Utility.hanleCountyResponse(responseText,selectedCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else  if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示对话框
     */
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     * 关闭对话框
     */
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
