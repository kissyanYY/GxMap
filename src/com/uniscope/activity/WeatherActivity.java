package com.uniscope.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gxmap.MainActivity;
import com.gxmap.R;
import com.uniscope.model.CheckoutCity;
import com.uniscope.model.ShowWeatherInfo;
import com.uniscope.service.MyLocationService;
import com.uniscope.utils.CheckoutCityUtils;
import com.uniscope.utils.DataUtils;
import com.uniscope.utils.HttpCallbackListener;
import com.uniscope.utils.HttpUtil;
import com.uniscope.utils.WeatherJsonUtil;
import com.uniscope.utils.WeatherPicUtil;

public class WeatherActivity extends Activity implements OnClickListener {

	private TextView currentTemp;
	private TextView highTemp;
	private TextView lowTemp;
	private ImageView weatherPic;
	private TextView currentCity;
	private TextView weatherTxt;
	private TextView arrow;
	private TextView arrowAll;

	private TextView oneDayWeek;
	private ImageView oneDayWeatherPic;
	private TextView oneDayTemp;

	private TextView twoDayWeek;
	private ImageView twoDayWeatherPic;
	private TextView twoDayTemp;

	private TextView threeDayWeek;
	private ImageView threeDayWeatherPic;
	private TextView threeDayTemp;

	private TextView fourDayWeek;
	private TextView txt_wrather;
	private ImageView fourDayWeatherPic;
	private TextView fourDayTemp;

	private ImageView btnUpdate;
	private ImageView btnAdd;
	private TextView updateTime;

	private ImageButton to_amap_app;
	private ImageView imgLoding;
	private AnimationDrawable ad;

	/**
	 * 储存天气信息的集合
	 */
	private ArrayList<ShowWeatherInfo> cityList = new ArrayList<ShowWeatherInfo>();
	/**
	 * 已选中城市的集合
	 */
	private ArrayList<CheckoutCity> cities = new ArrayList<CheckoutCity>();

	private float x1 = 0;
	private float y1 = 0;
	private float x2 = 0;
	private float y2 = 0;
	private int cityNum = 0;
	private int showCity = 0;
	private MyReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_query_layout);
		startAction();//加载动画
		findView();//
		bindView();//开启定位服务
	}

	/**
	 * 开启时的操作
	 */
	private void startAction() {
		imgLoding = (ImageView) findViewById(R.id.img_loding);
		ad = (AnimationDrawable) imgLoding.getDrawable();
		startAnimation();
	}

	/**
	 * 打开定位服务,注册广播接收器
	 */
	private void openLocationService() {
		Log.d("WWW", "开启定位服务：0 openLocationService");
		Intent intent = new Intent(WeatherActivity.this, MyLocationService.class);
		startService(intent);
		Log.d("WWW", "开启定位服务：1 ");
		
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("MyLocation");
		registerReceiver(receiver, filter);
	}

	private void findView() {
		cities = CheckoutCityUtils.getCityList(WeatherActivity.this);
		
		Log.d("WWW","数据库选择的城市："+cities.toString());
		currentTemp = (TextView) findViewById(R.id.txt_current_temp);
		highTemp = (TextView) findViewById(R.id.txt_high_temp);
		lowTemp = (TextView) findViewById(R.id.txt_low_temp);
		weatherPic = (ImageView) findViewById(R.id.img_weather);
		currentCity = (TextView) findViewById(R.id.txt_current_city);
		weatherTxt = (TextView) findViewById(R.id.txt_cn_weather);
		arrow = (TextView) findViewById(R.id.txt_city_num);
		arrowAll = (TextView) findViewById(R.id.txt_city_all);
		oneDayWeek = (TextView) findViewById(R.id.txt_week_oneday);
		oneDayWeatherPic = (ImageView) findViewById(R.id.img_weather_oneday);
		oneDayTemp = (TextView) findViewById(R.id.txt_temp_oneday);
		twoDayWeek = (TextView) findViewById(R.id.txt_week_twoday);
		twoDayWeatherPic = (ImageView) findViewById(R.id.img_weather_twoday);
		twoDayTemp = (TextView) findViewById(R.id.txt_temp_twoday);
		threeDayWeek = (TextView) findViewById(R.id.txt_week_threeday);
		threeDayWeatherPic = (ImageView) findViewById(R.id.img_weather_threeday);
		threeDayTemp = (TextView) findViewById(R.id.txt_temp_threeday);
		fourDayWeek = (TextView) findViewById(R.id.txt_week_fourday);
		fourDayWeatherPic = (ImageView) findViewById(R.id.img_weather_fourday);
		fourDayTemp = (TextView) findViewById(R.id.txt_temp_fourday);
		btnUpdate = (ImageView) findViewById(R.id.img_btn_update);
		btnAdd = (ImageView) findViewById(R.id.img_btn_add);
		updateTime = (TextView) findViewById(R.id.txt_update_time);
		to_amap_app = (ImageButton) findViewById(R.id.to_amap_app);
		txt_wrather = (TextView) findViewById(R.id.txt_wrather);
	}

	private void bindView() {
		btnAdd.setOnClickListener(this);
		btnUpdate.setOnClickListener(this);
		to_amap_app.setOnClickListener(this);
		txt_wrather.setOnClickListener(this);
		openLocationService();//
	}

	private void httpRequest(int loction){
		String address = "http://wthrcdn.etouch.cn/weather_mini?citykey="
				+cities.get(cityNum).getCityCode();
		Log.d("WWW","httpR:"+cities.get(cityNum).getCityCode());
		HttpUtil.sendHttpRequest(address, "UTF-8", new HttpCallbackListener() {

			@Override
			public void onFinish(final String response) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						WeatherJsonUtil wju = WeatherJsonUtil.getInstance(response);
						cityList.add(wju.setShowWeatherInfo());
						if (cityList.size() < cities.size()) {
							cityNum++;
							httpRequest(cityNum);
						}else if (cityList.size() == cities.size()) {
							showWeather(showCity);
							stopAnimation();
						}
					}
				});
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(WeatherActivity.this,
								"网络连接失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.img_btn_add:
				addCity();
				break;
			case R.id.img_btn_update:
				updateWeatherInfo();
				break;
			case R.id.to_amap_app:
				toMapView();
				break;
			case R.id.txt_wrather:
				toMapView();
				break;
		}
	}
	
	private void toMapView(){
		Intent intent = new Intent(WeatherActivity.this, MainActivity.class);
		startActivity(intent);
	}

	/**
	 * 添加显示天气城市：最多选择五个城市
	 */
	private void addCity() {
		Intent intent = new Intent(WeatherActivity.this, CheckoutCityActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data.getExtras().getBoolean("ischange")) {
			startAnimation();
			showCity = data.getExtras().getInt("citynum");
			cities = CheckoutCityUtils.getCityList(WeatherActivity.this);
			changeNumb(showCity+1);
			cityList.clear();
			cityNum = 0;
			httpRequest(0);
		}
	}


	/**
	 * 显示天气信息
	 * @param location
	 */
	private void showWeather(int location){

		currentTemp.setText(cityList.get(location).getCurrentTemp());
		highTemp.setText(cityList.get(location).getHighTemp());
		lowTemp.setText(cityList.get(location).getLowTemp());
		WeatherPicUtil.transformWeatherToCode(weatherPic,
				cityList.get(location).getWeatherPic());
		currentCity.setText(cityList.get(location).getCurrentCity());
		if (location == 0) {
			currentCity.setText(cityList.get(location).getCurrentCity()+"(当前)");
		}
		weatherTxt.setText(cityList.get(location).getWeatherTxt());

		oneDayWeek.setText(cityList.get(location).getOneDayWeek());
		WeatherPicUtil.transformWeatherToCode(oneDayWeatherPic,
				cityList.get(location).getOneDayWeatherPic());
		oneDayTemp.setText(cityList.get(location).getOneDayTemp());


		twoDayWeek.setText(cityList.get(location).getTwoDayWeek());
		WeatherPicUtil.transformWeatherToCode(twoDayWeatherPic,
				cityList.get(location).getTwoDayWeatherPic());
		twoDayTemp.setText(cityList.get(location).getTwoDayTemp());


		threeDayWeek.setText(cityList.get(location).getThreeDayWeek());
		WeatherPicUtil.transformWeatherToCode(threeDayWeatherPic,
				cityList.get(location).getThreeDayWeatherPic());
		threeDayTemp.setText(cityList.get(location).getThreeDayTemp());


		fourDayWeek.setText(cityList.get(location).getFourDayWeek());
		WeatherPicUtil.transformWeatherToCode(fourDayWeatherPic,
				cityList.get(location).getFourDayWeatherPic());
		fourDayTemp.setText(cityList.get(location).getFourDayTemp());

		updateTime.setText(DataUtils.updataTome());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				x1 = event.getX();
				y1 = event.getY();
				break;
			case MotionEvent.ACTION_UP:
				x2 = event.getX();
				y2 = event.getY();
				if (cityList.size() > 1) {
					if ((y1-y2)>300) {
						if (showCity < cities.size()-1) {
							showCity++;
						}else {
							showCity = 0;
						}
					}else if ((y2-y1)>300) {
						if (showCity > 0) {
							showCity--;
						}else {
							showCity = cities.size()-1;
						}
					}
					arrow.setText(showCity+1+"");
					showWeather(showCity);
				}
				break;
		}

		return super.onTouchEvent(event);
	}

	/**
	 * 刷新当前城市天气信息
	 */
	private void updateWeatherInfo() {
		startAction();
		cityList.clear();
		cityNum = 0;
		httpRequest(0);
	}


	/**
	 * 打开加载动画
	 */
	private void startAnimation(){
		imgLoding.setVisibility(View.VISIBLE);
		if (ad.isRunning()) {
			ad.stop();
		}
		ad.start();
	}

	/**
	 * 关闭加载动画
	 */
	private void stopAnimation(){
		imgLoding.setVisibility(View.GONE);
		if (!ad.isRunning()) {
			ad.start();
		}
		ad.stop();
	}


	/**
	 * 显示城市顺序
	 * @param num
	 */
	private void changeNumb(int num){
		arrow.setText(""+num);
		arrowAll.setText("/ "+(cities.size()));
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	/**
	 * 自定义广播接收器，接受定位服务返回的定位信息
	 * @author wangwei
	 *
	 */
	public class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("WWW", "收到定位返回结果");
			Bundle bundle=intent.getExtras();
			String locationDistrict = bundle.getString("location_district");
			String locationCity = bundle.getString("location_city");
			cities = CheckoutCityUtils.getAllCityList(WeatherActivity.this,
					cities, locationDistrict, locationCity);
			httpRequest(cityNum);
			changeNumb(1);
			context.unregisterReceiver(this);
		}
	}
}
