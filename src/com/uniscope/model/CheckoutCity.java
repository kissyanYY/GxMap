package com.uniscope.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 已选择城市封装类
 *
 * @author wangwei
 *
 */
public class CheckoutCity {
	private String cityCode;
	private String cityName;
	private static final String JSON_CODE = "citycode";
	private static final String JSON_NAME = "cityname";

	public CheckoutCity(String cityCode, String cityName) {
		super();
		this.cityCode = cityCode;
		this.cityName = cityName;
	}

	public CheckoutCity(JSONObject json){
		if (json.has(JSON_CODE)) {
			try {
				cityCode = json.getString(JSON_CODE);
				cityName = json.getString(JSON_NAME);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public JSONObject saveJson(){
		JSONObject json = new JSONObject();
		try {
			json.put(JSON_CODE, cityCode);
			json.put(JSON_NAME, cityName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public static String getJsonCode() {
		return JSON_CODE;
	}
	public static String getJsonName() {
		return JSON_NAME;
	}
}
