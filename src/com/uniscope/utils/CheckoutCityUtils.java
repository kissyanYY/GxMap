package com.uniscope.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import com.uniscope.db.CityQueryDB;
import com.uniscope.model.CheckoutCity;

import android.content.Context;

/**
 * 已选城市的工具类：
 * 1.保存已选城市到json文件中
 * 2.从json文件中读取已选城市
 *
 * @author wangwei
 *
 */
public class CheckoutCityUtils {

	private static final String JSONNAME = "checked_city.json";

	/**
	 * 1.保存选中城市的名称和id
	 * @param context
	 * @param cities
	 */
	public static void saveCityList(Context context, ArrayList<CheckoutCity> cities){
		Writer writer = null;
		OutputStream out = null;
		JSONArray jsonArray = new JSONArray();
		for(CheckoutCity city : cities){
			jsonArray.put(city.saveJson());
		}

		try {
			out = context.openFileOutput(JSONNAME, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(jsonArray.toString());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	/**
	 * 2.获取已选中城市信息
	 * @param context
	 * @return ArrayList
	 */
	public static ArrayList<CheckoutCity> getCityList(Context context){
		FileInputStream in = null;
		ArrayList<CheckoutCity> cities = new ArrayList<CheckoutCity>();
		try {
			in = context.openFileInput(JSONNAME);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder builder = new StringBuilder();
			JSONArray jsonArray = new JSONArray();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				jsonArray = (JSONArray) new JSONTokener(builder.toString()).nextValue();
				for (int i=0; i<jsonArray.length(); i++) {
					CheckoutCity city = new CheckoutCity(jsonArray.getJSONObject(i));
					cities.add(city);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return cities;
	}


	/**
	 * 在已选中城市的开头插入定位城市
	 * @param context
	 * @param cities
	 * @param locationDistrict
	 * @param locationCity
	 * @return ArrayList<CheckoutCity>
	 */
	public static ArrayList<CheckoutCity> getAllCityList(Context context,
														 ArrayList<CheckoutCity> cities, String locationDistrict, String locationCity){

		CheckoutCity city;
		locationDistrict = DataUtils.locationInfo(locationDistrict);
		locationCity = DataUtils.locationInfo(locationCity);
		CityQueryDB cityQueryDb = CityQueryDB.getInstance(context);
		if (cityQueryDb.loadWeatherCode(locationDistrict)==null) {
			city = new CheckoutCity(cityQueryDb.loadWeatherCode(locationCity), locationCity);

		}else {
			city = new CheckoutCity(cityQueryDb.loadWeatherCode(locationDistrict), locationDistrict);
		}

		if (cities.size() > 0) {
			cities.set(0, city);
		}else {
			cities.add(0, city);
		}
		CheckoutCityUtils.saveCityList(context, cities);
		return cities;
	}

}
