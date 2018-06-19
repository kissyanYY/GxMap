package com.uniscope.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库查询类：
 * 1.查询全国所有的省
 * 2.根据省名查询该省所有的市
 * 3.根据市名查询该市所有的县(区)
 * 4.查询县(区)的天气代码
 * 5.模糊查询县(区)
 * @author wangwei
 *
 */
public class CityQueryDB {

	private SQLiteDatabase db;
	private static CityQueryDB cityQueryDB;
	public DBManager dbHelper;

	private CityQueryDB(Context context){
		dbHelper = new DBManager(context);
		dbHelper.openDatabase();
		dbHelper.closeDatabase();
		db = SQLiteDatabase.openOrCreateDatabase(DBManager.dbPack+
				"/"+DBManager.dbName, null);
	}

	public synchronized static CityQueryDB getInstance(Context context){
		if (cityQueryDB == null) {
			cityQueryDB = new CityQueryDB(context);
		}
		return cityQueryDB;
	}

	/**
	 * 1.查询全部省
	 * @return list
	 */
	public List<String> loadProvinces(){
		List<String> list = new ArrayList<String>();
		boolean boo;
		Cursor cursor = db.query("city_info", new String[]{"province"}, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				String province = cursor.getString(cursor.getColumnIndex("province"));
				boo = list.contains(province);
				if (boo == false) {
					list.add(province);
				}
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}

	/**
	 * 2.查询某个省下的全部市
	 * @param province
	 * @return list
	 */
	public List<String> loadCities(String province){
		List<String> list = new ArrayList<String>();
		boolean boo;
		Cursor cursor = db.query("city_info", null, "province = ?", new String[]{province}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				String city = cursor.getString(cursor.getColumnIndex("city"));
				boo = list.contains(city);
				if (boo == false) {
					list.add(city);
				}
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}

	/**
	 * 3.查询某个市下的全部县
	 * @param city
	 * @return list
	 */
	public List<String> loadTowns(String city){
		List<String> list = new ArrayList<String>();
		Cursor cursor = db.query("city_info", null, "city = ?", new String[]{city}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				String town = cursor.getString(cursor.getColumnIndex("town"));
				list.add(town);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}

	/**
	 * 4.查询某个县的天气代码
	 * @param town
	 * @return String
	 */
	public String loadWeatherCode(String town){
		String cityWeatherCode = null;
		Cursor cursor = db.query("city_info", null, "town = ?", new String[]{town}, null, null, null);
		if (cursor.moveToFirst()) {
			cityWeatherCode = cursor.getString(cursor.getColumnIndex("cityWeatherCode"));
		}
		if (cursor != null) {
			cursor.close();
		}
		return cityWeatherCode;
	}


	/**
	 * 5.模糊查询县(区)
	 * @param info
	 * @return List
	 */
	public List<String> fuzzyQuestCitys(String info){

		List<String> list = new ArrayList<String>();
		Cursor cursor = db.query("city_info", null, "town like ?",
				new String[]{"%"+info+"%"}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				String town = cursor.getString(cursor.getColumnIndex("town"));
				list.add(town);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}

		return list;
	}


}
