package com.uniscope.model;

/**
 * 当前天气信息封装类
 *
 * @author wangwei
 *
 */
public class DataInfo {
	/**
	 * 当前温度
	 */
	private String CurrentTemp;
	/**
	 * 温馨提示
	 */
	private String Prompt;
	/**
	 * 未来几天天气
	 */
	private WeatherInfo weaterInfo;
	/**
	 * 城市名称
	 */
	private String city;
	/**
	 * 昨天天气
	 */
	private YesterdayWeather yesterdayWeather;
	public String getCurrentTemp() {
		return CurrentTemp;
	}
	public void setCurrentTemp(String currentTemp) {
		CurrentTemp = currentTemp;
	}
	public String getPrompt() {
		return Prompt;
	}
	public void setPrompt(String prompt) {
		Prompt = prompt;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public WeatherInfo getWeaterInfo() {
		return weaterInfo;
	}
	public void setWeaterInfo(WeatherInfo weaterInfo) {
		this.weaterInfo = weaterInfo;
	}
	public YesterdayWeather getYesterdayWeather() {
		return yesterdayWeather;
	}
	public void setYesterdayWeather(YesterdayWeather yesterdayWeather) {
		this.yesterdayWeather = yesterdayWeather;
	}

}
