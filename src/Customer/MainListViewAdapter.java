package Customer;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxmap.R;

public class MainListViewAdapter extends BaseAdapter {
	 Context mContext = null;
	 ArrayList<MyListItem> mList;  
	public MainListViewAdapter(Context context, ArrayList<MyListItem> sourList){
		 this.mContext = context;
		 this.mList =sourList;
	 }
	/** 
    * 返回item的个数 
    */  
   @Override  
   public int getCount() {  
       // TODO Auto-generated method stub  
       return mList.size();  
   }  

   /** 
    * 返回item的内容 
    */  
   @Override  
   public Object getItem(int position) {  
       // TODO Auto-generated method stub  
       return mList.get(position);  
   }  

   /** 
    * 返回item的id 
    */  
   @Override  
   public long getItemId(int position) {  
       // TODO Auto-generated method stub  
       return position;  
   }  

	
	 /** 
    * 返回item的视图 
    */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView listItemView;  
		  
       // 初始化item view  
       if (convertView == null) {  
           // 通过LayoutInflater将xml中定义的视图实例化到一个View中  
           convertView = LayoutInflater.from(mContext).inflate(  
                   R.layout.my_listitem, null);  

           // 实例化一个封装类ListItemView，并实例化它的两个域  
           listItemView = new ListItemView();  
           
           listItemView.imageView = (ImageView) convertView  
                   .findViewById(R.id.tqPng);  
           listItemView.cityNameView = (TextView) convertView  
                   .findViewById(R.id.ItemTitle);  
           listItemView.weatherTextView = (TextView) convertView  
           		.findViewById(R.id.ItemText);  

           // 将ListItemView对象传递给convertView  
           convertView.setTag(listItemView);  
       } else {  
           // 从converView中获取ListItemView对象  
           listItemView = (ListItemView) convertView.getTag();  
       }  

       // 获取到mList中指定索引位置的资源  
       Drawable img = mList.get(position).getImage();  
       String cityName = mList.get(position).getTitle();  
       String weather = mList.get(position).getWeather();  

       // 将资源传递给ListItemView的两个域对象  
       listItemView.imageView.setImageDrawable(img);  
       listItemView.cityNameView.setText(cityName);  
       listItemView.weatherTextView.setText(weather);  

       // 返回convertView对象  
       return convertView;  
	}
	class ListItemView {  
       ImageView imageView;  
       TextView cityNameView;  
       TextView weatherTextView; 
   }  
	
}
