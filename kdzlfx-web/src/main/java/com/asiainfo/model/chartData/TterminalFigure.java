package com.asiainfo.model.chartData;

public class TterminalFigure {
	
	private String user_name;	//宽带账号
	
	private String city_code;	//地市标识
	
	private String badquality;	//是否质差终端
	
	private String inteligentgateway;	//是否智能家庭网关
	
	private String subdevices;
	
	private String city_name;
	
	
	
	public String getSubdevices() {
		return subdevices;
	}

	public void setSubdevices(String subdevices) {
		this.subdevices = subdevices;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getCity_code() {
		return city_code;
	}

	public void setCity_code(String city_code) {
		this.city_code = city_code;
	}

	public String getBadquality() {
		return badquality;
	}

	public void setBadquality(String badquality) {
		this.badquality = badquality;
	}

	public String getInteligentgateway() {
		return inteligentgateway;
	}

	public void setInteligentgateway(String inteligentgateway) {
		this.inteligentgateway = inteligentgateway;
	}
	
	


}
