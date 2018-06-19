package com.gxmap;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.google.gson.Gson;

public class LocationService implements LocationSource,AMapLocationListener {
	
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private AMapLocationClient mlocationClient;
	private AMapLocationClientOption mLocationOption;
	private Activity hostActity;
	private boolean myLocationGet = false;
	private double latitude;
	private double logtitude;
	boolean canLocation = true;
	public LocationService(AMap amp,Activity hostAc){
		this.aMap = amp;
		this.hostActity = hostAc;
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				Log.d("TT:","[timer]  canLocation:"+canLocation);

				canLocation  = true;
			}
		}, 0, 2*60*1000);//2分钟更新一次
		setUpMap();
	}
	
	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
		myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
		myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
		// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
		myLocationStyle.interval(120*1000);//1分钟定位一次
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
//		Log.e("YYYYYYYYYYYYYYYY", "定位更新");

		if (canLocation && mListener != null && amapLocation != null) {
			canLocation = false;
			Log.d("TT:","[locationChange] canLocation:"+canLocation);
			if (amapLocation != null
					&& amapLocation.getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
				latitude = amapLocation.getLatitude();
				logtitude = amapLocation.getLongitude();
				String city = amapLocation.getCity();
				sendLocationChange();
				if(!myLocationGet){
					TextView tv = (TextView)hostActity.findViewById(R.id.city);
					tv.setText(city);
					GlobleVarString.locationCity = city;
					WeatherService ws = new WeatherService(hostActity,city);
					ws.getWeather();
				}
				if(!myLocationGet)myLocationGet = true;
//				WeatherService ws = new WeatherService(hostActity, city);
//				ws.getWeather();
				
//				Log.d("YYY:","city:"+city);
			} else {
				String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
				Log.e("AmapErr",errText);
			}
		}
		canLocation = false;
	
	}
	
	private void sendLocationChange(){
    	LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(hostActity);
    	
    	Intent intent = new Intent();
    	intent.setAction(GlobleVarString.LOCATION_CHAGER_ACTION);
    	Gson gs = new Gson();
    	String jsb = gs.toJson(getMylocation());
    	intent.putExtra("LatLng", jsb);
    	//sendBroadcast(intent);
    	//发送应用内广播
    	localBroadcastManager.sendBroadcast(intent);
    	Log.d("TT", "消息发送成功"+jsb);
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		
		Log.d("TT:","[定位激活activate]");
		mListener = listener;
		if (mlocationClient == null) {
			mlocationClient = new AMapLocationClient(hostActity);
			mLocationOption = new AMapLocationClientOption();
			//设置定位监听
			mlocationClient.setLocationListener(this);
			//设置为高精度定位模式
			mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
			//设置定位参数
			mlocationClient.setLocationOption(mLocationOption);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用onDestroy()方法
			// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
			mlocationClient.startLocation();
		}
	}

	@Override
	public void deactivate() {
		mListener = null;
		if (mlocationClient != null) {
			mlocationClient.stopLocation();
			mlocationClient.onDestroy();
		}
		mlocationClient = null;
	}
	
	public LatLng getMylocation(){
		if(!Double.isNaN(latitude) && !Double.isNaN(logtitude)){
			LatLng ll = new LatLng(latitude, logtitude);
			return ll;
		}
		return null;
	}
}
