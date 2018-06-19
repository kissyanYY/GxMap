package Customer;

import android.graphics.drawable.Drawable;

public class MyListItem {
	 private Drawable image;  
     private String title;//city
     private String weather;//天气

     public Drawable getImage() {  
         return image;  
     }  

     public void setImage(Drawable image) {  
         this.image = image;  
     }  

     public String getTitle() {  
         return title;  
     }  

     public void setTitle(String title) {  
         this.title = title;  
     }

		public String getWeather() {
			return weather;
		}

		public void setWeather(String weather) {
			this.weather = weather;
		}  
}
