package com.uniscope.model;

/**
 * 昨天天气信息封装类
 *
 * @author wangwei
 *
 */
public class YesterdayWeather {
	/**
	 * 风向
	 */
	private String windDirection;
	/**
	 * 风力
	 */
	private String windPower;
	/**
	 * 最高稳温度
	 */
	private String highTemp;
	/**
	 * 最低温度
	 */
	private String lowTemp;
	/**
	 * 天气
	 */
	private String type;
	/**
	 * 时间
	 */
	private String date;
	public String getWindDirection() {
		return windDirection;
	}
	public void setWindDirection(String windDirection) {
		this.windDirection = windDirection;
	}
	public String getWindPower() {
		return windPower;
	}
	public void setWindPower(String windPower) {
		this.windPower = windPower;
	}
	public String getHighTemp() {
		return highTemp;
	}
	public void setHighTemp(String highTemp) {
		this.highTemp = highTemp;
	}
	public String getLowTemp() {
		return lowTemp;
	}
	public void setLowTemp(String lowTemp) {
		this.lowTemp = lowTemp;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

}
