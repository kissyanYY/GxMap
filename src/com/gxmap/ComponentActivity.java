package com.gxmap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.services.core.LatLonPoint;

public class ComponentActivity extends Activity implements INaviInfoCallback {
    LatLng p1 = new LatLng(39.993266, 116.473193);//首开广场
    LatLng p2 = new LatLng(39.917337, 116.397056);//故宫博物院
    LatLng p3 = new LatLng(39.904556, 116.427231);//北京站
    LatLng p4 = new LatLng(39.773801, 116.368984);//新三余公园(南5环)
    LatLng p5 = new LatLng(40.041986, 116.414496);//立水桥(北5环)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        initView();
    }

    private void initView() {
    		String snip = getIntent().getStringExtra("snip");
    		if(snip!=null && !"".equals(snip)){
    			JSONObject jb;
    			try {
    				jb = new JSONObject(snip);
    				Double jd = (Double )jb.get("jd");
    				Double wd = (Double )jb.get("wd");
    				String toName = jb.getString("title");
    				LatLng endLocation = new LatLng(jd,wd);

    				AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), new AmapNaviParams(null, null, new Poi(toName, endLocation, ""), AmapNaviType.DRIVER), ComponentActivity.this);
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onGetNavigationText(String s) {
//        amapTTSController.onGetNavigationText(s);
    }

    @Override
    public void onStopSpeaking() {
//        amapTTSController.stopSpeaking();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        amapTTSController.destroy();
    }
}
