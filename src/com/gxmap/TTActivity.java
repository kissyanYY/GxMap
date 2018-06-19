package com.gxmap;

import org.json.JSONException;
import org.json.JSONObject;

import overlay.DrivingRouteOverlay;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.AutoCompleteTextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.map3d.demo.util.AMapUtil;
import com.amap.map3d.demo.util.ToastUtil;
import com.google.gson.Gson;

public class TTActivity extends Activity implements OnRouteSearchListener, INaviInfoCallback {
	private AMap aMap;
	private MapView mapView;
	private String snip;
	private LocationService locationService;
	private ProgressDialog progDialog = null;// 搜索时进度条
	private LatLonPoint mStartPoint = null;
	private LatLonPoint mEndPoint =null;
	RouteSearch routeSearch ;
	AutoCompleteTextView at1;
	private Context mContext;
	
    LatLng p3 = new LatLng(39.904556, 116.427231);//北京站
    LatLng p5 = new LatLng(40.041986, 116.414496);//立水桥(北5环)

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tt);
		
		mapView = (MapView) findViewById(R.id.map2);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		mContext = this.getApplicationContext();
		init();
	}
	
	private void init() {
		aMap = mapView.getMap();
		locationService = new LocationService(aMap, this);
		
		at1 =  (AutoCompleteTextView)findViewById(R.id.fromLocationTextInput);
		AutoCompleteTextView at2 =  (AutoCompleteTextView)findViewById(R.id.toLocationTextInput);
		snip = getIntent().getStringExtra("snip");
		Log.d("TT","getSnip:"+snip);
		if(snip!=null && !"".equals(snip)){
			JSONObject jb;
			try {
				jb = new JSONObject(snip);
				Double jd = (Double )jb.get("jd");
				Double wd = (Double )jb.get("wd");
				String toName = jb.getString("title");
				LatLng ll = new LatLng(jd,wd);
				mEndPoint = new LatLonPoint(ll.latitude, ll.longitude);

				Log.d("TT","jb:"+wd);
				aMap.moveCamera(CameraUpdateFactory.changeLatLng(ll));
				
				at1.setText("我的位置");
				at2.setText(toName);
				initBroder();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void initBroder(){
		BroadcastReceiver r = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context,Intent intent){
				
				String wb = intent.getStringExtra("LatLng");
				Log.d("TT:","【定位广播接受成功：】"+wb);

				Gson gs = new Gson();
				if(wb != null && !"null".equals(wb)){
					LatLng mylocation = gs.fromJson(wb, LatLng.class);
					mStartPoint = new LatLonPoint(mylocation.latitude, mylocation.longitude);
					AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), new AmapNaviParams(new Poi("北京站", mylocation, ""), null, new Poi("故宫博物院", p5, ""), AmapNaviType.DRIVER), TTActivity.this);
//					searchRouteResult();
				}
				

			}
		};
		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GlobleVarString.LOCATION_CHAGER_ACTION);
        localBroadcastManager.registerReceiver(r, intentFilter);
	}
	
	/**
	 * 开始搜索路径规划方案
	 */
	public void searchRouteResult() {
		if( routeSearch == null)	{
			routeSearch = new RouteSearch(this);
			routeSearch.setRouteSearchListener(this);

		}

		if (mStartPoint == null) {
			ToastUtil.show(this, "定位中，稍后再试...");
			return;
		}
		if (mEndPoint == null) {
			ToastUtil.show(this, "终点未设置");
		}
		showProgressDialog();
		RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				mStartPoint, mEndPoint);
		DriveRouteQuery query = new DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null,
				null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
		routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
	}
	
	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		    progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    progDialog.setIndeterminate(false);
		    progDialog.setCancelable(true);
		    progDialog.setMessage("正在搜索");
		    progDialog.show();
	    }

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
		Log.d("TT:","【onDriveRouteSearched】");
		dissmissProgressDialog();
		aMap.clear();// 清理地图上的所有覆盖物
		if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
			if (result != null && result.getPaths() != null) {
				if (result.getPaths().size() > 0) {
					DriveRouteResult mDriveRouteResult = result;
					final DrivePath drivePath = mDriveRouteResult.getPaths()
							.get(0);
					DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
							mContext, aMap, drivePath,
							mDriveRouteResult.getStartPos(),
							mDriveRouteResult.getTargetPos(), null);
					drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
					drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
					drivingRouteOverlay.removeFromMap();
					drivingRouteOverlay.addToMap();
					drivingRouteOverlay.zoomToSpan();
					int dis = (int) drivePath.getDistance();
					int dur = (int) drivePath.getDuration();
					String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
				} else if (result != null && result.getPaths() == null) {
					ToastUtil.show(mContext, R.string.no_result);
				}

			} else {
				ToastUtil.show(mContext, R.string.no_result);
			}
		} else {
			ToastUtil.showerror(this.getApplicationContext(), errorCode);
		}
		
	}
	
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}
	
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
    @Override
    public void onInitNaviFailure() {  }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) { }

    @Override
    public void onArriveDestination(boolean b) { }

    @Override
    public void onStartNavi(int i) { 
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) { }

    @Override
    public void onCalculateRouteFailure(int i) { }

    @Override
    public void onGetNavigationText(String s) {
//        amapTTSController.onGetNavigationText(s);
    }

    @Override
    public void onStopSpeaking() {
//        amapTTSController.stopSpeaking();
    }

	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {}
	@Override
	public void onBusRouteSearched(BusRouteResult result, int errorCode) {}
	@Override
	public void onRideRouteSearched(RideRouteResult arg0, int arg1) {}
}
