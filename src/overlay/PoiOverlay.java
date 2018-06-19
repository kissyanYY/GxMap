package overlay;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.PoiItem;
import com.google.gson.JsonObject;
import com.gxmap.CallFunction;
/**
 * Poi图层类。在高德地图API里，如果要显示Poi，可以用此类来创建Poi图层。如不满足需求，也可以自己创建自定义的Poi图层。
 * @since V2.1.0
 */
public class PoiOverlay extends BroadcastReceiver {
	private List<PoiItem> mPois;
	private AMap mAMap;
	private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();
	public CallFunction cf;
	public String temperature = "0 ";//温度
	
	/**
	 * 通过此构造函数创建Poi图层。
	 * @param amap 地图对象。
	 * @param pois 要在地图上添加的poi。列表中的poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类<strong> <a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
	 * @since V2.1.0
	 */
	public PoiOverlay(AMap amap, List<PoiItem> pois) {
		mAMap = amap;
		mPois = pois;
		Log.d("PoiOverlay", "1-初始化");

	}
    @Override
    public void onReceive(Context context, Intent intent) {
//        String action = intent.getAction();
//        if(GlobleVarString.BROADCAST_ACTION.equals(action)){
//    	temperature =  intent.getStringExtra("Weather");
//    	addToMap();
//        }
    }
    
	/**
	 * 添加Marker到地图中。
	 * @since V2.1.0
	 */
	public void addToMap() {
		try{
			for (int i = 0; i < mPois.size(); i++) {
				if(cf != null)cf.call();
				Marker marker = mAMap.addMarker(getMarkerOptions(i));
		
				marker.setObject(i);
				mPoiMarks.add(marker);
			}
		}catch(Throwable e){
			e.printStackTrace();
		}
	}
	/**
	 * 去掉PoiOverlay上所有的Marker。
	 * @since V2.1.0
	 */
	public void removeFromMap() {
		for (Marker mark : mPoiMarks) {
			mark.remove();
		}
	}
	/**
	 * 移动镜头到当前的视角。
	 * @since V2.1.0
	 */
	public void zoomToSpan() {
		try{
			if (mPois != null && mPois.size() > 0) {
				if (mAMap == null)
					return;
				if(mPois.size()==1){
					mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mPois.get(0).getLatLonPoint().getLatitude(),
							mPois.get(0).getLatLonPoint().getLongitude()), 18f));
				}else{
					LatLngBounds bounds = getLatLngBounds();
					mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
				}
			}
		}catch(Throwable e){
			e.printStackTrace();
		}
	}

	private LatLngBounds getLatLngBounds() {
		LatLngBounds.Builder b = LatLngBounds.builder();
		for (int i = 0; i < mPois.size(); i++) {
			b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
					mPois.get(i).getLatLonPoint().getLongitude()));
		}
		return b.build();
	}

	private MarkerOptions getMarkerOptions(int index) {
		String cty = mPois.get(index).getCityName();
		String ctcode = mPois.get(index).getCityCode();
		Log.d("Poi","进入Options:"+mPois.get(index).getCityName());
		
		JsonObject jb = new JsonObject();
		jb.addProperty("cityname", cty);
		jb.addProperty("ctcode", ctcode);
		jb.addProperty("title", getTitle(index));
		jb.addProperty("snippet", getSnippet(index));
		jb.addProperty("jd", mPois.get(index).getLatLonPoint().getLatitude());
		jb.addProperty("wd", mPois.get(index).getLatLonPoint().getLongitude());
		
		Log.e("YYYYY:", jb.toString());
		return new MarkerOptions()
				.position(
						new LatLng(mPois.get(index).getLatLonPoint()
								.getLatitude(), mPois.get(index)
								.getLatLonPoint().getLongitude()))
				.title(cty)
//				.snippet(getSnippet(index))
				.snippet(jb.toString())
				.icon(getBitmapDescriptor(index));
	}
	/**
	 * 给第几个Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
	 */
	protected BitmapDescriptor getBitmapDescriptor(int index) {
		return null;
	}
	/**
	 * 返回第index的Marker的标题。
	 */
	protected String getTitle(int index) {
		return mPois.get(index).getTitle();
	}
	/**
	 * 返回第index的Marker的详情。
	 */
	protected String getSnippet(int index) {
		return mPois.get(index).getSnippet();
	}
	/**
	 * 从marker中得到poi在list的位置。
	 */
	public int getPoiIndex(Marker marker) {
		for (int i = 0; i < mPoiMarks.size(); i++) {
			if (mPoiMarks.get(i).equals(marker)) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * 返回第index的poi的信息。
	 * @param index 第几个poi。
	 * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
	 * @since V2.1.0
	 */
	public PoiItem getPoiItem(int index) {
		if (index < 0 || index >= mPois.size()) {
			return null;
		}
		return mPois.get(index);
	}
}
