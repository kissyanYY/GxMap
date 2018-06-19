package com.uniscope.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;

public class MyLocationService  extends Service {
	private AMapLocationClient mLocationClient;
	public AMapLocationListener mLocationListener;
	//声明AMapLocationClientOption对象
	public AMapLocationClientOption mLocationOption = null;
	private String cityName;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.d("WWW","进入定位服务-MyLocationService Create");
		
		mLocationClient = new AMapLocationClient(getApplicationContext());
		//置定位回调监听
		mLocationListener = new AMapLocationListener() {
			
			@Override
			public void onLocationChanged(AMapLocation amapLocation) {
					if (amapLocation != null && amapLocation.getErrorCode() == 0) {
						Log.d("WWW","定位成功,发送广播");
						broderLocaionInfo(amapLocation);
					} else {
						String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
						Log.e("AmapErr",errText);
					}
			}
		};
		mLocationClient.setLocationListener(mLocationListener);
		initLocationOptions();
		
		super.onCreate();
	}
	
	public void initLocationOptions(){
		//初始化AMapLocationClientOption对象
		mLocationOption = new AMapLocationClientOption();
		//设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		
		//获取一次定位结果：
		//该方法默认为false。
//		mLocationOption.setOnceLocation(true);
		
		//获取最近3s内精度最高的一次定位结果：
		//设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
		mLocationOption.setOnceLocationLatest(true);
		
		//设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
//		mLocationOption.setInterval(4000);
		
		//设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		
		//单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
		mLocationOption.setHttpTimeOut(20000);
		
		//给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("WWW","进入定位服务-onStartCommand");
		startService();
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * 获取当前位置的城市名称和区县名称，通过广播发送出去
	 * @param location
	 */
	private void broderLocaionInfo(AMapLocation amapLocation){
		amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
		amapLocation.getLatitude();//获取纬度
		amapLocation.getLongitude();//获取经度
//		amapLocation.getAccuracy();//获取精度信息
//		amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//		amapLocation.getCountry();//国家信息
//		amapLocation.getProvince();//省信息
		cityName =  amapLocation.getCity();//城市信息
		String dis = amapLocation.getDistrict();//城区信息
//		amapLocation.getStreet();//街道信息
//		amapLocation.getStreetNum();//街道门牌号信息
		amapLocation.getCityCode();//城市编码
		amapLocation.getAdCode();//地区编码
//		amapLocation.getAoiName();//获取当前定位点的AOI信息
//		amapLocation.getBuildingId();//获取当前室内定位的建筑物Id
//		amapLocation.getFloor();//获取当前室内定位的楼层
//		amapLocation.getGpsStatus();//获取GPS的当前状态
		//获取定位时间
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date date = new Date(amapLocation.getTime());
//		df.format(date);
		Log.d("WWW","定位成功:"+cityName+",dis:"+dis);

		Intent intent = new Intent();
		intent.putExtra("location_district",dis);
		intent.putExtra("location_city", cityName);
		intent.setAction("MyLocation");
		sendBroadcast(intent);
	}

	

	/**
	 * 开启定位服务
	 */
	public void startService(){
		Log.d("WWW","start定位服务startService");
		mLocationClient.startLocation();
	}

	/**
	 * 关闭定位服务
	 */
	public void stopService(){
		mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
	}
	
	@Override
	public void onDestroy() {
		stopService();
		mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
		super.onDestroy();
	}

}
