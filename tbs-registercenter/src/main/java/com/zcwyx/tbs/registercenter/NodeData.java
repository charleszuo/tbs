package com.zcwyx.tbs.registercenter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

public class NodeData {

	private static final String STATE_FIELD = "state";
	private static final String HEALTHY_FIELD = "healthy";
	private static final String DISABLED_FIELD = "disabled";
	
	private String state;
	private boolean healthy;
	private boolean disabled;
	
	public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public NodeData(String state, boolean disabled, boolean healthy) {
        super();
        this.state = state;
        this.disabled = disabled;
        this.healthy = healthy;
    }

    @Override
    public String toString() {
        return "NodeData [state=" + state + ", disabled=" + disabled + ", healthy=" + healthy + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (disabled ? 1231 : 1237);
        result = prime * result + (healthy ? 1231 : 1237);
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        NodeData other = (NodeData) obj;
        if (disabled != other.disabled) return false;
        if (healthy != other.healthy) return false;
        if (state == null) {
            if (other.state != null) return false;
        } else if (!state.equals(other.state)) return false;
        return true;
    }
    
    public byte[] toBytes(){
    	return new StringBuffer().append(DISABLED_FIELD).append("=")
                .append(String.valueOf(this.disabled)).append("\n").append(HEALTHY_FIELD)
                .append("=").append(String.valueOf(this.healthy)).append("\n").append(STATE_FIELD)
                .append("=").append(String.valueOf(this.state)).append("\n").toString().getBytes();
    }
    
    public static NodeData valueOf(String data){
    	return valueOf(data.getBytes());
    }
    
    public static NodeData valueOf(byte[] data){
    	Properties properties = new Properties();
    	try {
			properties.load(new ByteArrayInputStream(data));
		} catch (IOException e) {
			throw new IllegalArgumentException("Invalid data");
		}
    	
    	String state = properties.getProperty(STATE_FIELD);
    	String healthyStr = properties.getProperty(HEALTHY_FIELD);
    	String disabledStr = properties.getProperty(DISABLED_FIELD);
    	
    	if (state == null || "".equals(state)) {
            throw new IllegalArgumentException("Invalid state field in data.");
        }
    	
    	boolean healthy = Boolean.valueOf(healthyStr);
    	boolean disabled = Boolean.valueOf(disabledStr);
    	
    	return new NodeData(state, healthy, disabled);
    }
    
    
}
