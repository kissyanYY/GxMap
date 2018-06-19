package com.uniscope.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxmap.R;
import com.uniscope.db.CityQueryDB;

public class ChooseCityActivity extends Activity implements OnClickListener {

	/**
	 * 当前为省级地区
	 */
	public static final int LEVEL_PROVINCE = 0;
	/**
	 * 当前为市级地区
	 */
	public static final int LEVEL_CITY = 1;
	/**
	 * 当前为县级地区
	 */
	public static final int LEVEL_TOWN = 2;
	/**
	 * 选中的城市级别
	 */
	private int currentLevel;

	private TextView txtTitle;
	private ListView listCity;
	private RelativeLayout btnBack;
	private List<String> dataList = new ArrayList<String>();
	private ArrayAdapter<String> dataAdapt;
	private CityQueryDB cityQueryDb;

	private List<String> provinceList;	//省级城市列表
	private List<String> cityList;		//市级城市列表
	private List<String> townList;		//县级城市列表
	private String selectedProvince;	//选中的省级城市
	private String selectedCity;		//选中的市级城市

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_city_layout);
		findView();
		bindView();
		queryProvince();
	}

	private void findView() {
		txtTitle = (TextView) findViewById(R.id.txt_title);
		listCity = (ListView) findViewById(R.id.list_city);
		btnBack = (RelativeLayout) findViewById(R.id.rel_back);
		dataAdapt = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listCity.setAdapter(dataAdapt);
		cityQueryDb = CityQueryDB.getInstance(ChooseCityActivity.this);
	}

	private void bindView() {
		listCity.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				chooseArea(position);
			}
		});

		btnBack.setOnClickListener(this);
	}

	/**
	 * 选择地区的点击事件
	 */
	protected void chooseArea(int position) {
		if (currentLevel == LEVEL_PROVINCE) {
			selectedProvince = provinceList.get(position);
			queryCities();
		}else if (currentLevel == LEVEL_CITY) {
			selectedCity = cityList.get(position);
			queryTowns();
		}else if (currentLevel == LEVEL_TOWN) {
			String cityCode = cityQueryDb.loadWeatherCode(townList.get(position));
			Intent intent = new Intent();
			intent.putExtra("citycode", cityCode);
			intent.putExtra("cityname", townList.get(position));
			setResult(1, intent);
			finish();
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.rel_back:
				actionBack();
				break;
		}

	}


	/**
	 * 选择省级城市
	 */
	private void queryProvince(){
		provinceList = cityQueryDb.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for(String province : provinceList){
				dataList.add(province);
			}
			dataAdapt.notifyDataSetChanged();
			listCity.setSelection(0);
			txtTitle.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else{

		}
	}

	/**
	 * 选择市级城市
	 */
	private void queryCities(){
		cityList = cityQueryDb.loadCities(selectedProvince);
		if (cityList.size() > 0) {
			dataList.clear();
			for(String city : cityList){
				dataList.add(city);
			}
			dataAdapt.notifyDataSetChanged();
			listCity.setSelection(0);
			txtTitle.setText(selectedProvince);
			currentLevel = LEVEL_CITY;
		}else{

		}
	}

	/**
	 * 选择市级城市
	 */
	private void queryTowns(){
		townList = cityQueryDb.loadTowns(selectedCity);
		if (townList.size() > 0) {
			dataList.clear();
			for(String town : townList){
				dataList.add(town);
			}
			dataAdapt.notifyDataSetChanged();
			listCity.setSelection(0);
			txtTitle.setText(selectedCity);
			currentLevel = LEVEL_TOWN;
		}else{

		}
	}

	/**
	 * 返回的点击事件
	 */
	private void actionBack(){
		if (currentLevel == LEVEL_TOWN) {
			queryCities();
		}else if (currentLevel == LEVEL_CITY) {
			queryProvince();
		}else {
			Intent intent = new Intent(ChooseCityActivity.this, CheckoutCityActivity.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		actionBack();
	}

}
