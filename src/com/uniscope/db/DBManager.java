package com.uniscope.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.gxmap.R;

/**
 * 将数据库文件复制到本地
 * @author wangwei
 *
 */
public class DBManager {
	public static final String dbName = "db_weather.db";
	public static final String dbPack = "/data"+Environment.
			getDataDirectory().getAbsolutePath()+"/"+"com.gxmap";
	private SQLiteDatabase database;
	private Context context;

	public DBManager(Context context) {
		super();
		this.context = context;
	}

	public void openDatabase(){
		this.database= this.openDatabase(dbPack+"/"+dbName);
	}

	private SQLiteDatabase openDatabase(String dbFile) {
		try {
			if (!(new File(dbFile).exists())) {
				InputStream is = context.getResources().openRawResource(R.raw.db_weather);
				FileOutputStream fos = new FileOutputStream(dbFile);
				byte[] buffer = new byte[1024];
				int line;
				while((line = is.read(buffer))>0){
					fos.write(buffer,0,line);
				}
				fos.close();
				is.close();
			}
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
			return db;

		} catch (Exception e) {
			Log.d("DBManager", "复制数据库失败");
			e.printStackTrace();
		}

		return null;
	}

	public void closeDatabase(){
		if (database != null) {
			this.database.close();
		}
	}

}
