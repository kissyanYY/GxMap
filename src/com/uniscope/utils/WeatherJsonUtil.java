package com.uniscope.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.uniscope.model.DataInfo;
import com.uniscope.model.ShowWeatherInfo;
import com.uniscope.model.WeatherInfo;
import com.uniscope.model.YesterdayWeather;

/**
 * 从网络返回的json文件中获取信息:
 * 1.获取未来几天天气信息
 * 2.获取当前天气信息
 * 3.获取昨天天气信息
 * 4.封装天气信息
 *
 * @author wangwei
 *
 */
public class WeatherJsonUtil {
	private static String response;
	private static JSONObject json1;
	public static WeatherJsonUtil weatherJsonUtil;

	public static WeatherJsonUtil getInstance(String str){
		if (weatherJsonUtil == null) {
			weatherJsonUtil = new WeatherJsonUtil();
		}
		response = str;
		try {
			JSONObject json = new JSONObject(response);
			json1 = json.getJSONObject("data");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return weatherJsonUtil;
	}

	/**
	 * 1.获取未来几天天气信息
	 * @return List
	 */
	public ArrayList<WeatherInfo> parseFutureWeatherJson(){
		ArrayList<WeatherInfo> weatherList = new ArrayList<WeatherInfo>();
		WeatherInfo weatherInfo;
		try {
			JSONArray json2 = json1.optJSONArray("forecast");
			for (int i = 0; i < json2.length(); i++) {
				JSONObject obj = json2.getJSONObject(i);
				weatherInfo = new WeatherInfo();
				setValueWithJson(weatherInfo, obj);
				weatherList.add(weatherInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return weatherList;
	}

	/**
	 * 2.获取当前天气信息
	 * @return DataInfo
	 */
	public DataInfo parseCurrentWeatherJson(){
		DataInfo dataInfo = new DataInfo();
		try {
			dataInfo.setCurrentTemp(json1.getString("wendu"));
			dataInfo.setPrompt(json1.getString("ganmao"));
			dataInfo.setCity(json1.getString("city"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataInfo;
	}

	/**
	 * 3.获取昨天天气信息
	 * @return YesterdayWeather
	 */
	public YesterdayWeather parseYesterdayWeatherJson(){
		YesterdayWeather yesWeather = new YesterdayWeather();
		JSONObject json3;
		try {
			json3 = json1.getJSONObject("yesterday");
			yesWeather.setWindDirection(json3.getString("fl"));
			yesWeather.setWindPower(json3.getString("fx"));
			yesWeather.setHighTemp(DataUtils.tempCupUtil(json3.getString("high")));
			yesWeather.setLowTemp(DataUtils.tempCupUtil(json3.getString("low")));
			yesWeather.setType(json3.getString("type"));
			yesWeather.setDate(DataUtils.weekCupUtString(json3.getString("date")));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return yesWeather;
	}

	/**
	 * 4.将需要展示的信息封装在ShowWeatherInfo对象中
	 * @return swi
	 */
	public ShowWeatherInfo setShowWeatherInfo(){
		List<WeatherInfo> weatherList = new ArrayList<WeatherInfo>();
		weatherList = parseFutureWeatherJson();
		DataInfo dataInfo = parseCurrentWeatherJson();
		ShowWeatherInfo  swi = new ShowWeatherInfo();

		swi.setCurrentTemp(dataInfo.getCurrentTemp()+"°");
		swi.setHighTemp(weatherList.get(0).getHighTemp());
		swi.setLowTemp(weatherList.get(0).getLowTemp());
		swi.setWeatherPic(weatherList.get(0).getType());
		swi.setCurrentCity(dataInfo.getCity());
		swi.setWeatherTxt(weatherList.get(0).getType());

		swi.setOneDayWeek(weatherList.get(1).getDate());
		swi.setOneDayWeatherPic(weatherList.get(1).getType());
		swi.setOneDayTemp(DataUtils.showTempRang(weatherList.get(1)));

		swi.setTwoDayWeek(weatherList.get(2).getDate());
		swi.setTwoDayWeatherPic(weatherList.get(2).getType());
		swi.setTwoDayTemp(DataUtils.showTempRang(weatherList.get(2)));

		swi.setThreeDayWeek(weatherList.get(3).getDate());
		swi.setThreeDayWeatherPic(weatherList.get(3).getType());
		swi.setThreeDayTemp(DataUtils.showTempRang(weatherList.get(3)));

		swi.setFourDayWeek(weatherList.get(4).getDate());
		swi.setFourDayWeatherPic(weatherList.get(4).getType());
		swi.setFourDayTemp(DataUtils.showTempRang(weatherList.get(4)));

		return swi;
	}




	/**
	 * 将天气信息存入WeatherInfo对象中
	 * @param weatherInfo
	 * @param obj
	 */
	private void setValueWithJson(WeatherInfo weatherInfo, JSONObject obj) {
		try {
			weatherInfo.setWindDirection(obj.getString("fengxiang"));
			weatherInfo.setWindPower(obj.getString("fengli"));
			weatherInfo.setHighTemp(DataUtils.tempCupUtil(obj.getString("high")));
			weatherInfo.setType(obj.getString("type"));
			weatherInfo.setLowTemp(DataUtils.tempCupUtil(obj.getString("low")));
			weatherInfo.setDate(DataUtils.weekCupUtString(obj.getString("date")));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


}
