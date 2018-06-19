package com.uniscope.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gxmap.R;
import com.uniscope.adapter.CheckOutCityAdapter;
import com.uniscope.adapter.CheckOutCityAdapter.RemoveCityListener;
import com.uniscope.db.CityQueryDB;
import com.uniscope.model.CheckoutCity;
import com.uniscope.utils.CheckoutCityUtils;
import com.uniscope.utils.KeyBoardUtils;

public class CheckoutCityActivity extends Activity implements
		RemoveCityListener, OnItemClickListener, OnClickListener {

	public static final int LEVEL_ALLCITY = 0;
	public static final int LEVEL_FOUNDCITY = 1;
	private int currentLevel = LEVEL_ALLCITY;

	private ListView cityList;
	private Button addCity;
	private RelativeLayout back;
	private EditText editFound;
	private ImageView btnEmpty;
	private ImageView imgFound;
	private ArrayList<CheckoutCity> cities = new ArrayList<CheckoutCity>();
	private List<String> fuzzyCities = new ArrayList<String>();
	private CheckOutCityAdapter adapter;
	private ArrayAdapter<String> mAdapter;
	private boolean ischange = false;
	private CityQueryDB cityQueryDb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.checkout_city_layout);
		findView();
		bindView();
	}

	private void findView() {
		cities = CheckoutCityUtils.getCityList(CheckoutCityActivity.this);
		cityList = (ListView) findViewById(R.id.list_checkout_city);
		addCity = (Button) findViewById(R.id.btn_add_city);
		back = (RelativeLayout) findViewById(R.id.rel_back);
		editFound = (EditText) findViewById(R.id.edit_found);
		btnEmpty = (ImageView) findViewById(R.id.img_empty);
		imgFound = (ImageView) findViewById(R.id.img_found);
		cityQueryDb = CityQueryDB.getInstance(CheckoutCityActivity.this);
	}


	private void bindView() {
		adapter = new CheckOutCityAdapter(CheckoutCityActivity.this,
				R.layout.city_list_adapter, cities, this);

		cityList.setAdapter(adapter);
		cityList.setOnItemClickListener(this);
		addCity.setOnClickListener(this);
		btnEmpty.setOnClickListener(this);
		addCity.setText("添加城市("+cities.size()+")");
		back.setOnClickListener(this);
		imgFound.setOnClickListener(this);

		editFound.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String foundInfo = s.toString();
				if (foundInfo.isEmpty()) {
					currentLevel = LEVEL_ALLCITY;
					showView();
					cityList.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				}else {
					currentLevel = LEVEL_FOUNDCITY;
					showView();
					fuzzyCities = cityQueryDb.fuzzyQuestCitys(foundInfo);
					mAdapter = new ArrayAdapter<String>(CheckoutCityActivity.this,
							android.R.layout.simple_list_item_1, fuzzyCities);
					cityList.setAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_add_city:
				Intent intent = new Intent(
						CheckoutCityActivity.this, ChooseCityActivity.class);
				startActivityForResult(intent, 1);
				break;
			case R.id.rel_back:
				actionBack();
				break;
			case R.id.img_empty:
				editFound.setText("");
				break;
			case R.id.img_found:
				KeyBoardUtils.openKeyBoard(editFound, CheckoutCityActivity.this);
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			String cityName = data.getExtras().getString("cityname");
			if (!isFound(cityName)) {
				String cityCode = data.getExtras().getString("citycode");
				CheckoutCity city = new CheckoutCity(cityCode, cityName);
				cities.add(city);
				showCheckoutCity();
				adapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * 删除已选中的城市
	 */
	@Override
	public void removeCity(View view, int position) {

		cities.remove(position);
		showCheckoutCity();
		adapter.notifyDataSetChanged();
	}

	/**
	 * listview的点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (currentLevel == LEVEL_ALLCITY) {
			ischange = true;
			Intent intent = new Intent();
			intent.putExtra("ischange", ischange);
			intent.putExtra("citynum", arg2);
			setResult(0, intent);
			finish();
		}else if (currentLevel == LEVEL_FOUNDCITY) {
			KeyBoardUtils.closeKeyBoard(editFound, CheckoutCityActivity.this);
			currentLevel = LEVEL_ALLCITY;
			showView();
			editFound.setText("");
			editFound.clearFocus();
			if (!isFound(fuzzyCities.get(arg2))) {
				CheckoutCity city = new CheckoutCity(cityQueryDb
						.loadWeatherCode(fuzzyCities.get(arg2)), fuzzyCities.get(arg2));
				cities.add(city);
				showCheckoutCity();
			}
			cityList.setAdapter(adapter);

		}

	}

	@Override
	public void onBackPressed() {
		actionBack();
	}


	/**
	 * 返回WeatherActivity操作
	 */
	private void actionBack(){
		if (cities.size() < 1) {
			Toast.makeText(CheckoutCityActivity.this,
					"至少需要选择一个城市", Toast.LENGTH_SHORT).show();
		}else {
			Intent intent = new Intent();
			intent.putExtra("ischange", ischange);
			setResult(0, intent);
			finish();
		}
	}

	/**
	 * 检查城市是否已被选择
	 *
	 * @param cityName
	 * @return boolean
	 */
	private boolean isFound(String cityName){
		boolean isFound = false;
		for(CheckoutCity city : cities){
			if (city.getCityName().equals(cityName)) {
				isFound = true;
			}
		}
		return isFound;
	}


	/**
	 * 修改已选中城市
	 */
	private void showCheckoutCity(){
		CheckoutCityUtils.saveCityList(CheckoutCityActivity.this, cities);
		addCity.setText("添加城市("+cities.size()+")");
		ischange = true;
	}

	/**
	 * 控件显示
	 */
	private void showView(){
		if (currentLevel == LEVEL_ALLCITY) {
			btnEmpty.setVisibility(View.INVISIBLE);
			addCity.setVisibility(View.VISIBLE );
		}else if (currentLevel == LEVEL_FOUNDCITY) {
			btnEmpty.setVisibility(View.VISIBLE);
			addCity.setVisibility(View.INVISIBLE );
		}
	}

}
