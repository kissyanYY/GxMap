package com.uniscope.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.format.Time;

import com.uniscope.model.WeatherInfo;

/**
 * 信息处理工具类
 *
 * @author wangwei
 *
 */
public class DataUtils {

	/**
	 * 格式化温度数据
	 * @param temp
	 * @return String
	 */
	public static String tempCupUtil(String temp){
		String reg = "[\u4e00-\u9fa5]";
		Pattern pat = Pattern.compile(reg);
		Matcher mat=pat.matcher(temp.replace(" ", ""));
		return mat.replaceAll("");
	}

	/**
	 * 格式化星期数据
	 * @param week
	 * @return String
	 */
	public static String weekCupUtString(String week){
		return week.substring(week.length()-3, week.length());
	}


	/**
	 * 得到温度范围
	 * @param info
	 * @return String
	 */
	public static String showTempRang(WeatherInfo info){

		return info.getHighTemp()+"/"+info.getLowTemp();
	}


	/**
	 * 天气信息刷新时间
	 * @return String
	 */
	public static String updataTome(){
		Time time = new Time();
		time.setToNow();
		int hour = time.hour;
		int minute = time.minute;
		String hourTime = (hour>12) ? ("下午"+(hour-12)):("上午"+hour);
		return hourTime+":"+minute;
	}


	/**
	 * 获得标准位置信息
	 * @param location
	 * @return String
	 */
	public static String locationInfo(String location){

		return location.substring(0, location.length()-1);
	}

}
