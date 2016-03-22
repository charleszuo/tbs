package com.zcwyx.tbs.registercenter.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


public class QuotaInfo2 {
	public static enum QuotaLevel {
		HOUR, MIN, SEC
	}

	private String clientId;
	private Map<QuotaLevel, Integer> singleMap;
	private Map<QuotaLevel, Integer> totalMap;

	public static List<QuotaInfo2> createQuotaInfo2List(List<QuotaInfo> list) {

		return null;
	}

	public static List<QuotaInfo2> createQuota2List(List<QuotaInfo> list) {
		Comparator<QuotaInfo> camp = new Comparator<QuotaInfo>() {
			@Override
			public int compare(QuotaInfo o1, QuotaInfo o2) {
				return o1.getClientId().compareTo(o2.getClientId());
			}
		};
		Collections.sort(list, camp);
		List<QuotaInfo2> list2 = new ArrayList<QuotaInfo2>();
		int k = 0;
		for (int i = 1; i < list.size(); ++i) {
			if (!list.get(k).getClientId().equals(list.get(i).getClientId())) {
				// End of a client.
				list2.add(createQuotaInfo2(list.subList(k, i)));
				k = i;
			}
		}
		if (k != list.size()) {
			list2.add(createQuotaInfo2(list.subList(k, list.size())));
		}
		return list2;
	}

	public static QuotaInfo2 createQuotaInfo2(List<QuotaInfo> list) {
		if (list == null || list.size() == 0) {
			return null;
		}
		QuotaInfo2 quotaInfo = new QuotaInfo2();
		quotaInfo.setClientId(list.get(0).getClientId());
		Map<QuotaLevel, Integer> singleMap = new EnumMap<QuotaLevel, Integer>(
				QuotaLevel.class);
		Map<QuotaLevel, Integer> totalMap = new EnumMap<QuotaLevel, Integer>(
				QuotaLevel.class);

		for (QuotaInfo q : list) {
			String l = q.getQuotaLevel();
			if (StringUtils.isEmpty(l)) {
				continue;
			}
			if ("h".equals(l)) {
				singleMap.put(QuotaLevel.HOUR, q.getSingleQuota());
				totalMap.put(QuotaLevel.HOUR, q.getTotalQuota());
			} else if ("m".equals(l)) {
				singleMap.put(QuotaLevel.MIN, q.getSingleQuota());
				totalMap.put(QuotaLevel.MIN, q.getTotalQuota());
			} else if ("s".equals(l)) {
				singleMap.put(QuotaLevel.SEC, q.getSingleQuota());
				totalMap.put(QuotaLevel.SEC, q.getTotalQuota());
			}
		}
		return quotaInfo;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Map<QuotaLevel, Integer> getSingleMap() {
		return singleMap;
	}

	public void setSingleMap(Map<QuotaLevel, Integer> singleMap) {
		this.singleMap = singleMap;
	}

	public Map<QuotaLevel, Integer> getTotalMap() {
		return totalMap;
	}

	public void setTotalMap(Map<QuotaLevel, Integer> totalMap) {
		this.totalMap = totalMap;
	}

}
