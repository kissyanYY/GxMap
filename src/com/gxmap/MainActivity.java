package com.gxmap;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import overlay.PoiOverlay;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.map3d.demo.util.AMapUtil;
import com.amap.map3d.demo.util.ToastUtil;
import com.google.gson.Gson;

public class MainActivity extends FragmentActivity implements OnClickListener, TextWatcher,InputtipsListener,
			OnPoiSearchListener, InfoWindowAdapter {
	private String keyWord = "";// 要输入的poi搜索关键字
	private AutoCompleteTextView searchText;// 输入搜索关键字
	private ProgressDialog progDialog = null;// 搜索时进度条
	private PoiSearch.Query query;// Poi查询条件类
	private int currentPage = 0;// 当前页面，从0开始计数
	private EditText editCity;// 要输入的城市名字或者城市区号
	private PoiResult poiResult; // poi返回的结果
	private WeatherBean locationWeather;
	private PoiSearch poiSearch;// POI搜索
	private AMap aMap;
	ImageButton travelBtn ;
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState); 
	    setContentView(R.layout.activity_main);
	    
		init();
	    //获取地图控件引用
//	    mMapView = (MapView) findViewById(R.id.map);
	    //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
//	    mMapView.onCreate(savedInstanceState);
	  }

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = ((SupportMapFragment)getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
		}
		
		new LocationService(aMap, this);//初始化定位
		
		TextView searButton = (TextView) findViewById(R.id.searchButton);
		searButton.setOnClickListener(this);
		
		searchText = (AutoCompleteTextView) findViewById(R.id.keyWord);
		searchText.addTextChangedListener(this);// 添加文本输入框监听事件
		editCity = (EditText) findViewById(R.id.city);
		aMap.setInfoWindowAdapter(this);// 添加显示infowindow监听事件
	
		BroadcastReceiver r = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context,Intent intent){
				
				String wb = intent.getStringExtra("Weather");
				Gson gs = new Gson();
				locationWeather = gs.fromJson(wb, WeatherBean.class);
				
				Log.d("YYYY:","广播接受成功："+locationWeather.getTq());
			}
		};
		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GlobleVarString.BROADCAST_ACTION);
        localBroadcastManager.registerReceiver(r, intentFilter);
		
	}
	
	@Override
	public View getInfoWindow(final Marker marker) {
		Log.d("YYY:","getInfoWindow:");
		String tileString ="";
		String snippetString = "";
		View view = getLayoutInflater().inflate(R.layout.poikeywordsearch_uri, null);
		TextView title = (TextView) view.findViewById(R.id.title);
		TextView snippet = (TextView) view.findViewById(R.id.snippet);
		
		String choicePoint = null;

		
		if(marker.getTitle() == null || "".equals(marker.getTitle()) ){
			//进入系统定位显示天气
			if(locationWeather != null){
				tileString = "天气 : " +locationWeather.getTq()+"/"+locationWeather.getWd();
				title.setText(tileString);
				snippet.setText(snippetString);
			}
		}
		else{
			Log.e("【YYYY】",marker.getTitle()+",,"+snippetString);
			snippetString = marker.getSnippet();
			try {
				JSONObject jb = new JSONObject(snippetString);
				String st = (String )jb.get("title");
				String ssp = (String )jb.get("snippet");
				title.setText( st);
				snippet.setText(ssp);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ImageButton button = (ImageButton) view.findViewById(R.id.start_amap_app);
		ImageButton wb = (ImageButton) view.findViewById(R.id.start_amap_app_2);
		travelBtn = (ImageButton) view.findViewById(R.id.travelBtn);
		// 调起高德地图app
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startAMapNavi(marker);
			}
		});
		
		wb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goToWeatherInfo(marker);
			}
		});
		
		travelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				  View popupView = MainActivity.this.getLayoutInflater().inflate(R.layout.popupwindow, null);

	                // TODO: 2016/5/17 为了演示效果，简单的设置了一些数据，实际中大家自己设置数据即可，相信大家都会。
//	                ListView lsvMore = (ListView) popupView.findViewById(R.id.lsvMore);
//	                lsvMore.setAdapter(new ArrayAdapter<String>(PopupActivity.this, android.R.layout.simple_list_item_1, datas));

	                // TODO: 2016/5/17 创建PopupWindow对象，指定宽度和高度
	                PopupWindow window = new PopupWindow(popupView, 600, 800);
	                // TODO: 2016/5/17 设置动画
	                window.setAnimationStyle(R.style.popup_window_anim);
	                // TODO: 2016/5/17 设置背景颜色
	                window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
	                // TODO: 2016/5/17 设置可以获取焦点
	                window.setFocusable(true);
	                // TODO: 2016/5/17 设置可以触摸弹出框以外的区域
	                window.setOutsideTouchable(true);
	                // TODO：更新popupwindow的状态
	                window.update();
	                // TODO: 2016/5/17 以下拉的方式显示，并且可以设置显示的位置
	                window.showAsDropDown(travelBtn, 0, 20);
			}
		});
		
		return view;
	}
	
	public void goToWeatherInfo(Marker marker) {
		//跳转到天气详细页面
		Intent wit = new Intent(getApplicationContext(), WeatherSearchActivity.class);
		String mgt= marker.getTitle();
		wit.putExtra("City", mgt);
		startActivity(wit);
	}
	
	
	public void startAMapNavi(Marker marker) {
//		// 构造导航参数
//		NaviPara naviPara = new NaviPara();
//		// 设置终点位置
//		naviPara.setTargetPoint(marker.getPosition());
//		// 设置导航策略，这里是避免拥堵
//		naviPara.setNaviStyle(NaviPara.DRIVING_AVOID_CONGESTION);
//
//		// 调起高德地图导航
//		try {
//			AMapUtils.openAMapNavi(naviPara, getApplicationContext());
//		} catch (com.amap.api.maps.AMapException e) {
//
//			// 如果没安装会进入异常，调起下载页面
//			AMapUtils.getLatestAMapApp(getApplicationContext());
//
//		}
		
		String snippetString = marker.getSnippet();
		Intent ti = new Intent(MainActivity.this,
				GetNaviStepsAndLinksActivity.class);
		ti.putExtra("snip",snippetString);
		startActivity(ti);
			
	}


	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String newText = s.toString().trim();
		if (!AMapUtil.IsEmptyOrNullString(newText)) {
		    InputtipsQuery inputquery = new InputtipsQuery(newText, editCity.getText().toString());
		    Inputtips inputTips = new Inputtips(MainActivity.this, inputquery);
		    inputTips.setInputtipsListener(this);
		    inputTips.requestInputtipsAsyn();
		}
	}

	/**
	 * Button点击事件回调方法
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/**
		 * 点击搜索按钮
		 */
		case R.id.searchButton:
			searchButton();
			break;
		/**
		 * 点击下一页按钮
		 */
//		case R.id.nextButton:
//			nextButton();
//			break;
		default:
			break;
		}
	}
	
	/**
	 * 点击搜索按钮
	 */
	public void searchButton() {
		keyWord = AMapUtil.checkEditText(searchText);
		if ("".equals(keyWord)) {
			ToastUtil.show(MainActivity.this, "请输入搜索关键字");
			return;
		} else {
			doSearchQuery();
		}
	}
	
	
	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery() {
		showProgressDialog();// 显示进度框
		currentPage = 0;
		String s_city = editCity.getText().toString();
		Log.d("YYYY:","开始搜:"+keyWord+","+s_city);
		query = new PoiSearch.Query(keyWord, "", s_city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(10);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页

		poiSearch = new PoiSearch(this, query);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}
	
	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage("正在搜索:\n" + keyWord);
		progDialog.show();
	}

	  @Override
	  protected void onDestroy() {
	    super.onDestroy();
	    //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
//	    mMapView.onDestroy();
	  }
	 @Override
	 protected void onResume() {
	    super.onResume();
	    //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
//	    mMapView.onResume();
	    }
	 @Override
	 protected void onPause() {
	    super.onPause();
	    //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
//	    mMapView.onPause();
	    }
	 @Override
	 protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
//	    mMapView.onSaveInstanceState(outState);
	  } 
	 
	 
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onPoiItemSearched(PoiItem arg0, int arg1) {
			
		}

		/**
		 * POI信息查询回调方法
		 */
		@Override
		public void onPoiSearched(PoiResult result, int rCode) {
			dissmissProgressDialog();// 隐藏对话框
			
			Log.d("YYYY:", "搜索返回结果"+rCode);
			if (rCode == AMapException.CODE_AMAP_SUCCESS) {
				if (result != null && result.getQuery() != null) {// 搜索poi的结果
					if (result.getQuery().equals(query)) {// 是否是同一条
						poiResult = result;
						// 取得搜索到的poiitems有多少页
						List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
						List<SuggestionCity> suggestionCities = poiResult
								.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

						if (poiItems != null && poiItems.size() > 0) {
							aMap.clear();// 清理之前的图标
							PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
							poiOverlay.removeFromMap();
							poiOverlay.addToMap();
							poiOverlay.zoomToSpan();
						} else if (suggestionCities != null
								&& suggestionCities.size() > 0) {
							showSuggestCity(suggestionCities);
						} else {
							ToastUtil.show(MainActivity.this,
									R.string.no_result);
						}
					}
				} else {
					ToastUtil.show(MainActivity.this,
							R.string.no_result);
				}
			} else {
				ToastUtil.showerror(this, rCode);
			}
			
		}
		/**
		 * 隐藏进度框
		 */
		private void dissmissProgressDialog() {
			if (progDialog != null) {
				progDialog.dismiss();
			}
		}
		
		/**
		 * poi没有搜索到数据，返回一些推荐城市的信息
		 */
		private void showSuggestCity(List<SuggestionCity> cities) {
			String infomation = "推荐城市\n";
			for (int i = 0; i < cities.size(); i++) {
				infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
						+ cities.get(i).getCityCode() + "城市编码:"
						+ cities.get(i).getAdCode() + "\n";
			}
			ToastUtil.show(MainActivity.this, infomation);

		}
		
		@Override
		public void onGetInputtips(List<Tip> tipList, int rCode) {
			if (rCode == AMapException.CODE_AMAP_SUCCESS) {// 正确返回
				List<String> listString = new ArrayList<String>();
				for (int i = 0; i < tipList.size(); i++) {
					listString.add(tipList.get(i).getName());
				}
				ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
						getApplicationContext(),
						R.layout.route_inputs, listString);
				searchText.setAdapter(aAdapter);
				aAdapter.notifyDataSetChanged();
			} else {
				ToastUtil.showerror(this, rCode);
			}
		}
		

		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}
	    		
	}
