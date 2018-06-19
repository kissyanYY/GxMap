package Customer;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.gxmap.R;


public class WeatherMarkerOptions{

	
	public static MarkerOptions getWeatherMarkerOptions(LatLng location, int imap ){
		MarkerOptions markerOption = new MarkerOptions();
	    markerOption.position(location);
	    markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
//	    markerOption.anchor(0.8f, 0.8f);
	    markerOption.draggable(false);//设置Marker可拖动
//	    markerOption.icon(BitmapDescriptorFactory
//				.fromResource(R.drawable.arrow));
	    // 将Marker设置为贴地显示，可以双指下拉地图查看效果
//	    markerOption.setFlat(true);//设置marker平贴地图效果
	    
	    return markerOption;
	}
}
