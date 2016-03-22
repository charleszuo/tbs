package com.zcwyx.tbs.registercenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;


public class PropertiesFileConfiguration implements RegisterCenterConfiguration{

	 /**
     * root字段的字面量
     * */
    public final static String ROOT_FIELD = "root";

    /**
     * server username字段的字面量
     * */
    public final static String SERVER_USERNAME_FIELD = "server.username";

    /**
     * server password字段的字面量
     * */
    public final static String SERVER_PASSWORD_FIELD = "server.password";

    /**
     * client username字段的字面量
     * */
    public final static String CLIENT_USERNAME_FIELD = "client.username";

    /**
     * client password字段的字面量
     * */
    public final static String CLIENT_PASSWORD_FIELD = "client.password";

    /**
     * cluster字段的字面量
     * */
    public final static String CLUSTER_FIELD = "cluster";

    private String root;

    private String clientUsername;

    private String clientPassword;

    private String serverUsername;

    private String serverPassword;

    private String cluster;

    public PropertiesFileConfiguration(String filePath){
        Properties props = new Properties();
        URI fileUri = new File(filePath).toURI();
        try {
            props.load(new FileInputStream(new File(fileUri)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(fileUri.toString() + " is not found.", e);
        } catch (IOException e) {
            throw new RuntimeException("Can not load config file: "
                    + fileUri.toString(), e);
        }
        initConfiguration(props);
    }

    public PropertiesFileConfiguration(Properties props){
        initConfiguration(props);
    }

    private void initConfiguration(Properties props){
        if (!props.containsKey(ROOT_FIELD)) {
            throw new RuntimeException("Config is missing required field '"
                    + ROOT_FIELD + "'");
        }
        if (!props.containsKey(CLUSTER_FIELD)) {
            throw new RuntimeException("Config is missing required field '"
                    + CLUSTER_FIELD + "'");
        }

        this.root = props.getProperty(ROOT_FIELD);
        this.clientUsername = props.getProperty(CLIENT_USERNAME_FIELD);
        this.clientPassword = props.getProperty(CLIENT_PASSWORD_FIELD);
        this.serverUsername = props.getProperty(SERVER_USERNAME_FIELD);
        this.serverPassword = props.getProperty(SERVER_PASSWORD_FIELD);
        this.cluster = props.getProperty(CLUSTER_FIELD);
    }

    public String getRoot() {
        return root;
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public String getClientPassword() {
        return clientPassword;
    }

    public String getServerUsername() {
        return serverUsername;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public String getCluster() {
        return cluster;
    }

    private List<String> tokenizeToStringList(String str) {

        List<String> tokens = Collections.emptyList();
        if (str == null) {
            return tokens;
        }
        StringTokenizer st = new StringTokenizer(str, ",");
        tokens = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken());
        }
        return tokens;
    }

}
