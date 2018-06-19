package com.gxmap;

public class WeatherBean {
	private String sd;//湿度
	private String fl;//风力
	private String updateTime;//更新时间
	private String tq;//天气--晴/雨/雪等
	private String wd_fw;//温度范围
	private String cityName;//城市名称
	
	private String wd;//温度
	public String getWd() {
		return wd;
	}
	public void setWd(String wd) {
		this.wd = wd;
	}
	public String getSd() {
		return sd;
	}
	public void setSd(String sd) {
		this.sd = sd;
	}
	public String getFl() {
		return fl;
	}
	public void setFl(String fl) {
		this.fl = fl;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getTq() {
		return tq;
	}
	public void setTq(String tq) {
		this.tq = tq;
	}
	public String getWd_fw() {
		return wd_fw;
	}
	public void setWd_fw(String wd_fw) {
		this.wd_fw = wd_fw;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
}
