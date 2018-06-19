package com.amap.map3d.demo.util;

import android.database.sqlite.SQLiteDatabase;

public class DbService {
	SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/data/data/gxmap.db",null); 
	public DbService(){
	}
	public void createTable(){ 
			//创建表SQL语句 
			String stu_table="create table gaoDeCityCode(_id integer primary key autoincrement,cityName text,citycode text)"; 
			//执行SQL语句 
			db.execSQL(stu_table); 
			
			//创建表SQL语句 
			String tq_table="create table tqCityCode(_id integer primary key autoincrement,cityName text,citycode text)"; 
			//执行SQL语句 
			db.execSQL(tq_table); 
		}
}
