package com.zcwyx.tbs.registercenter.bean;

public class QuotaInfo {
	private String clientId;
	private String quotaLevel;
	private int totalQuota;
	private int singleQuota;
	
	public QuotaInfo(){}

	public QuotaInfo(String commaSepStr) {
		if (commaSepStr != null) {
			String[] strs = commaSepStr.split(",");
			if (strs.length == 4) {
				clientId = strs[0];
				quotaLevel = strs[1];
				totalQuota = Integer.parseInt(strs[2]);
				singleQuota = Integer.parseInt(strs[3]);
			}
		}
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getQuotaLevel() {
		return quotaLevel;
	}

	public void setQuotaLevel(String quotaLevel) {
		this.quotaLevel = quotaLevel;
	}

	public int getTotalQuota() {
		return totalQuota;
	}

	public void setTotalQuota(int totalQuota) {
		this.totalQuota = totalQuota;
	}

	public int getSingleQuota() {
		return singleQuota;
	}

	public void setSingleQuota(int singleQuota) {
		this.singleQuota = singleQuota;
	}

}
