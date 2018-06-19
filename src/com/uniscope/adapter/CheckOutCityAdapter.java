package com.uniscope.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxmap.R;
import com.uniscope.model.CheckoutCity;

/**
 * 已选城市显示的适配器
 *
 * @author wangwei
 *
 */
public class CheckOutCityAdapter extends ArrayAdapter<CheckoutCity> {

	private int resourceId;
	private RemoveCityListener mListener;

	public CheckOutCityAdapter(Context context, int resource,
							   List<CheckoutCity> objects, RemoveCityListener listener) {
		super(context, resource, objects);
		resourceId = resource;
		mListener = listener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		CheckoutCity city = getItem(position);
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
		TextView cityName = (TextView) view.findViewById(R.id.txt_city_list);
		cityName.setText(city.getCityName());
		ImageView remove = (ImageView) view.findViewById(R.id.img_city_list);
		if (position == 0) {
			remove.setImageResource(R.drawable.img_current);
		}else {
			remove.setImageResource(R.drawable.delete_user);
			remove.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					mListener.removeCity(v, position);
				}
			});
			remove.setTag(position);
		}
		return view;
	}

	/**
	 * 响应删除操作的接口
	 * @author xiayesheng
	 *
	 */
	public interface RemoveCityListener{
		public void removeCity(View view, int position);
	}
}
