package com.zcwyx.tbs.registry.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IPAddressUtil {

    private static Logger logger = LoggerFactory.getLogger(IPAddressUtil.class);

    private static String localhostIp = null;
    static {
        try {
            for (Enumeration<NetworkInterface> ni =
                    NetworkInterface.getNetworkInterfaces(); ni.hasMoreElements() && (localhostIp == null);) {
                NetworkInterface eth = ni.nextElement();
                for (Enumeration<InetAddress> add = eth.getInetAddresses(); add.hasMoreElements() &&(localhostIp == null);) {
                    InetAddress i = add.nextElement();
                    if (i instanceof Inet4Address) {
                        if (i.isSiteLocalAddress()) {
                            localhostIp = i.getHostAddress();
                        }
                    }
                }
            }
            if (localhostIp == null) {
                logger.error("No available IP address. XOA will exit.");
                throw new RuntimeException("No available IP address");
            } else {
                logger.warn("XOA will use local ip " + localhostIp
                            + " to regist service node");
            }
        } catch (SocketException e) {
            logger.warn("get local ip address failed:" + e.getMessage());
        }
    }

    public static String getLocalhostIp() {
        return localhostIp;
    }
    
    public static void main(String[] args) {
        System.out.println(getLocalhostIp());
    }
}
