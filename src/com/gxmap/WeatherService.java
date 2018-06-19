package com.gxmap;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearch.OnWeatherSearchListener;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.amap.map3d.demo.util.ToastUtil;
import com.google.gson.Gson;

public class WeatherService  implements OnWeatherSearchListener {
	private LocalWeatherForecast weatherforecast;
	private WeatherSearchQuery mquery;
	private LocalWeatherLive weatherlive;
	private WeatherSearch mweathersearch;
	private Activity hostActivity;
	private String cityname;
    private List<LocalDayWeatherForecast> forecastlist = null;
    private WeatherBean wb;
    
    public WeatherService(Activity hosta,String cn){
    	this.hostActivity = hosta;
    	this.cityname = cn;
    	wb = new WeatherBean();
    }
    
    public void getWeather(){
    	Log.d("YYYY:","天气服务开启：");
    	searchliveweather();
    	// 发送广播
    }
    
    /**
     * 实时天气查询
     */
    private void searchliveweather() {
        mquery = new WeatherSearchQuery(cityname, WeatherSearchQuery.WEATHER_TYPE_LIVE);//检索参数为城市和天气类型，实时天气为1、天气预报为2
        mweathersearch = new WeatherSearch(hostActivity);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
        Log.d("YYYY:","开始查询天气");
    }

    /**
     * 实时天气查询回调
     */
    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
            	weatherlive = weatherLiveResult.getLiveResult();
            	
            	wb.setFl(weatherlive.getWindDirection() + "风     " + weatherlive.getWindPower() + "级");
            	wb.setWd(weatherlive.getTemperature() + "°");
            	wb.setTq(weatherlive.getWeather());
            	wb.setUpdateTime(weatherlive.getReportTime());
            	wb.setSd("湿度         " + weatherlive.getHumidity() + "%");
            	Log.d("YYYY:","天气获取成功："+wb.getWd());
                sendBoder(wb);
            } else {
                ToastUtil.show(hostActivity, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(hostActivity, rCode);
        }
    }

    /**
     * 天气预报查询结果回调--未来时间
     */
    @Override
    public void onWeatherForecastSearched(
            LocalWeatherForecastResult weatherForecastResult, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (weatherForecastResult != null && weatherForecastResult.getForecastResult() != null
                    && weatherForecastResult.getForecastResult().getWeatherForecast() != null
                    && weatherForecastResult.getForecastResult().getWeatherForecast().size() > 0) {
                weatherforecast = weatherForecastResult.getForecastResult();
                forecastlist = weatherforecast.getWeatherForecast();
               String wltq =  fillforecast();
               //发送广播
               sendBoder(wb);

            } else {
                ToastUtil.show(hostActivity, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(hostActivity, rCode);
        }
    }
    
    private void sendBoder(WeatherBean wbs){
    	//registerReceiver(mBroadcastReceiver, intentFilter);
    	//注册应用内广播接收器
    	LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(hostActivity);
//    	localBroadcastManager.registerReceiver(mBroadcastReceiver, intentFilter);
    	        
//    	//unregisterReceiver(mBroadcastReceiver);
//    	//取消注册应用内广播接收器
//    	localBroadcastManager.unregisterReceiver(mBroadcastReceiver);

    	Intent intent = new Intent();
    	intent.setAction(GlobleVarString.BROADCAST_ACTION);
    	Gson gs = new Gson();
    	String jsb = gs.toJson(wbs);
    	intent.putExtra("Weather", jsb);
    	//sendBroadcast(intent);
    	//发送应用内广播
    	localBroadcastManager.sendBroadcast(intent);
    	Log.d("YYYY","广播发送成功");
    }
    
    private String fillforecast() {
//        reporttime2.setText(weatherforecast.getReportTime() + "发布");
        String forecast = "";
        for (int i = 0; i < forecastlist.size(); i++) {
            LocalDayWeatherForecast localdayweatherforecast = forecastlist.get(i);
            String week = null;
            switch (Integer.valueOf(localdayweatherforecast.getWeek())) {
                case 1:
                    week = "周一";
                    break;
                case 2:
                    week = "周二";
                    break;
                case 3:
                    week = "周三";
                    break;
                case 4:
                    week = "周四";
                    break;
                case 5:
                    week = "周五";
                    break;
                case 6:
                    week = "周六";
                    break;
                case 7:
                    week = "周日";
                    break;
                default:
                    break;
            }
            String temp = String.format("%-3s/%3s",
                    localdayweatherforecast.getDayTemp() + "°",
                    localdayweatherforecast.getNightTemp() + "°");
            String date = localdayweatherforecast.getDate();
            forecast += date + "  " + week + "                       " + temp + "\n\n";
        }
        
        return forecast;
    }
}
