package com.uniscope.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 软键盘的工具类
 * 1.打开软键盘		
 * 2.关闭软键盘
 *
 * @author wangwei
 *
 */
public class KeyBoardUtils {

	/**
	 * 打开软键盘
	 * @param edit
	 * @param mContext
	 */
	public static void openKeyBoard(EditText edit, Context mContext){
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(edit, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
				InputMethodManager.HIDE_IMPLICIT_ONLY);
	}


	/**
	 * 关闭软键盘
	 * @param edit
	 * @param context
	 */
	public static void closeKeyBoard(EditText edit, Context context){
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
	}

}
