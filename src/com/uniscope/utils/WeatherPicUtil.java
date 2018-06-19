package com.uniscope.utils;

import android.widget.ImageView;

import com.gxmap.R;

/**
 * 判断天气图像（未完成）
 *
 * @author wangwei
 *
 */
public class WeatherPicUtil {

	/**
	 * 显示天气图像
	 * @param weatherType
	 * @return code
	 */
	public static void transformWeatherToCode(ImageView pic, String weatherType){

		if (weatherType.equals("晴")) {
			pic.setImageResource(R.drawable.weather_sun);
		}else if (weatherType.equals("多云")) {
			pic.setImageResource(R.drawable.weather_cloudy);
		}else if (weatherType.equals("阴")) {
			pic.setImageResource(R.drawable.weather_overcast);
		}else if (weatherType.equals("阵雨")) {
			pic.setImageResource(R.drawable.weather_shower);
		}else if (weatherType.equals("雷阵雨")) {
			pic.setImageResource(R.drawable.weather_thundershower);
		}else if (weatherType.equals("小雨")) {
			pic.setImageResource(R.drawable.weather_lightrain);
		}else if (weatherType.equals("中雨")) {
			pic.setImageResource(R.drawable.weather_moderaterain);
		}else if (weatherType.equals("大雨")) {
			pic.setImageResource(R.drawable.weather_heavyrain);
		}else if (weatherType.equals("暴雨")) {
			pic.setImageResource(R.drawable.weather_heavyrain);
		}else if (weatherType.equals("雨夹雪")) {
			pic.setImageResource(R.drawable.weather_icyrain);
		}else if (weatherType.equals("小雪")) {
			pic.setImageResource(R.drawable.weather_lightsnow);
		}else if (weatherType.equals("中雪")) {
			pic.setImageResource(R.drawable.weather_moderatersnow);
		}else if (weatherType.equals("大学")) {
			pic.setImageResource(R.drawable.weather_heavysnow);
		}else if (weatherType.equals("暴雪")) {
			pic.setImageResource(R.drawable.weather_heavysnow);
		}else {
			pic.setImageResource(R.drawable.weather);
		}

	}

}
