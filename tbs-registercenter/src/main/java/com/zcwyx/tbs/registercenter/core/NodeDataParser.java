package com.zcwyx.tbs.registercenter.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import com.zcwyx.tbs.registercenter.bean.Developer;
import com.zcwyx.tbs.registercenter.bean.QuotaInfo;
import com.zcwyx.tbs.registercenter.bean.QuotaInfo2;

public class NodeDataParser {
	private List<String> ipList;
	private List<QuotaInfo> quotaList;
	private List<Developer> developerList;
	
	public NodeDataParser(byte[] data){
		
	}
	
	public void parse(byte[] data) throws IOException {
        if(data == null) {
            quotaList = new ArrayList<QuotaInfo>();
            ipList = new ArrayList<String>();
            developerList = new ArrayList<Developer>();
            return;
        }
        JsonFactory f = new JsonFactory();
        JsonParser jp = f.createJsonParser(data);
        jp.nextToken(); // will return JsonToken.START_OBJECT (verify?)
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = jp.getCurrentName();
            jp.nextToken(); // move to value, or START_OBJECT/START_ARRAY
            if ("IP".equalsIgnoreCase(fieldname)) { // contains an IP list.
                List<String> list = new ArrayList<String>();
                while (jp.nextToken() != JsonToken.END_ARRAY) {
                    list.add(jp.getText());
                }
                ipList = list;
            } else if ("quota_info".equalsIgnoreCase(fieldname)) {
                // This is an list of object.
                List<QuotaInfo> list = new ArrayList<QuotaInfo>();
                while (jp.nextToken() != JsonToken.END_ARRAY) { // start of object List
                    QuotaInfo q = new QuotaInfo();
                    while (jp.nextToken() != JsonToken.END_OBJECT) {
                        String subfieldname = jp.getCurrentName();
                        jp.nextToken();
                        String value = jp.getText();
                        if ("clientId".equalsIgnoreCase(subfieldname)) {
                            q.setClientId(value);
                        } else if ("quotaLevel".equalsIgnoreCase(subfieldname)) {
                            q.setQuotaLevel(value);
                        } else if ("totalQuota".equalsIgnoreCase(subfieldname)) {
                            q.setTotalQuota(Integer.parseInt(value.trim()));
                        } else if ("singleQuota".equalsIgnoreCase(subfieldname)) {
                            q.setSingleQuota(Integer.parseInt(value.trim()));
                        }
                    } // End of Object.
                    list.add(q);
                } // End of array.
                quotaList = list;
            } else if ("developers".equalsIgnoreCase(fieldname)) {
                // This is an list of object.
                List<Developer> list = new ArrayList<Developer>();
                while (jp.nextToken() != JsonToken.END_ARRAY) { // start of object List
                    Developer q = new Developer();
                    while (jp.nextToken() != JsonToken.END_OBJECT) {
                        String subfieldname = jp.getCurrentName();
                        jp.nextToken();
                        String value = jp.getText();
                        if ("id".equalsIgnoreCase(subfieldname)) {
                            q.setId(value);
                        } else if ("mobile".equalsIgnoreCase(subfieldname)) {
                            q.setMobile(value);
                        } else if ("email".equalsIgnoreCase(subfieldname)) {
                            q.setEmail(value);
                        }
                    } // End of Object.
                    list.add(q);
                } // End of array.
                developerList = list;
            }
        }
    }
	
	public List<QuotaInfo> getQuotaList(String clientId) {
        ArrayList<QuotaInfo> list = new ArrayList<QuotaInfo>();
        for (QuotaInfo info : quotaList) {
            if (clientId.equals(info.getClientId())) {
                list.add(info);
            }
        }
        return list;
    }
    
    public List<QuotaInfo2> getQuota2List() {
        return QuotaInfo2.createQuota2List(quotaList);
    }
    
    public QuotaInfo2 getQuota2(String clientId) {
        return QuotaInfo2.createQuotaInfo2(this.getQuotaList(clientId));
    }

	public List<String> getIpList() {
		return ipList;
	}

	public void setIpList(List<String> ipList) {
		this.ipList = ipList;
	}

	public List<QuotaInfo> getQuotaList() {
		return quotaList;
	}

	public void setQuotaList(List<QuotaInfo> quotaList) {
		this.quotaList = quotaList;
	}

	public List<Developer> getDeveloperList() {
		return developerList;
	}

	public void setDeveloperList(List<Developer> developerList) {
		this.developerList = developerList;
	}
    
}
