package com.imservices.im.bmm.bean;


public class Config{
	
	private String weblogo = "";
	private String websiteName = "";
	private String websiteUrl = "";
	private String welcomeStr ="";//欢迎语
	private String wanfa ="";//玩法介绍
	private String kefuStr="";//客服信息
	private Double txMoneyMin = 0.0;//提现金额最小
	private Double txMoneyMax = 0.0;//提现金额最大
	private String site1_url;
	
	
	
	public String getSite1_url() {
		return site1_url;
	}
	public void setSite1_url(String site1_url) {
		this.site1_url = site1_url;
	}
	public Double getTxMoneyMin() {
		return txMoneyMin;
	}
	public void setTxMoneyMin(Double txMoneyMin) {
		this.txMoneyMin = txMoneyMin;
	}
	public Double getTxMoneyMax() {
		return txMoneyMax;
	}
	public void setTxMoneyMax(Double txMoneyMax) {
		this.txMoneyMax = txMoneyMax;
	}
	public String getKefuStr() {
		return kefuStr;
	}
	public void setKefuStr(String kefuStr) {
		this.kefuStr = kefuStr;
	}
	public String getWanfa() {
		return wanfa;
	}
	public void setWanfa(String wanfa) {
		this.wanfa = wanfa;
	}
	public String getWeblogo() {
		return weblogo;
	}
	public void setWeblogo(String weblogo) {
		this.weblogo = weblogo;
	}
	public String getWebsiteName() {
		return websiteName;
	}
	public void setWebsiteName(String websiteName) {
		this.websiteName = websiteName;
	}
	public String getWebsiteUrl() {
		return websiteUrl;
	}
	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}
	public String getWelcomeStr() {
		return welcomeStr;
	}
	public void setWelcomeStr(String welcomeStr) {
		this.welcomeStr = welcomeStr;
	}
	
	
	
	
	

}