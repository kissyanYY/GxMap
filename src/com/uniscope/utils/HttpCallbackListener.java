package com.uniscope.utils;

/**
 * 网络返回结果的接口
 *
 * @author wangwei
 *
 */
public interface HttpCallbackListener {
	void onFinish(String response);

	void onError(Exception e);
}
