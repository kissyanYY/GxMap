package com.uniscope.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络访问工具类
 *
 * @author wangwei
 *
 */
public class HttpUtil {
	public static void sendHttpRequest(final String address,final String type, final HttpCallbackListener listener){
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					StringBuilder response = new StringBuilder();
					int len = 0;
					byte[] buf = new byte[1024];
					while ((len = in.read(buf)) != -1)
					{
						response.append(new String(buf, 0, len, type));
					}
					in.close();
					if (listener != null) {
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if (listener != null) {
						listener.onError(e);
					}
				}finally{
					if ( connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}
