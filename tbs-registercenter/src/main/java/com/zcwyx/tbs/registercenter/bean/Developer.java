package com.zcwyx.tbs.registercenter.bean;

import org.apache.commons.lang.StringUtils;

public class Developer {
	private String id;
	private String mobile;
	private String email;
	
	public Developer(){
		
	}
	
	public Developer(String commaSepStr){
		if(!StringUtils.isEmpty(commaSepStr)){
			String[] strs = commaSepStr.split(",");
			this.id = strs[0];
			this.mobile = strs[1];
			this.email = strs[2];
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
