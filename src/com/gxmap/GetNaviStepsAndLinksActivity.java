package com.gxmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Customer.MainListViewAdapter;
import Customer.MyListItem;
import Customer.WeatherMarkerOptions;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.map3d.demo.util.CityMaps;
import com.amap.map3d.demo.util.WeatherSerice;

public class GetNaviStepsAndLinksActivity extends BaseActivity {
    private DrawerLayout drawerLayout;
	 ArrayList<MyListItem> mList;  
	 String endPointName;
		private AMap aMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_navi_1);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
        aMap = mAMapNaviView.getMap();
//        guideWidget = (NaviGuideWidget) findViewById(R.id.route_select_guidelist);

        initDrawerLayout();
    }

    private void initDrawerLayout() {
        drawerLayout = (DrawerLayout) super.findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
    }

    public void openDetailRoute(View view) {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        } else{
            drawerLayout.openDrawer(GravityCompat.END);
        }
    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        Log.d("TT","导航加载成功");
        /**
         * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
         *
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
         *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
         */
        int strategy = 0;
        try {
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
            initNavi(strategy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private void initNavi(int strategy ) {
    		String snip = getIntent().getStringExtra("snip");
    		if(snip!=null && !"".equals(snip)){
    			JSONObject jb;
    			try {
    				jb = new JSONObject(snip);
    				Double jd = (Double )jb.get("jd");
    				Double wd = (Double )jb.get("wd");
    				endPointName = jb.getString("title");
//    				LatLng endLocation = new LatLng(jd,wd);
    			    List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
    				NaviLatLng endNLL = new NaviLatLng(jd,wd);
    				endList.add(endNLL);
    				
    				 Log.d("TT","到达目的地点解析成功，开始规划");
    				 // mWayPointList 沿途线路，，可为空
    				mAMapNavi.calculateDriveRoute( endList, mWayPointList, strategy);
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    }

    
    @Override
    public void onCalculateRouteSuccess(int[] ids) {
        super.onCalculateRouteSuccess(ids);
        Log.d("TT","导航线路规划成功");

        try {
            AMapNaviPath path = mAMapNavi.getNaviPath();
            int[] cityCodes = path.getCityAdcodeList();
            List<NaviLatLng>  cs = path.getCoordList();
            Log.d("TT","【获取的沿途点位数：】"+cs.size());
            
//            List<NaviLatLng> coords = path.getCoordList();
            setMyWeatherList(cityCodes);
            setWeatherMarkToMaps(cs,cityCodes.length);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    private void setWeatherMarkToMaps(List<NaviLatLng>  ps,int citys){
    	int xx = 3;
    	if(citys > 1){
    		 xx = ps.size()/citys;
    	}else{
    		xx = ps.size()/2;
    	}
        if(ps != null && ps.size() > 0){
        	for (int i = 0; i < ps.size(); i=i+xx) {
        		NaviLatLng p = ps.get(i);
        		LatLng l = new LatLng(p.getLatitude(), p.getLongitude());
        		MarkerOptions mark =getMark(l,getWeatherImg(i));
        		
        		aMap.addMarker(mark);
        	}
        	
        }
    }
    
    
    private void setMyWeatherList(int[] cityCodes ){
        //绑定XML中的ListView，作为Item的容器
    	   ListView mylistView = (ListView) findViewById(R.id.MyListView);
        // 获取Resources对象  
        Resources res = this.getResources();
        mList = new ArrayList<MyListItem>();  
        
        	MyListItem sitem = new MyListItem(); 
        	sitem.setImage(res.getDrawable(R.drawable.navi_start));  
        	sitem.setTitle("起点：");  
        	sitem.setWeather("我的位置");  
            mList.add(sitem); 
            
        	MyListItem eitem = new MyListItem(); 
        	eitem.setImage(res.getDrawable(R.drawable.navi_end));  
        	eitem.setTitle("终点：");  
        	eitem.setWeather(endPointName);  
            mList.add(eitem); 
        
        for (int i = 0; i < cityCodes.length; i++) {
        	String weather = WeatherSerice.getWeather();
        	int cy = cityCodes[i];
        	String cityName = CityMaps.getGaodeCode().get(""+cy);
        	
        	MyListItem item = new MyListItem(); 
        	item.setTitle("途径->"+cityName);
        	item.setWeather(weather);
        	item.setImage(res.getDrawable(getWeatherImg(i)));  
        	mList.add(item);
        	
        	Log.d("TT","沿途城市："+cityName+",天气:"+weather);
        }
     // 获取MainListAdapter对象  
        MainListViewAdapter adapter = new MainListViewAdapter(getApplicationContext(),mList);  
  
        // 将MainListAdapter对象传递给ListView视图  
        mylistView.setAdapter(adapter);
    }
    
    private void setList( int[] cityCodes ){
        ListView list = (ListView) findViewById(R.id.MyListView);
        if(cityCodes == null )return;
        //生成动态数组，并且转载数据
        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
        
        for (int i = 0; i < cityCodes.length; i++) {
        	String weather = WeatherSerice.getWeather();
        	int cy = cityCodes[i];
        	String cityName = CityMaps.getGaodeCode().get(""+cy);
        	HashMap<String, String> wlist = new HashMap<String, String>();
        	wlist.put("ItemTitle", "途径->"+cityName);
        	wlist.put("ItemText", weather);
        	mylist.add(wlist);
        	
        	 Log.d("TT","沿途城市："+cityName+",天气:"+weather);
        }
        
        //生成适配器，数组===》ListItem
        SimpleAdapter mSchedule = new SimpleAdapter(this, //没什么解释
        		                                    mylist,//数据来源 
        		                                    R.layout.my_listitem,//ListItem的XML实现
        		                                    
        		                                    //动态数组与ListItem对应的子项        
        		                                    new String[] {"ItemTitle", "ItemText"}, 
        		                                    
        		                                    //ListItem的XML文件里面的两个TextView ID
        		                                    new int[] {R.id.ItemTitle,R.id.ItemText});
        //添加并且显示
        list.setAdapter(mSchedule);
    }
    
    private int getWeatherImg(int idx){
    	int x = R.drawable.weathy_01;
    	if(idx%10 == 1){
    		x = R.drawable.weathy_02;
    	}
    	else if(idx%10 == 2){
    		x = R.drawable.weathy_08;
    	}
    	else if(idx%10 == 3){
    		x = R.drawable.weathy_04;
    	}
    	else if(idx%10 == 4){
    		x = R.drawable.weathy_05;
    	}
    	else if(idx%10 == 5){
    		x = R.drawable.weathy_09;
    	}
    	
    	
    	return x;
    }
    
    private MarkerOptions getMark(LatLng location,int rb){
		MarkerOptions markerOption = new MarkerOptions();
	    markerOption.position(location);
//	    markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
//	    markerOption.anchor(0.8f, 0.8f);
	    markerOption.draggable(false);//设置Marker可拖动
	    markerOption.icon(BitmapDescriptorFactory.fromBitmap(
	    		BitmapFactory
	            .decodeResource(getResources(),rb))
	    		);
	    // 将Marker设置为贴地显示，可以双指下拉地图查看效果
//	    markerOption.setFlat(true);//设置marker平贴地图效果
	    return markerOption;
    }
    
    private int getXX(List<NaviLatLng>  ps) {
    	int xx=2;
    	LatLng start = new LatLng(ps.get(0).getLatitude(),ps.get(0).getLongitude());
    	LatLng endp = new LatLng(ps.get(ps.size()-1).getLatitude(),ps.get(ps.size()-1).getLongitude());
    	float distance = AMapUtils.calculateLineDistance(start,endp);
    	//10公里内
    	if(distance < 100000){
    		xx = ps.size()/5;
    	}
    	//10公里内
    	else if(distance < 1000000){
    		xx = ps.size()/6;
    	}
    	else if(distance < 1000000){
    		xx = ps.size()/30;
    	}
    	else{
    		xx = ps.size()/6;
    	}
    	Log.d("TT","【开始结束距离：】"+distance+",xx:"+xx);
    	return xx;
    }
}
